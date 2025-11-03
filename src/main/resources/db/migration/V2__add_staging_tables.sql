CREATE TABLE staging_teaching_unit
(
    teaching_unit_id BIGSERIAL PRIMARY KEY,
    level_id         BIGINT       NULL,
    name             VARCHAR(255) NOT NULL,
    abbreviation     VARCHAR(50)  NOT NULL,
    stage_id VARCHAR(255) NOT NULL,
    filename         TEXT,
    row_index        INTEGER,
    row_context      TEXT
);

CREATE TABLE staging_groups
(
    group_id    BIGSERIAL PRIMARY KEY,
    level_id    BIGINT       NOT NULL,
    name        VARCHAR(255) NOT NULL,
    size        INTEGER      NOT NULL,
    stage_id VARCHAR(255) NOT NULL,
    filename    TEXT,
    row_index   INTEGER,
    row_context TEXT
);