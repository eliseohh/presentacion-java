package com.ejemplo.repository;

import com.ejemplo.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Optional<Categoria> findByNombre(String nombre);

    List<Categoria> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT c FROM Categoria c LEFT JOIN FETCH c.productos WHERE c.id = :id")
    Optional<Categoria> findByIdWithProductos(Long id);

    boolean existsByNombre(String nombre);
}
