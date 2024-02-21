DROP TABLE IF EXISTS schedule;
DROP TABLE IF EXISTS sample;

CREATE TABLE schedule
(
    id       VARCHAR(200) PRIMARY KEY COMMENT '아이디',
    pid      VARCHAR(100)                                         NOT NULL COMMENT '프로세스 아이디',
    type     VARCHAR(100)                                         NOT NULL COMMENT '종류',
    state    ENUM ('ACTIVE', 'DELETED') DEFAULT 'ACTIVE'          NOT NULL COMMENT '상태(등록, 삭제)',
    reg_date DATETIME                   DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    mod_date DATETIME                   DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    KEY idx_schedule_type (type),
    KEY idx_schedule_state (state),
    KEY idx_schedule_reg_date (reg_date),
    KEY idx_schedule_mod_date (mod_date)
) COMMENT '스케쥴';

CREATE TABLE sample
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '아이디',
    name     VARCHAR(100)                                         NOT NULL COMMENT '이름',
    descp    VARCHAR(1000) COMMENT '설명',
    state    ENUM ('ACTIVE', 'DELETED') DEFAULT 'ACTIVE'          NOT NULL COMMENT '상태(등록, 삭제)',
    reg_date DATETIME                   DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    mod_date DATETIME                   DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    KEY idx_sample_name (name),
    KEY idx_sample_state (state),
    KEY idx_sample_reg_date (reg_date),
    KEY idx_sample_mod_date (mod_date)
) COMMENT '샘플';

COMMIT;
