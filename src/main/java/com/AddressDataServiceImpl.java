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

@Service
public class AddressDataServiceImpl implements AddressDataService {
	@Value( "${API_KEY}" )
	String key;
	@Value( "${filename}" )
	String fileName;

	Map<String,String> GoogleAddressToPersonMap = new HashMap<>();
	private ObjectMapper mapper = new ObjectMapper();

	@PostConstruct
	private void createListOfAddress() throws IOException, URISyntaxException {
		Map<String,String> addressToPersonMap = new LinkedHashMap<>();
		readFromFile(addressToPersonMap);
		createGoogleAddressList(addressToPersonMap);
	}

	private void readFromFile(Map<String, String> addressToPersonMap) throws IOException {
		URL filePath = getClass().getClassLoader().getResource(fileName);
		BufferedReader csvReader = new BufferedReader(new FileReader(filePath.getPath()));
		String row = csvReader.readLine();
		while ((row = csvReader.readLine()) != null) {
			PersonAddress personAddress = PersonAddress.fromCsv(row);
			addressToPersonMap.put(personAddress.getAddress(), personAddress.getName());
		}
		csvReader.close();
	}

	private void createGoogleAddressList(Map<String,String> addressToPersonMap) throws IOException, URISyntaxException {
		List<String> addressSet = new ArrayList<>(addressToPersonMap.keySet());
		List<GoogleData> googleResponse = getGoogleAddresses(addressSet, addressSet.size(), "london");
		createGoogleAddressMap( googleResponse,addressSet,addressToPersonMap);
	}

	@NotNull
	private List<GoogleData> getGoogleAddresses(List<String> addressSet, int size, String london) throws IOException, URISyntaxException {
		List<GoogleData> googleResponse = new ArrayList<>();
		int index = 0;
		while (index < size) {
			AppendAddress appendAddress = appendAddress(addressSet, index);
			index = appendAddress.getIndex();
			googleResponse.add(getDistanceListFromGoogleapis(london, appendAddress.getAddress()));
		}
		return googleResponse;
	}

	@Override
	public List<PersonDistance> getNearestAddressList(String address, int number) throws IOException, URISyntaxException {

		List<PersonDistance> personDistances = new ArrayList<>();
		List<String> addressSet = new ArrayList<>(GoogleAddressToPersonMap.keySet());
		List<GoogleData> googleResponse = getGoogleAddresses(addressSet, GoogleAddressToPersonMap.size(), address);
		googleResponse.forEach(response -> personDistances.addAll(createListOfClosetAddress(response)));
		personDistances.sort(Comparator.comparing(PersonDistance::getDistanceValue));
		return number > personDistances.size() ? personDistances :personDistances.subList(0, number);
	}
	private void createGoogleAddressMap(List<GoogleData> googleResponse,List<String> addressSet,Map<String,String> addressToPersonMap) {
			List<String> addresses = new ArrayList<>();
			googleResponse.forEach(res->addresses.addAll(res.getDestination_addresses()));
			for (int i =0; i<addresses.size();i++){
				GoogleAddressToPersonMap.put(addresses.get(i), addressToPersonMap.get(addressSet.get(i)));
			}
	}

	private List<PersonDistance> createListOfClosetAddress(GoogleData returnData) {
		List<PersonDistance> closeAddress = new ArrayList<>();
			if (returnData.getRows().size() > 0) {
				GoogleData.Row row = returnData.getRows().get(0);
				for (int i = 0; i < row.getElements().size(); i++) {
					if (row.getElements().get(i).getStatus().equals("OK")) {
						final String address = returnData.getDestination_addresses().get(i);
						int distanceValue = row.getElements().get(i).getDistance().getValue();
						String distanceText = row.getElements().get(i).getDistance().getText();
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

	private AppendAddress appendAddress(List<String> addressSet,int currentIndex){
		StringBuilder destinations = new StringBuilder();
			int tmpCounter = 0;
			for(int i = currentIndex; i<addressSet.size() && tmpCounter<15;i++, tmpCounter++){
				destinations.append(addressSet.get(i));
				destinations.append("|");
			}
			currentIndex +=15;

		return new AppendAddress(destinations.toString(), currentIndex);
	}
}

