package com.example.encryption.ibe;

import cn.edu.buaa.crypto.algebra.serparams.PairingKeyEncapsulationSerPair;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeySerPair;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeySerParameter;
import cn.edu.buaa.crypto.access.lsss.lw10.LSSSLW10Engine;
import cn.edu.buaa.crypto.access.parser.PolicySyntaxException;
import cn.edu.buaa.crypto.algebra.serparams.PairingCipherSerParameter;
import cn.edu.buaa.crypto.encryption.ibe.IBEEngine;
import cn.edu.buaa.crypto.encryption.ibe.bf01b.IBEBF01bEngine;
import cn.edu.buaa.crypto.utils.TestUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;


import java.io.IOException;

/**
 * Created by Weiran Liu on 2015/10/5.
 *
 * IBE engine test.
 */
public class IBEEngineTest {
	private static final String identity_1 = "ID_1";
	private static final String identity_2 = "ID_2";

	private static IBEEngine engine;

	public static void main(String[] args)
			throws IOException, ClassNotFoundException, PolicySyntaxException, InvalidCipherTextException {
		engine = IBEBF01bEngine.getInstance();
		System.out.println("Test " + engine.getEngineName() + " using " + LSSSLW10Engine.SCHEME_NAME);
		PairingParameters pairingParameters = PairingFactory
				.getPairingParameters(TestUtils.TEST_PAIRING_PARAMETERS_PATH_a_80_256);
		Pairing pairing = PairingFactory.getPairing(pairingParameters);

		// Setup and serialization
		PairingKeySerPair keyPair = engine.setup(pairingParameters);
		PairingKeySerParameter publicKey = keyPair.getPublic();

		PairingKeySerParameter masterKey = keyPair.getPrivate();

		// test valid example
		System.out.println("Test valid examples");
		try_valid_decryption(pairing, publicKey, masterKey, identity_1, identity_1);
		try_valid_decryption(pairing, publicKey, masterKey, identity_2, identity_2);

		// test valid example
		System.out.println("Test invalid examples");
		try_invalid_decryption(pairing, publicKey, masterKey, identity_1, identity_2);
		try_invalid_decryption(pairing, publicKey, masterKey, identity_2, identity_1);
		System.out.println(engine.getEngineName() + " test passed");

	}

	private static void try_decryption(Pairing pairing, PairingKeySerParameter publicKey,
			PairingKeySerParameter masterKey, String identityForSecretKey, String identityForCiphertext)
			throws InvalidCipherTextException, IOException, ClassNotFoundException {
		//KeyGen and serialization
		PairingKeySerParameter secretKey = engine.keyGen(publicKey, masterKey, identityForSecretKey);

		//Encryption and serialization
		Element message = pairing.getGT().newRandomElement().getImmutable();
		System.out.println("message:" + message);
		PairingCipherSerParameter ciphertext = engine.encryption(publicKey, identityForCiphertext, message);

		//Decryption
		Element anMessage = engine.decryption(publicKey, secretKey, identityForCiphertext, ciphertext);
		System.out.println("anmessage:" + anMessage);

		//Encapsulation and serialization
		PairingKeyEncapsulationSerPair encapsulationPair = engine.encapsulation(publicKey, identityForCiphertext);
		byte[] sessionKey = encapsulationPair.getSessionKey();
		PairingCipherSerParameter header = encapsulationPair.getHeader();
		//Decapsulation
		byte[] anSessionKey = engine.decapsulation(publicKey, secretKey, identityForCiphertext, header);
	}

	private static void try_valid_decryption(Pairing pairing, PairingKeySerParameter publicKey,
			PairingKeySerParameter masterKey, String identityForSecretKey, String identityForCiphertext) {
		try {
			try_decryption(pairing, publicKey, masterKey, identityForSecretKey, identityForCiphertext);
		} catch (Exception e) {
			System.out.println("Valid decryption test failed, " + "secret key identity  = " + identityForSecretKey
					+ ", " + "ciphertext identity = " + identityForCiphertext);
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static void try_invalid_decryption(Pairing pairing, PairingKeySerParameter publicKey,
			PairingKeySerParameter masterKey, String identityForSecretKey, String identityForCiphertext) {
		try {
			try_decryption(pairing, publicKey, masterKey, identityForSecretKey, identityForCiphertext);
		} catch (InvalidCipherTextException e) {
			// correct if getting there, nothing to do.
		} catch (Exception e) {
			System.out.println("Invalid decryption test failed, " + "secret key identity  = " + identityForSecretKey
					+ ", " + "ciphertext identity = " + identityForCiphertext);
			e.printStackTrace();
			System.exit(1);
		}
	}

}
