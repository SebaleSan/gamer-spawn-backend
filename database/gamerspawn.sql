CREATE DATABASE gamerspawn;

USE gamerspawn;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS event_photos;
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

CREATE TABLE event_photos (
    event_id BIGINT NOT NULL,
    photo_urls VARCHAR(255),
    CONSTRAINT fk_event_photos_event
        FOREIGN KEY (event_id) REFERENCES event (id)
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

INSERT INTO event_photos (event_id, photo_urls) VALUES
    (1, '/images/events/6a9c6c917ffebef7796971aa_ZONA-GAMER-INICIO.jpg'),
    (2, '/images/events/organizar-evento-gaming.jpg'),
    (3, '/images/events/300-nodonueve-imagen.webp'),
    (4, '/images/events/e6e6d1_87f0220d52334452bea9a7cecbd722b3~mv2.png'),
    (5, '/images/events/DSC6686.jpg'),
    (6, '/images/events/FOTOS-BARBB-1.jpg'),
    (7, '/images/events/captura-de-pantalla-2023-09-15-a-las-19.56.10.png');

ALTER TABLE event AUTO_INCREMENT = 8;
