package org.example.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.example.bean.enumtype.TaskStatusEnum;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Eric.Lee
 * Date: 2024/4/8
 */
@Entity
@Table(name = "task_send_promote_message", schema = "crawler_ig")
@IdClass(org.example.entity.TaskSendPromoteMessagePK.class)
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "promote任務序列")
@Getter
@Setter
public class TaskSendPromoteMessage {
    @Id
    @ManyToOne
    @JoinColumn(name = "task_queue_id", referencedColumnName = "id")
    private TaskQueue taskQueue;

    @Id
    @Column(name = "account")
    private String account;

    @Column(name = "account_full_name")
    private String accountFullName;

    @Column(name = "text_zh_tw")
    private String textZhTw;

    @Column(name = "text_en")
    private String textEn;

    @Column(name = "text_ja")
    private String textJa;

    @Column(name = "text_ru")
    private String textRu;

    @Column(name = "post_url")
    private String postUrl;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TaskStatusEnum status;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "modify_time")
    private LocalDateTime modifyTime;

    /**
     * 標記任務為已完成。
     */
    public void completeTask() {
        this.status = TaskStatusEnum.COMPLETED;
        this.modifyTime = LocalDateTime.now();
    }

    /**
     * 標記任務為失敗。
     *
     */
    public void failTask() {
        this.status = TaskStatusEnum.FAILED;
        this.modifyTime = LocalDateTime.now(); // 設定任務結束時間為目前時間
    }
}
