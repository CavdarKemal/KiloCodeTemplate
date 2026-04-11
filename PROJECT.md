# Verein Verwaltung - Projekt文档

## Inhaltsverzeichnis

1. [Projektübersicht](#projektübersicht)
2. [Funktionen](#funktionen)
3. [Technologie-Stack](#technologie-stack)
4. [Architektur](#architektur)
5. [Datenmodell](#datenmodell)
6. [API-Endpunkte](#api-endpunkte)
7. [Sicherheit](#sicherheit)
8. [Test-Strategie](#test-strategie)
9. [Build & Deployment](#build--deployment)
10. [Konfiguration](#konfiguration)

---

## 1. Projektübersicht

**Projektname:** Verein Verwaltung  
**ArtifactId:** verein-verwaltung  
**Version:** 1.0.0  
**Java Version:** 25  

Ein RESTful-API-Backend für die Verwaltung von Vereinen und deren Mitgliedern. Das System ermöglicht die Verwaltung von Vereinen (Clubs) und Mitgliedern (Members) mit erweiterten Funktionen wie Soft-Delete, Audit-Logging, Pagination und JWT-basierter Authentifizierung.

### Zielgruppe
- Sportvereine und gemeinnützige Organisationen
- Vereinsverwaltungen
- Administratoren und Benutzer

---

## 2. Funktionen

### 2.1 Vereinsverwaltung (Clubs)

| Funktion | Beschreibung |
|----------|--------------|
| **Verein erstellen** | Neuen Verein anlegen (nur ADMIN) |
| **Verein abrufen** | Verein nach ID abrufen |
| **Alle Vereine** | Liste aller aktiven Vereine |
| **Pagination** | Paginiertes Laden mit Sortierung |
| **Suche** | Volltextsuche nach Vereinsnamen |
| **Verein aktualisieren** | Vereinsdaten ändern (nur ADMIN) |
| **Soft-Delete** | Logisches Löschen (nur ADMIN) |
| **Wiederherstellen** | Gelöschten Verein wiederherstellen (nur ADMIN) |
| **Gelöschte abrufen** | Liste aller soft-gelöschten Vereine (nur ADMIN) |

### 2.2 Mitgliederverwaltung (Members)

| Funktion | Beschreibung |
|----------|--------------|
| **Mitglied erstellen** | Neues Mitglied anlegen (nur ADMIN) |
| **Mitglied abrufen** | Mitglied nach ID abrufen |
| **Alle Mitglieder** | Liste aller aktiven Mitglieder |
| **Mitglieder nach Verein** | Mitglieder einem bestimmten Club zuordnen |
| **Pagination** | Paginiertes Laden mit Sortierung |
| **Suche** | Suche nach Nachname |
| **Filter nach Status** | Filterung nach Mitgliedschaftsstatus |
| **Filter nach Typ** | Filterung nach Mitgliedschaftstyp |
| **Mitglied aktualisieren** | Mitgliedsdaten ändern (nur ADMIN) |
| **Soft-Delete** | Logisches Löschen (nur ADMIN) |
| **Wiederherstellen** | Gelöschtes Mitglied wiederherstellen (nur ADMIN) |
| **Gelöschte abrufen** | Liste aller soft-gelöschten Mitglieder (nur ADMIN) |

### 2.3 Authentifizierung

| Funktion | Beschreibung |
|----------|--------------|
| **Registrierung** | Benutzer-Registrierung |
| **Login** | JWT-basierte Anmeldung |
| **Token-Refresh** | Erneuerung abgelaufener Tokens |
| **Rollenverwaltung** | USER und ADMIN Rollen |

### 2.4 Audit-Logging

| Funktion | Beschreibung |
|----------|--------------|
| **Aktionsprotokoll** | Automatisches Logging aller CRUD-Operationen |
| **Benutzer-Tracking** | Erfassung des ausführenden Benutzers |
| **Zeitstempel** | Zeitpunkt der Aktion |
| **Altwert/Neuwert** | Erfassung von Änderungen |

---

## 3. Technologie-Stack

### 3.1 Kern-Frameworks

| Technologie | Version | Verwendung |
|------------|---------|-------------|
| Java | 25 | Primäre Programmiersprache |
| Spring Boot | 4.0.5 | Framework |
| Spring Security | 7.0.4 | Authentifizierung & Autorisierung |
| Spring Data JPA | 4.0.5 | Datenpersistenz |
| Hibernate | 6.5.x | ORM |

### 3.2 Datenbank

| Technologie | Version | Verwendung |
|------------|---------|-------------|
| PostgreSQL | 15+ | Primäre Datenbank |
| H2 | 2.4.x | Test-Datenbank |

### 3.3 Build & Tools

| Technologie | Version | Verwendung |
|------------|---------|-------------|
| Maven | 3.9+ | Build-Tool |
| Lombok | 1.18.40 | Code-Generierung |
| JUnit 5 | 5.11.x | Unit-Testing |
| TestContainers | 1.20.0 | Integration-Testing |
| RestAssured | 5.5.0 | API-Testing |
| Mockito | 5.14.0 | Mocking |
| Jackson | 2.17.1 | JSON-Verarbeitung |

### 3.4 Monitoring & Documentation

| Technologie | Version | Verwendung |
|------------|---------|-------------|
| SpringDoc OpenAPI | 2.6.0 | API-Dokumentation |
| Swagger UI | integriert | Interaktive API-Doku |
| Spring Boot Actuator | 4.0.5 | Monitoring |
| Micrometer | 1.13.x | Metriken |
| Prometheus | - | Metrik-Sammlung |

### 3.5 JWT & Security

| Technologie | Version | Verwendung |
|------------|---------|-------------|
| jjwt | 0.12.5 | JWT-Token-Handling |
| BCrypt | - | Passwort-Hashing |

---

## 4. Architektur

### 4.1 Schichten-Architektur

```
┌─────────────────────────────────────────────┐
│           Presentation Layer              │
│         (REST Controllers)                 │
│  ClubController, MemberController,         │
│  AuthController, AuditController          │
└─────────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────┐
│            Service Layer                   │
│        (Business Logic)                    │
│   ClubService, MemberService, AuditService │
└─────────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────┐
│           Repository Layer                │
│        (Data Access)                      │
│   ClubRepository, MemberRepository,       │
│   UserRepository, AuditLogRepository      │
└─────────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────┐
│             Entity Layer                   │
│        (Domain Models)                    │
│    Club, Member, User, AuditLog           │
└─────────────────────────────────────────────┘
```

### 4.2 Package-Struktur

```
com.verein/
├── config/           # Konfiguration
│   ├── SecurityConfig.java
│   ├── OpenApiConfig.java
│   └── CorsConfig.java
├── controller/      # REST API Controller
│   ├── ClubController.java
│   ├── MemberController.java
│   ├── AuthController.java
│   └── AuditController.java
├── service/         # Business Logic
│   ├── ClubService.java
│   ├── ClubServiceImpl.java
│   ├── MemberService.java
│   ├── MemberServiceImpl.java
│   └── AuditService.java
├── repository/     # Data Access
│   ├── ClubRepository.java
│   ├── MemberRepository.java
│   ├── UserRepository.java
│   └── AuditLogRepository.java
├── entity/         # Domain Models
│   ├── Club.java
│   ├── Member.java
│   ├── User.java
│   ├── AuditLog.java
│   ├── MembershipType.java
│   └── MembershipStatus.java
├── dto/            # Data Transfer Objects
│   ├── ClubRequest.java
│   ├── ClubResponse.java
│   ├── MemberRequest.java
│   ├── MemberResponse.java
│   ├── AuthRequest.java
│   ├── AuthResponse.java
│   ├── UserRequest.java
│   ├── UserResponse.java
│   └── PagedResponse.java
├── security/       # Security
│   ├── JwtUtil.java
│   ├── JwtUtils.java
│   ├── JwtAuthenticationFilter.java
│   ├── CustomUserDetailsService.java
│   └── UserPrincipal.java
├── exception/      # Exception Handling
│   ├── ResourceNotFoundException.java
│   ├── DuplicateResourceException.java
│   └── GlobalExceptionHandler.java
└── VereinVerwaltungApplication.java
```

---

## 5. Datenmodell

### 5.1 Club (Verein)

| Feld | Typ | Nullable | Beschreibung |
|------|-----|---------|-------------|
| id | BIGINT | Nein | Primärschlüssel (auto-generated) |
| name | VARCHAR(255) | Nein | Vereinsname |
| description | VARCHAR(500) | Ja | Beschreibung |
| city | VARCHAR(100) | Ja | Stadt |
| foundedDate | DATE | Ja | Gründungsdatum |
| createdAt | TIMESTAMP | Nein | Erstellungszeitpunkt |
| updatedAt | TIMESTAMP | Nein | Aktualisierungszeitpunkt |
| deletedAt | TIMESTAMP | Ja | Löschzeitpunkt (Soft-Delete) |
| deletedBy | VARCHAR(255) | Ja | Gelöscht von |

### 5.2 Member (Mitglied)

| Feld | Typ | Nullable | Beschreibung |
|------|-----|---------|-------------|
| id | BIGINT | Nein | Primärschlüssel (auto-generated) |
| firstName | VARCHAR(255) | Nein | Vorname |
| lastName | VARCHAR(255) | Nein | Nachname |
| email | VARCHAR(255) | Nein | E-Mail (eindeutig) |
| phoneNumber | VARCHAR(20) | Ja | Telefonnummer |
| gender | VARCHAR(10) | Ja | Geschlecht |
| birthDate | DATE | Ja | Geburtsdatum |
| membershipDate | DATE | Ja | Mitgliedschaftsdatum |
| membershipType | ENUM | Nein | Typ: REGULAR, STUDENT, SENIOR, FAMILY, HONORARY |
| status | ENUM | Nein | Status: ACTIVE, INACTIVE, SUSPENDED, CANCELLED |
| clubId | BIGINT | Nein | Fremdschlüssel zu Club |
| createdAt | TIMESTAMP | Nein | Erstellungszeitpunkt |
| updatedAt | TIMESTAMP | Nein | Aktualisierungszeitpunkt |
| deletedAt | TIMESTAMP | Ja | Löschzeitpunkt (Soft-Delete) |
| deletedBy | VARCHAR(255) | Ja | Gelöscht von |

### 5.3 User (Benutzer)

| Feld | Typ | Nullable | Beschreibung |
|------|-----|---------|-------------|
| id | BIGINT | Nein | Primärschlüssel (auto-generated) |
| username | VARCHAR(255) | Nein | Benutzername (eindeutig) |
| password | VARCHAR(255) | Nein | Passwort (BCrypt gehasht) |
| email | VARCHAR(255) | Ja | E-Mail |
| role | TINYINT | Nein | 0=USER, 1=ADMIN |
| enabled | BOOLEAN | Nein | Aktiviert |
| createdAt | TIMESTAMP | Nein | Erstellungszeitpunkt |
| updatedAt | TIMESTAMP | Nein | Aktualisierungszeitpunkt |

### 5.4 AuditLog (Protokoll)

| Feld | Typ | Nullable | Beschreibung |
|------|-----|---------|-------------|
| id | BIGINT | Nein | Primärschlüssel |
| entityName | VARCHAR(255) | Nein | Entitätsname (Club, Member) |
| entityId | BIGINT | Ja | ID der Entität |
| action | VARCHAR(255) | Nein | CREATE, UPDATE, DELETE, RESTORE |
| oldValue | VARCHAR(1000) | Ja | Alter Wert |
| newValue | VARCHAR(1000) | Ja | Neuer Wert |
| performedBy | VARCHAR(255) | Nein | Ausführender Benutzer |
| performedAt | TIMESTAMP | Nein | Zeitpunkt |
| ipAddress | VARCHAR(255) | Ja | IP-Adresse |

---

## 6. API-Endpunkte

### 6.1 Authentifizierung

| Methode | Pfad | Beschreibung | Auth |
|---------|------|-------------|------|
| POST | /api/auth/register | Benutzer registrieren | Nein |
| POST | /api/auth/login | Login (Token erhalten) | Nein |
| POST | /api/auth/refresh | Token erneuern | Ja |

### 6.2 Clubs

| Methode | Pfad | Beschreibung | Auth |
|---------|------|-------------|------|
| POST | /api/clubs | Verein erstellen | ADMIN |
| GET | /api/clubs/{id} | Verein abrufen | USER/ADMIN |
| GET | /api/clubs | Alle aktiven Vereine | USER/ADMIN |
| GET | /api/clubs/paginated | Paginiert mit Sortierung | USER/ADMIN |
| GET | /api/clubs/search?search=... | Volltextsuche | USER/ADMIN |
| GET | /api/clubs/deleted | Gelöschte Vereine | ADMIN |
| PUT | /api/clubs/{id} | Verein aktualisieren | ADMIN |
| DELETE | /api/clubs/{id} | Soft-Delete | ADMIN |
| PUT | /api/clubs/{id}/restore | Wiederherstellen | ADMIN |

### 6.3 Members

| Methode | Pfad | Beschreibung | Auth |
|---------|------|-------------|------|
| POST | /api/members | Mitglied erstellen | ADMIN |
| GET | /api/members/{id} | Mitglied abrufen | USER/ADMIN |
| GET | /api/members | Alle aktiven Mitglieder | USER/ADMIN |
| GET | /api/members/paginated | Paginiert mit Sortierung | USER/ADMIN |
| GET | /api/members/club/{clubId} | Mitglieder nach Club | USER/ADMIN |
| GET | /api/members/club/{clubId}/paginated | Paginiert nach Club | USER/ADMIN |
| GET | /api/members/search?search=... | Suche nach Name | USER/ADMIN |
| GET | /api/members/status/{status} | Filter nach Status | USER/ADMIN |
| GET | /api/members/type/{type} | Filter nach Typ | USER/ADMIN |
| GET | /api/members/deleted | Gelöschte Mitglieder | ADMIN |
| PUT | /api/members/{id} | Mitglied aktualisieren | ADMIN |
| DELETE | /api/members/{id} | Soft-Delete | ADMIN |
| PUT | /api/members/{id}/restore | Wiederherstellen | ADMIN |

### 6.4 Monitoring

| Methode | Pfad | Beschreibung | Auth |
|---------|------|-------------|------|
| GET | /actuator/health | Health-Check | Nein |
| GET | /actuator/prometheus | Prometheus Metriken | Nein |

### 6.5 Swagger/OpenAPI

| Methode | Pfad | Beschreibung |
|---------|------|-------------|
| GET | /swagger-ui.html | API-Dokumentation |
| GET | /v3/api-docs | OpenAPI JSON |

---

## 7. Sicherheit

### 7.1 Authentifizierung

- **JWT-basierte Authentifizierung** mit Bearer Token
- **Access Token**: 24 Stunden Gültigkeit
- **Refresh Token**: 7 Tage Gültigkeit
- **BCrypt Passwort-Hashing** mit Salt

### 7.2 Autorisierung

| Rolle | Berechtigungen |
|-------|---------------|
| USER | Lesen (Clubs, Members) |
| ADMIN | Vollzugriff (CRUD + Delete + Restore) |

### 7.3 Sicherheitskonfiguration

- RESTful Session-Management (stateless)
- CSRF-Schutz deaktiviert (stateless API)
- JWT-Filter für Request-Validierung
- KONFIGURIERBARE CORS-Richtlinien

---

## 8. Test-Strategie

### 8.1 Test-Typen

| Testtyp | Beschreibung | Framework |
|--------|-------------|----------|
| Unit Tests | Business Logic | JUnit 5 + Mockito |
| Integration Tests | REST API mit TestContainers | JUnit 5 + RestAssured |
| E2E Tests | Full-Stack Tests | JUnit 5 + TestContainers |

### 8.2 Test-Klassen

```
src/test/java/com/verein/
├── service/
│   ├── ClubServiceImplTest.java
│   ├── MemberServiceImplTest.java
│   ├── ClubServicePaginationTest.java
│   ├── MemberServicePaginationTest.java
│   ├── ClubServiceSoftDeleteTest.java
│   └── MemberServiceSoftDeleteTest.java
├── integration/
│   ├── BaseIntegrationTest.java
│   ├── AbstractIntegrationTest.java
│   ├── ClubIntegrationTest.java
│   └── MemberIntegrationTest.java
├── e2e/
│   ├── BaseE2ETest.java
│   ├── AuthE2ETest.java
│   ├── ClubE2ETest.java
│   └── MemberE2ETest.java
├── repository/
│   ├── ClubRepositoryTest.java
│   └── MemberRepositoryTest.java
└── security/
    └── JwtUtilTest.java
```

### 8.3 Test-Ausführung

```powershell
# Alle Tests (except E2E & Integration)
cit 25 test

# Nur Integration Tests
cit 25 test -Dtest=*IntegrationTest

# Nur E2E Tests
cit 25 test -Dtest=*E2ETest
```

---

## 9. Build & Deployment

### 9.1 Build

```powershell
# Mit Tests
cit 25 test

# Ohne Tests (CI/CD)
cit 25

# Nur Kompilierung
cit 25 compile
```

### 9.2 Docker Build

```powershell
# Image bauen
docker build -t verein-verwaltung:latest .

# Container starten
docker run -p 8080:8080 verein-verwaltung:latest
```

### 9.3 Umgebungsvariablen

| Variable | Standard | Beschreibung |
|----------|---------|-------------|
| SERVER_PORT | 8080 | Server-Port |
| DB_HOST | localhost | Datenbank-Host |
| DB_PORT | 5432 | Datenbank-Port |
| DB_NAME | verein_db | Datenbank-Name |
| DB_USER | postgres | Datenbank-Benutzer |
| DB_PASSWORD | - | Datenbank-Passwort |
| JWT_SECRET | - | JWT-Secret |
| JWT_EXPIRATION | 86400000 | Token-Gültigkeit (ms) |

---

## 10. Konfiguration

### 10.1 application.yml

```yaml
server:
  port: ${SERVER_PORT:8080}

spring:
  application:
    name: verein-verwaltung
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:verein_db}
    username: ${DB_USER:postgres}
    password: ${DB_PASSWORD:}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  jackson:
    serialization:
      write-dates-as-timestamps: false

jwt:
  secret: ${JWT_SECRET:}
  expiration: ${JWT_EXPIRATION:86400000}

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html

management:
  endpoints:
    web:
      exposure:
        include: health,prometheus
```

---

## Lizenz

Copyright © 2024. Alle Rechte vorbehalten.

---

## Kontakt

Bei Fragen oder Anregungen wenden Sie sich bitte an das Entwicklungsteam.