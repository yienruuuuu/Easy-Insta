package org.example.bean.dto;

import com.github.instagram4j.instagram4j.models.user.Profile;
import lombok.*;

import java.util.List;

/**
 * @author Eric.Lee
 * Date: 2024/3/11
 */
@Data
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class LikerProfilesAndMaxIdDTO {
    private List<Profile> likerProfiles;
    private String maxId;
}
