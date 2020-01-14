package cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.test;

import java.io.IOException;
import java.util.Map;

import org.bouncycastle.crypto.CipherParameters;

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


public class MHOOEncryptionIn {
private static CPABEMHOOEngine engine;	

	public static  CPABEMHOOCiphertextInSerParameter CPABEMHOOEncryptionIn(String PKpath, String access_policy_T0 , String oFile, String scrambledFile, Map<String, int[]> faces, Element ek) {
		engine = CPABEMHOOEngine.getInstance();
		engine.setAccessControlEngine(LSSSLW10Engine.getInstance());
		CPABEMHOOCiphertextInSerParameter cipherInParameter = null;
		try {
			PairingKeySerParameter publicKey = TestUtils.deSerializationKey(CPABEMHOOAddress.keyPairAddress + PKpath);
	
	 		String comparableAttributeMaxValue = "scl:10 ts:1569809439999 te:1569809439999";
	 		Map<String, Integer> binaryLength =PolicyUtil.getComparableAttributeBinaryLength(comparableAttributeMaxValue);
	 		access_policy_T0 = PolicyUtil.policyReplace(access_policy_T0, binaryLength);
	        int[][] accessPolicyT0 = ParserUtils.GenerateAccessPolicy(access_policy_T0);
	        String[] rhosT0 = ParserUtils.GenerateRhos(access_policy_T0);   
	        
		    cipherInParameter = engine.encryptionIn(publicKey, accessPolicyT0, rhosT0, oFile, scrambledFile, faces, ek, CPABEMHOOAddress.encryptedAddress);
			System.out.println("EncryptionIn sucessful!");
			return cipherInParameter;
		 } catch (IOException e) {
	            System.out.println("EncryptionIn test failed.");
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
	    return cipherInParameter;
	} 
	
}
