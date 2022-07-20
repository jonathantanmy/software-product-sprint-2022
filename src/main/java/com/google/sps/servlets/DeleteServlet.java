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

import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;
import com.google.sps.data.Set;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible for listing tasks. */
@WebServlet("/delete")
public class DeleteServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      
        
    String uid = Jsoup.clean(request.getParameter("uid"), Whitelist.none());

    String kind = Jsoup.clean(request.getParameter("kind"), Whitelist.none());

    //get the setId [id of the set general info in the "Sets" kind]
    long setId = Long.parseLong(Jsoup.clean(request.getParameter("Id"), Whitelist.none()));

    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    
    KeyFactory keyFactory = datastore.newKeyFactory().setKind(kind);
    Query<Entity> query =
        Query.newEntityQueryBuilder().setKind(kind).setOrderBy(OrderBy.desc("timestamp")).build();
    QueryResults<Entity> results = datastore.run(query);

    List<Set> sets = new ArrayList<>();
    while (results.hasNext()) {
      Entity entity = results.next();

      long id = entity.getKey().getId();
      Key setKey = keyFactory.newKey(id);
      //if the term has an image, delete it
      if(entity.getBoolean("hasImage")){
          //deletes the img
        deleteObject(entity.getString("imageName"));
      }
      //deletes the term
      datastore.delete(setKey);

    }
    //delete the set from "Sets"
    KeyFactory keyFactory2 = datastore.newKeyFactory().setKind("Sets");
    Key setKey2 = keyFactory2.newKey(setId);
    datastore.delete(setKey2);

    
    
    //redirect to view the Mysets after deletion
    response.sendRedirect("/myset.html?uid="+uid);
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
}
