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
public class TestUpdateContact extends BaseTest {

    @BeforeEach
    void setup() throws IOException {
        logTestStep("Setup for updateContact tests");
        // Create a contact to update later
        Contact newContact = new Contact(3, "Testa", "Testb");
        createContact(newContact);
    }

    @AfterEach
    void cleanup() throws IOException {
        logTestStep("Cleanup for updateContact tests");
        // Clean up created contacts
        List<Contact> testContacts = getContactByFirstname("Testa");
        for (Contact contact : testContacts) {
            deleteContact(contact.getId());
        }

        // Clean up contacts updated with "abc" and "def"
        List<Contact> testContactsByLastname = getContactByLastname("def");
        for (Contact contact : testContactsByLastname) {
            deleteContact(contact.getId());
        }
    }

    @Test
    void testInitialUpdateContact() throws IOException {
        logTestStep("Step 1: Fetching contact with firstname 'Testa'");
        List<Contact> contactsBefore = getContactByFirstname("Testa");

        // Verify that there is exactly one contact with this firstname
        assertThat(contactsBefore.size()).as("number of contacts before update").isOne();
        Contact contactBeforeUpdate = contactsBefore.get(0);

        // Log info
        logInfo("Contact found: " + contactBeforeUpdate.getFirstname() + " " + contactBeforeUpdate.getLastname());

        // Create the updated contact with new values
        Contact updatedContact = new Contact(contactBeforeUpdate.getId(), "abc", "def");

        // Update the contact
        logTestStep("Step 2: Updating contact to firstname 'abc' and lastname 'def'");
        updateContact(contactBeforeUpdate, updatedContact);

        // Verify update
        logTestStep("Step 3: Verifying the updated contact by firstname 'abc'");
        List<Contact> contactsAfter = getContactByFirstname("abc");
        assertThat(contactsAfter.size()).as("number of contacts after update").isOne();

        Contact contactAfterUpdate = contactsAfter.get(0);

        // Assert details
        assertThat(contactAfterUpdate.getId()).as("contact id should not change").isEqualTo(contactBeforeUpdate.getId());
        assertThat(contactAfterUpdate.getFirstname()).as("firstname should be updated").isEqualTo("abc");
        assertThat(contactAfterUpdate.getLastname()).as("lastname should be updated").isEqualTo("def");

        // Log success
        logInfo("Contact updated successfully: " + contactAfterUpdate.getFirstname() + " " + contactAfterUpdate.getLastname());
    }

    @Test
    void testRevertUpdatedContact() throws IOException {
        // Fetch the contact with lastname 'def'
        logTestStep("Step 1: Fetching contact by lastname 'def' for reverting");
        List<Contact> contactsByLastName = getContactByLastname("def");
        assertThat(contactsByLastName.size()).as("number of contacts with lastname 'def'").isOne();
        Contact contactToRevert = contactsByLastName.get(0);

        // Revert the contact back to original values
        logTestStep("Step 2: Reverting contact to original firstname 'Testa' and lastname 'Testb'");
        Contact revertedContact = new Contact(contactToRevert.getId(), "Testa", "Testb");
        updateContact(contactToRevert, revertedContact);

        // Verify the contact is reverted correctly
        logTestStep("Step 3: Verifying the reverted contact by firstname 'Testa'");
        List<Contact> revertedContacts = getContactByFirstname("Testa");
        assertThat(revertedContacts.size()).as("number of contacts after revert").isOne();

        Contact contactAfterRevert = revertedContacts.get(0);

        // Assert details
        assertThat(contactAfterRevert.getId()).as("contact id should not change").isEqualTo(contactToRevert.getId());
        assertThat(contactAfterRevert.getFirstname()).as("firstname should be reverted").isEqualTo("Testa");
        assertThat(contactAfterRevert.getLastname()).as("lastname should be reverted").isEqualTo("Testb");

        // Log success
        logInfo("Contact reverted successfully: " + contactAfterRevert.getFirstname() + " " + contactAfterRevert.getLastname());
    }
}
