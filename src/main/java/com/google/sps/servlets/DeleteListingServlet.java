// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/deleteListing")
public class DeleteListingServlet extends HttpServlet {

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Extracting key information from request data
    String keyString = request.getParameter("key");
    Key listingKey = KeyFactory.stringToKey(keyString);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String currentUserId = userService.getCurrentUser().getUserId();
      try {
        Entity foundListing = datastore.get(listingKey);
        String listingUserId = (String) foundListing.getProperty("userId");
        if (currentUserId.equals(listingUserId)) {
          datastore.delete(listingKey);
          response.sendRedirect("/listings.html");
          System.out.println("Post successfully removed");
        }
        else { // listing does not belong to user
          response.setContentType("text/html");
          response.getWriter().println("<h1>Error: You tried to delete a post that was not yours!</h1>");
        }
      }
      catch (EntityNotFoundException e) { // listing not found
        // If it's not found, do something
        response.setContentType("text/html");
        response.getWriter().println("<h1>Error: listing key not found in datastore!</h1>");
      }
    }
    else { // user is not even logged in.
      response.setContentType("text/html");
      response.getWriter().println("<h1>Error: You cannot delete a listing if you're not logged in...</h1>");
    }
  }

}