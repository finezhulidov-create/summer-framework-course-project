package dev.zhulidov.summer_framework_course_project.config.utils;

import java.lang.annotation.Annotation;

public class AnnotationUtils {
    public static boolean hasAnnotation(Class<?> clazz, Class<? extends Annotation> annotation){
        if (clazz.isAnnotationPresent(annotation)){
            return true;
        }
        for (Annotation a : clazz.getAnnotations()){
            if (a.annotationType().isAnnotationPresent(annotation)){
                return true;
            }
        }
        return false;
    }
}
