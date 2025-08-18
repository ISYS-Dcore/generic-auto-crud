# Generic Auto CRUD Library

[![CI][build-badge]][build-url]
[![Tests][test-badge]][test-url]
[![E2E Tests][test-e2e-badge]][test-e2e-url]
[![Codecov Coverage][coverage-badge]][coverage-url]
[![Issues][issues-badge]][issues-url]

_🚀 Build Java Spring Boot RESTful APIs in minutes — no boilerplate code required._

---

## 📦 Installation

Add the dependency to your `pom.xml`:

```xml
<!-- Domingos Masta - Generic Auto CRUD for Java Spring Boot -->
<dependency>
    <groupId>io.github.isys-dcore</groupId>
    <artifactId>generic-auto-crud</artifactId>
    <version>0.2.74</version>
</dependency>
```

Run:

```sh
mvn dependency:resolve
```

Or simply **Build** from your IDE.

---

## ✨ Features

- ✅ Auto-generate CRUD APIs for entities with SQL and MongoDB  
- 🌳 Extensible and overridable methods (service & controller levels)  
- ⚡ Compatible with **Java 11+** and **Spring Boot 3.x**  
- 🔧 Out-of-the-box classes:
  - **EntityRepository** – database access  
  - **EntityServiceImplementation** – business logic layer  
  - **EntityRestController** – REST API endpoints  

---

## 🗄 SQL Example (PostgreSQL / MySQL)

### Step 1 – Create an Entity

```java
@Entity
@Table
@Data
@EqualsAndHashCode(callSuper = true)
public class Person extends GenericEntity<UUID> {

    @NonNull
    @Column(nullable = false)
    private String name;

    @NonNull
    @Column(nullable = false)
    private Date dob;

    @NonNull
    @Column(nullable = false, unique = true)
    private String docId;

    // Additional attributes...
}
```

> `GenericEntity<UUID>` provides a built-in **ID field**, plus audit fields (`createdAt`, `updatedAt`, `deletedAt`).  
> ID type can be `UUID`, `Long`, `Integer`, or even `String`.

---

### Step 2 – Repository

```java
@Repository
public interface PersonRepository extends GenericRepository<Person, UUID> {
}
```

Provides CRUD methods: `save`, `update`, `delete`, `findAll`, `findById`.

---

### Step 3 – Service Layer

```java
@Service
public class PersonServiceImpl extends GenericRestServiceAbstract<Person, PersonRepository, UUID> {
    @Override
    public Person save(Person person) {
        // Custom business logic before saving
        return repository.save(person);
    }
}
```

- Extends `GenericRestServiceAbstract`  
- You can override any method (optional)  
- Without overrides, the service acts as a transparent middleware  

---

### Step 4 – REST Controller

```java
@RestController
@RequestMapping(FULL_API_URL_BASE_NAME + "/person")
public class PersonRestController extends GenericRestControllerAbstract<Person, PersonServiceImpl, UUID> {
    public PersonRestController(PersonServiceImpl serviceImpl) {
        super(serviceImpl);
    }
}
```

> All CRUD endpoints are now automatically exposed via REST.  
> Use Swagger/OpenAPI to explore them easily.  

---

## 🍃 MongoDB Example

Steps are similar to SQL.  
The main differences are:  

- Use `@Document` instead of `@Entity`  
- Extend **Mongo** versions of repository and service classes  

### Step 1 – Entity

```java
@Document
@Data
@EqualsAndHashCode(callSuper = true)
public class Person extends GenericEntity<UUID> {

    @NonNull
    private String name;

    @NonNull
    private Date dob;

    @NonNull
    private String docId;

    // Additional attributes...
}
```

---

### Step 2 – Repository

```java
@Repository
public interface PersonRepository extends MongoGenericRepository<Person, UUID> {
}
```

---

### Step 3 – Service

```java
@Service
public class PersonServiceImpl extends MongoGenericRestServiceAbstract<Person, PersonRepository, UUID> {

    // Mandatory constructor
    public PersonServiceImpl() {
        super(Person.class);
    }

    @Override
    public Person save(Person person) {
        // Custom logic before saving
        return repository.save(person);
    }
}
```

---

### Step 4 – REST Controller

```java
@RestController
@RequestMapping(FULL_API_URL_BASE_NAME + "/person")
public class PersonRestController extends MongoGenericRestControllerAbstract<Person, PersonServiceImpl, UUID> {
    public PersonRestController(PersonServiceImpl serviceImpl) {
        super(serviceImpl);
    }
}
```

---

### Step 5 – MongoDB Config (for deep search)

```java
@Configuration
public class MongoConfig {
    @Bean
    public MongoPropertyResolver mongoPropertyResolver(MongoMappingContext context) {
        return new MongoPropertyResolver(context);
    }
}
```

---

## ⚡ Cache Support

This library includes an optional in-memory caching mechanism.  

### ⚠️ Warning
- **High Memory Usage** – avoid caching large datasets  
- **Stale Data** – cache must be refreshed or invalidated  
- **Server Load** – reloading large caches can impact CPU/GC  

✅ Best Practices:
- Only cache frequently accessed, lightweight entities  
- Monitor heap/GC in production  
- Use distributed cache invalidation if running multiple service instances  

---

### Example Usage

```java
GenericCache<String, Person, PrsonService> cache = new GenericCache<>(PrsonService);

// Add secondary indexes
cache.addSecondaryIndex("byName", e -> e.getName());
cache.addSecondaryIndex("byNameAndType", e -> Arrays.asList(e.getName(), e.getType()));

// Query in O(1)
List<MyEntity> list = cache.getByIndex("byNameAndType", Arrays.asList("John", "Admin"));
```

---

## 🔍 Advanced RSQL Search

This library supports **RSQL-based queries** without writing custom repository methods.  

Example request with `curl`:

```sh
curl -X GET "http://localhost:8080/api/person/search?page=0&size=10&query=name==*mingo*"
```

Equivalent SQL:

```sql
SELECT * FROM Person p WHERE p.name LIKE '%mingo%';
```

More on RSQL: [rsql-parser](https://github.com/jirutka/rsql-parser)

---

## 📜 License

Licensed under [MPL-2.0](https://choosealicense.com/licenses/mpl-2.0/)

---

## 🤝 Contributing

Contributions are welcome!  
Check out our [Pull Request template](.github/pull_request_template.md) and open an [Issue](https://github.com/ISYS-Dcore/generic-auto-crud/issues/new/choose).

---

[mvn-url]: https://github.com/ISYS-Dcore/generic-auto-crud/packages
[mvn-badge]: https://img.shields.io/npm/v/react-parallax-tilt.svg
[size-url]: https://bundlephobia.com/package/react-parallax-tilt
[size-badge]: https://badgen.net/bundlephobia/minzip/react-parallax-tilt
[downloads-badge]: https://img.shields.io/npm/dm/react-parallax-tilt.svg?color=blue
[lint-badge]: https://github.com/mkosir/react-parallax-tilt/actions/workflows/lint.yml/badge.svg
[lint-url]: https://github.com/mkosir/react-parallax-tilt/actions/workflows/lint.yml
[tsc-badge]: https://github.com/mkosir/react-parallax-tilt/actions/workflows/tsc.yml/badge.svg
[tsc-url]: https://github.com/mkosir/react-parallax-tilt/actions/workflows/tsc.yml
[build-badge]: https://github.com/mkosir/react-parallax-tilt/actions/workflows/build.yml/badge.svg
[build-url]: https://github.com/mkosir/react-parallax-tilt/actions/workflows/build.yml
[test-badge]: https://github.com/mkosir/react-parallax-tilt/actions/workflows/test.yml/badge.svg
[test-url]: https://react-parallax-tilt-test-unit-report.netlify.app/
[test-e2e-badge]: https://github.com/mkosir/react-parallax-tilt/actions/workflows/test-e2e.yml/badge.svg
[test-e2e-url]: https://react-parallax-tilt-test-e2e-report.netlify.app/
[deploy-storybook-badge]: https://github.com/mkosir/react-parallax-tilt/actions/workflows/deploy-storybook.yml/badge.svg
[deploy-storybook-url]: https://github.com/mkosir/react-parallax-tilt/actions/workflows/deploy-storybook.yml
[mvn-release-badge]: https://github.com/mkosir/react-parallax-tilt/actions/workflows/npm-release.yml/badge.svg
[mvn-release-url]: https://github.com/mkosir/react-parallax-tilt/actions/workflows/npm-release.yml
[coverage-badge]: https://codecov.io/gh/mkosir/react-parallax-tilt/branch/main/graph/badge.svg
[coverage-url]: https://app.codecov.io/github/mkosir/react-parallax-tilt/tree/main
[issues-badge]: https://img.shields.io/github/issues/mkosir/react-parallax-tilt
[issues-url]: https://github.com/mkosir/react-parallax-tilt/issues
[semantic-badge]: https://img.shields.io/badge/%20%20%F0%9F%93%A6%F0%9F%9A%80-semantic--release-e10079.svg
[semantic-url]: https://github.com/semantic-release/semantic-release
[typescript-badge]: https://badges.frapsoft.com/typescript/code/typescript.svg?v=101
[typescript-url]: https://github.com/microsoft/TypeScript
