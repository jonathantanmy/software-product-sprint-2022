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

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;
import com.google.sps.data.SetOverview;

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
@WebServlet("/search-results")
public class searchResultsServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
String search = Jsoup.clean(request.getParameter("search"), Whitelist.none());
    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    Query<Entity> query;
    

        if(search.isEmpty()){
        query=  Query.newEntityQueryBuilder()
        .setKind("Sets")
        .setOrderBy(OrderBy.asc("setname"))
        .build();
        
        
        }
        else{
            query=  Query.newEntityQueryBuilder()
            .setKind("Sets")
            .setFilter(
            CompositeFilter.and(
            PropertyFilter.le("setname", search.toLowerCase()), PropertyFilter.ge("setname", search.toUpperCase())))
             .build();
            
        } 
       
    QueryResults<Entity> results = datastore.run(query);

    List<SetOverview> sets = new ArrayList<>();
    while (results.hasNext()) {
      Entity entity = results.next();

      long id = entity.getKey().getId();
      String creator = entity.getString("creator");
       String setname = entity.getString("setname");
       String set_database = entity.getString("set_database");
      long timestamp = entity.getLong("timestamp");
      int  term_amount= (int) entity.getLong("term_amount");
      String uid = entity.getString("uid");

      SetOverview set = new SetOverview( id, creator, set_database, setname,  term_amount,  timestamp, uid);
      sets.add(set);
    }

    Gson gson = new Gson();

    response.setContentType("application/json;");
    //response.getWriter().println(gson.toJson(sets));
    response.getWriter().write(gson.toJson(sets));
  }
}
