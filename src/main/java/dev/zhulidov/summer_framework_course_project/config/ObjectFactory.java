package dev.zhulidov.summer_framework_course_project.config;


import dev.zhulidov.summer_framework_course_project.config.annotations.Inject;
import dev.zhulidov.summer_framework_course_project.config.annotations.PostConstruct;
import dev.zhulidov.summer_framework_course_project.config.exceptions.BeanConfigurationException;
import dev.zhulidov.summer_framework_course_project.config.exceptions.BeanCreationException;
import dev.zhulidov.summer_framework_course_project.config.exceptions.CircularDependencyException;
import dev.zhulidov.summer_framework_course_project.config.scanner.PackageScanner;
import dev.zhulidov.summer_framework_course_project.config.utils.MessageSource;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ObjectFactory {
    private static final Logger log = Logger.getLogger(ObjectFactory.class.getName());
    private ApplicationContext context;
    private List<ObjectConfigurator> configurators = new ArrayList<>();
    private ThreadLocal<Set<Class<?>>> inCreation = ThreadLocal.withInitial(HashSet::new);

    public ObjectFactory(ApplicationContext context) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
        this.context = context;
        this.configurators = scanFrameworkConfigurators();
        for (Class<? extends ObjectConfigurator> aClass : context.getConfig()
                .getScanner().getSubTypesOf(ObjectConfigurator.class)){
            configurators.add(aClass.getDeclaredConstructor().newInstance());
        }

    }

    private List<ObjectConfigurator> scanFrameworkConfigurators() throws IOException, ClassNotFoundException {
        PackageScanner frameworkScan = new PackageScanner(InjectAnnotationObjectConfigurator.class.getPackageName());
        return frameworkScan.getSubTypesOf(ObjectConfigurator.class).stream()
                .map(aClass -> {
                    try{
                        return aClass.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        try {
                            log.severe("Произошла ошибка при выполнении конструктора " + aClass.getDeclaredConstructor().getName() + " " + e.getMessage());
                        } catch (NoSuchMethodException ex) {
                            throw new RuntimeException(ex);
                        }
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toCollection(ArrayList::new));
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
                log.info("Объект класса: " + t.getClass().getName() +" успешно сконфигурирован");
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException |
                     NoSuchMethodException | IOException e) {
                log.severe("Ошибка конфигурирования объекта " + t.getClass().getName() + ": " + e.getMessage());
                throw new BeanConfigurationException("Проверьте правильность конфигурирования объектов и проверьте логи");
            } catch (ClassNotFoundException e) {
                log.severe("Класс: " + t.getClass().getName()+ "Не найден");
                throw new BeanConfigurationException("Класс не найден или не существует, проверьте логи"+e);
            }
        });
    }
    @SuppressWarnings("unchecked")
    public  <T> T create(Class<? extends T> implClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
        List<Object> dependencies = new ArrayList<>();

        if (inCreation.get().contains(implClass)){
            log.severe("Найдена циклическая зависимoсть " + implClass.getName());
            throw new CircularDependencyException(MessageSource.getMessage("circular.dependency",implClass.getName()));
        }
        log.info("Начинается создание объекта из класса: " + implClass.getName());
        inCreation.get().add(implClass);
        for (Constructor<?> constructor : implClass.getDeclaredConstructors()){
           if (constructor.isAnnotationPresent(Inject.class)){
              Class<?>[] types =  constructor.getParameterTypes();
               for (Class<?> Aclass : types){
                 Object  o = context.getObject(Aclass);
                 dependencies.add(o);
                 log.info("Добавлена зависимость в конструктор " + constructor.getName());
               }
               inCreation.get().remove(implClass);
               log.info("Создан объект из класса: " + implClass.getName());
              return (T) constructor.newInstance(dependencies.toArray());

           }
       }

        inCreation.get().remove(implClass);
        log.info("Создан объект из класса: " + implClass.getName());
        return implClass.getDeclaredConstructor().newInstance();

    }
}
