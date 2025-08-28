-- Création de la table Level
CREATE TABLE level (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    abbreviation VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Création de la table Teacher
CREATE TABLE teacher (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    abbreviation VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Création de la table Room
CREATE TABLE room (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    size INTEGER NOT NULL,
    abbreviation VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Création de la table TeachingUnit
CREATE TABLE teaching_unit (
    id BIGSERIAL PRIMARY KEY,
    level_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    abbreviation VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_teaching_unit_level 
        FOREIGN KEY (level_id) 
        REFERENCES level(id) 
        ON DELETE CASCADE
);

-- Création de la table Group
CREATE TABLE "group" (
    id BIGSERIAL PRIMARY KEY,
    level_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    size INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_group_level 
        FOREIGN KEY (level_id) 
        REFERENCES level(id) 
        ON DELETE CASCADE
);

-- Création de la table Scheduler
CREATE TABLE schedule_item (
    id BIGSERIAL PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    teaching_unit_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_scheduler_teacher 
        FOREIGN KEY (teacher_id) 
        REFERENCES teacher(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_scheduler_teaching_unit 
        FOREIGN KEY (teaching_unit_id) 
        REFERENCES teaching_unit(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_scheduler_room 
        FOREIGN KEY (room_id) 
        REFERENCES room(id) 
        ON DELETE CASCADE
);

-- Création des index pour améliorer les performances
CREATE INDEX idx_teaching_unit_level_id ON teaching_unit(level_id);
CREATE INDEX idx_group_level_id ON "group"(level_id);
CREATE INDEX idx_scheduler_teacher_id ON schedule_item(teacher_id);
CREATE INDEX idx_scheduler_teaching_unit_id ON schedule_item(teaching_unit_id);
CREATE INDEX idx_scheduler_room_id ON schedule_item(room_id);
CREATE INDEX idx_scheduler_times ON schedule_item(start_time, end_time);

-- Commentaires sur les tables
COMMENT ON TABLE level IS 'Table des niveaux d''enseignement';
COMMENT ON TABLE teacher IS 'Table des enseignants';
COMMENT ON TABLE room IS 'Table des salles de classe';
COMMENT ON TABLE teaching_unit IS 'Table des unités d''enseignement';
COMMENT ON TABLE "group" IS 'Table des groupes d''étudiants';
COMMENT ON TABLE schedule_item IS 'Table de planification des cours';