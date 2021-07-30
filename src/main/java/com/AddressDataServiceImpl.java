package com;


import com.data.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.ParameterStringBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AddressDataServiceImpl implements AddressDataService {
	@Value( "${API_KEY}" )
	String key;
	@Value( "${filename}" )
	String fileName;


//	List<PersonAddress> addressList = new ArrayList<>();
	Map<String,String> addressToPersonMap = new HashMap<>();
	private ObjectMapper mapper = new ObjectMapper();

	@PostConstruct
	private void createListOfAddress() throws IOException {
		URL filePath = getClass().getClassLoader().getResource(fileName);
		BufferedReader csvReader = new BufferedReader(new FileReader(filePath.getPath()));
		String row = csvReader.readLine();
		while ((row = csvReader.readLine()) != null) {
			PersonAddress personAddress = PersonAddress.fromCsv(row);
			addressToPersonMap.put(personAddress.getAddress(), personAddress.getName());
		}
		csvReader.close();
	}

	@Override
	public Set<String> getAllAddressList(){
		return addressToPersonMap.keySet();
	}



	@Override
	public List<PersonDistance> getNearestAddressList(String address, int number) throws IOException, URISyntaxException {
		int index = 0;
		//create http req
		//add retern data to list
		List<PersonDistance> personDistances = new ArrayList<>();
		while (index < addressToPersonMap.size()){
			AppendAddress appendAddress = appendAddress(index);
			index = appendAddress.getIndex();
			GoogleData distanceListFromGoogleapis = getDistanceListFromGoogleapis(address, appendAddress.getAddress());
			personDistances.addAll(createListOfClosetAddress(distanceListFromGoogleapis));
		}
		//sort list
//		Collections.sort(returnData);


//		List<PersonDistance> listOfClosetAddress = createListOfClosetAddress(number, returnData);
		personDistances.sort(Comparator.comparing(PersonDistance::getDistanceValue));

		//return sub list size n;
		return number > personDistances.size() ? personDistances.subList(0, number) : personDistances;
	}

	private List<PersonDistance> createListOfClosetAddress(GoogleData returnData) {
		List<PersonDistance> closeAddress = new ArrayList<>();

			if (returnData.getRows().size() > 0) {
				GoogleData.Row row = returnData.getRows().get(0);
				for (int j = 0; j < row.getElements().size(); j++) {
					if (row.getElements().get(j).getStatus().equals("OK")) {
						final String address = returnData.getDestination_addresses().get(j);
						int distanceValue = row.getElements().get(j).getDistance().getValue();
						String distanceText = row.getElements().get(j).getDistance().getText();
						String name = addressToPersonMap.get(address.substring(0, address.indexOf(",")));
						closeAddress.add(new PersonDistance(address, distanceValue, distanceText));
					}
				}
			}
		return closeAddress;
	}

	private GoogleData getDistanceListFromGoogleapis(String origins , String destinations) throws IOException, URISyntaxException {
		HttpGet httpGet = new HttpGet("https://maps.googleapis.com/maps/api/distancematrix/json");

		URI uri = new URIBuilder(httpGet.getURI())
				.addParameter("origins", origins)
				.addParameter("destinations", destinations)
				.addParameter("key", "AIzaSyDfRDr-Nsv7zEw2CEsoLuNqEEU6R6nVObQ" )
				.build();
		OkHttpClient client = new OkHttpClient().newBuilder()
				.build();

		Request request = new Request.Builder()
				.url(uri.toURL())
				.method("GET", null)
				.addHeader("Accept", "application/json")
				.addHeader("Content-Type", "application/json")
				.build();
		Response response = client.newCall(request).execute();
		String responseString = response.body().string();
		return mapper.readValue(responseString, GoogleData.class);
	}

	private AppendAddress appendAddress(int currentIndex){
		List<String> addresses = new ArrayList(addressToPersonMap.keySet());

		StringBuilder destinations = new StringBuilder();
		while (currentIndex< addresses.size() && destinations.length() + addresses.get(currentIndex).length() < 500){
			destinations.append(addresses.get(currentIndex));
			destinations.append('|');
			currentIndex++;
		}
		return new AppendAddress(destinations.toString(), currentIndex);
	}
}

