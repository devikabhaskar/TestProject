package StepDefinition;

import java.util.List;
import java.util.Map;
import org.junit.Assert;
import apiEngine.requests.AddBookRequest;
import apiEngine.requests.Authorization;
import apiEngine.requests.ISBN;
import apiEngine.requests.RemoveBookRequest;
import apiEngine.response.Book;
import apiEngine.response.Books;
import apiEngine.response.Token;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class EndToEndScenario {

	private static final String USER_ID = "55708b1a-69d6-4792-85c9-3c580bd36bdc";
	private static final String BASE_URL = "https://demoqa.com";

	private static Token tokenGenerated;
	public static String jsonString;
	private static Book bookID;

	@Given("I am Authorized User")
	public void verifyAuthorisedUser() {
		RestAssured.baseURI = BASE_URL;
		RequestSpecification request = RestAssured.given();

		Authorization credentials = new Authorization("API_Class", "Apiclass@2021");

		// String requestBody = "{\r\n" + " \"userName\": \""+USERNAME+"\",\r\n" + "
		// \"password\": \""+PASSWORD+"\"\r\n"
		// + "}";
		request.header("Content-Type", "application/json");
		Response tokenResponse = request.body(credentials).request(Method.POST, "/Account/v1/GenerateToken");

		Assert.assertEquals(tokenResponse.getStatusCode(), 200);

		tokenGenerated = tokenResponse.getBody().as(Token.class);
	}

	@When("A list of books is available")
	public void checkListOfBooksAvailable() {
		RestAssured.baseURI = BASE_URL;
		RequestSpecification request = RestAssured.given();

		request.header("Content-Type", "application/json");

		Response booksResponse = request.request(Method.GET, "/BookStore/v1/Books");
		Assert.assertEquals(booksResponse.getStatusCode(), 200);

//		jsonString = booksResponse.asString();
//
//		List<Map<String, String>> books = JsonPath.from(jsonString).get("books");
//		Assert.assertTrue(books.size() > 0);

		Books books = booksResponse.getBody().as(Books.class);
		bookID = books.books.get(1);

	}

	@When("I assign a book to myself")
	public void assignBook() {
		RestAssured.baseURI = BASE_URL;
		RequestSpecification request = RestAssured.given();

		request.header("Authorization", "Bearer " + tokenGenerated.token).header("Content-Type", "application/json");

		/*
		 * String addBookDetails = "{\r\n" + "  \"userId\": \""+USER_ID+"\",\r\n" +
		 * "  \"collectionOfIsbns\": [\r\n" + "    {\r\n" +
		 * "      \"isbn\": \""+bookID+"\"\r\n" + "    }\r\n" + "  ]\r\n" + "}";
		 */
		ISBN isbn = new ISBN(bookID.isbn);
		AddBookRequest addBookDetails = new AddBookRequest(USER_ID, isbn);

		Response addBooksResponse = request.body(addBookDetails).post("/BookStore/v1/Books");

		Assert.assertEquals(addBooksResponse.getStatusCode(), 201);
	}

	@Then("I remove the book")
	public void removeBook() {
		RestAssured.baseURI = BASE_URL;
		RequestSpecification request = RestAssured.given();

		/*
		 * deleteBody = "{\r\n" + "  \"isbn\": \""+bookID+"\",\r\n" +
		 * "  \"userId\": \""+USER_ID+"\"\r\n" + "}";
		 */

		RemoveBookRequest removeBookRequest = new RemoveBookRequest(USER_ID, bookID.isbn);

		request.header("Authorization", "Bearer " + tokenGenerated.token).header("Content-Type", "application/json");
		Response responseDelete = request.body(removeBookRequest).delete("/BookStore/v1/Book");

		Assert.assertEquals(responseDelete.getStatusCode(), 204);
	}

	@Then("I confirm the book is removed")
	public void confirmBookRemoved() {
		RestAssured.baseURI = BASE_URL;
		RequestSpecification request = RestAssured.given();

		RemoveBookRequest removeBookRequest = new RemoveBookRequest(USER_ID, bookID.isbn);

		request.header("Authorization", "Bearer " + tokenGenerated.token).header("Content-Type", "application/json");

		Response responseDeleteConfirm = request.body(removeBookRequest).delete("/BookStore/v1/Book");

		Assert.assertEquals(responseDeleteConfirm.getStatusCode(), 400);

		Assert.assertEquals(responseDeleteConfirm.getBody().path("message"),
				"ISBN supplied is not available in User's Collection!");
	}

}
