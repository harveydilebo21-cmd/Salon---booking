package com.salon.booking;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ServiceCatalog {

    private static final int DEFAULT_MINUTES = 60;
    private static final Map<String, Integer> MINUTES = new LinkedHashMap<>();

    static {
        MINUTES.put("Haircut", 60);
        MINUTES.put("Colour", 120);
        MINUTES.put("Braids", 180);
        MINUTES.put("Styling", 60);
    }
    private ServiceCatalog(){

    }
    public static List<String> names(){
        return List.copyOf(MINUTES.keySet());
    }
    public static int minutesFor(String service) {
        return MINUTES.getOrDefault(service,DEFAULT_MINUTES);
    }
    public static int maxMinutes(){
        return MINUTES.values().stream().mapToInt(Integer::intValue).max().orElse(DEFAULT_MINUTES);
    }
}
