package conversion7.game.classes;

/**
 * Time-frame in years.<br><br>
 * Example:
 * lowerBorder == -99 000 000 == 99 billions years BC
 */
public class TimeFrame {

    int lowerBorder;
    int higherBorder;

    public TimeFrame(int lowerBorder, int higherBorder) {
        this.lowerBorder = lowerBorder;
        this.higherBorder = higherBorder;
    }


    public TimeFrame(int lowerBorder) {
        this(lowerBorder, lowerBorder + 1000);
    }
}
