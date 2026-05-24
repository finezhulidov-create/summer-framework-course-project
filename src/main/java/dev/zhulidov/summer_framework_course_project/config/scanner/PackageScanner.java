package dev.zhulidov.summer_framework_course_project.config.scanner;

import dev.zhulidov.summer_framework_course_project.config.utils.AnnotationUtils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class PackageScanner {
    private final String packageToScan;
    private final ClassLoader classLoader;

    public PackageScanner(String packageToScan) {
        this.packageToScan = packageToScan;
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }
@SuppressWarnings("unchecked")
    public <T> Set<Class<? extends T>> getSubTypesOf(Class<T> ifc) throws IOException, ClassNotFoundException {
        Set<Class<?>> allClasses = scan();
    Set<Class<? extends T>> result = new HashSet<>();
        for (Class<?> clazz : allClasses){
            if (ifc.isAssignableFrom(clazz) && !clazz.equals(ifc)){
                result.add((Class<? extends T>) clazz);
            }
        }
        return result;
    }
    @SuppressWarnings("unchecked")
    public Set<Class<?>> getTypesAnnotatedWith(Class<?> annotationClass) throws IOException, ClassNotFoundException {
        Set<Class<?>> allClasses = scan();
        Set<Class<?>> result = new HashSet<>();

        for (Class<?> clazz : allClasses) {
            if (AnnotationUtils.hasAnnotation(clazz, (Class<? extends Annotation>) annotationClass)) {
                result.add(clazz);
            }
        }

        return result;
    }
    Set<Class<?>> scan() throws IOException, ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<>();
        String packagePath = packageToScan.replace('.','/');

        Enumeration<URL> resources = classLoader.getResources(packagePath);

        while (resources.hasMoreElements()){
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("file")){
                classes.addAll(scanFileSystem(resource, packagePath));
            } else if (resource.getProtocol().equals("jar")) {
                classes.addAll(scanJarFile(resource, packagePath));

            }
        }
        return classes;
    }

    private Set<Class<?>> scanJarFile(URL resource, String packagePath) throws IOException, ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<>();
        // Извлекаем путь к JAR файлу
        String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));

        try (JarInputStream jar = new JarInputStream(new URL("file:" + jarPath).openStream())) {
            JarEntry entry;

            while ((entry = jar.getNextJarEntry()) != null) {
                String entryName = entry.getName();

                // Проверяем, что класс находится в нужном пакете и имеет расширение .class
                if (entryName.startsWith(packagePath) && entryName.endsWith(".class") && !entryName.equals(packagePath + "/")) {
                    String className = entryName.substring(0, entryName.length() - 6).replace('/', '.');
                    classes.add(Class.forName(className));
                }

                jar.closeEntry();
            }
        }

        return classes;
    }

    private String convertToClassName(String packagePath, String name) {
        return packagePath.replace('/','.') + "." + name.substring(0, name.length() - 6 );
    }

    private Set<Class<?>> scanFileSystem(URL resource, String packagePath) throws MalformedURLException, ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<>();
        File directory = new File(resource.getFile());
       if (directory.exists() && directory.isDirectory()){
            File[] files = directory.listFiles();
            if (files != null){
                for (File file : files){
                    if (file.isDirectory()){
                        String subPackage = packagePath + "/" + file.getName();
                        URL subPackageUrl = file.toURI().toURL();
                        classes.addAll(scanFileSystem(subPackageUrl, subPackage));
                    } else if (file.getName().endsWith(".class")) {
                        String className = convertToClassName(packagePath, file.getName());
                        classes.add(Class.forName(className));

                    }
                }
            }
        }
        return classes;
    }
}
