package dev.zhulidov.summer_framework_course_project.config;

import dev.zhulidov.summer_framework_course_project.config.annotations.AppComponent;
import dev.zhulidov.summer_framework_course_project.config.annotations.Value;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@AppComponent
public class ValueAnnotaationObjectConfigurator implements ObjectConfigurator {
    private final Map<String,String> properties = new ConcurrentHashMap<>();

    public ValueAnnotaationObjectConfigurator() {
        Properties props = new Properties();
        try(InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties")){
            if (input != null){
                props.load(input);
                props.forEach((k,v)-> properties.put(k.toString(), v.toString()));
            }
        } catch (IOException e){
            throw new RuntimeException("Не удалось прочитать application.properties", e);
        }
    }

    @Override
    public void configure(Object t, ApplicationContext context) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException, IOException, ClassNotFoundException {
        for (Field field: t.getClass().getDeclaredFields()){
            if (field.isAnnotationPresent(Value.class)){
                String placeHolder = field.getAnnotation(Value.class).value();
                String key = placeHolder
                        .replace("${", "")
                        .replace("}","");
                String value = properties.get(key);
                if (value != null) {
                    field.setAccessible(true);
                    field.set(t, value);
                } else {
                    throw new RuntimeException("Проверьте наличие ключа "+key+" в application.properties"+" в классе: "+ t.getClass().getName());
                }
            }
        }
    }
}
