package cz.engeto.radim.secondproject.controller;

import cz.engeto.radim.secondproject.dto.GlobalErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(PersonIdUsedException.class)
    public ResponseEntity<GlobalErrorResponse> handlePersonIdUsedException(PersonIdUsedException e){
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(HttpStatus.LOCKED.value(), e.getMessage()); {
            return ResponseEntity.status(HttpStatus.LOCKED).body(errorResponse);
        }
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<GlobalErrorResponse> handleUserNotFoundException(NotFoundException e){
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(HttpStatus.NOT_FOUND.value(),  e.getMessage()); {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @ExceptionHandler(InvalidIdException.class)
    public ResponseEntity<GlobalErrorResponse> handleException(Exception e){
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),  e.getMessage()); {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
