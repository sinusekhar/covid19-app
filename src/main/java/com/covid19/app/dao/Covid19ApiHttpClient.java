package com.covid19.app.dao;

import com.covid19.app.cbo.Covid19Data;
import com.covid19.app.cbo.Covid19Object;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class Covid19ApiHttpClient {
    private static CloseableHttpClient httpClient = HttpClients.createDefault();

    public void processDate(String date){
        try{
            HttpGet request = new HttpGet("https://covid.mathdro.id/api/daily/" + date);
            CloseableHttpResponse response = httpClient.execute(request);

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // return it as a String
                String result = EntityUtils.toString(entity);
                //System.out.println(result);

                List<JSONObject> list = new ArrayList<JSONObject>();
                JSONArray jsonArray = new JSONArray(result);
                if (jsonArray != null) {
                    int len = jsonArray.length();
                    for (int j = 0; j < len; j++) {
                        String country = jsonArray.getJSONObject(j).getString("countryRegion");
                        if(country.equals("US") || country.equals("China") || country.equals("Mainland China") || country.equals("Italy")
                                || country.equals("Iran") || country.equals("Spain") || country.equals("France") || country.equals("Germany")) {
                            if (jsonArray.getJSONObject(j).has("provinceState")) {
                                String province = jsonArray.getJSONObject(j).getString("provinceState");

                                Covid19Object covid19Object = new Covid19Object();
                                covid19Object.setConfirmed(Integer.parseInt(jsonArray.getJSONObject(j).getString("confirmed")));
                                covid19Object.setDeaths(Integer.parseInt(jsonArray.getJSONObject(j).getString("deaths")));
                                covid19Object.setRecovered(Integer.parseInt(jsonArray.getJSONObject(j).getString("recovered")));

                                StringTokenizer tokens = new StringTokenizer(date, "-");
                                if(tokens.countTokens() == 3){
                                    String month = tokens.nextToken();
                                    String dayOfMonth = tokens.nextToken();
                                    String year = tokens.nextToken();
                                    covid19Object.setLastUpdate(year + "-" + month + "-" + dayOfMonth);
                                }

                                Covid19Data.setCovid19Data(country, province, covid19Object);
                            }
                        }
                    }
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void main(String[] args){
        try{
            Covid19ApiHttpClient client = new Covid19ApiHttpClient();
            client.processDate("3-18-2020");
            client.processDate("3-17-2020");
            client.processDate("3-16-2020");

            System.out.println(Covid19Data.getCovid19Data());

            Map<String, Map<String, Map<String,Covid19Object>>> copy = Covid19Data.getCovid19Data();

            System.out.println(Covid19Data.getCovid19Data().get("US").entrySet());
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

}
