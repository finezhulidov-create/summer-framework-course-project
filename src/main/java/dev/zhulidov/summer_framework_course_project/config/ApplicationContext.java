package dev.zhulidov.summer_framework_course_project.config;


import dev.zhulidov.summer_framework_course_project.config.annotations.AppComponent;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ApplicationContext {
    private  Map<Class, Object> cache = new HashMap<>();
    private Set<Class<?>> components = new HashSet<>();
    private JavaConfig config;
    private ObjectFactory factory;

    public ApplicationContext(JavaConfig config) {
        this.config = config;
    }

    public <T> T getObject(Class<T> type) throws IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        return getObject(type,null);
    }

    public <T> T getObject(Class<T> type, String qualifier) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, IOException, ClassNotFoundException {
        if (cache.containsKey(type) && qualifier == null) {
            return (T) cache.get(type);
        }
        Class<? extends T> implClass = type;
        if (type.isInterface() || type.isAnnotationPresent(AppComponent.class)){
            implClass = config.getImplClass(type, qualifier);
        }
        T t = factory.createObject(implClass);

        if (implClass.isAnnotationPresent(AppComponent.class) && qualifier == null){
            cache.put(type, t);
        }
        return t;
    }




    public JavaConfig getConfig() {
        return config;
    }

    public void setFactory(ObjectFactory factory) {
        this.factory = factory;
    }
}
