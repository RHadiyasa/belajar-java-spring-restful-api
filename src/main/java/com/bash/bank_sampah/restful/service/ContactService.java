package com.bash.bank_sampah.restful.service;

import com.bash.bank_sampah.restful.entity.Contact;
import com.bash.bank_sampah.restful.entity.User;
import com.bash.bank_sampah.restful.model.*;
import com.bash.bank_sampah.restful.repository.ContactRepository;
import jakarta.persistence.OneToMany;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ContactService {
    private final ContactRepository contactRepository;

    private ValidationService validationService; // Injection

    @Autowired
    public ContactService(ContactRepository contactRepository, ValidationService validationService) {
        this.contactRepository = contactRepository;
        this.validationService = validationService;
    }

    public ContactResponse create(User user, CreateContactRequest createContactRequest) {
        validationService.validate(createContactRequest);

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName(createContactRequest.getFirstName());
        contact.setLastName(createContactRequest.getLastName());
        contact.setEmail(createContactRequest.getEmail());
        contact.setPhone(createContactRequest.getPhone());
        contact.setUser(user);

        contactRepository.save(contact);

        return toContactResponse(contact);
    }

    private ContactResponse toContactResponse(Contact contact) {
        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .phone(contact.getPhone())
                .email(contact.getEmail())
                .build();
    }

    @Transactional(readOnly = true)
    public ContactResponse get(User user, String id) {
        Contact contact = contactRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));
        return toContactResponse(contact);
    }

    @Transactional
    public ContactResponse update(User user, UpdateContactRequest updateContactRequest) {
        validationService.validate(updateContactRequest);
        Contact contact = contactRepository.findFirstByUserAndId(user, updateContactRequest.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        contact.setFirstName(updateContactRequest.getFirstName());
        contact.setLastName(updateContactRequest.getLastName());
        contact.setEmail(updateContactRequest.getEmail());
        contact.setPhone(updateContactRequest.getPhone());

        contactRepository.save(contact);

        return toContactResponse(contact);
    }

    public void delete(User user, String contactId) {
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        contactRepository.delete(contact);
//        contactRepository.deleteById(contactId);
    }

    @Transactional(readOnly = true)
    public Page<ContactResponse> search(User user, SearchContactRequest searchContactRequest) {
        Specification<Contact> specification = ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("user"), user));
            if (Objects.nonNull(searchContactRequest.getName())) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("firstName"),"%" + searchContactRequest.getName() + "%"),
                        criteriaBuilder.like(root.get("lastName"),"%" + searchContactRequest.getName() + "%")
                ));
            }
            if (Objects.nonNull(searchContactRequest.getEmail())) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("email"), "%" + searchContactRequest.getEmail() + "%")
                ));
            }
            if (Objects.nonNull(searchContactRequest.getPhone())) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("phone"), "%" + searchContactRequest.getPhone() + "%")
                ));
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        });

        Pageable pageable = PageRequest.of(searchContactRequest.getPage(), searchContactRequest.getSize());
        Page<Contact> contacts = contactRepository.findAll(specification, pageable);
        List<ContactResponse> contactResponses = contacts.getContent().stream()
                .map(this::toContactResponse)
                .toList();

        return new PageImpl<>(contactResponses, pageable, contacts.getTotalElements());
    }

}
