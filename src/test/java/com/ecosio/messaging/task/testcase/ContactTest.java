package com.ecosio.messaging.task.testcase;

import com.ecosio.messaging.task.model.Contact;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: have a look at all the existing tests (some may need fixing or can be implemented in a
//  better way, you are not expected to fix them, but think about why and how you would address
//  these issues and we'll talk about them in the next interview)

// TODO: think of a few different tests (including/excluding the ones here)
//  you would implement and why
@Slf4j
public class ContactTest extends BaseTest {

  // TODO: 22.10.2024 here prepare the main contacts to be testet so before and after each test case we can cleanup (cleanup could change based on the test forexample if we updated then update to the first contact or if we created a contact then delete it)
  // TODO: 22.10.2024 cleanup could follow the changes from post and delete methods and understandhow to cleanup

  @Test
  void allContacts() throws IOException {

    // get all available contacts
    List<Contact> contacts = getAllContacts();

    log.info("all contacts: {}", contacts);

    // number of contacts is expected to stay the same
    assertThat(contacts.size())
        .as("number of contacts")
        .isEqualTo(3);

  }

  @Test
  void updateContact() throws IOException {
    // Get specific contact by firstname "Testa"
    List<Contact> contactsBefore = getContactByFirstname("Testa");

    // Verify that there is exactly one contact with this firstname
    assertThat(contactsBefore.size())
            .as("number of contacts before update")
            .isOne();

    Contact contactBeforeUpdate = contactsBefore.get(0);

    // Create the updated contact with new values (Update firstname and lastname)
    Contact updatedContact = new Contact(
            contactBeforeUpdate.getId(),
            "abc", // updated firstname
            "def"  // updated lastname
    );

    // Log before update
    log.info("Updating contact with id {} from firstname : {} & lastname: {} to firstname : {} & lastname: {}",
            contactBeforeUpdate.getId(),
            contactBeforeUpdate.getFirstname(), contactBeforeUpdate.getLastname(),
            updatedContact.getFirstname(), updatedContact.getLastname()
    );

    // Update the contact
    updateContact(contactBeforeUpdate, updatedContact);

    // Retrieve the updated contact by firstname "abc" (new firstname)
    List<Contact> contactsAfter = getContactByFirstname("abc");

    // Verify that the updated contact has the new firstname and lastname
    assertThat(contactsAfter.size())
            .as("number of contacts after update")
            .isOne();

    Contact contactAfterUpdate = contactsAfter.get(0);

    // Verify that the ID remains the same, but the first and last names are updated
    assertThat(contactAfterUpdate.getId())
            .as("contact id should not change")
            .isEqualTo(contactBeforeUpdate.getId());

    assertThat(contactAfterUpdate.getFirstname())
            .as("firstname should be updated")
            .isEqualTo("abc");

    assertThat(contactAfterUpdate.getLastname())
            .as("lastname should be updated")
            .isEqualTo("def");

    // Log after update
    log.info("Contact updated successfully: {} {}",
            contactAfterUpdate.getFirstname(), contactAfterUpdate.getLastname());

    // Part 2: Update the contact again by lastname
    List<Contact> contactsByLastName = getContactByLastname("def");

    assertThat(contactsByLastName.size())
            .as("number of contacts with lastname 'def'")
            .isOne();

    Contact contactToRevert = contactsByLastName.get(0);

    // Revert the contact back to the original values (firstname: "Testa", lastname: "Testb")
    Contact revertedContact = new Contact(
            contactToRevert.getId(),
            contactBeforeUpdate.getFirstname(),  // revert firstname
            contactBeforeUpdate.getLastname()    // revert lastname
    );

    log.info("Reverting contact with id {} from firstname : {} & lastname: {} to firstname : {} & lastname: {}",
            contactToRevert.getId(),
            contactToRevert.getFirstname(), contactToRevert.getLastname(),
            revertedContact.getFirstname(), revertedContact.getLastname()
    );

    // Update the contact back to original state
    updateContact(contactToRevert, revertedContact);

    // Verify the contact is reverted correctly
    List<Contact> revertedContacts = getContactByFirstname(contactBeforeUpdate.getFirstname());

    assertThat(revertedContacts.size())
            .as("number of contacts after revert")
            .isOne();

    Contact contactAfterRevert = revertedContacts.get(0);

    assertThat(contactAfterRevert.getId())
            .as("contact id should not change")
            .isEqualTo(contactBeforeUpdate.getId());

    assertThat(contactAfterRevert.getFirstname())
            .as("firstname should be reverted")
            .isEqualTo(contactBeforeUpdate.getFirstname());

    assertThat(contactAfterRevert.getLastname())
            .as("lastname should be reverted")
            .isEqualTo(contactBeforeUpdate.getLastname());

    // Log after revert
    log.info("Contact reverted successfully: {} {}",
            contactAfterRevert.getFirstname(), contactAfterRevert.getLastname());
  }



  @Test
  void getContactByFirstname() {

    // TODO: get ALL contacts with the string "name" in it and add assertions

  }

}
