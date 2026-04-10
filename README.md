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
- **Validierung**: Automatische Validierung von Eingabedaten
- **Fehlerbehandlung**: Zentrale Ausnahmebehandlung mit aussagekräftigen Fehlermeldungen

---

## Technologien

| Kategorie | Technologie | Version |
|-----------|-------------|---------|
| Framework | Spring Boot | 2.7.18 |
| Java Version | OpenJDK / Eclipse Temurin | 8 |
| Datenbank | PostgreSQL | 15 |
| Build-Tool | Maven | 3.5.3 |
| ORM | Spring Data JPA / Hibernate | - |
| Testing | JUnit 5, Mockito, Spring Test | - |
| E2E Testing | Testcontainers, RestAssured | 1.17.6 |
| Lombok | Project Lombok | - |

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

---

## API Endpunkte

### Vereine (Clubs)

| Methode | Endpunkt | Beschreibung |
|---------|----------|---------------|
| POST | `/api/clubs` | Neuen Verein erstellen |
| GET | `/api/clubs` | Alle Vereine abrufen |
| GET | `/api/clubs/{id}` | Verein nach ID abrufen |
| PUT | `/api/clubs/{id}` | Verein aktualisieren |
| DELETE | `/api/clubs/{id}` | Verein löschen |

### Mitglieder (Members)

| Methode | Endpunkt | Beschreibung |
|---------|----------|---------------|
| POST | `/api/members` | Neues Mitglied erstellen |
| GET | `/api/members` | Alle Mitglieder abrufen |
| GET | `/api/members/{id}` | Mitglied nach ID abrufen |
| GET | `/api/members/club/{clubId}` | Mitglieder nach Verein |
| PUT | `/api/members/{id}` | Mitglied aktualisieren |
| DELETE | `/api/members/{id}` | Mitglied löschen |

### Beispiele

#### Verein erstellen
```bash
curl -X POST http://localhost:8081/api/clubs \
  -H "Content-Type: application/json" \
  -d '{
    "name": "FC Beispiel",
    "description": "Ein Beispielverein",
    "city": "Berlin"
  }'
```

#### Mitglied erstellen
```bash
curl -X POST http://localhost:8081/api/members \
  -H "Content-Type: application/json" \
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
curl http://localhost:8081/api/clubs
```

---

## Installation

### Voraussetzungen

- Java 8 oder höher
- Maven 3.5+
- PostgreSQL 15 (für lokale Entwicklung)
- Docker und Docker Compose (optional)

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

3. **Projekt kompilieren**
   ```bash
   mvn clean compile
   ```

4. **Anwendung starten**
   ```bash
   mvn spring-boot:run
   ```

   Die Anwendung ist dann unter `http://localhost:8080` erreichbar.

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