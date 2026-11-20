CREATE DATABASE IF NOT EXISTS gamerspawn;

USE gamerspawn;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS event_photos;
DROP TABLE IF EXISTS event_media;
DROP TABLE IF EXISTS event_comment;
DROP TABLE IF EXISTS event_rating;
DROP TABLE IF EXISTS event;
DROP TABLE IF EXISTS user;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE user (
    id BIGINT NOT NULL,
    email VARCHAR(255),
    password VARCHAR(255),
    role VARCHAR(255),
    username VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE event (
    id BIGINT NOT NULL AUTO_INCREMENT,
    access_link VARCHAR(255),
    category VARCHAR(255),
    city VARCHAR(255),
    country VARCHAR(255),
    description VARCHAR(255),
    event_date DATE,
    event_time TIME,
    event_type VARCHAR(255),
    image_url VARCHAR(255),
    location VARCHAR(255),
    modality VARCHAR(255),
    organizer VARCHAR(255),
    participation_requirements VARCHAR(255),
    registration_information VARCHAR(255),
    title VARCHAR(255),
    video_game VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE event_media (
    id BIGINT NOT NULL AUTO_INCREMENT,
    type VARCHAR(10) NOT NULL,
    url VARCHAR(500) NOT NULL,
    created_at DATETIME NOT NULL,
    event_id BIGINT NOT NULL,
    user_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT chk_event_media_type CHECK (type IN ('PHOTO', 'VIDEO')),
    CONSTRAINT fk_event_media_event
        FOREIGN KEY (event_id) REFERENCES event (id),
    CONSTRAINT fk_event_media_user
        FOREIGN KEY (user_id) REFERENCES user (id)
) ENGINE = InnoDB;

CREATE TABLE event_comment (
    id BIGINT NOT NULL AUTO_INCREMENT,
    content VARCHAR(1000) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_event_comment_event
        FOREIGN KEY (event_id) REFERENCES event (id),
    CONSTRAINT fk_event_comment_user
        FOREIGN KEY (user_id) REFERENCES user (id)
) ENGINE = InnoDB;

CREATE TABLE event_rating (
    id BIGINT NOT NULL AUTO_INCREMENT,
    score INTEGER NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_event_rating_event_user UNIQUE (event_id, user_id),
    CONSTRAINT chk_event_rating_score CHECK (score BETWEEN 1 AND 5),
    CONSTRAINT fk_event_rating_event
        FOREIGN KEY (event_id) REFERENCES event (id),
    CONSTRAINT fk_event_rating_user
        FOREIGN KEY (user_id) REFERENCES user (id)
) ENGINE = InnoDB;

INSERT INTO event (
    id, access_link, category, city, country, description, event_date,
    event_time, event_type, image_url, location, modality, organizer,
    participation_requirements, registration_information, title, video_game
) VALUES
    (1, NULL, 'Torneo', 'Santiago', 'Chile',
     'Competencia nacional con equipos profesionales y finales en vivo.',
     '2026-11-29', '18:00:00', 'Torneo eSports',
     '/images/events/6a9c6c917ffebef7796971aa_ZONA-GAMER-INICIO.jpg',
     'Movistar Arena, Santiago', 'PRESENCIAL', 'GamerSpawn Chile',
     'Tener cuenta activa y equipo inscrito antes del evento.',
     'Inscripciones hasta el 20 de noviembre en la plataforma GamerSpawn.',
     'Copa Nacional de eSports', 'EA Sports FC 26'),
    (2, NULL, 'Festival', 'Santiago', 'Chile',
     'Festival con torneos, cosplay, invitados y novedades de la industria.',
     '2026-11-15', '10:00:00', 'Festival gamer',
     '/images/events/organizar-evento-gaming.jpg',
     'Espacio Riesco, Santiago', 'PRESENCIAL', 'GamerSpawn Producciones',
     'Presentar entrada y respetar las normas del recinto.',
     'Entradas disponibles desde el 1 de noviembre.',
     'Santiago Gaming Fest', 'League of Legends'),
    (3, 'https://meet.example.com/game-jam-chile', 'Desarrollo', 'Online', 'Chile',
     'Desafío de desarrollo de videojuegos con equipos de todo Chile.',
     '2026-12-13', '09:00:00', 'Game jam',
     '/images/events/300-nodonueve-imagen.webp',
     'Online', 'ONLINE', 'Indie Devs Chile',
     'Equipo de 2 a 5 personas y conocimientos básicos de Unity.',
     'Registro gratuito hasta el 10 de diciembre mediante GamerSpawn.',
     'Game Jam Chile', 'Unity'),
    (4, NULL, 'Cosplay', 'Santiago', 'Chile',
     'Jornada dedicada al cosplay, concursos y comunidad gamer.',
     '2026-12-20', '11:00:00', 'Cosplay',
     '/images/events/e6e6d1_87f0220d52334452bea9a7cecbd722b3~mv2.png',
     'Estación Mapocho, Santiago', 'PRESENCIAL', 'Cosplay Fest Chile',
     'El cosplay debe respetar las reglas de convivencia del evento.',
     'Inscripción para concursos en el acceso del recinto.',
     'Cosplay Fest Chile', 'Múltiples juegos'),
    (5, NULL, 'Competencia', 'Valparaíso', 'Chile',
     'Encuentro presencial de 24 horas con partidas, competencias y comunidad.',
     '2026-11-08', '20:00:00', 'LAN Party',
     '/images/events/DSC6686.jpg',
     'Valparaíso', 'PRESENCIAL', 'Valpo Gaming Club',
     'Llevar computador o consola y contar con inscripción confirmada.',
     'Cupos limitados; inscripción previa obligatoria.',
     'LAN Party Valparaíso', 'Counter-Strike 2'),
    (6, NULL, 'Indie', 'Concepción', 'Chile',
     'Torneo de videojuegos independientes con demostraciones de desarrolladores locales.',
     '2026-11-22', '14:00:00', 'Torneo y demos',
     '/images/events/FOTOS-BARBB-1.jpg',
     'Concepción', 'PRESENCIAL', 'Indie Games Concepción',
     'Participación abierta para mayores de doce años.',
     'Registro gratuito en el recinto hasta completar cupos.',
     'Torneo Indie Games', 'Hollow Knight'),
    (7, NULL, 'Retro', 'La Serena', 'Chile',
     'Experiencia retro con clásicos arcade de los años 80 y 90.',
     '2026-12-06', '19:30:00', 'Noche temática',
     '/images/events/captura-de-pantalla-2023-09-15-a-las-19.56.10.png',
     'La Serena', 'PRESENCIAL', 'Retro Club La Serena',
     'Actividad familiar; no se requiere experiencia previa.',
     'Entrada general disponible en la puerta del evento.',
     'Retro Arcade Night', 'Street Fighter II');

INSERT INTO event_media (event_id, type, url, created_at) VALUES
    (1, 'PHOTO', '/images/events/6a9c6c917ffebef7796971aa_ZONA-GAMER-INICIO.jpg', NOW()),
    (2, 'PHOTO', '/images/events/organizar-evento-gaming.jpg', NOW()),
    (3, 'PHOTO', '/images/events/300-nodonueve-imagen.webp', NOW()),
    (4, 'PHOTO', '/images/events/e6e6d1_87f0220d52334452bea9a7cecbd722b3~mv2.png', NOW()),
    (5, 'PHOTO', '/images/events/DSC6686.jpg', NOW()),
    (6, 'PHOTO', '/images/events/FOTOS-BARBB-1.jpg', NOW()),
    (7, 'PHOTO', '/images/events/captura-de-pantalla-2023-09-15-a-las-19.56.10.png', NOW());

ALTER TABLE event AUTO_INCREMENT = 8;
