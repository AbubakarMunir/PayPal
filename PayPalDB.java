package  PayPalProject;
import java.sql.*;
import java.util.*;

import com.microsoft.sqlserver.jdbc.*;


public class PayPalDB {

	private static PayPalDB instance = null;
	private Connection con = null;
	private CallableStatement call_stmt = null;
	private Statement stmt = null;

	public static PayPalDB	getInstance()
	{
		if(instance == null)
		{
			instance = new PayPalDB();
		}
		return instance;
	}


	private PayPalDB()
	{
		try {
                        String s = "jdbc:sqlserver://desktop-a0mgr7n\\sqlexpress:1433;databaseName=PayPal";
                        con=DriverManager.getConnection(s,"Abubakar","123456");

			
			System.out.println("-- DB: Connection Established.");

		}
		catch (Exception e)
		{
			System.out.println("-- DB: Error establishing connection.");
			System.out.println(e);
			System.out.println(Arrays.toString(e.getStackTrace()));
		}
	}

	boolean login(String email, String password)
	{
		boolean result=false;
		try
		{
			call_stmt = con.prepareCall("{call login(?,?,?)}");
			call_stmt.setString(1,email);
			call_stmt.setString(2,password);
			call_stmt.registerOutParameter(3,Types.INTEGER);
			call_stmt.execute();
			if(call_stmt.getInt(3)==1)
				result=true;
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		finally
		{
			call_stmt=null;
		}
		return result;                                                  
	}                                                                       
                                                                                    
	public int storeAccount(UserAccount acc) {                              
		int id=0;                                                       
		try {                                                           
			call_stmt = con.prepareCall("{call addStdUser(?,?,?,?,?,?)}");
			call_stmt.setString(1,acc.name);                             
			call_stmt.setString(2,acc.email);                           
			call_stmt.setString(3,acc.password);                        
			call_stmt.setString(4,acc.phoneNum);                        
			call_stmt.setString(5,acc.address);                         
			call_stmt.registerOutParameter(6,Types.INTEGER);            
			call_stmt.execute();                                    
			id = call_stmt.getInt(6);                                                               
		}                                                               
		catch (Exception e)                                             
		{                                                               
			System.out.println(e);                                  
			System.out.println(e.getStackTrace());                                        
		}                                                                           
		finally                                                         
		{                                                               
			call_stmt = null;                                       
		}                                                               
		return id;                                                      
	}                                                                       
                                                                                

	public int storeAccount(BusinessAcc acc) {
		int id =0;
		try
		{
			call_stmt = con.prepareCall("{ call addBsnUser(?,?,?,?,?,?,?)}");
			call_stmt.setString(1,acc.name);
			call_stmt.setString(2,acc.email);
			call_stmt.setString(3,acc.password);
			call_stmt.setString(4,acc.phoneNum);
			call_stmt.setString(5,acc.address);
			call_stmt.setString(6,acc.businessName);
			call_stmt.registerOutParameter(7,Types.INTEGER);
			call_stmt.execute();
			id=call_stmt.getInt(7);
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		finally {
			call_stmt=null;
		}
		return id;
	}


	public int storeAccount(AdminAccount acc) {
		int id =0;
		try
		{
			call_stmt = con.prepareCall("{call addAdmin(?,?,?,?,?,?)}");
			call_stmt.setString(1,acc.name);
			call_stmt.setString(2,acc.email);
			call_stmt.setString(3,acc.password);
			call_stmt.setString(4,acc.phoneNum);
			call_stmt.setInt(5,3);
			call_stmt.registerOutParameter(6,Types.INTEGER);
			call_stmt.execute();
			id = call_stmt.getInt(6);
		}
		catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		finally {
			call_stmt=null;
		}
		return id;
	}

	public List<UserAccount> getAllUserAccounts() {
		List<UserAccount> accounts=null;
		try
		{
			call_stmt = con.prepareCall("{call getAllUserAccounts()}");
			call_stmt.execute();
			ResultSet rs = call_stmt.getResultSet();

			while (rs.next())
			{
				if (accounts == null)
					accounts = new ArrayList<UserAccount>();
				if(rs.getInt("_type") == 1)
				{
					UserAccount account = new UserAccount();
					account.accID = rs.getInt("actId");
					account.email = rs.getString("email");
					account.password = rs.getString("passwd");
					account.name = rs.getString("_name");
					account.phoneNum = rs.getString("phoneNum");
					account.address = rs.getString("_address");
					account.status = rs.getString("_status");

					account.cards = getAllCards(account);
					account.transaction = getAllTransactions(account);

					List<Request> requests = getAllRequests(account);
					List<MonetaryReq> monRequests = getAllMonRequests(account);
					List<Report> reports = getAllRepRequests(account);

					if(requests != null)
						account.sentRequests.addAll(requests);

					if(monRequests != null)
						for(MonetaryReq x : monRequests)
							if(x.requester.accID == account.accID)
								account.sentRequests.add(x);
							else if(x.requestee.accID == account.accID)
								account.receivedRequests.add(x);

					if(reports != null)
						account.sentRequests.addAll(reports);

					accounts.add(account);

				}
				else if( rs.getInt("_type") == 2)
				{
					BusinessAcc account = new BusinessAcc();
					account.accID = rs.getInt("actId");
					account.email = rs.getString("email");
					account.password = rs.getString("passwd");
					account.name = rs.getString("_name");
					account.phoneNum = rs.getString("phoneNum");
					account.address = rs.getString("_address");
					account.status = rs.getString("_status");
					account.businessName = rs.getString("businessName");

					account.cards = getAllCards(account);
					account.transaction = getAllTransactions(account);
					//TODO: add request list

					List<Request> requests = getAllRequests(account);
					List<MonetaryReq> monRequests = getAllMonRequests(account);
					List<Report> reports = getAllRepRequests(account);

					if(requests != null)
						account.sentRequests.addAll(requests);

					if(monRequests != null)
						for(MonetaryReq x : monRequests)
							if(x.requester.accID == account.accID)
								account.sentRequests.add(x);
							else if(x.requestee.accID == account.accID)
								account.receivedRequests.add(x);

					if(reports != null)
						account.sentRequests.addAll(reports);

					accounts.add(account);
				}
			}

		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		finally {
			call_stmt=null;
		}
		return accounts;
	}

	public List<AdminAccount> getAllAdminAccounts()
	{
		List<AdminAccount> admins=null;
		try
		{
			call_stmt=con.prepareCall("{call getAllAdmins() }");
			call_stmt.execute();
			ResultSet rs = call_stmt.getResultSet();

			while (rs.next())
			{
				if(admins == null)
					admins = new ArrayList<AdminAccount>();
				AdminAccount account = new AdminAccount();
				account.accID = rs.getInt("actId");
				account.email = rs.getString("email");
				account.password = rs.getString("passwd");
				account.name = rs.getString("_name");
				account.phoneNum = rs.getString("phoneNum");

				admins.add(account);
			}

		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		finally {
			call_stmt=null;
		}
		return admins;
	}

	public Account getAccount(int id) {
		UserAccount Uact = null;
		BusinessAcc Bact =null;
		try
		{
			call_stmt = con.prepareCall("{call getAccount(?)}");
			call_stmt.setInt(1,id);
			call_stmt.execute();
			ResultSet rs = call_stmt.getResultSet();

			while (rs.next())
			{
				if(rs.getInt("_type") == 1)
				{
					Uact = new UserAccount();
					Uact.accID = rs.getInt("actId");
					Uact.email = rs.getString("email");
					Uact.password = rs.getString("passwd");
					Uact.name = rs.getString("_name");
					Uact.phoneNum = rs.getString("phoneNum");
					Uact.address = rs.getString("_address");
				}
				else if(rs.getInt("_type")==2)
				{
					Bact = new BusinessAcc();

				}
			}

		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		finally {
			call_stmt =null;
		}

		return (Uact != null? Uact:Bact);
	}

	public boolean deleteAccount(Account account)
	{
		boolean result = false;
		try
		{
			call_stmt = con.prepareCall("{call deleteAccount(?,?)}");
			call_stmt.setInt(1,account.accID);
			call_stmt.registerOutParameter(2,Types.INTEGER);
			call_stmt.execute();
			if(call_stmt.getInt(2)==1)
				result=true;
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		finally
		{
			call_stmt = null;
		}
		return result;
	}

	public boolean updateAccount(UserAccount account)
	{
		int ret=0;
		try
		{
			call_stmt = con.prepareCall("{call updateStdAccount(?,?,?,?,?,?,?,?)}");
			call_stmt.setInt(1,account.accID);
			call_stmt.setString(2,account.email);
			call_stmt.setString(3,account.password);
			call_stmt.setString(4,account.name);
			call_stmt.setString(5,account.phoneNum);
			call_stmt.setString(6,account.address);
			call_stmt.setString(7,account.status);
			call_stmt.registerOutParameter(8,Types.INTEGER);
			call_stmt.execute();
			ret = call_stmt.getInt(8);
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		finally {
			call_stmt=null;
		}
		return ret>0;
	}

	public boolean updateAccount(BusinessAcc account)
	{
		int ret=0;
		try
		{
			call_stmt = con.prepareCall("{call updateStdAccount(?,?,?,?,?,?,?,?,?)}");
			call_stmt.setInt(1,account.accID);
			call_stmt.setString(2,account.email);
			call_stmt.setString(3,account.password);
			call_stmt.setString(4,account.name);
			call_stmt.setString(5,account.phoneNum);
			call_stmt.setString(6,account.address);
			call_stmt.setString(7,account.status);
			call_stmt.setString(8,account.businessName);
			call_stmt.registerOutParameter(9,Types.INTEGER);
			call_stmt.execute();
			ret = call_stmt.getInt(9);
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		finally {
			call_stmt=null;
		}
		return ret>0;

	}

	public void updateAccount(AdminAccount account)
	{

	}



	public int storeTransaction(BusinessTransaction bt) {
		int id=0;
		try
		{
			call_stmt = con.prepareCall("{call AddTransaction(?,?,?,?,?,?,?,?,?,?)}");
			call_stmt.setDate(1,(java.sql.Date)bt.datePerformed);
			call_stmt.setDouble(2,bt.amount);
			call_stmt.setInt(3,bt.invoice.id);
			call_stmt.setString(4,bt.status);
			if(bt.instant)
				call_stmt.setInt(5,1);
			else
				call_stmt.setInt(5,0);
			if(bt.shipment!=null)
				call_stmt.setInt(6,bt.shipment.id);
			call_stmt.setInt(7,bt.customer.accID);
			call_stmt.setInt(8,bt.merchant.accID);
			call_stmt.setInt(9,2);
			call_stmt.registerOutParameter(10,Types.INTEGER);
			call_stmt.execute();
			id = call_stmt.getInt(10);
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		finally {
			call_stmt=null;
		}
		return id;
	}


	public int storeTransaction(StandardTransaction st) {
		int id=0;
		try
		{
			call_stmt = con.prepareCall("{call addTransaction(?,?,?,?,?,?,?,?,?,?)}");
			call_stmt.setDate(1,(java.sql.Date)st.datePerformed);
			call_stmt.setDouble(2,st.amount);
			call_stmt.setInt(3,st.invoice.id);
			call_stmt.setString(4,st.status);
			if(st.instant)
				call_stmt.setInt(5,1);
			else
				call_stmt.setInt(5,0);
			call_stmt.setInt(7,st.beneficiary.accID);
			call_stmt.setInt(8,st.benefactor.accID);
			call_stmt.setInt(9,1);
			call_stmt.registerOutParameter(10,Types.INTEGER);
			call_stmt.execute();
			id = call_stmt.getInt(10);

		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		finally {
			call_stmt=null;
		}
		return id;
	}


	public Transaction getTransaction(int id) {

		return null;
	}


	public List<Transaction> getAllTransactions(Account acc) {
		int id = acc.accID;
		List<Transaction> l = null;
		try{
			call_stmt = con.prepareCall("{call getAllTranactions(?) }");
			call_stmt.setInt(1,id);
			call_stmt.execute();
			ResultSet rs = call_stmt.getResultSet();

			while (rs.next())
			{
				if(l == null)
					l = new ArrayList<Transaction>();

				if (rs.getInt("_type") == 1)
				{
					StandardTransaction T = new StandardTransaction();
					T.id = rs.getInt("id");
					T.datePerformed = rs.getDate("datePerformed");
					T.amount = rs.getDouble("amount");
					T.status = rs.getString("transStatus");
					T.instant = rs.getBoolean("instant");

					int invId = rs.getInt("invoiceId");
					if (invId > 0)
						T.invoice = getInvoice(invId);
					l.add(T);
				}
				else
				{
					BusinessTransaction T = new BusinessTransaction();
					T.id = rs.getInt("id");
					T.datePerformed = rs.getDate("datePerformed");
					T.amount = rs.getDouble("amuount");
					T.status = rs.getString("transStatus");
					T.instant = rs.getBoolean("instant");
					int invId = rs.getInt("invoiceId");
					int sId = rs.getInt("shipmentID");

					if(invId>0)
						T.invoice = getInvoice(invId);
					if(sId>0)
						T.shipment = getShipment(sId);
					l.add(T);
				}
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		finally
		{
			call_stmt = null;
		}

		return l;
	}


	public void deleteTransaction(int id) {
		try
		{
			call_stmt = con.prepareCall("{call deleteTransaction(?)}");
			call_stmt.setInt(1,id);
			call_stmt.execute();
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		finally {
			call_stmt=null;
		}
	}


	public int storeRequest(Request req) {
		int id=0;
		try{

			call_stmt = con.prepareCall("{call addRequest(?,?,?,?)}");
			call_stmt.setString(1,req.message);
			call_stmt.setString(2,req.status);
			call_stmt.setInt(3,req.requester.accID);
			call_stmt.registerOutParameter(4,Types.INTEGER);
			call_stmt.execute();
			id = call_stmt.getInt(4);
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		finally {
			call_stmt=null;
		}
		return  id;
	}


	public int storeRequest(MonetaryReq mr)
	{
		int id=0;
		try
		{
			call_stmt = con.prepareCall("{call addMonetaryRequest(?,?,?,?,?,?)}");
			call_stmt.setString(1,mr.message);
			call_stmt.setString(2,mr.status);
			call_stmt.setInt(3,mr.requester.accID);
			call_stmt.setDouble(4,mr.amount);
			call_stmt.setInt(5,mr.requestee.accID);
			call_stmt.registerOutParameter(6, Types.INTEGER);
			call_stmt.execute();
			id = call_stmt.getInt(6);

		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		finally {
			call_stmt=null;
		}
		return id;
	}


	public int storeRequest(Report rep) {
		int id=0;
		try
		{
			call_stmt = con.prepareCall("{call addMonetaryRequest(?,?,?,?,?,?,?)}");
			call_stmt.setString(1,rep.message);
			call_stmt.setString(2,rep.status);
			call_stmt.setInt(3,rep.requester.accID);
			call_stmt.setInt(4,rep.reported.accID);
			call_stmt.setInt(5,rep.admin.accID);
			call_stmt.setString(6,rep.response);
			call_stmt.registerOutParameter(7, Types.INTEGER);
			call_stmt.execute();
			id = call_stmt.getInt(7);

		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		finally {
			call_stmt=null;
		}
		return id;
	}


	public Request getRequest(int id)
	{
		return null;
	}


	public List<Request> getAllRequests(Account acc) {
		List<Request> requests = null;
		try
		{
			call_stmt = con.prepareCall("{call getallRequests(?)}");
			call_stmt.setInt(1,acc.accID);
			call_stmt.execute();
			ResultSet rs = call_stmt.getResultSet();
			while (rs.next())
			{
				if (requests==null)
					requests=new ArrayList<Request>();

				Request r = new Request();
				r.id=rs.getInt("reqId");
				r.message=rs.getString("_message");
				r.status=rs.getString("_status");
				r.requester=(UserAccount) acc;
				requests.add(r);
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		finally {
			call_stmt=null;
		}
		return requests;
	}

	public List<MonetaryReq> getAllMonRequests(Account acc) {
		List<MonetaryReq> requests = null;
		try
		{
			call_stmt = con.prepareCall("{call getallMonReq(?)}");
			call_stmt.setInt(1,acc.accID);
			call_stmt.execute();
			ResultSet rs = call_stmt.getResultSet();

			while (rs.next())
			{
				if(requests == null)
					requests = new ArrayList<MonetaryReq>();

				MonetaryReq req = new MonetaryReq();
				req.id = rs.getInt("reqId");
				req.message = rs.getString("_message");
				req.status = rs.getString("_status");
				req.amount = rs.getDouble("amount");

				if(acc.accID == rs.getInt("requester"))
				{
					req.requester = (UserAccount) acc;
					int requesteeId = rs.getInt("requestee");
					PayPal pp = PayPal.getInstance();
					for(Account x : pp.accounts)
					{
						if(x.accID == requesteeId) {
							req.requestee = (UserAccount) x;
							break;
						}
					}
				}

				else
				{
					req.requestee = (UserAccount)acc;
					int requesterId = rs.getInt("requester");
					PayPal pp = PayPal.getInstance();
					for(Account x : pp.accounts)
					{
						if(x.accID==requesterId)
						{
							req.requester=(UserAccount) x;
							break;
						}
					}
				}

				requests.add(req);
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		finally {
			call_stmt=null;
		}
		return requests;
	}

	public List<Report> getAllRepRequests(Account acc) {
		List<Report> reports = null;

		try
		{
			call_stmt = con.prepareCall("{call getAllReports(?)}");
			call_stmt.setInt(1,acc.accID);
			call_stmt.execute();
			ResultSet rs = call_stmt.getResultSet();

			while(rs.next())
			{
				if(reports == null)
					reports = new ArrayList<Report>();

				Report rep = new Report();
				rep.id = rs.getInt("reqId");
				rep.message = rs.getString("_message");
				rep.status = rs.getString("_status");
				rep.response = rs.getString("response");
				rep.requester = (UserAccount) acc;
				int reported = rs.getInt("reportedAct");
				int adminId = rs.getInt("adminAct");

				PayPal pp = PayPal.getInstance();

				for(AdminAccount x : pp.admins)
				{
					if(x.accID == adminId)
					{
						rep.admin = x;
						break;
					}
				}

				for(UserAccount x : pp.accounts)
				{
					if(x.accID == reported)
					{
						rep.reported=x;
						break;
					}
				}

				reports.add(rep);
			}

		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		finally {
			call_stmt=null;
		}
		return reports;

	}

	public void deleteRequest(int id)
	{
		try
		{
			call_stmt = con.prepareCall("{call deleteRequest(?)}");
			call_stmt.setInt(1, id);
			call_stmt.execute();
		}
		catch(Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		finally
		{
			call_stmt=null;
		}
	}


	public int storeInvoice(Invoice in)
	{
		int id=0;
		try
		{
			call_stmt = con.prepareCall("{call createInvoice(?)}");
			call_stmt.setDouble(1, in.total);
			call_stmt.setDate(2, (java.sql.Date)in.dateGenerated);
			call_stmt.setString(3, in.receipt);
			call_stmt.registerOutParameter(4, Types.INTEGER);
			call_stmt.execute();
			id = call_stmt.getInt(4);
		}
		catch(Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		finally
		{
			call_stmt=null;
		}
		return id;
	}




	// public List<Invoice> getAllInvoice(Account acc) {
	// 	List<Invoice> invoices=null;
	// 	try
	// 	{
	// 		call_stmt = con.prepareCall("{call getAllInvoice(?)}");
	// 		call_stmt.setInt(1, acc.accID);

	// 	}
	// 	catch (Exception e)
	// 	{
	// 		System.out.println(e);
	// 		e.printStackTrace();
	// 	}
	// 	finally {
	// 		call_stmt=null;
	// 	}
	// 	return invoices;
	// }

	public Invoice getInvoice(int id)
	{
		Invoice inv = null;
		try {
			CallableStatement ST = con.prepareCall("{call getInvById(?)}");
			ST.setInt(1,id);
			ST.execute();
			ResultSet r2 = ST.getResultSet();
			if(r2.next())
			{
				inv = new Invoice();
				inv.id = r2.getInt("invoiceId");
				inv.total = r2.getDouble("bill");
				inv.dateGenerated = r2.getDate("dateGen");
				inv.receipt = r2.getString("receipt");
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		return inv;
	}

	public void deleteInvoice(int id) {
		try
		{
			call_stmt = con.prepareCall("{ call deleteInvoice(?)}");
			call_stmt.setInt(1,id);
			call_stmt.execute();
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		finally
		{
			call_stmt=null;
		}

	}



	public int addShipment(int actId, Shipment shipment) {
		try
		{
			call_stmt = con.prepareCall("{ call addShipment(?,?,?,?,?,?,?,?)}");
			call_stmt.setInt(1,actId);
			call_stmt.setString(2,shipment.address);
			call_stmt.setString(3,shipment.status);
			call_stmt.setDate(4,(java.sql.Date)shipment.dispatchDate);
			call_stmt.setDate(5,(java.sql.Date)shipment.arrivalDate);
			call_stmt.setString(6,shipment.details);
			call_stmt.setDouble(7,shipment.fee);
			call_stmt.registerOutParameter(8, Types.INTEGER);
			call_stmt.execute();
			shipment.id = call_stmt.getInt(8);

		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}

		return shipment.id;
	}

	public Shipment getShipment(int id)
	{
		Shipment S = null;
		try {
			CallableStatement ST = con.prepareCall("{call getShipmentById(?)}");
			ST.setInt(1,id);
			ST.execute();
			ResultSet rs = ST.getResultSet();
			if(rs.next())
			{
				S = new Shipment();
				S.id = rs.getInt("shipmentId");
				S.address = rs.getString("_address");
				S.status = rs.getString("_status");
				S.dispatchDate = rs.getDate("dispatchDate");
				S.arrivalDate = rs.getDate("arrivalDate");
				S.details = rs.getString("details");
				S.fee = rs.getDouble("fee");
			}
		}
		catch (Exception E)
		{
			System.out.println(E);
			E.printStackTrace();
		}

		return S;
	}

	public void deleteShipment(int id) {
		try
		{
			call_stmt = con.prepareCall("{call removeShipment(?)}");
			call_stmt.setInt(1,id);
			call_stmt.execute();
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		finally {
			call_stmt = null;
		}

	}


	public int storeCard(CreditCard card)
	{
		int cardId=0;
		try
		{
			call_stmt = con.prepareCall("{call addCreditCard(?,?,?,?,?)}");
			call_stmt.setDate(1, (java.sql.Date) card.expirationDate);
			call_stmt.setInt(2,card.cvv);
			call_stmt.setString(3,card.cardNum);
			call_stmt.setDouble(4,card.balance);
			call_stmt.registerOutParameter(5, Types.BIGINT);
			call_stmt.execute();
			cardId = call_stmt.getInt(5);
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		finally {
			call_stmt=null;
		}
		return cardId;
	}


	public List<CreditCard> getAllCards(UserAccount acc) {
		int id = acc.accID;
		List<CreditCard> cards = null;
		try
		{
			call_stmt = con.prepareCall("{call getAllCards(?)}");
			call_stmt.setInt(1,id);
			call_stmt.execute();
			ResultSet rs = call_stmt.getResultSet();

			while (rs.next())
			{
				if(cards==null)
					cards = new ArrayList<CreditCard>();
				CreditCard card = new CreditCard(rs.getDate("expirationDate"),rs.getInt("cvv"),rs.getString("cardNum"),rs.getDouble("balance"));
				cards.add(card);
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		finally {
			call_stmt=null;
		}
		return cards;
	}


	public void removeCard(CreditCard card)
	{
		try
		{
			call_stmt =  con.prepareCall("{call removeCard(?,?)}");
			call_stmt.setString(1,card.cardNum);
			call_stmt.setInt(2,card.cvv);
			call_stmt.execute();
		}
		catch (Exception E)
		{
			System.out.println(E);
			E.printStackTrace();
		}
		finally {
			call_stmt=null;
		}
	}

	public boolean updateCard(CreditCard card)
	{
		int ret=0;
		try
		{
			call_stmt = con.prepareCall("{call updateCreditCard(?,?,?,?,?)}");
			call_stmt.setDate(1,(java.sql.Date) card.expirationDate);
			call_stmt.setInt(2,card.cvv);
			call_stmt.setString(3,card.cardNum);
			call_stmt.setDouble(4,card.balance);
			call_stmt.registerOutParameter(5,Types.INTEGER);
			call_stmt.execute();
			ret=call_stmt.getInt(5);

		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		finally {
			call_stmt=null;
		}
		return ret>0;
	}

	public boolean updateBalance(UserAccount u1, UserAccount u2)
	{
		CreditCard c1 = u1.cards.get(0);
		CreditCard c2 = u2.cards.get(0);

		if(updateCard(c1) && updateCard(c2))
			return true;
		else
			return false;
	}
}