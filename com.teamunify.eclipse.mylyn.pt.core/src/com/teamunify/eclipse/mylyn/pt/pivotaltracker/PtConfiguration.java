package com.teamunify.eclipse.mylyn.pt.pivotaltracker;

/**
 * @author DL
 *
 */

import java.util.Collections;
import java.util.List;

public class PtConfiguration {

  long updated = -1;

  private List<Person> members = Collections.emptyList();

  private boolean bugsChoreEstimatable;

  private String[] estimates;

  private String[] labels;

  private final String[] choreStates = { "Not Yet Started", "Started", "Accepted" };

  private final String[] releaseStates = { "Not Yet Started", "Accepted" };

  public String[] getChoreStates() {
    return choreStates;
  }

  public String[] getReleaseStates() {
    return releaseStates;
  }

  private String requestedBy;

  private List<Iteration> iterations;

  public void setEstimates(String[] estimates) {
    this.estimates = estimates;
  }

  public String[] getEstimates() {
    return estimates;
  }

  public String[] getLabels() {
    return labels;
  }

  public void setLabels(String[] labels) {
    this.labels = labels;
  }

  public boolean isBugsChoreEstimatable() {
    return bugsChoreEstimatable;
  }

  public void setBugsChoreEstimatable(boolean bugsChoreEstimatable) {
    this.bugsChoreEstimatable = bugsChoreEstimatable;
  }

  public List<Person> getMembers() {
    return members;
  }

  public void setMembers(List<Person> members) {
    this.members = members;
  }

  public List<Iteration> getIterations() {
    return iterations;
  }

  public void setIterations(List<Iteration> iterations) {
    this.iterations = iterations;
  }

  public int getMemberId(String name) {
    for (Person person : getMembers()) {
      if (person.getName().equals(name)) { return person.getId(); }
    }
    return -1;
  }

  public String getMemberName(int personId) {
    for (Person person : getMembers()) {
      if (person.getId() == personId) { return person.getName(); }
    }
    return "";
  }
}
