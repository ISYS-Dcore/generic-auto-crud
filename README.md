# Generic Auto CRUD Library

[![CI][build-badge]][build-url]
[![Tests][test-badge]][test-url]
[![E2E Tests][test-e2e-badge]][test-e2e-url]
[![Codecov Coverage][coverage-badge]][coverage-url]
[![Issues][issues-badge]][issues-url]

_🚀 Build Java Spring Boot RESTful APIs in minutes — no boilerplate code required.

#### by: Domingos Masta

---

## 📦 Installation

Add the dependency to your `pom.xml`:

```xml
<!-- Domingos Masta - Generic Auto CRUD for Java Spring Boot -->
<dependency>
    <groupId>io.github.isys-dcore</groupId>
    <artifactId>generic-auto-crud</artifactId>
    <version>0.4.32</version>
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
- 🔊 Automatic audit logs with `@Auditable` annotation and `AuditService`
- 🔄 **DTO Support** – Built-in DTO mapping and transformation for cleaner APIs
- 📁 **File Storage Service** – Automatic file upload, download, and deletion utilities
- 🧪 **Generic Test Support** – Pre-built integration test base class with CRUD test templates
- 🛡️ **Comprehensive Exception Handling** – 15+ exception types handled automatically
- 🔗 **HATEOAS Support** – Hypermedia-driven APIs with Spring HATEOAS
- 🔐 **Authentication Testing** – JWT token handling and authentication test utilities
- 🔧 Out-of-the-box classes:
    - **EntityRepository** – database access
    - **EntityServiceImplementation** – business logic layer
    - **EntityRestController** – REST API endpoints
    - **EntityServiceImplementationDto** – DTO service layer
    - **EntityRestControllerDto** – DTO REST endpoints

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

> `GenericEntity<UUID>` provides a built-in **ID field**, plus audit fields (`createdAt`, `updatedAt`, `deletedAt`, `updatedBy`, `deletedBy`).  
> It also includes a **`resourceRef`** field (UUID) for unique resource identification.
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

## 🔄 DTO Support (SQL & MongoDB)

Use DTOs to decouple your API contract from internal domain models.

### Step 1 – Create a DTO

```java
@Data
@Builder
public class PersonDto extends GenericDto<UUID> {
    private String name;
    private Date dob;
    private String docId;
}
```

### Step 2 – Implement a DTO Mapper

```java
@Component
public class PersonDtoMapper implements GenericDTOMapper<PersonDto, Person> {
    
    @Override
    public PersonDto toDto(Person entity) {
        return PersonDto.builder()
            .id(entity.getId())
            .name(entity.getName())
            .dob(entity.getDob())
            .docId(entity.getDocId())
            .build();
    }

    @Override
    public Person toEntity(PersonDto dto) {
        return Person.builder()
            .id(dto.getId())
            .name(dto.getName())
            .dob(dto.getDob())
            .docId(dto.getDocId())
            .build();
    }

    @Override
    public List<PersonDto> toDtoList(List<Person> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<Person> toEntityList(List<PersonDto> dtos) {
        return dtos.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
```

### Step 3 – Service with DTO Support

```java
@Service
public class PersonServiceImplDto extends GenericRestServiceAbstractDto<Person, PersonDto, PersonRepository, PersonDtoMapper, UUID> {
    public PersonServiceImplDto(PersonRepository repository, PersonDtoMapper mapper) {
        super(repository, mapper);
    }
}
```

### Step 4 – REST Controller with DTO

```java
@RestController
@RequestMapping(FULL_API_URL_BASE_NAME + "/person")
public class PersonRestControllerDto extends GenericRestControllerAbstractDto<Person, PersonDto, PersonServiceImplDto, UUID> {
    public PersonRestControllerDto(PersonServiceImplDto serviceImpl) {
        super(serviceImpl);
    }
}
```

> Now your API accepts and returns `PersonDto` objects instead of raw entities!

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

## 🕵️ Auditable Interface

Track user and system operations automatically using the `@Auditable` annotation or manually via `AuditService`.

### Option 1: Annotation (AOP)

Annotate your methods to automatically log actions.

```java
@Service
public class UserService {

    @Auditable(action = "USER_CREATED", entity = "User")
    public void createUser(User user) {
        // Business logic...
    }
}
```

### Option 2: Manual Logging

Inject `AuditService` to log complex or conditional events.

```java
@Service
@RequiredArgsConstructor
public class FileProcessingService {
    private final AuditService auditService;

    public void processFile(String fileName) {
        // ... processing logic
        auditService.log("system", "FILE_PROCESSED", "File", fileName, "File processed successfully");
    }
}
```

### Automatic User Tracking
To automatically track the current user (actor), configure a filter:

```java
// Example Spring Security Filter
AuditContext.setCurrentActor(auth.getName());
```

---

## 📁 File Storage Service

Handle file uploads, downloads, and deletions with automatic directory management.

### Save a File

```java
@Service
@RequiredArgsConstructor
public class DocumentService {
    
    public String uploadDocument(MultipartFile file) {
        return FileStorageService.saveFile(file, "/uploads/documents");
    }
}
```

### Read a File

```java
public byte[] downloadDocument(String fileName) {
    return FileStorageService.readFile(fileName, "/uploads/documents");
}
```

### Delete a File

```java
public boolean removeDocument(String fileName) {
    return FileStorageService.deleteFile(fileName, "/uploads/documents");
}
```

### Check if File Exists

```java
public boolean documentExists(String fileName) {
    return FileStorageService.fileExists(fileName, "/uploads/documents");
}
```

---

## 📄 CSV Utilities

Easily parse CSV files into Java objects or Maps using `CsvUtils`.

### Parse to Entity List

```java
@Autowired
private CsvUtils<Person> csvUtils;

public void importPeople(byte[] csvBytes) {
    List<Person> people = csvUtils.parseCsv(csvBytes, Person.class);
    people.forEach(repository::save);
}
```

### Parse to Generic Map

```java
Set<String> expectedFields = Set.of("name", "email");
List<Map<String, Object>> records = csvUtils.parseCsvToGenericMap(csvBytes, expectedFields);
```

---

## ⚡ Cache Support

This library provides in-memory caching implementations for both SQL and MongoDB.

### Classes
- **GenericSQLCache** – For JPA/SQL entities
- **GenericMongoCache** – For MongoDB documents

### ⚠️ Warning
- **High Memory Usage** – avoid caching large datasets
- **Stale Data** – cache must be refreshed or invalidated manually
- **Server Load** – reloading large caches can impact CPU/GC

✅ **Best Practices**:
- Only cache frequently accessed, lightweight entities (e.g., configurations, types)
- Monitor heap/GC in production
- Use distributed cache (Redis/Hazelcast) for large-scale apps instead of this in-memory solution

### Example Usage

```java
@Service
public class PersonService extends GenericRestServiceAbstract<Person, PersonRepository, UUID> {
    
    private final GenericSQLCache<UUID, Person, PersonService> cache;

    public PersonService(PersonRepository repository) {
        super(repository);
        this.cache = new GenericSQLCache<>(this);
        
        // Initialize cache
        this.cache.init();
        
        // Add secondary indexes for O(1) lookup
        this.cache.addSecondaryIndex("byName", Person::getName);
    }

    public Person findByNameCached(String name) {
        List<Person> results = cache.getByIndex("byName", name);
        return results.isEmpty() ? null : results.get(0);
    }
}
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

## 🧪 Generic Test Support

Built-in integration testing framework for CRUD operations with authentication support.

### Create a Test Class

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PersonControllerTest extends GenericRestAbstractIntegrationTests<Person, PersonServiceImpl> {
    
    public PersonControllerTest() {
        super(new Person(), new TestProperties(
            resourceUrl = "/api/person",
            auth = false  // Set to true if your API requires authentication
        ));
    }

    @Override
    public void authenticationManagement() throws Exception {
        // Leave empty if no authentication is needed
        // Or implement authentication logic if required
    }
}
```

### Built-in Test Cases

The base class includes ready-to-use test methods:

- ✅ `shouldCreateEntity()` – Test POST /api/person
- ✅ `shouldReturnEntityByGivenId()` – Test GET /api/person/{id}
- ✅ `shouldReturnListOfEntities()` – Test GET /api/person
- ✅ `shouldReturnListOfEntitiesWithFilter()` – Test RSQL queries
- ✅ `shouldUpdateEntityByGivenId()` – Test PUT /api/person/{id}
- ✅ `shouldDeleteEntityByGivenId()` – Test DELETE /api/person/{id}
- ✅ `shouldReturnNotFoundEntityByGivenId()` – Test 404 responses
- ✅ `shouldReturnNoContentWhenFilter()` – Test empty results

### With JWT Authentication

```java
public class PersonControllerTest extends GenericRestAbstractIntegrationTests<Person, PersonServiceImpl> {
    
    public PersonControllerTest() {
        super(new Person(), new TestProperties(
            resourceUrl = "/api/person",
            auth = true,
            authHeaderName = "Authorization",
            tokenType = "Bearer",
            authToken = "your_jwt_token_here"
        ));
    }

    @Override
    public void authenticationManagement() throws Exception {
        // Implement if you need to generate tokens dynamically
    }
}
```

---

## 🛡️ Exception Handling

The library provides comprehensive exception handling with `CustomRestExceptionHandler`.

### Built-in Exception Handlers

| HTTP Status | Exception Type | Description |
|---|---|---|
| 400 | `MethodArgumentNotValidException` | Validation errors |
| 400 | `ConstraintViolationException` | Constraint violations |
| 400 | `MissingServletRequestParameterException` | Missing query parameters |
| 403 | `AccessDeniedException` | Access denied errors |
| 404 | `ResourceNotFoundException` | Resource not found |
| 404 | `NoHandlerFoundException` | Endpoint not found |
| 405 | `HttpRequestMethodNotSupported` | Invalid HTTP method |
| 409 | `DataAccessException` | Database conflicts |
| 415 | `HttpMediaTypeNotSupportedException` | Unsupported media type |
| 500 | `Exception` | Generic server errors |

### Example Error Response

```json
{
  "status": "BAD_REQUEST",
  "message": "Validation failed",
  "errors": [
    "name: must not be null",
    "docId: must be unique"
  ]
}
```

---

## 🔗 HATEOAS Integration

The library integrates with Spring HATEOAS to provide hypermedia links in API responses.

### Enable HATEOAS

Use `GenericModelAssemblerDto` for automatic link generation:

```java
@RestController
@RequestMapping(FULL_API_URL_BASE_NAME + "/person")
public class PersonRestControllerDto extends GenericRestControllerAbstractDto<Person, PersonDto, PersonServiceImplDto, UUID> {
    public PersonRestControllerDto(PersonServiceImplDto serviceImpl) {
        super(serviceImpl);
    }
}
```

### Example HATEOAS Response

```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "John Doe",
  "dob": "1990-01-01",
  "docId": "12345678900",
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/person/123e4567-e89b-12d3-a456-426614174000"
    },
    "person": {
      "href": "http://localhost:8080/api/person"
    }
  }
}
```

---

## 📊 Entity Features at a Glance

### Built-in Fields in `GenericEntity<ID>`

Every entity automatically includes:

```java
- id (ID)                    // Primary key - Serializable type
- resourceRef (String)       // UUID for unique resource identification
- createdAt (Instant)        // Automatically set on creation
- updatedAt (Instant)        // Automatically updated on modification
- deletedAt (Instant)        // Set when entity is soft-deleted
- deleted (Boolean)          // Soft delete flag
- updatedBy (String)         // User who last updated the entity
- deletedBy (String)         // User who deleted the entity
```

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
[semantic-badge]: https://img.shields.io/badge/%20%20%F0%9F%93%A6%F0%9A%80-semantic--release-e10079.svg
[semantic-url]: https://github.com/semantic-release/semantic-release
[typescript-badge]: https://badges.frapsoft.com/typescript/code/typescript.svg?v=101
[typescript-url]: https://github.com/microsoft/TypeScript

---

## 📋 Summary of Changes

| Change | Details |
|--------|---------|
| **Version** | Updated from `0.4.3` → `0.4.32` |
| **Features Section** | Added 8 new features with icons |
| **New Sections** | 🔄 DTO Support, 📁 File Storage, 🧪 Test Support, 🛡️ Exception Handling, 🔗 HATEOAS, 📊 Entity Fields |
| **GenericEntity Docs** | Added documentation for `resourceRef` and audit fields |
| **Use Cases Expanded** | Added practical examples for all new features |
| **Table Added** | Exception handling and built-in entity fields reference |

