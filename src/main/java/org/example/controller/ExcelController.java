package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.entity.IgUser;
import org.example.exception.ApiException;
import org.example.exception.SysCode;
import org.example.service.IgUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Tag(name = "Excel controller", description = "開發導出Excel用API")
@RestController
@RequestMapping("excel")
public class ExcelController extends BaseController {
    private final IgUserService igUserService;

    public ExcelController(IgUserService igUserService) {
        this.igUserService = igUserService;
    }

    @GetMapping(value = "/exportComment/{igUserName}")
    @Operation(summary = "倒出user comment excel", description = "倒出user comment excel")
    public void export(HttpServletResponse response, @PathVariable String igUserName) {
        IgUser igUser = igUserService.findUserByIgUserName(igUserName)
                .orElseThrow(() -> new ApiException(SysCode.IG_USER_NOT_FOUND));
        // 創建Excel工作簿和工作表
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("目錄");
        // 填充數據到工作表
        fillUserData(sheet, igUser);

        // 設置合併單元格和樣式
        setCellStylesAndMergeCells(sheet);

        // 設置響應頭部，告訴瀏覽器這是一個需要下載的檔案
        String fileName = URLEncoder.encode(igUser.getUserName(), StandardCharsets.UTF_8) + ".xlsx";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try {
            // 將工作簿寫入HTTP響應流
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (Exception e) {
            log.error("export excel error", e);
            throw new ApiException(SysCode.EXCEL_OUTPUT_FAILED);
        }
    }

    private void fillUserData(Sheet sheet, IgUser igUser) {
        // 假設IgUser有getUserName(), getFollowingCount(), getFollowerCount(), 和 getCreationDate()方法

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
        String[] headers = {"目標帳號", "用戶全名", "貼文數量", "粉絲數", "生產日期"};
        String[] data = {
                igUser.getUserName(),
                igUser.getFullName(),
                String.valueOf(igUser.getMediaCount()),
                String.valueOf(igUser.getFollowerCount()),
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
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

    private void setCellStylesAndMergeCells(Sheet sheet) {
        // 創建工作簿
        Workbook workbook = sheet.getWorkbook();

        // 設定框線樣式
        CellStyle borderStyle = workbook.createCellStyle();
        borderStyle.setBorderBottom(BorderStyle.THIN);
        borderStyle.setBorderTop(BorderStyle.THIN);
        borderStyle.setBorderRight(BorderStyle.THIN);
        borderStyle.setBorderLeft(BorderStyle.THIN);

        // 設定表頭樣式
        Font headerFont = workbook.createFont();
        headerFont.setFontName("Malgun Gothic Semilight");
        headerFont.setFontHeightInPoints((short) 24);
        headerFont.setBold(true);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillForegroundColor(getCustomColor());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setFont(headerFont);

        // 設定普通單元格樣式
        Font cellFont = workbook.createFont();
        cellFont.setFontName("Malgun Gothic Semilight");
        cellFont.setFontHeightInPoints((short) 15);
        cellFont.setBold(true);

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(cellFont);
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle.setFillForegroundColor(getCustomColor());
        cellStyle.setWrapText(true); // 自動換行
        cellStyle.cloneStyleFrom(borderStyle); // 使用相同的框線樣式

        // 設定樣式到單元格
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (row.getRowNum() == 0) { // 表頭行
                    cell.setCellStyle(headerStyle);
                } else {
                    cell.setCellStyle(cellStyle);
                }
            }
        }

        // 根據內容調整列寬
        for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private XSSFColor getCustomColor() {
        byte[] rgb = new byte[3];
        rgb[0] = (byte) 133; // R - 紅色分量
        rgb[1] = (byte) 223; // G - 綠色分量
        rgb[2] = (byte) 255; // B - 藍色分量
        return new XSSFColor(rgb, null);
    }
}