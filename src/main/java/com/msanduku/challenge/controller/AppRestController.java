package com.msanduku.challenge.controller;

import com.msanduku.challenge.lib.ExcelReader;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Clifford Owino
 */
@RestController
@RequestMapping("/file")
public class AppRestController {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    Calendar currenttime = Calendar.getInstance();
  
    public AppRestController() {
        //required public constructor
    }

    @RequestMapping(value = "/{fileName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getProcessingData(@PathVariable("fileName") String fileName) {
        LOG.info("request initiate : "+(new Timestamp(System.currentTimeMillis()))); 
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<String> future = executorService.submit(new ExcelReader(fileName));
        JSONObject jsonObject = null;
        String responseData = null;
        boolean listen = true;
        while (listen) {
            if (future.isDone()) {
                LOG.info("request return : "+(new Timestamp(System.currentTimeMillis()))); 
                try {
                    responseData = future.get();
                    jsonObject = new JSONObject(future.get());
                    listen = false;

               } catch (InterruptedException | ExecutionException | JSONException ex) {
                   LOG.info("request error : "+(new Timestamp(System.currentTimeMillis()))); 
                   listen = false;
                   ex.printStackTrace();
               return new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
               }
            }
        }

        return new ResponseEntity<>(jsonObject, HttpStatus.OK);        
    }
}
