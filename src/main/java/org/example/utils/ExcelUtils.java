package org.example.utils;

import org.apache.poi.ss.usermodel.*;

/**
 * @author Eric.Lee
 * Date: 2024/4/1
 */
public final class ExcelUtils {
    private ExcelUtils() {
        // 拋出異常是為了防止透過反射呼叫私有建構函數
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
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

    public static void setStyle(Sheet sheet, CellStyle headerStyle, CellStyle cellStyle) {
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

    public static void setBorder(CellStyle cellStyle) {
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
    }
}
