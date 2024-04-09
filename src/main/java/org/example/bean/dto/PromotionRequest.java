package org.example.bean.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Eric.Lee
 * Date: 2024/4/8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionRequest {
    @ExcelProperty("帳號")
    private String account;
    @ExcelProperty("帳號全名")
    private String accountFullName;
    @ExcelProperty("英文訊息")
    private String textEn;
    @ExcelProperty("中文訊息")
    private String textZhTw;
    @ExcelProperty("日文訊息")
    private String textJa;
    @ExcelProperty("俄文訊息")
    private String textRu;
    @ExcelProperty("影片網址")
    private String postUrl;
}
