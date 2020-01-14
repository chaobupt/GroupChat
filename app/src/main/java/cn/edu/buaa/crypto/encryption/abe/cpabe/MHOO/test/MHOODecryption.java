package cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.test;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
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


public class MHOODecryption {
private static CPABEMHOOEngine engine;	

	public static  Map<String, BigInteger> CPABEMHOODecryption(String PKpath, String SKpath, Map<String, String> accessPolicys, String ct0Path , Map<String, String> ctiPaths, String basicPath) {
		engine = CPABEMHOOEngine.getInstance();
		engine.setAccessControlEngine(LSSSLW10Engine.getInstance());
		Map<String, BigInteger> scramblingKeys = null;
		
		try {
			PairingKeySerParameter publicKey = TestUtils.deSerializationKey(CPABEMHOOAddress.keyPairAddress + PKpath);
			PairingKeySerParameter secretKey = TestUtils.deSerializationKey(CPABEMHOOAddress.secretKeyAddress + SKpath);

			//产生accessPolicyTis、rhosTis
	 		String comparableAttributeMaxValue = "scl:10 ts:1569809439999 te:1569809439999";
	 		Map<String, Integer> binaryLength =PolicyUtil.getComparableAttributeBinaryLength(comparableAttributeMaxValue);
	 		String access_policy_Ti ="";
	 		Map<String, int[][]> accessPolicyTis = new HashMap<String, int[][]>();
	 		Map<String, String[]> rhosTis = new HashMap<String, String[]>();
	 		for(String user: accessPolicys.keySet()) {
	 			access_policy_Ti = PolicyUtil.policyReplace(accessPolicys.get(user), binaryLength); 
	 			int[][] accessPolicyTi = ParserUtils.GenerateAccessPolicy(access_policy_Ti);
	 	        String[] rhosTi = ParserUtils.GenerateRhos(access_policy_Ti);   
	 	        accessPolicyTis.put(user, accessPolicyTi);
	 	        rhosTis.put(user, rhosTi);
	 		}

	 		PairingCipherSerParameter ct0 = (PairingCipherSerParameter)TestUtils.deSerializationCipher(ct0Path);
	 		//产生ctis
	 		Map<String, PairingCipherSerParameter> ctis = new HashMap<String, PairingCipherSerParameter>();
	 		for(String couser: ctiPaths.keySet()) {
	 			PairingCipherSerParameter cti = (PairingCipherSerParameter)TestUtils.deSerializationCipher(ctiPaths.get(couser));
	 			ctis.put(couser, cti);
	 		}
	 		
	 		Map<String, Element> ms = engine.decryptionPhotoAndFaces(publicKey, secretKey, accessPolicyTis, rhosTis, ct0, ctis);
		
	 		scramblingKeys = new HashMap<String, BigInteger>();
	 		Element ek0 = ms.get("sender");
	 		AESCoder.decryptFile(ek0.toBytes(), basicPath + "hdr0.txt",  basicPath + "anScrambledPhoto.jpg");
	 		for(String couser: ms.keySet()) {
	 			if(!couser.equals("sender")){
					BigInteger ki = ms.get(couser).toBigInteger();
					scramblingKeys.put(couser, ki);
				}
	 		}
			System.out.println("Decryption sucessful!");
			return scramblingKeys;
		 } catch (IOException e) {
	            System.out.println("Decryption test failed.");
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
		return scramblingKeys;
	} 
	
}
