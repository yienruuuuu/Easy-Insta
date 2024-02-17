package org.example.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.example.bean.enumtype.LoginAccountStatusEnum;

import javax.persistence.*;

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
    private LoginAccountStatusEnum status;
    @Column(name = "status_remark")
    private String statusRemark;
    @Column(name = "email")
    private String email;
    @Column(name = "email_password")
    private String emailPassword;
    @Column(name = "backup_email")
    private String backupEmail;
}
