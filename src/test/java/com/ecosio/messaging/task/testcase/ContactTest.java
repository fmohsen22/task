package com.ecosio.messaging.task.testcase;

import com.ecosio.messaging.task.model.Contact;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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

    // Part 2 Create a new contact
    Contact newContact = new Contact(3, "John", "Doe");

    // Call the method to create the contact
    createContact(newContact);

    // number of contacts is increased to 4
    assertThat(getAllContacts().size())
            .as("number of contacts")
            .isEqualTo(4);

    // Verify that the contact was created
    List<Contact> contactslist = getContactByFirstname("John");
    assertThat(contactslist.size())
            .as("number of contacts with firstname 'John'")
            .isOne();

    Contact createdContact = contactslist.get(0);
    assertThat(createdContact.getFirstname()).isEqualTo("John");
    assertThat(createdContact.getLastname()).isEqualTo("Doe");

    //part 3 delete the created contact
    int contactIdToDelete = 3;

    // Call the method to delete the contact
    deleteContact(contactIdToDelete);

    // number of contacts is reduced to 3
    assertThat(getAllContacts().size())
            .as("number of contacts")
            .isEqualTo(3);
  }

  @Test
  void createContact() throws IOException {
  }


  @Test
  void deleteContact() throws IOException {
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
  void getContactByFirstname() throws IOException {


    //Part 1_ check multiple Matches
    // Assume there are contacts with first names containing "name"
    String commonName = "name";
    List<Contact> contacts = getContactByFirstname(commonName);

    // Verify that multiple contacts are returned
    assertThat(contacts.size())
            .as("number of contacts with 'name' in firstname")
            .isGreaterThan(1);

    // Assert that all returned contacts contain "name" in their firstname
    for (Contact contact : contacts) {
      assertThat(contact.getFirstname().toLowerCase())
              .as("contact firstname should contain commonName ")
              .contains(commonName);
    }

    log.info("Found contacts with '" + commonName + "' in their firstname: {}", contacts);

    //Part2 - check exact match
    String exactMatch = "Firstname";
    // Assume there's a contact with firstname "Firstname"
    contacts = getContactByFirstname(exactMatch);

    // Verify that only one contact is returned
    assertThat(contacts.size())
            .as("number of contacts with firstname '" + exactMatch + "'")
            .isOne();

    // Verify the contact has the exact firstname "John"
    Contact contact = contacts.get(0);
    assertThat(contact.getFirstname())
            .as("contact firstname should be '" + exactMatch + "'")
            .isEqualTo(exactMatch);

    log.info("Contact with exact match for " + exactMatch + " found: {}", contact);

    //part3 - no matches
    String notExistingName = "NonExistentName";
    // Search for a firstname that doesn't exist in the contacts
    contacts = getContactByFirstname(notExistingName);

    // Verify that no contacts are returned
    assertThat(contacts.size())
            .as("no contacts should be returned for '" + notExistingName + "'")
            .isZero();

    log.info("No contacts found with firstname '" + notExistingName + "'");

    //part4- case sensitivity test
    String lowercaseName = "firstname";
    // Assume there's a contact with firstname lowercaseName
    contacts = getContactByFirstname(lowercaseName);  // Lowercase query

    // Verify that the contact with firstname "John" is returned
    assertThat(contacts.size())
            .as("number of contacts with firstname '" + lowercaseName + "'")
            .isOne();

    contact = contacts.get(0);
    assertThat(contact.getFirstname())
            .as("contact firstname should be 'lowercaseName'")
            .isNotEqualTo(lowercaseName);

    log.info("Contact found with case-insensitive search: {}", contact);

    //part5 - empty String
    // Search with an empty string
    contacts = getContactByFirstname("");

    // Check expected behavior: could be returning all contacts or none, based on the specification
    assertThat(contacts.size())
            .as("number of contacts with empty string search")
            .isEqualTo(getAllContacts().size());  // Assuming empty search returns all contacts
    //note-> it shows that API is not supportng the empty String

    log.info("All contacts returned for empty string search: {}", contacts);

    //Part 6 - Search with a whitespace string
    String whitespaceName = "   ";  // Just spaces
    contacts = getContactByFirstname(whitespaceName);

    // Verify the behavior: likely it should return no results or behave similarly to an empty string
    assertThat(contacts.size())
            .as("no contacts should be returned for a whitespace-only search")
            .isZero();

    log.info("No contacts found with whitespace-only search.");

    //note -> this need to be defined

    //Part 7 -  Search for a contact with numeric characters in the firstname
    String numericName = "12345";
    contacts = getContactByFirstname(numericName);

    // Verify that no contacts are returned
    assertThat(contacts.size())
            .as("no contacts should be returned for numeric string in firstname")
            .isZero();

    log.info("No contacts found with numeric string as firstname.");
  }

  @Test
  void getContactByFirstname_AllScenarios() throws IOException {
    assertAll(
            "Check all scenarios for getContactByFirstname",

            // Part 1: Check multiple matches
            () -> {
              logTestStep("Part 1: Check multiple matches");
              String commonName = "name";
              List<Contact> contacts = getContactByFirstname(commonName);
              assertThat(contacts.size()).isGreaterThan(1);
              for (Contact contact : contacts) {
                assertThat(contact.getFirstname().toLowerCase()).contains(commonName);
              }
              log.info("Found contacts with 'name' in their firstname: {}", contacts);
            },

            // Part 2: Check exact match
            () -> {
              logTestStep("Part 2: Check exact match");
              String exactMatch = "Firstname";
              List<Contact> contacts = getContactByFirstname(exactMatch);
              assertThat(contacts.size()).isOne();
              assertThat(contacts.get(0).getFirstname()).isEqualTo(exactMatch);
              log.info("Exact match found: {}", contacts.get(0));
            },

            // Part 3: No matches
            () -> {
              logTestStep("Part 3: No matches");
              String notExistingName = "NonExistentName";
              List<Contact> contacts = getContactByFirstname(notExistingName);
              assertThat(contacts.size()).isZero();
              log.info("No contacts found with '" + notExistingName + "'");
            },

            // Part 4: Case sensitivity
            () -> {
              logTestStep("Part 4: Case sensitivity");
              String lowercaseName = "firstname";
              List<Contact> contacts = getContactByFirstname(lowercaseName);
              assertThat(contacts.size()).isOne();
              assertThat(contacts.get(0).getFirstname()).isNotEqualTo(lowercaseName);
              log.info("Case-insensitive search result: {}", contacts.get(0));
            },

            // Part 5: Empty string
            () -> {
              logTestStep("Part 5: Empty string");
              List<Contact> contacts = getContactByFirstname("");
              assertThat(contacts.size()).isEqualTo(getAllContacts().size());
              log.info("Empty string search result: {}", contacts);
            },

            // Part 6: Whitespace string
            () -> {
              logTestStep("Part 6: Whitespace string");
              String whitespaceName = "   ";
              List<Contact> contacts = getContactByFirstname(whitespaceName);
              assertThat(contacts.size()).isZero();
              log.info("Whitespace string search result: {}", contacts);
            },

            // Part 7: Numeric string
            () -> {
              logTestStep("Part 7: Numeric string");
              String numericName = "12345";
              List<Contact> contacts = getContactByFirstname(numericName);
              assertThat(contacts.size()).isZero();
              log.info("Numeric string search result: {}", contacts);
            }
    );
  }


}
