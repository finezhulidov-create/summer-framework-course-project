package dev.zhulidov.summer_framework_course_project.config.trst;

import dev.zhulidov.summer_framework_course_project.config.Application;
import dev.zhulidov.summer_framework_course_project.config.ApplicationContext;
import dev.zhulidov.summer_framework_course_project.config.annotations.ComponentScan;
import dev.zhulidov.summer_framework_course_project.config.annotations.Configuration;
import dev.zhulidov.summer_framework_course_project.config.annotations.Inject;

import java.util.Map;
import java.util.Set;

@ComponentScan
public class Main {
    @Inject
    private Service service;

    public static void main(String[] args) {
        Application.run(Main.class);



    }


}
