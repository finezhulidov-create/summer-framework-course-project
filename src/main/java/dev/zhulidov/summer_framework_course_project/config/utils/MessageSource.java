package dev.zhulidov.summer_framework_course_project.config.utils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class MessageSource {

    private static final ResourceBundle bundle;

    static {
        Locale locale = Locale.getDefault();
        bundle = ResourceBundle.getBundle("messages", locale);
    }

    public static String getMessage(String key, Object... args){
        String message = bundle.getString(key);
        return MessageFormat.format(message,args);
    }
}
