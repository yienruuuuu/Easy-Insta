package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.example.bean.enumtype.TaskStatusEnum;

import javax.persistence.*;

/**
 * @author Eric.Lee
 * Date: 2024/3/13
 */
@Entity
@Table(name = "task_queue_followers_detail", schema = "crawler_ig")
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "follower detail任務序列")
@Getter
@Setter
public class TaskQueueFollowersDetail {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "task_queue_id")
    @JsonIgnore
    private TaskQueue taskQueue;

    @OneToOne
    @JoinColumn(name = "follower_id", unique = true)
    @JsonIgnore
    private Followers follower;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TaskStatusEnum status;

}

