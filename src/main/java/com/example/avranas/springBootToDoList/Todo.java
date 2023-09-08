package com.example.avranas.springBootToDoList;

import java.time.LocalDate;
import java.util.Map;

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
  private String createdAt;
  private String updatedAt;

  // Hibernate expects entities to have a no-arg constructor,
  // though it does not necessarily have to be public.
  private Todo() {
  }

  public Todo(Map<?, ?> gson) {
    for (Map.Entry<?, ?> entry : gson.entrySet()) {
      if (entry.getValue() == null)
        continue;
      switch (entry.getKey().toString()) {
        case "id":
          this.id = (int) Double.parseDouble(entry.getValue().toString());
          break;
        case "content":
          this.content = entry.getValue().toString();
          break;
        case "createdAt":
          this.createdAt = entry.getValue().toString();
          break;
        case "updatedAt":
          this.updatedAt = entry.getValue().toString();
          break;
        default:
          break;
      }
    }
  }

  public Todo(String content) {
    this.content = content;
    this.setCreatedAt(java.time.LocalDate.now());
    this.updatedAt = null;
  }

  public Integer getId() {
    return this.id;
  }

  public String getContent() {
    return this.content;
  }

  public String getCreatedAt() {
    return this.createdAt;
  }

  public String getUpdatedAt() {
    return this.updatedAt;
  }

  // This should only be used for testing purposes
  public Integer setId(Integer id) {
    return this.id = id;
  }

  public String setCreatedAt(LocalDate newDate) {
    return this.createdAt = newDate.toString();
  }

  public String setUpdatedAt(LocalDate newDate) {
    return this.updatedAt = newDate.toString();
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