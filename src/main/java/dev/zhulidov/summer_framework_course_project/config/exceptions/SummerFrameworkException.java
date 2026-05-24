package dev.zhulidov.summer_framework_course_project.config.exceptions;

public class SummerFrameworkException extends RuntimeException {
    public SummerFrameworkException(String message) {
        super(message);
    }

    public SummerFrameworkException(String message, Throwable cause){
        super(message, cause);
    }
}
