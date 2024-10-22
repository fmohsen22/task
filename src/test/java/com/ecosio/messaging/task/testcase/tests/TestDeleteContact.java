package com.ecosio.messaging.task.testcase.tests;


import com.ecosio.messaging.task.model.Contact;
import com.ecosio.messaging.task.testcase.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class TestDeleteContact extends BaseTest {

    @BeforeEach
    void setup() throws IOException {
        logTestStep("Setup for deleteContact tests");
        // Create a contact to delete later
        Contact newContact = new Contact(3, "John", "Doe");
        createContact(newContact);
    }

    @Test
    void deleteContact() throws IOException {
        // Get the created contact
        logTestStep("Get the created contact");
        List<Contact> contacts = getContactByFirstname("John");
        assertThat(contacts.size()).isOne();

        // Delete the contact
        logTestStep("Delete the contact");
        Contact contactToDelete = contacts.get(0);
        deleteContact(contactToDelete.getId());

        // Verify that the contact was deleted
        logTestStep("Verify that the contact was deleted");
        List<Contact> remainingContacts = getContactByFirstname("John");
        assertThat(remainingContacts.size()).isZero();
    }
}
