package com;

import com.data.PersonDistance;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface AddressDataService<E> {
	List<PersonDistance> getNearestAddressList(String address, int number) throws IOException, URISyntaxException;
}
