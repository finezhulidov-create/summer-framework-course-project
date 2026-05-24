package dev.zhulidov.summer_framework_course_project.config.exceptions;

public class ContextCreationException extends SummerFrameworkException {
    public ContextCreationException(String message) {
        super(message);
    }

    public ContextCreationException(String message, Throwable cause){
        super(message, cause);
    }
}
