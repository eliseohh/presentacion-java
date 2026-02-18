package com.ejemplo.service;

import com.ejemplo.dto.CategoriaDTO;
import com.ejemplo.dto.ProductoDTO;
import com.ejemplo.entity.Categoria;
import com.ejemplo.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoriaService {

    private final CategoriaRepository repository;

    public List<CategoriaDTO> obtenerTodas() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public CategoriaDTO obtenerPorId(Long id) {
        return repository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada: " + id));
    }

    public CategoriaDTO.ConProductos obtenerConProductos(Long id) {
        Categoria cat = repository.findByIdWithProductos(id)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada: " + id));

        List<ProductoDTO> productos = cat.getProductos().stream()
                .map(p -> new ProductoDTO(
                        p.getId(), p.getNombre(), p.getPrecio(),
                        p.getStock(), p.getActivo(), cat.getNombre(),
                        p.getCreatedAt(), p.getUpdatedAt()
                ))
                .toList();

        return new CategoriaDTO.ConProductos(cat.getId(), cat.getNombre(), cat.getDescripcion(), productos);
    }

    @Transactional
    public CategoriaDTO crear(String nombre, String descripcion) {
        Categoria categoria = Categoria.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .build();
        return toDTO(repository.save(categoria));
    }

    @Transactional
    public CategoriaDTO actualizar(Long id, String nombre, String descripcion) {
        Categoria categoria = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada: " + id));
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);
        return toDTO(repository.save(categoria));
    }

    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    private CategoriaDTO toDTO(Categoria c) {
        return new CategoriaDTO(
                c.getId(),
                c.getNombre(),
                c.getDescripcion(),
                c.getProductos() != null ? c.getProductos().size() : 0,
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}
