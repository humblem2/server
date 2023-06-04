package kr.co._29cm.homework.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * 실행시간 측정/로깅 하는 AOP
 */
@Aspect
@Component
public class ExecutionTimeLoggerAspect {
    private static final Logger LOGGER = Logger.getLogger(ExecutionTimeLoggerAspect.class.getName());

    @Around("@annotation(kr.co._29cm.homework.annotation.LogExecutionTime)")
    public Object executionTimeLogger(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long executionTimeMillis = System.currentTimeMillis() - start;
        double executionTimeSeconds = executionTimeMillis / 1000.0;

        LOGGER.info(joinPoint.getSignature() + " executed in " + executionTimeSeconds + " 초");

        return proceed;
    }
}
