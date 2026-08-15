-- V3 follows the Java-based V2 CSV seed migration.
CREATE TABLE recommended_schedules (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_recommended_schedules_period CHECK (start_date <= end_date),
    CONSTRAINT uk_recommended_schedules_name_period UNIQUE (name, start_date, end_date)
);

CREATE INDEX idx_recommended_schedules_period
    ON recommended_schedules(end_date, start_date);

INSERT INTO recommended_schedules (name, start_date, end_date) VALUES
    ('광복절 연휴', '2026-08-15', '2026-08-17'),
    ('추석 연휴', '2026-09-24', '2026-09-27'),
    ('개천절 연휴', '2026-10-03', '2026-10-05'),
    ('한글날 연휴', '2026-10-09', '2026-10-11'),
    ('성탄절 연휴', '2026-12-25', '2026-12-27'),
    ('신정 연휴', '2027-01-01', '2027-01-03'),
    ('설 연휴', '2027-02-06', '2027-02-09'),
    ('삼일절 연휴', '2027-02-27', '2027-03-01'),
    ('노동절 연휴', '2027-05-01', '2027-05-03'),
    ('제헌절 연휴', '2027-07-17', '2027-07-19'),
    ('광복절 연휴', '2027-08-14', '2027-08-16'),
    ('추석 연휴', '2027-09-14', '2027-09-16'),
    ('개천절 연휴', '2027-10-02', '2027-10-04'),
    ('한글날 연휴', '2027-10-09', '2027-10-11'),
    ('성탄절 연휴', '2027-12-25', '2027-12-27');
