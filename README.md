[![CICD](https://github.com/amigoscode/spring-boot-fullstack-professional/actions/workflows/deploy.yml/badge.svg?branch=main)](https://github.com/amigoscode/spring-boot-fullstack-professional/actions/workflows/deploy.yml)

https://amigoscode.com/p/full-stack-spring-boot-react

![Cover](https://user-images.githubusercontent.com/40702606/111074799-bdfbcf00-84dc-11eb-98c0-d40a99aa0da7.png)

# Course Description
Spring Boot allows to take an idea/prototype and turn it into a real thing in matters minutes hours of months and years. A lot of companies use Spring Boot because it's easy to setup, learn and write code very fast without having to setup the low level platform code. Recently, Netflix has decided to switch their entire backend to Spring Boot. This shows that Spring Boot is a must if you are or want to become a software engineer in the Java world.
This course teaches how to build a full stack application from the ground up and touches on very import concepts used in real live software development. Concepts such as:

- Spring Boot Backend API
- Frontend with React.js Hooks and Functions Components
- Maven Build Tool
- Databases using Postgres on Docker
- Spring Data JPA
- Server and Client Side Error Handling
- Packaging applications for deployment using Docker and Jib
- AWS RDS & Elastic Beanstalk
- Software Deployment Automation with Github Actions
- Software Deployment Monitoring with Slack
- Unit and Integration Testing

This course focus on teaching you the process needed to build your own apps and deploy to real users using real software development techniques and skills. The skills gained at the end of this can be applied immediately on your own projects, university projects and at your work place.

Have you got what it takes to become a professional software engineer? Cool I'll see you inside. https://amigoscode.com/p/full-stack-spring-boot-react

![Screenshot 2021-03-11 at 22 56 19](https://user-images.githubusercontent.com/40702606/111074929-5003d780-84dd-11eb-8284-e7c92c7e2905.png)

<img width="773" alt="Screenshot 2021-03-12 at 20 48 48" src="https://user-images.githubusercontent.com/40702606/111074947-627e1100-84dd-11eb-9d3f-85fdbf23e290.png">

## Workforce Attendance Setup

Run local infrastructure:

```powershell
docker compose up -d postgres redis
```

Run the backend:

```powershell
.\mvnw.cmd -DskipTests -P!build-frontend spring-boot:run
```

The local backend runs on `http://localhost:8082`. The React app calls that API directly.

## Supabase Staging Database

Use the Supabase connection pooler JDBC URL for staging and production, not the direct database URL. The pooler URL uses port `6543` and is designed for moderate concurrent traffic through PgBouncer.

Set these environment variables for the `staging` profile:

```text
SPRING_PROFILES_ACTIVE=staging
SUPABASE_POOLER_JDBC_URL=jdbc:postgresql://aws-...pooler.supabase.com:6543/postgres?sslmode=require
SUPABASE_DB_USERNAME=...
SUPABASE_DB_PASSWORD=...
WORKFORCE_CORS_ALLOWED_ORIGINS=https://your-frontend.example.com
REDIS_HOST=...
REDIS_PORT=6379
```

`application-staging.properties` sets Hikari `max-lifetime` below common Supabase idle limits, enables keepalive, and raises the pool size for staging traffic. External API calls use short connect/read timeouts and are made before overtime summary database reads so slow third-party services do not hold database connections.

