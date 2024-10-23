package com.ecosio.messaging.task.testcase.tests;

import com.ecosio.messaging.task.model.Contact;
import com.ecosio.messaging.task.testcase.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class TestUpdateContact extends BaseTest {

    @Override
    protected void cleanupBeforeAndAfter() throws IOException {
        logTestStep("Create all required Test data");
        // Create some test contacts
        createContact(new Contact(1, "Testa", "Testb"));
    }

    @Override
    protected void cleanupAfter(){
        try {
            deleteContact(11);
        }catch (Exception e){
            logInfo(e.toString());
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
        logInfo("Contact found: fisrt name: " + contactBeforeUpdate.getFirstname() + " last name: " + contactBeforeUpdate.getLastname());

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
        logSuccessfulresault("Contact updated successfully: " + contactAfterUpdate.getFirstname() + " " + contactAfterUpdate.getLastname());
    }

    @Test
    @Tag("failing")
    void testRevertUpdatedContact() throws IOException {
        // Fetch the contact with lastname 'def'
        logTestStep("Step 1: Fetching contact by lastname 'def' for reverting");
        List<Contact> contactsByLastName = getContactByLastname("def");
        assertThat(contactsByLastName.size()).as("number of contacts with lastname 'def'").isOne();
        Contact contactToRevert = contactsByLastName.get(0);
        // TODO: 23.10.2024  Bug - the same bug as by the last name -> in the API it searches Firstname instead of last name
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

    @Test
    void testUpdateUnavailableId() throws IOException {
        //make an available contact with unavailable ID
        Contact unavailableContact =new Contact(11, "Testa", "Testb");
        logTestStep("Step 1: Create a Contact object with unavailable ID = "+unavailableContact.toString());

        //make an update
        Contact updatedUnavailableContact =new Contact(11, "abc", "def");
        logTestStep("Step 2: update the Contact object with unavailable ID = "+updatedUnavailableContact.toString());

        //update
        logTestStep("Update the Contact"+ unavailableContact + " to "+updatedUnavailableContact);
        updateContact(unavailableContact, updatedUnavailableContact);

        // Verify update
        logTestStep("Step 3: Verifying the updated contact by firstname 'abc'");
        List<Contact> contactsAfter = getContactByFirstname("abc");
        assertThat(contactsAfter.size()).as("number of contacts after update").isOne();

        Contact contactAfterUpdate = contactsAfter.get(0);

        // Assert details
        assertThat(contactAfterUpdate.getId()).as("contact id should not change").isEqualTo(unavailableContact.getId());
        assertThat(contactAfterUpdate.getFirstname()).as("firstname should be updated").isEqualTo("abc");
        assertThat(contactAfterUpdate.getLastname()).as("lastname should be updated").isEqualTo("def");

        logSuccessfulresault("The contact "+unavailableContact + "updated to "+updatedUnavailableContact);

        // TODO: 23.10.2024 right now the method is the same for update and create so we can even updte unavailabe ID!
        //  but in future if we separate it to two methods this test will have meaning
    }
    //update correct ID to an unavailable ID -> the id in the URl would be different from json
    @Test
    void testUpdateCorrectIDWithUnavailableID() throws IOException {
        // Step 1: Fetch the contact with valid ID (assuming "Testa" exists).
        logTestStep("Step 1: Fetching contact with valid ID and first name 'Testa'");
        List<Contact> contactsBeforeUpdate = getContactByFirstname("Testa");

        // Verify that there is exactly one contact with this firstname
        assertThat(contactsBeforeUpdate.size()).as("number of contacts before update").isOne();
        Contact contactBeforeUpdate = contactsBeforeUpdate.get(0);

        // Log the initial contact details
        logInfo("Contact found: first name: " + contactBeforeUpdate.getFirstname() + " last name: " + contactBeforeUpdate.getLastname());

        // Step 2: Create the updated contact with a different (unavailable) ID in the JSON
        logTestStep("Step 2: Creating an update with an unavailable ID in JSON (ID=999)");
        Contact updatedContact = new Contact(999, "UpdatedFirstName", "UpdatedLastName");  // Unavailable ID

        // Log the update request
        logTestStep("Step 3: Updating contact using URL ID = " + contactBeforeUpdate.getId() + " and JSON ID = 999");

        // Update the contact using the correct ID in the URL but with a different (unavailable) ID in the JSON
        updateContact(contactBeforeUpdate, updatedContact);

        // Step 4: Verify the response for the mismatched ID case
        logTestStep("Step 4: Verifying the response for mismatched IDs");
        assertThat(responseWrapper.getStatusCode()).isEqualTo(400);
        assertThat(responseWrapper.getContent()).contains("IDs not matching");

        logSuccessfulresault("Correctly shows the error msg '400' for ID mismatch.");
    }

}
