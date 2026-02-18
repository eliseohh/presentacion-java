package com.ejemplo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Java 21: Records como DTOs inmutables
public record ProductoDTO(
        Long id,
        String nombre,
        BigDecimal precio,
        Integer stock,
        Boolean activo,
        String categoriaNombre,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    // Record para creacion/actualizacion
    public record CrearProducto(
            String nombre,
            BigDecimal precio,
            Integer stock,
            Long categoriaId
    ) {}
}
