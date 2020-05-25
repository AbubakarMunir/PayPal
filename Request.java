package  PayPalProject;
import java.util.*;

public class Request {

	public  Request() {
	}

	public String message;
	public String status;
	public int id;
	public UserAccount requester;

	public void printRequest() {
		// TODO implement here
	}

	public Request(String msg , UserAccount acc){
		message = msg;
		status  = new String("UnResolved");
		requester = acc;

	}

	@Override
	public String toString() {
		return "Request{" +
				"message='" + message + '\'' +
				", status='" + status + '\'' +
				", requester=" + requester +
				'}';
	}
}