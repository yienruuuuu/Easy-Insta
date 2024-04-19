package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.example.bean.enumtype.TaskStatusEnum;

import jakarta.persistence.*;

/**
 * @author Eric.Lee
 * Date: 2024/3/8
 */
@Entity
@Table(name = "task_queue_media", schema = "crawler_ig")
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "media任務序列")
@Getter
@Setter
public class TaskQueueMedia {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "task_queue_id")
    @JsonIgnore
    private TaskQueue taskQueue;

    @OneToOne
    @JoinColumn(name = "media_id", unique = true)
    @JsonIgnore
    private Media media;

    @Column(name = "next_media_id")
    private String nextMediaId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TaskStatusEnum status;
}
