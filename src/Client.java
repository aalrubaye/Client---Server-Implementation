import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.security.*;
public class Client {
	
	private static BigInteger[] public_key;
	private static BigInteger[] private_key;
	public static String[][] data;
	private static Server my_server=null;
	private static BigInteger[] server_pub=null;
	
	public static void connect(Server s){
		my_server = s;
		server_pub = s.public_key;
		
		String my_pub_e = public_key[0].toString();
		String my_pub_n = public_key[1].toString();
		
		String[] p1 = enc(my_pub_e,server_pub);
		String[] p2 = enc(my_pub_n,server_pub);
		String[][] pp = new String[2][];
		pp[0] = p1;
		pp[1] = p2;
		s.data = pp;
		
		System.out.println("- The client sent his public key to the server");
		System.out.println();
	}

	public static void Request_Balance() throws NoSuchAlgorithmException{
		Scanner in= new Scanner(System.in);

		System.out.println("---------------------------------------------------------------------");
		System.out.println(" - The client must enter the account number, to see the balance : ");
		String message = in.nextLine();
		System.out.println("---------------------------------------------------------------------");
		System.out.println();
		System.out.println("- The client wants to see the balance of the account number ("+message+")");
		System.out.println();
		String hash = Hash(message);
		String[] signature = enc(hash,private_key);
		System.out.println("- The client creates the request, hashs it and encrypts the hash using his private key");
		System.out.println();
		String[] signed_message = new String [signature.length+1];
		signed_message[0] = message;
		for (int i=1; i<signature.length+1; i++){
			signed_message[i] = signature[i-1];
		}
		System.out.println("- The client appends the signature to the original request, and sedns it to the server");
		System.out.println();
		send_to_server(signed_message);
	}

	public static void send_to_server(String[] signed_message){
		my_server.data = new String[signed_message.length][];
		my_server.data[0] = signed_message;
	}
	
	public static void verify(){
	String[] m = new String[data.length];	
		for (int i=0; i<data.length; i++){
			m[i] = dec(data[i],private_key);
		}
		System.out.println("- The client receives a message");
		System.out.println();
		String msg = dec(m,server_pub);
		System.out.println("- The client decrypts the message using the server's public key successfully");
		System.out.println();
		System.out.println("- The receivd message from the server is : ("+msg+")");
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
	
		
		System.out.println("* The client generated two pairs of keys using RSA successfully : Public = [ "+public_key[0].toString()+" , "+public_key[1].toString()+" ] ,  Private = [ "+private_key[0].toString()+" , "+private_key[1].toString()+" ]");
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

	public static void terminate(){
		my_server = null;
		server_pub = null;
	}	
}
