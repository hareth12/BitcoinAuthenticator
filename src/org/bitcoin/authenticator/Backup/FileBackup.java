package org.bitcoin.authenticator.Backup;

import java.io.File;
import java.io.FileOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.bitcoin.authenticator.Backup.Exceptions.CannotBackupToFileException;
import org.bitcoin.authenticator.utils.CryptoUtils;

import android.os.Environment;

public class FileBackup {
	static public String FILE_BACKUP_NAME = "Bitcoin_Authenticator_Seed_Backup";
	
	public static void backupToFile(String mnemonic, String passwordStr) throws CannotBackupToFileException {
		try {
			File file = new File(getBackupFileAbsolutePath());
	    	FileOutputStream f = new FileOutputStream(file);
			f.write(prepareBackupFileContent(mnemonic, passwordStr));
			f.close();
		}
		catch (Exception e) {
			throw new CannotBackupToFileException("Failed to backup wallet seed to file");
		}		
	}
	
	public static byte[] prepareBackupFileContent(String mnemonic, String passwordStr) {
		SecretKey sk = CryptoUtils.deriveSecretKeyFromPasswordString(passwordStr);
    	byte[] cipherBytes = CryptoUtils.encryptPayload(sk, mnemonic.getBytes());
    	return cipherBytes;
	}
	
	/**
	 * also makes the dir if not existing
	 * @return
	 */
	public static String getBackupFileFolderPath() {
		File storage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		File dir = new File (storage.getAbsolutePath() + "/backups/");
		if(!dir.exists())
			dir.mkdir();
		return dir.getAbsolutePath();
	}
	
	public static String getBackupFileAbsolutePath() {
		File dir = new File(getBackupFileFolderPath());
		return new File(dir, FILE_BACKUP_NAME).getAbsolutePath();
	}
}
