package com.ecosio.messaging.task.testcase.tests;

import com.ecosio.messaging.task.model.Contact;
import com.ecosio.messaging.task.testcase.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Fail;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class TestGetContactByLastname extends BaseTest {

    @Override
    protected void cleanupBeforeAndAfter() throws IOException {
        logTestStep("Create all required Test data");
        // Create some test contacts
        createContact(new Contact(0, "Firstname", "Lastname"));
        createContact(new Contact(1, "Testa", "Testb"));
        createContact(new Contact(2, "name", "name"));
    }

    @Test
    @Tag("failing")
    void testMatchByLastname() throws IOException {
        logTestStep("Fetching contacts by lastname 'Last'");
        List<Contact> contacts = getContactByLastname("Last");

        assertThat(contacts.size()).as("number of contacts with lastname 'Last'").isOne();

        Contact contact = contacts.get(0);
        assertThat(contact.getLastname()).isEqualTo("Last");

        logSuccessfulresault("Contact found with lastname 'Last': " + contact);
        // TODO: 23.10.2024 Bug - it is failing because in the api last name also search for first name!
    }

    @Test
    void testNoMatchByLastname() throws IOException {
        logTestStep("Searching for a non-existent lastname 'NonExistent'");
        String notExistingLastname = "NonExistent";
        List<Contact> contacts = getContactByLastname(notExistingLastname);

        assertThat(contacts.size()).as("no contacts should be returned for '" + notExistingLastname + "'").isZero();

        logInfo("No contacts found with lastname '" + notExistingLastname + "'");
    }
    @Test
    void testMultipleMatchesByLastname() throws IOException {
        logTestStep("Fetching contacts with substring 'name' in lastname");
        String commonName = "name";
        List<Contact> contacts = getContactByLastname(commonName);

        assertThat(contacts.size()).as("number of contacts with 'name' in lastname").isGreaterThan(1);

        for (Contact contact : contacts) {
            assertThat(contact.getLastname().toLowerCase()).contains(commonName);
        }

        logSuccessfulresault("Found contacts with '" + commonName + "' in their lastname: " + contacts);
    }

    @Test
    void testExactMatchByLastname() throws IOException {
        logTestStep("Fetching contact with exact lastname 'Testb'");
        String exactMatch = "Testb";
        List<Contact> contacts = getContactByLastname(exactMatch);

        assertThat(contacts.size()).as("number of contacts with lastname '" + exactMatch + "'").isOne();
        // TODO: 23.10.2024 Bug - it is failing because in the api last name also search for first name!

        Contact contact = contacts.get(0);
        assertThat(contact.getLastname()).isEqualTo(exactMatch);

        logSuccessfulresault("Exact match found: " + contact);
    }

    @Test
    void testEmptyStringLastname() throws IOException {
        logTestStep("Search with the Empty String as LastName");
        // Search with an empty string
        List<Contact> contacts = getContactByLastname("");

        // as input is required then the response status should be 405
        assertThat(responseWrapper.getStatusCode()).isEqualTo(405);
        assertThat(responseWrapper.getContent()).contains("Method Not Allowed");
        logSuccessfulresault("The empty input as lastname is not allowed.");
    }

    @Test
    void testWhiteSpaceLastname() throws IOException {
        logTestStep("Search with the Whitespace as String as LastName");
        String whitespaceName = "   ";  // Just spaces
        List<Contact> contacts = getContactByLastname(whitespaceName);

        // Verify the behavior: likely it should return no results or behave similarly to an empty string
        assertThat(contacts.size()).as("no contacts should be returned for a whitespace-only search").isZero();

        logSuccessfulresault("No contacts found with whitespace-only search.");
    }

    @Test
    void testNumericCharacterInLastname() throws IOException {
        logTestStep("Search with the Numeric characters as String as LastName");
        String numericName = "12345";
        List<Contact> contacts = getContactByLastname(numericName);

        // Verify that no contacts are returned
        assertThat(contacts.size()).as("no contacts should be returned for numeric string in lastname").isZero();

        logSuccessfulresault("No contacts found with numeric string as lastname.");
    }
}

