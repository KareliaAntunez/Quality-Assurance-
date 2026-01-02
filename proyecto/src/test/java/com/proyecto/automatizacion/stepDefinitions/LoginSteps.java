package com.proyecto.automatizacion.stepDefinitions;

import static org.junit.Assert.assertEquals;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.proyecto.automatizacion.utils.DriverManager;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LoginSteps {
    WebDriver driver = DriverManager.getDriver();

//login 
    @Given("que el usuario abre la página de Swag Labs")
    public void abrirPagina() {
        driver.get("https://www.saucedemo.com/v1/");
    }

    @When("ingresa el usuario {string} y la contraseña {string}")
    public void ingresarCredenciales(String usuario, String password) {
        driver.findElement(By.id("user-name")).sendKeys(usuario);
        driver.findElement(By.id("password")).sendKeys(password);
    }

    @When("hace clic en el botón de login")
    public void clickLogin() {
        driver.findElement(By.id("login-button")).click();
    }

    @Then("debería ver la página de productos con el título {string}")
    public void validarLogin(String tituloEsperado) {
        String titulo = driver.findElement(By.className("product_label")).getText();
        assertEquals(tituloEsperado, titulo);
    }

//loginBlock
    @Then("debería ver el error {string}")
    public void validarLoginBlock(String errorEsperado) {
        String error = driver.findElement(By.cssSelector("h3[data-test='error']")).getText();
        assertEquals(errorEsperado, error);
    }

//AddCart
    @Then("debería agregar al carrito {string}")
    public void elegirArticulo(String articuloComprado) {
        String articulo = driver.findElement(By.xpath("//div[@class='inventory_item_name' and text()='" + articuloComprado + "']")).getText();
        assertEquals(articuloComprado, articulo);
    }

    @When("hace clic en el botón de Add to cart para {string}")
    public void clickAddCartParaProducto(String producto) {
        driver.findElement(By.xpath("//div[@class='inventory_item_name' and text()='" + producto + "']/ancestor::div[@class='inventory_item_label']/following-sibling::div[@class='pricebar']/button")).click();
    }

    @Then("debería ver {int} productos en el carrito")
    public void verificarCantidadCarrito(int cantidadEsperada) {
        String cantidad = driver.findElement(By.cssSelector(".shopping_cart_badge")).getText();
        assertEquals(String.valueOf(cantidadEsperada), cantidad);
    }

//Checkout
    @When("hace clic en el botón de carrito")
    public void clickCarrito() {
        driver.findElement(By.cssSelector("div#shopping_cart_container a.shopping_cart_link")).click();
    }

    @Then("debería ver la página del carrito con el título {string}")
    public void verificarTituloCarrito(String tituloEsperado) {
        String titulo = driver.findElement(By.cssSelector("div.subheader")).getText();
        assertEquals(tituloEsperado, titulo);
    }

    @When("hace clic en el botón de checkout")
    public void clickCheckout() {
        driver.findElement(By.cssSelector("a.btn_action.checkout_button")).click();
    }

    @Then("debería ver la página del checkout con el título {string}")
    public void verificarTituloCheckout(String tituloEsperado) {
        String titulo = driver.findElement(By.cssSelector("div.subheader")).getText();
        assertEquals(tituloEsperado, titulo);
    }

    @When("ingresa el First Name {string}, el Last Name {string} y el Postal Code {string}")
    public void ingresarCheckout(String First, String Last, String Zip) {
        driver.findElement(By.id("first-name")).sendKeys(First);
        driver.findElement(By.id("last-name")).sendKeys(Last);
        driver.findElement(By.id("postal-code")).sendKeys(Zip);
    }

    @When("hace clic en el botón de continue")
    public void clickContinue() {
        driver.findElement(By.cssSelector(".btn_primary.cart_button")).click();
    }

    @Then("debería verificar que el subtotal concuerda con la suma de los artículos")
    public void verificarSubtotal() {
        String subtotalTexto = driver.findElement(By.cssSelector(".summary_subtotal_label")).getText();
        String subtotal = subtotalTexto.replace("Item total: $", "").trim();
        
        if (!subtotal.equals("39.98")) {
            throw new RuntimeException("El subtotal no es correcto. Se esperaba $39.98 pero se encontró: $" + subtotal);
        }
    }

    @When("hace clic en el botón de finish")
    public void clickFinish() {
        driver.findElement(By.cssSelector(".btn_action.cart_button")).click();
    }

    @Then("debería ver el mensaje de confirmación de pedido")
    public void verificarMensajeConfirmacion() {
        String mensaje = driver.findElement(By.className("complete-header")).getText();
        if (!mensaje.equals("THANK YOU FOR YOUR ORDER")) {
            throw new RuntimeException("El mensaje de confirmación no es correcto. Se encontró: " + mensaje);
        }
    }

//Logout
    @When("hace clic en el botón para desplegar el menú")
    public void clickMenu() {
        driver.findElement(By.xpath("//button[contains(text(), 'Open Menu')]")).click();
    }

    @When("hace clic en el enlace de logout")
    public void clickLogout() {
        driver.findElement(By.id("logout_sidebar_link")).click();

    }

//LowtoHigh
    @When("hace clic en el select de ordenamiento y elige Price low to high")
    public void seleccionarOrdenamiento() {
        driver.findElement(By.className("product_sort_container")).click();
        driver.findElement(By.xpath("//option[@value='lohi']")).click();
    }

    @Then("debería ver los precios en orden ascendente")
    public void verificarPreciosOrdenAscendente() {
        String primerPrecio = driver.findElement(By.cssSelector(".inventory_item_price")).getText();
        if (!primerPrecio.contains("$7.99")) {
            throw new RuntimeException("El primer precio no es el más bajo. Se esperaba $7.99 pero se encontró: " + primerPrecio);
        }
    }
}
