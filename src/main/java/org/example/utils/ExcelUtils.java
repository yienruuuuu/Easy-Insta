package org.example.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.util.Map;

/**
 * @author Eric.Lee
 * Date: 2024/4/1
 */
public final class ExcelUtils {
    private ExcelUtils() {
        // 拋出異常是為了防止透過反射呼叫私有建構函數
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * 建立並返回表頭的樣式。
     *
     * @param workbook 工作簿
     * @return 表头样式
     */
    public static CellStyle createHeaderCellStyle(Workbook workbook, XSSFColor color, short fontSize) {
        Font headerFont = workbook.createFont();
        headerFont.setFontName("Malgun Gothic Semilight");
        headerFont.setFontHeightInPoints(fontSize);
        headerFont.setBold(true);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillForegroundColor(color);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorder(headerStyle);
        return headerStyle;
    }

    /**
     * 建立並傳回普通儲存格的樣式。
     *
     * @param workbook 工作簿
     * @return 单元格样式
     */
    public static CellStyle createCellStyle(Workbook workbook, XSSFColor color, short fontSize, HorizontalAlignment alignment) {
        Font cellFont = workbook.createFont();
        cellFont.setFontName("Malgun Gothic Semilight");
        cellFont.setFontHeightInPoints(fontSize);
        cellFont.setBold(true);

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(cellFont);
        cellStyle.setAlignment(alignment);
        cellStyle.setFillForegroundColor(color);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorder(cellStyle);
        return cellStyle;
    }

    public static void setAutoColumnWidth(Sheet sheet) {
        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            for (int j = 0; j < row.getLastCellNum(); j++) {
                sheet.autoSizeColumn(j);
            }
        }
    }

    public static void setAutoColumnWidthForChinese(Sheet sheet) {
        int columnCount = sheet.getRow(0).getLastCellNum();
        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
            sheet.autoSizeColumn(columnIndex);
        }

        // 然後，基於自動調整的結果，為包含中文的欄位增加額外寬度
        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
            int currentWidth = sheet.getColumnWidth(columnIndex);
            int extraWidth = currentWidth / 4;
            int newWidth = Math.min(255 * 256, currentWidth + extraWidth);
            sheet.setColumnWidth(columnIndex, newWidth);
        }
    }

    /**
     * 設定固定的自訂列寬。
     *
     * @param sheet        工作表
     * @param columnWidths 列寬映射
     */
    public static void setCustomColumnWidth(Sheet sheet, Map<Integer, Integer> columnWidths) {
        for (Map.Entry<Integer, Integer> entry : columnWidths.entrySet()) {
            int column = entry.getKey();
            int width = entry.getValue();
            sheet.setColumnWidth(column, width * 256); // 在POI中列寬的單位是1/256個字符寬度
        }
    }

    /**
     * 應用樣式到工作表的所有儲存格。
     *
     * @param sheet       工作表
     * @param headerStyle 表頭樣式
     * @param cellStyle   單元格樣式
     */
    public static void applyStylesToSheet(Sheet sheet, CellStyle headerStyle, CellStyle cellStyle) {
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (row.getRowNum() == 0) {
                    cell.setCellStyle(headerStyle);
                } else {
                    cell.setCellStyle(cellStyle);
                }
            }
        }
    }

    /**
     * 設定單元格的邊框。
     *
     * @param cellStyle 單元格樣式
     */
    public static void setBorder(CellStyle cellStyle) {
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
    }

    /**
     * 取得自訂顏色。
     *
     * @return 自訂顏色對象
     */
    public static XSSFColor getCustomColor(byte r, byte g, byte b) {
        byte[] rgb = new byte[3];
        rgb[0] = r; // R - 红色分量
        rgb[1] = g; // G - 绿色分量
        rgb[2] = b; // B - 蓝色分量
        return new XSSFColor(rgb, null);
    }
}
