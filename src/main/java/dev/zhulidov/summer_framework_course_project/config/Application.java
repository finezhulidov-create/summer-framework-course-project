package dev.zhulidov.summer_framework_course_project.config;

import dev.zhulidov.summer_framework_course_project.config.annotations.AppComponent;
import dev.zhulidov.summer_framework_course_project.config.annotations.ComponentScan;
import dev.zhulidov.summer_framework_course_project.config.exceptions.ContextCreationException;
import dev.zhulidov.summer_framework_course_project.config.exceptions.SummerFrameworkException;
import dev.zhulidov.summer_framework_course_project.config.utils.MessageSource;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class Application {

public static void run(Class<?> mainComponent)  {

    Application.getContext(mainComponent);

}
    public static ApplicationContext getContext(Class<?> mainComponent)  {
        try {
           var context = getApplicationContext(mainComponent);
           Runtime.getRuntime().addShutdownHook(new Thread(context::close));
           return context;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 IOException | ClassNotFoundException e) {
            throw new SummerFrameworkException(MessageSource.getMessage("context.creation.error", e), e);
        }

    }

    public static ApplicationContext getApplicationContext(Class<?> mainComponent) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
        ComponentScan componentScan = mainComponent.getAnnotation(ComponentScan.class);
        String packageToscan;
        if (componentScan != null && !componentScan.basePackage().isEmpty()){
            packageToscan = componentScan.basePackage();
        } else {
            packageToscan = mainComponent.getPackage().getName();
        }
        JavaConfig config = new JavaConfig(packageToscan);

        ApplicationContext context = new ApplicationContext(config);

        ObjectRegistrator registrator = new BeanObjectRegistrator();
        registrator.register(context);
        Set<Class<?>> components = context.getConfig().getScanner()
                .getTypesAnnotatedWith(AppComponent.class);
        for (Class<?> aClass: components){
            context.getObject(aClass);
        }
        return context;
    }


}
