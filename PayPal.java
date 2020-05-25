package  PayPalProject;
import java.util.*;

public class PayPal {

	private PayPal() {
		
	}

	static PayPal getInstance(){
		if(instance == null){
                        instance=new PayPal();
			return instance;
		}
		return instance;
	}

	private static PayPal instance;
	public List<UserAccount> accounts=new ArrayList();
	public List<AdminAccount> admins=new ArrayList();
	public List<Account> online=new ArrayList();

	public UserAccount login(String email, String password) {

		UserAccount act = null;
		PayPalDB db = PayPalDB.getInstance();
		boolean check = db.login(email,password);

		if(check==true)
		{
			for(UserAccount x : accounts)
			{
				if (x.email == email && x.password == password)
				{
					online.add(x);
					System.out.println("User " + x.name + " logged in");
					act=x;
				}
			}
		}

		return act;
	}

	public boolean logout(UserAccount account)
	{
		if(online.contains(account))
		{
			System.out.println("User " + account.name + " logged out.");
			online.remove(account);
			return true;
		}
		return false;
	}

	public Boolean signUp(String email, String password, String name, String address, String phoneNum) {
		PayPalDB pdb=PayPalDB.getInstance();

		Scanner s=new Scanner(System.in);
		System.out.println("Enter 1 to make a Standard user account or 2 for business account");
		int inp=s.nextInt();
		if(inp==1)
		{
                        
			UserAccount ua=new UserAccount(email,name,password,phoneNum,address);
			int uid=pdb.storeAccount(ua);
			ua.accID=uid;
			instance.addUserToAdmins(ua);
			instance.accounts.add(ua);
			return true;
		}
		else if(inp ==2)
		{

			System.out.println("Enter your business name");
			String bname=s.next();
			BusinessAcc b=new BusinessAcc(email,name,password,phoneNum,address,bname);
			int bid=pdb.storeAccount(b);
			b.accID=bid;
			instance.addUserToAdmins(b);
			instance.accounts.add(b);
			return true;
		}

		return false;
	}

	public Boolean deleteUserAccount(UserAccount ua)
	{
		Boolean check=false;

		if(accounts.remove(ua)==true)
		{
			check=true;
		}
		for(int i=0;i<admins.size();i++)
		{
			admins.get(i).remAcc(ua);
		}
		return check;
	}


	public void addUserToAdmins(Account acc)
	{
		for(int i=0;i<instance.admins.size();i++)
		{
			instance.admins.get(i).addAcc(acc);
		}

	}


	public void manageCards(UserAccount act)
	{
		System.out.println("--- All of user's cards	---");
		for(CreditCard c : act.cards)
			System.out.println(c);

	}

	public boolean addCard(UserAccount act)
	{
		Date expDate;
		int year,month,day;
		int cvv;
		String cardNum;
		Double balance;
		Scanner input = new Scanner(System.in);
		System.out.println("Add a new credit card to your PayPal account.");

		System.out.println("Enter the Credit Card Number.");
		cardNum = input.nextLine();
		System.out.println("Enter the Credit Card CVV.");
		cvv = input.nextInt();
		System.out.println("Enter the date of expiration:");
		System.out.printf("Enter Year: ");
		year = input.nextInt();
		System.out.printf("Enter Month: ");
		month = input.nextInt();
		System.out.printf("Enter Day: ");
		day = input.nextInt();
		expDate = new Date(year,month,day);
		System.out.println("Enter the Credit Card Balance.");
		balance = input.nextDouble();

		System.out.printf("Input Details:");
		System.out.println("Card Number: " + cardNum);
		System.out.printf("CVV: " + cvv);
		System.out.println("Balance: " + balance);
		System.out.printf("Date of Expiration: " + expDate);

		input.close();
		return act.bindCard(new CreditCard(expDate,cvv,cardNum,balance));
	}

	public void removeCard(UserAccount act)
	{
		Scanner input = new Scanner(System.in);
		CreditCard card=null;
		String cardNum;
		int cvv;
		System.out.printf("--- Remove a Credit Card from PayPal Account ---");
		System.out.printf("Enter the credit card number");
		cardNum = input.nextLine();
		System.out.println("Enter the credit card CVV");
		cvv = input.nextInt();
		for (CreditCard x : act.cards)
		{
			if(x.cardNum.equalsIgnoreCase(cardNum) && x.cvv == cvv) {
				card = x;
				break;
			}
		}
		if(card!=null)
			act.unbindCard(card);
		input.close();
	}

	public void manageTransactions(Account act)
	{
		PayPalDB db = PayPalDB.getInstance();
		List<Transaction> userTransactions = db.getAllTransactions(act);
		for(Transaction x : userTransactions)
			System.out.println(x);
	}


	public void requestShipment(UserAccount act, BusinessTransaction transaction)
	{
		PayPalDB db = PayPalDB.getInstance();
		Calendar cal = Calendar.getInstance();
		Shipment S = new Shipment();
		S.fee=50.0;
		S.details="";

		S.dispatchDate = new Date();
		cal.setTime(S.dispatchDate);
		cal.add(Calendar.DATE,14);
		S.arrivalDate = cal.getTime();

		S.status="processing";
		S.address = act.address;
		int Sid = db.addShipment(act.accID,S);
		S.id=Sid;
		transaction.shipment = S;
	}

	public void TrackShipment(UserAccount act)
	{
		Scanner input = new Scanner(System.in);
		int tranId=0;
		BusinessTransaction trans=null;
		System.out.println("---		Shipment Tracking		---");
		System.out.println("Enter the Id of transaction:");
		tranId = input.nextInt();
		input.close();

		for(Transaction X : act.transaction)
		{
			if(X.id == tranId)
			{
				trans = (BusinessTransaction)X;
				break;
			}
		}

		if(trans != null)
		{
			act.trackShipment(trans.shipment);
		}
		else
			System.out.println("Error: no transaction exists with transaction id = " + tranId);
	}

	public Boolean initiateTransaction(UserAccount beneficiary,  UserAccount benefactor, Double amount) {

		StandardTransaction trans = new StandardTransaction(new Date(),amount,beneficiary,benefactor);

		if(beneficiary.authenticate(trans.calculateFinalAmount()))
		{
			if(!benefactor.banStatus())
			{
				Scanner in = new Scanner((System.in));

				System.out.println("Do you want to process This Standard Transaction instantly (Additional $10 charges apply)? (Y/N)");
				String userInput = in.next();

				if(userInput.equalsIgnoreCase("Y"))
					trans.instant = true;

				if(!trans.executeTransaction())
					return false;

				trans.generateInvoice();

				trans.notifyUsers();

				PayPalDB db = PayPalDB.getInstance();

				trans.invoice.id = db.storeInvoice(trans.invoice);
				trans.id = db.storeTransaction(trans);

				beneficiary.transaction.add(trans);

				benefactor.transaction.add(trans);

				return true;
			}
			else
				return false;
		}
		else
			return false;
	}

	public Boolean initiateTransaction(UserAccount customer,  BusinessAcc business, Double amount) {

		BusinessTransaction trans = new BusinessTransaction(new Date(),amount,business,customer);

		if(customer.authenticate(trans.calculateFinalAmount()))
		{
			if(!business.banStatus())
			{
				Scanner in = new Scanner((System.in));
				PayPalDB db = PayPalDB.getInstance();

				System.out.println("Do you want to process This Business Transaction instantly (Additional $10 charges apply)? (Y/N)");
				String userInput = in.next();

				if(userInput.equalsIgnoreCase("Y"))
					trans.instant = true;

				if(!trans.executeTransaction())
					return false;

				System.out.println("Do you want us to provide product shipping along this transaction (Shipping costs apply)? (Y/N)");
				userInput = in.next();

				if(userInput.equalsIgnoreCase("Y"))
				{
					requestShipment(customer,trans);
				}

				trans.generateInvoice();

				trans.notifyUsers();

				trans.invoice.id = db.storeInvoice(trans.invoice);
				trans.id = db.storeTransaction(trans);

				customer.transaction.add(trans);

				business.transaction.add(trans);

				return true;

			}
			else
				return false;

		}
		else
			return false;
	}

	public void assignToAdmin(Request req) {

		int minReq = admins.get(0).requests.size();
		AdminAccount admin = admins.get(0);
		for(AdminAccount adminAcc :admins){
			if(minReq > adminAcc.requests.size()){
				admin = adminAcc;
				minReq = adminAcc.requests.size();
			}
		}
		admin.requests.add(req);
	}

	void generateReport(UserAccount requester){
		Scanner userInput = new Scanner(System.in);
		System.out.println("Enter User account ID that you want to report. ");
		int ID = userInput.nextInt();
		System.out.println("Enter your message: ");
		String msg = userInput.nextLine();
		userInput.close();

		Report report = null;

		for(UserAccount acc:accounts){
			if(acc.accID == ID){
				report = new Report(msg,acc,requester);
			}
		}

		assignToAdmin(report);
		//report.id = PayPalDB.storeReport(report);

	}

	void generateMonetaryRequest(UserAccount user){
		Scanner userInput = new Scanner(System.in);
		System.out.println("Enter Requestee account ID: ");
		int ID = userInput.nextInt();
		System.out.println("Enter your message: ");
		String msg = userInput.nextLine();
		System.out.println("Enter amount: ");
		int amount = userInput.nextInt();
		userInput.close();

		MonetaryReq mReq =null;
		for(UserAccount acc:accounts){
			if(acc.accID == ID){
				mReq = new MonetaryReq(amount,acc,msg,user);
				acc.receivedRequests.add(mReq);
			}
		}
		user.sentRequests.add(mReq);
		//mReq.id = PayPalDB.storeRequest(mReq);

	}

	public void loadAllTransactions(AdminAccount admin) {
		PayPalDB db = PayPalDB.getInstance();
		admin.transactions = db.getAllTransactions(admin);
	}

	public void loadAllRequests(AdminAccount admin) {
		// TODO implement here
		PayPalDB db = PayPalDB.getInstance();
		admin.requests = db.getAllRequests(admin);
	}

	Boolean checkFraud(Transaction trans){
		if(trans.fraudDetection()){
			trans.rollback();
			return true;
		}
		else
			return false;
	}

}