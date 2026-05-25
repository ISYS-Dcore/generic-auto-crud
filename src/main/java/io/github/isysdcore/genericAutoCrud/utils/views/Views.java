package io.github.isysdcore.genericAutoCrud.utils.views;

/**
 * Helper class to provide view definitions for JSON serialization.
 * Uses Jackson's @JsonView to control which fields are included in the JSON output for different use cases.
 * For example: Summary view for listing entities, and Details view for detailed information.
 * This allows for flexible API responses without needing to create multiple DTO classes for different views.
 * for field in summary view, use @JsonView(Views.Summary.class)
 * for field in details view, use @JsonView(Views.Details.class)
 */
public class Views {
    public static class Summary {}
    public static class Details extends Summary {}
}