package com.ecosio.messaging.task.testcase.tests;


import com.ecosio.messaging.task.model.Contact;
import com.ecosio.messaging.task.testcase.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class TestDeleteContact extends BaseTest {

    @Override
    protected void cleanupBeforeAndAfter() throws IOException {
        logTestStep("Delete contact with the name of 'John' from the Databace.");
        List<Contact> testContacts = getContactByFirstname("John");
        for (Contact contact : testContacts) {
            deleteContact(contact.getId());
        }
    }

    @Override
    protected void testPrepration() throws IOException {
        logTestStep("create 'John' in contacts list");
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
        logTestStep("Delete 'John from contacts");
        Contact contactToDelete = contacts.get(0);
        deleteContact(contactToDelete.getId());

        // Verify that the contact was deleted
        logTestStep("Verify that the contact was deleted");
        List<Contact> remainingContacts = getContactByFirstname("John");
        assertThat(remainingContacts.size()).isZero();
    }

    @Test
    void deleteUnavailableId() throws IOException {
        // Delete the contact
        logTestStep("Delete  contacts, but with the unavailable ID of 100!");
        deleteContact(100);

        // as input is required then the response status should be 405
        assertThat(responseWrapper.getStatusCode()).isEqualTo(404);
        assertThat(responseWrapper.getContent()).contains("Contact with requested ID doesn't exist");
        logSuccessfulresault("Correctly shows the error msg '404'.");
    }

}
