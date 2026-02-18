package com.ejemplo.controller;

import com.ejemplo.dto.CategoriaDTO;
import com.ejemplo.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService service;

    @GetMapping
    public List<CategoriaDTO> listar() {
        return service.obtenerTodas();
    }

    @GetMapping("/{id}")
    public CategoriaDTO obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @GetMapping("/{id}/productos")
    public CategoriaDTO.ConProductos obtenerConProductos(@PathVariable Long id) {
        return service.obtenerConProductos(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaDTO crear(@RequestBody CategoriaDTO request) {
        return service.crear(request.nombre(), request.descripcion());
    }

    @PutMapping("/{id}")
    public CategoriaDTO actualizar(@PathVariable Long id, @RequestBody CategoriaDTO request) {
        return service.actualizar(id, request.nombre(), request.descripcion());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
