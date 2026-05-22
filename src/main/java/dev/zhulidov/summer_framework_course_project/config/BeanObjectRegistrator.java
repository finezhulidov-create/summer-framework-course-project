package dev.zhulidov.summer_framework_course_project.config;

import dev.zhulidov.summer_framework_course_project.config.annotations.Bean;
import dev.zhulidov.summer_framework_course_project.config.annotations.Configuration;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;


public class BeanObjectRegistrator implements ObjectRegistrator{
    @Override
    public void register(ApplicationContext context) throws IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
     Set<Class<?>> aClass = context.getConfig().getScanner().getTypesAnnotatedWith(Configuration.class);
     for (Class<?> config : aClass){
       Object o =  context.getObject(config);
         Method[] methods = Arrays.stream(config.getMethods())
                 .filter(method -> method.isAnnotationPresent(Bean.class))
                 .toArray(Method[]::new);

         Arrays.stream(methods).forEach(method -> {
             try {
                 context.registerBean(method.getReturnType(), method.invoke(o));
             } catch (IllegalAccessException | InvocationTargetException e) {
                 throw new RuntimeException(e);
             }
         });
     }

    }
}
