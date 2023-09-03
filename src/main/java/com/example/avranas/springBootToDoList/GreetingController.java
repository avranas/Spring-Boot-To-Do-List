package com.example.avranas.springBootToDoList;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

  @RequestMapping("/")
  public String getGreeting() {
    return "Hello worldddd";
  }

  @RequestMapping("/greeting")
  public String getAnotherGreeting() {
    return "Got greeting";
  }
}
