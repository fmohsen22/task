package com.ecosio.messaging.task.testcase;

import com.ecosio.messaging.task.model.Contact;
import com.ecosio.messaging.task.util.HttpClientHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseTest {

  protected final String appUnderTestBaseUrl = "http://localhost:18080/contact/";

  /**
   * gets all contacts where parameter <code>firstname</code> is a substring of the contacts firstname
   *
   * @param firstname
   * @return list of all matching contacts
   * @throws IOException
   */
  protected List<Contact> getContactByFirstname(String firstname) throws IOException {

    HttpGet httpGet = new HttpGet(appUnderTestBaseUrl + "firstname/" + firstname);
    return connectionHelper(httpGet);

  }

  /**
   * gets all contacts where parameter <code>lastname</code> is a substring of the contacts' lastname
   *
   * @param lastname
   * @return list of all matching contacts
   * @throws IOException
   */
  protected List<Contact> getContactByLastname(String lastname) throws IOException {

    HttpGet httpGet = new HttpGet(appUnderTestBaseUrl + "lastname/" + lastname);
    return connectionHelper(httpGet);
  }


  /**
   * gets a list of all contacts stored of the app
   *
   * @return list of all contacts
   * @throws IOException
   */
  protected List<Contact> getAllContacts() throws IOException {

    HttpGet httpGet = new HttpGet(appUnderTestBaseUrl + "allContacts");
    return connectionHelper(httpGet);

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
    String updateUrl = appUnderTestBaseUrl + "createOrUpdateContact/" + currentContact.getId();

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
    connectionHelper(httpPost);
  }



  /**
   * connection helper to abstract the "connection layer" from the "application layer"
   *
   * @param httpRequestBase
   * @return list of contacts based on the request
   * @throws IOException
   */
  private List<Contact> connectionHelper(HttpRequestBase httpRequestBase) throws IOException {

    try (CloseableHttpClient httpClient = HttpClientHelper.getHttpClientAcceptsAllSslCerts()) {
      try {

        ObjectMapper mapper = new ObjectMapper();
        String response = IOUtils.toString(
                httpClient.execute(httpRequestBase)
                        .getEntity()
                        .getContent(),
                StandardCharsets.UTF_8
        );

        // Check if the response is an array or a single object
        if (response.trim().startsWith("[")) {
          // Deserialize as a list if it's an array
          return mapper.readValue(response, new TypeReference<List<Contact>>() {});
        } else {
          // Deserialize as a single object and wrap it in a list
          Contact contact = mapper.readValue(response, Contact.class);
          return List.of(contact);
        }

      } finally {
        httpRequestBase.releaseConnection();
      }
    }
  }

}