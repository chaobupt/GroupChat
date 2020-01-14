package cn.edu.buaa.crypto.encryption.ibe.bf01b.test;

import java.io.IOException;

import org.bouncycastle.crypto.CipherParameters;
import org.junit.Assert;

import cn.edu.buaa.crypto.access.lsss.lw10.LSSSLW10Engine;
import cn.edu.buaa.crypto.algebra.serparams.PairingCipherSerParameter;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeyEncapsulationSerPair;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeySerPair;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeySerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.test.CPABEMHOOAddress;
import cn.edu.buaa.crypto.encryption.ibe.bf01b.IBEBF01bEngine;
import cn.edu.buaa.crypto.encryption.ibe.bf01b.serparams.IBEBF01bHeaderSerParameter;
import cn.edu.buaa.crypto.utils.AESCoder;
import cn.edu.buaa.crypto.utils.TestUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;


public class IBEEncryption {
	private static IBEBF01bEngine engine;
    
	public static void IBE01bEncryption(String PKpath, String id, String sigmaPath, String IBEHeadPath, String TauPath){
		engine = IBEBF01bEngine.getInstance();
		try {	
			PairingKeySerParameter publicKey = TestUtils.deSerializationKey(IBEAddress.keyPairAddress + PKpath);
			PairingKeyEncapsulationSerPair pair = engine.encapsulation(publicKey,id);
			byte[] byteArraySessionKey = pair.getSessionKey();
			IBEBF01bHeaderSerParameter header = (IBEBF01bHeaderSerParameter)pair.getHeader();
			TestUtils.serialization(header, CPABEMHOOAddress.encryptedAddress + IBEHeadPath);
			AESCoder.encryptFile(byteArraySessionKey,CPABEMHOOAddress.encryptedAddress + sigmaPath, CPABEMHOOAddress.encryptedAddress + TauPath);

			System.out.println("IBEEncryption sucessful!");
		 } catch (IOException e) {
	            System.out.println("IBEEncryption test failed.");
	            e.printStackTrace();
	            System.exit(1);
	     }catch(ClassNotFoundException e) {
	            e.printStackTrace();
	            System.exit(1);
	     } catch (Exception e) {
			e.printStackTrace();
		}
	} 
}
