package conversion7.game.ui.utils;

import static java.lang.String.valueOf;

public class UiUtils {

    public static String getShortStringFromInt(int original) {
        if (original >= 1000000000) {
            return ">G";
        } else if (original >= 1000000) {
            return original / 1000000 + "M";

        } else if (original >= 1000) {
            StringBuilder sb = new StringBuilder();
            if (original < 10000) {
                // 9876 to 9.87K
                sb.append((valueOf(original)).substring(0, 1))
                        .append(".")
                        .append((valueOf(original)).substring(1, 3))
                        .append("K");
            } else if (original < 100000) {
                // 98765 to 98.7K
                sb.append((valueOf(original)).substring(0, 2))
                        .append(".")
                        .append(("" + original).substring(2, 3))
                        .append("K");
            } else {
                // 987654 to 987K
                sb.append((valueOf(original)).substring(0, 3))
                        .append("K");
            }
            return sb.toString();

        } else {
            return valueOf(original);
        }
    }

    public static String getTemperatureString(int temperature) {
        return temperature > 0 ? "+" + temperature : valueOf(temperature);
    }
}
