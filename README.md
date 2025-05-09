# Generic Auto CRUD Lib

[//]: # ([![mvn version][mvn-badge]][mvn-url])

[//]: # ([![mvn downloads][downloads-badge]][mvn-url])

[//]: # ([![mvn bundle size][size-badge]][size-url])

[//]: # ([![Open issues][issues-badge]][issues-url])

[//]: # ([![TypeScript][typescript-badge]][typescript-url])

[![CI][tsc-badge]][tsc-url]
[![CI][build-badge]][build-url]
[![CI][test-badge]][test-url]
[![CI][test-e2e-badge]][test-e2e-url]
[![Codecov Coverage][coverage-badge]][coverage-url]

_👀 Create java spring boot RESTFull APIs quickly_

## Install

Edit your pom.xml and add this dependency
```
<!--Domingos Masta - Java Spring boot Genetic Auto CRUD Library-->
<dependency>
    <groupId>io.github.isys-dcore</groupId>
    <artifactId>generic-auto-crud</artifactId>
    <version>0.2.73</version>
</dependency>
```

## Features

- Implement CRUD of an entity automatically with SQL databases and MongoDB 📦
- Scalable and overridable methods 🌳 
- Works with Java v11 and Spring Boot 3.X.X or new versions
- Automatic auto implemented classes:
  - **EntityRepository** Class responsible to interact with database
  - **EntityServiceImplementation** Class responsible to implement business logic or data transformations
  - **EntityRestController** Class responsible to interact with rest clients and manage requests
  
## SQL Example
This example will show how to create a simple CRUD API for a **Person** entity, using a 
SQL database like **PostgreSQL** or **MySQL**.

### Step 1 : 
After create and configure your spring boot 3.x.x project, import our dependecy lib:
```
<!--Domingos Masta - Java Spring boot Genetic Auto CRUD Library-->
<dependency>
    <groupId>io.github.isys-dcore</groupId>
    <artifactId>generic-auto-crud</artifactId>
    <version>0.2.74</version>
</dependency>
```
### Step 2 :
Execute this command:
```
mvn dependency:resolve
```
or if you are using IDE click **Build**

### Step 3 :

Create your persistent entity like bellow:
```
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
    
    //And other attrubutes you like do add
    ............
}
```
In this example note **GenericEntity\<UUID>** this class belong to
our lib, and is responsible to provide some generic attributes and JPA configurations
like **ID** field, and other maintainable and audit fields like **createdAt**, **updatedAt** and **deletedAt**.
The modifier **\<UUID>** is used to inform generic class that the datatype for **ID** field will be UUID if you like to use other
you can change to **\<Long>** or **\<Integer>** for numerics auto generated IDs or iven a **\<String>** type.

### Step 4 :

Create your entity repository:

```
@Repository
public interface PersonRepository extends GenericRepository<Person, UUID> {
}
```
In this example note **GenericRepository<Person, UUID>** this class belong to 
our lib and implement CRUD operations to interact with database like **save** -> Create, **update** -> Update,**delete** -> Delete, **findAll** and **findById** -> View or Read.
All entities that will be represented or persistent need to follow this pattern on your project. 

### Step 5 :

On this step we create our business logic level, to do this we need to create a 
Service for our entity.

```
@Service
public class PersonServiceImpl extends GenericRestServiceAbstract<Person, PersonRepository, UUID> {
    @Override
    public Person save(Person person){
        // Do something with Person information before store in database
        return repository.save(person);
    }
}

```
Note **GenericRestServiceAbstract<Person, PersonRepository, UUID>** we extend this library class that is responsible for intermediate 
interactions between Repository and Controller or modify the information before store or transformations before provide to client request.
For example the method **Save(Person person)** illustrate the override of lib default method to do some modification before save a person, 
this behavior is not mandatory because if you do not have any modifications you can leave the service empty and all 
methods will work as default, and service will act like a transparent middleware between controller and repository, however the class must be created e we want to benefit of
methods provided by the Controller level.

### Step 6 :

This is the step where we will expose our operations creating a rest controller.
To do this, you must follow step 5, and guarantee that have created the Service, 
and then you just need to create a class like this:

```
@RestController
@RequestMapping(FULL_API_URL_BASE_NAME + "/person")
public class PersonRestController extends GenericRestControllerAbstract<Person, PersonServiceImpl, UUID> {
    public PersonRestController(PersonServiceImpl serviceImpl) {
        super(serviceImpl);
    }
}
```
One more time note this **GenericRestControllerAbstract<Person, PersonServiceImpl, UUID>** 
We need to extend this class because this abstract class implement all methods and endpoints that expose CRUD operations 
via web service.
Like we see in service step you cans add any method you like to all this classes and will work alongside the 
default methods, and you can override all methods you want to provide same different behaviour, 
you can check implementing [Swagger](https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api)
and open your documentation endpoint, you wil find all rest endpoints exposed.

## MongoDB Example
This example will show how to create a simple CRUD API for a **Person** entity, using a
MongoDB database.

### Step 1 and 2 :

This step is the same as SQL example from [Step 1](https://github.com/ISYS-Dcore/generic-auto-crud?tab=readme-ov-file#step-1-),
to [Step 2](https://github.com/ISYS-Dcore/generic-auto-crud?tab=readme-ov-file#step-2-)

Only from step 3 we will have some key differences, so we will jump directly to step 3.

### Step 3 :

Create your persistent entity like bellow:
```
@Document
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
    @Column(nullable = false)
    private String docId;
    
    //And other attrubutes you like do add
    ............
}
```
In this example note **GenericEntity\<UUID>** this class belong to
our lib, and is responsible to provide some generic attributes and Mongo data configurations
like **ID** field, and other maintainable and audit fields like **createdAt**, **updatedAt** and **deletedAt**.
The modifier **\<UUID>** is used to inform generic class that the datatype for **ID** field will be UUID if you like to use other
you can change to **\<Long>** or **\<Integer>** for numerics auto generated IDs or iven a **\<String>** type.

The only difference from SQL example is the annotation **@Document** this annotation is used to inform that this 
entity will be stored in a MongoDB database, and the table name will be the same as class name.

### Step 4 :

Create your entity repository:

```
@Repository
public interface PersonRepository extends MongoGenericRepository<Person, UUID> {
}
```
In this example note **MongoGenericRepository<Person, UUID>** this class belong to
our lib and implement CRUD operations to interact with database like **save** -> Create, **update** -> Update,**delete** -> Delete, **findAll** and **findById** -> View or Read.
All entities that will be represented or persistent need to follow this pattern on your project.

The only difference from SQL example is the interface name, this interface is used to interact with MongoDB database.

### Step 5 :

On this step we create our business logic level, to do this we need to create a
Service for our entity.

```
@Service
public class PersonServiceImpl extends MongoGenericRestServiceAbstract<Person, PersonRepository, UUID> {
    
    // Mandatory constructor
    public PersonServiceImpl() {
        super(Person.class);
    }
    
    @Override
    public Person save(Person person){
        // Do something with Person information before store in database
        return repository.save(person);
    }
}

```
Note **MongoGenericRestServiceAbstract<Person, PersonRepository, UUID>** we extend this library class that is responsible for intermediate
interactions between Repository and Controller or modify the information before store or transformations before provide to client request.
For example the method **Save(Person person)** illustrate the override of lib default method to do some modification before save a person,
this behavior is not mandatory because if you do not have any modifications you can leave the service empty and all
methods will work as default, and service will act like a transparent middleware between controller and repository, 
however the class must be created if we want to benefit of
methods provided by the Controller level.

The only difference from SQL example is the constructor, this constructor is mandatory to inform the class type that
**MongoGenericRestServiceAbstract** will be used to create the entity,

### Step 6 :

This is the step where we will expose our operations creating a rest controller.
To do this, you must follow step 5, and guarantee that have created the Service,
and then you just need to create a class like this:

```
@RestController
@RequestMapping(FULL_API_URL_BASE_NAME + "/person")
public class PersonRestController extends MongoGenericRestControllerAbstract<Person, PersonServiceImpl, UUID> {
    public PersonRestController(PersonServiceImpl serviceImpl) {
        super(serviceImpl);
    }
}
```
One more time note this **GenericRestControllerAbstract<Person, PersonServiceImpl, UUID>**
We need to extend this class because this abstract class implement all methods and endpoints that expose CRUD operations
via web service.
Like we see in service step you cans add any method you like to all this classes and will work alongside the
default methods, and you can override all methods you want to provide same different behaviour,
you can check implementing [Swagger](https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api)
and open your documentation endpoint, you wil find all rest endpoints exposed.

The only difference from SQL example is the class name **GenericRestControllerAbstract**, this class is used to interact 
with MongoDB database.

### Step 7 :

This step we must create a configurations and expose a bean
to help us with deep search inside nested objects with mongo db documents, 
to do this we need to create a class like this:

```
@Configuration
public class MongoConfig {
    @Bean
    public MongoPropertyResolver mongoPropertyResolver(MongoMappingContext context) {
        return new MongoPropertyResolver(context);
    }
}
```

This class is responsible to create a bean that will be used to resolve the properties of the nested objects,
and this will be used to create the query to search for the nested objects.

## Extra Step :

This lib provide advanced search query by RSQL, this mean that you dont need to implement custom queries
on repositories because its already provided, for example if you wand to quey all persons that contains some 
string in name, you just need to call this endpoint:

```
http://{{hostname, or ip}}/{{application url}}/person/search?page=0&size=10&sort=0&query=name==*mingo*
```
This will automatically create a query like that:

```
  SELECT * FROM Person as p WHERE p.name like %mingo%;
```
And will return all mach records. 
If you what to know more about RSQL click [here](https://github.com/jirutka/rsql-parser)

## Props

All of the props are optional.  
Below is the complete list of possible props and their options:

## Licence

[mpl-2.0](https://choosealicense.com/licenses/mpl-2.0/)

## Contributing

All contributions are welcome!  
Please take a moment to review guidelines [PR](.github/pull_request_template.md) | [Issues](https://github.com/mkosir/react-parallax-tilt/issues/new/choose)

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
