package apiEngine.requests;

import java.util.ArrayList;
import java.util.List;

public class AddBookRequest {
	
	public String userId;
	public List<ISBN> collectionOfIsbns;
	
	public AddBookRequest(String userId, ISBN isbn) {

		this.userId = userId;
		collectionOfIsbns  = new ArrayList<ISBN>();
		collectionOfIsbns.add(isbn);
	}
	

}
