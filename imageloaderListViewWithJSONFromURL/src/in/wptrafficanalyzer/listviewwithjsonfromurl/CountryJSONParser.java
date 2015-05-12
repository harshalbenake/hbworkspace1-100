package in.wptrafficanalyzer.listviewwithjsonfromurl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** A class to parse json data */
public class CountryJSONParser {
	
	// Receives a JSONObject and returns a list
	public List<HashMap<String,Object>> parse(JSONObject jObject){		
		
		JSONArray jCountries = null;
		try {		
			// Retrieves all the elements in the 'countries' array 
			jCountries = jObject.getJSONArray("countries");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		 // Invoking getCountries with the array of json object
		 // where each json object represent a country
		return getCountries(jCountries);
	}
	
	
	private List<HashMap<String, Object>> getCountries(JSONArray jCountries){
		int countryCount = jCountries.length();
		List<HashMap<String, Object>> countryList = new ArrayList<HashMap<String,Object>>();
		HashMap<String, Object> country = null;	

		// Taking each country, parses and adds to list object 
		for(int i=0; i<countryCount;i++){
			try {
				// Call getCountry with country JSON object to parse the country 
				country = getCountry((JSONObject)jCountries.get(i));
				countryList.add(country);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return countryList;
	}
	
	// Parsing the Country JSON object 
	private HashMap<String, Object> getCountry(JSONObject jCountry){

		HashMap<String, Object> country = new HashMap<String, Object>();
		String countryName = "";
		String flag="";
		String language = "";
		String capital = "";
		String currencyCode = "";
		String currencyName = "";		
		
		try {
			countryName = jCountry.getString("countryname");
			flag = jCountry.getString("flag");
			language = jCountry.getString("language");
			capital = jCountry.getString("capital");
			currencyCode = jCountry.getJSONObject("currency").getString("code");
			currencyName = jCountry.getJSONObject("currency").getString("currencyname");
			
			String details =        "Language : " + language + "\n" +
                    "Capital : " + capital + "\n" +
                    "Currency : " + currencyName + "(" + currencyCode + ")";
			
			country.put("country", countryName);
			country.put("flag", R.drawable.blank);
			country.put("flag_path", flag);
			country.put("details", details);
			
		} catch (JSONException e) {			
			e.printStackTrace();
		}		
		return country;
	}
}