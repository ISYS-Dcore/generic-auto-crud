# Architecture

`generic-auto-crud` is based on:

- **Generic Programming**: Uses Java generics to create reusable CRUD components.
- **Spring Data JPA**: Integrates directly with Spring repositories.
- **Spring MVC**: Exposes generic REST controllers via annotations.
- **Modular Design**: Easy to extend with custom behavior.

## Key Interfaces

- `GenericEntity<ID>`
- `GenericRepository<T, ID>`
- `GenericService<T, ID>`
- `GenericController<T, ID>`

## Auto Configuration
The library uses Spring Boot's auto-configuration to register CRUD endpoints dynamically.
