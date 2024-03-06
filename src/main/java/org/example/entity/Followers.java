package org.example.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;

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

}
