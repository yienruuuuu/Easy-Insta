package org.example.bean.dto;

import com.github.instagram4j.instagram4j.models.user.Profile;
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
public class FollowersAndMaxIdDTO {
    private List<Profile> followers;
    private String maxId;
}
