package club.chillrainqcna.chillrainbbs.annotation;

import java.lang.annotation.*;

/**
 * AOP用注解
 */
@Target(ElementType.METHOD)
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface GobalAnnotation {
    /**
     * 是否需要登录
     * @return
     */
    boolean needLogin() default false;

    /**
     * 是否需要非空
     * @return
     */

    boolean needNotNull() default false;

    boolean needAdmin() default false;
}
