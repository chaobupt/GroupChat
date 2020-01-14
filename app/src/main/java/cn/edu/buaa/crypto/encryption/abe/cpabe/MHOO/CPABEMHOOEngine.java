package cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO;

import cn.edu.buaa.crypto.algebra.Engine.PayloadSecLevel;
import cn.edu.buaa.crypto.algebra.Engine.PredicateSecLevel;
import cn.edu.buaa.crypto.algebra.Engine.ProveSecModel;
import cn.edu.buaa.crypto.algebra.serparams.PairingCipherSerParameter;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeyEncapsulationSerPair;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeySerPair;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeySerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.CPABEEngine;
import cn.edu.buaa.crypto.encryption.abe.cpabe.OOCPABEEngine;
import cn.edu.buaa.crypto.encryption.abe.cpabe.OOCPABEEngine;
import cn.edu.buaa.crypto.encryption.abe.cpabe.genparams.CPABECTUpdateGenerationParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.genparams.CPABEDecryptionGenerationParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.genparams.CPABEEncryptionGenerationParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.genparams.CPABEIntermediateGenerationParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.genparams.CPABEKeyPairGenerationParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.genparams.CPABESecretKeyGenerationParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.genparams.CPABEIntermediateGenerationParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.generators.CPABEMHOOEncryptionGenerator;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.generators.CPABEMHOOIntermediateGenerator;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.generators.CPABEMHOOCTUpdateGenerator;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.generators.CPABEMHOODecryptionGenerator;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.generators.CPABEMHOOEncryptionGenerator;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.generators.CPABEMHOOKeyPairGenerator;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.generators.CPABEMHOOSecretKeyGenerator;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.generators.CPABEMHOOUKeyGenerator;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.*;
import cn.edu.buaa.crypto.utils.AESCoder;
import cn.edu.buaa.crypto.utils.PairingUtils;
import cn.edu.buaa.crypto.utils.TestUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.crypto.InvalidCipherTextException;

/**
 * Created by Weiran Liu on 2016/11/29.
 *
 * Rouselakis-Waters large-universe CP-ABE engine.
 */
public class CPABEMHOOEngine extends OOCPABEEngine {
	private static final String SCHEME_NAME = "Message hierarchy and online/offline CP-ABE";
	private static CPABEMHOOEngine engine;

	public static CPABEMHOOEngine getInstance() {
		if (engine == null) {
			engine = new CPABEMHOOEngine();
		}
		return engine;
	}

	private CPABEMHOOEngine() {
		super(SCHEME_NAME, ProveSecModel.Standard, PayloadSecLevel.CPA, PredicateSecLevel.NON_ANON);
	}

	@Override
	public PairingCipherSerParameter encryption(PairingKeySerParameter publicKey, int[][] accessPolicyIntArrays,
												String[] rhos, Element message) {
		// TODO Auto-generated method stub
		return null;
	}

	public PairingKeySerPair setup(PairingParameters pairingParameters, int maxAttributesNum) {
		CPABEMHOOKeyPairGenerator keyPairGenerator = new CPABEMHOOKeyPairGenerator();
		keyPairGenerator.init(new CPABEKeyPairGenerationParameter(pairingParameters));

		return keyPairGenerator.generateKeyPair();
	}

	public PairingKeySerParameter keyGen(PairingKeySerParameter publicKey, PairingKeySerParameter masterKey,
										 String[] attributes) {
		if (!(publicKey instanceof CPABEMHOOPublicKeySerParameter)) {
			PairingUtils.NotVerifyCipherParameterInstance(SCHEME_NAME, publicKey,
					CPABEMHOOPublicKeySerParameter.class.getName());
		}
		if (!(masterKey instanceof CPABEMHOOMasterSecretKeySerParameter)) {
			PairingUtils.NotVerifyCipherParameterInstance(SCHEME_NAME, masterKey,
					CPABEMHOOMasterSecretKeySerParameter.class.getName());
		}
		CPABEMHOOSecretKeyGenerator secretKeyGenerator = new CPABEMHOOSecretKeyGenerator();
		secretKeyGenerator.init(new CPABESecretKeyGenerationParameter(publicKey, masterKey, attributes));
		return secretKeyGenerator.generateKey();
	}

	public Element decryption(PairingKeySerParameter publicKey, PairingKeySerParameter secretKey,
							  int[][] accessPolicyIntArrays, String[] rhos, PairingCipherSerParameter ciphertext)
			throws InvalidCipherTextException {
		if (!(publicKey instanceof CPABEMHOOPublicKeySerParameter)) {
			PairingUtils.NotVerifyCipherParameterInstance(SCHEME_NAME, publicKey,
					CPABEMHOOPublicKeySerParameter.class.getName());
		}
		if (!(secretKey instanceof CPABEMHOOSecretKeySerParameter)) {
			PairingUtils.NotVerifyCipherParameterInstance(SCHEME_NAME, secretKey,
					CPABEMHOOSecretKeySerParameter.class.getName());
		}
		if (!(ciphertext instanceof CPABEMHOOCiphertextSerParameter)) {
			PairingUtils.NotVerifyCipherParameterInstance(SCHEME_NAME, ciphertext,
					CPABEMHOOCiphertextSerParameter.class.getName());
		}
		CPABEMHOODecryptionGenerator decryptionGenerator = new CPABEMHOODecryptionGenerator();
		decryptionGenerator.init(new CPABEDecryptionGenerationParameter(accessControlEngine, publicKey, secretKey,
				accessPolicyIntArrays, rhos, ciphertext));
		return decryptionGenerator.recoverMessage();
	}

	public PairingKeyEncapsulationSerPair encapsulation(PairingKeySerParameter publicKey, int[][] accessPolicyIntArrays,
														String[] rhos) {
		if (!(publicKey instanceof CPABEMHOOPublicKeySerParameter)) {
			PairingUtils.NotVerifyCipherParameterInstance(SCHEME_NAME, publicKey,
					CPABEMHOOPublicKeySerParameter.class.getName());
		}
		CPABEMHOOEncryptionGenerator encryptionGenerator = new CPABEMHOOEncryptionGenerator();
		encryptionGenerator.init(new CPABEEncryptionGenerationParameter(accessControlEngine, publicKey,
				accessPolicyIntArrays, rhos, null));
		return encryptionGenerator.generateEncryptionPair();
	}

	public byte[] decapsulation(PairingKeySerParameter publicKey, PairingKeySerParameter secretKey,
								int[][] accessPolicyIntArrays, String[] rhos, PairingCipherSerParameter header)
			throws InvalidCipherTextException {
		if (!(publicKey instanceof CPABEMHOOPublicKeySerParameter)) {
			PairingUtils.NotVerifyCipherParameterInstance(SCHEME_NAME, publicKey,
					CPABEMHOOPublicKeySerParameter.class.getName());
		}
		if (!(secretKey instanceof CPABEMHOOSecretKeySerParameter)) {
			PairingUtils.NotVerifyCipherParameterInstance(SCHEME_NAME, secretKey,
					CPABEMHOOSecretKeySerParameter.class.getName());
		}
		if (!(header instanceof CPABEMHOOHeaderSerParameter)) {
			PairingUtils.NotVerifyCipherParameterInstance(SCHEME_NAME, header,
					CPABEMHOOHeaderSerParameter.class.getName());
		}
		CPABEMHOODecryptionGenerator decryptionGenerator = new CPABEMHOODecryptionGenerator();
		decryptionGenerator.init(new CPABEDecryptionGenerationParameter(accessControlEngine, publicKey, secretKey,
				accessPolicyIntArrays, rhos, header));
		return decryptionGenerator.recoverKey();
	}

	/******************************************** online/offline 加密 ***********************************************************/

	// 2.2 后connect and correct 加密生成cti
	public PairingCipherSerParameter encryption(PairingKeySerParameter publicKey,
												PairingCipherSerParameter intermediate, int[][] accessPolicyIntArrays, String[] rhos, Element message) {
		if (!(publicKey instanceof CPABEMHOOPublicKeySerParameter)) {
			PairingUtils.NotVerifyCipherParameterInstance(SCHEME_NAME, publicKey,
					CPABEMHOOPublicKeySerParameter.class.getName());
		}
		if (!(intermediate instanceof CPABEMHOOIntermediateSerParameter)) {
			PairingUtils.NotVerifyCipherParameterInstance(SCHEME_NAME, intermediate,
					CPABEMHOOIntermediateSerParameter.class.getName());
		}
		CPABEMHOOEncryptionGenerator encryptionGenerator = new CPABEMHOOEncryptionGenerator();
		CPABEEncryptionGenerationParameter encryptionGenerationParameter = new CPABEEncryptionGenerationParameter(
				accessControlEngine, publicKey, accessPolicyIntArrays, rhos, message);
		encryptionGenerationParameter.setIntermediate(intermediate);
		encryptionGenerator.init(encryptionGenerationParameter);

		return encryptionGenerator.generateCiphertext();
	}

	public PairingKeyEncapsulationSerPair encapsulation(PairingKeySerParameter publicKey,
														PairingCipherSerParameter intermediate, int[][] accessPolicyIntArrays, String[] rhos) {
		if (!(publicKey instanceof CPABEMHOOPublicKeySerParameter)) {
			PairingUtils.NotVerifyCipherParameterInstance(SCHEME_NAME, publicKey,
					CPABEMHOOPublicKeySerParameter.class.getName());
		}
		if (!(intermediate instanceof CPABEMHOOIntermediateSerParameter)) {
			PairingUtils.NotVerifyCipherParameterInstance(SCHEME_NAME, intermediate,
					CPABEMHOOIntermediateSerParameter.class.getName());
		}
		CPABEMHOOEncryptionGenerator encryptionGenerator = new CPABEMHOOEncryptionGenerator();
		CPABEEncryptionGenerationParameter encryptionGenerationParameter = new CPABEEncryptionGenerationParameter(
				accessControlEngine, publicKey, accessPolicyIntArrays, rhos, null);
		encryptionGenerationParameter.setIntermediate(intermediate);
		encryptionGenerator.init(encryptionGenerationParameter);

		return encryptionGenerator.generateEncryptionPair();
	}

	/***************************************************** MHOO-CP-ABE
	 * @throws IOException **********************************************************************/
	// 普通加密m0生成ct0
	public PairingCipherSerParameter encryption(PairingKeySerParameter publicKey, int[][] accessPolicyIntArrays,
												String[] rhos, Element s, Element message) throws IOException{
		if (!(publicKey instanceof CPABEMHOOPublicKeySerParameter)) {
			PairingUtils.NotVerifyCipherParameterInstance(SCHEME_NAME, publicKey,
					CPABEMHOOPublicKeySerParameter.class.getName());
		}
		CPABEMHOOEncryptionGenerator encryptionGenerator = new CPABEMHOOEncryptionGenerator();
		encryptionGenerator.init(new CPABEEncryptionGenerationParameter(accessControlEngine, publicKey,
				accessPolicyIntArrays, rhos, message));
		return encryptionGenerator.generateCiphertext(s);
	}

	public PairingKeyEncapsulationSerPair encapsulation2(PairingKeySerParameter publicKey, int[][] accessPolicyIntArrays,
														 String[] rhos, Element s) {
		if (!(publicKey instanceof CPABEMHOOPublicKeySerParameter)) {
			PairingUtils.NotVerifyCipherParameterInstance(SCHEME_NAME, publicKey,
					CPABEMHOOPublicKeySerParameter.class.getName());
		}
		CPABEMHOOEncryptionGenerator encryptionGenerator = new CPABEMHOOEncryptionGenerator();
		encryptionGenerator.init(new CPABEEncryptionGenerationParameter(accessControlEngine, publicKey,
				accessPolicyIntArrays, rhos, null));
		return encryptionGenerator.generateEncryptionPair(s);
	}
	// offlineEncryption 加密mi生成 中间密文 it
	public PairingCipherSerParameter offlineEncryption(PairingKeySerParameter publicKey, Element s0, Element message,
													   int P) {
		if (!(publicKey instanceof CPABEMHOOPublicKeySerParameter)) {
			PairingUtils.NotVerifyCipherParameterInstance(SCHEME_NAME, publicKey,
					CPABEMHOOPublicKeySerParameter.class.getName());
		}
		CPABEMHOOIntermediateGenerator intermediateGenerator = new CPABEMHOOIntermediateGenerator();
		intermediateGenerator.init(new CPABEIntermediateGenerationParameter(publicKey, message, P));

		return intermediateGenerator.generateCiphertext(s0);
	}

	// TODO:EncryptIn(pk, T, (m0, F), P) -> (ct0, s0, ek0, IT)
	//文件
	public CPABEMHOOCiphertextInSerParameter encryptionIn(PairingKeySerParameter publicKey, int[][] accessPolicyT0,
														  String[] rhosT0,  String oFile, String scrambledFile, Map<String, int[]> faces, Element ek, String filePath) throws Exception {
		Pairing pairing = PairingFactory.getPairing(publicKey.getParameters());
		// (1) o - ek - hdr
		byte[] b_ek = ek.toBytes();
		byte[] b_photo = AESCoder.readFile(oFile);
		byte[] ct0 = AESCoder.encrypt(b_ek, b_photo);
		AESCoder.writeFile(filePath + "hdr.txt", ct0);

		// (2) m0 - ek0 - hdr0
		Element s0 = pairing.getZr().newRandomElement().getImmutable();
		byte[] b_scrambledPhoto = AESCoder.readFile(scrambledFile);
		PairingKeyEncapsulationSerPair pair = encapsulation2(publicKey, accessPolicyT0, rhosT0, s0);
		byte[] ek0 = pair.getSessionKey();
		PairingCipherSerParameter header = pair.getHeader();
		byte[] hdr0 = AESCoder.encrypt(ek0,  b_scrambledPhoto);
		AESCoder.writeFile(filePath + "hdr0.txt", hdr0);
		byte[] byteArrayCT0 = TestUtils.SerCipherParameter(header);
		AESCoder.writeFile(filePath + "ct0.txt", byteArrayCT0);

		// (3) mi - eki - cti
		Map<String, PairingCipherSerParameter> IT = new HashMap<String, PairingCipherSerParameter>();
		for (String couser: faces.keySet()) {
			int[] face = faces.get(couser);
			Element faceMi = pairing.getGT().newElement(face[0]).getImmutable();
			System.out.println(couser + ":" + faceMi.toBigInteger());
			PairingCipherSerParameter iti = offlineEncryption(publicKey, s0, faceMi, rhosT0.length);
			IT.put(couser, iti);
		}
		Map<String, PairingCipherSerParameter> ets = new HashMap<String, PairingCipherSerParameter>();
		for(String key: IT.keySet()){
			CPABEMHOOIntermediateSerParameter it = (CPABEMHOOIntermediateSerParameter)(IT.get(key));
			ets.put(key, it.getEt());
		}
		for(String key: ets.keySet()){
			CPABEMHOOIntermediateETSerParameter eti = (CPABEMHOOIntermediateETSerParameter)ets.get(key);
			//eti存到cloud server, eti安全传送给co-user
			TestUtils.serialization(eti, filePath + "et_"+ key +".txt");
		}

		return new CPABEMHOOCiphertextInSerParameter(publicKey.getParameters(), header, s0, IT);
	}

	public CPABEMHOOCiphertextInSerParameter encryptionIn(PairingKeySerParameter publicKey, int[][] accessPolicyT0,
														  String[] rhosT0, Map<String, Element> M) throws IOException {
		// (1) m0 - ek0 - ct0
		Pairing pairing = PairingFactory.getPairing(publicKey.getParameters());
		Element s0 = pairing.getZr().newRandomElement().getImmutable();
		PairingCipherSerParameter ct0 = encryption(publicKey, accessPolicyT0, rhosT0, s0, M.get("m0"));

		// (2) mi - eki - cti
		Map<String, PairingCipherSerParameter> IT = new HashMap<String, PairingCipherSerParameter>();
		IT.put("m0", ct0);
		for (String m : M.keySet()) {
			if(!m.equals("m0")) {
				Element mi = M.get(m);
				PairingCipherSerParameter iti = offlineEncryption(publicKey, s0, mi, rhosT0.length);
				IT.put(m, iti);
			}
		}
		return new CPABEMHOOCiphertextInSerParameter(publicKey.getParameters(), ct0, s0, IT);
	}

	// TODO:EncryptOut(pk, Ti, it) -> (cti)
	public PairingCipherSerParameter encryptionOut(PairingKeySerParameter publicKey, int[][] accessPolicyIntArrays,
												   String[] rhos, PairingCipherSerParameter intermediate) {
		CPABEMHOOEncryptionGenerator encryptionGenerator = new CPABEMHOOEncryptionGenerator();
		CPABEEncryptionGenerationParameter encryptionGenerationParameter = new CPABEEncryptionGenerationParameter(
				accessControlEngine, publicKey, accessPolicyIntArrays, rhos, null);
		encryptionGenerationParameter.setIntermediate(intermediate);
		encryptionGenerator.init(encryptionGenerationParameter);

		return encryptionGenerator.generateCiphertext(null);
	}
	public Map<String, Element> decryptionPhotoAndFaces(PairingKeySerParameter publicKey, PairingKeySerParameter secretKey,
														Map<String, int[][]> accessPolicyTis, Map<String, String[]> rhosTis, PairingCipherSerParameter ciphertext,
														Map<String, PairingCipherSerParameter> ctis) throws InvalidCipherTextException {
		CPABEMHOODecryptionGenerator decryptionGenerator = new CPABEMHOODecryptionGenerator();
		// (1)decryptPhoto
		decryptionGenerator.init(new CPABEDecryptionGenerationParameter(accessControlEngine, publicKey, secretKey,
				accessPolicyTis.get("sender"), rhosTis.get("sender"), ciphertext));
		Element ek0 = decryptionGenerator.recoverEK0();
		Element A0 = decryptionGenerator.recoverA0();

		Map<String, Element> recoverMessages = new HashMap<String, Element>();
		recoverMessages.put("sender", ek0);
		// (2)decryptFaces
		for (String couser : ctis.keySet()) {
			PairingCipherSerParameter cti = ctis.get(couser);
			int[][] accessPolicyTi = accessPolicyTis.get(couser);
			String[] rhosTi = rhosTis.get(couser);
			decryptionGenerator.init(new CPABEDecryptionGenerationParameter(accessControlEngine, publicKey,
					secretKey, accessPolicyTi, rhosTi, cti));
			Element mi = decryptionGenerator.recoverMessagei(A0);
			recoverMessages.put(couser, mi);
		}
		return recoverMessages;
	}


	// TODO:Decryption(ct0, {cti}, sk) ->(ek0, {eki})
	public Map<String, Element> decryption(PairingKeySerParameter publicKey, PairingKeySerParameter secretKey,
										   Map<String, int[][]> accessPolicyTis, Map<String, String[]> rhosTis, PairingCipherSerParameter ciphertext,
										   Map<String, PairingCipherSerParameter> ctis) throws InvalidCipherTextException {
		CPABEMHOODecryptionGenerator decryptionGenerator = new CPABEMHOODecryptionGenerator();
		// (1)decryptPhoto
		decryptionGenerator.init(new CPABEDecryptionGenerationParameter(accessControlEngine, publicKey, secretKey,
				accessPolicyTis.get("m0"), rhosTis.get("m0"), ciphertext));
		Element ek0 = decryptionGenerator.recoverEK0();
		Element A0 = decryptionGenerator.recoverA0();
		Map<String, Element> recoverEkis = new HashMap<String, Element>();
		recoverEkis.put("m0", ek0);

		// (2)decryptFaces
		for (String m : ctis.keySet()) {
			PairingCipherSerParameter cti = ctis.get(m);
			int[][] accessPolicyTi = accessPolicyTis.get(m);
			String[] rhosTi = rhosTis.get(m);
			decryptionGenerator.init(new CPABEDecryptionGenerationParameter(accessControlEngine, publicKey,
					secretKey, accessPolicyTi, rhosTi, cti));
			Element eki = decryptionGenerator.recoverEKi(A0);
			recoverEkis.put(m, eki);
		}

		return recoverEkis;
	}

	public Map<String, Element> decryptionM(PairingKeySerParameter publicKey, PairingKeySerParameter secretKey,
											Map<String, int[][]> accessPolicyTis, Map<String, String[]> rhosTis, PairingCipherSerParameter ciphertext,
											Map<String, PairingCipherSerParameter> ctis) throws InvalidCipherTextException {
		CPABEMHOODecryptionGenerator decryptionGenerator = new CPABEMHOODecryptionGenerator();
		// (1)decryptPhoto
		decryptionGenerator.init(new CPABEDecryptionGenerationParameter(accessControlEngine, publicKey, secretKey,
				accessPolicyTis.get("m0"), rhosTis.get("m0"), ciphertext));
		Element m0 = decryptionGenerator.recoverMessage();
		Element A0 = decryptionGenerator.recoverA0();
		Map<String, Element> recoverMessages = new HashMap<String, Element>();
		recoverMessages.put("m0", m0);

		// (2)decryptFaces
		for (String m : ctis.keySet()) {
			PairingCipherSerParameter cti = ctis.get(m);
			int[][] accessPolicyTi = accessPolicyTis.get(m);
			String[] rhosTi = rhosTis.get(m);
			decryptionGenerator.init(new CPABEDecryptionGenerationParameter(accessControlEngine, publicKey,
					secretKey, accessPolicyTi, rhosTi, cti));
			Element mi = decryptionGenerator.recoverMessagei(A0);
			recoverMessages.put(m, mi);
		}

		return recoverMessages;
	}

	// TODO:UKeyGen(pk, s0, T0') ->uk :owner转发更新密钥
	// 1.正常加密m0生成ct0
	public PairingCipherSerParameter UKeyGen(PairingKeySerParameter publicKey, int[][] accessPolicyIntArrays,
											 String[] rhos, Element s) {
		CPABEMHOOUKeyGenerator ukGenerator = new CPABEMHOOUKeyGenerator();
		ukGenerator.init(
				new CPABEEncryptionGenerationParameter(accessControlEngine, publicKey, accessPolicyIntArrays, rhos, s));
		return ukGenerator.UKeyGen(s);
	}

	// TODO:CTUpdate(ct0, uk) -> ct0':更新密文
	public PairingCipherSerParameter CTUpdate(PairingKeySerParameter publicKey, PairingCipherSerParameter ct0,
											  PairingCipherSerParameter uk) {
		CPABEMHOOCTUpdateGenerator ctUpdateGenerator = new CPABEMHOOCTUpdateGenerator();
		ctUpdateGenerator.init(new CPABECTUpdateGenerationParameter(publicKey, ct0, uk));
		return ctUpdateGenerator.CTUpate();
	}

}
