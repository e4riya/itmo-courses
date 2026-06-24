CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql^^

CREATE OR REPLACE FUNCTION check_course_price()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.price < 0 THEN
        RAISE EXCEPTION 'Course price cannot be negative';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql^^

CREATE OR REPLACE FUNCTION create_enrollment_after_successful_payment()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status = 'PAID' THEN
        INSERT INTO enrollments (id, student_id, course_id, status)
        VALUES (gen_random_uuid(), NEW.student_id, NEW.course_id, 'ACTIVE')
        ON CONFLICT (student_id, course_id) DO NOTHING;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql^^

CREATE OR REPLACE FUNCTION update_enrollment_progress()
RETURNS TRIGGER AS $$
DECLARE
    total_lessons_count NUMERIC;
    completed_lessons_count NUMERIC;
    new_progress NUMERIC(5,2);
BEGIN
    SELECT COUNT(l.id)
    INTO total_lessons_count
    FROM enrollments e
    JOIN course_modules cm ON cm.course_id = e.course_id
    JOIN lessons l ON l.module_id = cm.id
    WHERE e.id = NEW.enrollment_id;

    SELECT COUNT(DISTINCT lp.lesson_id)
    INTO completed_lessons_count
    FROM lesson_progress lp
    WHERE lp.enrollment_id = NEW.enrollment_id;

    IF total_lessons_count = 0 THEN
        new_progress := 0;
    ELSE
        new_progress := ROUND((completed_lessons_count / total_lessons_count) * 100, 2);
    END IF;

    UPDATE enrollments
    SET progress_percent = new_progress,
        status = CASE WHEN new_progress >= 100 THEN 'COMPLETED' ELSE status END
    WHERE id = NEW.enrollment_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql^^

CREATE OR REPLACE FUNCTION create_certificate_when_course_completed()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.progress_percent >= 100 AND OLD.progress_percent IS DISTINCT FROM NEW.progress_percent THEN
        INSERT INTO certificates (id, student_id, course_id, certificate_number)
        VALUES (gen_random_uuid(), NEW.student_id, NEW.course_id, 'CERT-' || gen_random_uuid()::text)
        ON CONFLICT (student_id, course_id) DO NOTHING;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql^^

CREATE OR REPLACE FUNCTION check_grade_score()
RETURNS TRIGGER AS $$
DECLARE
    allowed_score NUMERIC(5,2);
BEGIN
    SELECT a.max_score
    INTO allowed_score
    FROM submissions s
    JOIN assignments a ON a.id = s.assignment_id
    WHERE s.id = NEW.submission_id;

    IF NEW.score < 0 THEN
        RAISE EXCEPTION 'Grade score cannot be negative';
    END IF;

    IF NEW.score > allowed_score THEN
        RAISE EXCEPTION 'Grade score % exceeds assignment max score %', NEW.score, allowed_score;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql^^

CREATE OR REPLACE FUNCTION prevent_delete_published_course()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.status = 'PUBLISHED' AND EXISTS (SELECT 1 FROM enrollments e WHERE e.course_id = OLD.id) THEN
        RAISE EXCEPTION 'Cannot delete published course with enrollments';
    END IF;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql^^

DROP TRIGGER IF EXISTS trg_users_set_updated_at ON users^^
CREATE TRIGGER trg_users_set_updated_at
BEFORE UPDATE ON users
FOR EACH ROW EXECUTE FUNCTION set_updated_at()^^

DROP TRIGGER IF EXISTS trg_courses_set_updated_at ON courses^^
CREATE TRIGGER trg_courses_set_updated_at
BEFORE UPDATE ON courses
FOR EACH ROW EXECUTE FUNCTION set_updated_at()^^

DROP TRIGGER IF EXISTS trg_courses_check_price ON courses^^
CREATE TRIGGER trg_courses_check_price
BEFORE INSERT OR UPDATE ON courses
FOR EACH ROW EXECUTE FUNCTION check_course_price()^^

DROP TRIGGER IF EXISTS trg_payments_create_enrollment ON payments^^
CREATE TRIGGER trg_payments_create_enrollment
AFTER INSERT OR UPDATE ON payments
FOR EACH ROW EXECUTE FUNCTION create_enrollment_after_successful_payment()^^

DROP TRIGGER IF EXISTS trg_lesson_progress_update_enrollment ON lesson_progress^^
CREATE TRIGGER trg_lesson_progress_update_enrollment
AFTER INSERT ON lesson_progress
FOR EACH ROW EXECUTE FUNCTION update_enrollment_progress()^^

DROP TRIGGER IF EXISTS trg_enrollments_create_certificate ON enrollments^^
CREATE TRIGGER trg_enrollments_create_certificate
AFTER UPDATE OF progress_percent ON enrollments
FOR EACH ROW EXECUTE FUNCTION create_certificate_when_course_completed()^^

DROP TRIGGER IF EXISTS trg_grades_check_score ON grades^^
CREATE TRIGGER trg_grades_check_score
BEFORE INSERT OR UPDATE ON grades
FOR EACH ROW EXECUTE FUNCTION check_grade_score()^^

DROP TRIGGER IF EXISTS trg_courses_prevent_delete_published ON courses^^
CREATE TRIGGER trg_courses_prevent_delete_published
BEFORE DELETE ON courses
FOR EACH ROW EXECUTE FUNCTION prevent_delete_published_course()^^
