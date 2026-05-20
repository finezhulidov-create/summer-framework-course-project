package dev.zhulidov.summer_framework_course_project.config;


import dev.zhulidov.summer_framework_course_project.config.annotations.Inject;
import dev.zhulidov.summer_framework_course_project.config.annotations.Qualifier;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class InjectAnnotationObjectConfigurator implements ObjectConfigurator {

    @Override
    public void configure(Object t, ApplicationContext context) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, IOException, ClassNotFoundException {
        for (Field field : t.getClass().getDeclaredFields()){
            if (field.isAnnotationPresent(Inject.class)){
                field.setAccessible(true);
                Qualifier qualifier = field.getAnnotation(Qualifier.class);
                String qualifierValue = (qualifier != null) ? qualifier.value() : null;
                Object object = context.getObject(field.getType(), qualifierValue );
                field.set(t, object);
            }
        }

    }
}
