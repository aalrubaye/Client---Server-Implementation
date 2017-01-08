import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class Server {

	public static BigInteger[] public_key;
	private static BigInteger[] private_key;
	public static String[][] data=null;
	private static String account="null";
	private static BigInteger[] client_pub=null;
	private static Client client;
	
	public static void connect(Client c) throws NoSuchAlgorithmException {
		String[] p1,p2;
		p1 = data[0];
		p2 = data[1];
		data = null;
				
		String client_e = dec(p1,private_key);
		String client_n = dec(p2,private_key);
		
		client_pub = new BigInteger[2];
		client_pub[0] = new BigInteger(client_e);
		client_pub[1] = new BigInteger(client_n);
		client = c;
	}

	public static boolean verify() throws NoSuchAlgorithmException{
		System.out.println("- The server received a request");
		System.out.println();
		String[] rec_msg = data[0];
		String msg = rec_msg[0];
		String[] sign = new String[rec_msg.length-1];
		for (int i=1; i<rec_msg.length; i++){
			sign[i-1] = rec_msg[i]; 
		}
		data = null;
		System.out.println("- The server splits the received message into two parts: (the request) and (the signature)");
		System.out.println();
		
		String received_hashed_signature = dec(sign,client_pub);
		System.out.println("- The server decrypts the signature using the client's public key");
		System.out.println();
		String hash = Hash(msg);
		System.out.println("- The server hashs the recieved request");
		System.out.println();
		if (hash.equals(received_hashed_signature))
			{	account = msg;
			System.out.println("- The server compares the hash and the decrypted signature and verifies that the request is not altered");
			System.out.println();
				return true;   }
		else		
			{
			System.out.println("- The request has been changed during the transmission: [hashed sign = "+received_hashed_signature.getBytes()+"] <> [current hash = "+hash.getBytes()+" ]");
			return false;
			}
	}

	public static void respond(){
		String balance = data_base(account);
		String msg;
		if (!balance.equals("error"))
			{
			 msg = "The account number ("+account+") has "+balance+" .";
			 System.out.println("- The server retrieves the balance from its data base. The balance is= ("+balance+")");
			 System.out.println();
			}
		else
			{
			 msg = "The account number ("+account+") is not correct!";
			 System.out.println("- The server didn't find data for the account ("+account+")");
			 System.out.println();
			}
		
		String[] c = new String[enc(msg, private_key).length];
		c = enc(msg, private_key);
		
		client.data = new String[c.length][];
		for (int i=0; i<c.length; i++){
			client.data[i] = enc(c[i], client_pub);
		}
	
		System.out.println("- The server encryptes the respond using its priavte key and sends it to the client");
		System.out.println();
	}

	public static String[] enc(String b, BigInteger[] key){
		String[] st = new String[b.length()];
		BigInteger e,n;
		e = key[0];
		n = key[1];
		for (int i=0; i<b.length(); i++)
		{
			BigInteger m = BigInteger.valueOf((int)b.charAt(i)); 
			st[i] = m.modPow(e, n).toString();
		}
		return st;
	}

	public static String dec(String[] c, BigInteger[] key){
		String m="";
		BigInteger d,n;
		d = key[0];
		n = key[1];
		for (int i=0; i<c.length; i++){
			BigInteger cc = new BigInteger(c[i]);
			m += (char) cc.modPow(d, n).intValue();
		}
		return m;
	}

	public static String Hash(String st) throws NoSuchAlgorithmException {
	   byte[] h = null;
	   try {
	       MessageDigest msgd = MessageDigest.getInstance("SHA-256");
	       h = msgd.digest(st.getBytes());
	   } 
	   catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
	   StringBuilder stb = new StringBuilder();
	   for (int i = 0; i < h.length; ++i) {
	       String hx = Integer.toHexString(h[i]);
	       if (hx.length() == 1) {
	           stb.append(0);
	           stb.append(hx.charAt(hx.length() - 1));
	       } else {
	           stb.append(hx.substring(hx.length() - 2));
	       }
	   }
	   return stb.toString();
	}

	public static void key_gen(){
		
		BigInteger p,q,n,teta,e,d,one;
		
		public_key = new BigInteger[2];
		private_key = new BigInteger[2];
		
		one = BigInteger.valueOf(1);
		p = gen_random_prime();
		q = gen_random_prime();
		
		n = p.multiply(q); // n=p*q
		teta = (p.subtract(one)).multiply(q.subtract(one)); //teta = (p-1)*(q-1)
		
		e = find_e(teta);
		d = find_d(e,teta);
		
		public_key[0] = e;		public_key[1] = n;
		private_key[0] = d;		private_key[1] = n;
		
		System.out.println("* The server generated two pairs of keys using RSA successfully : Public = [ "+public_key[0].toString()+" , "+public_key[1].toString()+" ] ,  Private = [ "+private_key[0].toString()+" , "+private_key[1].toString()+" ]");
		System.out.println();

	}
	
	public static BigInteger gen_random_prime(){
		BigInteger B;
		int rand_num,min_val,max_val;
		min_val = 100;
		max_val = 1500;
		B= BigInteger.valueOf(min_val);
		while (!B.isProbablePrime(1)){
			rand_num = min_val + (int)(Math.random()*max_val);
			B= BigInteger.valueOf(rand_num);
		}
		return B;
	}

	public static BigInteger find_e(BigInteger teta){
		BigInteger E,gcd,one;
		one = BigInteger.valueOf(1);
		E = BigInteger.valueOf(2);
		gcd = teta.gcd(E);
		while (!gcd.equals(one)){
			E = E.add(one);
			gcd = teta.gcd(E);
		}
		return E;
	}

	public static BigInteger find_d(BigInteger e, BigInteger teta){
		return e.modInverse(teta);
	}
	
	public static String data_base(String account){
		if (account.equals("100")){
			return "100,000$";
		}else if(account.equals("200")){
			return "200,000$";
		}else if(account.equals("300")){
			return "300,000$";
		}else if(account.equals("400")){
			return "400,000$";
		}else if(account.equals("500")){
			return "500,000$";
		}else return "error";
	}
	
}
