package com.google.sps.servlets;

import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import java.util.List;
import java.util.Map;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.util.ArrayList;

@WebServlet("/listings")
public class ListingServlet extends HttpServlet {

  private static class Listing {
    private String uniqueKey;
    private String subject;
    private String description;
    private String imageUrl;
    private String email;
    private String userId;
    private long timestamp;
    private boolean contactForm;

    public Listing(Key uniqueKey, String subject, String desc, String imageUrl, String email, String userId, long time, boolean contactForm) {
      this.uniqueKey = KeyFactory.keyToString(uniqueKey);
      this.subject = subject;
      this.description = desc;
      this.imageUrl = imageUrl;
      this.email = email;
      this.userId = userId;
      this.timestamp = time;
      this.contactForm = contactForm;
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Needed variables
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ArrayList<Listing> listings = new ArrayList<>();

    // Query datastore for comments by earliest timestamp
    Query query = new Query("Listing").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    // Add each listing to our array
    for (Entity e : results.asIterable()) {
      Listing newListing = new Listing((Key) e.getKey(),
                                       (String) e.getProperty("subject"),
                                       (String) e.getProperty("description"),
                                       (String) e.getProperty("imageUrl"),
                                       (String) e.getProperty("email"),
                                       (String) e.getProperty("userId"),
                                       (long) e.getProperty("timestamp"),
                                       (boolean) e.getProperty("contactForm"));
      listings.add(newListing);
    }

    Gson gson = new Gson();
    String json = gson.toJson(listings);
    response.setContentType("application/json");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get data from client request
    String subject = request.getParameter("subject");
    String desc = request.getParameter("description");
    String imageUrl = getUploadedFileUrl(request,"image");
    String email = request.getParameter("email");
    String userId = request.getParameter("userId");

    long timestamp = System.currentTimeMillis();

    // Get Datastore Service
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // Build entity
    Entity newListing = new Entity("Listing");
    newListing.setProperty("subject", subject);
    newListing.setProperty("description", desc);
    newListing.setProperty("email", email);
    newListing.setProperty("userId", userId);
    newListing.setProperty("timestamp", timestamp);
    newListing.setProperty("image", imageUrl);
    newListing.setProperty("contactForm", false);


    datastore.put(newListing);
    response.sendRedirect("/listings.html");
    
}
private String getUploadedFileUrl(HttpServletRequest request, String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get("image");

    // User submitted form without selecting a file, so we can't get a URL. (dev server)
    if (blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }

    // Our form only contains a single file input, so get the first index.
    BlobKey blobKey = blobKeys.get(0);

    // User submitted form without selecting a file, so we can't get a URL. (live server)
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }
   // Use ImagesService to get a URL that points to the uploaded file.
    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);
    String url = imagesService.getServingUrl(options);

    // GCS's localhost preview is not actually on localhost,
    // so make the URL relative to the current domain.
    if(url.startsWith("http://localhost:8080/")){
      url = url.replace("http://localhost:8080/", "/");
    }
    return url;
  }
}

  