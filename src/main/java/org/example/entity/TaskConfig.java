package org.example.entity;

import lombok.*;
import org.example.bean.enumtype.TaskTypeEnum;

import javax.persistence.*;

/**
 * @author Eric.Lee
 * Date: 2024/2/26
 */
@Entity
@Table(name = "task_config", schema = "crawler_ig")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "serial")
    private Long id;

    @Column(name = "task_type")
    @Enumerated(EnumType.STRING)
    private TaskTypeEnum taskType;

    @Column(name = "need_login_ig")
    private boolean needLoginIg;
}
