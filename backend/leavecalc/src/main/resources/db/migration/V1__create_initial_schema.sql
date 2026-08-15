-- 비어 있는 PostgreSQL DB에 최초 스키마를 생성한다.
-- 기존 테이블이나 데이터를 삭제하는 구문은 포함하지 않는다.

CREATE TABLE dictionary_category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    CONSTRAINT uk_dic_ctg_name UNIQUE (name)
);

CREATE TABLE dictionary (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL REFERENCES dictionary_category(id),
    question VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE feedback (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(20) NOT NULL,
    content VARCHAR(1000),
    email VARCHAR(255),
    rating INTEGER,
    calculation_id VARCHAR(36),
    platform VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'RECEIVED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    provider VARCHAR(255) NOT NULL,
    nickname VARCHAR(255),
    last_login_at TIMESTAMP,
    refresh_token VARCHAR(512),
    onboarding_completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE user_leave_policies (
    user_id BIGINT PRIMARY KEY REFERENCES users(id),
    accepted_at TIMESTAMP NOT NULL,
    leave_accrual_basis VARCHAR(30) NOT NULL,
    hire_date DATE NOT NULL,
    fiscal_year_base_month INTEGER,
    company_size INTEGER,
    work_pattern JSON,
    break_time_pattern JSON,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE leave_yearly_balances (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    weekly_working_days INTEGER NOT NULL,
    avg_daily_work_hours DECIMAL(4, 2) NOT NULL,
    total_leave_minutes INTEGER NOT NULL,
    used_leave_minutes INTEGER NOT NULL DEFAULT 0,
    is_finalized BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT uk_leave_yearly_balances_user_period UNIQUE (user_id, start_date, end_date)
);

CREATE TABLE calendar_events (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    leave_yearly_balance_id BIGINT REFERENCES leave_yearly_balances(id),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_datetime TIMESTAMP NOT NULL,
    end_datetime TIMESTAMP NOT NULL,
    used_leave_minutes INTEGER NOT NULL,
    is_all_day BOOLEAN DEFAULT FALSE,
    is_leave_event BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE leave_grants (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    leave_yearly_balance_id BIGINT NOT NULL REFERENCES leave_yearly_balances(id) ON DELETE CASCADE,
    grant_type VARCHAR(30) NOT NULL,
    source VARCHAR(30) NOT NULL,
    source_key VARCHAR(120) NOT NULL,
    granted_minutes INTEGER NOT NULL CHECK (granted_minutes >= 0),
    adjusted_minutes INTEGER NOT NULL DEFAULT 0,
    used_minutes INTEGER NOT NULL DEFAULT 0 CHECK (used_minutes >= 0),
    granted_date DATE NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_leave_grants_source_key UNIQUE (user_id, source_key),
    CONSTRAINT ck_leave_grants_period CHECK (start_date <= end_date),
    CONSTRAINT ck_leave_grants_remaining CHECK (granted_minutes + adjusted_minutes - used_minutes >= 0)
);

CREATE TABLE calendar_event_leave_allocations (
    id BIGSERIAL PRIMARY KEY,
    calendar_event_id BIGINT NOT NULL REFERENCES calendar_events(id) ON DELETE CASCADE,
    leave_grant_id BIGINT NOT NULL REFERENCES leave_grants(id) ON DELETE CASCADE,
    allocated_minutes INTEGER NOT NULL CHECK (allocated_minutes > 0)
);

CREATE TABLE user_notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(100) NOT NULL,
    message VARCHAR(500) NOT NULL,
    granted_minutes INTEGER,
    event_key VARCHAR(150) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_notifications_event_key UNIQUE (user_id, event_key)
);

CREATE INDEX idx_leave_yearly_balances_finalized_end_date ON leave_yearly_balances(is_finalized, end_date);
CREATE INDEX idx_leave_yearly_balances_user_id_id_desc ON leave_yearly_balances(user_id, id DESC);
CREATE INDEX idx_calendar_events_user_leave_period ON calendar_events(user_id, is_leave_event, start_datetime, end_datetime);
CREATE INDEX idx_leave_grants_user_period ON leave_grants(user_id, start_date, end_date);
CREATE INDEX idx_leave_grants_balance ON leave_grants(leave_yearly_balance_id);
CREATE INDEX idx_leave_allocations_event ON calendar_event_leave_allocations(calendar_event_id);
CREATE INDEX idx_leave_allocations_grant ON calendar_event_leave_allocations(leave_grant_id);
CREATE INDEX idx_user_notifications_user_created ON user_notifications(user_id, created_at DESC);
CREATE INDEX idx_user_notifications_user_unread ON user_notifications(user_id, is_read) WHERE is_read = FALSE;
