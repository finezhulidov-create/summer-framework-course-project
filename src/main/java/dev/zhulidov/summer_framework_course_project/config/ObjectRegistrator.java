package dev.zhulidov.summer_framework_course_project.config;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface ObjectRegistrator {
    void register(ApplicationContext context) throws IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException;
}
