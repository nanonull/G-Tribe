package conversion7.engine.utils;

import com.badlogic.gdx.utils.GdxRuntimeException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExcelFile {

    /**
     * WARNING: cells without value in the middle of sheet are skipped...
     * W/A is to put some value like 'null' and handle it in parser
     */
    public static List<Map<String, String>> readXlsx(InputStream inputStream) throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = wb.getSheetAt(0);
        Iterator rows = sheet.rowIterator();
        Assert.assertTrue(rows.hasNext());

        XSSFRow row;
        List<Map<String, String>> toRows = new ArrayList<>();
        row = (XSSFRow) rows.next();
        ArrayList<String> headers = parseHeaders(row);

        while (rows.hasNext()) {
            row = (XSSFRow) rows.next();
            toRows.add(parseRow(row, headers));
        }

        return toRows;
    }

    private static Map<String, String> parseRow(XSSFRow row, ArrayList<String> headers) {
        Map<String, String> toRow = new LinkedHashMap<>();
        Iterator cells = row.cellIterator();
        for (String header : headers) {
            if (cells.hasNext()) {

                XSSFCell cell = (XSSFCell) cells.next();
                if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
                    toRow.put(header, cell.getStringCellValue().trim());

                } else if (cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {
                    String cellValue;
                    try {
                        cellValue = cell.getStringCellValue();
                    } catch (IllegalStateException e) {
                        if (e.getMessage().contains("Cannot get a text value from a numeric formula cell")) {
                            cellValue = parseNumeric(cell);
                        } else {
                            throw e;
                        }
                    }
                    toRow.put(header, cellValue);

                } else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
                    toRow.put(header, parseNumeric(cell));
                } else {
                    System.out.println("cell.getCellType()");
                    System.out.println(cell.getCellType());
                    throw new GdxRuntimeException("unknown cell type" + header);
                }
            } else {
                toRow.put(header, "");
            }
        }

        return toRow;
    }

    private static String parseNumeric(XSSFCell cell) {
        double doubleVal = cell.getNumericCellValue();
        String value = String.valueOf(doubleVal);
        if (value.endsWith(".0")) {
            value = value.replaceAll(".0$", "");
        }
        return value;
    }

    private static ArrayList<String> parseHeaders(XSSFRow row) {
        ArrayList<String> headers = new ArrayList<>();
        Iterator cells = row.cellIterator();
        while (cells.hasNext()) {
            XSSFCell cell = (XSSFCell) cells.next();
            headers.add(cell.getRichStringCellValue().toString().trim());
        }
        return headers;
    }

    public static void writeXLSXFile(File file) throws IOException {

        String sheetName = "Sheet1";//name of sheet

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(sheetName);

        //iterating r number of rows
        for (int r = 0; r < 5; r++) {
            XSSFRow row = sheet.createRow(r);

            //iterating c number of columns
            for (int c = 0; c < 5; c++) {
                XSSFCell cell = row.createCell(c);

                cell.setCellValue("Cell " + r + " " + c);
            }
        }

        FileOutputStream fileOut = new FileOutputStream(file);

        //write this workbook to an Outputstream.
        wb.write(fileOut);
        fileOut.flush();
        fileOut.close();
    }

}