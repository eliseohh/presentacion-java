package com.ejemplo.dto;

import java.math.BigDecimal;

// Java 21: Records como DTOs inmutables
public record ProductoDTO(
        Long id,
        String nombre,
        BigDecimal precio,
        Integer stock,
        Boolean activo,
        String categoriaNombre
) {
    // Record para creacion/actualizacion
    public record CrearProducto(
            String nombre,
            BigDecimal precio,
            Integer stock,
            Long categoriaId
    ) {}
}
