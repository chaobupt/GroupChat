package cn.edu.buaa.crypto.encryption.ibe.bf01b.test;

import android.os.Environment;

import java.io.File;

public class IBEAddress {
	public final static String keyPairAddress = Environment.getExternalStorageDirectory() + File.separator + "GroupChat" + File.separator + "IBE" + File.separator + "KeyPair" + File.separator;
	public final static String secretKeyAddress = Environment.getExternalStorageDirectory() + File.separator + "GroupChat" + File.separator + "IBE" + File.separator + "SecretKey" + File.separator;
	public final static String rcvZIPAddress = Environment.getExternalStorageDirectory() + File.separator + "GroupChat" + File.separator + "rcvZip"+ File.separator;
}
