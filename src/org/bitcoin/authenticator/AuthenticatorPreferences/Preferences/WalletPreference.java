package org.bitcoin.authenticator.AuthenticatorPreferences.Preferences;

import org.bitcoin.authenticator.AuthenticatorPreferences.BAPreferenceBase;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.SharedPreferences;

public class WalletPreference extends BAPreferenceBase{
	
	
	public WalletPreference(Activity activity){
		setActivity(activity);
		setPrefix("WalletData");
	}
	
	/**
	 * Wallet
	 */
	
	public void setWallet(String 
			walletID, 
			String id,
			String fingerprint, 
			String type, 
			String extIP, 
			String locIP, 
			int networkType,
			boolean deleted){
		setID(walletID, id);
		setFingerprint(walletID, fingerprint);
		setType(walletID, type);
		setExternalIP(walletID, extIP);
		setLocalIP(walletID, locIP);
		setNetworkType(walletID, networkType);
		setDeleted(walletID, deleted);
	}
	
	
	/**
	 * ID
	 */
	
	public void setID(String walletID,String value){
		SharedPreferences.Editor editor = getEditor(getPrefix() + walletID);	
		editor.putString(BAPreferenceType.ID.toString(), value);
		editor.commit();
	}
	
	public String getID(String walletID, String defValue){
		SharedPreferences data = getActivity().getSharedPreferences(getPrefix() + walletID, 0);
		return data.getString(BAPreferenceType.ID.toString(), defValue);
	}
	
	/**
	 * Deleted
	 */
		
	public void setDeleted(String walletID,boolean value){
		SharedPreferences.Editor editor = getEditor(getPrefix() + walletID);
		editor.putBoolean(BAPreferenceType.DELETED.toString(), value);
		editor.commit();
	}

	public boolean getDeleted(String walletID, boolean defValue){
		SharedPreferences data = getActivity().getSharedPreferences(getPrefix() + walletID, 0);
		return data.getBoolean(BAPreferenceType.DELETED.toString(), defValue);
	}
	
	/**
	 * Network Type
	 */
	
	public void setNetworkType(String walletID,int value){
		SharedPreferences.Editor editor = getEditor(getPrefix() + walletID);
		editor.putInt(BAPreferenceType.NETWORK.toString(), value);
		editor.commit();
	}

	public int getNetworkType(String walletID, int defValue){
		SharedPreferences data = getActivity().getSharedPreferences(getPrefix() + walletID, 0);
		return data.getInt(BAPreferenceType.NETWORK.toString(), defValue);
	}
	
	/**
	 * Fingerprint
	 */
	
	public void setFingerprint(String walletID,String value){
		SharedPreferences.Editor editor = getEditor(getPrefix() + walletID);
		editor.putString(BAPreferenceType.FINGERPRINT.toString(), value);
		editor.commit();
	}

	public String getFingerprint(String walletID, String defValue){
		SharedPreferences data = getActivity().getSharedPreferences(getPrefix() + walletID, 0);
		return data.getString(BAPreferenceType.FINGERPRINT.toString(), defValue);
	}
	
	/**
	 * Type
	 */
	
	public void setType(String walletID,String value){
		SharedPreferences.Editor editor = getEditor(getPrefix() + walletID);
		editor.putString(BAPreferenceType.TYPE.toString(), value);
		editor.commit();
	}

	public String getType(String walletID, String defValue){
		SharedPreferences data = getActivity().getSharedPreferences(getPrefix() + walletID, 0);
		return data.getString(BAPreferenceType.TYPE.toString(), defValue);
	}
	
	/**
	 * External IP
	 */
	
	public void setExternalIP(String walletID,String value){
		SharedPreferences.Editor editor = getEditor(getPrefix() + walletID);
		editor.putString(BAPreferenceType.EXTERNAL_IP.toString(), value);
		editor.commit();
	}

	public String getExternalIP(String walletID, String defValue){
		SharedPreferences data = getActivity().getSharedPreferences(getPrefix() + walletID, 0);
		return data.getString(BAPreferenceType.EXTERNAL_IP.toString(), defValue);
	}
	
	/**
	 * Local IP
	 */
	
	public void setLocalIP(String walletID,String value){
		SharedPreferences.Editor editor = getEditor(getPrefix() + walletID);
		editor.putString(BAPreferenceType.LOCAL_IP.toString(), value);
		editor.commit();
	}

	public String getLocalIP(String walletID, String defValue){
		SharedPreferences data = getActivity().getSharedPreferences(getPrefix() + walletID, 0);
		return data.getString(BAPreferenceType.LOCAL_IP.toString(), defValue);
	}
	
	/**
	 * Enum for the various keys in the wallet preference
	 * @author alon
	 *
	 */
	private enum BAPreferenceType {
		ID					("ID"			),
		DELETED				("Deleted"		),
		NETWORK				("NetworkType"	),
		FINGERPRINT			("Fingerprint"	),
		TYPE				("Type"			),
		EXTERNAL_IP			("ExternalIP"	),
		LOCAL_IP			("LocalIP"		);
		
		private String name;       

	    private BAPreferenceType(String s) {
	        name = s;
	    }

	    public boolean equalsName(String otherName){
	        return (otherName == null)? false:name.equals(otherName);
	    }

	    public String toString(){
	       return name;
	    }
	}
}