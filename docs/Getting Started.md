# Getting Started

## Prerequisites
- Java 17+
- Spring Boot 3.x

## Installation

Add the dependency to your `build.gradle`:
```groovy
implementation 'com.isys-dcore:generic-auto-crud:<latest-version>'
```

Or for Maven:
```xml
<dependency>
  <groupId>com.isys-dcore</groupId>
  <artifactId>generic-auto-crud</artifactId>
  <version>latest-version</version>
</dependency>
```

## Basic Setup

1. Define your entity class extending `GenericEntity<UUID>`:
```java
@Entity
public class Customer extends GenericEntity<UUID> {
    private String name;
    private String email;
}
```

2. Enable the library:
```java
@SpringBootApplication
@EnableGenericCrud
public class MyApp { ... }
```

That's it! Your CRUD endpoints are ready at `/api/customers`.
