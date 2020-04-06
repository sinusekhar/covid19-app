package com.covid19.app.cbo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class Covid19Data {
    private static Map<String, String> provinceLookupMap = new HashMap<String, String>() {{
        put("AL","Alabama");
        put("AK","Alaska");
        put("AZ","Arizona");
        put("AR","Arkansas");
        put("CA","California");
        put("CO","Colorado");
        put("CT","Connecticut");
        put("DE","Delaware");
        put("FL","Florida");
        put("GA","Georgia");
        put("HI","Hawaii");
        put("ID","Idaho");
        put("IL","Illinois");
        put("IN","Indiana");
        put("KS","Kansas");
        put("KY","Kentucky");
        put("LA","Louisiana");
        put("ME","Maine");
        put("IA","Iowa");
        put("MD","Maryland");
        put("MA","Massachusetts");
        put("MI","Michigan");
        put("MN","Minnesota");
        put("MS","Mississippi");
        put("MO","Missouri");
        put("MT","Montana");
        put("NE","Nebraska");
        put("NV","Nevada");
        put("NH","New Hampshire");
        put("NJ","New Jersey");
        put("NM","New Mexico");
        put("NY","New York");
        put("NC","North Carolina");
        put("ND","North Dakota");
        put("OH","Ohio");
        put("OK","Oklahoma");
        put("OR","Oregon");
        put("PA","Pennsylvania");
        put("RI","Rhode Island");
        put("SC","South Carolina");
        put("SD","South Dakota");
        put("TN","Tennessee");
        put("TX","Texas");
        put("UT","Utah");
        put("VT","Vermont");
        put("VA","Virginia");
        put("WA","Washington");
        put("WV","West Virginia");
        put("WI","Wisconsin");
    }};

    private static Map<String, Map<String,Map<String,Covid19Object>>> covid19Data = new HashMap();

    public static void setCovid19Data(String country, String province, Covid19Object covid19Object){
        //Country correction
        country = countryCorrection(country);
        //Province Correction
        province = provinceCorrections(province);

        if(covid19Data.get(country) == null){
            Map<String,Map<String,Covid19Object>> provinceMap = createNewProvinceMap(covid19Object, province);

            covid19Data.put(country,provinceMap);
        }else{
            Map<String,Map<String,Covid19Object>> countryMap = covid19Data.get(country);

            if(countryMap.get(province) == null){
                addToExistingProvinceMap(covid19Object, province, countryMap);
            }else{
                Map<String,Covid19Object> dateMap = countryMap.get(province);

                if(dateMap == null){
                    dateMap = new HashMap<>();
                    dateMap.put(covid19Object.getLastUpdate(),covid19Object);
                }else{
                    Covid19Object existingObj = dateMap.get(covid19Object.getLastUpdate());

                    if(existingObj == null){
                        dateMap.put(covid19Object.getLastUpdate(), covid19Object);
                    }else {
                        existingObj.setConfirmed(existingObj.getConfirmed() + covid19Object.getConfirmed());
                        existingObj.setDeaths(existingObj.getDeaths() + covid19Object.getDeaths());
                        existingObj.setRecovered(existingObj.getRecovered() + covid19Object.getRecovered());
                    }

                }

            }
        }
    }

    public static Map<String, Map<String,Map<String,Covid19Object>>> getCovid19Data(){
        return covid19Data;
    }

    private static void addToExistingProvinceMap(Covid19Object covid19Object, String province,Map<String,Map<String,Covid19Object>>  provinceMap){
        Map<String,Covid19Object> dateObj = new HashMap<>();
        dateObj.put(covid19Object.getLastUpdate(),covid19Object);

        provinceMap.put(province, dateObj);
    }

    private static Map<String,Map<String,Covid19Object>> createNewProvinceMap(Covid19Object covid19Object, String province){
        Map<String,Covid19Object> dateObj = new HashMap<>();
        dateObj.put(covid19Object.getLastUpdate(),covid19Object);

        Map<String,Map<String,Covid19Object>> provinceMap = new HashMap<>();
        provinceMap.put(province,dateObj);

        return provinceMap;
    }

    private static String provinceCorrections(String province){
        if(province.indexOf(',') != -1){
            StringTokenizer tokens = new StringTokenizer(province,",");

            if(tokens.countTokens() > 1){
                String lastToken = null;
                while(tokens.hasMoreTokens()){
                    lastToken = tokens.nextToken();
                }

                if(lastToken.equals("(From Diamond Princess)")){
                    lastToken.replaceAll("(From Diamond Princess)","");
                }

                String cleanState = provinceLookupMap.get(lastToken.trim());

                if(cleanState == null)
                    return province;
                else{
                    return cleanState;
                }
            }else{
                return province;
            }
        }else{
            if(province == null || province.trim().equals(""))
                province = "NoProvince";

            return province;
        }
    }

    public static void calculateDailyGrowthRate(List<Covid19Object> dateList){
        float growthRate = 0;
        int previousConfirmed = 0;
        int previousDeaths = 0;
        int previousRecovered = 0;
        for(Covid19Object data:dateList){
            if(previousConfirmed == 0)
            {
                data.setDailyGrowthRate((int)growthRate);
                previousConfirmed = data.getConfirmed();
                previousDeaths = data.getDeaths();
                previousRecovered = data.getRecovered();
                data.setDailyGrowthCount(0);
            }else{
                growthRate = (((float)data.getConfirmed() - (float)previousConfirmed)/(float)data.getConfirmed()) * 100;
                if(growthRate < 0)
                    growthRate = 0;

                data.setDailyGrowthRate((int)growthRate);
                data.setDailyGrowthCount(data.getConfirmed() - previousConfirmed);
                previousConfirmed = data.getConfirmed();


                if(data.getDailyGrowthCount() < 0)
                    data.setDailyGrowthCount(0);

                if(previousDeaths > data.getDeaths())
                    data.setDeaths(previousDeaths);
                else
                    previousDeaths = data.getDeaths();
                if(previousRecovered > data.getRecovered())
                    data.setRecovered(previousRecovered);
                else
                    previousRecovered = data.getRecovered();

            }
        }
    }

    public static void calculateCountryTrends(){
        Map<String,Map<String, Covid19Object>> countryAggMap = new HashMap<>();
        for(String country: covid19Data.keySet()){
            if(country.equals("US") || country.equals("China") || country.equals("Italy") || country.equals("Iran")
                    || country.equals("Spain") || country.equals("France") || country.equals("Germany")) {
                for (String state : covid19Data.get(country).keySet()){
                    Map<String,Covid19Object> dateMap = covid19Data.get(country).get(state);

                    for(String date: dateMap.keySet()){
                        if(countryAggMap.get(country) == null){
                            Map<String,Covid19Object> tmpDateEntry = new HashMap<>();
                            tmpDateEntry.put(date, dateMap.get(date));

                            countryAggMap.put(country, tmpDateEntry);
                        }else{
                            //Date doesn't exist for the country
                            if(countryAggMap.get(country).get(date) == null){
                                countryAggMap.get(country).put(date, dateMap.get(date));
                            }else{
                                Covid19Object obj = dateMap.get(date);
                                Covid19Object existingObj = countryAggMap.get(country).get(date);

                                existingObj.setConfirmed(existingObj.getConfirmed() + obj.getConfirmed());
                                existingObj.setDeaths(existingObj.getDeaths() + obj.getDeaths());
                                existingObj.setRecovered(existingObj.getRecovered() + obj.getRecovered());
                            }

                        }
                    }
                }
            }
        }

        covid19Data.get("US").put("US",countryAggMap.get("US"));
        covid19Data.get("China").put("China",countryAggMap.get("China"));
        covid19Data.get("Italy").put("Italy",countryAggMap.get("Italy"));
        covid19Data.get("Iran").put("Iran",countryAggMap.get("Iran"));
        covid19Data.get("Spain").put("Spain",countryAggMap.get("Spain"));
        covid19Data.get("France").put("France",countryAggMap.get("France"));
        covid19Data.get("Germany").put("Germany",countryAggMap.get("Germany"));

    }

    private static String countryCorrection(String country){
        if(country.equals("Mainland China"))
            return "China";
        else
            return country;
    }

}
