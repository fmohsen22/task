package com.ecosio.messaging.task.testcase.tests;

import com.ecosio.messaging.task.model.Contact;
import com.ecosio.messaging.task.testcase.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class TestGetContactByFirstname extends BaseTest {

    @Override
    protected void cleanupBeforeAndAfter() throws IOException {
        logTestStep("Create all required Test data");
        // Create some test contacts
        createContact(new Contact(0, "Firstname", "Lastname"));
        createContact(new Contact(1, "Testa", "Testb"));
        createContact(new Contact(2, "name", "name"));
    }


    @Test
    void testMultipleMatchesByFirstname() throws IOException {
        logTestStep("Fetching contacts with substring 'name' in firstname");
        String commonName = "name";
        List<Contact> contacts = getContactByFirstname(commonName);

        assertThat(contacts.size()).as("number of contacts with 'name' in firstname").isGreaterThan(1);

        for (Contact contact : contacts) {
            assertThat(contact.getFirstname().toLowerCase()).contains(commonName);
        }

        logSuccessfulresault("Found contacts with '" + commonName + "' in their firstname: " + contacts);
    }

    @Test
    void testExactMatchByFirstname() throws IOException {
        logTestStep("Fetching contact with exact firstname 'Testa'");
        String exactMatch = "Testa";
        List<Contact> contacts = getContactByFirstname(exactMatch);

        assertThat(contacts.size()).as("number of contacts with firstname '" + exactMatch + "'").isOne();

        Contact contact = contacts.get(0);
        assertThat(contact.getFirstname()).isEqualTo(exactMatch);

        logSuccessfulresault("Exact match found: " + contact);
    }

    @Test
    void testNoMatchByFirstname() throws IOException {
        logTestStep("Searching for a non-existent firstname 'NonExistent'");
        String notExistingName = "NonExistent";
        List<Contact> contacts = getContactByFirstname(notExistingName);

        assertThat(contacts.size()).as("no contacts should be returned for '" + notExistingName + "'").isZero();

        logSuccessfulresault("No contacts found with firstname '" + notExistingName + "'");
    }

    @Test
    void testCaseSensitivity() throws IOException {
        logTestStep("case sensitivity test, check if the Firstname search is case sensitive.");
        String lowercaseName = "firstname";
        // Assume there's a contact with firstname lowercaseName
        List<Contact> contacts = getContactByFirstname(lowercaseName);  // Lowercase query

        // Verify that the contact with firstname "John" is returned
        assertThat(contacts.size())
                .as("number of contacts with firstname '" + lowercaseName + "'")
                .isOne();

        Contact contact = contacts.get(0);
        log.info("Contact found with case-insensitive search: {}", contact);

        logSuccessfulresault("The firstname input is not Case sensitive.");

    }

    @Test
    void testEmptyString() throws IOException {
        logTestStep("Search with the Empty String as FirstName");
        // Search with an empty string
        List<Contact> contacts = getContactByFirstname("");

        // as input is required then the response status should be 405
        assertThat(responseWrapper.getStatusCode()).isEqualTo(405);
        assertThat(responseWrapper.getContent()).contains("Method Not Allowed");
        logSuccessfulresault("The empty input as first name is not allowed.");
    }

    @Test
    void testWhiteSpace() throws IOException {
        logTestStep("Search with the WithSpace as String as FirstName");
        String whitespaceName = "   ";  // Just spaces
        List<Contact> contacts = getContactByFirstname(whitespaceName);

        // Verify the behavior: likely it should return no results or behave similarly to an empty string
        assertThat(contacts.size())
                .as("no contacts should be returned for a whitespace-only search")
                .isZero();

        logSuccessfulresault("No contacts found with whitespace-only search.");
    }

    @Test
    void testNumericCharacter() throws IOException {
        logTestStep("Search with the Numeric characters as String as FirstName");
        String numericName = "12345";
        List<Contact> contacts = getContactByFirstname(numericName);

        // Verify that no contacts are returned
        assertThat(contacts.size())
                .as("no contacts should be returned for numeric string in firstname")
                .isZero();

        logSuccessfulresault("No contacts found with numeric string as firstname.");
    }
    @Test
    void testLongStringAsFirstname() throws IOException {
        logTestStep("Search with a long string as Firstname");
        String longFirstname = "A".repeat(1000);  // Very long string
        List<Contact> contacts = getContactByFirstname(longFirstname);

        assertThat(contacts.size()).as("no contacts should be returned for long string in firstname").isZero();
        logSuccessfulresault("No contacts found with long string as firstname.");
    }

    @Test
    void testSpecialCharactersInFirstname() throws IOException {
        logTestStep("Search with special characters as Firstname");
        String specialChars = "@#!%";
        List<Contact> contacts = getContactByFirstname(specialChars);

        assertThat(contacts.size()).as("no contacts should be returned for special characters in firstname").isZero();
        logSuccessfulresault("No contacts found with special characters as firstname.");
    }


}
