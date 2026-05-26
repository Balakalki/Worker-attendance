[![CICD](https://github.com/amigoscode/spring-boot-fullstack-professional/actions/workflows/deploy.yml/badge.svg?branch=main)](https://github.com/amigoscode/spring-boot-fullstack-professional/actions/workflows/deploy.yml)

# Spring Boot Fullstack Professional

A full-stack Spring Boot + React workforce attendance application.

## Overview

This project includes:

- initial project set up cloned from https://github.com/amigoscode/spring-boot-fullstack-professional 
- Spring Boot backend API
- React frontend under `src/frontend`
- Maven build with frontend integration via `frontend-maven-plugin`
- PostgreSQL and Redis support
- Multiple profiles: `default`, `dev`, and `staging`

## Prerequisites

- Java 17+ or Java 23 installed
- Maven wrapper available via `mvnw.cmd`
- Docker Desktop (for local Postgres/Redis in dev)
- Node/npm installed if you want to run the frontend separately

## Run modes

The application supports three modes:

1. `default` profile (no active profile)
2. `dev` profile
3. `staging` profile

All modes load `application.properties` first, then overlay the profile-specific file when a profile is active.

Use cmd to run all the bellow commands

### Default mode

This is the fallback mode when no `SPRING_PROFILES_ACTIVE` is set.

Start local infrastructure:

```powershell
docker compose up -d postgres redis
```

```powershell
.\ mvnw spring-boot:run
```

The backend runs on `http://localhost:8082` using the values in `src/main/resources/application.properties`.

### Dev mode

Use the `dev` profile for local development with Docker-managed Postgres and Redis.

Start local infrastructure:

```powershell
docker compose up -d postgres redis
```

Run the backend with the `dev` profile:

```powershell
set SPRING_PROFILES_ACTIVE=dev
.\mvnw spring-boot:run
```

Dev mode uses the local database settings from `src/main/resources/application-dev.properties` and still listens on port `8082`.

### Staging mode

Staging mode uses the Supabase pooler URL and environment variables for secrets.

Set the required staging variables in Windows CMD like this:

```cmd
set SPRING_PROFILES_ACTIVE=staging
set SUPABASE_POOLER_JDBC_URL=jdbc:postgresql://your-supabase-pooler.supabase.co:6543/postgres?sslmode=require
set SUPABASE_DB_USERNAME=your_db_username
set SUPABASE_DB_PASSWORD=your_db_password
set WORKFORCE_CORS_ALLOWED_ORIGINS=https://your-frontend.example.com
```

Then start the app:

```powershell
.\mvnw spring-boot:run
```

Staging mode reads values from `src/main/resources/application-staging.properties`, including:

- `SUPABASE_POOLER_JDBC_URL`
- `SUPABASE_DB_USERNAME`
- `SUPABASE_DB_PASSWORD`
- `WORKFORCE_CORS_ALLOWED_ORIGINS`

The staging configuration also adjusts Hikari pool settings for production-style database traffic.

## Notes

- The `build-frontend` Maven profile is active by default. Use `-P!build-frontend` to skip frontend build during backend startup.
- The backend server uses port `8082` by default.
- For local dev, Postgres is expected at `jdbc:postgresql://localhost:5433/workforce` and Redis at `localhost:6379`.
- For staging, use the Supabase pooler JDBC URL on port `6543` rather than the direct database URL.

## Running the frontend separately

If you want to work on the React frontend separately, open `src/frontend` and run:

```powershell
npm install
```
no need to run this separately that will be build by maven while running backend and lisents to same port on which backend is running

## Useful commands

```powershell
# Local dev backend
set SPRING_PROFILES_ACTIVE=dev
.\mvnw spring-boot:run

# Default backend
.\mvnw spring-boot:run

# Staging backend
set SPRING_PROFILES_ACTIVE=staging
set SUPABASE_POOLER_JDBC_URL=... 
set SUPABASE_DB_USERNAME=...
set SUPABASE_DB_PASSWORD=...
set WORKFORCE_CORS_ALLOWED_ORIGINS=https://your-frontend.example.com
.\mvnw.cmd -DskipTests -P!build-frontend spring-boot:run
```

## AI tools used

- used codex to build and set up the project and work on it
- used gpt to understand the folders JPA and what Hibernate do and other queries I got while developing
- copilot to generate the README.md file and later modified it according to the requirements


## Features Included if more time

- site and worker activate and deactivate
- site update
- payment history
- list all overtime workers and apply filters such as settled, pending, date range etc as of now we only have per worker per month listing.
- front end improvements like preventing multiple button clicks (ex: to create site if we click create site then the button should be disabled preventing another api request with same data) etc.
- logs for debuging

## Addition features implemented

- create and update worer
- create site