package org.example.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.persistence.*;

/**
 * @author Eric.Lee
 * Date: 2024/2/17
 */
@Entity
@Schema(description = "追蹤者")
@Getter
@Setter
@Table(name = "followers", schema = "crawler_ig")
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Followers {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ig_user_id", referencedColumnName = "id", nullable = false)
    private IgUser igUser;

    @Column(name = "follower_pk")
    private Long followerPk;

    @Column(name = "follower_user_name")
    private String followerUserName;

    @Column(name = "follower_full_name")
    private String followerFullName;

    @Column(name = "is_private")
    private Boolean isPrivate;

    @Lob
    @Column(name = "profile_pic_url")
    private String profilePicUrl;

    @Column(name = "profile_pic_id")
    private String profilePicId;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "has_anonymous_profile_picture")
    private Boolean hasAnonymousProfilePicture;

    @Column(name = "latest_reel_media")
    private Long latestReelMedia;

    @Column(name = "post_count")
    private Integer postCount;

    @Column(name = "follower_count")
    private Integer followerCount;

    @Column(name = "following_count")
    private Integer followingCount;

}
