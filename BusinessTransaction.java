package  PayPalProject;
import java.util.*;

public class BusinessTransaction extends Transaction {

	public BusinessTransaction() {

	}

	public BusinessTransaction(Date date, Double amount, BusinessAcc acc, UserAccount cus)
	{
		super(date,amount);
		merchant = acc;
		customer = cus;
	}

	public static Double feeRate = 0.1;
	public Shipment shipment = null;
	public BusinessAcc merchant;
	public UserAccount customer;

	public Double calculateFinalAmount() {

		if(instant)
			amount+=instantFee;

		if(feeRate*amount > 10.0)
			amount+=10;
		else
			amount+=amount*feeRate;

		return amount;

	}

	@Override
	public Boolean executeTransaction() {

		PayPalDB db = PayPalDB.getInstance();

		customerBalanceHist = customer.cards.get(0).balance;
		businessBalanceHist = merchant.cards.get(0).balance;

		if(instant)
			processInstantly();
		else {
			customer.cards.get(0).balance -= calculateFinalAmount();
			status = new String("In Process");
		}

		return db.updateBalance(customer,merchant);

	}

	@Override
	public void processInstantly() {
		customer.cards.get(0).balance -= calculateFinalAmount();
		merchant.cards.get(0).balance += amount;
		status = new String("Completed");
	}

	public void notifyUsers()
	{
		String str = new String("Business Transaction between "+ customer.name +" "+ merchant.businessName+ "has been successful on " + datePerformed+ ".");
		customer.notifications(str);
		merchant.notifications(str);
	}

	public void generateInvoice()
	{
		String receipt = new String("--Business Transaction-- \nCustomer: "+ customer + "\nBusiness: "+ merchant +"Amount = "+ amount);
		invoice  = new Invoice(receipt,amount,new Date());
	}

	public void setShipment(Shipment sp) {
		shipment = sp;
	}

	public Boolean fraudDetection(){
		if(this.status.equalsIgnoreCase("complete") && customer.cards.get(0).balance == this.customerBalanceHist){
				if (merchant.cards.get(0).balance == this.businessBalanceHist)
					return true;
				return true;
			}
		else
			return false;
	}

	public Boolean rollback(){
		PayPalDB db = PayPalDB.getInstance();
		customer.cards.get(0).balance += this.amount;
		merchant.cards.get(0).balance -= this.amount;

		return db.updateBalance(customer,merchant);
	}

	@Override
	public String toString() {
		return "BusinessTransaction{" +
				"shipment=" + shipment +
				", merchant=" + merchant +
				", customer=" + customer +
				", datePerformed=" + datePerformed +
				", amount=" + amount +
				", invoice=" + invoice +
				", status='" + status + '\'' +
				", instant=" + instant +
				'}';
	}
}