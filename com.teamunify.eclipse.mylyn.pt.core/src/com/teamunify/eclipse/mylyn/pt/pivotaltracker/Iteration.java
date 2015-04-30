package com.teamunify.eclipse.mylyn.pt.pivotaltracker;

import org.joda.time.DateTime;
import com.google.gson.annotations.SerializedName;

public class Iteration {

  private int number;

  @SerializedName("project_id")
  private int projectId;

  private int length;

  @SerializedName("team_strength")
  private float teamStrength;

  @SerializedName("story_ids")
  private int[] storyIds;

  private DateTime start;
  private DateTime finish;
  private String kind;

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public float getTeamStrength() {
    return teamStrength;
  }

  public void setTeamStrength(float teamStrength) {
    this.teamStrength = teamStrength;
  }

  public int[] getStoryIds() {
    return storyIds;
  }

  public void setStoryIds(int[] storyIds) {
    this.storyIds = storyIds;
  }

  public DateTime getStart() {
    return start;
  }

  public void setStart(DateTime start) {
    this.start = start;
  }

  public DateTime getFinish() {
    return finish;
  }

  public void setFinish(DateTime finish) {
    this.finish = finish;
  }

  public String getKind() {
    return kind;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }
}
