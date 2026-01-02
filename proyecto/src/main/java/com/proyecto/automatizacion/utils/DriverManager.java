package com.proyecto.automatizacion.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

public class DriverManager {

    // Singleton (una instancia por ejecución). Si luego ejecutas en paralelo,
    // conviene migrar a ThreadLocal<WebDriver>.
    private static WebDriver driver;

    /** Devuelve el driver existente o lo inicializa según configuración. */
    public static WebDriver getDriver() {
        if (driver == null) {
            initializeDriver();
        }
        return driver;
    }

    /** Permite que otros componentes (Hooks) inyecten un driver ya creado. */
    public static void setDriver(WebDriver externalDriver) {
        driver = externalDriver;
    }

    /** Inicializa el driver leyendo config.properties (browser, headless, implicit.wait). */
    private static void initializeDriver() {
        String browser = defaultIfBlank(ConfigReader.getProperty("browser"), "chrome");
        boolean headless = Boolean.parseBoolean(defaultIfBlank(ConfigReader.getProperty("headless"), "false"));
        int implicitWaitSec = parseIntOrDefault(ConfigReader.getProperty("implicit.wait"), 5);
        int pageLoadTimeoutSec = parseIntOrDefault(ConfigReader.getProperty("page.load.timeout"), 30);
        int scriptTimeoutSec = parseIntOrDefault(ConfigReader.getProperty("script.timeout"), 30);

        switch (browser.toLowerCase()) {
            case "chrome": {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    // headless moderno
                    options.addArguments("--headless=new");
                }
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                // Especificar la ubicación exacta de Chrome
                options.setBinary("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
                driver = new ChromeDriver(options);
                break;
            }
            case "firefox": {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }
                driver = new FirefoxDriver(options);
                break;
            }
            default:
                throw new IllegalArgumentException("Browser no soportado: " + browser);
        }

        // Configuraciones comunes
        try {
            driver.manage().window().maximize();
        } catch (Exception ignored) {} // por si el SO no lo permite

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWaitSec));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeoutSec));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(scriptTimeoutSec));
    }

    /** Cierra el navegador y limpia la instancia singleton. */
    public static void quit() {
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                driver = null;
            }
        }
    }

    /** Alias para compatibilidad con llamadas existentes. */
    public static void quitDriver() {
        quit();
    }

    // ----------------- helpers -----------------
    private static String defaultIfBlank(String value, String dflt) {
        return (value == null || value.trim().isEmpty()) ? dflt : value.trim();
    }

    private static int parseIntOrDefault(String value, int dflt) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return dflt;
        }
    }
}
