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

import java.util.*;  
import com.google.gson.Gson;
import java.util.ArrayList;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.users.User;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AuthServlet")
public class AuthServlet extends HttpServlet {

  private static class UserData {
    public String loginUrl = "";
    public String logoutUrl = "";
    public User info = null;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    UserData userData = new UserData();
    String urlToRedirectTo= "/index.html";

    String currentPage = request.getParameter("currentPage");

    // tests whether user is loogged in or not and add appropriate links to ArrayList
    // to then send to the javascript to update nav bar accordingly
    
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      userData.logoutUrl = userService.createLogoutURL(urlToRedirectTo);
      userData.info = userService.getCurrentUser();
      //if a user is logged in check to see if they are in DS by id
      //if they aren't in DS add their UserData class w associated email,idand empty name property
      //first time logging in popup alert, default username is first part of your email, change this and add additional info in settings
      
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    
      Query query = new Query("userData");
      FilterPredicate filter = new FilterPredicate("id", FilterOperator.EQUAL,userService.getCurrentUser().getUserId());
      query.setFilter(filter);
      PreparedQuery results = datastore.prepare(query);
      int matches = results.countEntities(FetchOptions.Builder.withLimit(10));
      System.out.println("num matches:" + matches);//test
      if (matches == 0){
        Entity userDataEntity = new Entity("userData");
        userDataEntity.setProperty("email", userData.info.getEmail());
        userDataEntity.setProperty("id", userData.info.getUserId());
        userDataEntity.setProperty("username", userData.info.getNickname());
        datastore.put(userDataEntity);
        response.getWriter().println(gson.toJson(userData));
      }
      else{
        response.getWriter().println(gson.toJson(userData));
      }
    //   for (Entity entity : results.asIterable()) {
    //     if (entity.getProperty("id") == userData.info.getUserId()) {
    //         String entityid = entity.getProperty("id");
    //         String userid = userData.info.getUserId();
    //         System.out.println(entityid + " :::: " userid);
    } else {
      userData.loginUrl = userService.createLoginURL(currentPage);
      response.getWriter().println(gson.toJson(userData));
    }
  }
}