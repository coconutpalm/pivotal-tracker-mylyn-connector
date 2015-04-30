package com.teamunify.eclipse.mylyn.pt.pivotaltracker;

/**
 * @author DL
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.teamunify.eclipse.mylyn.pt.core.PtCorePlugin;
import com.teamunify.eclipse.mylyn.pt.core.util.StringUtils;

public class PivotalTracker {
  private final String repositoryUrl;
  private final String apiToken;
  private volatile PtConfiguration configuration = new PtConfiguration();
  private final String projectUrl = "https://www.pivotaltracker.com/services/v5/projects/";
  String errmsg = "";
  private String projectId;

  public PivotalTracker(String url, String apiToken) throws Exception {
    this.repositoryUrl = projectUrl + url;
    this.apiToken = apiToken;
    updateProjectConfiguration();
  }

  public String getErrmsg() {
    return errmsg;
  }

  public void setErrmsg(String errmsg) {
    this.errmsg = errmsg;
  }

  private Me getMe() {
    DefaultHttpClient client = new DefaultHttpClient();
    HttpGet get = new HttpGet(repositoryUrl);
    get.addHeader("X-TrackerToken", apiToken);

    HttpResponse response;
    try {
      response = client.execute(get);

      HttpEntity entity = response.getEntity();

      return gsonWithDate().fromJson(new InputStreamReader(entity.getContent()), Me.class);
    } catch (Exception e) {
      PtCorePlugin.getDefault().getLog()
                  .log(new Status(IStatus.ERROR, PtCorePlugin.ID_PLUGIN, "Error requesting me resource", e));
    }
    return null;
  }

  public Boolean getProject(int projectId) {
    DefaultHttpClient client = new DefaultHttpClient();
    HttpGet get = new HttpGet(projectUrl + projectId);
    get.addHeader("X-TrackerToken", apiToken);
    HttpResponse response;
    try {
      response = client.execute(get);
      System.out.println("Response code : " + response.getStatusLine().getStatusCode());
      if (response.getStatusLine().getStatusCode() == 200) { return true; }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return false;
  }

  private HttpEntity get(final String apiQuery) {
    DefaultHttpClient client = new DefaultHttpClient();
    HttpGet get = new HttpGet(repositoryUrl + apiQuery);
    get.addHeader("X-TrackerToken", apiToken);
    HttpResponse response;

    try {
      response = client.execute(get);

      return response.getEntity();
    } catch (Exception e) {
      // TODO Auto-generated catch block
    }
    return null;
  }

  static String convertStreamToString(java.io.InputStream is) {
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }

  /**
   * Get list of Members in the given project
   *
   * @return
   */
  public void updateProjectConfiguration() {
    // List<String> list = new ArrayList<String>();
    // List<String> emailList = new ArrayList<String>();
    try {

      Project project = gsonWithDate().fromJson(new InputStreamReader(
                                                                      get(
                                                                          "?fields=id,name,version,point_scale,bugs_and_chores_are_estimatable,labels").getContent()),
                                                Project.class);
      configuration.setBugsChoreEstimatable(project.isBugsAndChoresAreEstimatable());
      configuration.setEstimates(project.getPointScale().split(","));

      List<ProjectMembership> projectMemberships = gsonWithDate().fromJson(new InputStreamReader(
                                                                                                 get("/memberships").getContent()),
                                                                           new TypeToken<List<ProjectMembership>>() {}.getType());
      List<Person> members = new LinkedList<Person>();
      for (ProjectMembership membership : projectMemberships) {
        members.add(membership.getPerson());
      }
      configuration.setMembers(members);

      List<Iteration> iterations = gsonWithDate().fromJson(new InputStreamReader(get("/iterations").getContent()),
                                                           new TypeToken<List<Iteration>>() {}.getType());
      configuration.setIterations(iterations);
    } catch (Exception e) {
      PtCorePlugin.getDefault().getLog()
                  .log(new Status(IStatus.ERROR, PtCorePlugin.ID_PLUGIN, "Error requesting project resource", e));
    }
  }

  public final List<Story> collectStories(IRepositoryQuery query) throws Exception {
    if (query != null) {
      StringBuilder sb = new StringBuilder();
      sb.append("/stories");

      String labelFilter = query.getAttribute(PtCorePlugin.QUERY_KEY_LABEL);
      if (StringUtils.isNotEmpty(labelFilter)) {
        sb.append("&labels:" + labelFilter);
      }

      String ownedFilter = query.getAttribute(PtCorePlugin.QUERY_KEY_OWNED_BY);
      if (StringUtils.isNotEmpty(ownedFilter)) {
        sb.append("&owned_by_id:" + ownedFilter);
      }

      String requestedFilter = query.getAttribute(PtCorePlugin.QUERY_KEY_REQUESTED_BY);
      if (StringUtils.isNotEmpty(requestedFilter)) {
        sb.append("&requested_by_id:" + requestedFilter);
      }

      String stateFilter = query.getAttribute(PtCorePlugin.QUERY_KEY_STATE);
      if (StringUtils.isNotEmpty(stateFilter)) {
        sb.append("&current_state:" + stateFilter);
      }

      String typeFilter = query.getAttribute(PtCorePlugin.QUERY_KEY_STORY_TYPE);
      if (StringUtils.isNotEmpty(typeFilter)) {
        sb.append("&story_type:" + typeFilter);
      }
      String urlQuery = sb.toString().replaceFirst("&", "?filter=").replaceAll("\\|", ",");
      return gsonWithDate().fromJson(new InputStreamReader(get(urlQuery).getContent()),
                                     new TypeToken<List<Story>>() {}.getType());
    }
    return Collections.emptyList();
  }

  /**
   * Get a story object by id
   *
   * @param url
   * @return
   * @throws Exception
   */
  public Story getStoryById(String storyId) throws Exception {

    return gsonWithDate().fromJson(new InputStreamReader(
                                                         get(
                                                             "/stories/"
                                                                 + storyId
                                                                 + "?fields=name,description,story_type,current_state,estimate,accepted_at,deadline,requested_by_id,owned_by_id,labels,tasks,comments,created_at,updated_at,url").getContent()),
                                   Story.class);
  }

  /**
   * Add a note into PT
   *
   * @param url
   * @param notes
   */
  public void addNote(String url, Notes notes) throws Exception {
    try {
      DefaultHttpClient client = new DefaultHttpClient();
      List<NameValuePair> formparams = new ArrayList<NameValuePair>();

      if (notes != null) {

        NoteData elements[] = notes.getNote();
        if (elements != null) {
          for (NoteData noteData : elements) {
            formparams.add(new BasicNameValuePair("note[text]", noteData.getText()));

          }
        }
      }

      String query = URLEncodedUtils.format(formparams, "UTF-8");
      url = url + query;
      HttpPost httpPost = new HttpPost(url);

      httpPost.addHeader("X-TrackerToken", apiToken);

      client.execute(httpPost);

    } catch (Exception e) {
      throw new Exception("Error while adding note.");
    }
  }

  /**
   * Add a story In PT (By default goes to ICEBOX)
   *
   * @param storyData
   * @return
   */
  public String addStory(Story storyData) throws Exception {
    DefaultHttpClient client = new DefaultHttpClient();
    List<NameValuePair> formparams = new ArrayList<NameValuePair>();
    formparams.add(new BasicNameValuePair("story[name]", storyData.getName()));
    formparams.add(new BasicNameValuePair("story[description]", storyData.getDescription()));
    formparams.add(new BasicNameValuePair("story[estimate]", storyData.getEstimate() + ""));
    formparams.add(new BasicNameValuePair("story[current_state]", storyData.getCurrentState().name()));
    formparams.add(new BasicNameValuePair("story[owned_by]", storyData.getOwnedBy() + ""));
    formparams.add(new BasicNameValuePair("story[requested_by]", "" + storyData.getRequestedBy()));
    // TODO to string
    // formparams.add(new BasicNameValuePair("story[labels]", storyData.getLabels()));
    formparams.add(new BasicNameValuePair("story[story_type]", storyData.getStoryType().name()));
    String query = URLEncodedUtils.format(formparams, "UTF-8");
    String uri = repositoryUrl + "/stories?" + query;
    HttpPost httpPost = new HttpPost(uri);
    httpPost.addHeader("X-TrackerToken", apiToken);
    httpPost.addHeader("Content-type", "application/xml");

    HttpResponse response = client.execute(httpPost);

    HttpEntity entity = response.getEntity();
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder loader = factory.newDocumentBuilder();
    Document document = loader.parse(entity.getContent());
    NodeList nodes = document.getElementsByTagName("id");
    // addNote(repositoryUrl + "/stories/" + nodes.item(0).getTextContent() + "/notes?", storyData.getNotes());
    // addUpdateTask(repositoryUrl + "/stories/" + nodes.item(0).getTextContent(), storyData.getTasks());
    if (nodes.getLength() > 0) { return nodes.item(0).getTextContent(); }
    return "";
  }

  /**
   * Update a story in PT
   *
   * @param storyData
   */
  public String updateStory(Story storyData) throws Exception {
    String response = "Unknown error";
    try {
      DefaultHttpClient client = new DefaultHttpClient();
      List<NameValuePair> formparams = new ArrayList<NameValuePair>();
      formparams.add(new BasicNameValuePair("story[name]", storyData.getName()));
      formparams.add(new BasicNameValuePair("story[description]", storyData.getDescription()));
      formparams.add(new BasicNameValuePair("story[estimate]", storyData.getEstimate() + ""));
      formparams.add(new BasicNameValuePair("story[current_state]", storyData.getCurrentState().name()));
      formparams.add(new BasicNameValuePair("story[owned_by]", storyData.getOwnedBy() + ""));
      formparams.add(new BasicNameValuePair("story[requested_by]", "" + storyData.getRequestedBy()));
      // TODO to string
      // formparams.add(new BasicNameValuePair("story[labels]", storyData.getLabels()));
      formparams.add(new BasicNameValuePair("story[story_type]", storyData.getStoryType().name()));

      String query = URLEncodedUtils.format(formparams, "UTF-8");
      String uri = repositoryUrl + "/stories/" + storyData.getId() + "?" + query;
      HttpPut httpPost = new HttpPut(uri);

      httpPost.addHeader("X-TrackerToken", apiToken);

      HttpResponse httpResponse = client.execute(httpPost);
      HttpEntity entity = httpResponse.getEntity();
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder loader = factory.newDocumentBuilder();
      Document document = loader.parse(entity.getContent());
      // addNote(repositoryUrl + "/stories/" + storyData.getId() + "/notes?", storyData.getNotes());
      // if (storyData.getTasks() != null) {
      // addUpdateTask(repositoryUrl + "/stories/" + storyData.getId(), storyData.getTasks());
      // }
      NodeList nodes = document.getElementsByTagName("error");
      if (nodes.getLength() > 0) {
        response = nodes.item(0).getTextContent();
      } else {
        response = "success";
      }
    } catch (Exception e) {
      new Exception(response);
    }
    return response;
  }

  /**
   * Get list of stories in the given iteration
   *
   * @param iterationType
   *          (done,backlog,current by default)
   * @return
   */
  public final List<Story> getCurrentIterations(String iterationType) throws Exception {
    if (iterationType != null && iterationType.length() > 0) { return gsonWithDate().fromJson(new InputStreamReader(
                                                                                                                    get(
                                                                                                                        "/stories").getContent()),
                                                                                              new TypeToken<List<Story>>() {}.getType()); }
    return Collections.emptyList();
  }

  public PtConfiguration updateConfiguration(IProgressMonitor monitor) throws Exception {
    try {
      updateProjectConfiguration();
      return configuration;
    } catch (Exception e) {
      throw new Exception("Error reading configuration");
    }
  }

  public final boolean hasConfiguration() {
    return configuration.updated != -1;
  }

  public InputStream getAttachment(String storyId, String url) throws Exception {
    try {
      // requesting the attachment url
      DefaultHttpClient client = new DefaultHttpClient();
      HttpContext localContext = new BasicHttpContext();
      HttpGet get = new HttpGet(url);

      HttpResponse response = client.execute(get, localContext);

      Header[] headers = response.getAllHeaders();
      String cookie = "";
      for (Header header : headers) {
        if (header.getValue().startsWith("tracker_session")) {
          cookie = header.getValue();
          break;
        }
      }

      HttpHost target = (HttpHost) localContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);

      String redirectUrl = "https://" + target.toHostString() + "/signin";

      // Sign in

      DefaultHttpClient client1 = new DefaultHttpClient();

      List<NameValuePair> formparams = new ArrayList<NameValuePair>();
      // formparams.add(new BasicNameValuePair("credentials[username]", userName));
      // formparams.add(new BasicNameValuePair("credentials[password]", password));
      UrlEncodedFormEntity paramEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
      HttpPost httpPost = new HttpPost(redirectUrl);
      httpPost.setEntity(paramEntity);
      httpPost.setHeader("Cookie", cookie);
      HttpResponse response1 = client1.execute(httpPost);

      Header[] headers1 = response1.getAllHeaders();
      String cookie1 = "";
      String cookie2 = "";
      String locationUrl = "";
      for (Header header : headers1) {
        if (header.getValue().startsWith("tracker_session")) {
          cookie1 = header.getValue();
        }
        if (header.getValue().startsWith("_tracker_verifier")) {
          cookie2 = header.getValue();
        }
        if (header.getName().startsWith("Location")) {
          locationUrl = header.getValue();
        }

      }
      DefaultHttpClient client2 = new DefaultHttpClient();
      HttpGet get2 = new HttpGet(locationUrl);
      get2.setHeader("Cookie", cookie1 + ";" + cookie2);
      HttpResponse httpResponse = client2.execute(get2);
      return httpResponse.getEntity().getContent();
    } catch (Exception e) {
      throw new Exception("Error while attachment download");
    }
  }

  public void addAttachment(String storyId, String filename, String contentType, InputStream source) {
    try {
      DefaultHttpClient client = new DefaultHttpClient();
      String uri = repositoryUrl + "/stories/" + storyId + "/attachments";
      HttpPost httpPost = new HttpPost(uri);

      MultipartEntity mpEntity = new MultipartEntity();

      // create a tmp file for uploading
      File file = new File(filename);
      FileOutputStream fileOutputStream = new FileOutputStream(file);

      byte[] bytes = new byte[1024];
      int read = 0;

      while ((read = source.read(bytes)) != -1) {
        fileOutputStream.write(bytes, 0, read);
      }
      ContentBody cbFile = new FileBody(file, contentType);

      mpEntity.addPart("Filedata", cbFile);

      httpPost.setEntity(mpEntity);
      httpPost.addHeader("X-TrackerToken", apiToken);

      client.execute(httpPost);
      file.delete();

    } catch (Exception e) {
      e.printStackTrace();
      new Exception("Error while uploading attachment");
    }
  }

  public String getLocation() {
    return repositoryUrl;

  }

  public Date getLastActivityAt() throws Exception {
    DefaultHttpClient client = new DefaultHttpClient();

    HttpGet get = new HttpGet(repositoryUrl);
    get.addHeader("X-TrackerToken", apiToken);
    HttpResponse response = client.execute(get);
    if (response.getStatusLine().getStatusCode() == 200) {
      HttpEntity entity = response.getEntity();
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder loader = factory.newDocumentBuilder();
      Document document = loader.parse(entity.getContent());
      NodeList nodes = document.getElementsByTagName("last_activity_at");
      if (nodes.getLength() > 0) {
        DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = formatter.parse(nodes.item(0).getTextContent());

        return date;
      }
    }

    return null;
  }

  public Set<Integer> getActivityStoryIds(Long longValue) throws Exception {
    Set<Integer> storyIds = new HashSet<Integer>();

    Date date = new Date(longValue * 1000l);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z");
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

    String s = dateFormat.format(date).toString().replaceAll("\\s", "%20");

    String url = repositoryUrl + "/activities?occurred_since_date=" + s;
    DefaultHttpClient client = new DefaultHttpClient();
    try {
      HttpGet get = new HttpGet(url);
      get.addHeader("X-TrackerToken", apiToken);
      HttpResponse response = client.execute(get);
      if (response.getStatusLine().getStatusCode() == 200) {
        HttpEntity entity = response.getEntity();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder loader = factory.newDocumentBuilder();
        Document document = loader.parse(entity.getContent());
        NodeList nodes = document.getElementsByTagName("story");
        for (int i = 0; i < nodes.getLength(); i++) {
          Element element = (Element) nodes.item(i);
          NodeList nodes1 = element.getElementsByTagName("id");
          if (nodes1.getLength() > 0) {
            storyIds.add(Integer.parseInt(nodes1.item(0).getTextContent()));
          }
        }
      }
    } catch (Exception e) {
      throw new Exception("Error while retrieving story activities");
    }
    return storyIds;

  }

  /**
   * Update PT tasks
   *
   * @param url
   * @param tasks
   * @throws Exception
   */
  private void addUpdateTask(String url, Tasks tasks) throws Exception {
    if (tasks != null) {
      for (Task task : tasks.getTask()) {
        if (task != null) {
          if (task.getId() == 0 && (task.getDescription() != null && (!task.getDescription().isEmpty()))) {
            DefaultHttpClient client = new DefaultHttpClient();
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            formparams.add(new BasicNameValuePair("task[description]", task.getDescription()));
            HttpPost httpPost = new HttpPost(url + "/tasks?" + URLEncodedUtils.format(formparams, "UTF-8"));
            httpPost.addHeader("X-TrackerToken", apiToken);
            HttpResponse httpResponse = client.execute(httpPost);
            System.out.println("httpResponse : " + httpResponse.getStatusLine().getStatusCode());
          }

          if (task.getId() != 0) {
            DefaultHttpClient client = new DefaultHttpClient();
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            String status = task.isComplete() == true ? "true" : "false";
            formparams.add(new BasicNameValuePair("task[complete]", status));
            formparams.add(new BasicNameValuePair("task[description]", task.getDescription()));
            HttpPut httpPut = new HttpPut(url + "/tasks/" + task.getId() + "?"
                                          + URLEncodedUtils.format(formparams, "UTF-8"));
            httpPut.addHeader("X-TrackerToken", apiToken);
            client.execute(httpPut);
          }
        }
      }
    }
  }

  public boolean deleteTask(int storyId, int taskId) {
    DefaultHttpClient client = new DefaultHttpClient();
    HttpDelete httpDelete = new HttpDelete(repositoryUrl + "/stories/" + storyId + "/tasks/" + taskId);
    httpDelete.addHeader("X-TrackerToken", apiToken);
    HttpResponse httpResponse = null;
    try {
      httpResponse = client.execute(httpDelete);
      System.out.println("httpResponse : " + httpResponse.getStatusLine().getStatusCode());
      if (httpResponse.getStatusLine().getStatusCode() == 200) { return true; }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean deleteStory(int storyId) {
    DefaultHttpClient client = new DefaultHttpClient();
    HttpDelete httpDelete = new HttpDelete(repositoryUrl + "/stories/" + storyId);
    httpDelete.addHeader("X-TrackerToken", apiToken);

    HttpResponse httpResponse = null;
    try {
      httpResponse = client.execute(httpDelete);
      System.out.println("httpResponse : " + httpResponse.getStatusLine().getStatusCode());
      if (httpResponse.getStatusLine().getStatusCode() == 200) { return true; }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  public String getProjectId() {
    return projectId;
  }

  /**
   *
   * Moves the given story Id before/after the target story Id in PT
   *
   * @param storyId
   *          - moving story id
   * @param targetStoryId
   *          - target story id
   * @param position
   *          - before or after the target story id
   * @throws Exception
   */
  public String moveStory(String storyId, String targetStoryId, String position) throws Exception {
    String response = null;
    try {
      DefaultHttpClient client = new DefaultHttpClient();
      String url = this.repositoryUrl + "/stories/" + storyId + "/moves?move[move]=" + position + "&move[target]="
                   + targetStoryId;
      System.out.println(url);
      HttpPost httpPost = new HttpPost(url);

      httpPost.addHeader("X-TrackerToken", apiToken);

      HttpResponse httpResponse = client.execute(httpPost);
      HttpEntity entity = httpResponse.getEntity();
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder loader = factory.newDocumentBuilder();
      Document document = loader.parse(entity.getContent());
      NodeList nodes = document.getElementsByTagName("error");
      if (nodes.getLength() > 0) {
        response = nodes.item(0).getTextContent();
      } else {
        response = "success";
      }
    } catch (Exception e) {
      throw new Exception("Error while moving story.");
    }
    return response;
  }

  /**
   * Check whether the given story is in current lane
   *
   * @param storyId
   * @return
   */
  public boolean isCurrentStory(String storyId) {
    List<Story> stories = new ArrayList<Story>();
    boolean flag = false;
    try {
      int id = Integer.parseInt(storyId);
      stories = getCurrentIterations("current");
      for (Story story : stories) {
        if (story.getId() == id) {
          flag = true;
          break;
        }
      }
      System.out.println("Flag : " + flag);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return flag;
  }

  public String getApiToken() {
    return apiToken;
  }

  public static Gson gsonWithDate() {
    final GsonBuilder builder = new GsonBuilder();

    builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {

      final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

      public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                                                                                                 throws JsonParseException {
        try {
          return df.parse(json.getAsString());
        } catch (final java.text.ParseException e) {
          e.printStackTrace();
          return null;
        }
      }
    });

    builder.registerTypeAdapter(DateTime.class, new JsonDeserializer<DateTime>() {

      final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

      public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                                                                                                     throws JsonParseException {
        try {
          return new DateTime(df.parse(json.getAsString()));
        } catch (final java.text.ParseException e) {
          e.printStackTrace();
          return null;
        }
      }
    });

    return builder.create();
  }

  public PtConfiguration getConfiguration() {
    return configuration;
  }
}
