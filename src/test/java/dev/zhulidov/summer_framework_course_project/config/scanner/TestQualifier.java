package dev.zhulidov.summer_framework_course_project.config.scanner;

import dev.zhulidov.summer_framework_course_project.config.annotations.AppComponent;
import dev.zhulidov.summer_framework_course_project.config.annotations.Inject;
import dev.zhulidov.summer_framework_course_project.config.annotations.Qualifier;

@AppComponent
public class TestQualifier {
    @Inject
    @Qualifier("tstImplA")
    public TestInterface dependency;
}
