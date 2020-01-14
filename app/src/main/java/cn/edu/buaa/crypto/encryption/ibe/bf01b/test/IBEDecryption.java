package cn.edu.buaa.crypto.encryption.ibe.bf01b.test;

import java.io.IOException;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.junit.Assert;

import cn.edu.buaa.crypto.access.lsss.lw10.LSSSLW10Engine;
import cn.edu.buaa.crypto.algebra.serparams.PairingCipherSerParameter;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeySerPair;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeySerParameter;
import cn.edu.buaa.crypto.encryption.ibe.bf01b.IBEBF01bEngine;
import cn.edu.buaa.crypto.utils.AESCoder;
import cn.edu.buaa.crypto.utils.TestUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;


public class IBEDecryption {
	private static IBEBF01bEngine engine;
    
	public static void IBE01bDecryption(String PKpath, String SKpath, String id, String IBEHeaderPath, String TauPath, String anSigmaPath){
		engine = IBEBF01bEngine.getInstance();
		try {	
			PairingKeySerParameter publicKey = TestUtils.deSerializationKey(IBEAddress.keyPairAddress+PKpath);
			PairingKeySerParameter secretKey = TestUtils.deSerializationKey(IBEAddress.secretKeyAddress+SKpath);
			PairingCipherSerParameter header = (PairingCipherSerParameter)TestUtils.deSerializationCipher(IBEAddress.rcvZIPAddress+ IBEHeaderPath);
			byte[] sessionKey = engine.decapsulation(publicKey, secretKey, id, header);

			AESCoder.decryptFile(sessionKey,IBEAddress.rcvZIPAddress+ TauPath,  IBEAddress.rcvZIPAddress+ anSigmaPath);

			System.out.println("IBEDecryption sucessful!");

		 } catch (IOException e) {
	            System.out.println("IBEDecryption test failed.");
	            e.printStackTrace();
	            System.exit(1);
	     }catch(ClassNotFoundException e) {
	            e.printStackTrace();
	            System.exit(1);
	     } catch (InvalidCipherTextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 
}
