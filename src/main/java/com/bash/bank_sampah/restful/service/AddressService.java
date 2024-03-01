package com.bash.bank_sampah.restful.service;

import com.bash.bank_sampah.restful.entity.Address;
import com.bash.bank_sampah.restful.entity.Contact;
import com.bash.bank_sampah.restful.entity.User;
import com.bash.bank_sampah.restful.model.AddressResponse;
import com.bash.bank_sampah.restful.model.ContactResponse;
import com.bash.bank_sampah.restful.model.CreateAddressRequest;
import com.bash.bank_sampah.restful.model.UpdateAddressRequest;
import com.bash.bank_sampah.restful.repository.AddressRepository;
import com.bash.bank_sampah.restful.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final ContactRepository contactRepository;
    private ValidationService validationService;

    @Autowired
    public AddressService(AddressRepository addressRepository, ContactRepository contactRepository, ValidationService validationService) {
        this.addressRepository = addressRepository;
        this.contactRepository = contactRepository;
        this.validationService = validationService;
    }

    public AddressResponse create(User user, CreateAddressRequest createAddressRequest){
        validationService.validate(createAddressRequest);

        Contact contact = contactRepository.findFirstByUserAndId(user, createAddressRequest.getContactId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Contact not Found"));

        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setContact(contact);
        address.setStreet(createAddressRequest.getStreet());
        address.setCity(createAddressRequest.getCity());
        address.setProvince(createAddressRequest.getProvince());
        address.setCountry(createAddressRequest.getCountry());
        address.setPostalCode(createAddressRequest.getPostalCode());

        addressRepository.save(address);

        return toAddressResponse(address);
    }

    private AddressResponse toAddressResponse(Address address){
        return AddressResponse.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .province(address.getProvince())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .build();
    }

    @Transactional(readOnly = true)
    public AddressResponse get(User user, String contactId, String addressId){
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Contact not Found"));

        Address address = addressRepository.findFirstByContactAndId(contact, addressId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Address not Found"));

        return toAddressResponse(address);
    }

    @Transactional
    public AddressResponse update(User user, UpdateAddressRequest updateAddressRequest){
        validationService.validate(updateAddressRequest);
        Contact contact = contactRepository.findFirstByUserAndId(user, updateAddressRequest.getContactId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Contact not Found"));

        Address address = addressRepository.findFirstByContactAndId(contact, updateAddressRequest.getAddressId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Address not Found"));

        address.setStreet(updateAddressRequest.getStreet());
        address.setCity(updateAddressRequest.getCity());
        address.setProvince(updateAddressRequest.getProvince());
        address.setCountry(updateAddressRequest.getCountry());
        address.setPostalCode(updateAddressRequest.getPostalCode());

        addressRepository.save(address);

        return toAddressResponse(address);
    }

    public void remove(User user, String contactId, String addressId){
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Contact not Found"));

        Address address = addressRepository.findFirstByContactAndId(contact, addressId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Address not Found"));

        addressRepository.delete(address);
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> list(User user, String contactId){
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Contact not Found"));

        List<Address> addresses = addressRepository.findAllByContact(contact);
        return addresses.stream().map(this::toAddressResponse).toList();
    }

}
