# Tariff Heatmap & Comparison

A simple web app to explore and compare international tariff data.

## Objective
Provide an easy way to visualise and compare tariffs:
- Interactive world heatmap showing relative tariff strength (per‑type percentile, continuous gradient).
- Comparison table with sorting, filtering and date-range controls to check whether tariffs are in effect.
- Tooltips and legends that show raw values and unit types for clarity.
- Admins have the ability to create new tariffs and products

## Key features
- Heatmap: continuous color gradient; score = per‑type percentile (relative across countries). Legend explains interpretation.
- Comparison table: sortable, filterable, with an "In Effect" column driven by effective / expiry dates and optional date range inputs.
- API-backed backend (Spring Boot) and Next.js frontend. Same‑origin proxying used in development.

## Run backend (local)
1. Open terminal and go to backend folder:
   cd "CS203/.../CS203/tariff-backend"
2. Run:
   ./mvnw spring-boot:run
   or
   mvn spring-boot:run
3. Backend default port: 8080 (adjust application.properties if changed).
4. Run tests:
   ./mvnw test

## Run frontend (local)
1. Open terminal and go to frontend folder:
   cd "CS203/.../CS203/frontend"
2. Install dependencies:
   npm install
3. Start dev server:
   npm run dev
4. App default: http://localhost:3000
   - Heatmap page: /heatmap
   - Other pages: /view-tariffs, /view-products, etc.

## Quick troubleshooting
- Backend: check stacktrace for NoSuchBeanDefinitionException or FK constraint errors. Use mvn test and logs.
- Frontend: check browser console / network for failed fetches; verify /backapi rewrites and that backend is reachable.
- If filters or computed columns appear stale, ensure relevant state (startPeriod/endPeriod) is included in the table state so react-table recomputes.

That’s it — run the backend first, then start the frontend and explore the Heatmap and Comparison table.