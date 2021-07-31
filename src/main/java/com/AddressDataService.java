package com;

import com.data.PersonAddress;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

public interface AddressDataService<E> {
	List<PersonAddress> getNearestAddressList(String address, int number) throws IOException, URISyntaxException;
}
