package com.proyecto.automatizacion.hooks;

import io.cucumber.java.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import com.proyecto.automatizacion.utils.DriverManager;
import com.proyecto.automatizacion.utils.ExtentReportManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Hooks {

    @BeforeAll
    public static void setUpReport() {
        ExtentReportManager.initReport();
        System.out.println("Extent Report inicializado");
    }

    @Before
    public void setUp(Scenario scenario) {
        System.out.println("Iniciando escenario: " + scenario.getName());

        // 1) Crear test en Extent
        ExtentReportManager.createTest(scenario.getName(), "Escenario de prueba: " + scenario.getName());
        ExtentReportManager.logInfo("Iniciando ejecución del escenario");

        // 2) Iniciar WebDriver y guardarlo en DriverManager
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        DriverManager.setDriver(driver);
        ExtentReportManager.logInfo("Driver iniciado y maximizado");
    }

    @After
    public void tearDown(Scenario scenario) {
        WebDriver driver = DriverManager.getDriver();

        // 3) Tomar screenshot SIEMPRE (éxito o fallo)
        String screenshotPath = takeScreenshot(driver, scenario.getName());

        // 4) Log de estado
        if (scenario.isFailed()) {
            ExtentReportManager.logFail("Escenario FALLÓ: " + scenario.getName());
        } else {
            ExtentReportManager.logPass("Escenario EXITOSO: " + scenario.getName());
        }

        // 5) Adjuntar screenshot a Extent y (opcional) a Cucumber
        if (screenshotPath != null) {
            ExtentReportManager.addScreenshot(screenshotPath); // Extent
            ExtentReportManager.logInfo("Screenshot guardado en: " + screenshotPath);

            // Adjuntar a reporte de Cucumber (consola/HTML plugins)
            if (driver != null) {
                byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                scenario.attach(bytes, "image/png", "Screenshot - " + (scenario.isFailed() ? "FAILED" : "PASSED"));
            }
        }

        // 6) Cerrar navegador
        DriverManager.quit();
        System.out.println("Finalizando escenario: " + scenario.getName());

        // 7) Guardar/actualizar reporte
        ExtentReportManager.flushReport();
    }

    @AfterAll
    public static void tearDownReport() {
        // Si ya haces flush en @After, aquí solo avisas la ruta
        System.out.println("Reporte generado en: " + ExtentReportManager.getReportPath());
    }

    // ----------------- Helpers -----------------

    private String takeScreenshot(WebDriver driver, String scenarioName) {
        if (driver == null) return null;

        try {
            // Carpeta: reports/screenshots
            Files.createDirectories(Paths.get("reports", "screenshots"));

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String safe = scenarioName.replaceAll("[^a-zA-Z0-9-_\\. ]", "_");
            String path = "reports/screenshots/" + safe + "_" + timestamp + ".png";

            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File dst = new File(path);
            FileUtils.copyFile(src, dst);

            return path.replace("\\", "/");
        } catch (IOException e) {
            System.err.println("Error tomando screenshot: " + e.getMessage());
            return null;
        }
    }
}
