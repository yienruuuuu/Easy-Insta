package org.example.bean.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author Eric.Lee
 * Date: 2024/3/4
 */
@Data
@Builder
public class CalculateMediaParams {
    private int likes;
    private int comments;
    private int shares;
    private int followers;
    private int postAmounts;
}
