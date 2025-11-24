package com.example.birthday.service;

import com.example.birthday.model.Friend;
import com.example.birthday.notification.NotificationSender;
import com.example.birthday.repository.FriendRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BirthdayServiceTest {

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private NotificationSender notificationSender;

    @InjectMocks
    private BirthdayService birthdayService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.out.println("==================================================");
        System.out.println(" INIZIALIZZAZIONE AMBIENTE DI TEST ");
        System.out.println("==================================================\n");
    }

    // -------------------------------------------------------------
    // 1. Trova festeggiati normale
    // -------------------------------------------------------------
    @Test
    void testFindTodaysBirthdays() {
        System.out.println("--------------------------------------------------");
        System.out.println(" TEST: testFindTodaysBirthdays");
        System.out.println(" Descrizione: verifica che solo gli amici nati oggi vengano selezionati.");
        System.out.println("--------------------------------------------------");

        LocalDate today = LocalDate.now();

        Friend f1 = new Friend("Mario", "Bianchi", today, "mario@test.com");
        Friend f2 = new Friend("Luca", "Rossi", LocalDate.of(1990, 5, 10), "luca@test.com");

        System.out.println("Mock repository con i seguenti amici:");
        System.out.println(" - Mario Bianchi, nato il " + f1.dateOfBirth());
        System.out.println(" - Luca Rossi, nato il " + f2.dateOfBirth() + "\n");

        when(friendRepository.findAll()).thenReturn(List.of(f1, f2));

        System.out.println("Esecuzione: ricerca festeggiati del giorno " + today);
        List<Friend> result = birthdayService.findTodaysBirthdays();

        System.out.println("Risultati ottenuti: " + result.size() + " festeggiato trovato.");
        result.forEach(f -> System.out.println(" - " + f.firstName() + " " + f.lastName()));

        assertThat(result).containsExactly(f1);
        System.out.println("Verifica completata: OK\n");
    }

    // -------------------------------------------------------------
    // 2. Regola 29 Feb
    // -------------------------------------------------------------
    @Test
    void testFeb29Rule() {
        System.out.println("--------------------------------------------------");
        System.out.println(" TEST: testFeb29Rule");
        System.out.println(" Descrizione: verifica che un nato il 29 Feb compaia tra i festeggiati il 28 Feb negli anni non bisestili.");
        System.out.println("--------------------------------------------------");

        Friend leap = new Friend("Mario", "Bianchi",
                LocalDate.of(1992, 2, 29),
                "mario@test.com");

        System.out.println("Mock repository con:");
        System.out.println(" - Mario Bianchi, nato il 29/02/1992\n");

        when(friendRepository.findAll()).thenReturn(List.of(leap));

        LocalDate fakeToday = LocalDate.of(2025, 2, 28);
        System.out.println("Simulazione: oggi è " + fakeToday + " (anno non bisestile).");
        System.out.println("Ci si aspetta che Mario venga considerato festeggiato.\n");

        List<Friend> result = birthdayService.findBirthdaysOn(fakeToday);

        System.out.println("Risultati ricerca festeggiati:");
        result.forEach(f -> System.out.println(" - " + f.firstName() + " " + f.lastName()));

        assertThat(result).containsExactly(leap);
        System.out.println("Verifica completata: OK\n");
    }

    // -------------------------------------------------------------
    // 3. Invio auguri
    // -------------------------------------------------------------
    @Test
    void testBirthdayEmailIsSent() {
        System.out.println("--------------------------------------------------");
        System.out.println(" TEST: testBirthdayEmailIsSent");
        System.out.println(" Descrizione: verifica che venga inviata la mail di compleanno ai festeggiati.");
        System.out.println("--------------------------------------------------");

        Friend f1 = new Friend("Mario", "Bianchi", LocalDate.now(), "mario@test.com");

        System.out.println("Mock repository con:");
        System.out.println(" - " + f1.firstName() + " " + f1.lastName() + "\n");

        when(friendRepository.findAll()).thenReturn(List.of(f1));

        System.out.println("Esecuzione: invio auguri di compleanno.");
        birthdayService.sendBirthdayGreetings();

        System.out.println("Verifica che sia stata inviata una mail a: " + f1.email());
        verify(notificationSender, times(1))
                .send(eq(f1.email()), eq("Happy birthday!"), contains("Mario"));

        System.out.println("Verifica completata: OK\n");
    }

    // -------------------------------------------------------------
    // 4. Reminder agli altri
    // -------------------------------------------------------------
    @Test
    void testReminderSentToOthers() {
        System.out.println("--------------------------------------------------");
        System.out.println(" TEST: testReminderSentToOthers");
        System.out.println(" Descrizione: verifica l'invio dei reminder agli amici non festeggiati e tra festeggiati.");
        System.out.println("--------------------------------------------------");

        Friend f1 = new Friend("Mario", "Bianchi", LocalDate.now(), "mario@test.com");
        Friend f2 = new Friend("Luca", "Rossi", LocalDate.now(), "luca@test.com");
        Friend f3 = new Friend("Sara", "Verdi", LocalDate.of(1990, 5, 10), "sara@test.com");

        System.out.println("Mock repository con:");
        System.out.println(" - Festeggiato: Mario Bianchi");
        System.out.println(" - Festeggiato: Luca Rossi");
        System.out.println(" - Non festeggiato: Sara Verdi\n");

        when(friendRepository.findAll()).thenReturn(List.of(f1, f2, f3));

        birthdayService.sendBirthdayGreetings();

        System.out.println("Verifica: Sara deve ricevere reminder per entrambi i festeggiati.");
        verify(notificationSender).send(
                eq("sara@test.com"),
                eq("Birthday Reminder"),
                contains("Mario Bianchi, Luca Rossi")
        );

        System.out.println("Verifica: Mario deve ricevere reminder per Luca.");
        verify(notificationSender).send(eq("mario@test.com"), eq("Birthday Reminder"), contains("Luca Rossi"));

        System.out.println("Verifica: Luca deve ricevere reminder per Mario.");
        verify(notificationSender).send(eq("luca@test.com"), eq("Birthday Reminder"), contains("Mario Bianchi"));

        System.out.println("Verifica completata: OK\n");
    }

    // -------------------------------------------------------------
    // 5. Reminder unico con più nomi
    // -------------------------------------------------------------
    @Test
    void testSingleReminderWithMultipleNames() {
        System.out.println("--------------------------------------------------");
        System.out.println(" TEST: testSingleReminderWithMultipleNames");
        System.out.println("--------------------------------------------------");

        Friend f1 = new Friend("Mario", "Bianchi", LocalDate.now(), "m1@test.com");
        Friend f2 = new Friend("Luca", "Rossi", LocalDate.now(), "m2@test.com");
        Friend f3 = new Friend("Anna", "Verdi", LocalDate.now(), "m3@test.com");
        Friend recipient = new Friend("Sara", "Neri", LocalDate.of(1990, 5, 10), "sara@test.com");

        System.out.println("Mock repository con 3 festeggiati e 1 destinatario:");
        System.out.println(" - Mario Bianchi");
        System.out.println(" - Luca Rossi");
        System.out.println(" - Anna Verdi");
        System.out.println(" - Ricevente: Sara Neri\n");

        when(friendRepository.findAll()).thenReturn(List.of(f1, f2, f3, recipient));

        birthdayService.sendBirthdayGreetings();

        System.out.println("Verifica: Sara deve ricevere reminder con tutti i nomi.");
        verify(notificationSender).send(
                eq("sara@test.com"),
                eq("Birthday Reminder"),
                argThat(body ->
                        body.contains("Mario Bianchi") &&
                                body.contains("Luca Rossi") &&
                                body.contains("Anna Verdi")
                )
        );

        System.out.println("Verifica completata: OK\n");
    }

    // -------------------------------------------------------------
    // 6. Nessun reminder se c’è un solo festeggiato
    // -------------------------------------------------------------
    @Test
    void testNoReminderIfOnlyOneBirthday() {
        System.out.println("--------------------------------------------------");
        System.out.println(" TEST: testNoReminderIfOnlyOneBirthday");
        System.out.println("--------------------------------------------------");

        Friend f1 = new Friend("Mario", "Bianchi", LocalDate.now(), "mario@test.com");
        Friend f2 = new Friend("Sara", "Verdi", LocalDate.of(2000, 10, 15), "sara@test.com");

        when(friendRepository.findAll()).thenReturn(List.of(f1, f2));

        birthdayService.sendBirthdayGreetings();

        System.out.println("Verifica: Mario non deve ricevere reminder.");
        verify(notificationSender, times(0))
                .send(eq("mario@test.com"), eq("Birthday Reminder"), anyString());

        System.out.println("Verifica: Sara deve ricevere reminder su Mario.");
        verify(notificationSender).send(eq("sara@test.com"), eq("Birthday Reminder"), contains("Mario Bianchi"));

        System.out.println("Verifica completata: OK\n");
    }

}
