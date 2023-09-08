package com.example.avranas.springBootToDoList;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
  public NotFoundException() {
    super();
  }

  public NotFoundException(String message, Throwable cause) {
    super(message, cause);
    System.out.println("Error:");
    System.out.println(message);
    System.out.println(cause);
  }

  public NotFoundException(String message) {
    super(message);
    System.out.println("Error:");
    System.out.println(message);
  }

  public NotFoundException(Throwable cause) {
    super(cause);
    System.out.println("Error:");
    System.out.println(cause);
  }
}