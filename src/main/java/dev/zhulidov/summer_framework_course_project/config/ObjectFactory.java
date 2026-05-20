package dev.zhulidov.summer_framework_course_project.config;


import dev.zhulidov.summer_framework_course_project.config.annotations.PostConstruct;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ObjectFactory {
    private ApplicationContext context;
    private List<ObjectConfigurator> configurators = new ArrayList<>();

    public ObjectFactory(ApplicationContext context) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
        this.context = context;
        for (Class<? extends ObjectConfigurator> aClass : context.getConfig().getScanner().getSubTypesOf(ObjectConfigurator.class)){
            configurators.add(aClass.getDeclaredConstructor().newInstance());
        }
    }

    public <T> T createObject(Class<? extends T> implClass) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        T t = create(implClass);
        cofigure(t);
        invokeInit(implClass, t);
        return t;
    }

    private <T> void invokeInit(Class<? extends T> implClass, T t) throws InvocationTargetException, IllegalAccessException {
        for (Method method : implClass.getMethods()){
            if (method.isAnnotationPresent(PostConstruct.class)){
                method.setAccessible(true);
                method.invoke(t);
            }
        }
    }

    private <T> void cofigure(T t) {
        configurators.forEach(objectConfigurator -> {
            try {
                objectConfigurator.configure(t, context);
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException |
                     NoSuchMethodException | IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("d");
            }
        });
    }

    private <T> T create(Class<? extends T> implClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return implClass.getDeclaredConstructor().newInstance();

    }
}
