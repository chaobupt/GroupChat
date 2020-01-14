package cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.test;

import java.io.IOException;
import cn.edu.buaa.crypto.access.lsss.lw10.LSSSLW10Engine;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeySerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.CPABEMHOOEngine;
import cn.edu.buaa.crypto.utils.TestUtils;


public class MHOOKeyGen {
private static CPABEMHOOEngine engine;	
	public static void CPABEMHOOKeyGen(String PKpath, String MKpath, String[] userAttributes, String SKpath) {
		engine = CPABEMHOOEngine.getInstance();
		engine.setAccessControlEngine(LSSSLW10Engine.getInstance());
		
		try {	
			PairingKeySerParameter publicKey = TestUtils.deSerializationKey(CPABEMHOOAddress.keyPairAddress + PKpath);
		    PairingKeySerParameter masterKey = TestUtils.deSerializationKey(CPABEMHOOAddress.keyPairAddress + MKpath);

			PairingKeySerParameter secretKey = engine.keyGen(publicKey, masterKey, userAttributes);
		    TestUtils.serialization(secretKey, CPABEMHOOAddress.secretKeyAddress + SKpath);

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
