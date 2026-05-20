package dev.zhulidov.summer_framework_course_project.config;

import dev.zhulidov.summer_framework_course_project.config.annotations.ComponentScan;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Application {
    public static void run(Class<?> mainComponent, String... args )  {
        try {
            ApplicationContext context = getApplicationContext(mainComponent);
            Object component = context.getObject(mainComponent);
            if (component instanceof Runnable) {
                ((Runnable) component).run();
            } else if (component instanceof AutoCloseable) {
                try {
                    mainComponent.getMethod("run").invoke(component);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Ошибка при вызове метода");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка создания контекста");
        }
    }

    private static ApplicationContext getApplicationContext(Class<?> mainComponent) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
        ComponentScan componentScan = mainComponent.getAnnotation(ComponentScan.class);
        String packageToscan;
        if (componentScan != null && !componentScan.basePackage().isEmpty()){
            packageToscan = componentScan.basePackage();
        } else {
            packageToscan = mainComponent.getPackage().getName().split("\\.")[0];
        }
        JavaConfig config = new JavaConfig(packageToscan);
        ApplicationContext context = new ApplicationContext(config);
        ObjectFactory factory = new ObjectFactory(context);
        context.setFactory(factory);
        return context;
    }


}
