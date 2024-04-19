package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.bean.dto.CommentReportDto;
import org.example.bean.dto.MediaCommentDetailDto;
import org.example.entity.IgUser;
import org.example.exception.ApiException;
import org.example.exception.SysCode;
import org.example.service.ExcelService;
import org.example.service.MediaCommentService;
import org.example.service.MediaService;
import org.example.utils.ExcelUtils;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * @author Eric.Lee
 * Date:2024/4/10
 */
@Service("excelService")
@Slf4j
public class ExcelServiceImpl implements ExcelService {
    private final MediaCommentService mediaCommentService;
    private final MediaService mediaService;

    public ExcelServiceImpl(MediaCommentService mediaCommentService, MediaService mediaService) {
        this.mediaCommentService = mediaCommentService;
        this.mediaService = mediaService;
    }

    @Override
    public void createExcelForIgUser(IgUser igUser, HttpServletResponse response) {
        Map<String, Long> hashTagMap = mediaService.analyzeHashtagsAndSort(igUser);

        try {
            Workbook workbook = new XSSFWorkbook();
            // 調用私有方法來添加工作表等
            setSheetFirst(workbook, igUser, hashTagMap);
            setSheetSecond(workbook, igUser);
            setSheetThird(workbook, igUser);
            setSheetFourth(workbook);

            String fileName = URLEncoder.encode(igUser.getUserName(), StandardCharsets.UTF_8) + ".xlsx";
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (Exception e) {
            log.error("export excel error", e);
            throw new ApiException(SysCode.EXCEL_OUTPUT_FAILED);
        }
    }

    //private

    private void setSheetFirst(Workbook workbook, IgUser igUser, Map<String, Long> hashTagMap) {
        Sheet sheet = workbook.createSheet("目錄Index");
        // 填充數據到工作表
        fillUserData(sheet, igUser, hashTagMap);
        // 設置合併單元格和樣式
        setCellStylesAndMergeCellsForCommentIndex(sheet);
    }

    private void setSheetSecond(Workbook workbook, IgUser igUser) {
        Sheet sheet = workbook.createSheet("統計資料CountData");
        List<CommentReportDto> commentIntegration = mediaCommentService.findCommentSummary(igUser);
        // 填充數據到工作表
        fillUserData(sheet, commentIntegration);
        // 設單元格樣式
        setCellStylesAndMergeCellsForCommentIntegration(sheet);
    }

    private void setSheetThird(Workbook workbook, IgUser igUser) {
        Sheet sheet = workbook.createSheet("明細資料DetailData");
        List<MediaCommentDetailDto> commentDetail = mediaCommentService.findCommentDetail(igUser);
        // 填充數據到工作表
        fillDetailData(sheet, commentDetail);
        // 設單元格樣式
        setCellStylesForCommentDetail(sheet);
    }

    private void setSheetFourth(Workbook workbook) {
        Sheet sheet = workbook.createSheet("私訊列表");
        // 填充數據到工作表
        fillPromoteTitle(sheet);
        // 設單元格樣式
        setCellStylesForPromoteTitle(sheet);
    }

    private void fillUserData(Sheet sheet, IgUser igUser, Map<String, Long> hashTagMap) {
        // 創建表頭行
        Row headerRow = sheet.createRow(0);

        // 創建表頭單元格
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("Easy Insta"); // 設定你的表頭內容

        // 合併A0和B0
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));

        // 設定行資料
        int rowNum = 1; // 從第二行開始，因為第一行是表頭
        // 範例的用戶資料，依您的IgUser實體的屬性進行調整
        String[] headers = {"目標帳號", "用戶全名", "貼文數量", "粉絲數", "生產日期", "常用HashTag"};
        String[] data = {
                igUser.getUserName(),
                igUser.getFullName(),
                String.valueOf(igUser.getMediaCount()),
                String.valueOf(igUser.getFollowerCount()),
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                hashTagMap.toString()
        };

        // 創建並填充資料行
        for (int i = 0; i < data.length; i++) {
            Row row = sheet.createRow(rowNum++);
            Cell cell = row.createCell(0);
            cell.setCellValue(headers[i]);
            cell = row.createCell(1);
            cell.setCellValue(data[i]);
        }
    }

    private void fillUserData(Sheet sheet, List<CommentReportDto> commentIntegration) {
        Row commentHeaderRow = sheet.createRow(0);
        String[] commentHeaders = {"帳號", "帳號全名", "留言次數", "留言被按讚數"};
        for (int i = 0; i < commentHeaders.length; i++) {
            Cell headerCell = commentHeaderRow.createCell(i);
            headerCell.setCellValue(commentHeaders[i]);
        }

        // 填充評論整合數據
        int rowNum = 1;
        for (CommentReportDto comment : commentIntegration) {
            Row row = sheet.createRow(rowNum++);
            Cell cell = row.createCell(0);
            cell.setCellValue(comment.getUserName());

            cell = row.createCell(1);
            cell.setCellValue(comment.getUserFullName());

            cell = row.createCell(2);
            cell.setCellValue(comment.getCommentCount());

            cell = row.createCell(3);
            cell.setCellValue(comment.getLikeCount());
        }
    }

    private void fillDetailData(Sheet sheet, List<MediaCommentDetailDto> commentDetail) {
        Row commentHeaderRow = sheet.createRow(0);
        String[] commentHeaders = {"留言貼文", "留言文章id", "留言帳號", "帳號全名", "留言內容", "公開帳號", "Meta驗證", "當下是否有發限動", "留言被按讚數"};
        for (int i = 0; i < commentHeaders.length; i++) {
            Cell headerCell = commentHeaderRow.createCell(i);
            headerCell.setCellValue(commentHeaders[i]);
        }

        // 填充評論數據
        int rowNum = 1;
        for (MediaCommentDetailDto comment : commentDetail) {
            List<Object> values = comment.toList();
            ExcelUtils.createRowAndFillData(sheet, rowNum++, values);
        }
    }

    private void fillPromoteTitle(Sheet sheet) {
        Row commentHeaderRow = sheet.createRow(0);
        String[] commentHeaders = {"帳號", "帳號全名", "英文訊息", "中文訊息", "日文訊息", "俄文訊息", "影片網址"};
        for (int i = 0; i < commentHeaders.length; i++) {
            Cell headerCell = commentHeaderRow.createCell(i);
            headerCell.setCellValue(commentHeaders[i]);
        }
    }


    private void setCellStylesAndMergeCellsForCommentIndex(Sheet sheet) {
        // 取得工作簿
        Workbook workbook = sheet.getWorkbook();
        // 取得自訂顏色
        XSSFColor customColor = ExcelUtils.getCustomColor((byte) 133, (byte) 223, (byte) 255);

        // 建立表頭樣式和儲存格樣式
        CellStyle headerStyle = ExcelUtils.createHeaderCellStyle(workbook, customColor, (short) 24);
        CellStyle cellStyle = ExcelUtils.createCellStyle(workbook, customColor, (short) 15, HorizontalAlignment.RIGHT);
        // 應用程式樣式到工作表
        ExcelUtils.applyStylesToSheet(sheet, headerStyle, cellStyle);
        // 調整列寬
        ExcelUtils.setAutoColumnWidth(sheet);
        ExcelUtils.setAutoColumnWidthForChinese(sheet);
    }

    private void setCellStylesAndMergeCellsForCommentIntegration(Sheet sheet) {
        // 取得工作簿
        Workbook workbook = sheet.getWorkbook();
        // 取得自訂顏色
        XSSFColor customColor = ExcelUtils.getCustomColor((byte) 241, (byte) 169, (byte) 131);
        XSSFColor whiteColor = ExcelUtils.getCustomColor((byte) 255, (byte) 255, (byte) 255);

        // 建立表頭樣式和儲存格樣式
        CellStyle headerStyle = ExcelUtils.createHeaderCellStyle(workbook, customColor, (short) 15);
        CellStyle cellStyle = ExcelUtils.createCellStyle(workbook, whiteColor, (short) 13, HorizontalAlignment.LEFT);
        // 應用程式樣式到工作表
        ExcelUtils.applyStylesToSheet(sheet, headerStyle, cellStyle);

        // 調整列寬
        Map<Integer, Integer> columnWidths = Map.of(
                0, 40,
                1, 60,
                2, 60,
                3, 60
        );
        ExcelUtils.setCustomColumnWidth(sheet, columnWidths);
    }

    private void setCellStylesForCommentDetail(Sheet sheet) {
        // 取得工作簿
        Workbook workbook = sheet.getWorkbook();
        // 取得自訂顏色
        XSSFColor customColor = ExcelUtils.getCustomColor((byte) 241, (byte) 169, (byte) 131);
        XSSFColor whiteColor = ExcelUtils.getCustomColor((byte) 255, (byte) 255, (byte) 255);

        // 建立表頭樣式和儲存格樣式
        CellStyle headerStyle = ExcelUtils.createHeaderCellStyle(workbook, customColor, (short) 15);
        CellStyle cellStyle = ExcelUtils.createCellStyle(workbook, whiteColor, (short) 13, HorizontalAlignment.LEFT);
        // 應用程式樣式到工作表
        ExcelUtils.applyStylesToSheet(sheet, headerStyle, cellStyle);

        // 調整列寬
        Map<Integer, Integer> columnWidths = Map.of(
                0, 40,
                1, 30,
                2, 40,
                3, 40,
                4, 30,
                5, 16,
                6, 16,
                7, 30,
                8, 20
        );
        ExcelUtils.setCustomColumnWidth(sheet, columnWidths);
    }

    private void setCellStylesForPromoteTitle(Sheet sheet) {
        // 取得工作簿
        Workbook workbook = sheet.getWorkbook();
        // 取得自訂顏色
        XSSFColor customColor = ExcelUtils.getCustomColor((byte) 241, (byte) 169, (byte) 131);
        // 建立表頭樣式和儲存格樣式
        CellStyle headerStyle = ExcelUtils.createHeaderCellStyle(workbook, customColor, (short) 15);
        // 應用程式樣式到工作表
        ExcelUtils.applyStylesToSheet(sheet, headerStyle, null);

        // 調整列寬
        Map<Integer, Integer> columnWidths = Map.of(
                0, 40,
                1, 30,
                2, 40,
                3, 40,
                4, 30,
                5, 16,
                6, 16
        );
        ExcelUtils.setCustomColumnWidth(sheet, columnWidths);
    }
}
