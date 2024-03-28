package org.example.controller;

import com.alibaba.excel.EasyExcel;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.IgUser;
import org.example.service.IgUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;

@Slf4j
@Tag(name = "Excel controller", description = "開發導出Excel用API")
@RestController
@RequestMapping("excel")
public class ExcelController extends BaseController {
    private final IgUserService igUserService;

    public ExcelController(IgUserService igUserService) {
        this.igUserService = igUserService;
    }

    @GetMapping(value = "/exportTest")
    @Operation(summary = "倒出excel測試", description = "倒出excel測試")
    public void export(HttpServletResponse response) {
        try {
            // 設置響應頭部，告訴瀏覽器這是一個需要下載的檔案
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 設置檔案名
            String fileName = URLEncoder.encode("你的檔案名稱", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

            // 使用EasyExcel寫入Excel到response的OutputStream
            EasyExcel.write(response.getOutputStream()).sheet("sheet1").doWrite(data());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // private

    private List<IgUser> data() {
        IgUser igUser = igUserService.findUserByIgUserName("moyouran2023").orElseThrow(() -> new RuntimeException("用戶不存在"));
        return Lists.newArrayList(igUser);
    }
}