package com.example.avranas.springBootToDoList;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "todos")
public class Todo {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;
  private String content;
  private LocalDate createdAt;
  private LocalDate updatedAt;
  
  // Hibernate expects entities to have a no-arg constructor,
  // though it does not necessarily have to be public.
  private Todo() {}
  
  public Todo(String content) {
    this.content = content;
  }
  
  public Integer getId() {
    return this.id;
  }
  
  public String getContent() {
    return this.content;
  }

  public LocalDate getCreatedAt() {
    return this.createdAt;
  }

  public LocalDate getUpdatedAt() {
    return this.updatedAt;
  }

  public LocalDate setCreatedAt(LocalDate newDate) {
    return this.createdAt = newDate;
  }

  public LocalDate setUpdatedAt(LocalDate newDate) {
    return this.createdAt = newDate;
  }

  public String setContent(String newContent) {
    return this.content = newContent;
  }

  public void print() {
    System.out.println("id: " + this.id);
    System.out.println("content: " + this.content);
    System.out.println("createdAt: " + this.createdAt);
    System.out.println("updatedAt: " + this.updatedAt);
  }
}