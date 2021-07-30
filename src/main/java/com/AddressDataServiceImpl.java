package com;


import com.data.Address;
import com.data.PersonAddress;
import com.utils.ParameterStringBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AddressDataServiceImpl implements AddressDataService {
	@Value( "${API_KEY}" )
	String key;
	@Value( "${filename}" )
	String fileName;
	private int index=0;
	List<PersonAddress> addressList = new ArrayList<>();

	@PostConstruct
	private void createListOfAddress() throws IOException {
		URL filePath = getClass().getClassLoader().getResource(fileName);
		BufferedReader csvReader = new BufferedReader(new FileReader(filePath.getPath()));
		String row = csvReader.readLine();
		while ((row = csvReader.readLine()) != null) {
			addressList.add(PersonAddress.fromCsv(row));
		}
		csvReader.close();
	}

	@Override
	public List<String> getAllAddressList(){
		List<String> addressOnlyList = addressList.stream()
				.map(Address::getAddress)
				.collect(Collectors.toList());
		return addressOnlyList;
	}



	@Override
	public List<PersonAddress> getNearestAddressList(String address, int number) throws IOException {
		while (index> addressList.size()){
			getDistanceListFromGoogleapis(address,appendAddress());
		}

		return null;
	}

	private void getDistanceListFromGoogleapis(String origins , String destinations) throws IOException {
	URL url = new URL("https://maps.googleapis.com/maps/api/distancematrix/json");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");

		Map<String, String> parameters = new HashMap<>();
		parameters.put("origins", origins);
		parameters.put("destinations", destinations);
		parameters.put("key", key);
		con.setDoOutput(true);
		DataOutputStream out = new DataOutputStream(con.getOutputStream());
		out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
		out.flush();
		out.close();

		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);




//
//		https://maps.googleapis.com/maps/api/distancematrix/json?origins=Boston,MA|Charlestown,MA&destinations=Lexington,MA|Concord,MA&departure_time=now&key=YOUR_API_KEY
//		return 4;
//

	}

	private String appendAddress(){
		StringBuilder destanation = new StringBuilder();
		while (destanation.length() + addressList.get(index).getAddress().length() < 7500 && index< addressList.size()){
			destanation.append(addressList.get(index).getAddress());
			destanation.append('|');
			index++;
		}
		destanation.delete(destanation.length()-1,1);
		return destanation.toString();
	}
}

