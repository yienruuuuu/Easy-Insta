package org.example.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.example.bean.enumtype.TaskStatusEnum;
import org.example.bean.enumtype.TaskTypeEnum;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * @author Eric.Lee
 * Date:2024/2/11
 */
@Entity
@Table(name = "task_queue", schema = "crawler_ig")
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "任務序列")
@Getter
@Setter
public class TaskQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "serial")
    private BigInteger id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "task_type")
    @Enumerated(EnumType.STRING)
    private TaskTypeEnum taskType;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TaskStatusEnum status;

    @Column(name = "submit_time")
    private LocalDateTime submitTime;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "result")
    private String result;

    @Column(name = "error_message")
    private String errorMessage;

}
