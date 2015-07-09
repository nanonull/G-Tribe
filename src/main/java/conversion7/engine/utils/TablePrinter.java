package conversion7.engine.utils;


import org.slf4j.Logger;

import static conversion7.engine.utils.Utils.error;

public class TablePrinter {

    private static final Logger LOG = Utils.getLoggerForClass();

    private static final char BORDER_KNOT = '+';
    private static final char HORIZONTAL_BORDER = '-';
    private static final char VERTICAL_BORDER = '|';

    private static final String DEFAULT_AS_NULL = "(NULL)";

    public static String getTableToString(String[][] table) {
        if (table == null) {
            throw new IllegalArgumentException("No tabular data provided");
        }
        if (table.length == 0) {
            error("Empty table could not be printed!");
            return null;
        }

        final int[] widths = new int[getMaxColumns(table)];
        adjustColumnWidths(table, widths);
        return prepareTable(table, widths, getHorizontalBorder(widths));
    }

    private static boolean verifyTableRowsHaveEqualLength(String[][] table) {
        int headerLength = table[0].length;
        for (int row = 1; row < table.length - 1; row++) {
            if (table[row].length != headerLength) {
                error(String.format("Row: %s has length (%s) - another than Table header length (%s).%n" +
                        "Table will not be printed", row, table[row].length, headerLength));

                StringBuilder errorRowText = new StringBuilder(" Row with error: ");
                for (int col = 0; col < table[row].length - 1; col++) {
                    errorRowText.append(table[row][col]).append(", ");
                }
                error(errorRowText.toString());

                return false;
            }
        }
        return true;
    }

    private static String prepareTable(String[][] table, int widths[], String horizontalBorder) {
        final int lineLength = horizontalBorder.length();
        StringBuilder stringBuilder = new StringBuilder("\n");
        stringBuilder.append(horizontalBorder).append("\n");
        for (final String[] row : table) {
            if (row != null) {
                stringBuilder.append(getRow(row, widths, lineLength)).append("\n");
                stringBuilder.append(horizontalBorder).append("\n");
            }
        }
        return stringBuilder.toString();
    }

    private static String getRow(String[] row, int[] widths, int lineLength) {
        final StringBuilder builder = new StringBuilder(lineLength).append(VERTICAL_BORDER);
        final int maxWidths = widths.length;
        for (int i = 0; i < maxWidths; i++) {
            builder.append(padRight(getCellValue(safeGet(row, i, null)), widths[i])).append(VERTICAL_BORDER);
        }
        return builder.toString();
    }

    private static String getHorizontalBorder(int[] widths) {
        final StringBuilder builder = new StringBuilder(256);
        builder.append(BORDER_KNOT);
        for (final int w : widths) {
            for (int i = 0; i < w; i++) {
                builder.append(HORIZONTAL_BORDER);
            }
            builder.append(BORDER_KNOT);
        }
        return builder.toString();
    }

    private static int getMaxColumns(String[][] rows) {
        int max = 0;
        for (final String[] row : rows) {
            if (row != null && row.length > max) {
                max = row.length;
            }
        }
        return max;
    }

    private static void adjustColumnWidths(String[][] rows, int[] widths) {
        for (final String[] row : rows) {
            if (row != null) {
                for (int c = 0; c < widths.length; c++) {
                    final String cv = getCellValue(safeGet(row, c, DEFAULT_AS_NULL));
                    final int l = cv.length();
                    if (widths[c] < l) {
                        widths[c] = l;
                    }
                }
            }
        }
    }

    private static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    private static String safeGet(String[] array, int index, String defaultValue) {
        return index < array.length ? array[index] : defaultValue;
    }

    private static String getCellValue(Object value) {
        return value == null ? DEFAULT_AS_NULL : value.toString();
    }

    public static void demoExample() {
        LOG.info(getTableToString(new String[][]{
                new String[]{"FIRST NAME", "LAST NAME", "DATE OF BIRTH", "NOTES"},
                new String[]{"Joe", "Smith", "November 2, 1972"},
                null,
                new String[]{"John", "Doe", "April 29, 1970", "Big Brother"},
                new String[]{"Jack", null, null, "(yes, no last name)"},
        }));
    }

}
