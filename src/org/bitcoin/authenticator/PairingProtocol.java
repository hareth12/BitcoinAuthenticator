package org.bitcoin.authenticator;



import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.*;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.*;

import org.bitcoin.authenticator.Connection.CannotConnectToWalletException;
import org.bitcoin.authenticator.GcmUtil.GcmUtilGlobal;
import org.bitcoin.authenticator.utils.EncodingUtils;
import org.json.simple.JSONValue;

import android.util.Log;

import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import com.google.common.primitives.Ints;
 
/**
 *	Opens a TCP socket connection to the wallet, derives a new master public key, encrypts it
 *  and sends it over to the wallet.
 */
public class PairingProtocol {
 
    private String[] ips;
    
    /**	Constructor creates a new connection object to connect to the wallet. */
    public PairingProtocol(String[] ips) {
    	this.ips = ips;
    }
    
    /**	
     * Takes in Authenticator seed and uses it to derive the master public key and chaincode.
     * Uses the AES key to calculate the message authentication code for the payload and concatenates 
     * it with the master public key and chaincode. The payload is encrypted and sent over to the wallet.
     * @throws CouldNotPairToWalletException 
     */
    @SuppressWarnings({ "unchecked", "rawtypes", "static-access" })
	public void run(byte[] seed, SecretKey AESKey, String pairingID, byte[] regID, long walletIndex) throws CouldNotPairToWalletException {
    	try {
    		//Derive the key and chaincode from the seed.
    		Log.i("asdf", "Pairing: Deriving Wallet Account");
        	HDKeyDerivation HDKey = null;
        	DeterministicKey masterkey = HDKey.createMasterPrivateKey(seed);
        	DeterministicKey childkey = HDKey.deriveChildKey(masterkey,Ints.checkedCast(walletIndex));
        	byte[] chaincode = childkey.getChainCode(); // 32 bytes
        	byte[] mpubkey = childkey.getPubKey(); // 32 bytes
        	
        	//Format data into a JSON object
        	Log.i("asdf", "Pairing: creating payload");
        	Map obj=new LinkedHashMap();
        	obj.put("version", 1);
    		obj.put("mpubkey", Utils.bytesToHex(mpubkey));
    		obj.put("chaincode", Utils.bytesToHex(chaincode));
    		obj.put("pairID", pairingID);
    		obj.put("gcmID", new String (regID));
    		StringWriter jsonOut = new StringWriter();
    		try {JSONValue.writeJSONString(obj, jsonOut);} 
    		catch (IOException e1) {e1.printStackTrace();}
    		String jsonText = jsonOut.toString();
    		byte[] jsonBytes = jsonText.getBytes();
    		
        	//Calculate the HMAC
    		Log.i("asdf", "Pairing: calculating checksum");
        	Mac mac = Mac.getInstance("HmacSHA256");
        	mac.init(AESKey);
        	byte[] macbytes = mac.doFinal(jsonBytes);
        	
        	//Concatenate with the JSON object
        	ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        	outputStream.write(jsonBytes);
        	outputStream.write(macbytes);
        	byte payload[] = outputStream.toByteArray();
        	
        	//Encrypt the payload
        	Log.i("asdf", "Pairing: encrypting payload");
        	Cipher cipher = null;
        	try {cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");} 
        	catch (NoSuchAlgorithmException e) {e.printStackTrace();} 
        	catch (NoSuchPaddingException e) {e.printStackTrace();}
        	try {cipher.init(Cipher.ENCRYPT_MODE, AESKey);} 
        	catch (InvalidKeyException e) {e.printStackTrace();}
        	byte[] cipherBytes = null;
        	try {cipherBytes = cipher.doFinal(payload);} 
        	catch (IllegalBlockSizeException e) {e.printStackTrace();} 
        	catch (BadPaddingException e) {e.printStackTrace();}
        	
        	//Send the payload over to the wallet
        	Connection.getInstance().writeAndClose(ips, cipherBytes);
    	}
    	catch (Exception e) {
    		throw new CouldNotPairToWalletException("Could not pair to wallet");
    	}
    	
	  }
    
    public static String getPairingIDDigest(long num, String gcmRegID)
	 {
		MessageDigest md = null;
		try {md = MessageDigest.getInstance("SHA-1");}
		catch(NoSuchAlgorithmException e) {e.printStackTrace();} 
	    byte[] digest = md.digest((gcmRegID + "_" + Long.toString(num)).getBytes());
	    String ret = new BigInteger(1, digest).toString(16);
	    //Make sure it is 40 chars, if less pad with 0, if more substringit
	    if(ret.length() > 40)
	    {
	    	ret = ret.substring(0, 39);
	    }
	    else if(ret.length() < 40)
	    {
	    	int paddingNeeded = 40 - ret.length();
	    	String padding = "";
	    	for(int i=0;i<paddingNeeded;i++)
	    		padding = padding + "0";
	    	ret = padding + ret;
	    }
	    //Log.v("ASDF","Reg id: " + ret);
	    return ret;
	}
    
    public static class PairingQRData {
    	public String AESKey;
    	public String IPAddress;
    	public String LocalIP;
    	public String walletType;
    	public String fingerprint;
    	public int networkType;
    	public long walletIndex;
    }
    
    public static PairingQRData parseQRString(String QRInput) {
    	PairingQRData ret = new PairingQRData();
    	
    	ret.AESKey = QRInput.substring(QRInput.indexOf("AESKey=")+7, QRInput.indexOf("&PublicIP="));
    	ret.IPAddress = QRInput.substring(QRInput.indexOf("&PublicIP=")+10, QRInput.indexOf("&LocalIP="));
    	ret.LocalIP = QRInput.substring(QRInput.indexOf("&LocalIP=")+9, QRInput.indexOf("&WalletType="));
    	ret.walletType = QRInput.substring(QRInput.indexOf("&WalletType=")+12, QRInput.indexOf("&NetworkType="));
		/**
		 * 1 for main net, 0 for testnet
		 */
    	ret.networkType = Integer.parseInt(QRInput.substring(QRInput.indexOf("&NetworkType=")+13, QRInput.indexOf("&index=")));
		
		/**
		 * get index
		 */
		String walletIndexHex = QRInput.substring(QRInput.indexOf("&index=")+7, QRInput.length());
		ret.walletIndex = new BigInteger(EncodingUtils.hexStringToByteArray(walletIndexHex)).longValue();
    	
    	return ret;
    }
    
    static public class CouldNotPairToWalletException extends Exception {
		public CouldNotPairToWalletException(String str) {
			super(str);
		}
	}
	
}