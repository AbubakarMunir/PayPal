package  PayPalProject;
import java.util.*;

/**
 * 
 */
public class Shipment {

	public Shipment() {
	}

	public int id;
	public String address;
	public String status;
	public Date dispatchDate;
	public Date arrivalDate;
	public String details;
	public Double fee;


	public void printDetails() {
		// TODO implement here
	}

	@Override
	public String toString() {
		return "Shipment{" +
				"address='" + address + '\'' +
				", status='" + status + '\'' +
				", dispatchDate=" + dispatchDate +
				", arrivalDate=" + arrivalDate +
				", details='" + details + '\'' +
				", fee=" + fee +
				'}';
	}
}