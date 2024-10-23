package com.ecosio.messaging.task.testcase;

import com.ecosio.messaging.task.model.Contact;
import com.ecosio.messaging.task.util.HttpClientHelper;
import com.ecosio.messaging.task.util.HttpResponseWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

@Slf4j
public abstract class BaseTest {
    protected HttpResponseWrapper responseWrapper;
    private Properties properties;

    /**
     * Loads the configuration from the properties file.
     */
    private void loadConfig() {
        properties = new Properties();
        try (FileInputStream input = new FileInputStream("src/test/resources/config.properties")) {
            properties.load(input);
        } catch (IOException e) {
            log.error("Error loading config.properties: " + e.getMessage());
        }
    }

    /**
     * Logs the test step description
     *
     * @param stepDescription A string that explains the current step
     */
    protected void logTestStep(String stepDescription) {
        log.info("TEST STEP: " + stepDescription);
    }

    /**
     * Logs a general information message.
     *
     * @param infoMessage A string that contains the general information message.
     */
    protected void logInfo(String infoMessage) {
        log.info("INFO: " + infoMessage);
    }

    /**
     * Logs a general information message.
     *
     * @param infoMessage string  contains the Success message after test.
     */
    protected void logSuccessfulresault(String infoMessage) {
        log.info("Test Successful: " + infoMessage);
    }

    /**
     * Logs the final result of a test
     *
     * @param testName     Name of the test
     * @param success      Whether the test was successful
     * @param errorMessage An optional error message if the test failed
     */
    protected void logTestResult(String testName, boolean success, String errorMessage) {
        if (success) {
            log.info("TEST RESULT: " + testName + " - SUCCESS");
        } else {
            log.error("TEST RESULT: " + testName + " - FAILURE - " + errorMessage);
        }
    }

    /**
     * gets all contacts where parameter <code>firstname</code> is a substring of the contacts firstname
     *
     * @param firstname
     * @return list of all matching contacts
     * @throws IOException
     */
    protected List<Contact> getContactByFirstname(String firstname) {

        // Encode the firstname to ensure it's safe for use in URLs
        String encodedFirstname = URLEncoder.encode(firstname, StandardCharsets.UTF_8);

        // Construct the HttpGet request with the encoded firstname
        String url = properties.getProperty("base_url") + properties.getProperty("firstname_endpoint") + encodedFirstname;
        HttpGet httpGet = new HttpGet(url);
        List<Contact> contactList = null;
        try {
            contactList = connectionHelper(httpGet);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return contactList;
    }

    /**
     * gets all contacts where parameter <code>lastname</code> is a substring of the contacts' lastname
     *
     * @param lastname
     * @return list of all matching contacts
     * @throws IOException
     */
    protected List<Contact> getContactByLastname(String lastname) throws IOException {

        // Encode the lastname to ensure it's safe for use in URLs
        String encodedLastname = URLEncoder.encode(lastname, StandardCharsets.UTF_8);

        // Construct the HttpGet request with the encoded lastname
        String url = properties.getProperty("base_url") + properties.getProperty("lastname_endpoint") + encodedLastname;
        HttpGet httpGet = new HttpGet(url);

        List<Contact> contactList = null;
        try {
            contactList = connectionHelper(httpGet);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return contactList;
    }


    /**
     * gets a list of all contacts stored of the app
     *
     * @return list of all contacts
     * @throws IOException
     */
    protected List<Contact> getAllContacts() throws IOException {

        String url = properties.getProperty("base_url") + properties.getProperty("endpoint_contacts");
        HttpGet httpGet = new HttpGet(url);
        return connectionHelper(httpGet);

    }

    //create a new contact

    /**
     * creates a new contact
     *
     * @param newContact contact to be created
     * @throws IOException
     */
    protected void createContact(Contact newContact) throws IOException {

        // Construct the URL for the create request
        String createUrl = properties.getProperty("base_url") + properties.getProperty("create_update_endpoint") + newContact.getId();

        // Create the HttpPost request
        HttpPost httpPost = new HttpPost(createUrl);

        // Convert the contact to JSON using ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContact = objectMapper.writeValueAsString(newContact);

        // Set the request body
        StringEntity entity = new StringEntity(jsonContact, StandardCharsets.UTF_8);
        httpPost.setEntity(entity);

        // Set headers
        httpPost.setHeader("Content-Type", "application/json");

        // Use the existing connectionHelper to execute the request
        connectionHelper(httpPost);
    }

    /**
     * deletes an existing contact
     *
     * @param contactId the ID of the contact to be deleted
     * @throws IOException
     */
    protected void deleteContact(int contactId) throws IOException {

        // Construct the URL for the delete request
        String url = properties.getProperty("base_url") + contactId;
        String deleteUrl = url;

        // Create the HttpDelete request
        HttpDelete httpDelete = new HttpDelete(deleteUrl);

        // Use the existing connectionHelper to execute the delete request
        try {

            connectionHelper(httpDelete);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    // here is the method to update the contact

    /**
     * updates an existing contact
     *
     * @param currentContact contact to be updated
     * @param updatedContact contact to update the existing one to
     * @throws IOException
     */
    protected void updateContact(Contact currentContact, Contact updatedContact) throws IOException {

        // Construct the URL for the update request
        String updateUrl = properties.getProperty("base_url") + properties.getProperty("create_update_endpoint") + currentContact.getId();

        // Create the HttpPost request
        HttpPost httpPost = new HttpPost(updateUrl);

        // Convert the updated contact to JSON using ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContact = objectMapper.writeValueAsString(updatedContact);

        // Set the request body
        StringEntity entity = new StringEntity(jsonContact, StandardCharsets.UTF_8);
        httpPost.setEntity(entity);

        // Set headers
        httpPost.setHeader("Content-Type", "application/json");

        // Use the existing connectionHelper to execute the request
        try {
            connectionHelper(httpPost);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    /**
     * connection helper to abstract the "connection layer" from the "application layer"
     *
     * @param httpRequestBase
     * @return list of contacts based on the request
     * @throws IOException
     */
    // Connection helper method with status code and response logging
    private List<Contact> connectionHelper(HttpRequestBase httpRequestBase) throws IOException {
        try (CloseableHttpClient httpClient = HttpClientHelper.getHttpClientAcceptsAllSslCerts()) {
            try (CloseableHttpResponse response = httpClient.execute(httpRequestBase)) {

                int statusCode = response.getStatusLine().getStatusCode();
                String responseContent = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);

                // Store the response in the wrapper
                responseWrapper = new HttpResponseWrapper(statusCode, responseContent);


                if (statusCode != 200) {
                    log.error("HTTP request failed with status code: " + statusCode + " and message: " + responseContent);
                }

                // Check if the response is an array or a single object
                ObjectMapper mapper = new ObjectMapper();
                if (responseContent.trim().startsWith("[")) {
                    return mapper.readValue(responseContent, new TypeReference<List<Contact>>() {
                    });
                } else {
                    Contact contact = mapper.readValue(responseContent, Contact.class);
                    return List.of(contact);
                }
            } finally {
                httpRequestBase.releaseConnection();
            }
        }
    }

    @BeforeEach
    void setup() throws IOException {
        loadConfig();  // Load properties before running tests
        logTestStep("Setup for " + getClass().getSimpleName() + " tests");
        cleanupBeforeAndAfter();
        testPrepration();
    }

    @AfterEach
    void cleanup() throws IOException {
        logTestStep("Cleanup for " + getClass().getSimpleName() + " tests");
        cleanupBeforeAndAfter();
        cleanupAfter();
    }

    protected void cleanupBeforeAndAfter() throws IOException {
    }

    protected void testPrepration() throws IOException {
    }

    protected void cleanupAfter() throws IOException {
    }


}
