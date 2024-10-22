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
public class TestAllContacts extends BaseTest {

    @Test
    void allContacts() throws IOException {
        // Get all available contacts
        List<Contact> contacts = getAllContacts();
        log.info("All contacts: {}", contacts);

        // Assert the initial number of contacts
        assertThat(contacts.size()).as("number of contacts").isEqualTo(3);
    }
}
