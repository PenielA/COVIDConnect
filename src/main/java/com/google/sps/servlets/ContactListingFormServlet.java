package com.google.sps.servlets;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import javax.activation.DataHandler;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

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
import com.google.appengine.api.datastore.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.util.ArrayList;

@WebServlet("/contactListingform")
public class ContactListingFormServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String keyString = request.getParameter("key");
        System.out.println(keyString);
        Key keyObject = KeyFactory.stringToKey(keyString);
 
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        try {
            Entity listing = datastore.get(keyObject);
            String email = (String) listing.getProperty("email");
            // ***change this to get user name instead of user id
            // String userId = (String) listing.getProperty("userId");
            //added System.out for testing and debigging
            System.out.println(email);
            //can use email for API here
            String message = request.getParameter("message");
            if (message != null){
                response.getWriter().print("Your email was sent!");
                sendSimpleMail(email, message);
            }
        } catch (EntityNotFoundException e) {
            // If it's not found, do something
            System.out.println ("Error: listing key not found in datastore!");
        }
    }
 
    private void sendSimpleMail(String email, String message) {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("pabebe@sps-program.com", "COVIDConnect"));
            msg.addRecipient(Message.RecipientType.TO,
                            new InternetAddress(email, "User")); // needs fixing: change user to user's name
            msg.setSubject("You have a new message about your listing");
            msg.setText(message);
            Transport.send(msg, msg.getAllRecipients());
            // session.setDebug(true);
        } catch (AddressException e) {
          // ...
          e.printStackTrace();
        } catch (MessagingException e) {
          // ...
          e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
          // ...
          e.printStackTrace();
        } 
    }
}

  