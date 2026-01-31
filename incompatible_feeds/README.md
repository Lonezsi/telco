# Telco Product Integration System

## Project Architecture

- **backend**: Spring Boot 3.x (Java 17), Hibernate/JPA, H2 Database.
- **frontend**: React 18 (TypeScript), Vite, Axios, Lucide-React.
- **logic**:
  - Normalizes SKU formats (`P-1001` -> `SKU1001`).
  - Converts Net prices to Gross (27% VAT)
  - Merges duplicates, priority is given to latest

## How to Run

### Quickrun

- run `cd backend && ./mvnw spring-boot:run`
- then run `cd frontend && npm install && npm start`

### Backend

- in `/backend`.
- run: `./mvnw spring-boot:run`
- url: `http://localhost:8080/products`
- uses: `/data` which gets fed into the system at start

## API Specification

**Endpoint:** `GET /products`

| Parameter   | Type    | Description                                                  |
| ----------- | ------- | ------------------------------------------------------------ |
| `filter`    | String  | Optional. searches SKU and Name                              |
| `sort`      | String  | Optional. sort by `sku`, `name`, `finalPriceHuf`, or `stock` |
| `onlyValid` | Boolean | Optional. if `true`, hides records with validation errors.   |

**Response Format (JSON):**

```json
[
  {
    "sku": "SKU1001",
    "name": "Smartphone X",
    "finalPriceHuf": 127000.0,
    "valid": true,
    "source": "MERGED"
  }
]
```

### Frontend

- in `/frontend`.
- run: `npm install`
- run: `npm start`
- url: `http://localhost:5173`

## Features

- filter: search by SKU or name
- validation: missing name/price are flagged
- skeleton: loading table has dummy content for smoother ux
- dark mode
-
