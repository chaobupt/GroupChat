package cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.test;

import java.io.IOException;
import java.util.Map;

import org.bouncycastle.crypto.CipherParameters;
import org.junit.Assert;

import cn.edu.buaa.crypto.access.lsss.lw10.LSSSLW10Engine;
import cn.edu.buaa.crypto.access.parser.ParserUtils;
import cn.edu.buaa.crypto.access.parser.PolicySyntaxException;
import cn.edu.buaa.crypto.algebra.serparams.PairingCipherSerParameter;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeySerPair;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeySerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.CPABEMHOOEngine;
import cn.edu.buaa.crypto.utils.PolicyUtil;
import cn.edu.buaa.crypto.utils.TestUtils;
import it.unisa.dia.gas.jpbc.Element;


public class MHOOCTUpdate {
private static CPABEMHOOEngine engine;	
	public static void CPABEMHOOCTUpdate(String PKpath, String ct0Path, String ukPath) {
		engine = CPABEMHOOEngine.getInstance();
		engine.setAccessControlEngine(LSSSLW10Engine.getInstance());
		try {	
			PairingKeySerParameter publicKey = TestUtils.deSerializationKey(CPABEMHOOAddress.keyPairAddress + PKpath);
			PairingCipherSerParameter ct0 = (PairingCipherSerParameter)TestUtils.deSerializationCipher(ct0Path);
			PairingCipherSerParameter uk =  (PairingCipherSerParameter)TestUtils.deSerializationCipher(ukPath);
			PairingCipherSerParameter ctv0 = engine.CTUpdate(publicKey, ct0, uk);
	        TestUtils.serialization(ctv0, "ctv0.txt");
			System.out.println("CTUpdate sucessful!");
		 } catch (IOException e) {
	            System.out.println("CTUpdate test failed.");
	            e.printStackTrace();
	            System.exit(1);
	     }catch(ClassNotFoundException e) {
	            e.printStackTrace();
	            System.exit(1);
	     }
	} 
}
