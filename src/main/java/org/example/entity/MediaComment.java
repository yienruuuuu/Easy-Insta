package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;

/**
 * @author Eric.Lee
 * Date: 2024/3/8
 */
@Entity
@Table(name = "media_comment", schema = "crawler_ig")
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "media comment資料")
@Getter
@Setter
public class MediaComment {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "media_id", unique = true)
    @JsonIgnore
    private Media media;
    
    @Column(name = "text")
    private String text;
    
    @Column(name = "commenter_full_name")
    private String commenterFullName;
    
    @Column(name = "commenter_user_id")
    private Long commenterUserId;
    
    @Column(name = "commenter_user_name")
    private String commenterUserName;
    
    @Column(name = "comment_pk")
    private String commentPk;
    
    @Column(name = "commenter_is_private")
    private boolean commenterIsPrivate;
    
    @Column(name = "commenter_is_verified")
    private boolean commenterIsVerified;
    
    @Column(name = "commenter_profile_pic_id")
    private String commenterProfilePicId;
    
    @Column(name = "commenter_profile_pic_url")
    private String commenterProfilePicUrl;
    
    @Column(name = "commenter_latest_reel_media")
    private long commenterLatestReelMedia;

    @Column(name = "content_type")
    private String contentType;
    
    @Column(name = "status")
    private String status;

    @Column(name = "comment_like_count")
    private Integer commentLikeCount;
}
