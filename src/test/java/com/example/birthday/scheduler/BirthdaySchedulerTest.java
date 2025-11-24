package com.example.birthday.scheduler;

import com.example.birthday.model.Friend;
import com.example.birthday.notification.NotificationSender;
import com.example.birthday.repository.FriendRepository;
import com.example.birthday.service.BirthdayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

class BirthdaySchedulerTest {

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private NotificationSender notificationSender;

    private BirthdayService birthdayService;

    @InjectMocks
    private BirthdayScheduler scheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        birthdayService = new BirthdayService(friendRepository, notificationSender);
        scheduler = new BirthdayScheduler(birthdayService);

        System.out.println("\n==================================================");
        System.out.println(" INIZIALIZZAZIONE TEST SCHEDULER");
        System.out.println("==================================================\n");
    }

    @Test
    void testSchedulerWithRealBirthdayFlow() {

        System.out.println("--------------------------------------------------");
        System.out.println(" TEST: testSchedulerWithRealBirthdayFlow");
        System.out.println(" Descrizione: si simula una giornata con 2 festeggiati e 1 non festeggiato");
        System.out.println("--------------------------------------------------\n");

        // ---- ARRANGE ----

        LocalDate today = LocalDate.now();

        Friend f1 = new Friend("Mario", "Bianchi", today, "mario@test.com");
        Friend f2 = new Friend("Luca", "Rossi", today, "luca@test.com");
        Friend f3 = new Friend("Sara", "Verdi",
                LocalDate.of(1990, 5, 10), "sara@test.com");

        System.out.println("Mock repository popolato con i seguenti utenti:");
        System.out.println(" - Festeggiato 1: Mario Bianchi (" + today + ")");
        System.out.println(" - Festeggiato 2: Luca Rossi   (" + today + ")");
        System.out.println(" - NON festeggiata: Sara Verdi (10/05)\n");

        when(friendRepository.findAll()).thenReturn(List.of(f1, f2, f3));

        // ---- ACT ----

        System.out.println(">>> ESECUZIONE SCHEDULER <<<\n");

        scheduler.runBirthdayJob();

        // ---- ASSERT ----
        System.out.println("\n>>> VERIFICHE <<<\n");

        System.out.println("✓ Verifica: Mario ha ricevuto auguri di compleanno");
        verify(notificationSender).send(
                eq("mario@test.com"),
                eq("Happy birthday!"),
                contains("Mario")
        );

        System.out.println("✓ Verifica: Luca ha ricevuto auguri di compleanno");
        verify(notificationSender).send(
                eq("luca@test.com"),
                eq("Happy birthday!"),
                contains("Luca")
        );

        System.out.println("✓ Verifica: Sara ha ricevuto un reminder su entrambi");
        verify(notificationSender).send(
                eq("sara@test.com"),
                eq("Birthday Reminder"),
                argThat(
                        msg -> msg.contains("Mario Bianchi") &&
                                msg.contains("Luca Rossi")
                )
        );

        System.out.println("✓ Verifica: Mario ha ricevuto reminder su Luca");
        verify(notificationSender).send(
                eq("mario@test.com"),
                eq("Birthday Reminder"),
                contains("Luca Rossi")
        );

        System.out.println("✓ Verifica: Luca ha ricevuto reminder su Mario");
        verify(notificationSender).send(
                eq("luca@test.com"),
                eq("Birthday Reminder"),
                contains("Mario Bianchi")
        );

        System.out.println("\n>>> Verifica totale invii <<<");
        verify(notificationSender, times(5))
                .send(anyString(), anyString(), anyString());

        System.out.println("✓ Tutti gli invii corrispondono al flusso atteso.\n");

        System.out.println("==================================================");
        System.out.println(" TEST COMPLETATO CON SUCCESSO");
        System.out.println("==================================================\n");
    }
}
