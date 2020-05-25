package  PayPalProject;
import java.util.*;

public class BusinessAcc extends UserAccount {

	public BusinessAcc() {
	}

	public BusinessAcc(String email,String name, String bName, String pass,String phone,String address) {
		super(email,name,pass,phone,address);
		this.businessName = new String(bName);
	}

	public List<BusinessTransaction> transactions;
	public String businessName;

	public void printBusinessTransactions() {
		System.out.println("Your Business Transactions History:");
		for(BusinessTransaction bt : transactions)
			System.out.println(bt);

	}

	@Override
	public Boolean updateInfo() {

		//option to the user
		System.out.print("What information do you wish to update? \n");
		System.out.print("Press 1 : Email \n" + "Press 2 : User Name \n" + "Press 3 : Phone Number \n" + "Press 4 : Address \n" + "Press 5 : Bussiness Name \n");

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
		else if(choice == 5)
		{
			System.out.print("Enter new Business Name please: ");
			String newname = input.next();

			//checks for the validity of the new  bussiness name
			//if an error occurs the data will not be changed

			if(newname.isEmpty()==true)
			{
				System.out.print("Error!");
				return false;
			}
			businessName=newname;
			System.out.print("Your infomation has been updated!");
			return true;
		}
		System.out.print("Error! Please input a valid number for action");
		return false;
	}

	@Override
	public String toString() {
		return "BusinessAcc{" +
				"businessName='" + businessName + '\'' +
				", address='" + address + '\'' +
				", status='" + status + '\'' +
				", email='" + email + '\'' +
				", name='" + name + '\'' +
				", phoneNum='" + phoneNum + '\'' +
				'}';
	}
}