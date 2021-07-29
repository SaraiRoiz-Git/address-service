package com;

import com.data.PersonAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(consumes = "application/json",produces = "application/json",path = "/api/address" )
public class AddressController {

	private AddressDataService addressDataService;

	@Autowired
	public AddressController(AddressDataService addressDataService) {
		this.addressDataService = addressDataService;
	}

	@GetMapping("/address-list")
	public List<String> countGivenWordInFile() throws IOException {
		return addressDataService.getAllAddressList();
	}
	
	@PostMapping("/nearby-address-list")
	public List<PersonAddress> replaceGivenWordInFile(@RequestParam String address, @RequestParam int number) {
		return addressDataService.getNearestAddressList(address,number);
	}
}
