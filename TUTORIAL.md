# Spring Boot 4.0.5 Vereinsverwaltung - Vollständiges Tutorial

## Inhaltsverzeichnis

1. [Einführung](#einführung)
2. [Projektübersicht](#projektübersicht)
3. [Technologie-Stack](#technologie-stack)
4. [Systemarchitektur](#systemarchitektur)
5. [Modul-Abhängigkeiten](#modul-abhängigkeiten)
6. [Projektstruktur](#projektstruktur)
7. [Datenmodell](#datenmodell)
8. [REST-API Endpunkte](#rest-api-endpunkte)
9. [Test-Architektur](#test-architektur)
10. [Build & Deployment](#build--deployment)
11. [Spring Boot 4 Änderungen](#spring-boot-4-änderungen)
12. [Häufige Probleme & Lösungen](#häufige-probleme--lösungen)
13. [Fazit](#fazit)

---

## 1. Einführung

Dieses Tutorial zeigt, wie man eine professionelle Vereinsverwaltungsanwendung mit **Spring Boot 4.0.5** und **Java 25** erstellt. Das Projekt ist ein vollständiges Starter-Template, das alle wesentlichen Aspekte einer modernen Spring Boot-Anwendung abdeckt:

- RESTful API mit PostgreSQL
- Umfassende Test-Abdeckung (Unit, Integration, E2E)
- Jakarta EE Migration (javax → jakarta)
- Docker-Containerisierung
- Moderne Build-Pipeline mit Maven 4

### Was Sie in diesem Tutorial lernen

- Projektstruktur und Architektur verstehen
- Datenmodell mit JPA/Hibernate implementieren
- REST-API mit Spring MVC erstellen
- Tests mit JUnit 5, Mockito und Testcontainers schreiben
- Spring Boot 4 Migration durchführen
- Anwendung containerisieren und deployen

---

## 2. Projektübersicht

Das Projekt "Verein Verwaltung" ist eine Spring Boot-Anwendung zur Verwaltung von Vereinen und deren Mitgliedern.

### Features

| Feature | Beschreibung |
|---------|---------------|
| **Vereinsverwaltung** | CRUD-Operationen für Vereine |
| **Mitgliederverwaltung** | Vollständige Mitgliederverwaltung mit verschiedenen Mitgliedschaftstypen |
| **Membership-Typen** | REGULAR, FAMILY, STUDENT, SENIOR, HONORARY |
| **Membership-Status** | ACTIVE, INACTIVE, SUSPENDED, CANCELLED |
| **Validierung** | Automatische Eingabevalidierung |
| **Fehlerbehandlung** | Zentrale Exception-Handling |
| **REST API** | Vollständige RESTful Schnittstelle |
| **Tests** | Unit, Integration und E2E Tests |

### Projekt-Statistiken

| Metrik | Wert |
|--------|------|
| Java Version | 25 |
| Spring Boot Version | 4.0.5 |
| Maven Version | 4.0.0 |
| Test-Klassen | 6 |
| Test-Methoden | 40 |
| Controller | 2 |
| Services | 4 |
| Repositories | 4 |
| Entities | 2 |

---

## 3. Technologie-Stack

### Core-Technologien

| Komponente | Technologie | Version | Beschreibung |
|------------|-------------|---------|---------------|
| Framework | Spring Boot | 4.0.5 | Das neueste Spring Boot Release |
| Sprache | Java | 25 | Neueste LTS-Version |
| Build-Tool | Maven | 4.0.0 | Unterstützt Variablen im Parent POM |
| Datenbank | PostgreSQL | 15 | Relationale Datenbank |
| ORM | Spring Data JPA / Hibernate | 7.2.7 | Object-Relational Mapping |

### Abhängigkeiten (POM)

```xml
<properties>
    <java.version>25</java.version>
    <spring-boot.version>4.0.5</spring-boot.version>
    <lombok.version>1.18.40</lombok.version>
    <testcontainers.version>1.20.0</testcontainers.version>
    <jackson.version>2.17.1</jackson.version>
    <rest-assured.version>5.5.0</rest-assured.version>
    <maven-surefire-plugin.version>3.2.5</maven-surefire-plugin.version>
</properties>
```

### Starter-Dependencies

| Artifact | Zweck |
|----------|-------|
| spring-boot-starter-webmvc | Web & REST |
| spring-boot-starter-data-jpa | Datenbankzugriff |
| spring-boot-starter-validation | Eingabevalidierung |
| spring-boot-starter-test | Test-Framework |
| spring-boot-starter-webmvc-test | WebMvc Test-Slice |
| spring-boot-starter-data-jpa-test | JPA Test-Slice |

### Externe Dependencies

| Library | Version | Zweck |
|---------|---------|-------|
| PostgreSQL Driver | 42.7.10 | Datenbank-Verbindung |
| Lombok | 1.18.40 | Code-Generierung |
| Testcontainers PostgreSQL | 1.20.0 | E2E Tests mit Docker |
| RestAssured | 5.5.0 | E2E API Testing |
| Jackson Databind | 2.17.1 | JSON Serialisierung |
| H2 Database | 2.4.240 | Integration Tests |

---

## 4. Systemarchitektur

### Architektur-Diagramm

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           CLIENT (REST API)                              │
│                                                                          │
│   ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                │
│   │   Web UI    │    │   Mobile    │    │   curl/    │                │
│   │  (Browser)  │    │     App     │    │   Postman  │                │
│   └──────┬──────┘    └──────┬──────┘    └──────┬──────┘                │
└──────────┼─────────────────┼─────────────────┼─────────────────────────┘
           │                 │                 │
           ▼                 ▼                 ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                        SPRING BOOT APPLICATION                           │
│                                                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                      CONTROLLER LAYER                             │   │
│  │   ┌─────────────────┐        ┌─────────────────┐               │   │
│  │   │ClubController  │        │MemberController │               │   │
│  │   └────────┬────────┘        └────────┬────────┘               │   │
│  └───────────┼──────────────────────────┼────────────────────────┘   │
│              │                           │                            │
│              ▼                           ▼                            │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                      SERVICE LAYER                               │   │
│  │   ┌─────────────────┐        ┌─────────────────┐               │   │
│  │   │ClubService      │        │MemberService   │               │   │
│  │   │ (Interface)     │        │ (Interface)    │               │   │
│  │   └────────┬────────┘        └────────┬────────┘               │   │
│  │            │                         │                         │   │
│  │            ▼                         ▼                         │   │
│  │   ┌─────────────────┐        ┌─────────────────┐               │   │
│  │   │ClubServiceImpl  │        │MemberServiceImpl│               │   │
│  │   └────────┬────────┘        └────────┬────────┘               │   │
│  └───────────┼──────────────────────────┼────────────────────────┘   │
│              │                           │                            │
│              ▼                           ▼                            │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                   REPOSITORY LAYER                               │   │
│  │   ┌─────────────────┐        ┌─────────────────┐               │   │
│  │   │ClubRepository   │        │MemberRepository │               │   │
│  │   │ (JPA)          │        │ (JPA)           │               │   │
│  │   └────────┬────────┘        └────────┬────────┘               │   │
│  └───────────┼──────────────────────────┼────────────────────────┘   │
│              │                           │                            │
│              ▼                           ▼                            │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                      ENTITY LAYER                                │   │
│  │   ┌─────────────────┐        ┌─────────────────┐               │   │
│  │   │      Club      │ 1:N    │     Member     │               │   │
│  │   │                │────────►│                │               │   │
│  │   └─────────────────┘        └─────────────────┘               │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└───────────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                        DATENBANK SCHICHT                               │
│                                                                          │
│   ┌────────────────────────────────────────────────────────────────┐  │
│   │                      POSTGRESQL                                  │  │
│   │                                                                  │  │
│   │   ┌──────────────┐         ┌──────────────┐                    │  │
│   │   │    club     │         │    member    │                    │  │
│   │   │─────────────│ 1     N │──────────────│                    │  │
│   │   │ id (PK)     │────────►│ id (PK)      │                    │  │
│   │   │ name        │         │ club_id (FK) │                    │  │
│   │   │ description │         │ first_name   │                    │  │
│   │   │ founded_date│         │ last_name    │                    │  │
│   │   │ city        │         │ email        │                    │  │
│   │   └──────────────┘         │ phone_number │                    │  │
│   │                              │ birth_date   │                    │  │
│   │                              │ membership_  │                    │  │
│   │                              │   type      │                    │  │
│   │                              │ status      │                    │  │
│   │                              └──────────────┘                    │  │
│   └────────────────────────────────────────────────────────────────┘  │
└───────────────────────────────────────────────────────────────────────┘
```

### Schichten-Architektur

```
┌─────────────────────────────────────────┐
│           Presentation Layer            │
│  (Controller - REST API)                 │
├─────────────────────────────────────────┤
│            Service Layer                 │
│  (Business Logic)                       │
├─────────────────────────────────────────┤
│           Repository Layer              │
│  (Data Access)                          │
├─────────────────────────────────────────┤
│             Entity Layer                │
│  (Domain Models)                        │
├─────────────────────────────────────────┤
│           Database Layer                │
│  (PostgreSQL)                           │
└─────────────────────────────────────────┘
```

---

## 5. Modul-Abhängigkeiten

### Abhängigkeits-Diagramm

```
┌──────────────────────────────────────────────────────────────────────────┐
│                        SPRING BOOT STARTER PARENT                         │
│                               (4.0.5)                                     │
└─────────────────────────────────────┬────────────────────────────────────┘
                                      │
         ┌────────────────────────────┼────────────────────────────┐
         │                            │                            │
         ▼                            ▼                            ▼
┌──────────────────────┐   ┌──────────────────────┐   ┌──────────────────────┐
│ spring-boot-starter │   │   spring-boot-      │   │   spring-boot-       │
│      -webmvc        │   │   starter-data-jpa  │   │   starter-validation │
│                      │   │                      │   │                      │
│ ┌──────────────────┐ │   │ ┌──────────────────┐ │   │ ┌──────────────────┐ │
│ │ spring-web       │ │   │ │ spring-data-jpa │ │   │ │ hibernate-       │ │
│ │ spring-webmvc    │ │   │ │ hibernate-core   │ │   │ │ validator        │ │
│ │ tomcat-embed    │ │   │ │ spring-data-commons│ │   │ │ jakarta.validation│ │
│ └──────────────────┘ │   │ └──────────────────┘ │   │ └──────────────────┘ │
└──────────────────────┘   └──────────────────────┘   └──────────────────────┘
         │                            │                            │
         └────────────────────────────┼────────────────────────────┘
                                      │
                                      ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                         EXTERNE DEPENDENCIES                             │
│                                                                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │
│  │ PostgreSQL │  │   Lombok    │  │   Jackson   │  │  Testcontainers│   │
│  │   Driver    │  │  1.18.40   │  │  2.17.1    │  │   1.20.0    │     │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘     │
└──────────────────────────────────────────────────────────────────────────┘
```

### POM Abhängigkeits-Baum

```xml
dependencies
├── spring-boot-starter-parent:4.0.5
│   └── spring-boot-dependencies:4.0.5
│
├── spring-boot-starter-webmvc
│   ├── spring-boot-starter
│   │   └── spring-boot-autoconfigure
│   │       └── spring-boot
│   ├── spring-web
│   ├── spring-webmvc
│   └── tomcat-embed
│
├── spring-boot-starter-data-jpa
│   ├── spring-data-jpa
│   ├── spring-data-commons
│   ├── hibernate-core:7.2.7
│   ├── spring-orm
│   └── spring-tx
│
├── spring-boot-starter-validation
│   ├── hibernate-validator
│   └── jakarta.validation-api
│
├── postgresql (runtime)
│
├── lombok (optional)
│
├── jackson-databind:2.17.1
├── jackson-datatype-jsr310:2.17.1
│
├── spring-boot-starter-test
│   ├── junit-jupiter:6.0.3
│   ├── mockito-core:5.20.0
│   ├── assertj-core
│   └── spring-test
│
├── spring-boot-starter-webmvc-test (test)
├── spring-boot-starter-data-jpa-test (test)
│
├── testcontainers-postgresql:1.20.0 (test)
├── testcontainers-junit-jupiter:1.20.0 (test)
│
├── h2 (test - runtime)
│
└── rest-assured:5.5.0 (test)
```

### Modul-Beziehungen (Klassen-Diagramm)

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           CONTROLLER                                    │
│                                                                          │
│   ClubController         MemberController                              │
│   ─────────────────       ──────────────────                            │
│   - clubService          - memberService          @Autowired            │
│   ────────────────       ───────────────────      ─────────────────    │
│   + createClub()         + createMember()       depends on            │
│   + getAllClubs()        + getAllMembers()            │               │
│   + getClubById()       + getMemberById()            ▼               │
│   + updateClub()        + updateMember()       ┌─────────────────┐    │
│   + deleteClub()        + deleteMember()       │    SERVICE      │    │
│                         + getMembersByClub()  └─────────────────┘    │
└─────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                            SERVICE                                      │
│                                                                          │
│   ClubService            ClubServiceImpl        MemberService          │
│   ────────────           ───────────────        ──────────────         │
│   (Interface)            (Implementation)       (Interface)            │
│   ────────────           ───────────────        ──────────────         │
│   + create()             + create()             + create()             │
│   + findAll()           + findAll()            + findAll()            │
│   + findById()         + findById()           + findById()           │
│   + update()           + update()             + update()             │
│   + delete()           + delete()             + delete()             │
│                         ───────────────        + findByClubId()       │
│                        depends on                   │                │
│                        ┌─────────────┐            ▼                 │
│                        │ REPOSITORY  │      ┌─────────────┐          │
│                        └─────────────┘      │   SERVICE  │          │
│                                              │  IMPLEMENT │          │
│   MemberServiceImpl                           └─────────────┘          │
│   ─────────────────                                                            │
│   + create()                                                             │
│   + findAll()                              depends on                  │
│   + findById()                            ┌─────────────┐               │
│   + update()                              │ REPOSITORY  │               │
│   + delete()                              └─────────────┘               │
│   + findByClubId()                                                     │
└─────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                           REPOSITORY                                     │
│                                                                          │
│   ClubRepository             MemberRepository                           │
│   ───────────────           ─────────────────                           │
│   + findAll()               + findAll()                                │
│   + findById()              + findById()                               │
│   + findByName()            + findByEmail()                             │
│   + existsByName()          + findByClubId()                           │
│                            + findByStatus()                            │
│   @Repository               @Repository                                 │
│   extends JpaRepository    extends JpaRepository                        │
└─────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                             ENTITY                                       │
│                                                                          │
│   Club                              Member                              │
│   ────                              ──────                               │
│   - id: Long                        - id: Long                         │
│   - name: String                    - firstName: String                │
│   - description: String            - lastName: String                 │
│   - foundedDate: LocalDate         - email: String                    │
│   - city: String                   - phoneNumber: String              │
│   - members: List<Member>          - birthDate: LocalDate             │
│   ───────────────                  - gender: String                   │
│   @Entity                          - membershipDate: LocalDate         │
│   @Id                              - membershipType: MembershipType    │
│   @GeneratedValue                  - status: MembershipStatus        │
│   @OneToMany(mappedBy)             - club: Club                       │
│   + getters/setters                ──────────────────                 │
│   + builder                        @Entity                             │
│                                    @Id                                 │
│                                    @GeneratedValue                     │
│                                    @ManyToOne                          │
│                                    + getters/setters                   │
│                                    + builder                           │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 6. Projektstruktur

### Verzeichnis-Struktur

```
verein-verwaltung/
├── src/
│   ├── main/
│   │   ├── java/com/verein/
│   │   │   ├── VereinVerwaltungApplication.java
│   │   │   │
│   │   │   ├── config/
│   │   │   │   └── (Future: Security, etc.)
│   │   │   │
│   │   │   ├── controller/
│   │   │   │   ├── ClubController.java
│   │   │   │   └── MemberController.java
│   │   │   │
│   │   │   ├── dto/
│   │   │   │   ├── ClubRequest.java
│   │   │   │   ├── ClubResponse.java
│   │   │   │   ├── MemberRequest.java
│   │   │   │   └── MemberResponse.java
│   │   │   │
│   │   │   ├── entity/
│   │   │   │   ├── Club.java
│   │   │   │   ├── Member.java
│   │   │   │   ├── MembershipType.java
│   │   │   │   └── MembershipStatus.java
│   │   │   │
│   │   │   ├── repository/
│   │   │   │   ├── ClubRepository.java
│   │   │   │   └── MemberRepository.java
│   │   │   │
│   │   │   └── service/
│   │   │       ├── ClubService.java
│   │   │       ├── ClubServiceImpl.java
│   │   │       ├── MemberService.java
│   │   │       └── MemberServiceImpl.java
│   │   │
│   │   └── resources/
│   │       └── application.yml
│   │
│   └── test/
│       ├── java/com/verein/
│       │   ├── controller/
│       │   │   ├── ClubControllerTest.java
│       │   │   └── MemberControllerTest.java
│       │   │
│       │   ├── repository/
│       │   │   ├── ClubRepositoryTest.java
│       │   │   └── MemberRepositoryTest.java
│       │   │
│       │   ├── service/
│       │   │   ├── ClubServiceImplTest.java
│       │   │   └── MemberServiceImplTest.java
│       │   │
│       │   └── e2e/
│       │       └── (Future: E2E Tests)
│       │
│       └── resources/
│           └── application.yml
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

### Datei-Beschreibungen

| Datei | Beschreibung |
|-------|---------------|
| `pom.xml` | Maven Konfiguration mit allen Abhängigkeiten |
| `VereinVerwaltungApplication.java` | Spring Boot Hauptklasse |
| `Club.java` | Club Entity mit JPA Annotations |
| `Member.java` | Member Entity mit Beziehungen |
| `ClubController.java` | REST Controller für Clubs |
| `MemberController.java` | REST Controller für Members |
| `ClubService.java` | Club Service Interface |
| `ClubServiceImpl.java` | Club Service Implementierung |
| `ClubRepository.java` | JPA Repository für Clubs |
| `ClubControllerTest.java` | Controller Test mit MockMvc |
| `ClubRepositoryTest.java` | Integration Test mit H2 |

---

## 7. Datenmodell

### Entity-Beziehungen

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          DATENMODELL                                    │
│                                                                          │
│   ┌──────────────────┐         1        N  ┌──────────────────┐    │
│   │       Club       │◄──────────────────────►│      Member      │    │
│   │                  │         N       ─────►│                  │    │
│   │ ─────────────────│                       │ ─────────────────│    │
│   │ id (PK)      Long │                       │ id (PK)      Long │    │
│   │ name         String│                       │ firstName    String│    │
│   │ description  String│                       │ lastName     String│    │
│   │ foundedDate  Date  │                       │ email        String│    │
│   │ city         String│                       │ phoneNumber  String│    │
│   │ createdAt    Date  │                       │ birthDate    Date  │    │
│   │ updatedAt    Date  │                       │ gender       String│    │
│   │                  │                       │ membershipDate Date │    │
│   │ [members]     List│                       │ membershipType Enum│    │
│   └──────────────────┘                       │ status       Enum  │    │
│                                             │ clubId (FK)   Long  │    │
│                                             │ createdAt     Date  │    │
│                                             │ updatedAt     Date  │    │
│                                             └──────────────────────┘    │
└─────────────────────────────────────────────────────────────────────────┘
```

### ER-Diagramm (PostgreSQL)

```sql
-- Club Table
CREATE TABLE club (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    founded_date DATE,
    city VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Member Table
CREATE TABLE member (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(50),
    birth_date DATE,
    gender VARCHAR(10),
    membership_date DATE NOT NULL,
    membership_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    club_id BIGINT REFERENCES club(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_member_club_id ON member(club_id);
CREATE INDEX idx_member_email ON member(email);
CREATE INDEX idx_member_status ON member(status);
```

### Enum-Typen

#### MembershipType

| Wert | Beschreibung |
|------|---------------|
| REGULAR | Normale Mitgliedschaft |
| FAMILY | Familienmitgliedschaft |
| STUDENT | Studentenmitgliedschaft |
| SENIOR | Seniorenmitgliedschaft |
| HONORARY | Ehrenmitgliedschaft |

#### MembershipStatus

| Wert | Beschreibung |
|------|---------------|
| ACTIVE | Aktives Mitglied |
| INACTIVE | Inaktives Mitglied |
| SUSPENDED | Gesperrtes Mitglied |
| CANCELLED | Abgebrochene Mitgliedschaft |

---

## 8. REST-API Endpunkte

### API Übersicht

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        REST API ENDPOINTS                                │
│                                                                          │
│  BASE URL: http://localhost:8080/api                                    │
│                                                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                     CLUBS                                        │   │
│  │  POST     /clubs              → Create new club                │   │
│  │  GET      /clubs              → Get all clubs                  │   │
│  │  GET      /clubs/{id}         → Get club by ID                 │   │
│  │  PUT      /clubs/{id}         → Update club                    │   │
│  │  DELETE   /clubs/{id}         → Delete club                     │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                     MEMBERS                                      │   │
│  │  POST     /members             → Create new member             │   │
│  │  GET      /members             → Get all members               │   │
│  │  GET      /members/{id}        → Get member by ID              │   │
│  │  GET      /members/club/{id}   → Get members by club           │   │
│  │  PUT      /members/{id}        → Update member                  │   │
│  │  DELETE   /members/{id}        → Delete member                  │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────┘
```

### Detailierte Endpunkte

#### Clubs API

| Methode | Pfad | Status | Beschreibung |
|---------|------|--------|--------------|
| POST | `/api/clubs` | 201 | Neuen Verein erstellen |
| GET | `/api/clubs` | 200 | Alle Vereine abrufen |
| GET | `/api/clubs/{id}` | 200 | Verein nach ID abrufen |
| PUT | `/api/clubs/{id}` | 200 | Verein aktualisieren |
| DELETE | `/api/clubs/{id}` | 204 | Verein löschen |

#### Members API

| Methode | Pfad | Status | Beschreibung |
|---------|------|--------|--------------|
| POST | `/api/members` | 201 | Neues Mitglied erstellen |
| GET | `/api/members` | 200 | Alle Mitglieder abrufen |
| GET | `/api/members/{id}` | 200 | Mitglied nach ID abrufen |
| GET | `/api/members/club/{clubId}` | 200 | Mitglieder nach Verein |
| PUT | `/api/members/{id}` | 200 | Mitglied aktualisieren |
| DELETE | `/api/members/{id}` | 204 | Mitglied löschen |

### Request/Response Beispiele

#### POST /api/clubs

**Request:**
```json
{
    "name": "FC Berlin",
    "description": "Sportverein Berlin",
    "foundedDate": "2000-01-01",
    "city": "Berlin"
}
```

**Response (201):**
```json
{
    "id": 1,
    "name": "FC Berlin",
    "description": "Sportverein Berlin",
    "foundedDate": "2000-01-01",
    "city": "Berlin",
    "createdAt": "2026-04-10T12:00:00",
    "updatedAt": "2026-04-10T12:00:00"
}
```

#### POST /api/members

**Request:**
```json
{
    "firstName": "Max",
    "lastName": "Mustermann",
    "email": "max@example.com",
    "phoneNumber": "+49 123 456789",
    "birthDate": "1990-05-15",
    "gender": "M",
    "membershipType": "REGULAR",
    "status": "ACTIVE",
    "clubId": 1
}
```

**Response (201):**
```json
{
    "id": 1,
    "firstName": "Max",
    "lastName": "Mustermann",
    "email": "max@example.com",
    "phoneNumber": "+49 123 456789",
    "birthDate": "1990-05-15",
    "gender": "M",
    "membershipDate": "2026-04-10",
    "membershipType": "REGULAR",
    "status": "ACTIVE",
    "clubId": 1,
    "createdAt": "2026-04-10T12:00:00",
    "updatedAt": "2026-04-10T12:00:00"
}
```

---

## 9. Test-Architektur

### Test-Pyramide

```
                    ▲
                   /│\
                  / │ \
                 /  │  \
                /   │   \
               /────┴────\
              /   E2E    \
             /   Tests   \
            /─────────────\
           /               \
          /  Integration   \
         /   Tests          \
        /─────────────────────\
       /                       \
      /    Unit Tests          \
     /    (Service Tests)       \
    /─────────────────────────────\
```

### Test-Typen Übersicht

| Typ | Framework | Scope | Ausführung |
|-----|-----------|-------|------------|
| **Unit Tests** | JUnit 5 + Mockito | Service Layer | `mvn test` |
| **Integration Tests** | JUnit 5 + H2 | Repository Layer | `mvn test` |
| **Controller Tests** | MockMvc + @WebMvcTest | Controller Layer | `mvn test` |
| **E2E Tests** | Testcontainers + RestAssured | Full Stack | `mvn verify` |

### Test-Klassen Struktur

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        TEST CLASSES                                     │
│                                                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                   CONTROLLER TESTS                              │   │
│  │                                                                 │   │
│  │  ClubControllerTest                                            │   │
│  │  ──────────────────                                            │   │
│  │  @WebMvcTest(ClubController.class)                             │   │
│  │  - mockMvc: MockMvc                                            │   │
│  │  - clubService: @MockitoBean                                   │   │
│  │                                                                 │   │
│  │  Tests:                                                        │   │
│  │  - createClub_Success()                                        │   │
│  │  - createClub_ValidationError()                                │   │
│  │  - getAllClubs_Success()                                       │   │
│  │  - getClubById_NotFound()                                      │   │
│  │  - updateClub_Success()                                        │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                  REPOSITORY TESTS                               │   │
│  │                                                                 │   │
│  │  ClubRepositoryTest                                            │   │
│  │  ──────────────────                                            │   │
│  │  @DataJpaTest                                                  │   │
│  │  - entityManager: EntityManager (SB4)                          │   │
│  │  - clubRepository: ClubRepository                              │   │
│  │                                                                 │   │
│  │  Tests:                                                        │   │
│  │  - save_Success()                                              │   │
│  │  - findById_Success()                                          │   │
│  │  - findAll_Success()                                           │   │
│  │  - delete_Success()                                            │   │
│  │  - findByName_Success()                                         │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                   SERVICE TESTS                                 │   │
│  │                                                                 │   │
│  │  ClubServiceImplTest                                           │   │
│  │  ───────────────────                                            │   │
│  │  @ExtendWith(MockitoExtension.class)                            │   │
│  │  - clubRepository: @Mock                                       │   │
│  │                                                                 │   │
│  │  Tests:                                                        │   │
│  │  - create_Success()                                            │   │
│  │  - create_ValidationError()                                   │   │
│  │  - findById_Success()                                          │   │
│  │  - findById_NotFound()                                         │   │
│  │  - update_Success()                                            │   │
│  │  - delete_Success()                                            │   │
│  │  - delete_NotFound()                                           │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────┘
```

### Spring Boot 4 Test-Annotationen

| Spring Boot 3 | Spring Boot 4 | Bemerkung |
|---------------|---------------|-----------|
| `@MockBean` | `@MockitoBean` | Neu in SB4 |
| `@SpyBean` | `@MockitoSpyBean` | Neu in SB4 |
| `@DataJpaTest` | `@DataJpaTest` | Bleibt gleich |
| `@WebMvcTest` | `@WebMvcTest` | Bleibt gleich |
| `TestEntityManager` | `EntityManager` | Entfernt in SB4 |

### Test-Ausführung

```bash
# Alle Tests ausführen (außer E2E)
mvn test

# Nur Controller Tests
mvn test -Dtest="com.verein.controller.*Test"

# Nur Service Tests
mvn test -Dtest="com.verein.service.*Test"

# Nur Repository Tests
mvn test -Dtest="com.verein.repository.*Test"

# E2E Tests (erfordert Docker)
mvn verify -Dtest="com.verein.e2e.*Test"
```

### Testergebnisse

```
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0 -- in ClubControllerTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0 -- in MemberControllerTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0 -- in ClubRepositoryTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0 -- in MemberRepositoryTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0 -- in ClubServiceImplTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0 -- in MemberServiceImplTest
[INFO] Tests run: 40, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 10. Build & Deployment

### Build-Skripte

Das Projekt enthält `ci.cmd` und `cit.cmd` für einfaches Kompilieren:

| Befehl | JDK | Maven | Tests | Beschreibung |
|--------|-----|-------|-------|---------------|
| `ci 25` | 25 | 4.0.0 | Nein | Kompiliert ohne Tests |
| `cit 25` | 25 | 4.0.0 | Ja | Kompiliert mit Tests |
| `ci 21` | 21 | 4.0.0 | Nein | Kompiliert ohne Tests |
| `cit 21` | 21 | 4.0.0 | Ja | Kompiliert mit Tests |

### Manuelle Build-Befehle

```bash
# Kompilieren ohne Tests
mvn clean compile -DskipTests

# Kompilieren mit Tests
mvn clean test

# JAR erstellen
mvn clean package

# JAR mit allen Dependencies
mvn clean package -DskipTests

# Spring Boot starten
mvn spring-boot:run
```

### Docker Deployment

#### docker-compose.yml

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/verein_db
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=postgres
    depends_on:
      - postgres

  postgres:
    image: postgres:15
    environment:
      - POSTGRES_DB=verein_db
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
    ports:
      - "5434:5432"
```

#### Container starten

```bash
# Starten
docker-compose up -d

# Status prüfen
docker ps

# Logs anzeigen
docker logs verein-app

# Stoppen
docker-compose down
```

---

## 11. Spring Boot 4 Änderungen

### Breaking Changes Übersicht

| Änderung | Spring Boot 3 | Spring Boot 4 |
|----------|---------------|---------------|
| **Namespace** | javax.* | jakarta.* |
| **Mock Beans** | @MockBean | @MockitoBean |
| **Spy Beans** | @SpyBean | @MockitoSpyBean |
| **TestEntityManager** | TestEntityManager | EntityManager |
| **Test Starter** | Combined | Modular |

### Detailierte Änderungen

#### 1. Jakarta EE Migration

```java
// Spring Boot 3
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Email;

// Spring Boot 4
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
```

#### 2. Mockito Beans

```java
// Spring Boot 3
@MockBean
private MemberService memberService;

// Spring Boot 4
@MockitoBean
private MemberService memberService;
```

#### 3. TestEntityManager

```java
// Spring Boot 3
@Autowired
private TestEntityManager entityManager;

// Spring Boot 4
@Autowired
private EntityManager entityManager;
```

#### 4. Modulare Test Starter

```xml
<!-- Spring Boot 3 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
</dependency>

<!-- Spring Boot 4 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc-test</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa-test</artifactId>
</dependency>
```

### Java Version Anforderungen

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    JAVA VERSION ANFORDERUNGEN                           │
│                                                                          │
│   Spring Boot 3.x  ──────────────────►  Java 17+                     │
│   Spring Boot 4.x  ──────────────────►  Java 21+ (empfohlen: 25)       │
│                                                                          │
│   Unterstützte JDK Versionen:                                           │
│   ──────────────────────────────                                        │
│   ✓ JDK 21 (LTS)                                                        │
│   ✓ JDK 24                                                              │
│   ✓ JDK 25 (empfohlen)                                                  │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 12. Häufige Probleme & Lösungen

### Problem 1: Parent POM Version nicht aufgelöst

**Fehler:**
```
Non-resolvable parent POM: Could not find artifact 
org.springframework.boot:spring-boot-starter-parent:pom:${spring-boot.version}
```

**Lösung:**
- Maven 4.0.0+ verwenden (mit ci/cit.cmd)
- Properties vor dem parent-Tag definieren

### Problem 2: @MockBean nicht gefunden

**Fehler:**
```
The annotation @MockBean is not found
```

**Lösung:**
```java
// Spring Boot 4
import org.springframework.test.context.bean.override.mockito.MockitoBean;

// Verwenden
@MockitoBean
private MemberService memberService;
```

### Problem 3: TestEntityManager nicht verfügbar

**Fehler:**
```
Cannot find symbol: class TestEntityManager
```

**Lösung:**
```java
// Spring Boot 4
@Autowired
private EntityManager entityManager;
```

### Problem 4: javax nicht gefunden

**Fehler:**
```
Package javax.persistence not found
```

**Lösung:**
```java
// Alle javax.* durch jakarta.* ersetzen
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
```

---

## 13. Fazit

Dieses Tutorial hat die wichtigsten Aspekte einer Spring Boot 4.0.5 Anwendung gezeigt:

### Zusammenfassung

| Aspekt | Details |
|--------|---------|
| **Framework** | Spring Boot 4.0.5 |
| **Java** | JDK 25 |
| **Build** | Maven 4.0.0 |
| **Datenbank** | PostgreSQL |
| **Tests** | 40 Tests, alle bestanden |
| **API** | RESTful mit vollständigem CRUD |

### Nächste Schritte

1. **Security hinzufügen** - Spring Security für Authentifizierung
2. **E2E Tests erweitern** - Mehr Testcontainers Szenarien
3. **API Dokumentation** - OpenAPI/Swagger integrieren
4. **CI/CD Pipeline** - GitHub Actions konfigurieren

### Ressourcen

- [Spring Boot 4.0 Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Testcontainers](https://www.testcontainers.org/)
- [Project Lombok](https://projectlombok.org/)

---

**Autor:** Kemal Cavdar  
**Version:** 1.0.0  
**Datum:** April 2026

Dieses Tutorial darf für eigene Projekte verwendet werden.