package quanli.ton.util;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XExcel {

    private static String[] col = null;
    private static String title = null;
    private static int index = 0;
    private static CellStyle cellStyleFormatNumber = null;
    private static List<Object[]> listObj = new ArrayList<>();

    public static void create(String path) throws IOException {
        final List<Object[]> objects = getListObjects();
        writeExcel(objects, path);
    }

    public static void clear() {
        col = null;
        listObj.clear();
        title = null;
        index = 0;
    }

    private static void writeExcel(List<Object[]> objects, String excelFilePath) throws IOException {
        Workbook workbook = getWorkbook(excelFilePath);

        Sheet sheet = workbook.createSheet("1");
        int rowIndex = 1;

        writeTitle(sheet);
        writeHeader(sheet, rowIndex);

        rowIndex++;

        for (Object[] obj : objects) {
            index++;
            Row row = sheet.createRow(rowIndex);
            writeObjects(rowIndex, obj, row);
            rowIndex++;
        }

        int numberOfColumn = sheet.getRow(1).getPhysicalNumberOfCells();
        autosizeColumn(sheet, numberOfColumn);

        createOutputFile(workbook, excelFilePath);
    }

    public static <T> void setObjects(List<T> list) {
        List<Object[]> result = new ArrayList<>();

        for (T item : list) {
            List<Object> row = new ArrayList<>();
            Field[] fields = item.getClass().getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true); // cho phép truy cập private field
                try {
                    row.add(field.get(item));
                } catch (IllegalAccessException e) {
                    row.add(null); // nếu lỗi thì thêm null
                }
            }

            result.add(row.toArray());
        }

        listObj = result;
    }

    private static List<Object[]> getListObjects() {
        return listObj;
    }

    private static Workbook getWorkbook(String excelFilePath) throws IOException {
        Workbook workbook = null;

        if (excelFilePath.endsWith("xlsx")) {
            workbook = new XSSFWorkbook();
        } else if (excelFilePath.endsWith("xls")) {
            workbook = new HSSFWorkbook();
        } else {
            throw new IllegalArgumentException("The specified file is not Excel file");
        }

        return workbook;
    }

    public static void setTitle(String title) {
        XExcel.title = title;
    }

    private static void writeTitle(Sheet sheet) {
        CellStyle cellStyle = createStyleForTitle(sheet);
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, col.length));
        titleCell.setCellStyle(cellStyle);
        titleCell.setCellValue(title);
    }

    public static void setHeader(String[] head) {
        col = head;
    }

    private static void writeHeader(Sheet sheet, int rowIndex) {
        CellStyle cellStyle = createStyleForHeader(sheet);
        Row row = sheet.createRow(rowIndex);
        Cell stt = row.createCell(0);
        stt.setCellValue("STT");
        stt.setCellStyle(cellStyle);
        for (int i = 0; i < col.length; i++) {
            Cell cell = row.createCell(i + 1);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(col[i]);
        }

    }

    private static void writeObjects(int rowIndex, Object[] objs, Row row) {
        if (cellStyleFormatNumber == null) {
            short format = (short) BuiltinFormats.getBuiltinFormat("#.##0");

            Workbook workbook = row.getSheet().getWorkbook();
            cellStyleFormatNumber = workbook.createCellStyle();
            cellStyleFormatNumber.setDataFormat(format);
        }
        Cell stt = row.createCell(0);
        stt.setCellValue(String.valueOf(index));
        for (int i = 0; i < objs.length; i++) {
            Object obj = objs[i];
            Cell cell = row.createCell(i + 1);
            cell.setCellValue(obj.toString());
        }
    }

    private static CellStyle createStyleForTitle(Sheet sheet) {
        XSSFColor customColor = new XSSFColor(new java.awt.Color(0, 102, 102), null);
        Font font = sheet.getWorkbook().createFont();
        font.setFontName("Times New Roman");
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        ((XSSFFont) font).setColor(new XSSFColor(new java.awt.Color(0, 102, 102), null));

        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return cellStyle;
    }

    private static CellStyle createStyleForHeader(Sheet sheet) {
        Font font = sheet.getWorkbook().createFont();
        font.setFontName("Times New Roman");
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        font.setColor(IndexedColors.WHITE.getIndex());

// Tạo màu custom RGB (0, 102, 102)
        XSSFColor customColor = new XSSFColor(new java.awt.Color(0, 102, 102), null);

// Tạo style cho header
        XSSFCellStyle cellStyle = (XSSFCellStyle) sheet.getWorkbook().createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(customColor);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return cellStyle;
    }

    private static void autosizeColumn(Sheet sheet, int lastColumn) {
        for (int columnIndex = 0; columnIndex < lastColumn; columnIndex++) {
            sheet.autoSizeColumn(columnIndex);
        }
    }

    private static void createOutputFile(Workbook workbook, String excelFilePath) throws IOException {
        try (OutputStream os = new FileOutputStream(excelFilePath)) {
            workbook.write(os);
        }
    }
}
