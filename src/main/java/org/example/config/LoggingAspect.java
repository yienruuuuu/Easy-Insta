package org.example.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * 這是一個切面，用於記錄日誌
 * @author Eric.Lee
 * Date: 2024/3/11
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * 在任務執行前記錄日誌
     * @param joinPoint 切入點
     */
    @Before("execution(* org.example.strategy.*.*.executeTask(..))")
    public void logBeforeTaskExecution(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length >= 2) {
            log.info("開始執行任務:{} ,帳號:{}", args[0], args[1]);
        }
    }
}
