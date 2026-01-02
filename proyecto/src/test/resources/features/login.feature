Feature: Login de usuario en Swag Labs
  Como usuario registrado en la aplicación
  Quiero poder iniciar sesión con credenciales válidas
  Para acceder al catálogo de productos

@login 
  Scenario: Login exitoso con usuario válido
    Given que el usuario abre la página de Swag Labs
    When ingresa el usuario "standard_user" y la contraseña "secret_sauce"
    And hace clic en el botón de login
    Then debería ver la página de productos con el título "Products"

@loginBlock
  Scenario: Login fallido con usuario bloqueado
    Given que el usuario abre la página de Swag Labs
    When ingresa el usuario "locked_out_user" y la contraseña "secret_sauce"
    And hace clic en el botón de login
    Then debería ver el error "Epic sadface: Sorry, this user has been locked out."
  
@AddCart 
  Scenario: Agregar al carrito varios articulos
    Given que el usuario abre la página de Swag Labs
    When ingresa el usuario "standard_user" y la contraseña "secret_sauce"
    And hace clic en el botón de login
    Then debería ver la página de productos con el título "Products"
    Then debería agregar al carrito "Sauce Labs Backpack"
    And hace clic en el botón de Add to cart para "Sauce Labs Backpack"
    Then debería agregar al carrito "Sauce Labs Bike Light"
    And hace clic en el botón de Add to cart para "Sauce Labs Bike Light"
    Then debería ver 2 productos en el carrito

@Checkout
  Scenario: Validacion del valor del subtotal en el Checkout
    Given que el usuario abre la página de Swag Labs
    When ingresa el usuario "standard_user" y la contraseña "secret_sauce"
    And hace clic en el botón de login
    Then debería ver la página de productos con el título "Products"
    Then debería agregar al carrito "Sauce Labs Backpack"
    And hace clic en el botón de Add to cart para "Sauce Labs Backpack"
    Then debería agregar al carrito "Sauce Labs Bike Light"
    And hace clic en el botón de Add to cart para "Sauce Labs Bike Light"
    And hace clic en el botón de carrito
    Then debería ver la página del carrito con el título "Your Cart"
    And hace clic en el botón de checkout
    Then debería ver la página del checkout con el título "Checkout: Your Information"
    When ingresa el First Name "Juan", el Last Name "Perez" y el Postal Code "1"
    And hace clic en el botón de continue
    Then debería ver la página del checkout con el título "Checkout: Overview"
    And debería verificar que el subtotal concuerda con la suma de los artículos
    And hace clic en el botón de finish
    Then debería ver el mensaje de confirmación de pedido
    
@Logout
  Scenario: Cerrar sesion desde el menú lateral
    Given que el usuario abre la página de Swag Labs
    When ingresa el usuario "standard_user" y la contraseña "secret_sauce"
    And hace clic en el botón de login
    Then debería ver la página de productos con el título "Products"
    And hace clic en el botón para desplegar el menú
    And hace clic en el enlace de logout

@LowtoHigh
  Scenario: Ordenar productos de menor a mayor precio. 
    Given que el usuario abre la página de Swag Labs
    When ingresa el usuario "standard_user" y la contraseña "secret_sauce"
    And hace clic en el botón de login
    Then debería ver la página de productos con el título "Products"
    When hace clic en el select de ordenamiento y elige Price low to high
    Then debería ver los precios en orden ascendente
    
    
    
    



