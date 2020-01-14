package cn.edu.buaa.crypto.encryption.ibe.bf01b.test;

import java.io.IOException;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeySerParameter;
import cn.edu.buaa.crypto.encryption.ibe.bf01b.IBEBF01bEngine;
import cn.edu.buaa.crypto.utils.TestUtils;


public class IBEKeyGen {
	private static IBEBF01bEngine engine;
    
	public static void IBE01bKeyGen(String PKpath, String MKpath, String id, String SKpath){
		engine = IBEBF01bEngine.getInstance();
		
		try {	
			PairingKeySerParameter publicKey = TestUtils.deSerializationKey(IBEAddress.keyPairAddress + PKpath);
		    PairingKeySerParameter masterKey = TestUtils.deSerializationKey( IBEAddress.keyPairAddress + MKpath);

		    PairingKeySerParameter secretKey = engine.keyGen(publicKey, masterKey, id);
		    TestUtils.serialization(secretKey, IBEAddress.secretKeyAddress + SKpath);
			
			System.out.println("KenGen sucessful!");
			
		 } catch (IOException e) {
	            System.out.println("KenGen test failed.");
	            e.printStackTrace();
	            System.exit(1);
	     }catch(ClassNotFoundException e) {
	            e.printStackTrace();
	            System.exit(1);
	     }
	} 
}
