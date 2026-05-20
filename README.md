# Summer Framework

Summer Framework - это легковесный DI-контейнер, вдохновленный Spring Framework, реализованный с нуля на чистом Java без внешних зависимостей.

## Особенности

- Внедрение зависимостей (DI)
- Автоматическое сканирование компонентов
- Поддержка аннотаций
- Гибкая конфигурация
- Минималистичный и понятный код

## Аннотации

### @Component
Помечает класс как компонент, управляемый контейнером.

```java
@Component
public class UserService {
    // ...
}
```

### @Service, @Repository, @Controller
Специализированные компоненты для разных слоев приложения.

```java
@Service
public class UserService {
    // ...
}

@Repository
public class UserRepository {
    // ...
}

@Controller
public class UserController {
    // ...
}
```

### @Inject
Внедряет зависимости в поля.

```java
@Service
public class OrderService {
    
    @Inject
    private UserService userService;
    
    @Inject
    private PaymentService paymentService;
}
```

### @Primary
Указывает приоритетную реализацию при наличии нескольких вариантов.

```java
@Component
@Primary
public class DatabaseUserService implements UserService {
    // ...
}

@Component
public class FileUserService implements UserService {
    // ...
}
```

### @Qualifier
Позволяет явно указать, какую реализацию использовать.

```java
@Service
public class OrderService {
    
    @Inject
    @Qualifier("databaseUserService")
    private UserService userService;
}
```

### @PostConstruct
Метод с этой аннотацией выполняется после создания бина.

```java
@Service
public class DatabaseService {
    
    @PostConstruct
    public void init() {
        // Инициализация базы данных
    }
}
```

## Быстрый старт

### 1. Добавьте зависимость

Установите JAR-файл в локальный репозиторий Maven:

```bash
mvn install:install-file -Dfile="path/to/summer-framework-course-project-1.0-SNAPSHOT.jar" -DgroupId=dev.zhulidov -DartifactId=summer-framework-course-project -Dversion=1.0-SNAPSHOT -Dpackaging=jar
```

Добавьте зависимость в `pom.xml`:

```xml
<dependency>
    <groupId>dev.zhulidov</groupId>
    <artifactId>summer-framework-course-project</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. Создайте основной класс приложения

```java
@ComponentScan(basePackage = "com.example")
public class Application {
    
    public static void main(String[] args) {
        ApplicationContext context = Summer.run(Application.class, args);
    }
}
```

### 3. Создайте компоненты

```java
@Service
public class UserService {
    
    public void createUser(String name) {
        System.out.println("Создан пользователь: " + name);
    }
}
```

```java
@Service
public class NotificationService {
    
    @Inject
    private EmailService emailService;
    
    public void notifyUser(String user, String message) {
        emailService.sendEmail(user + "@example.com", message);
    }
}
```

## Сборка

Соберите проект с помощью Maven:

```bash
mvn clean package
```

JAR-файл будет создан в папке `target/`.

## Архитектура

```
summer-framework-course-project/
├── src/
│   └── main/
│       └── java/
│           └── dev/
│               └── zhulidov/
│                   └── summer_framework_course_project/
│                       ├── config/
│                       │   ├── annotations/
│                       │   ├── Application.java
│                       │   ├── ApplicationContext.java
│                       │   ├── JavaConfig.java
│                       │   ├── ObjectFactory.java
│                       │   └── scanner/
│                       │       └── PackageScanner.java
│                       └── Summer.java
└── pom.xml
```

## Лицензия

MIT License