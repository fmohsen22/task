package com.ecosio.messaging.task.testcase.tests;


import com.ecosio.messaging.task.model.Contact;
import com.ecosio.messaging.task.testcase.BaseTest;
import groovy.util.logging.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class TestCreateContact extends BaseTest {


    @AfterEach
    void cleanup() throws IOException {
        logTestStep("Cleanup for createContact tests");
        List<Contact> testContacts = getContactByFirstname("John");
        for (Contact contact : testContacts) {
            deleteContact(contact.getId());
        }
    }

    @Test
    void createContact() throws IOException {
        // Create a new contact
        logTestStep("Create a new contact");
        Contact newContact = new Contact(3, "John", "Doe");
        createContact(newContact);

        // Assert the contact was created
        logTestStep("Assert the contact was created");
        List<Contact> contactslist = getContactByFirstname("John");
        assertThat(contactslist.size()).isOne();
        Contact createdContact = contactslist.get(0);
        assertThat(createdContact.getFirstname()).isEqualTo("John");
        assertThat(createdContact.getLastname()).isEqualTo("Doe");
    }
}

