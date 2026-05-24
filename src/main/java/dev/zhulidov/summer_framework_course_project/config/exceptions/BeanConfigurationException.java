package dev.zhulidov.summer_framework_course_project.config.exceptions;

public class BeanConfigurationException extends SummerFrameworkException{
    public BeanConfigurationException(String message) {
        super(message);
    }

    public BeanConfigurationException(String message, Throwable cause){
        super(message, cause);
    }
}
