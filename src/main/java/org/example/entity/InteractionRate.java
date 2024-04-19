package org.example.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Eric.Lee
 * Date: 2024/3/4
 */
@Entity
@Table(name = "interaction_rate", schema = "crawler_ig")
@RequiredArgsConstructor
@AllArgsConstructor
@Slf4j
@Schema(description = "Interaction_Rate entity")
@Getter
@Setter
@ToString
@Builder
public class InteractionRate {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ig_user_id", referencedColumnName = "id", nullable = false)
    private IgUser igUserId;

    @Column(name = "like_count")
    private Integer likeCount;

    @Column(name = "comment_count")
    private Integer commentCount;

    @Column(name = "reshare_count")
    private Integer reshareCount;

    @Column(name = "followers")
    private Integer followers;

    @Column(name = "media_count")
    private Integer mediaCount;

    @Column(name = "insert_time")
    private LocalDateTime insertTime;
}
