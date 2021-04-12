package apiEngine.requests;

public class RemoveBookRequest {

	public String isbn;
	public String userId;

	public RemoveBookRequest(String isbn, String userId) {
		this.isbn = isbn;
		this.userId = userId;
	}
}
