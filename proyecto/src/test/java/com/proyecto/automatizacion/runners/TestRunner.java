/* Configura ejecución: Dónde están features y steps
Filtra por tags: Ejecutar solo casos críticos
Genera reportes: HTML, JSON, JUnit
Punto de entrada: Para ejecutar todos los tests
Control de ejecución: Qué correr y cómo
Integración CI/CD: Punto único de entrada
Reportes automáticos
Flexibilidad: Cambiar configuración sin código
*/

package com.proyecto.automatizacion.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"com.proyecto.automatizacion.stepDefinitions", "com.proyecto.automatizacion.hooks"},
    plugin = {
        "pretty",
        "html:target/cucumber-reports/cucumber-html-report.html",
        "json:target/cucumber-reports/cucumber.json",
        "junit:target/cucumber-reports/cucumber.xml",
        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
    },
    tags = "@login"
)
public class TestRunner {
}
