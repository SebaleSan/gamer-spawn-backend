package com.gamerspawn.backend.config;


import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import com.gamerspawn.backend.entity.Event;
import com.gamerspawn.backend.entity.User;
import com.gamerspawn.backend.repository.EventRepository;
import com.gamerspawn.backend.repository.UserRepository;

@Component
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(
            UserRepository userRepository,
            EventRepository eventRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        createUserIfAbsent("admin", "admin@correo.cl", "admin", "ROLE_ADMIN");
        createUserIfAbsent("user1", "user1@correo.cl", "user1", "ROLE_USER");
        createUserIfAbsent("user2", "user2@correo.cl", "user2", "ROLE_USER");

        createEventIfAbsent(
                "Copa Nacional de eSports",
                LocalDate.of(2026, 11, 29),
                "Movistar Arena, Santiago",
                "Torneo eSports",
                "EA Sports FC 26",
                "Torneo",
                "Chile",
                "Santiago",
                "PRESENCIAL",
                LocalTime.of(18, 0),
                null,
                "Competencia nacional con equipos profesionales y finales en vivo.",
                "GamerSpawn Chile",
                "Tener cuenta activa y equipo inscrito antes del evento.",
                "Inscripciones hasta el 20 de noviembre en la plataforma GamerSpawn.",
                "/images/events/6a9c6c917ffebef7796971aa_ZONA-GAMER-INICIO.jpg");
        createEventIfAbsent(
                "Santiago Gaming Fest",
                LocalDate.of(2026, 11, 15),
                "Espacio Riesco, Santiago",
                "Festival gamer",
                "League of Legends",
                "Festival",
                "Chile",
                "Santiago",
                "PRESENCIAL",
                LocalTime.of(10, 0),
                null,
                "Festival con torneos, cosplay, invitados y novedades de la industria.",
                "GamerSpawn Producciones",
                "Presentar entrada y respetar las normas del recinto.",
                "Entradas disponibles desde el 1 de noviembre.",
                "/images/events/organizar-evento-gaming.jpg");
        createEventIfAbsent(
                "Game Jam Chile",
                LocalDate.of(2026, 12, 13),
                "Online",
                "Game jam",
                "Unity",
                "Desarrollo",
                "Chile",
                "Online",
                "ONLINE",
                LocalTime.of(9, 0),
                "https://meet.example.com/game-jam-chile",
                "Desafío de desarrollo de videojuegos con equipos de todo Chile.",
                "Indie Devs Chile",
                "Equipo de 2 a 5 personas y conocimientos básicos de Unity.",
                "Registro gratuito hasta el 10 de diciembre mediante GamerSpawn.",
                "/images/events/300-nodonueve-imagen.webp");
        createEventIfAbsent(
                "Cosplay Fest Chile",
                LocalDate.of(2026, 12, 20),
                "Estación Mapocho, Santiago",
                "Cosplay",
                "Múltiples juegos",
                "Cosplay",
                "Chile",
                "Santiago",
                "PRESENCIAL",
                LocalTime.of(11, 0),
                null,
                "Jornada dedicada al cosplay, concursos y comunidad gamer.",
                "Cosplay Fest Chile",
                "El cosplay debe respetar las reglas de convivencia del evento.",
                "Inscripción para concursos en el acceso del recinto.",
                "/images/events/e6e6d1_87f0220d52334452bea9a7cecbd722b3~mv2.png");
        createEventIfAbsent(
                "LAN Party Valparaíso",
                LocalDate.of(2026, 11, 8),
                "Valparaíso",
                "LAN Party",
                "Counter-Strike 2",
                "Competencia",
                "Chile",
                "Valparaíso",
                "PRESENCIAL",
                LocalTime.of(20, 0),
                null,
                "Encuentro presencial de 24 horas con partidas, competencias y comunidad.",
                "Valpo Gaming Club",
                "Llevar computador o consola y contar con inscripción confirmada.",
                "Cupos limitados; inscripción previa obligatoria.",
                "/images/events/DSC6686.jpg");
        createEventIfAbsent(
                "Torneo Indie Games",
                LocalDate.of(2026, 11, 22),
                "Concepción",
                "Torneo y demos",
                "Hollow Knight",
                "Indie",
                "Chile",
                "Concepción",
                "PRESENCIAL",
                LocalTime.of(14, 0),
                null,
                "Torneo de videojuegos independientes con demostraciones de desarrolladores locales.",
                "Indie Games Concepción",
                "Participación abierta para mayores de doce años.",
                "Registro gratuito en el recinto hasta completar cupos.",
                "/images/events/FOTOS-BARBB-1.jpg");
        createEventIfAbsent(
                "Retro Arcade Night",
                LocalDate.of(2026, 12, 6),
                "La Serena",
                "Noche temática",
                "Street Fighter II",
                "Retro",
                "Chile",
                "La Serena",
                "PRESENCIAL",
                LocalTime.of(19, 30),
                null,
                "Experiencia retro con clásicos arcade de los años 80 y 90.",
                "Retro Club La Serena",
                "Actividad familiar; no se requiere experiencia previa.",
                "Entrada general disponible en la puerta del evento.",
                "/images/events/captura-de-pantalla-2023-09-15-a-las-19.56.10.png");
    }

    private void createUserIfAbsent(String username, String email, String password, String role) {
        if (userRepository.findByUsername(username) == null) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            userRepository.save(user);
        }
    }

    private void createEventIfAbsent(
            String title,
            LocalDate eventDate,
            String location,
            String eventType,
            String videoGame,
            String category,
            String country,
            String city,
            String modality,
            LocalTime eventTime,
            String accessLink,
            String description,
            String organizer,
            String participationRequirements,
            String registrationInformation,
            String imageUrl) {
        Event existingEvent = null;
        for (Event event : eventRepository.findAll()) {
            if (title.equals(event.getTitle())) {
                existingEvent = event;
                break;
            }
        }

        Event event = existingEvent == null ? new Event() : existingEvent;
        event.setTitle(title);
        event.setEventDate(eventDate);
        event.setEventTime(eventTime);
        event.setLocation(location);
        event.setAccessLink(accessLink);
        event.setEventType(eventType);
        event.setVideoGame(videoGame);
        event.setCategory(category);
        event.setCountry(country);
        event.setCity(city);
        event.setModality(modality);
        event.setDescription(description);
        event.setOrganizer(organizer);
        event.setParticipationRequirements(participationRequirements);
        event.setRegistrationInformation(registrationInformation);
        event.setImageUrl(imageUrl);
        event.setPhotoUrls(List.of(imageUrl));
        eventRepository.save(event);
    }
}