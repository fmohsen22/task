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
public class TestGetContactByLastname extends BaseTest {

    @BeforeEach
    void setup() throws IOException {
        logTestStep("Setup for getContactByLastname tests");
        // Create test contact with lastname
        createContact(new Contact(3, "First", "Last"));
    }

    @AfterEach
    void cleanup() throws IOException {
        logTestStep("Cleanup for getContactByLastname tests");
        // Clean up created contacts
        List<Contact> testContacts = getAllContacts();
        for (Contact contact : testContacts) {
            deleteContact(contact.getId());
        }
    }

    @Test
    void testMatchByLastname() throws IOException {
        logTestStep("Step 1: Fetching contacts by lastname 'Last'");
        List<Contact> contacts = getContactByLastname("Last");

        assertThat(contacts.size()).as("number of contacts with lastname 'Last'").isOne();

        Contact contact = contacts.get(0);
        assertThat(contact.getLastname()).isEqualTo("Last");

        logInfo("Contact found with lastname 'Last': " + contact);
    }

    @Test
    void testNoMatchByLastname() throws IOException {
        logTestStep("Step 1: Searching for a non-existent lastname 'NonExistent'");
        String notExistingLastname = "NonExistent";
        List<Contact> contacts = getContactByLastname(notExistingLastname);

        assertThat(contacts.size()).as("no contacts should be returned for '" + notExistingLastname + "'").isZero();

        logInfo("No contacts found with lastname '" + notExistingLastname + "'");
    }
}

