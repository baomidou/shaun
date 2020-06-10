package shaun.test.cookie;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;

/**
 * @author miemie
 * @since 2020-06-10
 */
@Slf4j
@Order(100)
@Aspect
public class MyAspect {

    @Pointcut("@annotation(shaun.test.cookie.IAno)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("--------------------------------aop-------------------------------");
        return joinPoint.proceed();
    }
}
