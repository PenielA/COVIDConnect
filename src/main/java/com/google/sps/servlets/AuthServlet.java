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

  private static class UserData {
    public String loginUrl = "";
    public String logoutUrl = "";
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    response.setContentType("application/json;");
    UserData userData = new UserData();
    String urlToRedirectTo= "/index.html";

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      userData.logoutUrl = userService.createLogoutURL(urlToRedirectTo);   
      response.getWriter().println(gson.toJson(userData));
    } else {
      String urlToRedirectToAfterUserLogsIn = "/index.html";
      userData.loginUrl = userService.createLoginURL(urlToRedirectTo);
      response.getWriter().println(gson.toJson(userData));
    }
  }
}