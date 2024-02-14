package org.example.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.example.bean.enumtype.TaskStatusEnum;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * @author Eric.Lee
 * Date:2024/2/11
 */
@Entity
@Table(name = "task_queue", schema = "crawler_ig")
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder@Schema(description = "任務序列")
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
    private String taskType;
    @Column(name = "status")
    private TaskStatusEnum status;
    @Column(name = "submit_time")
    private Timestamp submitTime;
    @Column(name = "start_time")
    private Timestamp startTime;
    @Column(name = "end_time")
    private Timestamp endTime;
    @Column(name = "result")
    private String result;
    @Column(name = "error_message")
    private String errorMessage;

}
