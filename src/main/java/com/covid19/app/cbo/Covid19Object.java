package com.covid19.app.cbo;

public class Covid19Object implements Comparable<Covid19Object>{
    private int confirmed;
    private int deaths;
    private int recovered;
    private String lastUpdate;
    private int dailyGrowthRate;
    private int dailyGrowthCount;

    public int getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getRecovered() {
        return recovered;
    }

    public void setRecovered(int recovered) {
        this.recovered = recovered;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getDailyGrowthRate() {
        return dailyGrowthRate;
    }

    public void setDailyGrowthRate(int dailyGrowthRate) {
        this.dailyGrowthRate = dailyGrowthRate;
    }

    public int getDailyGrowthCount() {
        return dailyGrowthCount;
    }

    public void setDailyGrowthCount(int dailyGrowthCount) {
        this.dailyGrowthCount = dailyGrowthCount;
    }

    public int compareTo(Covid19Object obj){
        return this.lastUpdate.compareTo(obj.lastUpdate);
    }
}
