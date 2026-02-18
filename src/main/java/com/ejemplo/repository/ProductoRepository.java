package com.ejemplo.repository;

import com.ejemplo.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Query methods automaticos
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    List<Producto> findByPrecioBetween(BigDecimal min, BigDecimal max);

    List<Producto> findByStockLessThan(Integer cantidad);

    List<Producto> findByActivoTrue();

    List<Producto> findTop10ByActivoTrueOrderByCreatedAtDesc();

    // Paginacion
    Page<Producto> findByCategoriaId(Long categoriaId, Pageable pageable);

    // Query personalizado con JPQL
    @Query("SELECT p FROM Producto p WHERE p.categoria.nombre = :categoria " +
           "AND p.stock > 0 ORDER BY p.precio ASC")
    List<Producto> findDisponiblesPorCategoria(@Param("categoria") String categoria);

    // Join con fetch para evitar N+1
    @Query("SELECT p FROM Producto p JOIN FETCH p.categoria WHERE p.id = :id")
    Optional<Producto> findByIdWithCategoria(@Param("id") Long id);

    // Busqueda con filtros multiples
    @Query("SELECT p FROM Producto p WHERE " +
           "(:nombre IS NULL OR p.nombre LIKE %:nombre%) AND " +
           "(:minPrecio IS NULL OR p.precio >= :minPrecio) AND " +
           "(:maxPrecio IS NULL OR p.precio <= :maxPrecio)")
    List<Producto> buscarConFiltros(
            @Param("nombre") String nombre,
            @Param("minPrecio") BigDecimal minPrecio,
            @Param("maxPrecio") BigDecimal maxPrecio
    );

    // EntityGraph para evitar N+1
    @EntityGraph(attributePaths = {"categoria"})
    List<Producto> findAll();

    // Update masivo
    @Modifying
    @Query("UPDATE Producto p SET p.precio = p.precio * :factor " +
           "WHERE p.categoria.id = :categoriaId")
    int actualizarPreciosPorCategoria(
            @Param("categoriaId") Long categoriaId,
            @Param("factor") BigDecimal factor
    );

    // Proyeccion con interfaz
    interface ProductoInfo {
        String getNombre();
        BigDecimal getPrecio();
        String getCategoriaNombre();
    }

    @Query("SELECT p.nombre as nombre, p.precio as precio, " +
           "p.categoria.nombre as categoriaNombre " +
           "FROM Producto p")
    List<ProductoInfo> findAllProductoInfo();
}
