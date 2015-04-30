package com.teamunify.eclipse.mylyn.pt.pivotaltracker;

import org.joda.time.DateTime;
import com.google.gson.annotations.SerializedName;

public class Comment {
  private int id;

  private String text;

  private Person person;

  @SerializedName("created_at")
  private DateTime createdAt;

  @SerializedName("updated_at")
  private DateTime updatedAt;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Person getPerson() {
    return person;
  }

  public void setPerson(Person person) {
    this.person = person;
  }

  public DateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(DateTime createdAt) {
    this.createdAt = createdAt;
  }

  public DateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(DateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

}
