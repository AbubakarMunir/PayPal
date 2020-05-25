package  PayPalProject;
import java.util.*;

public class StandardTransaction extends Transaction {

	public StandardTransaction() {
	}

	public StandardTransaction(Date date, Double amount, UserAccount beneficiary, UserAccount benefactor)
	{
		super(date,amount);
		this.beneficiary = beneficiary;
		this.benefactor = benefactor;
	}

	public UserAccount beneficiary;
	public UserAccount benefactor;

	public Double calculateFinalAmount() {

		if(instant)
			return amount+instantFee;
		else
			return amount;

	}

	@Override
	public Boolean executeTransaction() {

		PayPalDB db = PayPalDB.getInstance();

		benefactorBalanceHist = beneficiary.cards.get(0).balance;
		benefactorBalanceHist = benefactor.cards.get(0).balance;

		if(instant)
			processInstantly();
		else {
			beneficiary.cards.get(0).balance -= calculateFinalAmount();
			status = new String("In Process");
		}

		return db.updateBalance(beneficiary,benefactor);
	}

	@Override
	public void processInstantly() {
		beneficiary.cards.get(0).balance -= calculateFinalAmount();
		benefactor.cards.get(0).balance += amount;
		status = new String("Completed");
	}

	public void notifyUsers()
	{
		String str = new String("Standard Transaction between "+ beneficiary.name +" "+ benefactor.name+ "has been successful on " + datePerformed+ ".");
		beneficiary.notifications(str);
		benefactor.notifications(str);
	}

	public void generateInvoice()
	{
		String receipt = new String("--Standard Transaction-- \nBeneficiary: "+ beneficiary + "\nBenefactor: "+ benefactor +"Amount = "+ amount);
		invoice  = new Invoice(receipt,amount,new Date());

	}

	public Boolean fraudDetection(){
		if(this.status.equalsIgnoreCase("complete") && benefactor.cards.get(0).balance == this.benefactorBalanceHist){
				if (beneficiary.cards.get(0).balance == this.beneficiaryBalanceHist)
					return true;
				return true;
		}
		else
			return false;
	}

	public Boolean rollback(){
		PayPalDB db = PayPalDB.getInstance();
		benefactor.cards.get(0).balance += this.amount;
		beneficiary.cards.get(0).balance -= this.amount;

		return db.updateBalance(beneficiary,benefactor);
	}

	@Override
	public String toString() {
		return "StandardTransaction{" +
				"beneficiary=" + beneficiary +
				", benefactor=" + benefactor +
				", datePerformed=" + datePerformed +
				", amount=" + amount +
				", invoice=" + invoice +
				", status='" + status + '\'' +
				", instant=" + instant +
				'}';
	}
}