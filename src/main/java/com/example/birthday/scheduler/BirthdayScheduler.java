package com.example.birthday.scheduler;

import com.example.birthday.service.BirthdayService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BirthdayScheduler {

    private final BirthdayService birthdayService;

    public BirthdayScheduler(BirthdayService birthdayService) {
        this.birthdayService = birthdayService;
    }

    /**
     * Esegue ogni giorno alle 08:00 del mattino.
     * Formato cron: secondi minuti ore giorno_mese mese giorno_settimana
     */
    @Scheduled(cron = "${scheduler.birthday.cron}")
    public void runBirthdayJob() {
        System.out.println("==================================================");
        System.out.println(" SCHEDULER: Avvio job giornaliero per invio auguri");
        System.out.println("==================================================");
        System.out.println("Ora di esecuzione: 08:00");

        birthdayService.sendBirthdayGreetings();

        System.out.println("Job completato.");
        System.out.println("==================================================\n");
    }
}
