import com.fasterxml.jackson.core.JsonProcessingException;
import dto.Book;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StepsDefinitions {
    Properties prop = new Properties();
    FileInputStream file;
    Response response;
    JsonNode jsonResponse;
    int bookId;
    RequestSpecification request;

    {
        try {
            file = new FileInputStream("./src/test/resources/config.properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Book getBookFromDataTable(DataTable table) {
        Map<String, String> dataTableBook = table.asMaps(String.class, String.class).get(0);
        return new Book(dataTableBook.get("name"), dataTableBook.get("author"), dataTableBook.get("publication"), dataTableBook.get("category"), Integer.parseInt(dataTableBook.get("pages")), new BigDecimal(dataTableBook.get("price")));

    }

    private static Map<String, String> getBookFromDataTableAsStrings(DataTable table) {
        return table.asMaps(String.class, String.class).get(0);
    }

    @DataTableType(replaceWithEmptyString = "[blank]")
    public String listOfStringListsType(String cell) {
        return cell;
    }

    @Before
    public void setup() throws IOException {
        prop.load(file);
        RestAssured.baseURI = prop.getProperty("baseUrl");
        RestAssured.basePath = prop.getProperty("basePath");
        request = RestAssured.given().log().all();
        request.header("Content-Type", "application/json");
    }

    @Given("correct username and password used")
    public void correctUsernameAndPasswordUsed() {
        request.auth().basic(prop.getProperty("username"), prop.getProperty("password"));
    }

    @When("not possible to create book with duplicated data")
    public void notPossibleToCreateBookWithDuplicatedData(DataTable table) {
        Book book = getBookFromDataTable(table);
        JSONObject requestParams = new JSONObject();
        requestParams.put("name", book.getName());
        requestParams.put("author", book.getAuthor());
        requestParams.put("publication", book.getPublication());
        requestParams.put("category", book.getCategory());
        requestParams.put("pages", book.getPages());
        requestParams.put("price", book.getPrice());
        request.body(requestParams.toString());
        response = request.post();
        Assert.assertNotEquals("Add book service returns code 200 for book with duplicated data", 200, response.getStatusCode());
    }

    @When("creating book with the following data")
    public void creatingBookWithTheFollowingData(DataTable table) throws JsonProcessingException {
        Book expectedBook = getBookFromDataTable(table);
        JSONObject requestParams = new JSONObject();
        requestParams.put("name", expectedBook.getName());
        requestParams.put("author", expectedBook.getAuthor());
        requestParams.put("publication", expectedBook.getPublication());
        requestParams.put("category", expectedBook.getCategory());
        requestParams.put("pages", expectedBook.getPages());
        requestParams.put("price", expectedBook.getPrice());
        request.body(requestParams.toString());
        response = request.post();
        Assert.assertEquals("Add book service returns code other than 200: " + response.getBody().prettyPrint(), 200, response.getStatusCode());
        jsonResponse = responseAsJsonNode(response);
        bookId = jsonResponse.get("id").intValue();
        Book actualBook = getBookFromResponse(bookId);
        expectedBook.setId(bookId);
        Assert.assertEquals("Created book has wrong data", expectedBook, actualBook);
    }

    private JsonNode responseAsJsonNode(Response response) throws JsonProcessingException {
        String responseBody = response.getBody().asString();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(responseBody);
    }

    @And("book has correct data")
    public void bookHasCorrectData(DataTable table) {
        Book expectedBook = getBookFromDataTable(table);
        expectedBook.setId(Integer.parseInt(response.jsonPath().getString("id")));
        response = request.get("/" + bookId);

        Assert.assertEquals("Get book returns code other than 200: " + response.getBody().prettyPrint(), 200, response.getStatusCode());
        Book actualBook = getBookFromResponse(bookId);
        Assert.assertEquals(expectedBook, actualBook);
    }

    private Book getBookFromResponse(int bookId) {
        return new Book(bookId, jsonResponse.get("name").textValue(), jsonResponse.get("author").textValue(), jsonResponse.get("publication").textValue(), jsonResponse.get("category").textValue(), jsonResponse.get("pages").intValue(), jsonResponse.get("price").decimalValue());
    }

    @And("book present in all books")
    public void bookPresentInAllBooks(DataTable table) throws Exception {
        Book expectedBook = getBookFromDataTable(table);
        expectedBook.setId(bookId);
        response = request.get();
        jsonResponse = responseAsJsonNode(response);

        boolean bookFound = false;
        for (JsonNode book : jsonResponse) {
            if (book.has("id") && book.get("id").isInt() && book.get("id").asInt() == expectedBook.getId()) {
                Book actualBook = new Book(book.get("id").asInt(), book.get("name").asText(), book.get("author").asText(), book.get("publication").asText(), book.get("category").asText(), book.get("pages").asInt(), new BigDecimal(book.get("price").asText()));

                Assert.assertEquals("Data of book with id: " + expectedBook.getId() + " is wrong", expectedBook, actualBook);

                bookFound = true;
                break;
            }
        }
        if (!bookFound) {
            throw new Exception("Book with id: " + bookId + " not found on the books list");
        }

    }

    @When("book is deleted")
    public void bookIsDeleted() {
        int bookId = Integer.parseInt(response.jsonPath().getString("id"));
        response = request.delete("/" + bookId);
        Assert.assertEquals("Delete book returns code other than 200. " + response.getBody().prettyPrint(), 200, response.getStatusCode());
    }

    @And("book is not longer returned by the service")
    public void bookIsNotLongerReturnedByTheService() {
        response = request.get("/" + bookId);
        Assert.assertEquals("Read book for id: " + bookId + " returns code other than 404. " + response.getBody().prettyPrint(), 404, response.getStatusCode());
    }

    @And("book is no longer present in all books")
    public void bookIsNoLongerPresentInAllBooks() throws Exception {
        response = request.get();
        boolean bookFound = false;
        jsonResponse = responseAsJsonNode(response);
        for (JsonNode book : jsonResponse) {
            if (book.has("id") && book.get("id").isInt() && book.get("id").asInt() == bookId) {
                bookFound = true;
                break;
            }
        }

        if (bookFound) {
            throw new Exception("Book with id: " + bookId + " is found on the books list");
        }
    }

    @When("book is updated")
    public void bookIsUpdated(DataTable table) {
        Book book = getBookFromDataTable(table);
        JSONObject requestParams = new JSONObject();
        requestParams.put("id", book.getId());
        requestParams.put("name", book.getName());
        requestParams.put("author", book.getAuthor());
        requestParams.put("publication", book.getPublication());
        requestParams.put("category", book.getCategory());
        requestParams.put("pages", book.getPages());
        requestParams.put("price", book.getPrice());
        request.body(requestParams.toString());
        response = request.put("/" + bookId);
        Assert.assertEquals("Update book returns code other than 200. " + response.getBody().prettyPrint(), 200, response.getStatusCode());
    }

    @And("there are no books in the db")
    public void thereAreNoBooksInTheDB() throws Exception {
        response = request.get();
        jsonResponse = responseAsJsonNode(response);
        List<Object> bookList = response.jsonPath().getList("");
       if(!bookList.isEmpty()) {
           for (JsonNode book : jsonResponse) {
               if (book.has("id")) {
                   String id = book.get("id").asText();
                   Response deleteResponse = request.delete("/" + id);
                   Assert.assertEquals("Delete book returns code other than 200 for id: " + id + ". " + deleteResponse.getBody().prettyPrint(),
                           200, deleteResponse.getStatusCode());
               }
           }
       }
        response = request.get();
        bookList = response.jsonPath().getList("");
        Assert.assertTrue("There are books in the db", bookList.isEmpty());
    }

    @When("not possible to create book with wrong data")
    public void notPossibleToCreateBookWithIncompleteData(DataTable table) {
        Map<String, String> book = getBookFromDataTableAsStrings(table);
        JSONObject requestParams = new JSONObject();
        requestParams.put("name", book.get("name"));
        requestParams.put("author", book.get("author"));
        requestParams.put("publication", book.get("publication"));
        requestParams.put("category", book.get("category"));
        requestParams.put("pages", book.get("pages"));
        requestParams.put("price", book.get("price"));
        request.body(requestParams.toString());
        response = request.post();
        Assert.assertNotEquals("Add book service returns code 200 for book with incomplete data. " + response.getBody().prettyPrint(), 200, response.getStatusCode());
    }


}


