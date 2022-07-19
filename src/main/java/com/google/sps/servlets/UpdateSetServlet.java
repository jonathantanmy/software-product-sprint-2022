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
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.datastore.Key;
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
@WebServlet("/update")
@MultipartConfig
public class UpdateSetServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    //long timestamp = System.currentTimeMillis();
    

    //get the setId [id of the set general info in the "Sets" kind]
    long setId = Long.parseLong(request.getParameter("setId"));

    //use to update the setName
    String setName = Jsoup.clean(request.getParameter("setName"), Whitelist.none());

    //get the kind for which all the term are stored at for this set
    String setKind = Jsoup.clean(request.getParameter("setKind"), Whitelist.none());

    //gets the amount of terms there are now
    long term_amount = Long.parseLong(Jsoup.clean(request.getParameter("term_amout"), Whitelist.none()));

    // Get the deleted terms -- the all deleted term's id is stored here 
    String [] deleteIds = request.getParameterValues("deleteId");


    /*
    *The follow param are all arrays and will all be the length of the exisiting terms in the set
    */
    // Get all the id of the term
    String [] setIds = request.getParameterValues("set_id");

    //gets all the exisitng term's value here 
    String [] old_messages = request.getParameterValues("old_message");

    //stores true/false-- see if the exisiing term had an image associated with it
    String [] had_img = request.getParameterValues("had_img");

    //stores true/false-- see if the user changed the image
    String [] changed_img = request.getParameterValues("changed_img");

    
    //stores the name of all the img name for the existing terms in the set ["null" if no image]
    String [] old_img_name = request.getParameterValues("changed_img");

        
    // Retrieves <input type="file" name="image" multiple="true">
    List<Part> old_fileParts = request.getParts().stream().filter(part -> "old_image".equals(part.getName())).collect(Collectors.toList()); // Retrieves <input type="file" name="image" multiple="true">


    /**
     * the following is for the new terms to be added to the set
     * basically be the same as the CreateFormHandlerServlet
     */

    // Get the message entered by the user array because of multiple input field of name=message
    String [] message = request.getParameterValues("message");

    // Retrieves <input type="file" name="image" multiple="true">
    List<Part> fileParts = request.getParts().stream().filter(part -> "image".equals(part.getName())).collect(Collectors.toList()); // Retrieves <input type="file" name="image" multiple="true">


   
Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
KeyFactory keyFactory = datastore.newKeyFactory().setKind(setKind);
//delete the deleteID from the datastore and the images associated with it in the bucket
if(deleteIds != null){ // if it not null , means there are some terms that needs to be deleted
    for (String deleteId : deleteIds) 
    {
        long id = Long.parseLong(deleteId);
        Key setKey = keyFactory.newKey(id);
        Entity set = datastore.get(setKey);

        Boolean has_image =set.getBoolean("hasImage");
        if(has_image){ // of has an image-> need to delete that from bucket(cloud storage)
            String imageName = set.getString("imageName");
            deleteObject(imageName);//deletes the img
        }

        //delete the term
        datastore.delete(setKey);

    }
} // if deletedIds not null
int old_i=0;
for (Part  old_filePart :  old_fileParts) {
    String fileName = Paths.get(old_filePart.getSubmittedFileName()).getFileName().toString(); 
    String uniqueFileName = Paths.get(old_filePart.getSubmittedFileName()).getFileName().toString()+System.currentTimeMillis();

    long id = Long.parseLong(Jsoup.clean(setIds[old_i], Whitelist.none()));

    if (fileName.length() != 0) {//meaning there is a file

        boolean hadImageBoolean=Boolean.parseBoolean(had_img[old_i]);  
        if(hadImageBoolean){ //if had image, need to delete the old img 
            String imgName = Jsoup.clean(old_img_name[old_i], Whitelist.none());//get the old image name
            deleteObject(imgName);//deletes the img
            

        InputStream fileContent = old_filePart.getInputStream();
        String uploadedFileUrl = uploadToCloudStorage(uniqueFileName, fileContent);
        Key setKey = keyFactory.newKey(id);
        Entity set = datastore.get(setKey);

        //update the image name
        set = Entity.newBuilder(set).set("imageName", uniqueFileName).build();
        datastore.put(set);

        //update the image url
        set = Entity.newBuilder(set).set("url", uploadedFileUrl).build();
        datastore.put(set);
        } else { // adding an image
                
            InputStream fileContent = old_filePart.getInputStream();
            String uploadedFileUrl = uploadToCloudStorage(uniqueFileName, fileContent);
            Key setKey = keyFactory.newKey(id);
            Entity set = datastore.get(setKey);

            //update the image name
            set = Entity.newBuilder(set).set("imageName", uniqueFileName).build();
            datastore.put(set);

                
            //update the image url
            set = Entity.newBuilder(set).set("url", uploadedFileUrl).build();
            datastore.put(set);

             //update the hasImage to be true
             set = Entity.newBuilder(set).set("hasImage", true).build();
             datastore.put(set);

        }// else of if-else


    } else {

        //if there no file, and the term used to have an img associated with it, delete it 
        boolean hadImageBoolean=Boolean.parseBoolean(had_img[old_i]);  
        if(hadImageBoolean){
            String imgName = Jsoup.clean(old_img_name[old_i], Whitelist.none());//get the old image name
            deleteObject(imgName);//deletes the img
            Key setKey = keyFactory.newKey(id);
            Entity set = datastore.get(setKey);

            //update the hasImage 
            set = Entity.newBuilder(set).set("hasImage", false).build();
            datastore.put(set);

             //update the image url 
             set = Entity.newBuilder(set).set("url", "null").build();
             datastore.put(set);

             //update the imageName 
            set = Entity.newBuilder(set).set("imageName", "null").build();
            datastore.put(set);
        }

    }
    old_i++;    

}//for each of old_fileParts

//the following for-each will update the term value on exisiting terms 
int j=0;
if(old_messages!=null){
for (String  old_message : old_messages) {
    String term = Jsoup.clean(old_message, Whitelist.none());
    long id = Long.parseLong(Jsoup.clean(setIds[j], Whitelist.none()));
    Key setKey = keyFactory.newKey(id);
    Entity set = datastore.get(setKey);

    //update the term [regardless if it has changed or not]
    set = Entity.newBuilder(set).set("term", term).build();
    datastore.put(set);

    //update the setname
    set = Entity.newBuilder(set).set("setname", setName).build();
    datastore.put(set);


    j++;
}// for-each on update the term value
}

    /**NOW ONTO ADDING NEW TERMS INTO THE SET IF THERE ARE NEW ONES TO ADD */
    int k=0;
    if(fileParts!=null){
        for (Part filePart : fileParts) {
            long timestamp = System.currentTimeMillis();
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); 
            String uniqueFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString()+System.currentTimeMillis();
            
        //if there is a file
        if (fileName.length() != 0) {
            
            InputStream fileContent = filePart.getInputStream();
            String uploadedFileUrl = uploadToCloudStorage(uniqueFileName, fileContent);
            
            // Output some HTML that shows the data the user entered.
        // You could also store the uploadedFileUrl in Datastore instead.
        
        // Sanitize user input to remove HTML tags and JavaScript.
        String term = Jsoup.clean(message[k], Whitelist.none());
    
        FullEntity taskEntity =
            Entity.newBuilder(keyFactory.newKey())
                .set("setname", setName)
                .set("term", term)
                .set("hasImage", true)
                .set("url", uploadedFileUrl)
                .set("imageName",uniqueFileName)
                .set("timestamp", timestamp)
                .build();
        datastore.put(taskEntity);
        
        
        
            }else { // if there no image associated with term, the property for "url" and "imageName" will be null
        
        
                
        // Sanitize user input to remove HTML tags and JavaScript.
        String term = Jsoup.clean(message[k], Whitelist.none());
    
        FullEntity taskEntity =
            Entity.newBuilder(keyFactory.newKey())
                .set("setname", setName)
                .set("term", term)
                .set("hasImage", false)
                .set("url", "null")
                .set("imageName","null")
                .set("timestamp", timestamp)
                .build();
        datastore.put(taskEntity);
        
            }
            k++;
        }
        

    }

    /**NOW TO UPDATE THE INFO IN THE "SETS" KIND */
    Datastore datastore2 = DatastoreOptions.getDefaultInstance().getService();
    KeyFactory keyFactory2 = datastore2.newKeyFactory().setKind("Sets");
    Key setEntityKey = keyFactory2.newKey(setId);
    Entity setsGeneral = datastore2.get(setEntityKey);

    //update the setName
    setsGeneral = Entity.newBuilder(setsGeneral).set("setname",setName).build();
    datastore2.put(setsGeneral);

    //update the term_amount
    setsGeneral = Entity.newBuilder(setsGeneral).set("term_amount",term_amount).build();
    datastore2.put(setsGeneral);

    String creator = setsGeneral.getString("creator");


//redirect to view the set after modification
response.sendRedirect("/viewSet.html?kind="+setKind+"&C="+creator+"&A="+term_amount+"&T="+setName);

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



  public static void deleteObject(String objectName) {
    // The ID of your GCP project
     String projectId = "summer22-sps-23";

    // The ID of your GCS bucket
     String bucketName = "summer22-sps-23.appspot.com";

    // The ID of your GCS object
    // String objectName = "your-object-name";

    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    storage.delete(bucketName, objectName);

    System.out.println("Object " + objectName + " was deleted from " + bucketName);
  }
} // the class ending bracket
