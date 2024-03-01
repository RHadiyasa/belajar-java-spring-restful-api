package com.bash.bank_sampah.restful.controller;

import com.bash.bank_sampah.restful.entity.User;
import com.bash.bank_sampah.restful.model.AddressResponse;
import com.bash.bank_sampah.restful.model.CreateAddressRequest;
import com.bash.bank_sampah.restful.model.UpdateAddressRequest;
import com.bash.bank_sampah.restful.model.WebResponse;
import com.bash.bank_sampah.restful.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AddressController {
    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping(value = "/api/contacts/{contactId}/addresses",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<AddressResponse> create(User user,
                                               @RequestBody CreateAddressRequest createAddressRequest,
                                               @PathVariable("contactId") String contactId) {
        createAddressRequest.setContactId(contactId);
        AddressResponse addressResponse = addressService.create(user, createAddressRequest);
        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }

    @GetMapping(path = "/api/contacts/{contactId}/addresses/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<AddressResponse> get(User user,
                                            @PathVariable("contactId") String contactId,
                                            @PathVariable("addressId") String addressId) {
        AddressResponse addressResponse = addressService.get(user, contactId, addressId);
        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }

    @PutMapping(value = "/api/contacts/{contactId}/addresses/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<AddressResponse> update(User user,
                                               @RequestBody UpdateAddressRequest updateAddressRequest,
                                               @PathVariable("contactId") String contactId,
                                               @PathVariable("addressId") String addressId) {
        updateAddressRequest.setContactId(contactId);
        updateAddressRequest.setAddressId(addressId);

        AddressResponse addressResponse = addressService.update(user, updateAddressRequest);
        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }

    @DeleteMapping(path = "/api/contacts/{contactId}/addresses/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> remove(User user,
                                      @PathVariable("contactId") String contactId,
                                      @PathVariable("addressId") String addressId) {
        addressService.remove(user, contactId, addressId);
        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(path = "/api/contacts/{contactId}/addresses",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<List<AddressResponse>> list(User user, @PathVariable("contactId") String contactId){

        List<AddressResponse> addressResponses = addressService.list(user, contactId);
        return WebResponse.<List<AddressResponse>>builder().data(addressResponses).build();
    }
}
