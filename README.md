# A Simple Client - Server Implementation For a Banking Application

##Key generation:
The classes (Client) and (Server) are both created to implement the applications for the banking system. Initially the client and the server generate two different pair of keys (client’s public, client’s private) and (server’s public, server’s private) by calling the function key_gen() which uses RSA cryptography method. Since the server’s public key has been made available publicly, the client uses this key to send its public key to the server.

##Sending a signed balance request:
The client wants to know the current balance of a particular account number via a request. To send a request to the server, the client must sign it first. To do that, the client hashes the request by calling the function Hash(request), and encrypts it by calling the function enc() that encrypts the hash using the client’s private key. Then appends the signature to the original message (request + signature) and sends it to the server. 
The server’s verification and respond:
After the server receives the signed request, decrypts the signature using the client’s public key and by calling the function dec() to retrieve the hash. Then the server hashes the original message and compares these two hashes to verify that the request has not been altered. If they were equal it means every thing is okay. 
After the verification is done successfully, the server retrieves the corresponding balance by searching in its database [I have created a small database for the server that has information of only five accounts]. The server encrypts the result using its private key by calling the function enc(), then sends it to the client.

##The client’s verification:
The client wants to verify that the server sends that message. To accomplish the verification, the client must decrypt the respond using the server’s public key by calling the function dec(). If the decryption done successfully it means that the sender was the server.

##Security limitations:
The main security issue is the trust. How could both sides trust each other’s public key? I think that we need a trusted third party to certify other entities. For instance a client has to be sure that the server’s public key is not compromised. Also man in the middle attack is possible. The attacker could impersonate himself as one of the parties. 

##Instructions
There are 3 classes: MyBank.java (the main class) , Client.java and Server.java
to run the program : 
1- Run the main class;
2- You have to enter the account number as an input to see the corresponding balance. 
***[ The server’s database only contains the account numbers : 100 & 200 & 300 & 400 & 500]
3 - If you enter an invalid account number, there will be a respond says that your entered account number is incorrect.

##Sample Output:

