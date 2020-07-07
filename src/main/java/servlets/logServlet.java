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
import java.util.ArrayList;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/logServlet")
public class logServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    response.setContentType("application/json;");
    ArrayList<String> logInfo = new ArrayList<>();
    // response.setContentType("text/html;");
 

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String urlToRedirectToAfterUserLogsOut = "/index.html";
      String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);

      logInfo.add("Logout");
      logInfo.add(logoutUrl);    
      response.getWriter().println(gson.toJson(logInfo));
    // response.getWriter().println(logoutUrl);
    } else {
      String urlToRedirectToAfterUserLogsIn = "/index.html";
      String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);

      logInfo.add("Login");
      logInfo.add(loginUrl);
      response.getWriter().println(gson.toJson(logInfo));  
    // response.getWriter().println(loginUrl);
    }
  }
}