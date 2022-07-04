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
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;


import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.KeyFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * Takes an image submitted by the user and uploads it to Cloud Storage, and then displays it as
 * HTML in the response.
 */
@WebServlet("/upload")
@MultipartConfig
public class CreateFormHandlerServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // Get the message entered by the user array because of multiple input field of name=message
    String [] message = request.getParameterValues("message");
    long timestamp = System.currentTimeMillis();

     // Sanitize user input to remove HTML tags and JavaScript.
String setName = Jsoup.clean(request.getParameter("setName"), Whitelist.none());
String setNameKind=setName + System.currentTimeMillis();
String user = Jsoup.clean(request.getParameter("user"), Whitelist.none());
System.out.println(setNameKind);

Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
//the kind "SETS" will store all the sets created basic info (won't include the actual terms)
KeyFactory keyFactory = datastore.newKeyFactory().setKind("Sets");
FullEntity taskEntity =
    Entity.newBuilder(keyFactory.newKey())
        .set("setname", setName)
        .set("set_database", setNameKind)
        .set("creator", user)
        .set("term_amount", message.length)
        .set("timestamp", timestamp)
        .build();
datastore.put(taskEntity);
//-------------------------------------------------------------------------------


// Retrieves <input type="file" name="image" multiple="true">
List<Part> fileParts = request.getParts().stream().filter(part -> "image".equals(part.getName())).collect(Collectors.toList()); // Retrieves <input type="file" name="image" multiple="true">

// the counter since foreach loops don't count
int i=0;

//now will loop thru all the input field amount and stores the term and image url in datastore where Kind = setNameKind
for (Part filePart : fileParts) {
    PrintWriter out = response.getWriter();
    System.out.println("in for each");
    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); 
    String uniqueFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString()+System.currentTimeMillis();
   
    //if there is a file
    if (fileName.length() != 0) {
    InputStream fileContent = filePart.getInputStream();
    String uploadedFileUrl = uploadToCloudStorage(uniqueFileName, fileContent);
    
    // Output some HTML that shows the data the user entered.
  // You could also store the uploadedFileUrl in Datastore instead.
  
 // Sanitize user input to remove HTML tags and JavaScript.
String term = Jsoup.clean(message[i], Whitelist.none());
Datastore datastore2 = DatastoreOptions.getDefaultInstance().getService();
KeyFactory keyFactory2 = datastore.newKeyFactory().setKind(setNameKind);
FullEntity taskEntity2 =
    Entity.newBuilder(keyFactory2.newKey())
        .set("setname", setName)
        .set("term", term)
        .set("hasImage", true)
        .set("url", uploadedFileUrl)
        .set("imageName",uniqueFileName)
        .set("timestamp", timestamp)
        .build();
datastore2.put(taskEntity2);

  
  
    }else { // if there no image associated with term, the property for "url" and "imageName" will be null


        
 // Sanitize user input to remove HTML tags and JavaScript.
String term = Jsoup.clean(message[i], Whitelist.none());
Datastore datastore2 = DatastoreOptions.getDefaultInstance().getService();
KeyFactory keyFactory2 = datastore.newKeyFactory().setKind(setNameKind);
FullEntity taskEntity2 =
    Entity.newBuilder(keyFactory2.newKey())
        .set("setname", setName)
        .set("term", term)
        .set("hasImage", false)
        .set("url", "null")
        .set("imageName","null")
        .set("timestamp", timestamp)
        .build();
datastore2.put(taskEntity2);

    }
    i++;
}

    //redirect to index
    response.sendRedirect("/index.html");
}



  /** Uploads a file to Cloud Storage and returns the uploaded file's URL. */
  private static String uploadToCloudStorage(String fileName, InputStream fileInputStream) {
    String projectId = "summer22-sps-23";
    String bucketName = "summer22-sps-23.appspot.com";
    Storage storage =
        StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    BlobId blobId = BlobId.of(bucketName, fileName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

    // Upload the file to Cloud Storage.
    Blob blob = storage.create(blobInfo, fileInputStream);

    // Return the uploaded file's URL.
    return blob.getMediaLink();
  }
}
