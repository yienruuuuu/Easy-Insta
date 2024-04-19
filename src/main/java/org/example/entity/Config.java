package org.example.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;


/**
 * @author Eric.Lee
 * Date: 2024/2/29
 */
@Entity
@Schema(description = "設定")
@Getter
@Setter
@Table(name = "config", schema = "crawler_ig")
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Config {
    @Id
    @Column(name = "param")
    private String param;

    @Column(name = "value")
    private String value;
}
