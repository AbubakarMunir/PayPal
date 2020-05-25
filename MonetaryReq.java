package  PayPalProject;
import java.util.*;


public class MonetaryReq extends Request {

	public MonetaryReq() {
	}

	public MonetaryReq(double value, UserAccount req , String msg, UserAccount requester){
		super(msg,requester);
		amount = value;
		requestee = req;
	}

	public Double amount;
	public UserAccount requestee;

	public Boolean processRequest(){

		PayPal paypal = PayPal.getInstance();
		if(paypal.initiateTransaction(this.requestee , this.requester , this.amount)){
			this.status = new String("Resolved");
			return true;
		}
		else
			return false;

	}

	@Override
	public String toString() {
		return "MonetaryReq{" +
				"amount=" + amount +
				", requestee=" + requestee +
				", message='" + message + '\'' +
				", status='" + status + '\'' +
				", requester=" + requester +
				'}';
	}
}