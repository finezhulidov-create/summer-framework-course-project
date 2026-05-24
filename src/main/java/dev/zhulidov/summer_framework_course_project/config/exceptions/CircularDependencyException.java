package dev.zhulidov.summer_framework_course_project.config.exceptions;

public class CircularDependencyException extends RuntimeException {
    public CircularDependencyException(String message) {
        super(message);
    }

    public CircularDependencyException(String s, Throwable throwable) {
        super(s,throwable);
    }
}
