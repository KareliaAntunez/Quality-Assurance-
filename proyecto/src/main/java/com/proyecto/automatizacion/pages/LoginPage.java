package com.proyecto.automatizacion.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import java.time.Duration;

public class LoginPage {
    
    private final WebDriver driver;
    private final WebDriverWait wait;
    
    // Centralized XPaths
    private static final String LOGIN_FORM_BASE = "/html/body/app-root/div/app-auth-layout/app-auth-layout/app-login-page/div/div[2]/form/div[2]";
    private static final String RECOVERY_ELEMENTS_XPATH = "//input[@placeholder='Ingrese su correo electrónico'] | //button[contains(text(),'Enviar')]";
    private static final String PROFILE_BUTTON_XPATH = "/html/body/app-root/div/app-dashboard-layout/div/app-header/header/nav/div/div[2]/div/button";
    
    // Error validation patterns
    private static final String[] HARMFUL_CONTENT_PATTERNS = {
        "sql error", "mysql", "database error", "ora-", "postgresql"
    };
    
    private static final String[] SYSTEM_ERROR_PATTERNS = {
        "500 internal server error", "application error", "exception"
    };

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        PageFactory.initElements(driver, this);
    }
    
    // Form elements
    @FindBy(id = "userName")
    private WebElement usernameField;
    
    @FindBy(id = "password") 
    private WebElement passwordField;
    
    @FindBy(xpath = LOGIN_FORM_BASE + "/button")
    private WebElement loginButton;

    @FindBy(xpath = LOGIN_FORM_BASE + "/div[5]/small")
    private WebElement errorMessage;
    
    @FindBy(xpath = LOGIN_FORM_BASE + "/div[3]/div/button")
    private WebElement showPasswordIcon;

    @FindBy(xpath = "html/body/app-root/div/app-auth-layout/app-auth-layout/app-login-page/div/div[2]/form/div[2]/div[4]/button")
    private WebElement forgotPasswordLink;
    
    @FindBy(xpath = "//input[@placeholder='Captcha'] | //*[contains(text(),'captcha')] | //*[contains(@class,'captcha')]")
    private WebElement captchaElement;
    
    @FindBy(xpath = "//*[contains(text(),'bloqueado')] | //*[contains(text(),'intentos')] | //*[contains(text(),'blocked')]")
    private WebElement blockingMessage;

    @FindBy(xpath = PROFILE_BUTTON_XPATH)
    private WebElement profileButton;
    
    @FindBy(xpath = LOGIN_FORM_BASE + "/div[2]/div/small")
    private WebElement messageErrorUser;
    
    @FindBy(xpath = LOGIN_FORM_BASE + "/div[3]/div/div/small")
    private WebElement messageErrorPass;

    // Basic form actions
    public void enterUsername(String username) {
        fillField(usernameField, username);
    }
    
    public void enterPassword(String password) {
        fillField(passwordField, password);
    }
    
    public void clickLoginButton() {
        clickElement(loginButton);
    }
    
    public void clickShowPasswordIcon() {
        clickElement(showPasswordIcon);
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }
    
    // Validation methods
    public boolean isErrorMessageDisplayed() {
        return isElementDisplayed(errorMessage);
    }
    
    public String getErrorMessage() {
        return getElementText(errorMessage, "No se encontró mensaje de error");
    }
    
    public boolean isPasswordVisible() {
        try {
            return "text".equals(passwordField.getAttribute("type"));
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getUsernameValue() {
        return getFieldValue(usernameField);
    }
    
    public String getPasswordValue() {
        return getFieldValue(passwordField);
    }
    
    public boolean hasBlockingMechanism() {
        try {
            return captchaElement.isDisplayed() || blockingMessage.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    public boolean areElementsVisible() {
        try {
            return usernameField.isDisplayed() && 
                   passwordField.isDisplayed() && 
                   loginButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean areElementsClickable() {
        try {
            return usernameField.isEnabled() && 
                   passwordField.isEnabled() && 
                   loginButton.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean areFieldsAccessible() {
        return areElementsVisible() && areElementsClickable();
    }
    
    public boolean isTextLegible() {
        try {
            int minHeight = 20;
            return usernameField.getSize().height >= minHeight && 
                   passwordField.getSize().height >= minHeight && 
                   loginButton.getSize().height >= minHeight;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean hasDatabaseError() {
        return hasPageContentPattern(HARMFUL_CONTENT_PATTERNS);
    }
    
    public boolean hasSystemError() {
        try {
            String pageContent = getPageContent();
            return hasContentPattern(pageContent, SYSTEM_ERROR_PATTERNS) ||
                   driver.getTitle().toLowerCase().contains("error");
        } catch (Exception e) {
            return true;
        }
    }
    
    public boolean isLoginSuccessful() {
        return isElementDisplayed(profileButton);
    }

    public boolean respectsFieldLimits() {
        String usernameValue = getUsernameValue();
        String passwordValue = getPasswordValue();
        return usernameValue.length() <= 100 && passwordValue.length() <= 100;
    }
    
    public LoginResult getLoginResult() {
        try {
            Thread.sleep(1500);
            
            if (isErrorMessageDisplayed()) {
                return LoginResult.FAILED;
            }
            
            String currentUrl = driver.getCurrentUrl();
            if (!currentUrl.contains("login") || currentUrl.contains("dashboard")) {
                return LoginResult.SUCCESS;
            }
            
            return LoginResult.UNKNOWN;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return LoginResult.UNKNOWN;
        }
    }

    // Specific error message validation
    public boolean isAnyErrorMessageDisplayed() {
        return isUserErrorMessageDisplayed() || isPasswordErrorMessageDisplayed();
    }

    public boolean isUserErrorMessageDisplayed() {
        return isElementDisplayed(messageErrorUser);
    }

    public boolean isPasswordErrorMessageDisplayed() {
        return isElementDisplayed(messageErrorPass);
    }

    public boolean isErrorMessageDisplayed(String errorText) {
        try {
            return (isUserErrorMessageDisplayed() && messageErrorUser.getText().equals(errorText)) ||
                   (isPasswordErrorMessageDisplayed() && messageErrorPass.getText().equals(errorText));
        } catch (Exception e) {
            return false;
        }
    }

    // Password recovery
    public void clickForgotPasswordLink() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(forgotPasswordLink));
            forgotPasswordLink.click();
            Thread.sleep(1000);
        } catch (TimeoutException e) {
            throw new RuntimeException("No se pudo hacer clic en el enlace de recuperación");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean isRecoveryProcessStarted() {
        try {
            Thread.sleep(3000);
            String currentUrl = driver.getCurrentUrl();
            System.out.println("URL actual: " + currentUrl);
            
            return currentUrl.contains("password-recovery") || 
                   currentUrl.contains("recovery") ||
                   currentUrl.contains("forgot") ||
                   currentUrl.contains("reset");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public boolean isOnRecoveryPage() {
        try {
            wait.until(ExpectedConditions.urlContains("password-recovery"));
            String currentUrl = driver.getCurrentUrl();
            System.out.println("Verificando URL de recuperación: " + currentUrl);
            return currentUrl.contains("password-recovery");
        } catch (TimeoutException e) {
            String currentUrl = driver.getCurrentUrl();
            System.out.println("Timeout esperando URL de recuperación. URL actual: " + currentUrl);
            return currentUrl.contains("password-recovery") || currentUrl.contains("recovery");
        }
    }

    public boolean isRecoveryPageElementsVisible() {
        try {
            return !driver.findElements(By.xpath(RECOVERY_ELEMENTS_XPATH)).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    // Utility methods
    public void waitForPageLoad() {
        wait.until(ExpectedConditions.visibilityOf(usernameField));
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
    }
    
    public void clearAllFields() {
        try {
            usernameField.clear();
            passwordField.clear();
        } catch (Exception e) {
            System.err.println("Error limpiando campos: " + e.getMessage());
        }
    }
    
    public String getPageDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append("URL actual: ").append(driver.getCurrentUrl()).append("\n");
        info.append("Título: ").append(driver.getTitle()).append("\n");
        info.append("Error visible: ").append(isErrorMessageDisplayed()).append("\n");
        
        if (isErrorMessageDisplayed()) {
            info.append("Mensaje error: ").append(getErrorMessage()).append("\n");
        }
        
        info.append("Usuario valor: '").append(getUsernameValue()).append("'\n");
        info.append("Elementos visibles: ").append(areElementsVisible()).append("\n");
        
        return info.toString();
    }

    // Helper methods
    private void fillField(WebElement field, String value) {
        try {
            wait.until(ExpectedConditions.visibilityOf(field));
            field.clear();
            if (value != null && !value.isEmpty()) {
                field.sendKeys(value);
            }
        } catch (TimeoutException e) {
            System.err.println("Campo no encontrado: " + e.getMessage());
        }
    }

    private void clickElement(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
        } catch (TimeoutException e) {
            System.err.println("Elemento no clickeable: " + e.getMessage());
        }
    }

    private boolean isElementDisplayed(WebElement element) {
        try {
            return wait.until(ExpectedConditions.visibilityOf(element)).isDisplayed();
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    private String getElementText(WebElement element, String defaultText) {
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
            return element.getText();
        } catch (TimeoutException e) {
            return defaultText;
        }
    }

    private String getFieldValue(WebElement field) {
        try {
            return field.getAttribute("value");
        } catch (Exception e) {
            return "";
        }
    }

    private String getPageContent() {
        return (driver.getPageSource() + " " + driver.getTitle()).toLowerCase();
    }

    private boolean hasPageContentPattern(String[] patterns) {
        String pageContent = getPageContent();
        return hasContentPattern(pageContent, patterns);
    }

    private boolean hasContentPattern(String content, String[] patterns) {
        for (String pattern : patterns) {
            if (content.contains(pattern)) {
                return true;
            }
        }
        return false;
    }

    // Enum for login results
    public enum LoginResult {
        SUCCESS, FAILED, UNKNOWN
    }
}