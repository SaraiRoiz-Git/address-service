package com;


import com.data.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.ParameterStringBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.*;
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
	Map<String,String> GoogleAddressToPersonMap = new HashMap<>();
	private ObjectMapper mapper = new ObjectMapper();

	@PostConstruct
	private void createListOfAddress() throws IOException {
		URL filePath = getClass().getClassLoader().getResource(fileName);
		BufferedReader csvReader = new BufferedReader(new FileReader(filePath.getPath()));
		String row = csvReader.readLine();
		while ((row = csvReader.readLine()) != null) {
			PersonAddress personAddress = PersonAddress.fromCsv(row);
			addressToPersonMap.put(personAddress.getAddress(), personAddress.getName());
			createGoogleAddressList();
		}
		csvReader.close();
	}

	private void createGoogleAddressList() {


	}

	@Override
	public Set<String> getAllAddressList(){
		return addressToPersonMap.keySet();
	}



	@Override
	public List<PersonDistance> getNearestAddressList(String address, int number) throws IOException, URISyntaxException {
		int index = 0;
		List<PersonDistance> personDistances = new ArrayList<>();
		List<GoogleData> googleResponse = new ArrayList<>();
		while (index < addressToPersonMap.size()) {
			AppendAddress appendAddress = appendAddress(index);
			index = appendAddress.getIndex();
			googleResponse.add(getDistanceListFromGoogleapis(address, appendAddress.getAddress()));
		}
		getNameByGoogleAddress(googleResponse);
		googleResponse.forEach(response -> personDistances.addAll(createListOfClosetAddress(response)));
		personDistances.sort(Comparator.comparing(PersonDistance::getDistanceValue));
		return number > personDistances.size() ? personDistances.subList(0, number) : personDistances;
	}

	private void getNameByGoogleAddress(List<GoogleData> googleResponse) {
		if (GoogleAddressToPersonMap.isEmpty()) {
			for (GoogleData response:googleResponse) {
				List<String> addresses = response.getDestination_addresses();
				for (int i =0; i<addresses.size();i++){
					String currAddress = addresses.get(i);
					if(!currAddress.isEmpty()){
						String addressesFirstPart = currAddress.toLowerCase().substring(0, currAddress.indexOf(","));
						for (Map.Entry<String,String> entry :addressToPersonMap.entrySet()){
							if(entry.getKey().toLowerCase(Locale.ROOT).contains(addressesFirstPart)){
								GoogleAddressToPersonMap.put(currAddress,entry.getValue());
								break;
							}
						}
					}

				}
			}
		}
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
						String name = GoogleAddressToPersonMap.get(address);
						closeAddress.add(new PersonDistance(name, address, distanceValue, distanceText));
					}
				}
			}
		return closeAddress;
	}

	private GoogleData getDistanceListFromGoogleapis(String origins , String destinations) throws IOException, URISyntaxException {
		URI uri = getUri(origins, destinations);
		OkHttpClient client = new OkHttpClient().newBuilder()
				.build();
		Request request = getRequest(uri);
		Response response = client.newCall(request).execute();
		String responseString = response.body().string();
		return mapper.readValue(responseString, GoogleData.class);
	}

	@NotNull
	private Request getRequest(URI uri) throws MalformedURLException {
		Request request = new Request.Builder()
				.url(uri.toURL())
				.method("GET", null)
				.addHeader("Accept", "application/json")
				.addHeader("Content-Type", "application/json")
				.build();
		return request;
	}

	private URI getUri(String origins, String destinations) throws URISyntaxException {
		HttpGet httpGet = new HttpGet("https://maps.googleapis.com/maps/api/distancematrix/json");
		URI uri = new URIBuilder(httpGet.getURI())
				.addParameter("origins", origins)
				.addParameter("destinations", destinations)
				.addParameter("key", "AIzaSyDfRDr-Nsv7zEw2CEsoLuNqEEU6R6nVObQ" )
				.build();
		return uri;
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

