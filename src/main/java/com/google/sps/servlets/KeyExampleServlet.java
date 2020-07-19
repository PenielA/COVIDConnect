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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/keyExample")
public class KeyExampleServlet extends HttpServlet {

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Extracting key information from request data
    String keyString = request.getParameter("key");
    Key listingKey = KeyFactory.stringToKey(keyString);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // We need this try / catch block because when we call datastore.get() it's possible that it throws
    // EntityNotFoundException and this must be dealt with or the program crashes and burns lol.
    try {
      // Fetching key from datastore
      Entity listing = datastore.get(listingKey);

      // *** You can now use this entity in any way -- this entity is the listing and all its data ***
      // Example of extracting data from entity
      String subjectLine = (String) listing.getProperty("subject");
      String email = (String) listing.getProperty("email");
      String desc = (String) listing.getProperty("description");

      // Example of using extracted data
      response.setContentType("text/html;");
      response.getWriter().println("<h1>** TEST: Displaying Listing Information **</h1>");
      response.getWriter().println(String.format("<p>Subject line is: %s</p>", subjectLine));
      response.getWriter().println(String.format("<p>Email from post creator is: %s</p>", email));
      response.getWriter().println("<p>Description of listing is:</p>");
      response.getWriter().println(String.format("<p>%s</p>", desc));
    }
    catch (EntityNotFoundException e) {
      // If it's not found, do something
      response.setContentType("text/html");
      response.getWriter().println("<h1>Error: listing key not found in datastore!</h1>");
    }
  }

}