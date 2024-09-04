package com.ivan_degtev.whatsappbotforneoderma.handler;

import com.ivan_degtev.whatsappbotforneoderma.exception.NoParameterException;
import com.ivan_degtev.whatsappbotforneoderma.exception.NotFoundException;
import com.ivan_degtev.whatsappbotforneoderma.exception.NotSaveDataException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseStatusException handleNotFoundException(NotFoundException ex) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(NoParameterException.class)
    public ResponseStatusException handleNotFoundException(NoParameterException ex) {
        return new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, ex.getMessage());
    }

    @ExceptionHandler(NotSaveDataException.class)
    public ResponseStatusException handleNotSaveDataException(NotSaveDataException ex) {
        return new ResponseStatusException(HttpStatus.INSUFFICIENT_STORAGE, ex.getMessage());
    }
}