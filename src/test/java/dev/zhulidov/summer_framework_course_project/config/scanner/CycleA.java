package dev.zhulidov.summer_framework_course_project.config.scanner;

import dev.zhulidov.summer_framework_course_project.config.annotations.AppComponent;
import dev.zhulidov.summer_framework_course_project.config.annotations.Inject;


public class CycleA {

    public CycleA(CycleB b){}
}
