package org.example.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.example.bean.enumtype.TaskStatusEnum;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ig_user_id", referencedColumnName = "id")
    private IgUser igUser;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_config_id")
    private TaskConfig taskConfig;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TaskStatusEnum status;

    @Column(name = "submit_time")
    private LocalDateTime submitTime;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "modify_time")
    private LocalDateTime modifyTime;

    @Column(name = "result")
    private String result;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "next_id_for_search")
    private String nextIdForSearch;

    @Version
    @Column(name = "version")
    private Long version;

    /**
     * 標記任務為代辦中。
     */
    public void pendingTask() {
        this.status = TaskStatusEnum.PENDING;
        this.modifyTime = LocalDateTime.now(); // 設定任務修改時間為目前時間
    }

    /**
     * 標記任務為已完成。
     */
    public void completeTask() {
        this.status = TaskStatusEnum.COMPLETED;
        this.endTime = LocalDateTime.now(); // 設定任務結束時間為目前時間
    }

    /**
     * 標記任務為暫停。
     * 暫停狀態可以用來表示任務需要在將來某個時間點被重新啟動繼續執行。
     */
    public void pauseTask() {
        this.status = TaskStatusEnum.PAUSED;
        this.modifyTime = LocalDateTime.now(); // 設定任務修改時間為目前時間
    }

    /**
     * 標記任務為失敗。
     *
     * @param errorMessage 失敗原因描述
     */
    public void failTask(String errorMessage) {
        this.status = TaskStatusEnum.FAILED;
        this.endTime = LocalDateTime.now(); // 設定任務結束時間為目前時間
        this.errorMessage = errorMessage; // 設定錯誤訊息
    }
}
