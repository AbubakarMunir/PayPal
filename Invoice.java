package  PayPalProject;
import java.util.*;

public class Invoice {


	public Invoice() {

	}

	public Invoice(String r, Double total, Date date)
	{
		receipt = new String(r);
		this.total = total;
		dateGenerated = date;
	}

	public int id;
	public String receipt;
	public Double total;
	public Date dateGenerated;


	public void printDetails() {
		System.out.println(this);

	}

	@Override
	public String toString() {
		return "Invoice{" +
				"receipt='" + receipt + '\'' +
				", total=" + total +
				", dateGenerated=" + dateGenerated +
				'}';
	}
}