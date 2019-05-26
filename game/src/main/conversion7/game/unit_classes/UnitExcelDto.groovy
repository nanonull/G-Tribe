package conversion7.game.unit_classes

import com.badlogic.gdx.utils.Array
import conversion7.game.stages.world.unit.Unit

class UnitExcelDto {

    Class<? extends Unit> clazz
    int height
    int str
    int agi
    int vit
    String childClasses
    Array<Class<? extends Unit>> childClassesList = new Array<>()
    int lvl
    /**0 - means min health baseTemperature, 100 - max health baseTemperature*/
    Integer migrationTemperaturePercent
    Boolean aggrAnimal
    Boolean scaringAnimal
    String classShortName

    Integer skin
    Integer tooth
    Integer fang
    Integer tusk

    def calcChildClassesList() {
        if (!childClasses.empty) {
            childClasses.split(",").each {
                childClassesList.add(Class.forName(it.trim()))
            }
        }
        return this
    }

}
