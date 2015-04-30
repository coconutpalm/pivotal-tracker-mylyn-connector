package com.teamunify.eclipse.mylyn.pt.pivotaltracker;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Project {
  private int id;
  private String name;
  private int version;

  @SerializedName("point_scale")
  private String pointScale;

  @SerializedName("bugs_and_chores_are_estimatable")
  private boolean bugsAndChoresAreEstimatable;

  private List<Label> labels;

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

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public String getPointScale() {
    return pointScale;
  }

  public void setPointScale(String pointScale) {
    this.pointScale = pointScale;
  }

  public boolean isBugsAndChoresAreEstimatable() {
    return bugsAndChoresAreEstimatable;
  }

  public void setBugsAndChoresAreEstimatable(boolean bugsAndChoresAreEstimatable) {
    this.bugsAndChoresAreEstimatable = bugsAndChoresAreEstimatable;
  }

  public List<Label> getLabels() {
    return labels;
  }

  public void setLabels(List<Label> labels) {
    this.labels = labels;
  }

}
