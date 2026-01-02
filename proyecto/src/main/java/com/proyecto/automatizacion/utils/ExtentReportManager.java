package com.proyecto.automatizacion.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExtentReportManager {
    
    private static ExtentReports extent;
    private static ExtentTest test;
    private static String reportPath;
    
    public static void initReport() {
        if (extent == null) {
            // Crear nombre del reporte con timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            reportPath = "reports/Proyecto-Automatizacion-TestReport_" + timestamp + ".html";
            
            // Configurar el reporte
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setDocumentTitle("Proyecto Automatizacion - Test Automation Report");
            sparkReporter.config().setReportName("Login Module Test Results");
            sparkReporter.config().setTheme(Theme.STANDARD);
            
            // Inicializar Extent Reports
            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            
            // Información del sistema
            extent.setSystemInfo("Application", "Proyecto");
            extent.setSystemInfo("Module", "Automatizacion");
            extent.setSystemInfo("Environment", "Test");
            extent.setSystemInfo("Tester", "QA Team");
            extent.setSystemInfo("Browser", ConfigReader.getProperty("browser"));
        }
    }
    
    public static void createTest(String testName, String description) {
        test = extent.createTest(testName, description);
    }
    
    public static void logInfo(String message) {
        test.log(Status.INFO, message);
    }
    
    public static void logPass(String message) {
        test.log(Status.PASS, "✅ " + message);
    }
    
    public static void logFail(String message) {
        test.log(Status.FAIL, "❌ " + message);
    }
    
    public static void logSkip(String message) {
        test.log(Status.SKIP, "⏭️ " + message);
    }
    
    public static void addScreenshot(String screenshotPath) {
        test.addScreenCaptureFromPath(screenshotPath, "Screenshot");
    }
    
    public static void flushReport() {
        if (extent != null) {
            extent.flush();
        }
    }
    
    public static String getReportPath() {
        return reportPath;
    }
}