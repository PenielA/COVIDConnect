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
import java.util.ArrayList;

@WebServlet("/listings")
public class ListingServlet extends HttpServlet {

  private static class Listing {
    private String subject;
    private String description;
    private long timestamp;

    public Listing(String subject, String desc, long time) {
      this.subject = subject;
      this.description = desc;
      this.timestamp = time;
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
      Listing newListing = new Listing((String) e.getProperty("subject"),
                                       (String) e.getProperty("description"),
                                       (long) e.getProperty("timestamp"));
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
    long timestamp = System.currentTimeMillis();

    // Get Datastore Service
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // Build entity
    Entity newListing = new Entity("Listing");
    newListing.setProperty("subject", subject);
    newListing.setProperty("description", desc);
    newListing.setProperty("timestamp", timestamp);

    datastore.put(newListing);
    response.sendRedirect("/listings.html");
  }
}