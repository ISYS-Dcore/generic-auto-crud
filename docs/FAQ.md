# FAQ

## Does this support soft deletes?
Yes, implement `SoftDeletable` or override delete behavior in service layer.

## Can I customize the endpoint paths?
Yes, by creating your own controller extending `GenericController`.

## How can I disable certain endpoints?
Use Spring Security or override specific methods in your controller.
