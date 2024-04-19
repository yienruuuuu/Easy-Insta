package org.example.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.example.bean.enumtype.LoginAccountStatusEnum;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Eric.Lee
 */
@Entity
@Schema(description = "Restaurant's group")
@Getter
@Setter
@Table(name = "login_account", schema = "crawler_ig")
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Slf4j
@Builder
public class LoginAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "serial")
    private int id;

    @Column(name = "account")
    private String account;

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private LoginAccountStatusEnum status;

    @Column(name = "status_remark")
    private String statusRemark;

    @Column(name = "email")
    private String email;

    @Column(name = "email_password")
    private String emailPassword;

    @Column(name = "backup_email")
    private String backupEmail;

    @Column(name = "modify_time")
    private LocalDateTime modifyTime;

    /**
     * 標記帳號異常
     *
     * @param errorMessage 失敗原因描述
     */
    public void loginAccountDeviant(String errorMessage) {
        this.status = LoginAccountStatusEnum.DEVIANT;
        this.statusRemark = errorMessage;
        this.modifyTime = LocalDateTime.now();
    }

    /**
     * 標記帳號異常
     *
     */
    public void loginAccountExhausted() {
        this.status = LoginAccountStatusEnum.EXHAUSTED;
        this.modifyTime = LocalDateTime.now();
        log.info("帳號已標記為EXHAUSTED, 帳號:{}", this.account);
    }
}
