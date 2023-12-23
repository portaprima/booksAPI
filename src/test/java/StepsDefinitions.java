import dto.Book;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class StepsDefinitions {
    Properties prop = new Properties();
    FileInputStream file;

    {
        try {
            file = new FileInputStream("./src/test/resources/config.properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setup() throws IOException {
        prop.load(file);
        RestAssured.baseURI = prop.getProperty("baseUrl");
    }

    Response response;
    RequestSpecification request;
    List<Response> responses = new ArrayList<>();

    @Given("correct username and password used")
    public void correctUsernameAndPasswordUsed() {
        request = RestAssured.given();
        request.auth().basic(prop.getProperty("username"), prop.getProperty("password"));
    }

    @When("calling get all books API")
    public void callingGetAllBooksAPI() {
        response = request.get("/api/v1/books");
        System.out.println(response.prettyPrint());
    }


    @Then("API returns {int}")
    public void apiReturns(int httpCode) {
        Assert.assertEquals(response.getStatusCode(), httpCode);
    }

    @When("creating books with the following data")
    public void creatingBooksWithTheFollowingData(DataTable table) {

        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        List<Book> listOfBooks = new ArrayList<>();
        for (Map<String, String> columns : rows) {
            listOfBooks.add(new Book(columns.get("name"), columns.get("author"), columns.get("publication"), columns.get("category"),
                    Integer.valueOf(columns.get("pages")), new BigDecimal(columns.get("price"))));
        }
        for (Book book : listOfBooks) {
            JSONObject requestParams = new JSONObject();
            requestParams.put("name", book.getName());
            requestParams.put("author", book.getAuthor());
            requestParams.put("publication", book.getPublication());
            requestParams.put("category", book.getCategory());
            requestParams.put("pages", book.getPages());
            requestParams.put("price", book.getPrice());
            request.header("Content-Type", "application/json");
            System.out.print(request.body(requestParams.toString()));
            response = request.post("/api/v1/books");
            responses.add(response);
        }
    }

    @When("creating book with the following data")
    public void creatingBookWithTheFollowingData(DataTable table) {
        Book book = getBookFromDataTable(table);
        JSONObject requestParams = new JSONObject();
        requestParams.put("name", book.getName());
        requestParams.put("author", book.getAuthor());
        requestParams.put("publication", book.getPublication());
        requestParams.put("category", book.getCategory());
        requestParams.put("pages", book.getPages());
        requestParams.put("price", book.getPrice());
        request.header("Content-Type", "application/json");
        System.out.print(request.body(requestParams.toString()));
        response = request.post("/api/v1/books");
    }


    @Then("API returns {int} for each book")
    public void apiReturnsForEachBook(int expectedStatusCode) {
        for (Response response : responses) {
            Assert.assertEquals(expectedStatusCode, response.getStatusCode());
        }
    }

    @Then("API returns error - cannot add duplicated book")
    public void apiReturnsErrorCannotAddDuplicatedBook() {
        Assert.assertNotEquals(response.getStatusCode(),200);
    }

    @And("Added book has correct data")
    public void addedBookHasCorrectData(DataTable table) {
        Book expectedBook = getBookFromDataTable(table);
        expectedBook.setId(Integer.valueOf(response.jsonPath().getString("id")));

        response = request.get("/api/v1/books/"+expectedBook.getId());
        Book actualBook = new Book(Integer.valueOf(response.jsonPath().getString("id")), response.jsonPath().getString("name"), response.jsonPath().getString("author"), response.jsonPath().getString("publication"), response.jsonPath().getString("category"),
                Integer.valueOf(response.jsonPath().getString("pages")), new BigDecimal(response.jsonPath().getString("price")));
        Assert.assertEquals(expectedBook,actualBook);
    }

    private static Book getBookFromDataTable(DataTable table) {
        Map<String, String> dataTableBook = table.asMaps(String.class, String.class).get(0);
        Book expectedBook = new Book(dataTableBook.get("name"), dataTableBook.get("author"), dataTableBook.get("publication"), dataTableBook.get("category"),
                Integer.valueOf(dataTableBook.get("pages")), new BigDecimal(dataTableBook.get("price")));
        return expectedBook;
    }

    @And("Added book present in all books")
    public void addedBookPresentInAllBooks(DataTable table) throws Exception {
        Book expectedBook = getBookFromDataTable(table);
        expectedBook.setId(Integer.valueOf(response.jsonPath().getString("id")));
        response = request.get("/api/v1/books");
        boolean bookFound=false;
        for (Object book:response.jsonPath().getList("")) {
                Map<String, Object> bookMap = (Map<String, Object>) book;
                if((Integer)(bookMap.get("id"))== expectedBook.getId()){
                    Book actualBook = new Book(Integer.valueOf(bookMap.get("id").toString()),bookMap.get("name").toString(), bookMap.get("author").toString(), bookMap.get("publication").toString(), bookMap.get("category").toString(),
                    Integer.valueOf(bookMap.get("pages").toString()), new BigDecimal(bookMap.get("price").toString()));
                    Assert.assertEquals("Data of book with id: " + expectedBook.getId() + "wrong",  expectedBook,actualBook);

                    bookFound=true;
                    break;
                }
        }
        if(!bookFound){
            throw new Exception("Book with id: " + expectedBook.getId() + " not found on the books list" );
        }

    }
}


