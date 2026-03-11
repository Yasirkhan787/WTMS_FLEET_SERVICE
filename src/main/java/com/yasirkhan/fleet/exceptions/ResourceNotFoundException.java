package com.yasirkhan.fleet.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
public class ResourceNotFoundException extends RuntimeException{

    private final String message;

    private final HttpStatus status;

    public ResourceNotFoundException(String message){
        super(message);
        this.message = message;
        this.status = HttpStatus.NOT_FOUND;
    }

}
