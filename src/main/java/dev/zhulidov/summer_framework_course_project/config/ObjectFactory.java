package dev.zhulidov.summer_framework_course_project.config;


import dev.zhulidov.summer_framework_course_project.config.annotations.Inject;
import dev.zhulidov.summer_framework_course_project.config.annotations.PostConstruct;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ObjectFactory {
    private ApplicationContext context;
    private List<ObjectConfigurator> configurators = new ArrayList<>();
    private Set<Class<?>> inCreation = new HashSet<>();

    public ObjectFactory(ApplicationContext context) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
        this.context = context;
        for (Class<? extends ObjectConfigurator> aClass : context.getConfig().getScanner().getSubTypesOf(ObjectConfigurator.class)){
            configurators.add(aClass.getDeclaredConstructor().newInstance());
        }
    }

    public <T> T createObject(Class<? extends T> implClass) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, IOException, ClassNotFoundException {
        T t = create(implClass);
        configure(t);
        invokeInit(implClass, t);
        return t;
    }

    public  <T> void invokeInit(Class<? extends T> implClass, T t) throws InvocationTargetException, IllegalAccessException {
        for (Method method : implClass.getMethods()){
            if (method.isAnnotationPresent(PostConstruct.class)){
                method.setAccessible(true);
                method.invoke(t);
            }
        }
    }

    public  <T> void configure(T t) {
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
    @SuppressWarnings("unchecked")
    public  <T> T create(Class<? extends T> implClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
        List<Object> dependencies = new ArrayList<>();

        if (inCreation.contains(implClass)){
            throw new RuntimeException("Circular dependency detected" + implClass.getName());
        }
        inCreation.add(implClass);
        for (Constructor<?> constructor : implClass.getDeclaredConstructors()){
           if (constructor.isAnnotationPresent(Inject.class)){
              Class<?>[] types =  constructor.getParameterTypes();
               for (Class<?> Aclass : types){
                 Object  o = context.getObject(Aclass);
                 dependencies.add(o);

               }
               inCreation.remove(implClass);
              return (T) constructor.newInstance(dependencies.toArray());

           }
       }

        inCreation.remove(implClass);
        return implClass.getDeclaredConstructor().newInstance();

    }
}
