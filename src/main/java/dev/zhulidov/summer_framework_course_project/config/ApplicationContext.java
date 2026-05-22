package dev.zhulidov.summer_framework_course_project.config;


import dev.zhulidov.summer_framework_course_project.config.annotations.AppComponent;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;


public class ApplicationContext {
    private  Map<Class<?>, Object> cache = new HashMap<>();
    private JavaConfig config;
    private ObjectFactory factory;


    public ApplicationContext(JavaConfig config) throws IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        this.config = config;
        new BeanObjectRegistrator().register(this);
    }

    public <T> T getObject(Class<T> type) throws IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        return getObject(type,null);
    }

    public <T> T getObject(Class<T> type, String qualifier) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, IOException, ClassNotFoundException {
        if (cache.containsKey(type) && qualifier == null) {
            return (T) cache.get(type);
        }
        Class<? extends T> implClass = type;
        if (type.isInterface() || hasAnnotation(type, AppComponent.class)){
            implClass = config.getImplClass(type, qualifier);
        }
        T t = factory.create(implClass);
        cache.put(type,t);
        factory.configure(cache.get(type));
        factory.invokeInit(implClass,t);
        if (hasAnnotation(implClass, AppComponent.class) && qualifier == null){
            cache.put(type, t);
        }
        return t;
    }

    private boolean hasAnnotation(Class<?> clazz, Class<? extends Annotation> annotation){
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

    public void registerBean(Class<?> type, Object instance){
        cache.put(type, instance);
    }

    public JavaConfig getConfig() {
        return config;
    }

    public void setFactory(ObjectFactory factory) {
        this.factory = factory;
    }
}
