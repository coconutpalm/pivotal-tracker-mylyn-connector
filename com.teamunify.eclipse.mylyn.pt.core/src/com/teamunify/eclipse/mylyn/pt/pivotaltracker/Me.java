package com.teamunify.eclipse.mylyn.pt.pivotaltracker;

import org.joda.time.DateTime;
import com.google.gson.annotations.SerializedName;

public class Me {
  private String kind;
  private int id;
  private String name;
  private String initials;
  private String username;

  // @SerializedName("time_zone")
  // private TimeZone timeZone;

  @SerializedName("has_google_identity")
  private boolean hasGoogleIdentity;

  private String email;

  @SerializedName("receives_in_app_notifications")
  private boolean receivesInAppNotifications;

  @SerializedName("created_at")
  private DateTime createdAt;

  @SerializedName("updated_at")
  private DateTime updatedAt;

  public String getKind() {
    return kind;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getInitials() {
    return initials;
  }

  public void setInitials(String initials) {
    this.initials = initials;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  // public TimeZone getTimeZone() {
  // return timeZone;
  // }
  //
  // public void setTimeZone(TimeZone timeZone) {
  // this.timeZone = timeZone;
  // }

  public boolean isHasGoogleIdentity() {
    return hasGoogleIdentity;
  }

  public void setHasGoogleIdentity(boolean hasGoogleIdentity) {
    this.hasGoogleIdentity = hasGoogleIdentity;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public boolean isReceivesInAppNotifications() {
    return receivesInAppNotifications;
  }

  public void setReceivesInAppNotifications(boolean receivesInAppNotifications) {
    this.receivesInAppNotifications = receivesInAppNotifications;
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
