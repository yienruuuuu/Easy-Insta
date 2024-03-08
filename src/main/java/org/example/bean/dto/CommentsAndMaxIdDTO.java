package org.example.bean.dto;

import com.github.instagram4j.instagram4j.models.media.timeline.Comment;
import lombok.*;

import java.util.List;

/**
 * @author Eric.Lee
 * Date:2024/3/18
 */
@Data
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class CommentsAndMaxIdDTO {
    private List<Comment> comments;
    private String maxId;
}
