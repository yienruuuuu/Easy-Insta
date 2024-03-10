package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;

/**
 * @author Eric.Lee
 * Date:2024/3/11
 */
@Entity
@Table(name = "media_liker", schema = "crawler_ig")
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "media liker資料")
@Getter
@Setter
public class MediaLiker {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "media_id", unique = true)
    @JsonIgnore
    private Media mediaId;

    @Column(name = "liker_user_name")
    private String likerUserName;
    
    @Column(name = "liker_full_name")
    private String likerFullName;
    
    @Column(name = "liker_pk")
    private Long likerPk;
    
    @Column(name = "liker_is_private")
    private boolean likerIsPrivate;
    
    @Column(name = "liker_is_verified")
    private boolean likerIsVerified;
    
    @Column(name = "liker_profile_pic_id")
    private String likerProfilePicId;
    
    @Column(name = "liker_profile_pic_url")
    private String likerProfilePicUrl;
    
    @Column(name = "liker_latest_reel_media")
    private Long likerLatestReelMedia;
}
