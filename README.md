# Verein Verwaltung - Spring Boot Template

Ein professionelles Spring Boot Template für die Mitgliederverwaltung von Vereinen. Das Projekt bietet eine vollständige REST-API mit PostgreSQL-Datenbank, umfassenden Tests und Docker-Unterstützung.

## Inhaltsverzeichnis

- [Überblick](#überblick)
- [Technologien](#technologien)
- [Projektstruktur](#projektstruktur)
- [Funktionen](#funktionen)
- [API Endpunkte](#api-endpunkte)
- [Installation](#installation)
- [Tests](#tests)
- [Docker](#docker)
- [Entwicklung](#entwicklung)

---

## Überblick

Dieses Template dient als Grundlage für die Entwicklung einer Vereinsverwaltungsanwendung. Es implementsiert die gängigsten Funktionen für die Verwaltung von Vereinen und deren Mitgliedern.

### Hauptmerkmale

- **Vereinsverwaltung**: Erstellen, Bearbeiten, Löschen und Abrufen von Vereinen
- **Mitgliederverwaltung**: Vollständige CRUD-Operationen für Vereinsmitglieder
- **Membership-Typen**: Unterstützung für verschiedene Mitgliedschaftstypen (REGULAR, FAMILY, STUDENT, SENIOR, HONORARY)
- **Membership-Status**: Verwaltung von Mitgliedschaftsstatus (ACTIVE, INACTIVE, SUSPENDED, CANCELLED)
- **JWT-Authentifizierung**: Sichere REST-API mit Bearer Token Authentifizierung
- **Rollenbasierte Zugriffskontrolle**: @PreAuthorize für ADMIN und USER Rollen
- **Pagination**: Paginiertes Abrufen mit Such- und Filterfunktionen
- **CORS Support**: Unterstützung für React, Angular, Vue.js Frontends
- **Soft Delete**: Gelöschte Daten können wiederhergestellt werden
- **Audit Logging**: Vollständige Protokollierung aller Aktionen
- **OpenAPI/Swagger**: Automatisch generierte API-Dokumentation
- **Validierung**: Automatische Validierung von Eingabedaten
- **Fehlerbehandlung**: Zentrale Ausnahmebehandlung mit aussagekräftigen Fehlermeldungen

---

## Technologien

| Kategorie | Technologie | Version |
|-----------|-------------|---------|
| Framework | Spring Boot | 4.0.5 |
| Java Version | OpenJDK / JDK | 25 |
| Datenbank | PostgreSQL | 15 |
| Build-Tool | Maven | 4.0.0 |
| ORM | Spring Data JPA / Hibernate | - |
| Testing | JUnit 5, Mockito, Spring Test | - |
| E2E Testing | Testcontainers, RestAssured | 1.20.0 / 5.5.0 |
| Lombok | Project Lombok | 1.18.40 |

---

## Projektstruktur

```
verein-verwaltung/
├── src/
│   ├── main/
│   │   ├── java/com/verein/
│   │   │   ├── config/          # Konfigurationen
│   │   │   ├── controller/      # REST Controller
│   │   │   ├── dto/            # Data Transfer Objects
│   │   │   ├── entity/         # JPA Entities
│   │   │   ├── repository/     # Spring Data Repositories
│   │   │   └── service/        # Business Logic
│   │   └── resources/
│   │       └── application.yml # Anwendungskonfiguration
│   │
│   └── test/
│       ├── java/com/verein/
│       │   ├── controller/    # Controller Tests
│       │   ├── e2e/           # End-to-End Tests
│       │   ├── repository/    # Repository Tests
│       │   └── service/      # Service Unit Tests
│       └── resources/
│           └── application.yml # Test-Konfiguration
│
├── Dockerfile                   # Docker Image Konfiguration
├── docker-compose.yml           # Docker Compose Konfiguration
└── pom.xml                      # Maven Konfiguration
```

---

## Funktionen

### Entities

#### Club (Verein)
- `id`: Eindeutige Identifikationsnummer
- `name`: Name des Vereins (Pflichtfeld)
- `description`: Beschreibung des Vereins
- `foundedDate`: Gründungsdatum
- `city`: Stadt des Vereins
- `members`: Liste der Mitglieder (One-to-Many Beziehung)

#### Member (Mitglied)
- `id`: Eindeutige Identifikationsnummer
- `firstName`: Vorname (Pflichtfeld)
- `lastName`: Nachname (Pflichtfeld)
- `email`: E-Mail-Adresse (Pflichtfeld, eindeutig)
- `phoneNumber`: Telefonnummer
- `birthDate`: Geburtsdatum
- `gender`: Geschlecht
- `membershipDate`: Datum des Beitritts
- `membershipType`: Typ der Mitgliedschaft
- `status`: Status der Mitgliedschaft
- `club`: Zugehöriger Verein (Many-to-One Beziehung)

### Enums

#### MembershipType
- `REGULAR`: Normale Mitgliedschaft
- `FAMILY`: Familienmitgliedschaft
- `STUDENT`: Studentenmitgliedschaft
- `SENIOR`: Seniorenmitgliedschaft
- `HONORARY`: Ehrenmitgliedschaft

#### MembershipStatus
- `ACTIVE`: Aktives Mitglied
- `INACTIVE`: Inaktives Mitglied
- `SUSPENDED`: Gesperrtes Mitglied
- `CANCELLED`: Abgebrochene Mitgliedschaft

#### User (Benutzer)
- `id`: Eindeutige Identifikationsnummer
- `username`: Benutzername (Pflichtfeld, eindeutig)
- `password`: Passwort (BCrypt verschlüsselt)
- `role`: Rolle (USER oder ADMIN)
- `enabled`: Aktiviert/Deaktiviert
- `createdAt`: Erstellungszeitpunkt

---

## API Endpunkte

### Authentifizierung (Auth)

| Methode | Endpunkt | Beschreibung | Rolle |
|---------|----------|---------------|-------|
| POST | `/api/auth/register` | Benutzer registrieren | PUBLIC |
| POST | `/api/auth/login` | Benutzer anmelden | PUBLIC |

### Vereine (Clubs)

| Methode | Endpunkt | Beschreibung | Rolle |
|---------|----------|---------------|-------|
| POST | `/api/clubs` | Neuen Verein erstellen | ADMIN |
| GET | `/api/clubs` | Alle Vereine abrufen | USER/ADMIN |
| GET | `/api/clubs/{id}` | Verein nach ID abrufen | USER/ADMIN |
| GET | `/api/clubs/paginated` | Vereine paginiert abrufen | USER/ADMIN |
| GET | `/api/clubs/search?search=` | Vereine suchen | USER/ADMIN |
| GET | `/api/clubs/deleted` | Gelöschte Vereine abrufen | ADMIN |
| PUT | `/api/clubs/{id}` | Verein aktualisieren | ADMIN |
| DELETE | `/api/clubs/{id}` | Soft Delete Verein | ADMIN |
| PUT | `/api/clubs/{id}/restore` | Gelöschten Verein wiederherstellen | ADMIN |

### Mitglieder (Members)

| Methode | Endpunkt | Beschreibung | Rolle |
|---------|----------|---------------|-------|
| POST | `/api/members` | Neues Mitglied erstellen | ADMIN |
| GET | `/api/members` | Alle Mitglieder abrufen | USER/ADMIN |
| GET | `/api/members/{id}` | Mitglied nach ID abrufen | USER/ADMIN |
| GET | `/api/members/paginated` | Mitglieder paginiert abrufen | USER/ADMIN |
| GET | `/api/members/club/{clubId}` | Mitglieder nach Verein | USER/ADMIN |
| GET | `/api/members/club/{clubId}/paginated` | Mitglieder nach Verein paginiert | USER/ADMIN |
| GET | `/api/members/search?search=` | Mitglieder suchen | USER/ADMIN |
| GET | `/api/members/status/{status}` | Mitglieder nach Status | USER/ADMIN |
| GET | `/api/members/type/{type}` | Mitglieder nach Typ | USER/ADMIN |
| GET | `/api/members/deleted` | Gelöschte Mitglieder abrufen | ADMIN |
| PUT | `/api/members/{id}` | Mitglied aktualisieren | ADMIN |
| DELETE | `/api/members/{id}` | Soft Delete Mitglied | ADMIN |
| PUT | `/api/members/{id}/restore` | Gelöschtes Mitglied wiederherstellen | ADMIN |

### Audit

| Methode | Endpunkt | Beschreibung | Rolle |
|---------|----------|---------------|-------|
| GET | `/api/audit/entity/{entityName}/{entityId}` | Entity-Verlauf abrufen | ADMIN |
| GET | `/api/audit/user/{username}` | Benutzer-Aktivität abrufen | ADMIN |
| GET | `/api/audit/type/{entityName}` | Entity-Typ Verlauf abrufen | ADMIN |
| GET | `/api/audit/all` | Alle Audit-Logs abrufen | ADMIN |
| GET | `/api/audit/action?entityName=&action=` | Audit-Logs nach Aktion | ADMIN |

### OpenAPI / Swagger

| URL | Beschreibung |
|-----|---------------|
| `/swagger-ui.html` | Swagger UI (Web-Interface) |
| `/v3/api-docs` | OpenAPI 3.0 JSON |
| `/v3/api-docs.yaml` | OpenAPI 3.0 YAML |

### Beispiele

#### Benutzer registrieren
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123"
  }'
```

#### Benutzer anmelden
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123"
  }'
```

#### Verein erstellen (mit Token)
```bash
# Zuerst einloggen um Token zu erhalten
TOKEN=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}' | jq -r '.token')

# Dann mit Token aufrufen
curl -X POST http://localhost:8081/api/clubs \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "FC Beispiel",
    "description": "Ein Beispielverein",
    "city": "Berlin"
  }'
```

#### Alle Vereine abrufen (mit Token)
```bash
curl http://localhost:8081/api/clubs \
  -H "Authorization: Bearer $TOKEN"
```

#### Mitglied erstellen
```bash
curl -X POST http://localhost:8081/api/members \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "firstName": "Max",
    "lastName": "Mustermann",
    "email": "max@example.com",
    "phoneNumber": "123456789",
    "membershipType": "REGULAR",
    "status": "ACTIVE",
    "clubId": 1
  }'
```

#### Alle Vereine abrufen
```bash
curl http://localhost:8081/api/clubs \
  -H "Authorization: Bearer $TOKEN"
```

#### Paginiert abrufen
```bash
curl "http://localhost:8081/api/clubs/paginated?page=0&size=10&sortBy=name&sortDir=asc" \
  -H "Authorization: Bearer $TOKEN"
```

#### Nach Namen suchen
```bash
curl "http://localhost:8081/api/clubs/search?search=Berlin&page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"
```

#### Mitglieder paginiert abrufen
```bash
curl "http://localhost:8081/api/members/paginated?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"
```

#### Mitglieder nach Status filtern
```bash
curl "http://localhost:8081/api/members/status/ACTIVE?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"
```

---

## Installation

### Voraussetzungen

- Java 8, 11, 17, 21 oder 25 (JDK 25 wird empfohlen)
- Maven 4.0.0 (wird automatisch durch ci/cit.cmd verwendet)
- PostgreSQL 15 (für lokale Entwicklung)
- Docker und Docker Compose (optional)

### Build-Befehle

Das Projekt enthält `ci.cmd` und `cit.cmd` Skripte für einfaches Kompilieren:

| Befehl | Beschreibung |
|--------|---------------|
| `ci 25` | Kompiliert ohne Tests (JDK 25, Maven 4) |
| `cit 25` | Kompiliert mit Tests (JDK 25, Maven 4) |
| `ci 21` | Kompiliert ohne Tests (JDK 21, Maven 4) |
| `cit 21` | Kompiliert mit Tests (JDK 21, Maven 4) |

Alternative mit direktem Maven-Aufruf:

```bash
# Kompilieren ohne Tests
mvn clean compile -DskipTests

# Kompilieren mit Tests
mvn clean test
```

### Lokale Entwicklung

1. **Projekt klonen**
   ```bash
   git clone https://github.com/CavdarKemal/KiloCodeTemplate.git
   cd KiloCodeTemplate
   ```

2. **Datenbank konfigurieren**
   
   Die Anwendung erwartet eine PostgreSQL-Datenbank. Konfigurieren Sie die Verbindung in `src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/verein_db
       username: postgres
       password: postgres
   ```

3. **Projekt kompilieren (JDK 25)**
   ```bash
   ci 25
   ```

   Oder mit direktem Maven (Java 25 muss installiert sein):
   ```bash
   mvn clean compile -DskipTests
   ```

4. **Anwendung starten**
   ```bash
   mvn spring-boot:run
   ```

   Die Anwendung ist dann unter `http://localhost:8080` erreichbar.

---

## Spring Boot 4 Änderungen

Dieses Projekt verwendet Spring Boot 4.0.5 mit Java 25. Es gibt einige Breaking Changes gegenüber Spring Boot 2/3:

### Wichtige Änderungen
- **Jakarta EE**: Alle `javax.*` Packages wurden durch `jakarta.*` ersetzt
- **@MockBean entfernt**: Verwende stattdessen `@MockitoBean`
- **@SpyBean entfernt**: Verwende stattdessen `@MockitoSpyBean`
- **TestEntityManager entfernt**: Verwende standard `EntityManager`
- **Modulare Test-Starter**: `spring-boot-starter-webmvc-test` und `spring-boot-starter-data-jpa-test` werden separat benötigt
- **Java 21+ erforderlich**: Spring Boot 4 benötigt mindestens Java 21

---

## Tests

Das Projekt enthält verschiedene Testtypen, die alle eine umfassende Testabdeckung gewährleisten.

### Testtypen

| Typ | Beschreibung | Ausführung |
|-----|--------------|------------|
| **Unit Tests** | Testen einzelne Service-Klassen mit Mocks | `mvn test -Dtest="com.verein.service.*Test"` |
| **Repository Tests** | Integrationstests mit H2 In-Memory-DB | `mvn test -Dtest="com.verein.repository.*Test"` |
| **Controller Tests** | Testen REST-Endpoints mit MockMvc | `mvn test -Dtest="com.verein.controller.*Test"` |
| **E2E Tests** | Vollständige Integrationstests mit Testcontainers | `mvn test -Dtest="com.verein.e2e.*Test"` |

### Alle Tests ausführen

```bash
mvn test
```

### Nur Unit- und Integrationstests (ohne E2E)

```bash
mvn test -Dtest="com.verein.service.*Test,com.verein.controller.*Test,com.verein.repository.*Test"
```

### Testergebnisse

Die Testergebnisse werden im `target/surefire-reports/` Verzeichnis gespeichert.

---

## Docker

Das Projekt enthält Docker-Unterstützung für einfaches Deployment und Entwicklung.

### Voraussetzungen

- Docker
- Docker Compose

### Container starten

1. **Image bauen und Container starten**
   ```bash
   docker-compose up -d
   ```

2. **Status der Container prüfen**
   ```bash
   docker ps
   ```

3. **Logs anzeigen**
   ```bash
   docker logs verein-app
   ```

### Container-Struktur

| Container | Beschreibung | Port |
|-----------|---------------|------|
| `verein-app` | Spring Boot Anwendung | 8081 → 8080 |
| `verein-postgres` | PostgreSQL Datenbank | 5434 → 5432 |

### Container stoppen

```bash
docker-compose down
```

### Docker Hub Image (Optional)

Das Image kann auch manuell gebaut und deployed werden:

```bash
# Build
docker build -t verein-verwaltung .

# Container starten (ohne docker-compose)
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/verein_db \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  verein-verwaltung
```

---

## Entwicklung

### Empfohlene IDE-Konfiguration

Das Projekt enthält VS Code Einstellungen in `.vscode/settings.json` mit:
- Java Language Support
- Maven for Java
- Spring Boot Dashboard

### Code-Konventionen

- Lombok für automatische Getter/Setter/Builder
- Interface + Implementierung für Services
- DTOs für API-Requests und -Responses
- JUnit 5 für alle Tests

### Nützliche Maven-Befehle

```bash
# Projekt neu bauen
mvn clean package

# Nur kompilieren
mvn compile

# Tests überspringen
mvn clean package -DskipTests

# JAR-Datei erstellen
mvn jar:jar

# Abhängigkeiten aktualisieren
mvn dependency:resolve
```

---

## Lizenz

Dieses Projekt ist für Lern- und Entwicklungszwecke frei verfügbar.

---

## Autor

Kemal Cavdar

---

## Kontakt

Bei Fragen oder Problemen erstellen Sie bitte ein Issue auf GitHub.