package com.teamunify.eclipse.mylyn.pt.pivotaltracker;

import org.joda.time.DateTime;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author DL
 */
public class Task {

  private int id;
  private String description;
  private int position;
  private boolean complete;

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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public boolean isComplete() {
    return complete;
  }

  public void setComplete(boolean complete) {
    this.complete = complete;
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
