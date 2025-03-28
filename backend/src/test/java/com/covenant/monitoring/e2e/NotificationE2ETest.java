package com.covenant.monitoring.e2e;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class NotificationE2ETest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // Effettua il login prima di ogni test
        login();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    private void login() {
        driver.get("http://localhost:" + port + "/login");
        
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("login-button"));
        
        usernameInput.sendKeys("admin@example.com");
        passwordInput.sendKeys("password123");
        loginButton.click();
        
        // Attendi che la dashboard sia caricata
        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    @Test
    void viewNotificationList_shouldDisplayNotifications() {
        // Naviga alla pagina delle notifiche
        driver.get("http://localhost:" + port + "/notifications");
        
        // Verifica che la lista delle notifiche sia visualizzata
        WebElement notificationList = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notification-list")));
        assertTrue(notificationList.isDisplayed());
    }

    @Test
    void markNotificationAsRead_shouldUpdateNotificationStatus() {
        // Naviga alla pagina delle notifiche
        driver.get("http://localhost:" + port + "/notifications");
        
        // Attendi che la lista delle notifiche sia visualizzata
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notification-list")));
        
        // Trova una notifica non letta
        List<WebElement> unreadNotifications = driver.findElements(By.cssSelector(".notification-item.unread"));
        
        // Se non ci sono notifiche non lette, il test viene saltato
        if (unreadNotifications.isEmpty()) {
            return;
        }
        
        WebElement unreadNotification = unreadNotifications.get(0);
        String notificationText = unreadNotification.getText();
        
        // Clicca sulla notifica per segnarla come letta
        unreadNotification.click();
        
        // Attendi che la notifica sia segnata come letta
        wait.until(ExpectedConditions.invisibilityOf(unreadNotification));
        
        // Verifica che la notifica non sia più nella lista delle non lette
        List<WebElement> updatedUnreadNotifications = driver.findElements(By.cssSelector(".notification-item.unread"));
        boolean notificationStillUnread = false;
        for (WebElement notification : updatedUnreadNotifications) {
            if (notification.getText().equals(notificationText)) {
                notificationStillUnread = true;
                break;
            }
        }
        assertFalse(notificationStillUnread);
    }

    @Test
    void filterNotificationsByType_shouldShowFilteredResults() {
        // Naviga alla pagina delle notifiche
        driver.get("http://localhost:" + port + "/notifications");
        
        // Attendi che la lista delle notifiche sia visualizzata
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notification-list")));
        
        // Conta il numero iniziale di notifiche
        List<WebElement> initialNotifications = driver.findElements(By.className("notification-item"));
        int initialCount = initialNotifications.size();
        
        // Seleziona il filtro per tipo "Warning"
        WebElement typeFilter = driver.findElement(By.id("notification-type-filter"));
        typeFilter.sendKeys("Warning");
        
        // Attendi che la lista sia filtrata
        wait.until(ExpectedConditions.or(
            ExpectedConditions.numberOfElementsToBeLessThan(By.className("notification-item"), initialCount),
            ExpectedConditions.textToBe(By.id("no-notifications-message"), "Nessuna notifica trovata")
        ));
        
        // Verifica che tutte le notifiche visualizzate siano di tipo "Warning"
        List<WebElement> filteredNotifications = driver.findElements(By.className("notification-item"));
        for (WebElement notification : filteredNotifications) {
            assertTrue(notification.findElement(By.className("notification-type")).getText().contains("Warning"));
        }
    }

    @Test
    void notificationBadge_shouldShowCorrectCount() {
        // Naviga alla dashboard
        driver.get("http://localhost:" + port + "/dashboard");
        
        // Attendi che la dashboard sia caricata
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dashboard-content")));
        
        // Verifica che il badge delle notifiche sia visibile
        WebElement notificationBadge = driver.findElement(By.id("notification-badge"));
        assertTrue(notificationBadge.isDisplayed());
        
        // Ottieni il conteggio dal badge
        int badgeCount = Integer.parseInt(notificationBadge.getText());
        
        // Naviga alla pagina delle notifiche
        driver.get("http://localhost:" + port + "/notifications");
        
        // Attendi che la lista delle notifiche sia visualizzata
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notification-list")));
        
        // Conta il numero di notifiche non lette
        List<WebElement> unreadNotifications = driver.findElements(By.cssSelector(".notification-item.unread"));
        
        // Verifica che il conteggio del badge corrisponda al numero di notifiche non lette
        assertEquals(unreadNotifications.size(), badgeCount);
    }

    @Test
    void createNotification_shouldAddNewNotification() {
        // Questo test simula la creazione di una notifica tramite un'azione che genera notifiche
        // Ad esempio, creiamo un covenant con una data di revisione ravvicinata
        
        // Prima naviga alla pagina dei contratti per selezionare un contratto
        driver.get("http://localhost:" + port + "/contracts");
        
        // Attendi che la lista dei contratti sia visualizzata
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("contract-list")));
        
        // Clicca sul primo contratto nella lista
        WebElement firstContract = wait.until(ExpectedConditions.elementToBeClickable(By.className("contract-item")));
        firstContract.click();
        
        // Attendi che la pagina di dettaglio sia caricata
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("contract-details")));
        
        // Clicca sul pulsante per aggiungere un nuovo covenant
        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-covenant-button")));
        addButton.click();
        
        // Attendi che il form sia visualizzato
        WebElement covenantForm = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("covenant-form")));
        
        // Compila il form con una data di revisione ravvicinata (domani)
        WebElement nameInput = covenantForm.findElement(By.id("covenant-name"));
        WebElement descriptionInput = covenantForm.findElement(By.id("covenant-description"));
        WebElement thresholdInput = covenantForm.findElement(By.id("covenant-threshold"));
        WebElement frequencyInput = covenantForm.findElement(By.id("covenant-frequency"));
        WebElement nextReviewDateInput = covenantForm.findElement(By.id("covenant-next-review-date"));
        WebElement saveButton = covenantForm.findElement(By.id("save-covenant-button"));
        
        nameInput.sendKeys("Covenant con scadenza ravvicinata");
        descriptionInput.sendKeys("Questo covenant genererà una notifica");
        thresholdInput.sendKeys("2.0");
        frequencyInput.sendKeys("Mensile");
        
        // Imposta la data di revisione a domani
        nextReviewDateInput.sendKeys("01/01/2023"); // Data passata per generare notifica
        
        saveButton.click();
        
        // Naviga alla pagina delle notifiche
        driver.get("http://localhost:" + port + "/notifications");
        
        // Attendi che la lista delle notifiche sia visualizzata
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notification-list")));
        
        // Verifica che ci sia una notifica relativa al covenant appena creato
        List<WebElement> notifications = driver.findElements(By.className("notification-item"));
        boolean notificationFound = false;
        for (WebElement notification : notifications) {
            if (notification.getText().contains("Covenant con scadenza ravvicinata")) {
                notificationFound = true;
                break;
            }
        }
        assertTrue(notificationFound);
    }
}
