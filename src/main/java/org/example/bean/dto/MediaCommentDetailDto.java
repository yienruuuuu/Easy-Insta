package org.example.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Eric.Lee
 * Date: 2024/4/3
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaCommentDetailDto {
    private String message;
    private Long mediaPk;
    private String commenterUserName;
    private String commenterFullName;
    private String commentText;
    private Boolean isPrivateAccount;
    private Boolean isVerifiedAccount;
    private Long latestReelMedia;
    private Integer likeCount;

    /**
     * 將DTO的屬性轉換為Object列表
     *
     * @return DTO屬性的List<Object>
     */
    public List<Object> toList() {
        return List.of(
                message != null ? message : "N/A",
                mediaPk != null ? mediaPk.toString() : "N/A",
                commenterUserName != null ? commenterUserName : "N/A",
                commenterFullName != null ? commenterFullName : "N/A",
                commentText != null ? commentText : "N/A",
                Boolean.TRUE.equals(isPrivateAccount) ? "是" : "否",
                Boolean.TRUE.equals(isVerifiedAccount) ? "是" : "否",
                latestReelMedia != null ? "是" : "否",
                likeCount != null ? likeCount.toString() : "0"
        );
    }
}
