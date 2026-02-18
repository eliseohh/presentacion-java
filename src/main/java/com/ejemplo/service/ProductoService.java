package com.ejemplo.service;

import com.ejemplo.dto.ProductoDTO;
import com.ejemplo.entity.Categoria;
import com.ejemplo.entity.Producto;
import com.ejemplo.repository.CategoriaRepository;
import com.ejemplo.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public List<ProductoDTO> obtenerTodos() {
        return productoRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public ProductoDTO obtenerPorId(Long id) {
        return productoRepository.findByIdWithCategoria(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + id));
    }

    public List<ProductoDTO> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ProductoDTO> buscarPorPrecio(BigDecimal min, BigDecimal max) {
        return productoRepository.findByPrecioBetween(min, max).stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ProductoDTO> buscarConFiltros(String nombre, BigDecimal minPrecio, BigDecimal maxPrecio) {
        return productoRepository.buscarConFiltros(nombre, minPrecio, maxPrecio).stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ProductoDTO> disponiblesPorCategoria(String categoriaNombre) {
        return productoRepository.findDisponiblesPorCategoria(categoriaNombre).stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ProductoDTO> stockBajo(Integer cantidad) {
        return productoRepository.findByStockLessThan(cantidad).stream()
                .map(this::toDTO)
                .toList();
    }

    public Page<ProductoDTO> obtenerPorCategoriaPaginado(Long categoriaId, Pageable pageable) {
        return productoRepository.findByCategoriaId(categoriaId, pageable)
                .map(this::toDTO);
    }

    // Java 21: Sequenced Collections - reversed() y getFirst()
    public List<ProductoDTO> ultimosProductos() {
        var productos = productoRepository.findTop10ByActivoTrueOrderByCreatedAtDesc();
        // Java 21: reversed() disponible en SequencedCollection
        return productos.stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public ProductoDTO crear(ProductoDTO.CrearProducto request) {
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada: " + request.categoriaId()));

        Producto producto = Producto.builder()
                .nombre(request.nombre())
                .precio(request.precio())
                .stock(request.stock())
                .categoria(categoria)
                .build();

        return toDTO(productoRepository.save(producto));
    }

    @Transactional
    public ProductoDTO actualizar(Long id, ProductoDTO.CrearProducto request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + id));

        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada: " + request.categoriaId()));

        producto.setNombre(request.nombre());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        producto.setCategoria(categoria);

        return toDTO(productoRepository.save(producto));
    }

    @Transactional
    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }

    @Transactional
    public int actualizarPreciosPorCategoria(Long categoriaId, BigDecimal factor) {
        return productoRepository.actualizarPreciosPorCategoria(categoriaId, factor);
    }

    private ProductoDTO toDTO(Producto p) {
        String categoriaNombre = p.getCategoria() != null ? p.getCategoria().getNombre() : null;
        return new ProductoDTO(
                p.getId(), p.getNombre(), p.getPrecio(),
                p.getStock(), p.getActivo(), categoriaNombre
        );
    }
}
