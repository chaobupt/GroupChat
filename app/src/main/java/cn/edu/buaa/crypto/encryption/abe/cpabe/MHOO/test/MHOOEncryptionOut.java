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
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOCiphertextInSerParameter;
import cn.edu.buaa.crypto.utils.AESCoder;
import cn.edu.buaa.crypto.utils.PolicyUtil;
import cn.edu.buaa.crypto.utils.TestUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;


public class MHOOEncryptionOut {
private static CPABEMHOOEngine engine;	

	public static  void CPABEMHOOEncryptionOut(String PKpath, String access_policy_Ti, PairingCipherSerParameter it, String filePath, String couserName) {
		engine = CPABEMHOOEngine.getInstance();
		engine.setAccessControlEngine(LSSSLW10Engine.getInstance());
		try {
			PairingKeySerParameter publicKey = TestUtils.deSerializationKey(CPABEMHOOAddress.keyPairAddress + PKpath);
	
	 		String comparableAttributeMaxValue = "scl:10 ts:1569809439999 te:1569809439999";
	 		Map<String, Integer> binaryLength = PolicyUtil.getComparableAttributeBinaryLength(comparableAttributeMaxValue);
	 		access_policy_Ti = PolicyUtil.policyReplace(access_policy_Ti, binaryLength);
			
	        int[][] accessPolicyTi = ParserUtils.GenerateAccessPolicy(access_policy_Ti);
	        String[] rhosTi = ParserUtils.GenerateRhos(access_policy_Ti);   
	        
	        PairingCipherSerParameter cti = engine.encryptionOut(publicKey, accessPolicyTi, rhosTi, it);
	        TestUtils.serialization(cti, filePath + "cti_"+ couserName +".txt");

			System.out.println("EncryptionOut sucessful!");
			
		 } catch (IOException e) {
	            System.out.println("EncryptionOut test failed.");
	            e.printStackTrace();
	            System.exit(1);
	     }catch(ClassNotFoundException e) {
	            e.printStackTrace();
	            System.exit(1);
	     } catch (PolicySyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	
}
