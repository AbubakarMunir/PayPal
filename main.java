/*
 * To change this license header, choose1 License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PayPalProject;

/**
 *
 * @author hp
 */
public class main {
    public static void main(String[] args) {
       PayPal obj=PayPal.getInstance();
       obj.signUp("abc@gmail.com","Jake","123456","04235341332","walton street");
       UserAccount ua1=new UserAccount("abc@gmail.com","Jake","123456","04235341332","walton street");
       UserAccount ua2=new UserAccount("abcd@gmail.com","Joe","123456","04235341332","walton street");
       AdminAccount aa1=new AdminAccount("abcd@gmail.com","Harry","123456","04235415545","closer");
       AdminAccount aa2=new AdminAccount("abcd@gmail.com","HEnry","123456","04235415545","closer");
       obj.admins.add(aa1);
       obj.accounts.add(ua1);
       
       ua1.sendRequest("Please close my account asap");
       aa1.printInBoundRequests();
       aa1.handleCloseAccRequest(aa1.requests.get(0));
       aa1.printInBoundRequests();
       
      
    }
    
}
