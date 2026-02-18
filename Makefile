BASE_URL := http://localhost:8080/api

# ============================================================
# App lifecycle
# ============================================================

run: ## Start app with dev profile (H2 in-memory)
	mvn spring-boot:run -Dspring-boot.run.profiles=dev

test: ## Run tests
	mvn test

clean: ## Clean build artifacts
	mvn clean

build: ## Build the project
	mvn clean package -DskipTests

update: ## Update a producto and show audit timestamps → make update ID=1 NOMBRE="Mouse Pro" PRECIO=39.99 STOCK=40 CAT_ID=1
	@echo "=== BEFORE update ==="
	@curl -s $(BASE_URL)/productos/$(ID) | jq '{id, nombre, precio, createdAt, updatedAt}'
	@echo "\n=== Updating producto $(ID)… ==="
	@curl -s -X PUT $(BASE_URL)/productos/$(ID) \
		-H 'Content-Type: application/json' \
		-d '{"nombre":"$(NOMBRE)","precio":$(PRECIO),"stock":$(STOCK),"categoriaId":$(CAT_ID)}' | jq '{id, nombre, precio, createdAt, updatedAt}'
	@echo "\n=== AFTER update ==="
	@curl -s $(BASE_URL)/productos/$(ID) | jq '{id, nombre, precio, createdAt, updatedAt}'

# ============================================================
# Categorias
# ============================================================

cat-list: ## List all categorias
	curl -s $(BASE_URL)/categorias | jq .

cat-get: ## Get categoria by ID → make cat-get ID=1
	curl -s $(BASE_URL)/categorias/$(ID) | jq .

cat-products: ## Get categoria with its productos → make cat-products ID=1
	curl -s $(BASE_URL)/categorias/$(ID)/productos | jq .

cat-create: ## Create a categoria → make cat-create NOMBRE="Ropa" DESC="Prendas de vestir"
	curl -s -X POST $(BASE_URL)/categorias \
		-H 'Content-Type: application/json' \
		-d '{"nombre":"$(NOMBRE)","descripcion":"$(DESC)"}' | jq .

cat-update: ## Update a categoria → make cat-update ID=1 NOMBRE="Tech" DESC="Gadgets"
	curl -s -X PUT $(BASE_URL)/categorias/$(ID) \
		-H 'Content-Type: application/json' \
		-d '{"nombre":"$(NOMBRE)","descripcion":"$(DESC)"}' | jq .

cat-delete: ## Delete a categoria → make cat-delete ID=1
	curl -s -X DELETE $(BASE_URL)/categorias/$(ID) -w "\nHTTP %{http_code}\n"

# ============================================================
# Productos
# ============================================================

prod-list: ## List all productos
	curl -s $(BASE_URL)/productos | jq .

prod-get: ## Get producto by ID → make prod-get ID=1
	curl -s $(BASE_URL)/productos/$(ID) | jq .

prod-search: ## Search productos by name → make prod-search NOMBRE="Laptop"
	curl -s "$(BASE_URL)/productos/buscar?nombre=$(NOMBRE)" | jq .

prod-filter: ## Filter productos → make prod-filter NOMBRE="Laptop" MIN=100 MAX=2000
	curl -s "$(BASE_URL)/productos/filtrar?nombre=$(NOMBRE)&minPrecio=$(MIN)&maxPrecio=$(MAX)" | jq .

prod-price: ## Productos by price range → make prod-price MIN=50 MAX=500
	curl -s "$(BASE_URL)/productos/precio?min=$(MIN)&max=$(MAX)" | jq .

prod-by-cat: ## Productos by category name → make prod-by-cat NOMBRE="Electronica"
	curl -s $(BASE_URL)/productos/categoria/$(NOMBRE) | jq .

prod-by-cat-page: ## Paginated productos by category → make prod-by-cat-page ID=1 PAGE=0 SIZE=5
	curl -s "$(BASE_URL)/productos/categoria/$(ID)/paginado?page=$(PAGE)&size=$(SIZE)" | jq .

prod-low-stock: ## Productos with low stock → make prod-low-stock QTY=10
	curl -s "$(BASE_URL)/productos/stock-bajo?cantidad=$(QTY)" | jq .

prod-latest: ## Latest productos
	curl -s $(BASE_URL)/productos/ultimos | jq .

prod-create: ## Create a producto → make prod-create NOMBRE="Mouse" PRECIO=29.99 STOCK=50 CAT_ID=1
	curl -s -X POST $(BASE_URL)/productos \
		-H 'Content-Type: application/json' \
		-d '{"nombre":"$(NOMBRE)","precio":$(PRECIO),"stock":$(STOCK),"categoriaId":$(CAT_ID)}' | jq .

prod-update: ## Update a producto → make prod-update ID=1 NOMBRE="Mouse Pro" PRECIO=39.99 STOCK=40 CAT_ID=1
	curl -s -X PUT $(BASE_URL)/productos/$(ID) \
		-H 'Content-Type: application/json' \
		-d '{"nombre":"$(NOMBRE)","precio":$(PRECIO),"stock":$(STOCK),"categoriaId":$(CAT_ID)}' | jq .

prod-delete: ## Delete a producto → make prod-delete ID=1
	curl -s -X DELETE $(BASE_URL)/productos/$(ID) -w "\nHTTP %{http_code}\n"

prod-update-prices: ## Bulk update prices by category → make prod-update-prices ID=1 FACTOR=1.10
	curl -s -X PATCH "$(BASE_URL)/productos/categoria/$(ID)/precios?factor=$(FACTOR)" | jq .

# ============================================================
# Demo flow (run all key endpoints in sequence)
# ============================================================

demo: ## Run a full demo sequence against the running app
	@echo "=== Categorias ==="
	@curl -s $(BASE_URL)/categorias | jq .
	@echo "\n=== Productos ==="
	@curl -s $(BASE_URL)/productos | jq .
	@echo "\n=== Buscar 'Laptop' ==="
	@curl -s "$(BASE_URL)/productos/buscar?nombre=Laptop" | jq .
	@echo "\n=== Precio 50-500 ==="
	@curl -s "$(BASE_URL)/productos/precio?min=50&max=500" | jq .
	@echo "\n=== Stock bajo ==="
	@curl -s "$(BASE_URL)/productos/stock-bajo?cantidad=10" | jq .
	@echo "\n=== Ultimos ==="
	@curl -s $(BASE_URL)/productos/ultimos | jq .
	@echo "\n=== Crear categoria ==="
	@curl -s -X POST $(BASE_URL)/categorias \
		-H 'Content-Type: application/json' \
		-d '{"nombre":"Test","descripcion":"Categoria de prueba"}' | jq .
	@echo "\n=== Crear producto ==="
	@curl -s -X POST $(BASE_URL)/productos \
		-H 'Content-Type: application/json' \
		-d '{"nombre":"Producto Test","precio":99.99,"stock":10,"categoriaId":1}' | jq .

# ============================================================
# Audit fields (createdAt / updatedAt via H2 console)
# ============================================================

H2_URL := jdbc:h2:mem:demodb
H2_USER := sa

audit-cat: ## Show categorias with audit timestamps
	@echo "=== Categorias — audit fields ==="
	@curl -s "http://localhost:8080/h2-console" > /dev/null
	@curl -s $(BASE_URL)/categorias | jq '.[] | {id, nombre, createdAt, updatedAt}'

audit-prod: ## Show productos with audit timestamps
	@echo "=== Productos — audit fields ==="
	@curl -s $(BASE_URL)/productos | jq '.[] | {id, nombre, precio, createdAt, updatedAt}'

audit-all: ## Show audit timestamps for all tables
	@echo "=== Categorias — audit fields ==="
	@curl -s $(BASE_URL)/categorias | jq '.[] | {id, nombre, createdAt, updatedAt}'
	@echo "\n=== Productos — audit fields ==="
	@curl -s $(BASE_URL)/productos | jq '.[] | {id, nombre, precio, createdAt, updatedAt}'

# ============================================================
# Help
# ============================================================

help: ## Show this help
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2}'

.PHONY: run test clean build update help demo \
	cat-list cat-get cat-products cat-create cat-update cat-delete \
	prod-list prod-get prod-search prod-filter prod-price prod-by-cat \
	prod-by-cat-page prod-low-stock prod-latest prod-create prod-update \
	prod-delete prod-update-prices \
	audit-cat audit-prod audit-all

.DEFAULT_GOAL := help
