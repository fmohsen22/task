package com.ecosio.messaging.task.testcase.tests;


import com.ecosio.messaging.task.model.Contact;
import com.ecosio.messaging.task.testcase.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class TestAllContacts extends BaseTest {


    @Override
    protected void cleanupBeforeAndAfter() throws IOException {
        logTestStep("Delete contact with the name of 'John' from the Databace.");
        List<Contact> testContacts = new ArrayList<>();
        if (getContactByFirstname("Charlie").size()>0) {
            testContacts.add(getContactByFirstname("Charlie").get(0));
        }
        if (getContactByFirstname("Alice").size()>0) {
            testContacts.add(getContactByFirstname("Alice").get(0));
        }

        for (Contact contact : testContacts) {
            deleteContact(contact.getId());
        }
    }

    @Test
    void allContacts() throws IOException {
        // Get all available contacts
        List<Contact> contacts = getAllContacts();
        log.info("All contacts: {}", contacts);

        // Assert the initial number of contacts
        assertThat(contacts.size()).as("number of contacts").isEqualTo(3);
        logSuccessfulresault("Correct number of contacts");
    }

    @Test
    void testContactOrder() throws IOException {
        logTestStep("Step 1: Create contacts with known names");
        createContact(new Contact(3, "Charlie", "Brown"));
        createContact(new Contact(4, "Alice", "Wonderland"));

        logTestStep("Step 2: Fetch all contacts and check order");
        List<Contact> contacts = getAllContacts();

        // Extract the last two contacts from the list
        List<Contact> lastTwoContacts = contacts.subList(contacts.size() - 2, contacts.size());

        log.info("Last two contacts: {}", lastTwoContacts);

        // Assert that the last two contacts are "Charlie" and "Alice" in the correct order
        assertThat(lastTwoContacts).extracting(Contact::getFirstname).containsExactly("Charlie", "Alice");

        logSuccessfulresault("Last two contacts are 'Charlie' and 'Alice' in the correct order");
    }

}
