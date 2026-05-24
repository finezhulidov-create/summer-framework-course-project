package dev.zhulidov.summer_framework_course_project.config.exceptions;

public class BeanNotFoundException extends SummerFrameworkException {
    public BeanNotFoundException(String message) {
        super(message);
    }

    public BeanNotFoundException(String message, Throwable cause){
        super(message, cause);
    }
}
