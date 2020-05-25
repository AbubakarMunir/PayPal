package  PayPalProject;
import java.util.*;

/**
 * 
 */
public class Report extends Request {

	public Report() {
	}

	public Report(String msg , UserAccount acc, UserAccount report){
		super(msg,acc);
		reported = report;
		response = new String("No Response Yet");
	}

	public String response;
	public AdminAccount admin;
	public UserAccount reported;


	@Override
	public String toString() {
		return "Report{" +
				"response='" + response + '\'' +
				", admin=" + admin +
				", reported=" + reported +
				", message='" + message + '\'' +
				", status='" + status + '\'' +
				", requester=" + requester +
				'}';
	}
}