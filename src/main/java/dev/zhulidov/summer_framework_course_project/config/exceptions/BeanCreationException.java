package dev.zhulidov.summer_framework_course_project.config.exceptions;

public class BeanCreationException extends SummerFrameworkException {
    public BeanCreationException(String message) {
        super(message);
    }

    public BeanCreationException(String message, Throwable cause){
        super(message, cause);
    }
}
