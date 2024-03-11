package org.example.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Eric.Lee
 * Date: 2024/3/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadAccountRequest {
    private String account;
    private String password;
}
