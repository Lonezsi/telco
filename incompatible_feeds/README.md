## Incompatible Data Feeds – Telco Data Integration Assessment

## Történet – Telco kontextus

A vállalat a hazai távközlési piac egyik beszállítója, amely több nagy operátor rendszerei között végez adat-összehangolási és riportkészítési feladatokat. Az ügyfél új analitikai platformot vezet be, azonban a különböző forrásrendszerekből érkező adatok nem kompatibilisek egymással. Az egyik rendszer CSV exportot ad bruttó árakkal és magyar mezőnevekkel, míg a másik JSON REST API-t biztosít nettó árakkal, angol mezőnevekkel és időbélyegekkel. A menedzsment egy gyors, bizonyítékszintű egységesített nézetet kér, amelyre a BI-réteg rá tud csatlakozni.

## Backend feladat

**Technológiák:** Java 17+, Spring Boot, Spring Data JPA, Maven/Gradle, JUnit 5, H2 (in-memory) vagy PostgreSQL.

**Input adatok:**

- CSV: incompatible_feeds_products.csv
- JSON: incompatible_feeds_products.json

**Követelmények:**

- A két forrásból származó adatokat be kell olvasni és egységesíteni.
- Különbségek kezelése: bruttó és nettó ár konverzió, eltérő mezőnevek, duplikált SKU-k, hiányzó mezők.
- Normalizált mezők: sku, name, manufacturer, finalPriceHuf, stock, ean, updatedAt, source.
- Végpont: GET /products?filter=&sort=&onlyValid= (JSON válasz).

## Frontend feladat

**Technológiák:** React / Angular / Vue (TypeScript ajánlott).

**Feladatok:**

- Az API hívása és a terméklista megjelenítése.
- Szűrés, rendezés, keresés, hiba- és töltési állapot kezelése.
- A hibás rekordok jelölése (például külön szekcióban).
- Futtatható npm start paranccsal, rövid README.md leírással.

## Full-stack opció

- Integrált futtatás: mvn spring-boot:run és npm start.
- Közös API-szerződés (README vagy Swagger).
- Egyszerű Dockerfile / docker-compose pluszpontot ér.

## Mintafájlok

- incompatible_feeds_products.csv
- incompatible_feeds_products.json
  A minták szándékosan tartalmaznak adatminőségi eltéréseket (bruttó vs. nettó ár, duplikált és hiányzó mezők, eltérő azonosítók és gyártónevek, valamint JSON oldalon frissítési időbélyeg).

---

## Progress Checklist

- [x] Backend Project Initialization (Spring Boot)
- [x] Domain Entity Modeling (`Product.java`)
- [x] Data Normalization Logic (`SkuNormalizer`)
- [x] Multi-source Integration Service (`ProductIntegrationService`)
- [x] REST API Implementation (`ProductController`)
- [x] Unit & Integration Testing
- [ ] Frontend Project Setup
- [ ] API Integration & State Management
- [ ] UI Filtering & Validation Display
- [ ] Dockerization (Optional Plus)
