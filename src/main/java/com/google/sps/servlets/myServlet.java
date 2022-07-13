package com.google.sps.servlets;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Handles requests sent to the /hello URL. Try running a server and navigating to /hello! */

@WebServlet("/list")
public class myServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    //response.setContentType("text/html;");
    //response.getWriter().println("List of cars");

   
  // List<String> myCarList = new ArrayList<>();
  // myCarList.add("bmw");
  // myCarList.add("Toyota");
   //myCarList.add("Mazda");
   //myCarList.add("<h3>Mazda</h3>");
 String[] myCarList = {"Volvo", "BMW", "Ford", "Mazda", "Hyundai", "Lamborghini", "maybach", "Porsche", "Tesla", 
 "Kia", "Jeep", "Volkswagon", "Audi", "Fiat", "Land Rover", "Bugatti", "Jaguar", "Ferrari", "Honda", "Maserati"};

    // Convert  to JSON
    Gson gson = new Gson();
   String json = gson.toJson(myCarList);
     
   // Send the JSON as the response
    response.setContentType("application/json;");
     response.getWriter().println(json);
   
     

   
   }
  

  }