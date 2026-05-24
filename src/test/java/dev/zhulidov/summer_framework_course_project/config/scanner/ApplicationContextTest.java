package dev.zhulidov.summer_framework_course_project.config.scanner;

import dev.zhulidov.summer_framework_course_project.config.Application;
import dev.zhulidov.summer_framework_course_project.config.ApplicationContext;
import dev.zhulidov.summer_framework_course_project.config.exceptions.BeanCreationException;
import dev.zhulidov.summer_framework_course_project.config.exceptions.BeanNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationContextTest {
    private ApplicationContext context;

    @BeforeEach
    void setUp(){
        context = Application.getContext(TestConfig.class);
    }

    @BeforeAll
    static void setupLogging(){
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.ALL);
        for (Handler hnd: rootLogger.getHandlers()){
            hnd.setLevel(Level.ALL);
        }
    }

    @Test
    void contextShouldNotBeNull(){

        assertNotNull(context);
    }

    @Test
    void shouldReturnCorrectObj() throws Exception {
        Object o = context.getObject(TestService.class);
        assertInstanceOf(TestService.class, o);
    }

    @Test
    void shouldReturnSameObject() throws Exception{
        Object o1 = context.getObject(TestService.class);
        Object o2 = context.getObject(TestService.class);

        assertSame(o1,o2);
    }

    @Test
    void shouldReturnNotSameObj() throws Exception{
        Object o1 = context.getObject(TestPrototypeClass.class);
        Object o2 = context.getObject(TestPrototypeClass.class);

        assertNotSame(o1,o2);
    }

    @Test
    void shouldReturnTrueAfterPostConstruct() throws Exception{
        var o = context.getObject(PostConstructTestService.class);
        assertTrue(o.init);
    }

    @Test
    void shouldReturnNonNullAfterInjectField() throws Exception{
        var o = context.getObject(PostConstructTestService.class);

        assertNotNull(o);
    }

    @Test
    void shouldThrowRuntimeException() throws Exception{
        assertThrows(BeanCreationException.class, () -> context.getObject(CycleA.class));
    }

    @Test
    void shouldReturnPrimaryClass() throws Exception{
        var o = context.getObject(TestInterface.class);
        assertInstanceOf(TstImplB.class, o);
    }

    @Test
    void shouldReturnTstImplAAndNotReturnTstImplB()throws Exception{
        var o = context.getObject(TestQualifier.class);
        assertInstanceOf(TstImplA.class,o.dependency);

    }

    @Test
    void debugScan()throws Exception {
        String packagePath = "dev/zhulidov/summer_framework_course_project/config/scanner";
        var resources = Thread.currentThread().getContextClassLoader().getResources(packagePath);
        while (resources.hasMoreElements()) {
            System.out.println("Найден ресурс: " + resources.nextElement());
        }
    }
}
