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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CovenantE2ETest {

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
    void viewCovenantList_shouldDisplayCovenants() {
        // Naviga alla pagina dei covenant
        driver.get("http://localhost:" + port + "/covenants");
        
        // Verifica che la lista dei covenant sia visualizzata
        WebElement covenantList = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("covenant-list")));
        assertTrue(covenantList.isDisplayed());
        
        // Verifica che ci siano elementi nella lista
        List<WebElement> covenantItems = driver.findElements(By.className("covenant-item"));
        assertFalse(covenantItems.isEmpty());
    }

    @Test
    void createCovenant_shouldAddNewCovenant() {
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
        
        // Compila il form
        WebElement nameInput = covenantForm.findElement(By.id("covenant-name"));
        WebElement descriptionInput = covenantForm.findElement(By.id("covenant-description"));
        WebElement thresholdInput = covenantForm.findElement(By.id("covenant-threshold"));
        WebElement frequencyInput = covenantForm.findElement(By.id("covenant-frequency"));
        WebElement nextReviewDateInput = covenantForm.findElement(By.id("covenant-next-review-date"));
        WebElement saveButton = covenantForm.findElement(By.id("save-covenant-button"));
        
        String newCovenantName = "Interest Coverage Ratio Test E2E";
        nameInput.sendKeys(newCovenantName);
        descriptionInput.sendKeys("Il rapporto tra EBITDA e interessi deve essere superiore a 2.5");
        thresholdInput.sendKeys("2.5");
        frequencyInput.sendKeys("Annuale");
        
        // Imposta la data di revisione a 12 mesi da oggi
        LocalDate nextReviewDate = LocalDate.now().plusMonths(12);
        String formattedDate = nextReviewDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        nextReviewDateInput.sendKeys(formattedDate);
        
        saveButton.click();
        
        // Attendi che la lista dei covenant sia aggiornata
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'covenant-item') and contains(., '" + newCovenantName + "')]")));
        
        // Verifica che il nuovo covenant sia presente nella lista
        List<WebElement> covenantItems = driver.findElements(By.className("covenant-item"));
        boolean covenantFound = false;
        for (WebElement item : covenantItems) {
            if (item.getText().contains(newCovenantName)) {
                covenantFound = true;
                break;
            }
        }
        assertTrue(covenantFound);
    }

    @Test
    void viewCovenantDetails_shouldDisplayCovenantInfo() {
        // Naviga alla pagina dei covenant
        driver.get("http://localhost:" + port + "/covenants");
        
        // Attendi che la lista dei covenant sia visualizzata
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("covenant-list")));
        
        // Clicca sul primo covenant nella lista
        WebElement firstCovenant = wait.until(ExpectedConditions.elementToBeClickable(By.className("covenant-item")));
        String covenantName = firstCovenant.findElement(By.className("covenant-name")).getText();
        firstCovenant.click();
        
        // Attendi che la pagina di dettaglio sia caricata
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("covenant-details")));
        
        // Verifica che i dettagli del covenant siano visualizzati
        WebElement detailsName = driver.findElement(By.id("covenant-detail-name"));
        assertEquals(covenantName, detailsName.getText());
        
        // Verifica che la sezione dei risultati di monitoraggio sia presente
        assertTrue(driver.findElement(By.id("monitoring-results-section")).isDisplayed());
    }

    @Test
    void addMonitoringResult_shouldRecordNewResult() {
        // Naviga alla pagina dei covenant
        driver.get("http://localhost:" + port + "/covenants");
        
        // Attendi che la lista dei covenant sia visualizzata
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("covenant-list")));
        
        // Clicca sul primo covenant nella lista
        WebElement firstCovenant = wait.until(ExpectedConditions.elementToBeClickable(By.className("covenant-item")));
        firstCovenant.click();
        
        // Attendi che la pagina di dettaglio sia caricata
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("covenant-details")));
        
        // Clicca sul pulsante per aggiungere un nuovo risultato di monitoraggio
        WebElement addResultButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-monitoring-result-button")));
        addResultButton.click();
        
        // Attendi che il form sia visualizzato
        WebElement monitoringForm = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("monitoring-form")));
        
        // Compila il form
        WebElement actualValueInput = monitoringForm.findElement(By.id("actual-value"));
        WebElement statusSelect = monitoringForm.findElement(By.id("status"));
        WebElement notesInput = monitoringForm.findElement(By.id("notes"));
        WebElement reviewedByInput = monitoringForm.findElement(By.id("reviewed-by"));
        WebElement saveButton = monitoringForm.findElement(By.id("save-monitoring-result-button"));
        
        actualValueInput.sendKeys("1.8");
        // Seleziona "Compliant" dallo stato
        statusSelect.sendKeys("Compliant");
        notesInput.sendKeys("Il covenant è rispettato con un buon margine");
        reviewedByInput.sendKeys("Test E2E");
        saveButton.click();
        
        // Attendi che la lista dei risultati sia aggiornata
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'monitoring-result-item') and contains(., '1.8')]")));
        
        // Verifica che il nuovo risultato sia presente nella lista
        List<WebElement> resultItems = driver.findElements(By.className("monitoring-result-item"));
        boolean resultFound = false;
        for (WebElement item : resultItems) {
            if (item.getText().contains("1.8") && item.getText().contains("Compliant")) {
                resultFound = true;
                break;
            }
        }
        assertTrue(resultFound);
    }

    @Test
    void editCovenant_shouldUpdateCovenantInfo() {
        // Naviga alla pagina dei covenant
        driver.get("http://localhost:" + port + "/covenants");
        
        // Attendi che la lista dei covenant sia visualizzata
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("covenant-list")));
        
        // Clicca sul primo covenant nella lista
        WebElement firstCovenant = wait.until(ExpectedConditions.elementToBeClickable(By.className("covenant-item")));
        firstCovenant.click();
        
        // Attendi che la pagina di dettaglio sia caricata
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("covenant-details")));
        
        // Clicca sul pulsante di modifica
        WebElement editButton = driver.findElement(By.id("edit-covenant-button"));
        editButton.click();
        
        // Attendi che il form di modifica sia visualizzato
        WebElement covenantForm = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("covenant-form")));
        
        // Modifica la soglia del covenant
        WebElement thresholdInput = covenantForm.findElement(By.id("covenant-threshold"));
        thresholdInput.clear();
        thresholdInput.sendKeys("1.5");
        
        // Salva le modifiche
        WebElement saveButton = covenantForm.findElement(By.id("save-covenant-button"));
        saveButton.click();
        
        // Attendi che la pagina di dettaglio sia aggiornata
        wait.until(ExpectedConditions.textToBe(By.id("covenant-detail-threshold"), "1.5"));
        
        // Verifica che la soglia sia stata aggiornata
        WebElement detailsThreshold = driver.findElement(By.id("covenant-detail-threshold"));
        assertEquals("1.5", detailsThreshold.getText());
    }
}
