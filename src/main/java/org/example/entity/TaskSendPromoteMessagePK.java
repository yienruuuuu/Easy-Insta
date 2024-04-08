package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author Eric.Lee
 * Date: 2024/4/8
 */
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TaskSendPromoteMessagePK implements Serializable {
    private BigInteger taskQueue;
    private String account;
}
