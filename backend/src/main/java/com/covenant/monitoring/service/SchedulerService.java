package com.covenant.monitoring.service;

import com.covenant.monitoring.model.Covenant;
import com.covenant.monitoring.model.Notification;
import com.covenant.monitoring.model.User;
import com.covenant.monitoring.repository.CovenantRepository;
import com.covenant.monitoring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SchedulerService {

    private final CovenantRepository covenantRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Autowired
    public SchedulerService(CovenantRepository covenantRepository, 
                           UserRepository userRepository,
                           NotificationService notificationService) {
        this.covenantRepository = covenantRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    // Esegue ogni giorno alle 9:00
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendMonitoringReminders() {
        // Trova i covenant che non sono stati monitorati negli ultimi 3 mesi
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<Covenant> dueCovenants = covenantRepository.findByLastMonitoringDateBefore(threeMonthsAgo);
        
        if (!dueCovenants.isEmpty()) {
            // Trova gli utenti con ruolo Finance per inviare le notifiche
            List<User> financeUsers = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ROLE_FINANCE_HOLDING") || 
                              role.getName().equals("ROLE_FINANCE_COUNTRY")))
                .toList();
            
            for (User user : financeUsers) {
                notificationService.createNotification(
                    user,
                    "Promemoria Monitoraggio Covenant",
                    "Ci sono " + dueCovenants.size() + " covenant che richiedono monitoraggio."
                );
            }
        }
    }

    // Esegue 5 giorni dopo la fine di ogni trimestre
    @Scheduled(cron = "0 0 9 5 1,4,7,10 ?")
    public void sendQuarterlyMonitoringReminders() {
        // Trova tutti gli utenti con ruolo Finance per inviare le notifiche
        List<User> financeUsers = userRepository.findAll().stream()
            .filter(user -> user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_FINANCE_HOLDING") || 
                          role.getName().equals("ROLE_FINANCE_COUNTRY")))
            .toList();
        
        for (User user : financeUsers) {
            notificationService.createNotification(
                user,
                "Monitoraggio Trimestrale Covenant",
                "È necessario completare il monitoraggio trimestrale dei covenant entro 5 giorni."
            );
        }
    }

    // Esegue ogni giorno alle 10:00 per inviare reminder per i covenant non monitorati
    @Scheduled(cron = "0 0 10 * * ?")
    public void sendReminderForUnmonitoredCovenants() {
        // Trova i covenant con status "GIALLO" che potrebbero non essere rispettati nei prossimi 12 mesi
        List<Covenant> yellowCovenants = covenantRepository.findByStatus("GIALLO");
        
        if (!yellowCovenants.isEmpty()) {
            // Trova gli utenti con ruolo Finance e Admin per inviare le notifiche
            List<User> users = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ROLE_FINANCE_HOLDING") || 
                              role.getName().equals("ROLE_FINANCE_COUNTRY") ||
                              role.getName().equals("ROLE_ADMIN_HOLDING") ||
                              role.getName().equals("ROLE_ADMIN_COUNTRY")))
                .toList();
            
            for (User user : users) {
                notificationService.createNotification(
                    user,
                    "Covenant a Rischio",
                    "Ci sono " + yellowCovenants.size() + " covenant con status GIALLO che potrebbero non essere rispettati nei prossimi 12 mesi."
                );
            }
        }
        
        // Trova i covenant con status "ROSSO" che non sono rispettati
        List<Covenant> redCovenants = covenantRepository.findByStatus("ROSSO");
        
        if (!redCovenants.isEmpty()) {
            // Trova tutti gli utenti con ruoli rilevanti per inviare le notifiche
            List<User> users = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ROLE_FINANCE_HOLDING") || 
                              role.getName().equals("ROLE_FINANCE_COUNTRY") ||
                              role.getName().equals("ROLE_ADMIN_HOLDING") ||
                              role.getName().equals("ROLE_ADMIN_COUNTRY") ||
                              role.getName().equals("ROLE_LEGAL_HOLDING") ||
                              role.getName().equals("ROLE_LEGAL_COUNTRY")))
                .toList();
            
            for (User user : users) {
                notificationService.createNotification(
                    user,
                    "URGENTE: Covenant non Rispettati",
                    "Ci sono " + redCovenants.size() + " covenant con status ROSSO che non sono rispettati. È necessaria un'azione immediata."
                );
            }
        }
    }
}
