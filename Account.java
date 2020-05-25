package  PayPalProject;
import java.util.*;

public abstract class Account {


	public Account() {
	}

	public Account(String e,String n,String p,String pn) {
		email= new String(e);
		name =  new String(n);
		password = new String(p);
		phoneNum = new String(pn);
	}

	public int accID;
	public String email;
	public String name;
	public String password;
	public String phoneNum;

	public abstract void addAcc(Account acc);
	public abstract void remAcc(Account acc);
	public abstract Boolean updateInfo();

	public Boolean changePassword(String old, String New) {
		//if the entered password matches the current password then the password wil be changed
		if(old.equals(password))
		{
			password = New;

			//check for the length of the password
			if(password.length()<6)
			{
				System.out.print("Too short! Try again");
				password=old;
				return false;
			}
			System.out.print("Your password has been changed!");
			return true;
		}
		else
		{
			System.out.print("Incorrect old password. Please try again.");
			return false;
		}
	}

	String getPassword()
	{
		return password;
	}

	@Override
	public abstract String toString();
}