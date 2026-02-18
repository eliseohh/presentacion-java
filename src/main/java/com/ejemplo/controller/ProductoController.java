package com.ejemplo.controller;

import com.ejemplo.dto.ProductoDTO;
import com.ejemplo.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService service;

    @GetMapping
    public List<ProductoDTO> listar() {
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ProductoDTO obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @GetMapping("/buscar")
    public List<ProductoDTO> buscar(@RequestParam String nombre) {
        return service.buscarPorNombre(nombre);
    }

    @GetMapping("/filtrar")
    public List<ProductoDTO> filtrar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) BigDecimal minPrecio,
            @RequestParam(required = false) BigDecimal maxPrecio) {
        return service.buscarConFiltros(nombre, minPrecio, maxPrecio);
    }

    @GetMapping("/precio")
    public List<ProductoDTO> porPrecio(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        return service.buscarPorPrecio(min, max);
    }

    @GetMapping("/categoria/{nombre}")
    public List<ProductoDTO> porCategoria(@PathVariable String nombre) {
        return service.disponiblesPorCategoria(nombre);
    }

    @GetMapping("/categoria/{categoriaId}/paginado")
    public Page<ProductoDTO> porCategoriaPaginado(
            @PathVariable Long categoriaId, Pageable pageable) {
        return service.obtenerPorCategoriaPaginado(categoriaId, pageable);
    }

    @GetMapping("/stock-bajo")
    public List<ProductoDTO> stockBajo(@RequestParam(defaultValue = "10") Integer cantidad) {
        return service.stockBajo(cantidad);
    }

    @GetMapping("/ultimos")
    public List<ProductoDTO> ultimos() {
        return service.ultimosProductos();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoDTO crear(@RequestBody ProductoDTO.CrearProducto request) {
        return service.crear(request);
    }

    @PutMapping("/{id}")
    public ProductoDTO actualizar(@PathVariable Long id, @RequestBody ProductoDTO.CrearProducto request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    @PatchMapping("/categoria/{categoriaId}/precios")
    public int actualizarPrecios(
            @PathVariable Long categoriaId, @RequestParam BigDecimal factor) {
        return service.actualizarPreciosPorCategoria(categoriaId, factor);
    }
}
