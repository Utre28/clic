package org.example.clic.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller// Marca esta clase como un controlador Spring
public class CustomErrorController implements ErrorController {

    /**
     * Maneja las solicitudes de error redirigidas a "/error".
     * @param request Petición HTTP que contiene el código de error.
     * @return Nombre de la vista HTML correspondiente al error.
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        // Obtiene el código de estado HTTP de la solicitud
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            // Si el código es 403 Forbidden, muestra la página error403.html
            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "error403"; // nombre del html sin extensión
            }
        }
        return "error"; // Página de error genérica
    }
}
