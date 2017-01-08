/*****************************************************
Class Project

ABDULJALEEL AL-RUBAYE

Secure Data Communication and Networks
CSE 5636 - CYB 5290
Dr. M. Carvalho
Spring 2014

******************************************************

NOTE:

In this scenario the existing account numbers are [100, 200, 300, 400, 500]

You can enter whatever number you want as an account number but you'll receive the message (incorrect account #)

******************************************************/

public class MyBank {

	public static void main(String[] args) throws Exception{
		
		Client client = new Client();
		Server server = new Server();
		
		client.key_gen();
		server.key_gen();
		
		client.connect(server);
		server.connect(client);
		
		client.Request_Balance();
		
		if (server.verify())
		{
			server.respond();
			client.verify();
		}
		else
		{
			System.out.println("----------------------------------------------------------------------");
			System.out.println("The server checked the received data! It has been ALTERED!!, Try Again");
			System.out.println("----------------------------------------------------------------------");
		}
		client.terminate();
		
		
	}//end of main class	

}
