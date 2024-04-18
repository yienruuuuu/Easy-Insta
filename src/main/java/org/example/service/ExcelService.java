package org.example.service;

import org.example.entity.IgUser;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Eric.Lee
 * Date:2024/4/10
 */
public interface ExcelService {

    /**
     * 生成Excel文件
     *
     * @param igUser   使用者資訊
     * @param response 回應
     */
    void createExcelForIgUser(IgUser igUser, HttpServletResponse response);
}
