package dev.zhulidov.summer_framework_course_project.config.scanner;

import dev.zhulidov.summer_framework_course_project.config.annotations.AppComponent;
import dev.zhulidov.summer_framework_course_project.config.annotations.Inject;
import dev.zhulidov.summer_framework_course_project.config.annotations.PostConstruct;

@AppComponent
public class PostConstructTestService {
    public boolean init = false;
    @Inject
    public TestService service;
    @PostConstruct
    public void init(){
        init = true;
    }
}
