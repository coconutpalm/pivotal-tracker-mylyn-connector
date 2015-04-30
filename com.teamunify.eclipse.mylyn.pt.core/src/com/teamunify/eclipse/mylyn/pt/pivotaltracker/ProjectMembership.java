package com.teamunify.eclipse.mylyn.pt.pivotaltracker;

import org.joda.time.DateTime;
import com.google.gson.annotations.SerializedName;

public class ProjectMembership {

  public enum Role {
    owner, member, viewer, inactive
  }

  private int id;

  private Person person;

  private Role role;

  @SerializedName("project_color")
  private String projectColor;

  @SerializedName("last_viewed_at")
  private DateTime lastViewedAt;

  @SerializedName("wants_comment_notification_emails")
  private boolean wantsCommentNotificationEmails;

  @SerializedName("will_receive_mention_notifications_or_emails")
  private boolean willReceiveMentionNotificationsOrEmails;

  private String kind;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Person getPerson() {
    return person;
  }

  public void setPerson(Person person) {
    this.person = person;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public String getProjectColor() {
    return projectColor;
  }

  public void setProjectColor(String projectColor) {
    this.projectColor = projectColor;
  }

  public DateTime getLastViewedAt() {
    return lastViewedAt;
  }

  public void setLastViewedAt(DateTime lastViewedAt) {
    this.lastViewedAt = lastViewedAt;
  }

  public boolean isWantsCommentNotificationEmails() {
    return wantsCommentNotificationEmails;
  }

  public void setWantsCommentNotificationEmails(boolean wantsCommentNotificationEmails) {
    this.wantsCommentNotificationEmails = wantsCommentNotificationEmails;
  }

  public boolean isWillReceiveMentionNotificationsOrEmails() {
    return willReceiveMentionNotificationsOrEmails;
  }

  public void setWillReceiveMentionNotificationsOrEmails(boolean willReceiveMentionNotificationsOrEmails) {
    this.willReceiveMentionNotificationsOrEmails = willReceiveMentionNotificationsOrEmails;
  }

  public String getKind() {
    return kind;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }
}
