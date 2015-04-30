package com.teamunify.eclipse.mylyn.pt.pivotaltracker;

import java.util.List;
import org.joda.time.DateTime;
import com.google.gson.annotations.SerializedName;

public class Story {

  public enum StoryType {
    feature, bug, chore, release
  }

  public enum StoryState {
    accepted, delivered, finished, started, rejected, planned, unstarted, unscheduled
  }

  private int id;

  @SerializedName("project_id")
  private int projectId;

  private String name;

  private String description = "";

  @SerializedName("story_type")
  private StoryType storyType;

  @SerializedName("current_state")
  private StoryState currentState;

  private int estimate;

  @SerializedName("accepted_at")
  private DateTime acceptedAt;

  @SerializedName("created_at")
  private DateTime createdAt;

  @SerializedName("updated_at")
  private DateTime updatedAt;

  private DateTime deadline;

  @SerializedName("requested_by_id")
  private int requestedBy;

  @SerializedName("owned_by_id")
  private int ownedBy;

  @SerializedName("owner_ids")
  private int[] owner_ids;

  private String url;

  private List<Label> labels;

  private List<Task> tasks;

  private List<Comment> comments;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public StoryType getStoryType() {
    return storyType;
  }

  public void setStoryType(StoryType storyType) {
    this.storyType = storyType;
  }

  public StoryState getCurrentState() {
    return currentState;
  }

  public void setCurrentState(StoryState currentState) {
    this.currentState = currentState;
  }

  public int getEstimate() {
    return estimate;
  }

  public void setEstimate(int estimate) {
    this.estimate = estimate;
  }

  public DateTime getAcceptedAt() {
    return acceptedAt;
  }

  public void setAcceptedAt(DateTime acceptedAt) {
    this.acceptedAt = acceptedAt;
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

  public DateTime getDeadline() {
    return deadline;
  }

  public void setDeadline(DateTime deadline) {
    this.deadline = deadline;
  }

  public int getRequestedBy() {
    return requestedBy;
  }

  public void setRequestedBy(int requestedBy) {
    this.requestedBy = requestedBy;
  }

  public int getOwnedBy() {
    return ownedBy;
  }

  public void setOwnedBy(int ownedBy) {
    this.ownedBy = ownedBy;
  }

  public int[] getOwner_ids() {
    return owner_ids;
  }

  public void setOwner_ids(int[] owner_ids) {
    this.owner_ids = owner_ids;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public List<Label> getLabels() {
    return labels;
  }

  public void setLabels(List<Label> labels) {
    this.labels = labels;
  }

  public List<Task> getTasks() {
    return tasks;
  }

  public void setTasks(List<Task> tasks) {
    this.tasks = tasks;
  }

  public List<Comment> getComments() {
    return comments;
  }

  public void setComments(List<Comment> comments) {
    this.comments = comments;
  }
}
