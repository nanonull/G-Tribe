package conversion7.game.stages.world.unit;

import com.badlogic.gdx.utils.Array;

public class UnitNamesDatabase {
    public static final Array<String> maleNames = new Array<>();
    public static final Array<String> femaleNames = new Array<>();
    public static final Array<String> secondNames = new Array<>();
    static {
        maleNames.add("Bob");
        maleNames.add("Winston");
        maleNames.add("Jeff");
        maleNames.add("Dmitriy");
        maleNames.add("Oleg");
        maleNames.add("Alex");
        maleNames.add("Max");
        maleNames.add("Charlie");
        maleNames.add("Oscar");
        maleNames.add("Oliver");
        maleNames.add("Jack");
        maleNames.add("Harry");
        maleNames.add("James");
        maleNames.add("William");

        secondNames.add("Smith");
        secondNames.add("Brown");
        secondNames.add("Wilson");
        secondNames.add("Evans");
        secondNames.add("Murphy");
        secondNames.add("Walsh");
        secondNames.add("Davis");
        secondNames.add("Rodriguez");
        secondNames.add("Lam");
        secondNames.add("White");
        secondNames.add("Anderson");

        femaleNames.add("Amelia");
        femaleNames.add("Olivia");
        femaleNames.add("Isla");
        femaleNames.add("Margaret");
        femaleNames.add("Samantha");
        femaleNames.add("Bethany");
        femaleNames.add("Emma");
        femaleNames.add("Sophia");
        femaleNames.add("Mary");
        femaleNames.add("Patricia");
        femaleNames.add("Jennifer");
    }
}
