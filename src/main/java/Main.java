package com.challenge;

//general dependencies
import java.util.List;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.net.*;
import java.io.*;

//spring dependancies
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling //for the logger job
@RestController
@SpringBootApplication
public class Main {

  //stores the unique ids recieved each minute
  List<String> ids = new ArrayList<String>();

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }

  //the main endpoint
  @RequestMapping("/log")
  String logger(@RequestParam String id, @RequestParam(required = false) String endpoint) {

      //Count only if the ID is unique for all requests recieved in a minute
      if(!ids.contains(id))
        ids.add(id);

      //if endpoint is provided, then make the GET request
      if(endpoint != null) {
        try {
          int status = callHttpEndpoint(endpoint);

          //http error code
          if(status > 299) {
            logToFile("API call error : " + status);
          }
        }
        catch(MalformedURLException muex) {
          logToFile("API call error : Invalid URL");
        }
        catch(IOException ioex) {
          logToFile("API call error : Error opening connection");
        }
      }

      return "Recorded";
  }

  //Triggered every 60 seconds
  @Scheduled(fixedRate = 60000)
  public void loggingJob() {
      logToFile("Number of requests : " + ids.size()); //number of requests = number of unique ids per minute
      ids = new ArrayList<String>(); //clear the list
  }

  //a private method to handle the HTTP call
  private int callHttpEndpoint(String endpoint) throws MalformedURLException, IOException {
    URL url = new URL(endpoint);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("GET");
    con.disconnect();
    int status = con.getResponseCode();
    Reader streamReader = null;
    return status;
  }

  //a private method to handle writing to file
  private void logToFile(String content) {
    try {
      PrintWriter writer = new PrintWriter(new FileOutputStream(
        new File("log.txt"), 
        true /* append = true */));
      writer.println(new java.util.Date().toString() + "\t:\t" + content);
      writer.close();
    }
    catch(FileNotFoundException fex) {
      //file should be present. If not, NOP
    }
  }
}