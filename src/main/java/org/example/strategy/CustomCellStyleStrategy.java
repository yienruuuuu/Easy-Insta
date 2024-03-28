package org.example.strategy;

import com.alibaba.excel.metadata.Cell;
import com.alibaba.excel.metadata.data.CellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.*;

import java.util.List;

/**
 * @author Eric.Lee
 * Date: 2024/3/28
 */
public class CustomCellStyleStrategy implements CellWriteHandler {

    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, List<CellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        // 在這裡根據是否是表頭來設置不同的樣式
        Workbook workbook = writeSheetHolder.getSheet().getWorkbook();
        CellStyle cellStyle = workbook.createCellStyle();

        // 設定邊框、對齊方式等通用樣式
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        if (isHead != null && isHead) {
            // 設置表頭的樣式
            cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            cellStyle.setFont(font);
        } else {
            // 設置數據行的樣式
            cellStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        cell.setCellStyle(cellStyle);
    }
}

