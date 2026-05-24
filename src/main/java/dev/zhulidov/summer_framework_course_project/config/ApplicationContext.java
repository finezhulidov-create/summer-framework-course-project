package dev.zhulidov.summer_framework_course_project.config;


import dev.zhulidov.summer_framework_course_project.config.annotations.AppComponent;
import dev.zhulidov.summer_framework_course_project.config.annotations.PreDestroy;
import dev.zhulidov.summer_framework_course_project.config.annotations.Scope;
import dev.zhulidov.summer_framework_course_project.config.utils.AnnotationUtils;

import java.io.Closeable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;


public class ApplicationContext implements Closeable {
    private  Map<Class<?>, Object> cache = new ConcurrentHashMap<>();
    private JavaConfig config;
    private final ObjectFactory factory;
    private static final Logger log = Logger.getLogger(ApplicationContext.class.getName());


    public ApplicationContext(JavaConfig config) throws IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        this.config = config;
        this.factory = new ObjectFactory(this);
    }

    public <T> T getObject(Class<T> type) throws IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        return getObject(type,null);
    }
    @SuppressWarnings({"unchecked"})
    public <T> T getObject(Class<T> type, String qualifier) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, IOException, ClassNotFoundException {
        if (cache.containsKey(type) && qualifier == null) {
            log.info("Объект получен из кэша: " + type.getName());
            return (T) cache.get(type);
        }
        Class<? extends T> implClass = type;
        if (type.isInterface()){
            implClass = config.getImplClass(type, qualifier);
        }
        T t = factory.create(implClass);
        if ( implClass.isAnnotationPresent(Scope.class) &&
                implClass.getAnnotation(Scope.class).value().equalsIgnoreCase("prototype")){

                factory.configure(t);
                factory.invokeInit(implClass, t);
                log.info("Из класса: "+implClass.getName()+" создан prototype объект: "+ t.getClass().getName());
                return t;

        } else  {
            cache.put(type, t);
            log.info("Объект " + t.getClass().getName() +
                    " добавлен в кэш как singleton" +
                    (qualifier != null ? " с qualifier: " + qualifier : ""));
            factory.configure(cache.get(type));
            factory.invokeInit(implClass, t);
            if (qualifier == null) {
                cache.put(type, t);


            }
            return t;
        }
    }



    public void registerBean(Class<?> type, Object instance){
        cache.put(type, instance);
    }

    public JavaConfig getConfig() {
        return config;
    }



    @Override
    public void close() {
        for (Class<?> bean: cache.keySet()){
            Arrays.stream(bean.getMethods())
                    .forEach(method -> {
                        if (method.isAnnotationPresent(PreDestroy.class)){
                            method.setAccessible(true);
                            try {
                                method.invoke(cache.get(bean));
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            } catch (InvocationTargetException e) {
                                throw new RuntimeException("Не получилось вызвать метод");
                            }
                        }
                    });
        }
    }
    // Отладочные

    public Map<Class<?>, Object> getCache() {
        return cache;
    }
}
