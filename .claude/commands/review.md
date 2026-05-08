---
description: Spring Boot / Java code review with best-practice checklist
allowed-tools: Read Bash(grep *) Bash(find *) Bash(git diff *) Bash(git log *) Bash(wc *)
---

## Context

Review the changes on the current branch against best practices for Spring Boot and Java.

Scope: $ARGUMENTS

If no scope is provided, review all changed files on the current branch compared to `main`.

## Gather Changes

!`git diff --name-only main...HEAD 2>/dev/null || git diff --name-only HEAD~5..HEAD 2>/dev/null || echo "no diff available"`

Read every changed file in full before starting the review. Do not skip files.

## Review Checklist

Evaluate every changed file against the following checklist. Only report items that are actually violated — do not list passing checks. For each finding, cite the file path, line number, the violated rule, and a concrete fix.

### 1. @Transactional Correctness

- [ ] `@Transactional` is not placed on `private`, `protected`, or package-private methods (Spring proxies only intercept public methods — the annotation is silently ignored otherwise)
- [ ] No self-invocation of `@Transactional` methods within the same class (`this.method()` bypasses the proxy; extract to a separate bean or use `TransactionTemplate`)
- [ ] Read-only operations use `@Transactional(readOnly = true)` to enable Hibernate flush-mode optimizations
- [ ] `rollbackFor` is set explicitly when checked exceptions are thrown (`@Transactional` only rolls back on unchecked exceptions by default)
- [ ] Transaction boundaries are not too broad — long-running transactions hold DB connections and locks
- [ ] `@Transactional` is on service methods, not on repository or controller methods
- [ ] No transaction-sensitive work (HTTP calls, messaging) inside a transaction that would hold a connection open during I/O

### 2. JPA / Hibernate

- [ ] No N+1 query problems: collections accessed in loops should use `JOIN FETCH`, `@EntityGraph`, or `@BatchSize`
- [ ] `FetchType.EAGER` is not used on `@OneToMany` or `@ManyToMany` (always prefer `LAZY`)
- [ ] Bidirectional relationships have a proper owning side (`mappedBy`) and both sides are kept in sync
- [ ] `equals()` and `hashCode()` on entities use business key or ID — never rely on Lombok `@Data` which uses all fields
- [ ] Entity classes are not `final` (breaks Hibernate proxy generation)
- [ ] `@GeneratedValue(strategy = IDENTITY)` is not used with batch inserts (prevents JDBC batching; prefer `SEQUENCE`)
- [ ] No `CascadeType.REMOVE` or `CascadeType.ALL` on large collections (deletes one-by-one; use bulk `DELETE` queries)
- [ ] `@Version` is considered for optimistic locking on concurrently-modified entities
- [ ] `Optional<>` is used for nullable query results in repositories, not null returns
- [ ] Avoid `SELECT *` via projections or DTOs for read-heavy queries that don't need full entities
- [ ] No open-session-in-view anti-pattern for production code (check `spring.jpa.open-in-view=false`)
- [ ] Queries that return large result sets use pagination (`Pageable`) instead of unbounded `findAll()`

### 3. REST API Design

- [ ] `@Valid` or `@Validated` is present on every `@RequestBody` and validated `@PathVariable`/`@RequestParam`
- [ ] Controllers return DTOs/records, never JPA entities (prevents lazy-loading exceptions, circular references, and data leaks)
- [ ] Correct HTTP status codes: 201 for creation, 204 for no-content, 404 for not-found — not 200 for everything
- [ ] `POST` for creation, `PUT`/`PATCH` for updates, `DELETE` for deletion — verbs match semantics
- [ ] No business logic in controllers — controllers delegate to services
- [ ] Path variables and request params are validated (`@Min`, `@NotBlank`, `@Pattern`, etc.)
- [ ] Collection endpoints support pagination (`Pageable`) and do not return unbounded lists
- [ ] API error responses use a consistent structure (e.g., `ErrorResponse` DTO with timestamp, status, message)
- [ ] `@RestControllerAdvice` handles exceptions globally — no `try/catch` blocks in controllers returning error DTOs
- [ ] Sensitive data (passwords, tokens, internal IDs) is not exposed in responses
- [ ] Date/time fields use ISO-8601 format via `@JsonFormat` or Jackson configuration

### 4. Dependency Injection & Bean Design

- [ ] Constructor injection is used, not `@Autowired` on fields (immutability, testability, explicit dependencies)
- [ ] No circular dependencies — if present, refactor rather than using `@Lazy`
- [ ] Prototype-scoped beans are not injected into singleton beans without `ObjectProvider` or `@Lookup`
- [ ] `@Service`, `@Repository`, `@Component` are used appropriately — not interchangeably
- [ ] Configuration values use `@ConfigurationProperties` with validation over scattered `@Value` annotations
- [ ] No `static` mutable state in Spring beans (singletons are shared across requests)

### 5. Security (OWASP Top 10)

- [ ] No SQL/JPQL injection: all queries use parameterized bind variables (`:param`), never string concatenation
- [ ] User input is validated and sanitized before use — especially in `@PathVariable`, `@RequestParam`, headers
- [ ] No sensitive data (passwords, API keys, secrets) is hardcoded in source — must come from env/config/vault
- [ ] Actuator endpoints are secured or disabled in production (`management.endpoints.web.exposure.include` is minimal)
- [ ] CORS is explicitly configured — no `@CrossOrigin("*")` in production
- [ ] Logging does not include sensitive data (PII, credentials, tokens, full stack traces of handled exceptions)
- [ ] File upload endpoints validate file type, size, and name (path traversal prevention)
- [ ] Mass assignment is prevented: DTOs explicitly declare accepted fields rather than binding directly to entities

### 6. Exception Handling

- [ ] Exceptions carry domain meaning (`InsufficientFundsException`) not generic ones (`RuntimeException("not enough")`)
- [ ] `@RestControllerAdvice` maps exceptions to appropriate HTTP status codes
- [ ] Exceptions are not silently swallowed — empty `catch` blocks are flagged
- [ ] Stack traces are not returned to API callers in production (log internally, return user-friendly message)
- [ ] `Optional` is used instead of returning null from service methods for "not found" scenarios

### 7. Performance

- [ ] No `@Transactional` methods calling external HTTP APIs (holds DB connection during network I/O)
- [ ] Database queries inside loops are replaced with batch operations (`IN` clause, `findAllById`, `saveAll`)
- [ ] Heavy computations or I/O-bound work use `@Async` or are offloaded appropriately
- [ ] Connection pool is configured (`spring.datasource.hikari.*`), not using defaults for production
- [ ] Response compression is enabled for large payloads (`server.compression.enabled=true`)
- [ ] Appropriate caching (`@Cacheable`) is used for expensive, idempotent reads
- [ ] No unnecessary `findById` followed by `save` when `@Modifying` `@Query` suffices for bulk updates

### 8. Testing

- [ ] `@WebMvcTest` is used for controller tests (not `@SpringBootTest` which loads entire context)
- [ ] `@DataJpaTest` is used for repository tests with an in-memory database
- [ ] Service tests use `@ExtendWith(MockitoExtension.class)` with `@Mock`/`@InjectMocks`, not `@SpringBootTest`
- [ ] Tests verify behavior, not implementation — no asserting on mock call counts unless side effects matter
- [ ] Edge cases are covered: empty collections, null inputs, boundary values, concurrent scenarios
- [ ] No `@SpringBootTest` unless integration-testing the full stack is actually intended
- [ ] Test data is self-contained — tests don't depend on shared mutable state or execution order
- [ ] `@Transactional` on test classes is intentional — it auto-rolls back, which can mask flush/constraint bugs

### 9. Configuration & Profiles

- [ ] Secrets and environment-specific values use Spring profiles or environment variables, not hardcoded strings
- [ ] `application.properties`/`application.yml` does not contain production credentials
- [ ] Database DDL mode is `validate` or `none` in production — never `create`, `create-drop`, or `update`
- [ ] Logging levels are appropriate: not `DEBUG` in production, not `ERROR`-only in development
- [ ] Health check and readiness probes are configured for deployment platforms

### 10. Code Quality & Java Best Practices

- [ ] Records are used for immutable DTOs (Java 16+)
- [ ] `var` usage is clear — only when the type is obvious from the right-hand side
- [ ] Stream operations are readable and not deeply nested — extract to named methods if complex
- [ ] No raw types (`List` instead of `List<String>`)
- [ ] `BigDecimal` is used for monetary values, not `double` or `float`
- [ ] `Optional` is not used as a method parameter or field type — only as a return type
- [ ] No mutable static fields in any class
- [ ] String concatenation in loops uses `StringBuilder`
- [ ] Collections are returned as unmodifiable where mutation is not intended

## Output Format

Structure your review as:

### Summary
One paragraph: overall assessment, architecture quality, and risk level (low / medium / high).

### Critical Issues
Issues that will cause bugs, data loss, security vulnerabilities, or production failures. Each item:
- **[Category]** `file:line` — Description of the issue and concrete fix.

### Improvements
Issues that affect maintainability, performance, or best-practice adherence but won't cause immediate failures. Each item:
- **[Category]** `file:line` — Description and suggested change.

### Positive Observations
Briefly note 2-3 things done well (good patterns, clean design, thorough tests).

If no issues are found in a severity tier, omit that section. Do not pad the review with non-issues.
