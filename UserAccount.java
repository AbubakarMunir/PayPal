package  PayPalProject;
import java.util.*;


public class UserAccount extends Account {


	public UserAccount() {
	}

	public UserAccount(String email,String name,String pass,String phone,String address) {
		super(email,name,pass,phone);
		this.address = new String(address);
	}

	public String address;
	public List<CreditCard> cards=new ArrayList();
	public List<Transaction> transaction=new ArrayList();
	public String status;
	public List<Request> sentRequests=new ArrayList();
	public List<MonetaryReq> receivedRequests=new ArrayList();

	public Boolean banStatus()
	{
		return status.equalsIgnoreCase("banned");
	}

	public void notifications(String str)
	{
		//send sms notification to user
	}

	public Boolean authenticate(Double amount)
	{
		if(!banStatus()) {
			PayPalDB db = PayPalDB.getInstance();
			UserAccount latestInfo = (UserAccount) db.getAccount(this.accID);
			CreditCard primaryCard = latestInfo.cards.get(0);

			return amount <= primaryCard.balance;
		}
		else
			return false;
	}

	public void printTransactionHistory() {
		if(!transaction.isEmpty()) {
			System.out.println("Your Transaction History:");
			for (Transaction t : transaction)
				System.out.println(t);
		}
		else
			System.out.println("There are no Transactions in your History.");
	}


	public void viewInvoice(Transaction t){
		System.out.println("Invoice:\n" + t.invoice);
	}

	public void sendRequest(String message)
	{
		PayPal p = PayPal.getInstance();
		Request r = new Request(message,this);
		p.assignToAdmin(r);
		this.sentRequests.add(r);

	}

	public void reportUser() {

		PayPal paypal = PayPal.getInstance();
		paypal.generateReport(this);
	}

	public void viewPendingRequests(){
		System.out.println("Your Pending Requests:");
		for(MonetaryReq mr : receivedRequests)
			System.out.println(mr);
	}

	public void viewSentRequests(){
		System.out.println("Your Sent Requests:");
		for(Request r : sentRequests)
			System.out.println(r);
	}


	public void requestMoney() {

		PayPal paypal = PayPal.getInstance();
		paypal.generateMonetaryRequest(this );

	}

	public void transact(int id, Double amount)
	{
		PayPal paypal = PayPal.getInstance();
		Boolean success = false;

		for(Account acc : paypal.accounts)
		{
			if(acc.accID == id)
			{
				if(acc instanceof BusinessAcc)
					success = paypal.initiateTransaction(this,(BusinessAcc)acc,amount);
				else if(acc instanceof UserAccount)
					success = paypal.initiateTransaction(this,(UserAccount)acc,amount);

				break;
			}
		}

		if(success)
			System.out.println("Transaction has been successful.");
		else
			System.out.println("Transaction failed.");
	}


	public void handleRecievedRequest(int ID){
		MonetaryReq handleReq = null;
		for (MonetaryReq mReq : receivedRequests) {
			if (mReq.id == ID) {
				handleReq = mReq;
			}
		}
		Scanner userInput = new Scanner(System.in);
		System.out.println("Do you want to send Money to the requester? ");
		String input = userInput.nextLine();
		if(input.equalsIgnoreCase("yes")) {
			if(handleReq.processRequest())
				handleReq.status = new String("Resolved");
		}
		else
			handleReq.status = new String("Resolved");

	}

	public boolean bindCard(CreditCard card)
	{
		PayPalDB db = PayPalDB.getInstance();
		int id = db.storeCard(card);
		if (id>0)
		{
			this.cards.add(card);
			return true;
		}
		return false;
	}

	public void unbindCard(CreditCard card)
	{
		PayPalDB db = PayPalDB.getInstance();
		db.removeCard(card);
		this.cards.remove(card);
	}

	public void printCards() {
		System.out.println("Your Bind Cards are:");
		for(CreditCard c : cards)
			System.out.println(c);

	}

	public String trackShipment(Shipment order) {

		return "Order: " + order.status + "" + order.dispatchDate;
	}


	public void viewOrders() {
		System.out.println("Your Orders Shipping Information is as follows:");
		for(Transaction t : transaction)
		{
			if(t instanceof BusinessTransaction)
				System.out.println(((BusinessTransaction) t).shipment);
		}
			
	}

	public void deleteMyAcc() {
		PayPal p=PayPal.getInstance();
		Scanner s=new Scanner(System.in);
		System.out.println("Why do you want to close your account?");
		System.out.println("press 1 to proceed and skip the question or 2 if you want to answer");
		int b=s.nextInt();
		if (b==1)
		{
			String m = "close my account";
			Request r =new Request(m,this);
			p.assignToAdmin(r);
			this.sentRequests.add(r);

		}
		else
		{
			System.out.println("Enter your reply");
			String reply=s.next();
			String m="close my account because "+reply;
			Request r=new Request(m,this);
			p.assignToAdmin(r);
			this.sentRequests.add(r);

		}

	}

	@Override
	public void addAcc(Account acc) {

	}

	@Override
	public void remAcc(Account acc) {

	}

	@Override
	public Boolean updateInfo() {

		//option to the user
		System.out.print("What information do you wish to update? \n");
		System.out.print("Press 1 : Email \n" + "Press 2 : Name \n" + "Press 3 : Phone Number \n" + "Press 4 : Address \n");

		Scanner input = new Scanner(System.in);

		int choice = input.nextInt();

		if(choice == 1)
		{
			System.out.print("Enter new email please: ");
			String newemail=input.next();

			//checks for the validity of the new email address
			//if an error occurs the data will not be changed
			if(newemail.contains("@")==false)
			{
				System.out.print("Error!");
				return false;
			}
			if(newemail.contains(".com")==false)
			{
				System.out.print("Error!");
				return false;
			}
			if(newemail.isEmpty()==true)
			{
				System.out.print("Error!");
				return false;
			}
			email=newemail;
			System.out.print("Your infomation has been updated!");
			return true;
		}
		else if(choice == 2)
		{
			System.out.print("Enter new Name please: ");
			String newname = input.next();

			//checks for the validity of the new name
			//if an error occurs the data will not be changed
			if(newname.contains("1")||newname.contains("2")||newname.contains("3")||newname.contains("4")||newname.contains("5")||newname.contains("6")||newname.contains("7")||newname.contains("8")||newname.contains("9")||newname.contains("0"))
			{
				System.out.print("cannot input digits in name");
				return false;
			}
			if(newname.isEmpty()==true)
			{
				System.out.print("Error!");
				return false;
			}
			name=newname;
			System.out.print("Your infomation has been updated!");
			return true;
		}
		else if(choice == 3)
		{
			System.out.print("Enter new phone number please: ");
			String newphoneNum=input.next();

			//checks for the validity of the new phone nummber
			//if an error occurs the data will not be changed
			if(newphoneNum.length()!=11)
			{
				System.out.print("Error! number is not of correct length");
				return false;
			}
			if(newphoneNum.isEmpty()==true)
			{
				System.out.print("Error!");
				return false;
			}
			phoneNum=newphoneNum;
			System.out.print("Your infomation has been updated!");
			return true;
		}
		else if(choice == 4)
		{
			System.out.print("Enter new address please: ");

			address = input.next();


			System.out.print("Your infomation has been updated!");
			return true;
		}
		System.out.print("Error! Please input a valid number for action");
		return false;
	}

	@Override
	public String toString() {
		return "UserAccount{" +
				"address='" + address + '\'' +
				", status='" + status + '\'' +
				", email='" + email + '\'' +
				", name='" + name + '\'' +
				", phoneNum='" + phoneNum + '\'' +
				'}';
	}
}