package org.example.bean.dto;

import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import lombok.*;

import java.util.List;

/**
 * @author Eric.Lee
 * Date:2024/2/19
 */
@Data
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class PostsAndMaxIdDTO {
    private List<TimelineMedia> medias;
    private String maxId;
}
