# Usage Examples

## Define a Domain Model
```java
@Entity
public class Product extends GenericEntity<UUID> {
    private String name;
    private BigDecimal price;
}
```

## Add Custom Logic
Extend `GenericService`:
```java
@Service
public class ProductService extends GenericServiceImpl<Product, UUID> {
    // Custom business logic
}
```

Override the controller:
```java
@RestController
@RequestMapping("/api/products")
public class ProductController extends GenericController<Product, UUID> { }
```
