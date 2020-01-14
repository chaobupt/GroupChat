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


public class MHOOUKeyGen {
private static CPABEMHOOEngine engine;	
	public static void CPABEMHOOUKeyGen(String PKpath, String access_policy_T00, Element s) {
		engine = CPABEMHOOEngine.getInstance();
		engine.setAccessControlEngine(LSSSLW10Engine.getInstance());

		try {	
			PairingKeySerParameter publicKey = TestUtils.deSerializationKey(CPABEMHOOAddress.keyPairAddress + PKpath);
	 		String comparableAttributeMaxValue = "scl:10 ts:1569809439999 te:1569809439999";
	 		Map<String, Integer> binaryLength =PolicyUtil.getComparableAttributeBinaryLength(comparableAttributeMaxValue);
	 		access_policy_T00 = PolicyUtil.policyReplace(access_policy_T00, binaryLength); 	 
			
	        int[][] accessPolicyT00 = ParserUtils.GenerateAccessPolicy(access_policy_T00);
	        String[] rhosT00 = ParserUtils.GenerateRhos(access_policy_T00);   
			
	        PairingCipherSerParameter UKeyGen = engine.UKeyGen(publicKey, accessPolicyT00, rhosT00, s);
	        TestUtils.serialization(UKeyGen, CPABEMHOOAddress.encryptedAddress + "MHOO_UK.txt");

			System.out.println("UKenGen sucessful!");
			
		 } catch (IOException e) {
	            System.out.println("UKenGen test failed.");
	            e.printStackTrace();
	            System.exit(1);
	     }catch(ClassNotFoundException e) {
	            e.printStackTrace();
	            System.exit(1);
	     } catch (PolicySyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
}
