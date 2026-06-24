[![asciicast](https://asciinema.org/a/JJ3RGbyHiPM7u117.svg)](https://asciinema.org/a/JJ3RGbyHiPM7u117)
# Онлайн-курсы и обучение

Учебно-прикладной проект по дисциплине "Проектирование и реализация баз данных". Система моделирует платформу онлайн-обучения: преподаватели создают курсы, студенты записываются, оплачивают обучение, проходят уроки, отправляют решения, получают оценки и сертификаты.

Главный акцент проекта: нормализованная PostgreSQL-схема, триггеры, индексы, native SQL-аналитика, MongoDB для гибких данных и простой веб-интерфейс на Thymeleaf.

## Стек

- Java 21
- Spring Boot
- Gradle Kotlin DSL
- Spring Web
- Thymeleaf
- Spring Data JPA
- Spring Data MongoDB
- Jakarta Validation
- PostgreSQL
- MongoDB
- Docker Compose

## Быстрый запуск

```bash
cd /Users/pivpav_20/IdeaProjects/onlinecourses
docker compose up -d postgres mongodb
./gradlew bootRun
```

Приложение будет доступно на:

```text
http://localhost:8080
```

Полная сборка:

```bash
./gradlew clean build
```

## Docker Compose

Основные сервисы:

- PostgreSQL: `localhost:5432`, база `online_courses`, пользователь `postgres`, пароль `postgres`.
- MongoDB: `localhost:27017`, база `online_courses`.

Запуск только БД:

```bash
docker compose up -d postgres mongodb
```

Запуск вместе с UI-инструментами:

```bash
docker compose --profile tools up -d
```

После запуска профиля `tools` доступны:

- pgAdmin: `http://localhost:5050`, `admin@example.com` / `admin`
- mongo-express: `http://localhost:8081`

Остановка:

```bash
docker compose down
```

Полный сброс данных:

```bash
docker compose down -v
docker compose up -d postgres mongodb
```

## Веб-интерфейс

Thymeleaf UI сделан как учебный демонстрационный стенд: на страницах явно показано, какие действия работают через PostgreSQL, какие через MongoDB, а какие используют native SQL.

- `GET /` - главная страница со сводкой проекта.
- `GET /courses` - каталог курсов.
- `GET /courses/{courseId}` - страница курса: модули, уроки, задания, запись, оплата, отзывы.
- `GET /students/{studentId}` - кабинет студента: записи, оплаты, решения, сертификаты.
- `GET /analytics` - аналитические отчеты из PostgreSQL.

Демо-студент:

```text
10000000-0000-0000-0000-000000000001
```

Цены и оплаты отображаются в рублях.

## SQL-инициализация без Flyway

Flyway и Liquibase не используются. SQL-файлы лежат в:

```text
src/main/resources/db/init
```

Файлы:

- `01_schema.sql` - таблицы, PK, FK, UNIQUE, CHECK constraints и индексы.
- `02_triggers.sql` - PostgreSQL trigger functions и triggers.
- `03_seed_data.sql` - тестовые данные.

Spring Boot применяет их при старте через `spring.sql.init` в `application.yml`. Для PL/pgSQL используется separator `^^`, чтобы Spring корректно выполнял функции с внутренними `;`.

Hibernate не создает таблицы:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
```

Проверить таблицы:

```bash
docker exec -it online_courses_postgres psql -U postgres -d online_courses -c "\dt"
```

Проверить триггеры:

```bash
docker exec -it online_courses_postgres psql -U postgres -d online_courses -c "SELECT tgname FROM pg_trigger WHERE NOT tgisinternal;"
```

## PostgreSQL-схема

В PostgreSQL хранится строгая связанная бизнес-модель:

- `users`
- `roles`
- `user_roles`
- `categories`
- `courses`
- `course_modules`
- `lessons`
- `enrollments`
- `payments`
- `lesson_progress`
- `assignments`
- `submissions`
- `grades`
- `certificates`

Схема нормализована: роли вынесены отдельно, связь many-to-many реализована через `user_roles`, структура курса разложена на курсы, модули и уроки, а прогресс, оплаты, решения, оценки и сертификаты вынесены в отдельные таблицы.

Подробное описание реализации и БД:

```text
PROJECT_GUIDE.md
```

## MongoDB

MongoDB используется для гибких данных, где структура может развиваться без изменения основной SQL-схемы:

- `course_reviews` - отзывы по курсам.
- `lesson_comments` - комментарии к урокам и вложенные ответы.
- `user_activity_logs` - логи активности с гибким `metadata`.

Для корректной работы UUID в MongoDB добавлен `MongoConfig` с `UuidRepresentation.STANDARD`.

## Триггеры PostgreSQL

Реализованы trigger functions:

- `set_updated_at()` - обновляет `updated_at` в `users` и `courses`.
- `check_course_price()` - запрещает отрицательную цену курса.
- `create_enrollment_after_successful_payment()` - создает enrollment после оплаты `PAID`.
- `update_enrollment_progress()` - пересчитывает прогресс после завершения урока.
- `create_certificate_when_course_completed()` - создает сертификат при прогрессе 100%.
- `check_grade_score()` - запрещает оценку выше `assignment.max_score`.
- `prevent_delete_published_course()` - запрещает удалять опубликованный курс с записями.

## Индексы

Индексы созданы для часто используемых условий поиска и join-полей:

- `users(email)`
- `courses(category_id)`
- `courses(instructor_id)`
- `course_modules(course_id)`
- `lessons(module_id)`
- `enrollments(student_id)`
- `enrollments(course_id)`
- `payments(student_id)`
- `payments(course_id)`
- `lesson_progress(enrollment_id)`
- `submissions(student_id)`
- `submissions(assignment_id)`
- `certificates(student_id)`
- `certificates(course_id)`

Пример проверки использования индекса:

```bash
docker exec -it online_courses_postgres psql -U postgres -d online_courses -c "EXPLAIN SELECT * FROM enrollments WHERE student_id = '10000000-0000-0000-0000-000000000001';"
```

## REST API

REST API сохранен параллельно с веб-интерфейсом. Базовый префикс:

```text
/api
```

### Быстрый API-гайд для демонстрации

Перед выполнением команд удобно задать переменные:

```bash
export API=http://localhost:8080/api
export STUDENT_ID=10000000-0000-0000-0000-000000000004
export COURSE_ID=30000000-0000-0000-0000-000000000005
export MODULE_ID=40000000-0000-0000-0000-000000000005
export LESSON_ID=50000000-0000-0000-0000-000000000006
```

Основной сценарий, который показан в asciinema:

| Шаг | Ручка | Что демонстрирует |
| --- | --- | --- |
| 1 | `GET /api/courses` | Получение каталога курсов |
| 2 | `GET /api/courses/{courseId}` | Получение карточки конкретного курса |
| 3 | `GET /api/courses/{courseId}/modules` | Структура курса: модули |
| 4 | `GET /api/modules/{moduleId}/lessons` | Структура курса: уроки |
| 5 | `GET /api/students/{studentId}/enrollments` | Текущие записи студента на курсы |
| 6 | `POST /api/payments` | Создание оплаты; при `PAID` PostgreSQL trigger создает `enrollment` |
| 7 | `POST /api/lessons/{lessonId}/complete?studentId=...` | Завершение урока; trigger пересчитывает прогресс |
| 8 | `GET /api/students/{studentId}/certificates` | Проверка сертификатов после завершения курса |
| 9 | `POST /api/courses/{courseId}/reviews` | Создание отзыва в MongoDB |
| 10 | `GET /api/courses/{courseId}/reviews` | Чтение отзывов из MongoDB |
| 11 | `POST /api/lessons/{lessonId}/comments` | Создание комментария к уроку в MongoDB |
| 12 | `GET /api/lessons/{lessonId}/comments` | Чтение комментариев из MongoDB |
| 13 | `POST /api/activity-logs` | Запись пользовательского события в MongoDB |
| 14 | `GET /api/users/{userId}/activity-logs` | Чтение логов активности из MongoDB |
| 15 | `GET /api/analytics/top-courses` | Native SQL-аналитика: популярные курсы |
| 16 | `GET /api/analytics/course-average-progress` | Native SQL-аналитика: средний прогресс |
| 17 | `GET /api/analytics/instructor-income` | Native SQL-аналитика: доход преподавателей |

Минимальный пример команды:

```bash
curl -s "$API/courses" | python3 -m json.tool
```

Если `python3 -m json.tool` недоступен, можно убрать форматирование:

```bash
curl -s "$API/courses"
```

Пример оплаты, после которой срабатывает триггер записи на курс:

```bash
curl -s -X POST "$API/payments" \
  -H "Content-Type: application/json" \
  -d "{
    \"studentId\": \"$STUDENT_ID\",
    \"courseId\": \"$COURSE_ID\",
    \"amount\": 8990.00,
    \"status\": \"PAID\"
  }" | python3 -m json.tool
```

После этого можно проверить, что запись на курс появилась:

```bash
curl -s "$API/students/$STUDENT_ID/enrollments" | python3 -m json.tool
```

### Users

- `GET /api/users`
- `GET /api/users/{id}`
- `POST /api/users`
- `PUT /api/users/{id}`
- `DELETE /api/users/{id}`

Пример:

```bash
curl -X POST http://localhost:8080/api/users \
  -H 'Content-Type: application/json' \
  -d '{"email":"new@student.test","passwordHash":"hash","firstName":"New","lastName":"Student","roles":["STUDENT"]}'
```

### Courses

- `GET /api/courses`
- `GET /api/courses/{id}`
- `GET /api/courses/category/{categoryId}`
- `POST /api/courses`
- `PUT /api/courses/{id}`
- `DELETE /api/courses/{id}`
- `POST /api/courses/{courseId}/publish`

### Modules

- `GET /api/courses/{courseId}/modules`
- `POST /api/courses/{courseId}/modules`
- `PUT /api/modules/{moduleId}`
- `DELETE /api/modules/{moduleId}`

### Lessons

- `GET /api/modules/{moduleId}/lessons`
- `POST /api/modules/{moduleId}/lessons`
- `PUT /api/lessons/{lessonId}`
- `DELETE /api/lessons/{lessonId}`
- `POST /api/lessons/{lessonId}/complete?studentId=...`

Пример завершения урока:

```bash
curl -X POST "http://localhost:8080/api/lessons/50000000-0000-0000-0000-000000000001/complete?studentId=10000000-0000-0000-0000-000000000002"
```

После этого PostgreSQL trigger пересчитает `enrollments.progress_percent`.

### Enrollments

- `POST /api/courses/{courseId}/enroll?studentId=...`
- `GET /api/students/{studentId}/enrollments`
- `GET /api/courses/{courseId}/enrollments`

### Payments

- `POST /api/payments`
- `GET /api/payments/{id}`
- `GET /api/students/{studentId}/payments`

Пример оплаты в рублях:

```bash
curl -X POST http://localhost:8080/api/payments \
  -H 'Content-Type: application/json' \
  -d '{"studentId":"10000000-0000-0000-0000-000000000004","courseId":"30000000-0000-0000-0000-000000000005","amount":8990.00,"status":"PAID"}'
```

Если `status = PAID`, PostgreSQL trigger автоматически создает enrollment.

### Assignments

- `GET /api/lessons/{lessonId}/assignments`
- `POST /api/lessons/{lessonId}/assignments`

### Submissions

- `POST /api/assignments/{assignmentId}/submit?studentId=...`
- `GET /api/submissions/{id}`
- `GET /api/students/{studentId}/submissions`

Пример:

```bash
curl -X POST "http://localhost:8080/api/assignments/60000000-0000-0000-0000-000000000001/submit?studentId=10000000-0000-0000-0000-000000000002" \
  -H 'Content-Type: application/json' \
  -d '{"answerText":"Implemented REST controller and DTO validation."}'
```

### Grades

- `POST /api/submissions/{submissionId}/grade?graderId=...`
- `GET /api/submissions/{submissionId}/grade`

PostgreSQL trigger проверяет, что `score <= assignments.max_score`.

### Certificates

- `GET /api/students/{studentId}/certificates`
- `GET /api/courses/{courseId}/certificates`

### MongoDB API

- `POST /api/courses/{courseId}/reviews`
- `GET /api/courses/{courseId}/reviews`
- `GET /api/courses/{courseId}/reviews/average-rating`
- `POST /api/lessons/{lessonId}/comments`
- `POST /api/comments/{commentId}/replies`
- `GET /api/lessons/{lessonId}/comments`
- `POST /api/activity-logs`
- `GET /api/users/{userId}/activity-logs`

Пример отзыва:

```bash
curl -X POST http://localhost:8080/api/courses/30000000-0000-0000-0000-000000000001/reviews \
  -H 'Content-Type: application/json' \
  -d '{"userId":"10000000-0000-0000-0000-000000000001","rating":5,"text":"Полезный курс по backend-разработке"}'
```

## Аналитика

Все аналитические запросы написаны вручную на SQL в `AnalyticsService` через `JdbcTemplate`.

- `GET /api/analytics/top-courses`
- `GET /api/analytics/course-average-progress`
- `GET /api/analytics/student-average-grades`
- `GET /api/analytics/instructor-income`
- `GET /api/analytics/student-ranking`
- `GET /api/analytics/courses-with-progress-above?value=70`
- `GET /api/analytics/inactive-students`
- `GET /api/analytics/category-statistics`
- `GET /api/analytics/courses-without-payments`
- `GET /api/analytics/monthly-income`

Используются:

- `JOIN`
- `LEFT JOIN`
- `GROUP BY`
- `HAVING`
- агрегатные функции
- подзапросы
- CTE
- оконная функция `DENSE_RANK()`


## Соответствие требованиям курса

- Есть больше 7 связанных таблиц: реализовано 14 PostgreSQL-таблиц.
- Есть сложные SQL-запросы: реализовано 10 native SQL отчетов.
- Есть минимум 6 триггерных функций: реализовано 7 trigger functions.
- Схема приведена к нормальным формам.
- Есть ER/IDEF1X-диаграмма в презентации.
- ORM используется для прикладной логики, аналитика написана вручную на SQL.
- PostgreSQL используется для строгих связанных данных.
- MongoDB используется для отзывов, комментариев и логов.
- Есть REST API и веб-интерфейс Thymeleaf.
