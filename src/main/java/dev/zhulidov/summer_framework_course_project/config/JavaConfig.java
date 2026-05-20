package dev.zhulidov.summer_framework_course_project.config;



import dev.zhulidov.summer_framework_course_project.config.annotations.ComponentName;
import dev.zhulidov.summer_framework_course_project.config.annotations.Primary;
import dev.zhulidov.summer_framework_course_project.config.scanner.PackageScanner;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class JavaConfig {

    private PackageScanner scanner;

    public JavaConfig(String packageToscan) {
        this.scanner = new PackageScanner(packageToscan);
    }

    public <T> Class<? extends T> getImplClass(Class<T> ifc, String qualifier) throws IOException, ClassNotFoundException {
        Set<Class<? extends T>> classes = scanner.getSubTypesOf(ifc);
        if (classes.isEmpty()){
            throw new IllegalStateException("No implementation for found " + ifc.getName());
        }
        Class<? extends T> clazz = getAClass(ifc, qualifier, classes);
        if (clazz != null) return clazz;
        for (Class<? extends T> clasz : classes){
            if (clasz.isAnnotationPresent(Primary.class)){
                return clasz;
            }
        }
        if (classes.size() == 1){
            return classes.iterator().next();
        }

        throw new IllegalStateException(
                "Multiple implementations found for " + ifc.getName() +
                        ", but none marked with @Primary. Available: " +
                        classes.stream().map(Class::getSimpleName).collect(Collectors.joining(", ")));


    }

    private <T> Class<? extends T> getAClass(Class<T> ifc, String qualifier, Set<Class<? extends T>> classes) {
        if (qualifier != null && !qualifier.isEmpty()){
            for (Class<? extends T> clazz: classes){
                if (clazz.getSimpleName().equalsIgnoreCase(qualifier)){
                    return clazz;
                }
                if (clazz.isAnnotationPresent(ComponentName.class)){
                    ComponentName componentName = clazz.getAnnotation(ComponentName.class);
                    if (componentName.value().equalsIgnoreCase(qualifier)){
                        return clazz;
                    }
                }
            }
            throw new IllegalStateException("No implementations found for " + ifc.getName());
        }
        return null;
    }

    public PackageScanner getScanner() {
        return scanner;
    }
}
