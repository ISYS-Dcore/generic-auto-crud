# Extending the Library

You can override the following layers:

## Service
Extend `GenericServiceImpl<T, ID>` and add your methods.

## Controller
Extend `GenericController<T, ID>` to customize endpoints.

## Repository
Use Spring Data JPA interfaces:
```java
@Repository
public interface ProductRepository extends GenericRepository<Product, UUID> { }
```

## Filtering or Hooks
Use AOP or event listeners to add pre/post logic for CRUD actions.
