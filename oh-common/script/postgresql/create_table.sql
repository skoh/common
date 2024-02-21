-- DROP TYPE state;
-- CREATE TYPE state AS ENUM (
--     'ACTIVE',
--     'DELETED'
--     );

# DROP TABLE IF EXISTS schedule;
# CREATE TABLE schedule
# (
#     id       VARCHAR(100) NOT NULL,
#     pid      VARCHAR(100) NOT NULL,
#     type     VARCHAR(100) NOT NULL,
#     state    VARCHAR(100) NOT NULL DEFAULT 'ACTIVE',
#     mod_date TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
#     reg_date TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
#     CONSTRAINT schedule_pk PRIMARY KEY (id)
# );
#
# CREATE INDEX idx_schedule_type ON schedule (type);
# CREATE INDEX idx_schedule_state ON schedule (state);
# CREATE INDEX idx_schedule_reg_date ON schedule (reg_date);
# CREATE INDEX idx_schedule_mod_date ON schedule (mod_date);
#
# COMMENT ON TABLE schedule IS '스케쥴';
# COMMENT ON COLUMN schedule.id IS '아이디';
# COMMENT ON COLUMN schedule.pid IS '프로세스 아이디';
# COMMENT ON COLUMN schedule.type IS '종류';
# COMMENT ON COLUMN schedule.state IS '상태(등록, 삭제)';
# COMMENT ON COLUMN schedule.reg_date IS '등록일시';
# COMMENT ON COLUMN schedule.mod_date IS '수정일시';

DROP TABLE IF EXISTS sample;
CREATE TABLE sample
(
    id       BIGSERIAL    NOT NULL,
    name     VARCHAR(100) NOT NULL,
    descp    VARCHAR(1000),
    state    VARCHAR(100) NOT NULL DEFAULT 'ACTIVE',
#     state                 NOT NULL DEFAULT 'ACTIVE'::state,
    mod_date TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reg_date TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT sample_pk PRIMARY KEY (id)
);

CREATE INDEX idx_sample_name ON sample (name);
CREATE INDEX idx_sample_state ON sample (state);
CREATE INDEX idx_sample_reg_date ON sample (reg_date);
CREATE INDEX idx_sample_mod_date ON sample (mod_date);

COMMENT ON TABLE sample IS '샘플';
COMMENT ON COLUMN sample.id IS '아이디';
COMMENT ON COLUMN sample.name IS '이름';
COMMENT ON COLUMN sample.descp IS '설명';
COMMENT ON COLUMN sample.state IS '상태(등록, 삭제)';
COMMENT ON COLUMN sample.reg_date IS '등록일시';
COMMENT ON COLUMN sample.mod_date IS '수정일시';

COMMIT;
