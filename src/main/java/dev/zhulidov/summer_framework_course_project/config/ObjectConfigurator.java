package dev.zhulidov.summer_framework_course_project.config;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface ObjectConfigurator {

    void configure(Object t, ApplicationContext context) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException, IOException, ClassNotFoundException;
}
