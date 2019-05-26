package shared.steps

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.ObjectMap
import com.jayway.awaitility.Awaitility
import conversion7.aop.TestSteps
import conversion7.engine.Gdxg
import conversion7.engine.artemis.engine.time.BeforeGameEngineTickSystem
import conversion7.engine.artemis.engine.time.PollingComponent
import conversion7.engine.artemis.engine.time.PollingSystem
import conversion7.engine.artemis.engine.time.SchedulingSystem
import conversion7.engine.dialog.AbstractDialog
import conversion7.engine.dialog.QuestOption
import conversion7.engine.geometry.Point2s
import conversion7.engine.utils.FastAsserts
import conversion7.engine.utils.Utils
import conversion7.game.services.WorldServices
import conversion7.game.stages.world.WorldRelations
import conversion7.game.stages.world.inventory.BasicInventory
import conversion7.game.stages.world.inventory.CraftRecipe
import conversion7.game.stages.world.inventory.TeamCraftInventory
import conversion7.game.stages.world.inventory.items.ArrowItem
import conversion7.game.stages.world.inventory.items.SkinItem
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem
import conversion7.game.stages.world.inventory.items.types.RangeBulletItem
import conversion7.game.stages.world.inventory.items.types.RangeWeaponItem
import conversion7.game.stages.world.inventory.items.weapons.BowItem
import conversion7.game.stages.world.inventory.items.weapons.StickItem
import conversion7.game.stages.world.landscape.BrezenhamLine
import conversion7.game.stages.world.landscape.Cell
import conversion7.game.stages.world.landscape.Landscape
import conversion7.game.stages.world.objects.AreaObject
import conversion7.game.stages.world.objects.actions.items.BuildCampAction
import conversion7.game.stages.world.objects.actions.items.FortifyAction
import conversion7.game.stages.world.objects.actions.items.RitualAction
import conversion7.game.stages.world.objects.buildings.Camp
import conversion7.game.stages.world.objects.buildings.ResourceCosts
import conversion7.game.stages.world.objects.unit.AbstractSquad
import conversion7.game.stages.world.objects.unit.WorldSquad
import conversion7.game.stages.world.team.Team
import conversion7.game.stages.world.team.events.AbstractEventNotification
import conversion7.game.stages.world.team.events.UnitDefeatedEvent
import conversion7.game.stages.world.team.skills.AbstractSkill
import conversion7.game.stages.world.team.skills.SkillType
import conversion7.game.stages.world.unit.*
import conversion7.game.stages.world.unit.effects.items.ChildbearingEffect
import conversion7.game.stages.world.unit.effects.items.ColdEffect
import org.mockito.Mockito
import org.mockito.internal.util.reflection.Whitebox
import org.slf4j.Logger
import org.testng.Assert
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts
import system.break_point_steps_core.Step

import java.util.concurrent.Callable
import java.util.function.Function

import static org.fest.assertions.api.Assertions.assertThat

@TestSteps
class WorldSteps extends BaseSteps {

    private static final Logger LOG = Utils.getLoggerForClass(WorldSteps)
    private static Point2s cellsIterator
    private static Team lastCreatedHumanAiTeam
    private static final int COMMON_TEST_STANDALONE_CELLS_SAFETY = 5

    static Point2s getCellsIterator() {
        if (cellsIterator == null) {
            cellsIterator = new Point2s()
            cellsIterator.y = Gdxg.core.world.centralCellCoord.y
        }
        return cellsIterator
    }

    /**Get next neib cell by X in game coords*/
    Cell getNextNeighborCell() {
        getCellsIterator().x += 1

        FastAsserts.assertLessThan(cellsIterator.x.toInteger(), Gdxg.core.world.widthInCells,
                "There are no space for NeighborCell!")

        Cell cell = Gdxg.core.world.getCell(cellsIterator.x.toInteger(), cellsIterator.y.toInteger())
        clearCellForTest(cell)
        return cell
    }

    /**Get next standalone cell(X+2) by X in game coords*/
    Cell getNextStandaloneCell() {
        return getNextStandaloneCellsInRow(1).get(0)
    }

    List<Cell> getNextStandaloneCellsInRow(int cellsAmount) {
        LOG.info("getNextStandaloneCellsInRow: " + cellsAmount)
        getCellsIterator().x += 1
        if (cellsIterator.x >= Gdxg.core.world.widthInCells - COMMON_TEST_STANDALONE_CELLS_SAFETY - cellsAmount) {
            cellsIterator.x = 0
            cellsIterator.y += 2
        }

        List<Cell> cells = new ArrayList<>()
        for (int i = 0; i < cellsAmount; i++) {
            Cell cell = getNextNeighborCell()
            cells.add(cell)
            clearCellAndNeighborsForTest(cell)
        }

        return cells
    }

    void clearCellForTest(Cell origin) {
        if (origin.hasSquad()) {
            LOG.info("clearCellForTest > kill object for testing purposes on origin: " + origin)
            defeatKillUnit(origin.getSquad().unit)
        }
        if (!origin.hasFreeMainSlot()) {
            origin.getLandscapeController().regenerateLandscapeToBeAvailableForMove()
        }
    }

    void makePlayerTeamInvincible() {
        LOG.info("makePlayerTeamInvincible")
        def playerTeam = Gdxg.core.world.lastActivePlayerTeam
        assert playerTeam
        AbstractSquad squad = createUnit(
                createHumanTeam(),
                getNextStandaloneCell())
        def unit = squad.unit
        makeUnitInvincible(unit)
        squad.validate()
    }


    void makeUnitWeaker(Unit unit, Unit thanUnit) {
        LOG.info("makeUnitWeak: " + unit)
        unit.getBaseParams().copyFrom(thanUnit.baseParams)
        unit.getBaseParams().update(UnitParameterType.STRENGTH, -1)
        unit.getBaseParams().updateHealthToVitality()
    }

    void makeUnitStrong(Unit unit) {
        unit.getBaseParams().put(UnitParameterType.STRENGTH, 9999)
        unit.getBaseParams().put(UnitParameterType.AGILITY, 9999)
        unit.getBaseParams().put(UnitParameterType.VITALITY, 9999)
        unit.getBaseParams().updateHealthToVitality()
    }

    void makeUnitWeak(Unit unit) {
        LOG.info("makeUnitWeak: " + unit)
        unit.getBaseParams().put(UnitParameterType.STRENGTH, 1)
        unit.getBaseParams().put(UnitParameterType.AGILITY, 1)
        unit.getBaseParams().updateHealthToVitality()
    }

    void makeUnitInvincible(Unit unit) {
        LOG.info("makeUnitInvincible: " + unit)
        unit.getBaseParams().update(UnitParameterType.VITALITY, 10000)
        unit.getBaseParams().update(UnitParameterType.STRENGTH, 10000)
        unit.getBaseParams().updateHealthToVitality()
    }

    void clearCellAndNeighborsForTest(Cell origin) {
        clearCellForTest(origin)
        for (Cell cell : origin.getCellsAround()) {
            if (cell.hasSquad()) {
                LOG.info("clearCellAndNeighborsForTest > kill object for testing purposes on " + cell)
                defeatKillUnit(cell.getSquad().unit)
            }
        }
    }

    void rewindWorldSteps(int steps) {
        for (int i = 0; i < steps; i++) {
            LOG.info("rewind world step: " + i)
            rewindTeamsToStartNewWorldStep()
        }
    }

    void waitNewWorldStepStarted() {
        rewindTeamsToStartNewWorldStep()
    }

    void rewindTeamsToStartNewWorldStep() {
        BaseGdxgSpec.lockCore()
        def inFrameId = Gdxg.core.frameId
        PollingComponent schedule = PollingSystem.schedule(0, new Callable<Boolean>() {
            Integer initWorldStep

            @Override
            Boolean call() throws Exception {
                Assert.assertTrue(Gdxg.core.world.teams.size > 0, "rewind is not possible without teams")

                if (initWorldStep == null) {
                    initWorldStep = Gdxg.core.world.getStep()
                    LOG.info("rewindTeamsToStartNewWorldStep started at initWorldStep: {}", initWorldStep)
                    LOG.info("...try roll teams on next core step")
                    return false
                }

                Team activeTeam = Gdxg.core.world.activeTeam
                if (Gdxg.core.world.getStep() == initWorldStep + 1) {
                    LOG.info("rewindTeamsToStartNewWorldStep completed at step: {}", Gdxg.core.world.getStep())
                    Assert.assertEquals(activeTeam, Gdxg.core.world.teams.get(0))
                    return true
                } else {
                    LOG.info("rewindTeamsToStartNewWorldStep ask requestNextTeamTurn at step: {}", Gdxg.core.world.getStep())
                    LOG.info("...and active team is: {}", activeTeam)
                    Assert.assertNotNull(activeTeam)
                    assert Gdxg.core.world.teams.size() > 0: 'Cant rewind when no teams'
                    Gdxg.core.world.requestNextTeamTurn()
                }

                return false
            }
        })
        BaseGdxgSpec.releaseCore()

        PollingSystem.waitPollingCompleted(schedule.entityUuid)
        WorldServices.waitForNextCoreStep(inFrameId)
    }

    @Step
    void moveOnCell(AbstractSquad areaObject, Cell cell) {
        Assert.assertNotNull(cell)
        areaObject.moveOn(cell)
    }

    @Step
    void addDefaultEquipmentPackageToInventory(BasicInventory inventory) {
        inventory.addItem(StickItem.class, 1)
        inventory.addItem(BowItem.class, 1)
        inventory.addItem(ArrowItem.class, 1)
        inventory.addItem(SkinItem.class, 1)
    }

    @Step
    void teamLearnsSkill(Team humanTeam, Class<? extends AbstractSkill> skillClass) {
        humanTeam.getTeamSkillsManager().getSkill(SkillType.fromClass(skillClass)).learn()
    }

    static void unitFullEquip(Unit unit) {
        unit.equipment.equipMeleeWeaponItem(new StickItem())
        unit.equipment.equipRangeWeaponItem(new BowItem())
        def arrowItem = new ArrowItem()
        arrowItem.quantity = 3
        unit.equipment.equipRangeBulletsItem(arrowItem)
        unit.equipment.equipClothesItem(new SkinItem())
    }

    void makeUnitCouldEquipItemsFromDefaultEquipmentPackage(Unit unit) {
        makeUnitCouldEquipItem(unit, DefaultPackageItem.MELEE.getInventoryItemClass())
        makeUnitCouldEquipItem(unit, DefaultPackageItem.RANGE.getInventoryItemClass())
        makeUnitCouldEquipItem(unit, DefaultPackageItem.BULLETS.getInventoryItemClass())
        makeUnitCouldEquipItem(unit, DefaultPackageItem.CLOTHES.getInventoryItemClass())
    }

    @Step
    void makeUnitCouldEquipItem(Unit unit, Class<? extends AbstractInventoryItem> itemClass) {
        makeTeamCouldUseItem(unit.getSquad().getTeam(), itemClass)
    }

    @Step
    void makeTeamCouldUseItem(Team team, Class<? extends AbstractInventoryItem> itemClass) {
        try {
            makeTeamCouldUseItem(team, itemClass.newInstance())
        } catch (InstantiationException | IllegalAccessException e) {
            Utils.error(e)
        }
    }

    @Step
    void makeTeamCouldUseItem(Team team, AbstractInventoryItem inventoryItem) {
        SkillType requiredSkillLearned = inventoryItem.getParams().requiredSkill
        if (requiredSkillLearned == null) {
            return
        }

        AbstractSkill reqSkill = team.getTeamSkillsManager().getSkill(requiredSkillLearned)
        if (reqSkill.isNotStartedLearn()) {
            reqSkill.learn()
        }
    }

    @Step
    void makeTeamCouldUseItemsFromInventory(Team team, BasicInventory inventory) {
        for (ObjectMap.Entry<Class<? extends AbstractInventoryItem>, AbstractInventoryItem> inventoryItemEntry : inventory.getItemsIterator()) {
            makeTeamCouldUseItem(team, inventoryItemEntry.value)
        }
    }

    @Step
    void makeTeamCouldUseRecipes(Team team, Array<CraftRecipe> recipesList) {
        Array<CraftRecipe> craftRecipesCopy = new Array<>(recipesList)
        for (CraftRecipe craftRecipe : craftRecipesCopy) {
            makeTeamCouldUseRecipe(team, craftRecipe)
        }
    }

    @Step
    void makeTeamCouldUseRecipe(Team team, CraftRecipe recipe) {
        makeTeamCouldUseItem(team, recipe.getFinalItemClass())
    }

    @Step
    void addItemsNeededForCraft(AbstractSquad areaObject, Array<CraftRecipe> recipes) {
        LOG.info("addItemsNeededForCraft: " + recipes)
        Array<CraftRecipe> craftRecipesCopy = new Array<>(recipes)
        assertThat(recipes).isNotEmpty()
        for (CraftRecipe recipe : craftRecipesCopy) {
            addItemsNeededForCraft(areaObject, recipe)
        }
    }

    @Step
    void addItemsNeededForCraft(AbstractSquad areaObject, CraftRecipe recipe) {
        LOG.info("addItemsNeededForCraft " + recipe)
        Array<CraftRecipe.Consumable> consumablesCopy = new Array<>(recipe.getConsumables())
        for (CraftRecipe.Consumable consumable : consumablesCopy) {
            LOG.info(" consumable " + consumable)
            addItemToInventory(consumable.getItemClass(), consumable.getQuantity(), areaObject.getInventory())
        }
    }

    @Step
    void addItemToInventory(Class<? extends AbstractInventoryItem> itemClass, int qty, BasicInventory
            inventory) {
        inventory.addItem(itemClass, qty)
    }

    @Step
    void addItemsNeededForCraft(AbstractSquad areaObject, Class<? extends
            AbstractInventoryItem> itemClass) {
        addItemsNeededForCraft(areaObject, TeamCraftInventory.getRecipeForItem(itemClass))
    }

    @Step
    void craftAll(AbstractSquad areaObject, Array<CraftRecipe> recipes) {
        LOG.info("craftAll > " + recipes)
        Array<CraftRecipe> craftRecipesCopy = new Array<>(recipes)
        for (CraftRecipe recipe : craftRecipesCopy) {
            craft(areaObject, recipe)
        }
    }

    @Step
    void craft(AbstractSquad areaObject, CraftRecipe craftRecipe) {
        LOG.info("craft > " + craftRecipe)
        areaObject.getCraftInventory().craft(craftRecipe.getFinalItemClass(), 1)
    }

    @Step
    void craft(AbstractSquad areaObject, Class<? extends AbstractInventoryItem> itemClass) {
        craft(areaObject, TeamCraftInventory.getRecipeForItem(itemClass))
    }

    void makePerfectConditionsOnCell(Cell cell) {
        cell.setTemperature(Unit.HEALTHY_TEMPERATURE_MIN)
        cell.setFood(AbstractSquad.UNITS_AMOUNT_LIMIT)
        cell.setWater(AbstractSquad.UNITS_AMOUNT_LIMIT)
    }

    @Step
    void makeDeadlyConditionsOnCell(Cell cell) {
        cell.setTemperature(Unit.HEALTHY_TEMPERATURE_MIN - 1)
        cell.setFood(0)
        cell.setWater(0)
    }

    void squadCreatesCamp(AbstractSquad squad) {
        assertThat(squad.getLastCell().getCamp()).as("Camp already exist on " + squad.getLastCell()).isNull()
        boolean buildDone = false
        while (!buildDone) {
            SchedulingSystem.scheduleOnNextStep("squadCreatesCamp", {
                executeBuildCampAction(squad)
                if (squad.lastCell.camp.isConstructionCompleted()) {
                    buildDone = true
                }
            })

            if (!buildDone) {
                rewindTeamsToStartNewWorldStep()
            }
            WorldServices.waitForNextCoreSteps(2)
        }
        WorldAsserts.assertTownConstructionCompleted(squad.getLastCell().getCamp())
    }

    void squadIsBuildingCampOneStep(AbstractSquad squad) {
        SchedulingSystem.scheduleOnNextStep("squadIsBuildingCampOneStep", {
            Camp town = squad.getLastCell().getCamp()
            if (town != null) {
                assert !town.isConstructionCompleted()
            }
            executeBuildCampAction(squad)
        })

        WorldServices.waitForNextCoreSteps(2)
    }

    private static void executeBuildCampAction(AbstractSquad squad) {
        WorldAsserts.assertAreaObjectHasAction(squad, BuildCampAction.class)
        BuildCampAction townAction = squad.getActionsController().getAction(BuildCampAction.class)
        townAction.run()
        assertThat(squad.getLastCell().getCamp()).as("Camp was not created!").isNotNull()
    }

    AbstractSquad createFertilizedUnit(Team team, Cell cell) {
        AbstractSquad squad = createUnit(team, cell)

        Unit female = squad.unit
        female.setGender(false)

        def childUnit = UnitFertilizer2.createUnit(female.class, true,
                AreaObject.UnitSpecialization.getRandom()
                , new UnitParameters().copyFrom(female.baseParams))

        def childbearing = new ChildbearingEffect(female, childUnit)
        female.getEffectManager().addEffect(
                childbearing)

        WorldAsserts.assertUnitHasEffect(female, ChildbearingEffect.class)
        WorldAsserts.assertUnitEffectHasTickCounter(childbearing, 0)

        return squad
    }

    void selectCellInViewer(Cell cell) {
        SchedulingSystem.scheduleOnNextStep("selectCellInViewer", {
            Gdxg.getAreaViewer().selectCell(cell)
        })
        waitForNextCoreStep()
    }

    @Step
    void makeUnitCouldExecuteRangeAttack(Unit unit1, RangeBulletItem rangeBulletsItem, RangeWeaponItem rangeWeaponItem) {
        unit1.setSpecialization(AreaObject.UnitSpecialization.RANGE)
        makeUnitCouldEquipItem(unit1, rangeWeaponItem.getClass())
        makeUnitCouldEquipItem(unit1, rangeBulletsItem.getClass())

        rangeBulletsItem.setQuantity(rangeBulletsItem.getParams().getEquipQuantityLimit())
        unit1.getEquipment().equipRangeBulletsItem(rangeBulletsItem)

        rangeWeaponItem.setQuantity(1)
        unit1.getEquipment().equipRangeWeaponItem(rangeWeaponItem)
    }

    @Step
    void clearAllBattleModificatorsOnCell(Cell cell) {
        cell.getLandscape().setLevel(0)
        cell.getLandscape().setAddition(Landscape.Addition.NONE)
    }

    /**1st priority step from 'world requires 1 player team' moment*/
    Team createHumanTeam() {
        return createHumanTeam(Gdxg.core.world.lastActivePlayerTeam == null)
    }

    Team createHumanTeam(boolean humanPlayer) {
        Team team = Gdxg.core.world.createHumanTeam(humanPlayer)
        return team
    }


    Team createAnimalTeam() {
        if (Gdxg.core.world.animalTeam == null) {
            return Gdxg.core.world.createAnimalTeam()
        } else {
            return Gdxg.core.world.animalTeam
        }
    }

    @Step
    void removeBlockingSightFromCells(Cell... cells) {
        for (Cell cell : cells) {
            if (cell.getLandscape().hasForest()) {
                cell.getLandscape().setAddition(Landscape.Addition.NONE)
            }
            cell.getLandscape().setType(Landscape.Type.COMMON)
        }
    }

    @Step
    void makeCellsVisibleOnSightOfView(Cell cell1, Cell cell2) {
        Array<Cell> lineOfSight = BrezenhamLine.getCellsLine(cell1, cell2)
        removeBlockingSightFromCells(lineOfSight.toArray(Cell.class))
    }

    void defeatKillUnit(Unit unit) {
        assert unit.hurt(unit.getTotalParam(UnitParameterType.HEALTH))
        WorldSquad.killUnit(unit)
    }

    void defeatObject(AreaObject areaObject) {
        WorldAsserts.assertAreaObjectDefeated(areaObject, false, false)
        if (areaObject.isSquad()) {
            AbstractSquad abstractSquad = (AbstractSquad) areaObject
            defeatKillUnit(abstractSquad.unit)
        } else {
            throw new GdxRuntimeException("Not impl!")
        }
        WorldAsserts.assertAreaObjectDefeated(areaObject, true, false)
    }

    AbstractSquad createTeamTempGarantNoZeroTeamsInWorld() {
        def squad = createUnit(
                createHumanTeam(),
                getNextStandaloneCell())
        makeUnitInvincible(squad.unit)
        return squad
    }

    void waitNewTeamWasActivated(TeamActivationSnapshot prevActData) {
        Awaitility.await().until({
            def newActData = new TeamActivationSnapshot()
            return wasNewTeamWasActivated(prevActData, newActData)
        })
    }

    private boolean wasNewTeamWasActivated(TeamActivationSnapshot prevActData, TeamActivationSnapshot newActData) {
        return prevActData.frame < newActData.frame || prevActData.team != newActData.team
    }

    void waitNewPlayerTeamWasActivated(TeamActivationSnapshot prevActData) {
        Awaitility.await().until({
            def newActData = new TeamActivationSnapshot()
            return wasNewTeamWasActivated(prevActData, newActData) && newActData.team.isHumanPlayer()
        })
    }

    AbstractSquad createUnit(Team team, Cell cell) {
        AbstractSquad squad = WorldServices.createWorldInitialClassUnit(team, cell)
        return squad
    }

    AbstractSquad createUnit(Team team, Cell cell, Class<? extends Unit> unitClass) {
        AbstractSquad squad = WorldSquad.create(unitClass, team, cell)
        return squad
    }

    def defeatTeam(Team team) {
        Array<AbstractSquad> armies = new Array<>(team.squads)
        armies.each {
            defeatKillUnit(it.unit)
        }

        if (!team.defeated) {
            Array<Camp> townFragments = new Array<>(team.camps)
            townFragments.each { WorldServices.killTown(it) }
        }

        WorldAsserts.assertTeamDefeated(team, true, false)
    }

    def getSquadDefeatedEvent(AbstractSquad squad) {
        return squad.getTeam().getEvents().toList().stream().find { eventNotification ->
            if (eventNotification.class == UnitDefeatedEvent) {
                UnitDefeatedEvent defeatedEvent = (UnitDefeatedEvent) eventNotification
                return defeatedEvent.getSquad() == squad
            }
        }
    }

    def getEvent(Class<? extends AbstractEventNotification> aClass, Team team) {
        return team.events.find({ it.getClass() == aClass })
    }

    def makeUnitWillBeKilledOnWorldEndStepSimulation(Unit unit) {
        unit.baseParams.put(UnitParameterType.HEALTH, 1)
        unit.effectManager.addEffect(new ColdEffect())
        unit.squad.lastCell.temperature = Unit.HEALTHY_TEMPERATURE_MIN - 1
    }

    AbstractSquad createUnitGarantsTeamNotDefeated(Team team) {
        return createUnit(team, getNextStandaloneCell())
    }

    def transformCells(Array<Cell> cells, Function<Cell, Void> transform) {
        for (Cell cell : cells) {
            transform.apply(cell)
        }
    }

    def setUnitAp(Unit unit, int ap) {
        unit.updateActionPoints(-unit.actionPoints)
        unit.updateActionPoints(+ap)
    }

    def prepareForSuccessfulHit(AbstractSquad attacker, AbstractSquad target) {
        BaseGdxgSpec.lockCore()
        attacker.unit.battleHelper.overrideHitChance = 100
        target.unit.battleHelper.overrideDodgeChance = 0
        target.team.teamClassesManager = Mockito.spy(target.team.teamClassesManager)
        Mockito.doNothing().when(target.team.teamClassesManager).removeUnit(target.unit)
        BaseGdxgSpec.releaseCoreAndWaitNextCoreStep()
    }

    void doFortify(AbstractSquad squad) {
        squad.getActionsController().getAction(FortifyAction.class).run()
    }

    void doRitual(int food, AbstractSquad squad) {
        squad.getActionsController().getAction(RitualAction.class).complete(food)
    }

    @Deprecated
    Camp startCampConstruction(Team team, Cell cell) {
        assert !cell.camp

        Camp town
        BeforeGameEngineTickSystem.schedule({
            town = WorldServices.createCamp(team, cell)
            return true
        })
        if (town == null) {
            WorldServices.waitForNextCoreSteps(2)
        }
        assert town
        assert !town.constructionCompleted
        return town
    }

    Camp createAndCompleteCampConstruction(Team team, Cell cell) {
        return BuildCampAction.buildCamp(cell.squad)
    }

    void prepareForBuildCamp(AbstractSquad squad) {
        def skill = squad.team.teamSkillsManager.getSkill(SkillType.BUILD_CAMP)
        if (!skill.isFullyLearned()) {
            skill.learn()
        }
        makeCellsGoodForCamp([squad.lastCell])
        squad.actionsController.invalidate()
        squad.validate()
    }

    def makeCellsBadForCamp(List<Cell> cells) {
        for (Cell cell : cells) {
            cell.food = Camp.REQUIRED_CELL_FOOD - 1
        }
    }

    def makeCellsGoodForCamp(List<Cell> cells) {
        for (Cell cell : cells) {
            cell.food = Camp.REQUIRED_CELL_FOOD
            cell.water = Camp.REQUIRED_CELL_WATER
            assert Camp.couldBeBuiltOnCell(cell)
        }
    }

    def makeEnemies(AbstractSquad squad, AbstractSquad squad2) {
        squad.team.world.setRelation(WorldRelations.ENEMIES_RELATION_TOP, squad.team, squad2.team)
    }

    def createFirstDummyPlayerTeam() {
        createHumanTeam()
    }

    def turnOffResourceGenerator(Cell cell) {
        Whitebox.setInternalState(cell.resourcesGenerator, "resourcesGeneratedOnStep", Integer.MAX_VALUE)
    }

    void setAgeLevel(Unit unit, int level) {
        unit.squad.setAgeStep(UnitAge.get(level).getEndsAtAgeStep() - 1)
    }

    def revalidateUnits() {
        for (Team team : new Array.ArrayIterable(Gdxg.core.world.teams)) {
            new Array.ArrayIterable(team.squads).each { it.rootValidator.forceTreeValidationFromRootNode() }
        }
    }

    def pressDialogOption(AbstractDialog dialog, String nameContains) {
        for (QuestOption option : dialog.choiceItems) {
            if (option.text.toLowerCase().contains(nameContains.toLowerCase())) {
                dialog.runChoiceClosure(option)
                return
            }
        }
        Assert.fail("no option found: " + nameContains)
    }

    def addItemsToBuild(Team team, Class cls) {
        def cost = ResourceCosts.getCost(cls)
        team.inventory.startBatch()
        for (ObjectMap.Entry<Class<? extends AbstractInventoryItem>, Integer> e : cost) {
            team.inventory.addItem(e.key, e.value)
        }
        team.inventory.endBatch()
    }

    enum DefaultPackageItem {
        MELEE(StickItem.class), RANGE(BowItem.class), BULLETS(ArrowItem.class), CLOTHES(SkinItem.class)

        private Class<? extends AbstractInventoryItem> inventoryItemClass

        DefaultPackageItem(Class<? extends AbstractInventoryItem> inventoryItemClass) {
            this.inventoryItemClass = inventoryItemClass
        }

        Class<? extends AbstractInventoryItem> getInventoryItemClass() {
            return inventoryItemClass
        }
    }

}
