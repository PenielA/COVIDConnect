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
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.util.ArrayList;

@WebServlet("/toggle")
public class ToggleServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();

    String keyString = request.getParameter("key");
    System.out.println(keyString);
    Key keyObject = KeyFactory.stringToKey(keyString);
    response.setContentType("application/json");

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    try {
        Entity listing = datastore.get(keyObject);
        if ("false".equals(listing.getProperty("contactForm"))) {
            listing.setProperty("contactForm", "true");
            datastore.put(listing);
            System.out.println("change true:" + gson.toJson(listing));
            response.getWriter().println(gson.toJson(listing));
        } else {
            listing.setProperty("contactForm", "false");
            datastore.put(listing);
            System.out.println("change false:" + gson.toJson(listing));
            response.getWriter().println(gson.toJson(listing));
        }   
    }
    catch (EntityNotFoundException e) {
      // If it's not found, do something
      String text = "Error: listing key not found in datastore!";
      System.out.println (text);
      response.getWriter().println(text);
    }
}
}

  