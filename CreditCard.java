package  PayPalProject;
import java.util.*;

/**
 * 
 */
public class CreditCard {

	public CreditCard() {
	}

	CreditCard(Date date, int cvv, String cardNum, Double balance)
	{
		this.expirationDate=date;
		this.cvv=cvv;
		this.cardNum = new String(cardNum);
		this.balance=balance;
	}

	public Date expirationDate;
	public int cvv;
	public String cardNum;
	public Double balance;

	public void printInfo() {
		System.out.println(this);
	}

	@Override
	public String toString() {
		return "CreditCard{" +
				"expirationDate=" + expirationDate +
				", cvv=" + cvv +
				", cardNum='" + cardNum + '\'' +
				", balance=" + balance +
				'}';
	}
}