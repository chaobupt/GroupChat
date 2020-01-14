package cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.test;

import android.os.Environment;

import java.io.File;

public class CPABEMHOOAddress {
	public final static String keyPairAddress = Environment.getExternalStorageDirectory() + File.separator + "GroupChat" + File.separator + "MHOO" + File.separator + "KeyPair" + File.separator;
	public final static String secretKeyAddress = Environment.getExternalStorageDirectory() + File.separator + "GroupChat" + File.separator + "MHOO" + File.separator + "SecretKey" + File.separator;
	public final static String encryptedAddress = Environment.getExternalStorageDirectory() + File.separator + "GroupChat" + File.separator + "MHOO" + File.separator + "Encrypted" + File.separator;
	public final static String ZIPAddress = Environment.getExternalStorageDirectory() + File.separator + "GroupChat" + File.separator + "MHOO" + File.separator + "Encrypted";
	public final static String rcvZIPAddress = Environment.getExternalStorageDirectory() + File.separator + "GroupChat" + File.separator + "rcvZip"+ File.separator;
	public final static String CKKSAddress = Environment.getExternalStorageDirectory() + File.separator + "GroupChat" + File.separator + "CKKS";
	public final static String basicAddress = Environment.getExternalStorageDirectory() + File.separator + "GroupChat" + File.separator;
	public final static String RegisterFaceAddress = Environment.getExternalStorageDirectory() + File.separator + "GroupChat" + File.separator + "RegisterFace" + File.separator ;
}
