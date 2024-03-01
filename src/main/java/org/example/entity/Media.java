package org.example.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

/**
 * @author Eric.Lee
 * Date: 2024/2/29
 */
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@Slf4j
@Schema(description = "Media entity")
@Getter
@Setter
@ToString
@Table(name = "media", schema = "crawler_ig")
@Builder
public class Media {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ig_user_id", referencedColumnName = "id", nullable = false)
    private IgUser igUserId;

    @Column(name = "media_pk")
    private Long mediaPk;

    @Column(name = "media_id")
    private String mediaId;

    @Column(name = "play_count")
    private Integer playCount;

    @Column(name = "fb_play_count")
    private Integer fbPlayCount;

    @Column(name = "like_count")
    private Integer likeCount;

    @Column(name = "fb_like_count")
    private Integer fbLikeCount;

    @Column(name = "reshare_count")
    private Integer reshareCount;

    @Column(name = "comment_count")
    private Integer commentCount;

    @Column(name = "number_of_qualities")
    private Integer numberOfQualities;
}
