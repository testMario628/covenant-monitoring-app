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
public class ContractE2ETest {

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
    void viewContractList_shouldDisplayContracts() {
        // Naviga alla pagina dei contratti
        driver.get("http://localhost:" + port + "/contracts");
        
        // Verifica che la lista dei contratti sia visualizzata
        WebElement contractList = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("contract-list")));
        assertTrue(contractList.isDisplayed());
        
        // Verifica che ci siano elementi nella lista
        List<WebElement> contractItems = driver.findElements(By.className("contract-item"));
        assertFalse(contractItems.isEmpty());
    }

    @Test
    void createContract_shouldAddNewContract() {
        // Naviga alla pagina dei contratti
        driver.get("http://localhost:" + port + "/contracts");
        
        // Clicca sul pulsante per aggiungere un nuovo contratto
        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-contract-button")));
        addButton.click();
        
        // Attendi che il form sia visualizzato
        WebElement contractForm = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("contract-form")));
        
        // Compila il form
        WebElement titleInput = contractForm.findElement(By.id("contract-title"));
        WebElement legalEntityInput = contractForm.findElement(By.id("contract-legal-entity"));
        WebElement countryInput = contractForm.findElement(By.id("contract-country"));
        WebElement saveButton = contractForm.findElement(By.id("save-contract-button"));
        
        String newContractTitle = "Nuovo Finanziamento Test E2E";
        titleInput.sendKeys(newContractTitle);
        legalEntityInput.sendKeys("Enel S.p.A.");
        countryInput.sendKeys("Italia");
        saveButton.click();
        
        // Attendi che la lista dei contratti sia aggiornata
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'contract-item') and contains(., '" + newContractTitle + "')]")));
        
        // Verifica che il nuovo contratto sia presente nella lista
        List<WebElement> contractItems = driver.findElements(By.className("contract-item"));
        boolean contractFound = false;
        for (WebElement item : contractItems) {
            if (item.getText().contains(newContractTitle)) {
                contractFound = true;
                break;
            }
        }
        assertTrue(contractFound);
    }

    @Test
    void viewContractDetails_shouldDisplayContractInfo() {
        // Naviga alla pagina dei contratti
        driver.get("http://localhost:" + port + "/contracts");
        
        // Attendi che la lista dei contratti sia visualizzata
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("contract-list")));
        
        // Clicca sul primo contratto nella lista
        WebElement firstContract = wait.until(ExpectedConditions.elementToBeClickable(By.className("contract-item")));
        String contractTitle = firstContract.findElement(By.className("contract-title")).getText();
        firstContract.click();
        
        // Attendi che la pagina di dettaglio sia caricata
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("contract-details")));
        
        // Verifica che i dettagli del contratto siano visualizzati
        WebElement detailsTitle = driver.findElement(By.id("contract-detail-title"));
        assertEquals(contractTitle, detailsTitle.getText());
        
        // Verifica che la sezione dei covenant associati sia presente
        assertTrue(driver.findElement(By.id("covenant-section")).isDisplayed());
    }

    @Test
    void editContract_shouldUpdateContractInfo() {
        // Naviga alla pagina dei contratti
        driver.get("http://localhost:" + port + "/contracts");
        
        // Attendi che la lista dei contratti sia visualizzata
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("contract-list")));
        
        // Clicca sul primo contratto nella lista
        WebElement firstContract = wait.until(ExpectedConditions.elementToBeClickable(By.className("contract-item")));
        firstContract.click();
        
        // Attendi che la pagina di dettaglio sia caricata
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("contract-details")));
        
        // Clicca sul pulsante di modifica
        WebElement editButton = driver.findElement(By.id("edit-contract-button"));
        editButton.click();
        
        // Attendi che il form di modifica sia visualizzato
        WebElement contractForm = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("contract-form")));
        
        // Modifica il titolo del contratto
        WebElement titleInput = contractForm.findElement(By.id("contract-title"));
        titleInput.clear();
        String updatedTitle = "Contratto Aggiornato E2E";
        titleInput.sendKeys(updatedTitle);
        
        // Salva le modifiche
        WebElement saveButton = contractForm.findElement(By.id("save-contract-button"));
        saveButton.click();
        
        // Attendi che la pagina di dettaglio sia aggiornata
        wait.until(ExpectedConditions.textToBe(By.id("contract-detail-title"), updatedTitle));
        
        // Verifica che il titolo sia stato aggiornato
        WebElement detailsTitle = driver.findElement(By.id("contract-detail-title"));
        assertEquals(updatedTitle, detailsTitle.getText());
    }

    @Test
    void deleteContract_shouldRemoveContractFromList() {
        // Naviga alla pagina dei contratti
        driver.get("http://localhost:" + port + "/contracts");
        
        // Attendi che la lista dei contratti sia visualizzata
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("contract-list")));
        
        // Conta il numero iniziale di contratti
        List<WebElement> initialContractItems = driver.findElements(By.className("contract-item"));
        int initialCount = initialContractItems.size();
        
        // Clicca sul primo contratto nella lista
        WebElement firstContract = wait.until(ExpectedConditions.elementToBeClickable(By.className("contract-item")));
        firstContract.click();
        
        // Attendi che la pagina di dettaglio sia caricata
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("contract-details")));
        
        // Clicca sul pulsante di eliminazione
        WebElement deleteButton = driver.findElement(By.id("delete-contract-button"));
        deleteButton.click();
        
        // Conferma l'eliminazione nella finestra di dialogo
        WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("confirm-delete-button")));
        confirmButton.click();
        
        // Attendi che la lista dei contratti sia aggiornata
        wait.until(ExpectedConditions.urlContains("/contracts"));
        
        // Verifica che il numero di contratti sia diminuito
        List<WebElement> updatedContractItems = driver.findElements(By.className("contract-item"));
        assertEquals(initialCount - 1, updatedContractItems.size());
    }
}
