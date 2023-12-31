package com.example.weatherReport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Controller
public class WeatherController {
	
	@GetMapping("/index")
	public String indexPage() {
		return "index";
	}
	
	//GET FORECAST DATA
	@GetMapping("/forecast")
	public String getForecastData(Model model, @RequestParam String param) {

		// param = Pass US Zipcode, UK Postcode, Canada Postalcode, IP address,
		// Latitude/Longitude (decimal degree) or city name
		//HashMap<String, String> hourHM = new HashMap<>();
		List<Object> listHourHM = new ArrayList<>();
		RestTemplate restTemplate = new RestTemplate();
		String response;
		try {
			response = restTemplate
					.getForObject("http://api.weatherapi.com/v1/forecast.json?key=b64f769068a6432281a165936231211&q="
							+ param + "&days=1&aqi=no&alerts=no\r\n", String.class);
		} catch (Exception e) {
			//return an error page
			return "errorPage";
		}
		
		Gson gson = new Gson();
		JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
		JsonObject current = jsonObject.get("current").getAsJsonObject();			
		JsonObject condition = current.get("condition").getAsJsonObject();			
		String text = condition.get("text").getAsString();
		String temp_c = current.get("temp_c").getAsString();
		String humidity = current.get("humidity").getAsString();
		
		JsonObject location = jsonObject.get("location").getAsJsonObject();	
		String name = location.get("name").getAsString();
		
	    JsonObject forecast = jsonObject.get("forecast").getAsJsonObject();		
	    JsonArray forecastdayArray = forecast.get("forecastday").getAsJsonArray();
	    JsonObject forecastdayChildJson_0 = forecastdayArray.get(0).getAsJsonObject();
	    JsonObject day = forecastdayChildJson_0.get("day").getAsJsonObject();
	    double maxTempC = day.get("maxtemp_c").getAsDouble();	    
		double minTempC = day.get("mintemp_c").getAsDouble();

		// int forecastdaySize = forecastday.size();
		// for(int i=0; i<forecastdaySize; i++) {}
		JsonArray hourJsonArray = forecastdayChildJson_0.get("hour").getAsJsonArray();

		for (int i = 0; i < hourJsonArray.size(); i++) {
			JsonObject hourChildJson = hourJsonArray.get(i).getAsJsonObject();
			String timeString = hourChildJson.get("time").getAsString();
			String time = getTimeAmPm(timeString);
			HashMap<String, String> hourHM = new HashMap<>();
			JsonObject Hourcondition = hourChildJson.get("condition").getAsJsonObject();
			String icon = Hourcondition.get("icon").getAsString();
			hourHM.put("temp_c", (hourChildJson.get("temp_c").getAsString()));
			hourHM.put("time", time);
			hourHM.put("humidity", (hourChildJson.get("humidity").getAsString()));
			hourHM.put("icon", icon);
			listHourHM.add(hourHM);
		}

		model.addAttribute("temp_c", temp_c);
		model.addAttribute("text", text);
		model.addAttribute("humidity", humidity);
		model.addAttribute("maxTempC", maxTempC);
		model.addAttribute("minTempC", minTempC);
		model.addAttribute("name", name);
		model.addAttribute("listHourHM", listHourHM);
		return "weatherData";
	}

	private String getTimeAmPm(String timeString) {
		
		String[] parts = timeString.split(" ");
    	String railwayTime = parts[1];
    	
    	String[] timeParts = railwayTime.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        //int minutes = Integer.parseInt(timeParts[1]);

        String amPm = (hours >= 12) ? "PM" : "AM";
        int adjustedHours = (hours % 12 == 0) ? 12 : hours % 12; // Handle 12 AM/PM

       // String formattedTime = adjustedHours + ":" + String.format("%02d", minutes) + " " + amPm;
        String formattedTime = adjustedHours +" "+ amPm;

		return formattedTime;
	}


	@GetMapping("/realtime")
	public String getRealtimeData( @RequestParam String param) {
		// param = Pass US Zipcode, UK Postcode, Canada Postalcode, IP address, 
		//Latitude/Longitude (decimal degree) or city name
		RestTemplate restTemplate = new RestTemplate();
		String response = restTemplate.getForObject("https://api.weatherapi.com/v1/current.json?q="+param+"&key=b64f769068a6432281a165936231211", String.class);
        
		Gson gson = new Gson();
		JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
		
		JsonObject locationObject = jsonObject.getAsJsonObject("location");
		JsonObject currentObject = jsonObject.getAsJsonObject("current");
		
		String name = locationObject.get("name").getAsString();
		String region = locationObject.get("region").getAsString();
		String country = locationObject.get("country").getAsString();
		String localtime = locationObject.get("localtime").getAsString();
		String temp_c = currentObject.get("temp_c").getAsString();
		String temp_f = currentObject.get("temp_f").getAsString();
		String wind_mph = currentObject.get("wind_mph").getAsString();
		String wind_kph = currentObject.get("wind_kph").getAsString();
		String wind_dir = currentObject.get("wind_dir").getAsString();
		String pressure_mb = currentObject.get("pressure_mb").getAsString();
		String pressure_in = currentObject.get("pressure_in").getAsString();
		String humidity = currentObject.get("humidity").getAsString();
		String feelslike_c = currentObject.get("feelslike_c").getAsString();
		String feelslike_f = currentObject.get("feelslike_f").getAsString();
		String uv = currentObject.get("uv").getAsString();
		
        
        LinkedHashMap<String, String> weatherMap = new LinkedHashMap<>();
        weatherMap.put("name", name);
        weatherMap.put("region", region);
        weatherMap.put("country", country);
        weatherMap.put("localtime", localtime);
        weatherMap.put("temp_c", temp_c);
        weatherMap.put("temp_f", temp_f);
        weatherMap.put("wind_mph", wind_mph);
        weatherMap.put("wind_kph", wind_kph);
        weatherMap.put("wind_dir", wind_dir);
        weatherMap.put("pressure_mb", pressure_mb);
        weatherMap.put("pressure_in", pressure_in);
        weatherMap.put("humidity", humidity);
        weatherMap.put("feelslike_c", feelslike_c);
        weatherMap.put("feelslike_f", feelslike_f);
        weatherMap.put("uv", uv);
        

//        model.addAttribute("weatherMap", weatherMap);        
//        model.addAttribute("temp_c", temp_c);        
		
		return "temp_c";		
	}
	
	
	


}
