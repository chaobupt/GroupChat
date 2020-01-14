package com.example.encryption.abe;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import cn.edu.buaa.crypto.access.lsss.lw10.LSSSLW10Engine;
import cn.edu.buaa.crypto.access.parser.ParserUtils;
import cn.edu.buaa.crypto.access.parser.PolicySyntaxException;
import cn.edu.buaa.crypto.algebra.serparams.PairingCipherSerParameter;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeySerPair;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeySerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.CPABEMHOOEngine;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOCiphertextInSerParameter;
import cn.edu.buaa.crypto.utils.PolicyUtil;
import cn.edu.buaa.crypto.utils.TestUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class CPABEMHOOEngineTest {
	private static CPABEMHOOEngine engine;

	public static void main(String[] args)
			throws IOException, ClassNotFoundException, PolicySyntaxException, InvalidCipherTextException {
		engine = CPABEMHOOEngine.getInstance();

		System.out.println("Test " + engine.getEngineName() + " using " + LSSSLW10Engine.SCHEME_NAME);
		engine.setAccessControlEngine(LSSSLW10Engine.getInstance());
		PairingParameters pairingParameters = PairingFactory
				.getPairingParameters(TestUtils.TEST_PAIRING_PARAMETERS_PATH_a_80_256);
		Pairing pairing = PairingFactory.getPairing(pairingParameters);

		// 1.Setup and serialization
		PairingKeySerPair keyPair = engine.setup(pairingParameters, 100);
		PairingKeySerParameter publicKey = keyPair.getPublic();
		PairingKeySerParameter masterKey = keyPair.getPrivate();

		// test examples
		System.out.println("Test example 1");	
		String access_policy_T0 = "1 and acquaintance and classmate and scl>=3 and ts<=2019-09-30;13:11:33:365 and te>=2019-09-30;13:11:33:365"; 
 		String comparableAttributeMaxValue = "scl:10 ts:1569809439999 te:1569809439999";
 		Map<String, Integer> binaryLength =PolicyUtil.getComparableAttributeBinaryLength(comparableAttributeMaxValue);
 		access_policy_T0 = PolicyUtil.policyReplace(access_policy_T0, binaryLength); 	
		System.out.println("access_policy_T: "+access_policy_T0); 
 		String[] access_policy_satisfied_Alice_S = new String[] {"1", "acquaintance", "classmate", "scl=4", "ts=2019-09-30 08:10:30:360", "te=2020-01-01 08:10:30:360"};
 		access_policy_satisfied_Alice_S = PolicyUtil.attributeReplace(access_policy_satisfied_Alice_S, binaryLength);
		System.out.println("access_policy_satisfied_Alice_S："+Arrays.toString(access_policy_satisfied_Alice_S));
		
        int[][] accessPolicyT0 = ParserUtils.GenerateAccessPolicy(access_policy_T0);
        String[] rhosT0 = ParserUtils.GenerateRhos(access_policy_T0);      
        	
		// try online/offline mechanism
		// 2.KeyGen and serialization
		PairingKeySerParameter secretKey = engine.keyGen(publicKey, masterKey, access_policy_satisfied_Alice_S);
	
		//3.1TODO:EncryptIn(pk, T, (m0, F), P) -> (ct0, s0, ek0, IT) offline-----owner(sender)
		int miNum = 3;
		Map<String,Element> M = new HashMap<String,Element>(); //mi
		for(int i=0; i<miNum; i++) {
			Element mi =  pairing.getGT().newRandomElement().getImmutable();
			M.put("m"+i, mi);
			System.out.println("m"+i+":" + mi);
		}		
		CPABEMHOOCiphertextInSerParameter cipherInParameter= engine.encryptionIn(publicKey, accessPolicyT0, rhosT0, M);
		PairingCipherSerParameter ct0 = cipherInParameter.getCt0();
		Element s0 = cipherInParameter.getS0();
		Map<String, PairingCipherSerParameter> IT = cipherInParameter.getIT();
		
		//3.2TODO:EncryptOut(pk, Ti, it) -> (cti)  online-----co-owners     
		String access_policy_T1 = "1 and acquaintance and classmate and scl>=3 and ts<=2019-09-30;13:11:33:365 and te>=2019-09-30;13:11:33:365"; 
 		access_policy_T1 = PolicyUtil.policyReplace(access_policy_T1, binaryLength);
		String access_policy_T2 = "1 and acquaintance and classmate and scl>=3 and ts<=2019-09-30;13:11:33:365 and te>=2019-09-30;13:11:33:365"; 
 		access_policy_T2 = PolicyUtil.policyReplace(access_policy_T2, binaryLength);
 		
        int[][] accessPolicyT1 = ParserUtils.GenerateAccessPolicy(access_policy_T1);
        String[] rhosT1 = ParserUtils.GenerateRhos(access_policy_T1);
        int[][] accessPolicyT2 = ParserUtils.GenerateAccessPolicy(access_policy_T2);
        String[] rhosT2 = ParserUtils.GenerateRhos(access_policy_T2);
        
		Map<String, int[][]> accessPolicyTis = new HashMap<String, int[][]>();
		Map<String, String[]> rhosTis = new HashMap<String, String[]>();
		accessPolicyTis.put("m0", accessPolicyT0);
		rhosTis.put("m0", rhosT0);
		accessPolicyTis.put("m1", accessPolicyT1);
		rhosTis.put("m1", rhosT1);
		accessPolicyTis.put("m2", accessPolicyT2);
		rhosTis.put("m2", rhosT2);

		Map<String, PairingCipherSerParameter> ctis = new HashMap<String, PairingCipherSerParameter>();
		PairingCipherSerParameter ct1 = engine.encryptionOut(publicKey, accessPolicyT1, rhosT1, IT.get("m1"));
		PairingCipherSerParameter ct2 = engine.encryptionOut(publicKey, accessPolicyT2, rhosT2, IT.get("m2"));
		ctis.put("m1", ct1);
		ctis.put("m2", ct2);	
		
		//4.TODO:Decryption(ct0, {cti}, sk) ->(ek0, {eki})	
//		Map<String, Element> recoverEkis = new HashMap<String, Element>();
//		recoverEkis = engine.decryption(publicKey, secretKey, accessPolicyTis, rhosTis, ct0, ctis);
		Map<String, Element> recoverMessages = new HashMap<String, Element>();
		recoverMessages = engine.decryptionM(publicKey, secretKey, accessPolicyTis, rhosTis, ct0, ctis);
		for(String m: recoverMessages.keySet()) {
			System.out.println("recovermi: " + recoverMessages.get(m));
		}
		
		System.out.println( "Repost");
		
		//TODO:UKeyGen(pk, s0, T0') ->uk :owner转发更新密钥	
//		String access_policy_T00 = "1 and acquaintance and classmate and scl>=3 and ts<=2019-09-30;13:11:33:365 and te>=2019-09-30;13:11:33:365"; 
// 		access_policy_T00 = PolicyUtil.policyReplace(access_policy_T00, binaryLength); 
//		int[][] accessPolicyT00 = ParserUtils.GenerateAccessPolicy(access_policy_T00);
//        String[] rhosT00 = ParserUtils.GenerateRhos(access_policy_T00);      
//        
//		PairingCipherSerParameter uk = engine.UKeyGen(publicKey, accessPolicyT00, rhosT00, s0);//s0不变
//		
//		//TODO:CTUpdate(ct0, uk) -> ct0':更新密文
//		PairingCipherSerParameter ct0Update = engine.CTUpdate(publicKey, ct0, uk);		
//		//更新密文后解密
//		Map<String, Element> recoverMessages2 = new HashMap<String, Element>();
//		recoverMessages2 = engine.decryptionM(publicKey, secretKey, accessPolicyTis, rhosTis, ct0Update, ctis);
//		for(String m: recoverMessages2.keySet()) {
//			System.out.println("recovermi: " + recoverMessages2.get(m));
//		}
		
		System.out.println(engine.getEngineName() + " test passed");
	}
}
