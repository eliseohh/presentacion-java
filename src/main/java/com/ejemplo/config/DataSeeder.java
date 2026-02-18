package com.ejemplo.config;

import com.ejemplo.entity.Categoria;
import com.ejemplo.entity.Producto;
import com.ejemplo.repository.CategoriaRepository;
import com.ejemplo.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    @Override
    public void run(String... args) {
        if (categoriaRepository.count() > 0) {
            log.info("Datos ya existentes, saltando seed.");
            return;
        }

        log.info("Cargando datos de ejemplo...");

        // Categorias
        var electronica = categoriaRepository.save(
                Categoria.builder().nombre("Electronica").descripcion("Dispositivos electronicos").build());
        var hogar = categoriaRepository.save(
                Categoria.builder().nombre("Hogar").descripcion("Articulos para el hogar").build());
        var deportes = categoriaRepository.save(
                Categoria.builder().nombre("Deportes").descripcion("Equipamiento deportivo").build());

        // Productos - Electronica
        productoRepository.save(Producto.builder()
                .nombre("Laptop Pro 15").precio(new BigDecimal("1299.99")).stock(25).categoria(electronica).build());
        productoRepository.save(Producto.builder()
                .nombre("Monitor 4K 27\"").precio(new BigDecimal("449.99")).stock(40).categoria(electronica).build());
        productoRepository.save(Producto.builder()
                .nombre("Teclado Mecanico").precio(new BigDecimal("89.99")).stock(100).categoria(electronica).build());
        productoRepository.save(Producto.builder()
                .nombre("Mouse Ergonomico").precio(new BigDecimal("59.99")).stock(150).categoria(electronica).build());

        // Productos - Hogar
        productoRepository.save(Producto.builder()
                .nombre("Aspiradora Robot").precio(new BigDecimal("399.99")).stock(15).categoria(hogar).build());
        productoRepository.save(Producto.builder()
                .nombre("Cafetera Express").precio(new BigDecimal("199.99")).stock(30).categoria(hogar).build());
        productoRepository.save(Producto.builder()
                .nombre("Lampara LED Inteligente").precio(new BigDecimal("34.99")).stock(200).categoria(hogar).build());

        // Productos - Deportes
        productoRepository.save(Producto.builder()
                .nombre("Bicicleta Montana").precio(new BigDecimal("599.99")).stock(8).categoria(deportes).build());
        productoRepository.save(Producto.builder()
                .nombre("Pesas Ajustables").precio(new BigDecimal("149.99")).stock(45).categoria(deportes).build());
        productoRepository.save(Producto.builder()
                .nombre("Banda de Resistencia Set").precio(new BigDecimal("24.99")).stock(5).activo(true).categoria(deportes).build());

        log.info("Datos de ejemplo cargados exitosamente.");
    }
}
