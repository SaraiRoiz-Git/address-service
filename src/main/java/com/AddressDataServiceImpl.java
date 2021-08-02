package com;

import com.data.*;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    @Value("${API_KEY}")
    String key;
    @Value("${filename}")
    String fileName;
    @Value("${location}")
    String location;
    private Map<String, String> GoogleAddressToPersonMap = new LinkedHashMap<>();
    private List<String> appendAddresses= new ArrayList<>();
    private ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    private void createListOfAddress() throws IOException, URISyntaxException {
        Map<String, String>addressToPersonMap = createAddressMapFromFile();
        List<String>addressSet = new LinkedList<>(addressToPersonMap.keySet());
        createListOfAppendedAddresses(addressSet);
        createGoogleAddressList(addressToPersonMap,addressSet);
    }

    private Map<String, String> createAddressMapFromFile( ) throws IOException {
        Map<String, String> addressToPersonMap = new LinkedHashMap<>();
        URL filePath = getClass().getClassLoader().getResource( fileName);
        BufferedReader csvReader = new BufferedReader(new FileReader(filePath.getPath()));
        String row = csvReader.readLine();
        while ((row = csvReader.readLine()) != null) {
            String[] values = row.split(",",2);
            addressToPersonMap.put(values[1], values[0]);
        }
        csvReader.close();
        return addressToPersonMap;
    }

    private void createGoogleAddressList(Map<String, String> addressToPersonMap, List<String>addressSet) throws IOException, URISyntaxException {
        List<GoogleData> googleResponse = getGoogleAddresses(location);
        createGoogleAddressMap(googleResponse, addressSet, addressToPersonMap);
    }

    @NotNull
    private List<GoogleData> getGoogleAddresses(String location) throws IOException, URISyntaxException {
        List<GoogleData> googleResponse = new LinkedList<>();
        for (String list : appendAddresses) {
            googleResponse.add(getDistanceListFromGoogleapis(location, list));
        }
        return googleResponse;
    }

    private void createListOfAppendedAddresses(List<String> addressSet) {
        int index = 0;
        while (index < addressSet.size()) {
            String currAppendAddress = appendAddressesBlock(addressSet, index);
            index +=25;
            appendAddresses.add(currAppendAddress);
        }
    }

//    @NotNull
//    private List<GoogleData> getGoogleAddresses(List<String> addressSet, String location) throws IOException, URISyntaxException {
//        List<GoogleData> googleResponse = new LinkedList<>();
//        int index = 0;
//        while (index < addressSet.size()) {
//            AppendAddress appendAddress = appendAddress(addressSet, index);
//            index = appendAddress.getIndex();
//            googleResponse.add(getDistanceListFromGoogleapis(location, appendAddress.getAddress()));
//        }
//        return googleResponse;
//    }

    @Override
    public List<PersonDistance> getNearestAddressList(String address, int number) throws IOException, URISyntaxException {
        List<PersonDistance> personDistances = new LinkedList<>();
        List<GoogleData> googleResponse = getGoogleAddresses(address);
        googleResponse.forEach(response -> personDistances.addAll(createListOfClosetAddress(response)));
        personDistances.sort(Comparator.comparing(PersonDistance::getDistanceValue));
        return number > personDistances.size() ? personDistances : personDistances.subList(0, number);
    }

    private void createGoogleAddressMap(List<GoogleData> googleResponse, List<String> addressSet, Map<String, String> addressToPersonMap) {
        List<String> addresses = new LinkedList<>();
        googleResponse.forEach(res -> addresses.addAll(res.getDestination_addresses()));
        for (int i = 0; i < addresses.size(); i++) {
            GoogleAddressToPersonMap.put(addresses.get(i), addressToPersonMap.get(addressSet.get(i)));
        }
    }

    private List<PersonDistance> createListOfClosetAddress(GoogleData returnData) {
        List<PersonDistance> closeAddress = new LinkedList<>();
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

    private GoogleData getDistanceListFromGoogleapis(String origins, String destinations) throws IOException, URISyntaxException {
        URI uri = getUri(origins, destinations);
        OkHttpClient client = new OkHttpClient().newBuilder().build();
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
                .addParameter("key", "AIzaSyDfRDr-Nsv7zEw2CEsoLuNqEEU6R6nVObQ")
                .addParameter("region", "UK")
                .build();
        return uri;
    }

    private String appendAddressesBlock(List<String> addressSet, int currentIndex) {
        StringBuilder destinations = new StringBuilder();
        int tmpCounter = 0;
        for (int i = currentIndex; i < addressSet.size() && tmpCounter < 25; i++) {
            destinations.append(addressSet.get(i));
            destinations.append("|");
        }
        return destinations.toString();
    }


}

