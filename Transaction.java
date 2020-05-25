package  PayPalProject;
import java.util.*;

/**
 * 
 */
public abstract class Transaction {

	public Transaction() {
	}

	Transaction(Date date, Double money)
	{
		datePerformed = date;
		amount = money;
	}

	public int id;
	public Date datePerformed;
	public Double amount;
	public Double beneficiaryBalanceHist;
	public Double benefactorBalanceHist;
	public Double businessBalanceHist;
	public Double customerBalanceHist;
	public Invoice invoice;
	public String status;
	public Boolean instant;
	public static Double instantFee = 10.0;

	public abstract Double calculateFinalAmount();
	public abstract void notifyUsers();
	public abstract void generateInvoice();
	public abstract Boolean rollback();
	public abstract Boolean fraudDetection();
	public abstract Boolean executeTransaction();
	public abstract void processInstantly();


	@Override
	public abstract String toString();
}