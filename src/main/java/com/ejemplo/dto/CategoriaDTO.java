package com.ejemplo.dto;

import java.time.LocalDateTime;
import java.util.List;

// Java 21: Records como DTOs inmutables
public record CategoriaDTO(
        Long id,
        String nombre,
        String descripcion,
        int totalProductos,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    // Record con lista de productos
    public record ConProductos(
            Long id,
            String nombre,
            String descripcion,
            List<ProductoDTO> productos
    ) {}
}
