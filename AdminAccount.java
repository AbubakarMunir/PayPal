package  PayPalProject;
import java.util.*;

public class AdminAccount extends Account {


	public AdminAccount() {
	}

	public List<Transaction> transactions = new ArrayList<Transaction>();
	public List<Account> users = new ArrayList<Account>();
	public List<Request> requests = new ArrayList<Request>();
	public String privileges;


	public AdminAccount(String e,String n,String p,String pn,String prvlgs) {
		super(e,n,p,pn);
		privileges = new String(prvlgs);
                
	
	}

	@Override
	public Boolean updateInfo() {

		//option to the user
		System.out.print("What information do you wish to update? \n");
		System.out.print("Press 1 : Email \n" + "Press 2 : Name \n" + "Press 3 : Phone Number \n" + "Press 4 : Privileges \n");

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
		else if(choice ==4)
		{
			System.out.print("Enter new privileges please: ");
			privileges = input.next();

			System.out.print("Your infomation has been updated!");
			return true;
		}
		System.out.print("Error! Please input a valid number for action");
		return false;
	}


	public Boolean closeAccount(UserAccount ua) {
		//casting is essential here to check if useraccount has any pending monetary requests
		Boolean allowClosure = true;
		for(int i=0;i<ua.receivedRequests.size();i++)
		{
			if(ua.receivedRequests.get(i).status.equalsIgnoreCase("Unresolved")==true);
				allowClosure = false;

		}

		for(int i=0;i<ua.transaction.size();i++)
		{
			if(ua.transaction.get(i).status.equalsIgnoreCase("In Process")==true);
				allowClosure = false;

		}
		if(allowClosure==false)
		{
			System.out.println("Unable to delete your account as you have some pending stuff");
			return allowClosure;
		}
		else
		{
			PayPal p=PayPal.getInstance();
			p.deleteUserAccount(ua);

			//delete account procedure db called here
		}


		return true;
	}


	@Override
	public void addAcc(Account acc) {
		users.add(acc);
	}

	@Override
	public void remAcc(Account acc) {
		users.remove(acc);
	}

	public void handleCloseAccRequest(Request req)
	{
		if(closeAccount(req.requester))
			req.status = new String("Resolved");
		else
			req.status = new String("Unresolved");
	}

	public void handleReport(Report rep)
	{
		Boolean flag = false;
		UserAccount repAcc = ((Report) rep).reported;
		for(Transaction trans :repAcc.transaction){
			if(trans.fraudDetection()){
				banUser(repAcc);
				repAcc.notifications("Your Paypal account has been BANNED. ");
				rep.response = new String(repAcc.name + " has been banned.");
				flag = true;
			}
		}
		if(flag){
		rep.response = new String(repAcc.name + " can not banned.");
		}
		rep.status = new String("Resolved");
	}

	void printTransactions()
	{
		System.out.println("All Transactions made using the Platform:");
		for(Transaction t: transactions)
			System.out.println(t);
	}

	void printUsers()
	{
		System.out.println("All Registered Users on Platform:");
		for(Account acc: users)
			System.out.println(acc);
	}

	void printInBoundRequests()
	{
		System.out.println("Requests waiting to be entertained:");
		for(Request req : requests)
			System.out.println(req);
	}

	public void rollbackFraudulentTransaction(){
		PayPal paypal = PayPal.getInstance();
		paypal.loadAllTransactions(this);
		for(Transaction t: transactions){
			if(t.fraudDetection()){
				t.rollback();
			}
		}
	}

	void banUser(UserAccount acc)
	{
		acc.status = new String("Banned");
	}

	void inspectAccount(){

		PayPal paypal = PayPal.getInstance();
		paypal.loadAllRequests(this);
		for(Request req: requests){
			if(req instanceof Report){
				handleReport((Report) req);
			}
		}
	}

	@Override
	public String toString() {
		return "AdminAccount{" +
				"privileges='" + privileges + '\'' +
				", email='" + email + '\'' +
				", name='" + name + '\'' +
				", phoneNum='" + phoneNum + '\'' +
				'}';
	}
}