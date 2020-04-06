package com.covid19.app;

import com.amazonaws.services.lambda.runtime.Context;
import com.covid19.app.cbo.*;
import com.covid19.app.dao.Covid19ApiHttpClient;
import com.covid19.app.dao.S3DataAccessService;
import org.json.*;

import java.util.*;


public class Covid19DataProcessor {
    private static Calendar startDate = Calendar.getInstance();
    private static Covid19ApiHttpClient httpClient = new Covid19ApiHttpClient();
    private static final String bucketName = "covid19-ss";
    private static final int spacesToIndentEachLevel = 2;

    public String handleRequest(Input input, Context context) {

        try {
            Map<String,Map<String, List<Covid19Object>>> outputMap = new HashMap<>();
//            context.getLogger().log("Input: " + input);

            //Set Start Date
            startDate.set(Calendar.DAY_OF_MONTH, 2);
            startDate.set(Calendar.MONTH, Calendar.FEBRUARY);
            startDate.set(Calendar.YEAR, 2020);

            //End Date
            Calendar now = Calendar.getInstance();

            do {
                System.out.println((startDate.get(Calendar.MONTH) + 1)
                        + "-" + startDate.get(Calendar.DAY_OF_MONTH)
                        + "-" + startDate.get(Calendar.YEAR));

                httpClient.processDate(formatDateMonth(startDate.get(Calendar.MONTH) + 1)
                        + "-" + formatDateMonth(startDate.get(Calendar.DAY_OF_MONTH))
                        + "-" + startDate.get(Calendar.YEAR));

                Thread.sleep(1000);

                startDate.add(Calendar.DAY_OF_MONTH, 1);
            } while (!(now.get(Calendar.DAY_OF_MONTH) + 1 == startDate.get(Calendar.DAY_OF_MONTH)
                    && now.get(Calendar.MONTH) == startDate.get(Calendar.MONTH)
                    && now.get(Calendar.YEAR) == startDate.get(Calendar.YEAR)));

            System.out.println(Covid19Data.getCovid19Data());

            //Calculate US Trend
            Covid19Data.calculateCountryTrends();

            //Process US states
            Map<String, Map<String, Covid19Object>> stateMap = Covid19Data.getCovid19Data().get("US");
            Map<String, List<Covid19Object>> stateOutputMap = new HashMap<>();
            for (String state : stateMap.keySet()) {
                for (String date : stateMap.get(state).keySet()) {
                    if (stateOutputMap.get(state) == null) {
                        Covid19Object obj = stateMap.get(state).get(date);
                        List<Covid19Object> arr = new ArrayList<>();
                        arr.add(obj);
                        stateOutputMap.put(state, arr);
                    } else {
                        List<Covid19Object> arr = stateOutputMap.get(state);
                        Covid19Object obj = stateMap.get(state).get(date);
                        arr.add(obj);
                    }
                }
                List<Covid19Object> dateList = stateOutputMap.get(state);
                Collections.sort(dateList);
                Covid19Data.calculateDailyGrowthRate(dateList);
            }
            outputMap.put("US",stateOutputMap);

            //Process non US countries
            Map<String, List<Covid19Object>> nonUsStateOutputMap = new HashMap<>();
            for(String country: Covid19Data.getCovid19Data().keySet()){
                if(!country.equals("US")){
                    for(String state: Covid19Data.getCovid19Data().get(country).keySet()){
                        if(state.equals("China") || state.equals("Iran") || state.equals("Italy")
                                || state.equals("Spain") || country.equals("France") || country.equals("Germany")) {
                            for (String date : Covid19Data.getCovid19Data().get(country).get(state).keySet()) {
                                if (nonUsStateOutputMap.get(state) == null) {
                                    Covid19Object obj = Covid19Data.getCovid19Data().get(country).get(state).get(date);
                                    List<Covid19Object> arr = new ArrayList<>();
                                    arr.add(obj);
                                    nonUsStateOutputMap.put(state, arr);
                                } else {
                                    List<Covid19Object> arr = nonUsStateOutputMap.get(state);
                                    Covid19Object obj = Covid19Data.getCovid19Data().get(country).get(state).get(date);
                                    arr.add(obj);
                                }
                            }
                            List<Covid19Object> dateList = nonUsStateOutputMap.get(state);
                            Collections.sort(dateList);
                            Covid19Data.calculateDailyGrowthRate(dateList);
                        }
                    }
                }
            }
            outputMap.put("other_countries",nonUsStateOutputMap);

            JSONObject jsonObject = new JSONObject(outputMap);
            System.out.println(jsonObject);

            String today = (now.get(Calendar.MONTH) + 1) + "-" + (now.get(Calendar.DAY_OF_MONTH)) + "-" + now.get(Calendar.YEAR);
            S3DataAccessService s3DataAccessService = new S3DataAccessService();
            s3DataAccessService.writeToS3(bucketName, "covid19-us-" + today + ".json", jsonObject.toString(2));
            s3DataAccessService.writeToS3(bucketName, "covid19-us.json", jsonObject.toString(spacesToIndentEachLevel));
//            s3DataAccessService.writeToS3(bucketName, "covid19-us-debug.json", jsonObject.toString(spacesToIndentEachLevel));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "Hello World - " + input;
    }

    public static void main(String[] args) {
        Covid19DataProcessor processor = new Covid19DataProcessor();
        System.out.println(processor.handleRequest(new Input(), null));
    }

    private String formatDateMonth(int num){
        if(num < 10)
            return "0" + num;
        else
            return ""+num;
    }
}
