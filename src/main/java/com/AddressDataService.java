package com;

import com.data.PersonAddress;

import java.io.IOException;
import java.util.List;

public interface AddressDataService<E> {

	List<String> getAllAddressList() throws IOException;

	List<PersonAddress> getNearestAddressList(String address, int number);
}
