# Spring Data JPA con Java 21

## Desarrollo de persistencia moderna con Java 21

## Requisitos

- Java 21+
- Maven 3.9+

## Comandos

```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar tests
mvn test

# Ejecutar con H2 en memoria (no requiere PostgreSQL)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Ejecutar con PostgreSQL (requiere BD configurada)
mvn spring-boot:run

# Empaquetar como JAR
mvn clean package -DskipTests

# Ejecutar el JAR
java -jar target/spring-data-jpa-demo-1.0.0.jar --spring.profiles.active=dev
```

### Endpoints disponibles (con perfil dev en http://localhost:8080)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/productos` | Listar todos los productos |
| GET | `/api/productos/{id}` | Obtener producto por ID |
| GET | `/api/productos/buscar?nombre=laptop` | Buscar por nombre |
| GET | `/api/productos/filtrar?nombre=&minPrecio=50&maxPrecio=500` | Filtrar con criterios |
| GET | `/api/productos/precio?min=100&max=500` | Buscar por rango de precio |
| GET | `/api/productos/categoria/{nombre}` | Productos por categoria |
| GET | `/api/productos/stock-bajo?cantidad=10` | Productos con stock bajo |
| POST | `/api/productos` | Crear producto |
| PUT | `/api/productos/{id}` | Actualizar producto |
| DELETE | `/api/productos/{id}` | Eliminar producto |
| GET | `/api/categorias` | Listar categorias |
| GET | `/api/categorias/{id}/productos` | Categoria con sus productos |
| POST | `/api/categorias` | Crear categoria |
| | `/h2-console` | Consola H2 (solo perfil dev) |

---

## Presentacion

## Slide 1: Portada

# Spring Data JPA
**Desarrollo de persistencia moderna con Java 21**

Workshop Técnico

---

## Slide 2: ¿Qué es una ORM?

### Object-Relational Mapping

Una ORM es una técnica de programación que permite mapear objetos de tu aplicación a tablas de una base de datos relacional.

**Beneficios principales:**

- Abstracción del SQL nativo
- Código más mantenible y orientado a objetos
- Reducción del código boilerplate
- Portabilidad entre diferentes bases de datos

> **ORM**: Convierte objetos de Java en filas de base de datos y viceversa

---

## Slide 3: ¿Qué es JPA?

### Java Persistence API

JPA es la especificación estándar de Java para el mapeo objeto-relacional (ORM). Define cómo las aplicaciones Java deben interactuar con bases de datos relacionales.

**No es una implementación**, sino un conjunto de interfaces y anotaciones que diferentes proveedores implementan:

- **Hibernate** (la implementación más popular)
- EclipseLink
- OpenJPA

> **Spring Data JPA**: Facilita el uso de JPA mediante repositorios automáticos

---

## Slide 4: Entidades y Repositorios

### Ejemplo de Entidad

```java
@Entity
@Table(name = "usuarios")
public class Usuario {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(nullable = false)
  private String nombre;
  
  private String email;
}
```

### Repositorio

```java
public interface UsuarioRepository 
  extends JpaRepository<Usuario, Long> {
  
  // Métodos generados automáticamente:
  // save(), findAll(), findById()
  // delete(), count(), etc.
  
  // Query methods personalizados:
  List<Usuario> findByNombre(String nombre);
  
  Optional<Usuario> findByEmail(String email);
}
```

---

## Slide 5: Query Methods

### Composición de Queries

Spring Data JPA permite crear queries mediante la nomenclatura del método:

| Método | Query SQL Generado |
|--------|-------------------|
| `findByNombre(String nombre)` | `SELECT * FROM usuario WHERE nombre = ?` |
| `findByEdadGreaterThan(Integer edad)` | `SELECT * FROM usuario WHERE edad > ?` |
| `findByNombreAndEmail(String n, String e)` | `SELECT * FROM usuario WHERE nombre = ? AND email = ?` |
| `findByNombreContaining(String pattern)` | `SELECT * FROM usuario WHERE nombre LIKE %?%` |
| `findByActivoTrue()` | `SELECT * FROM usuario WHERE activo = true` |

---

## Slide 6: Keywords de Query Methods

### Operadores de comparación:
- GreaterThan, LessThan, Between
- GreaterThanEqual, LessThanEqual
- IsNull, IsNotNull

### Operadores de texto:
- Like, NotLike
- StartingWith, EndingWith, Containing
- IgnoreCase

### Operadores lógicos:
- And, Or, Not
- In, NotIn
- True, False

### Ordenamiento y paginación:
- OrderBy + propiedad + Asc/Desc
- First, Top + número
- Distinct

**Ejemplo:**
```java
List<Usuario> findTop10ByActivoTrueOrderByFechaCreacionDesc()
```

---

## Slide 7: Queries Personalizados con @Query

Para queries más complejos, usa la anotación `@Query` con JPQL o SQL nativo:

### JPQL (Java Persistence Query Language):

```java
@Query("SELECT u FROM Usuario u " +
       "WHERE u.edad > :edad " +
       "AND u.activo = true")
List<Usuario> buscarActivos(
  @Param("edad") Integer edad
);
```

### SQL Nativo:

```java
@Query(value = "SELECT * FROM usuarios " +
       "WHERE edad > ?1 " +
       "AND activo = 1", 
       nativeQuery = true)
List<Usuario> buscarActivosNative(Integer edad);
```

**✓ JPQL:**
- Independiente de BD
- Trabaja con entidades

**⚡ SQL Nativo:**
- Optimización específica de BD
- Funciones especiales

---

## Slide 8: Joins y Relaciones

### Tipos de Relaciones JPA:

| Tipo | Descripción | Ejemplo |
|------|-------------|---------|
| `@OneToOne` | Una entidad se relaciona con una única entidad | Usuario ↔ Perfil |
| `@OneToMany` | Una entidad tiene múltiples entidades relacionadas | Usuario → Pedidos |
| `@ManyToOne` | Múltiples entidades apuntan a una sola | Pedidos ← Usuario |
| `@ManyToMany` | Relación múltiple bidireccional | Estudiantes ↔ Cursos |

### Ejemplo @OneToMany con Join:

```java
@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
private List<Pedido> pedidos;

@Query("SELECT u FROM Usuario u JOIN FETCH u.pedidos WHERE u.id = :id")
Usuario findWithPedidos(@Param("id") Long id);
```

---

## Slide 9: Tipos de Joins en JPQL

### 1. JOIN (INNER JOIN):
```java
SELECT u FROM Usuario u JOIN u.pedidos p
```

### 2. LEFT JOIN:
```java
SELECT u FROM Usuario u LEFT JOIN u.pedidos p
```

### 3. JOIN FETCH (evita N+1):
```java
SELECT u FROM Usuario u JOIN FETCH u.pedidos
```

**⚡ JOIN FETCH**

**Ventajas:**
- Carga eager optimizada
- Evita el problema N+1
- Una sola query SQL

**Úsalo cuando:**
- Necesites las relaciones siempre
- Quieras optimizar rendimiento

---

## Slide 10: Demo Técnica

# Demo Técnica
**Implementación práctica con Java 21**

---

## Slide 11: Demo - Modelo de Datos

### Producto.java:

```java
@Entity
public class Producto {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(nullable = false)
  private String nombre;
  
  private BigDecimal precio;
  private Integer stock;
  
  @ManyToOne
  @JoinColumn(name = "categoria_id")
  private Categoria categoria;
}
```

### Categoria.java:

```java
@Entity
public class Categoria {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  private String nombre;
  private String descripcion;
  
  @OneToMany(mappedBy = "categoria")
  private List<Producto> productos;
}
```

---

## Slide 12: Demo - Repositorios

### ProductoRepository.java:

```java
public interface ProductoRepository extends JpaRepository<Producto, Long> {
  
  // Query methods automáticos
  List<Producto> findByNombreContainingIgnoreCase(String nombre);
  List<Producto> findByPrecioBetween(BigDecimal min, BigDecimal max);
  List<Producto> findByStockLessThan(Integer cantidad);
  
  // Query personalizado con JPQL
  @Query("SELECT p FROM Producto p WHERE p.categoria.nombre = :categoria " +
         "AND p.stock > 0 ORDER BY p.precio ASC")
  List<Producto> findDisponiblesPorCategoria(@Param("categoria") String categoria);
  
  // Join con fetch para evitar N+1
  @Query("SELECT p FROM Producto p JOIN FETCH p.categoria WHERE p.id = :id")
  Optional<Producto> findByIdWithCategoria(@Param("id") Long id);
}
```

---

## Slide 13: Demo - Servicio con Java 21

### ProductoService.java (usando records y pattern matching):

```java
@Service
public class ProductoService {
  
  private final ProductoRepository repository;
  
  // Java 21: Records para DTOs
  public record ProductoDTO(Long id, String nombre, BigDecimal precio, String categoria) {}
  
  public List<ProductoDTO> buscarPorPrecio(BigDecimal min, BigDecimal max) {
    return repository.findByPrecioBetween(min, max).stream()
      .map(p -> new ProductoDTO(
        p.getId(), p.getNombre(), p.getPrecio(), p.getCategoria().getNombre()
      ))
      .toList(); // Java 16+
  }
}
```

---

## Slide 14: Novedades de Java 21 para JPA

### 1. Records como DTOs
Clases inmutables perfectas para transferir datos desde entidades

### 2. Pattern Matching
Validaciones más limpias en tus servicios y controladores

### 3. Virtual Threads
Mejor concurrencia para operaciones de BD sin bloqueo

### 4. Sequenced Collections
Métodos `.reversed()` y `.getFirst()` en tus listas de entidades

**Ejemplo práctico:**
```java
var productos = repository.findAll();  
// Java 21: reversed() y getFirst()
```

---

## Slide 15: Cierre

# ¡Gracias!

**Spring Data JPA + Java 21**  
Desarrollo moderno de persistencia

¿Preguntas?

---

## Recursos Adicionales

### Documentación oficial:
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [JPA Specification](https://jakarta.ee/specifications/persistence/)
- [Hibernate Documentation](https://hibernate.org/orm/documentation/)

### Mejores prácticas:
- Usa `@Transactional` en métodos de servicio
- Evita el problema N+1 con JOIN FETCH
- Prefiere JPQL sobre SQL nativo cuando sea posible
- Usa DTOs para exponer datos al cliente
- Implementa paginación para grandes volúmenes de datos

### Keywords de Query Methods más usados:
- `findBy`, `findAllBy`, `getBy`, `queryBy`
- `countBy`, `existsBy`, `deleteBy`
- `And`, `Or`, `Between`, `LessThan`, `GreaterThan`
- `Like`, `Containing`, `StartingWith`, `EndingWith`
- `OrderBy`, `Top`, `First`, `Distinct`

### Anotaciones importantes:
- `@Entity`, `@Table`, `@Column`
- `@Id`, `@GeneratedValue`
- `@OneToOne`, `@OneToMany`, `@ManyToOne`, `@ManyToMany`
- `@JoinColumn`, `@JoinTable`
- `@Query`, `@Param`
- `@Transactional`
- `@EntityListeners`, `@PrePersist`, `@PostLoad`

---

## Ejemplo Completo de Aplicación

### application.properties:

```properties
# Database configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/mibd
spring.datasource.username=usuario
spring.datasource.password=password

# JPA configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Logging
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### Estructura de proyecto recomendada:

```
src/main/java/com/ejemplo/
├── entity/
│   ├── Producto.java
│   └── Categoria.java
├── repository/
│   ├── ProductoRepository.java
│   └── CategoriaRepository.java
├── service/
│   ├── ProductoService.java
│   └── CategoriaService.java
├── dto/
│   ├── ProductoDTO.java
│   └── CategoriaDTO.java
└── controller/
    ├── ProductoController.java
    └── CategoriaController.java
```

---

## Casos de Uso Comunes

### 1. Búsqueda con criterios múltiples:

```java
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    @Query("SELECT p FROM Producto p WHERE " +
           "(:nombre IS NULL OR p.nombre LIKE %:nombre%) AND " +
           "(:minPrecio IS NULL OR p.precio >= :minPrecio) AND " +
           "(:maxPrecio IS NULL OR p.precio <= :maxPrecio)")
    List<Producto> buscarConFiltros(
        @Param("nombre") String nombre,
        @Param("minPrecio") BigDecimal minPrecio,
        @Param("maxPrecio") BigDecimal maxPrecio
    );
}
```

### 2. Paginación y ordenamiento:

```java
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    Page<Producto> findByCategoria(
        Categoria categoria, 
        Pageable pageable
    );
}

// Uso:
Pageable pageable = PageRequest.of(0, 10, Sort.by("precio").descending());
Page<Producto> productos = repository.findByCategoria(categoria, pageable);
```

### 3. Proyecciones:

```java
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    // Proyección con interfaz
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
```

### 4. Operaciones en lote:

```java
@Service
public class ProductoService {
    
    @Transactional
    public void actualizarPreciosMasivos(List<Long> ids, BigDecimal incremento) {
        List<Producto> productos = repository.findAllById(ids);
        productos.forEach(p -> p.setPrecio(p.getPrecio().add(incremento)));
        repository.saveAll(productos);
    }
    
    @Transactional
    @Modifying
    @Query("UPDATE Producto p SET p.precio = p.precio * :factor " +
           "WHERE p.categoria.id = :categoriaId")
    int actualizarPreciosPorCategoria(
        @Param("categoriaId") Long categoriaId,
        @Param("factor") BigDecimal factor
    );
}
```

### 5. Auditoría con JPA:

```java
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class Auditable {
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @CreatedBy
    @Column(nullable = false, updatable = false)
    private String createdBy;
    
    @LastModifiedBy
    @Column(nullable = false)
    private String lastModifiedBy;
}

@Entity
public class Producto extends Auditable {
    // ... campos de producto
}
```

---

## Tips de Optimización

### 1. Evitar N+1 con EntityGraph:

```java
@EntityGraph(attributePaths = {"categoria"})
List<Producto> findAll();
```

### 2. Batch fetching:

```properties
spring.jpa.properties.hibernate.default_batch_fetch_size=10
```

### 3. Cache de segundo nivel:

```java
@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Categoria {
    // ...
}
```

```properties
spring.jpa.properties.hibernate.cache.use_second_level_cache=true
spring.jpa.properties.hibernate.cache.region.factory_class=org.hibernate.cache.jcache.JCacheRegionFactory
```

### 4. Read-only queries:

```java
@Transactional(readOnly = true)
public List<ProductoDTO> obtenerTodos() {
    // ...
}
```

---

**¡Fin de la presentación!**