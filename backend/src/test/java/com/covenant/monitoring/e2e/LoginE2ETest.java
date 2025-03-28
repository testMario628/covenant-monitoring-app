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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LoginE2ETest {

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
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void loginSuccess_shouldRedirectToDashboard() {
        // Naviga alla pagina di login
        driver.get("http://localhost:" + port + "/login");
        
        // Inserisci credenziali valide
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("login-button"));
        
        usernameInput.sendKeys("admin@example.com");
        passwordInput.sendKeys("password123");
        loginButton.click();
        
        // Verifica che l'utente sia stato reindirizzato alla dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        assertEquals("http://localhost:" + port + "/dashboard", driver.getCurrentUrl());
        
        // Verifica che elementi della dashboard siano visibili
        assertTrue(driver.findElement(By.id("welcome-message")).isDisplayed());
        assertTrue(driver.findElement(By.id("covenant-summary")).isDisplayed());
    }

    @Test
    void loginFailure_shouldShowErrorMessage() {
        // Naviga alla pagina di login
        driver.get("http://localhost:" + port + "/login");
        
        // Inserisci credenziali non valide
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("login-button"));
        
        usernameInput.sendKeys("wrong@example.com");
        passwordInput.sendKeys("wrongpassword");
        loginButton.click();
        
        // Verifica che appaia un messaggio di errore
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("error-message")));
        assertTrue(errorMessage.isDisplayed());
        assertTrue(errorMessage.getText().contains("Credenziali non valide"));
        
        // Verifica che l'utente rimanga nella pagina di login
        assertTrue(driver.getCurrentUrl().contains("/login"));
    }

    @Test
    void logout_shouldRedirectToLoginPage() {
        // Prima effettua il login
        driver.get("http://localhost:" + port + "/login");
        
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("login-button"));
        
        usernameInput.sendKeys("admin@example.com");
        passwordInput.sendKeys("password123");
        loginButton.click();
        
        // Verifica che l'utente sia stato reindirizzato alla dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        
        // Effettua il logout
        WebElement userMenu = wait.until(ExpectedConditions.elementToBeClickable(By.id("user-menu")));
        userMenu.click();
        
        WebElement logoutButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("logout-button")));
        logoutButton.click();
        
        // Verifica che l'utente sia stato reindirizzato alla pagina di login
        wait.until(ExpectedConditions.urlContains("/login"));
        assertEquals("http://localhost:" + port + "/login", driver.getCurrentUrl());
    }
}
