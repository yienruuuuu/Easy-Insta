package org.example.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
