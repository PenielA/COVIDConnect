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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AuthServlet")
public class AuthServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    response.setContentType("application/json;");
    ArrayList<String> logInfo = new ArrayList<>();

    // tests whether user is loogged in or not and add appropriate links to ArrayList
    // to then send to the javascript to update nav bar accordingly
    
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String logoutUrl = userService.createLogoutURL("/index.html");
      logInfo.add("Logout");
      logInfo.add(logoutUrl);    
      response.getWriter().println(gson.toJson(logInfo));
    } else {
      String indexloginUrl = userService.createLoginURL("/index.html");
      String listloginUrl = userService.createLoginURL("/listings.html");
      String nlistloginUrl = userService.createLoginURL("/new-listing.html");
      logInfo.add("Login");
      logInfo.add(indexloginUrl);
      logInfo.add(listloginUrl);
      logInfo.add(nlistloginUrl);
      response.getWriter().println(gson.toJson(logInfo));  
    }
  }
}