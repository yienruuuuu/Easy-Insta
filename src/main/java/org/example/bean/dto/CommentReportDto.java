package org.example.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Eric.Lee
 * Date: 2024/4/2
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class CommentReportDto {
    private String userName;
    private Long commentCount;
    private Long likeCount;
}
