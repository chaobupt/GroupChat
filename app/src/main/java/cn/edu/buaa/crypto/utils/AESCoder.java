package cn.edu.buaa.crypto.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESCoder {

	public static void encryptFile(byte[] seed, String plaintextPath, String ciphertextPath)
			throws Exception {
		byte[] byteArrayFile = readFile(plaintextPath);
		byte[] result = encrypt(seed,  byteArrayFile);
		writeFile(ciphertextPath,result);
	}

	public static void decryptFile(byte[] seed, String ciphertextPath, String plaintextPath)
			throws Exception {
		byte[] byteArrayFile = readFile(ciphertextPath);
		byte[] result = decrypt(seed,byteArrayFile);
		writeFile(plaintextPath,result);
	}

	public static byte[] encrypt(byte[] seed, byte[] plaintext)
			throws Exception {
		byte[] raw = InsecureSHA1PRNGKeyDerivator.deriveInsecureKey(seed, 16);
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(plaintext);
		return encrypted;
	}

	public static byte[] decrypt(byte[] seed, byte[] ciphertext)
			throws Exception {
		byte[] raw = InsecureSHA1PRNGKeyDerivator.deriveInsecureKey(seed, 16);
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] decrypted = cipher.doFinal(ciphertext);

		return decrypted;
	}


	/* read byte[] from inputfile */
	public static byte[] readFile(String inputfile) throws IOException {
		InputStream is = new FileInputStream(inputfile);
		int size = is.available();
		byte[] content = new byte[size];

		is.read(content);
		is.close();
		return content;
	}

	/* write byte[] into outputfile */
	public static void writeFile(String outputfile, byte[] b) throws IOException {
		OutputStream os = new FileOutputStream(outputfile);
		os.write(b);
		os.flush();
		os.close();
	}





}