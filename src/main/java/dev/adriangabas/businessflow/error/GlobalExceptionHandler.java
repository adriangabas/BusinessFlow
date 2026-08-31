package dev.adriangabas.businessflow.error;

import dev.adriangabas.businessflow.categoria.CategoriaDuplicadaException;
import dev.adriangabas.businessflow.categoria.CategoriaNoEncontradaException;
import dev.adriangabas.businessflow.cliente.ClienteDuplicadoException;
import dev.adriangabas.businessflow.cliente.ClienteNoEncontradoException;
import dev.adriangabas.businessflow.producto.ProductoDuplicadoException;
import dev.adriangabas.businessflow.producto.ProductoNoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({CategoriaNoEncontradaException.class, ClienteNoEncontradoException.class,
            ProductoNoEncontradoException.class})
    ResponseEntity<ApiError> noEncontrada(RuntimeException exception, HttpServletRequest request) {
        return respuesta(HttpStatus.NOT_FOUND, exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler({CategoriaDuplicadaException.class, ClienteDuplicadoException.class, ProductoDuplicadoException.class,
            DataIntegrityViolationException.class})
    ResponseEntity<ApiError> conflicto(RuntimeException exception, HttpServletRequest request) {
        String mensaje = exception instanceof CategoriaDuplicadaException || exception instanceof ClienteDuplicadoException
                || exception instanceof ProductoDuplicadoException
                ? exception.getMessage() : "La operación entra en conflicto con datos existentes";
        return respuesta(HttpStatus.CONFLICT, mensaje, request, Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validacion(MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, String> errores = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> errores.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return respuesta(HttpStatus.BAD_REQUEST, "Los datos enviados no son válidos", request, errores);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiError> cuerpoIlegible(HttpMessageNotReadableException exception, HttpServletRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST, "El cuerpo de la petición no es válido", request, Map.of());
    }

    private ResponseEntity<ApiError> respuesta(HttpStatus status, String message, HttpServletRequest request,
            Map<String, String> errores) {
        ApiError error = new ApiError(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message,
                request.getRequestURI(), errores);
        return ResponseEntity.status(status).body(error);
    }
}
