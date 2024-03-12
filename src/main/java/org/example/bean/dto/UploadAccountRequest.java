package org.example.bean.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Eric.Lee
 * Date: 2024/3/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadAccountRequest {
    @ExcelProperty("帳號")
    private String account;
    @ExcelProperty("密碼")
    private String password;
}
