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
public class TestGetContactByFirstname extends BaseTest {

    @BeforeEach
    void setup() throws IOException {
        logTestStep("Setup for getContactByFirstname tests");
        // Create some test contacts
        createContact(new Contact(3, "Testa", "Testb"));
        createContact(new Contact(4, "name", "Last"));
    }

    @AfterEach
    void cleanup() throws IOException {
        logTestStep("Cleanup for getContactByFirstname tests");
        // Clean up created contacts
        List<Contact> testContacts = getAllContacts();
        for (Contact contact : testContacts) {
            deleteContact(contact.getId());
        }
    }

    @Test
    void testMultipleMatchesByFirstname() throws IOException {
        logTestStep("Step 1: Fetching contacts with substring 'name' in firstname");
        String commonName = "name";
        List<Contact> contacts = getContactByFirstname(commonName);

        assertThat(contacts.size()).as("number of contacts with 'name' in firstname").isGreaterThan(1);

        for (Contact contact : contacts) {
            assertThat(contact.getFirstname().toLowerCase()).contains(commonName);
        }

        logInfo("Found contacts with '" + commonName + "' in their firstname: " + contacts);
    }

    @Test
    void testExactMatchByFirstname() throws IOException {
        logTestStep("Step 1: Fetching contact with exact firstname 'Testa'");
        String exactMatch = "Testa";
        List<Contact> contacts = getContactByFirstname(exactMatch);

        assertThat(contacts.size()).as("number of contacts with firstname '" + exactMatch + "'").isOne();

        Contact contact = contacts.get(0);
        assertThat(contact.getFirstname()).isEqualTo(exactMatch);

        logInfo("Exact match found: " + contact);
    }

    @Test
    void testNoMatchByFirstname() throws IOException {
        logTestStep("Step 1: Searching for a non-existent firstname 'NonExistent'");
        String notExistingName = "NonExistent";
        List<Contact> contacts = getContactByFirstname(notExistingName);

        assertThat(contacts.size()).as("no contacts should be returned for '" + notExistingName + "'").isZero();

        logInfo("No contacts found with firstname '" + notExistingName + "'");
    }
}
