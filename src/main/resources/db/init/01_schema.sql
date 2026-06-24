CREATE EXTENSION IF NOT EXISTS pgcrypto^^

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)^^

CREATE TABLE IF NOT EXISTS roles (
    id UUID PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
)^^

CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID NOT NULL REFERENCES users(id),
    role_id UUID NOT NULL REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
)^^

CREATE TABLE IF NOT EXISTS categories (
    id UUID PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT
)^^

CREATE TABLE IF NOT EXISTS courses (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(10,2) NOT NULL CHECK (price >= 0),
    difficulty_level VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    category_id UUID NOT NULL REFERENCES categories(id),
    instructor_id UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMP NULL
)^^

CREATE TABLE IF NOT EXISTS course_modules (
    id UUID PRIMARY KEY,
    course_id UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    position INT NOT NULL,
    UNIQUE (course_id, position)
)^^

CREATE TABLE IF NOT EXISTS lessons (
    id UUID PRIMARY KEY,
    module_id UUID NOT NULL REFERENCES course_modules(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    content_url VARCHAR(500),
    duration_minutes INT NOT NULL CHECK (duration_minutes > 0),
    position INT NOT NULL,
    is_free_preview BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE (module_id, position)
)^^

CREATE TABLE IF NOT EXISTS enrollments (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL REFERENCES users(id),
    course_id UUID NOT NULL REFERENCES courses(id),
    enrolled_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    progress_percent NUMERIC(5,2) NOT NULL DEFAULT 0 CHECK (progress_percent >= 0 AND progress_percent <= 100),
    status VARCHAR(30) NOT NULL,
    UNIQUE (student_id, course_id)
)^^

CREATE TABLE IF NOT EXISTS payments (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL REFERENCES users(id),
    course_id UUID NOT NULL REFERENCES courses(id),
    amount NUMERIC(10,2) NOT NULL CHECK (amount >= 0),
    status VARCHAR(30) NOT NULL,
    paid_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)^^

CREATE TABLE IF NOT EXISTS lesson_progress (
    id UUID PRIMARY KEY,
    enrollment_id UUID NOT NULL REFERENCES enrollments(id) ON DELETE CASCADE,
    lesson_id UUID NOT NULL REFERENCES lessons(id),
    completed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (enrollment_id, lesson_id)
)^^

CREATE TABLE IF NOT EXISTS assignments (
    id UUID PRIMARY KEY,
    lesson_id UUID NOT NULL REFERENCES lessons(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    max_score NUMERIC(5,2) NOT NULL CHECK (max_score > 0),
    deadline_days INT
)^^

CREATE TABLE IF NOT EXISTS submissions (
    id UUID PRIMARY KEY,
    assignment_id UUID NOT NULL REFERENCES assignments(id) ON DELETE CASCADE,
    student_id UUID NOT NULL REFERENCES users(id),
    answer_text TEXT NOT NULL,
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(30) NOT NULL
)^^

CREATE TABLE IF NOT EXISTS grades (
    id UUID PRIMARY KEY,
    submission_id UUID UNIQUE NOT NULL REFERENCES submissions(id) ON DELETE CASCADE,
    grader_id UUID NOT NULL REFERENCES users(id),
    score NUMERIC(5,2) NOT NULL CHECK (score >= 0),
    feedback TEXT,
    graded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)^^

CREATE TABLE IF NOT EXISTS certificates (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL REFERENCES users(id),
    course_id UUID NOT NULL REFERENCES courses(id),
    certificate_number VARCHAR(100) UNIQUE NOT NULL,
    issued_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (student_id, course_id)
)^^

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email)^^
CREATE INDEX IF NOT EXISTS idx_courses_category_id ON courses(category_id)^^
CREATE INDEX IF NOT EXISTS idx_courses_instructor_id ON courses(instructor_id)^^
CREATE INDEX IF NOT EXISTS idx_course_modules_course_id ON course_modules(course_id)^^
CREATE INDEX IF NOT EXISTS idx_lessons_module_id ON lessons(module_id)^^
CREATE INDEX IF NOT EXISTS idx_enrollments_student_id ON enrollments(student_id)^^
CREATE INDEX IF NOT EXISTS idx_enrollments_course_id ON enrollments(course_id)^^
CREATE INDEX IF NOT EXISTS idx_payments_student_id ON payments(student_id)^^
CREATE INDEX IF NOT EXISTS idx_payments_course_id ON payments(course_id)^^
CREATE INDEX IF NOT EXISTS idx_lesson_progress_enrollment_id ON lesson_progress(enrollment_id)^^
CREATE INDEX IF NOT EXISTS idx_submissions_student_id ON submissions(student_id)^^
CREATE INDEX IF NOT EXISTS idx_submissions_assignment_id ON submissions(assignment_id)^^
CREATE INDEX IF NOT EXISTS idx_certificates_student_id ON certificates(student_id)^^
CREATE INDEX IF NOT EXISTS idx_certificates_course_id ON certificates(course_id)^^
