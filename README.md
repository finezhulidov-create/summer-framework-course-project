# ☀️ Summer Framework

Лёгкий IoC-контейнер на Java — учебный аналог Spring Framework.  
Реализует ключевые механизмы инверсии управления и внедрения зависимостей.

---

## 📋 Содержание

- [Возможности](#возможности)
- [Быстрый старт](#быстрый-старт)
- [Аннотации](#аннотации)
- [Внедрение зависимостей](#внедрение-зависимостей)
- [Области видимости](#области-видимости)
- [Жизненный цикл бина](#жизненный-цикл-бина)
- [Конфигурация через @Configuration](#конфигурация-через-configuration)
- [Работа с application.properties](#работа-с-applicationproperties)
- [Разрешение неоднозначностей](#разрешение-неоднозначностей)
- [Исключения](#исключения)
- [Архитектура](#архитектура)
- [Подключение через Maven](#подключение-через-maven)

---

## Возможности

- Автоматическое сканирование пакетов (файловая система и JAR)
- Field injection и constructor injection через `@Inject`
- Поддержка мета-аннотаций (`@Service`, `@Controller`, `@Repository`)
- Управление областью видимости: `singleton` и `prototype`
- Полный жизненный цикл бина: `@PostConstruct` и `@PreDestroy`
- Разрешение реализаций через `@Primary`, `@Qualifier`, `@ComponentName`
- Регистрация сторонних объектов через `@Configuration` и `@Bean`
- Внедрение значений из `application.properties` через `@Value`
- Обнаружение циклических зависимостей при конструкторной инъекции
- Локализованные сообщения об ошибках (русский и английский)
- Расширяемость через интерфейс `ObjectConfigurator`

---

## Быстрый старт

### 1. Создай главный класс

```java
@ComponentScan
public class Main {
    public static void main(String[] args) {
        Application.run(Main.class);
    }
}
```

### 2. Создай компонент с логикой запуска

```java
@Service
public class MyApp {

    @Inject
    private MyService myService;

    @PostConstruct
    public void init() {
        myService.doSomething();
    }
}
```

### 3. Запусти — фреймворк сам найдёт все компоненты, внедрит зависимости и вызовет `@PostConstruct`

Если нужен доступ к контексту:

```java
ApplicationContext context = Application.getContext(Main.class);
MyService service = context.getObject(MyService.class);
```

---

## Аннотации

### Компонентные аннотации

| Аннотация | Применяется к | Описание |
|---|---|---|
| `@AppComponent` | Класс | Базовый маркер управляемого компонента |
| `@Service` | Класс | Для сервисного слоя (мета-аннотация над `@AppComponent`) |
| `@Controller` | Класс | Для контроллеров (мета-аннотация над `@AppComponent`) |
| `@Repository` | Класс | Для репозиториев (мета-аннотация над `@AppComponent`) |
| `@Configuration` | Класс | Для конфигурационных классов с `@Bean`-методами |

### Аннотации зависимостей

| Аннотация | Применяется к | Описание |
|---|---|---|
| `@Inject` | Поле, конструктор | Внедрение зависимости |
| `@Qualifier` | Поле, параметр | Уточнение реализации по имени |
| `@Primary` | Класс | Приоритетная реализация при неоднозначности |
| `@ComponentName` | Класс | Псевдоним компонента для `@Qualifier` |

### Остальные аннотации

| Аннотация | Применяется к | Описание |
|---|---|---|
| `@ComponentScan` | Класс | Указание корневого пакета для сканирования |
| `@Scope` | Класс | Область видимости бина (`singleton` / `prototype`) |
| `@Bean` | Метод | Регистрация объекта из `@Configuration`-класса |
| `@PostConstruct` | Метод | Вызывается после создания и конфигурирования бина |
| `@PreDestroy` | Метод | Вызывается при завершении приложения |
| `@Value` | Поле | Внедрение значения из `application.properties` |

---

## Внедрение зависимостей

### Field injection

```java
@Service
public class OrderService {

    @Inject
    private PaymentService paymentService;
}
```

### Constructor injection

```java
@Service
public class OrderService {

    private final PaymentService paymentService;

    @Inject
    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

---

## Области видимости

По умолчанию все бины — **singleton**: создаются один раз и переиспользуются.

```java
// Singleton — один экземпляр на контейнер (по умолчанию)
@Service
public class UserService { }

// Prototype — новый экземпляр при каждом getObject()
@Service
@Scope("prototype")
public class ShoppingCart { }
```

---

## Жизненный цикл бина

```
Создание → configure() → @PostConstruct → использование → @PreDestroy
```

```java
@Service
public class CacheService {

    @PostConstruct
    public void init() {
        // вызывается автоматически после внедрения зависимостей
        System.out.println("Кэш инициализирован");
    }

    @PreDestroy
    public void destroy() {
        // вызывается автоматически при завершении приложения
        System.out.println("Кэш очищен");
    }
}
```

---

## Конфигурация через @Configuration

Для регистрации объектов из сторонних библиотек (без возможности пометить `@AppComponent`):

```java
@Configuration
public class InfrastructureConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
```

---

## Работа с application.properties

Создай файл `src/main/resources/application.properties`:

```properties
db.url=jdbc:postgresql://localhost:5432/mydb
db.username=admin
db.password=secret
```

Внедри значения через `@Value`:

```java
@Service
public class DatabaseService {

    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.username}")
    private String username;
}
```

---

## Разрешение неоднозначностей

Если для интерфейса есть несколько реализаций — фреймворк выбирает по приоритету:

**1. По квалификатору:**

```java
@Inject
@Qualifier("cashPaymentService")
private PaymentService paymentService;
```

**2. По `@Primary`:**

```java
@Service
@Primary
public class OnlinePaymentService implements PaymentService { }
```

**3. По `@ComponentName`:**

```java
@Service
@ComponentName("cash")
public class CashPaymentService implements PaymentService { }

// использование:
@Inject
@Qualifier("cash")
private PaymentService paymentService;
```

---

## Исключения

Все исключения фреймворка наследуют `SummerFrameworkException`:

```
SummerFrameworkException
    ├── ContextCreationException     — ошибка создания контекста
    ├── BeanCreationException        — ошибка создания объекта
    ├── BeanConfigurationException   — ошибка конфигурирования
    ├── BeanNotFoundException        — реализация не найдена
    └── CircularDependencyException  — циклическая зависимость
```

Сообщения об ошибках локализованы — на русской системе выводятся на русском,
на остальных — на английском.

---

## Расширяемость

Добавь собственную логику конфигурирования объектов через `ObjectConfigurator`:

```java
@AppComponent
public class MyCustomConfigurator implements ObjectConfigurator {

    @Override
    public void configure(Object t, ApplicationContext context) throws ... {
        // своя логика — например чтение значений из переменных окружения
    }
}
```

Фреймворк подхватит его автоматически — без изменения кода фреймворка.

---

## Архитектура

```
Application
    └── ApplicationContext      — IoC-контейнер, кэш бинов (ConcurrentHashMap)
            ├── JavaConfig      — разрешение реализаций интерфейсов
            ├── ObjectFactory   — создание, конфигурирование, инициализация бинов
            │       └── ObjectConfigurator (список)
            │               ├── InjectAnnotationObjectConfigurator
            │               └── ValueAnnotationObjectConfigurator
            └── BeanObjectRegistrator — регистрация @Bean-объектов
```

### Поток создания бина

```
getObject(Type)
    → проверка кэша
    → JavaConfig.getImplClass() — поиск реализации
    → ObjectFactory.create()    — создание экземпляра
    → cache.put()               — ранняя регистрация (разрывает циклы field injection)
    → ObjectFactory.configure() — заполнение @Inject полей
    → ObjectFactory.invokeInit()— вызов @PostConstruct
    → return объект
```

---

## Подключение через Maven

Добавь репозиторий и зависимость в `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/finezhulidov-create/summer-framework-course-project</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>dev.zhulidov</groupId>
        <artifactId>summer-framework-course-project</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

Добавь в `~/.m2/settings.xml` токен с правом `read:packages`:

```xml
<settings>
    <servers>
        <server>
            <id>github</id>
            <username>ВАШ_GITHUB_USERNAME</username>
            <password>ВАШ_GITHUB_TOKEN</password>
        </server>
    </servers>
</settings>
```

---

## Требования

- Java 17+
- Maven 3.6+

---

*Summer Framework — учебный проект. Жулидов Александр Дмитриевич, УрФУ ИРИТ-РТФ, 2026*
