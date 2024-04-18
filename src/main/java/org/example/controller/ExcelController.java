package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.IgUser;
import org.example.exception.ApiException;
import org.example.exception.SysCode;
import org.example.service.ExcelService;
import org.example.service.IgUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@Tag(name = "Excel controller", description = "開發導出Excel用API")
@RestController
@RequestMapping("excel")
public class ExcelController extends BaseController {
    private final IgUserService igUserService;
    private final ExcelService excelService;

    public ExcelController(IgUserService igUserService, ExcelService excelService) {
        this.igUserService = igUserService;
        this.excelService = excelService;
    }

    @GetMapping(value = "/exportComment/{igUserName}")
    @Operation(summary = "倒出user comment excel", description = "倒出user comment excel")
    public void export(HttpServletResponse response, @PathVariable String igUserName) {
        IgUser igUser = igUserService.findUserByIgUserName(igUserName)
                .orElseThrow(() -> new ApiException(SysCode.IG_USER_NOT_FOUND));
        excelService.createExcelForIgUser(igUser, response);
    }
}