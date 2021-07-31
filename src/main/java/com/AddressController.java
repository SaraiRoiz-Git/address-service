package com;

import com.data.PersonAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping(produces = "application/json",path = "/api/address" )
public class AddressController {

	private AddressDataService addressDataService;

	@Autowired
	public AddressController(AddressDataService addressDataService) {
		this.addressDataService = addressDataService;
	}

	@CrossOrigin
	@GetMapping ("/nearby-address-list")
	public List<PersonAddress> replaceGivenWordInFile(@RequestParam String address, @RequestParam int number) throws IOException, URISyntaxException {
		return addressDataService.getNearestAddressList(address,number);
	}
}
