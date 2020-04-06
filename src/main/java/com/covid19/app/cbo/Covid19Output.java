package com.covid19.app.cbo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Covid19Output {
    private Map<String, List<Covid19Object>> covid19map = new HashMap<>();

    public Map<String, List<Covid19Object>> getCovid19map() {
        return covid19map;
    }

    public void setCovid19map(Map<String, List<Covid19Object>> covid19map) {
        this.covid19map = covid19map;
    }
}
