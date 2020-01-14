package cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.test;
import java.io.IOException;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import cn.edu.buaa.crypto.access.lsss.lw10.LSSSLW10Engine;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeySerPair;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeySerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.CPABEMHOOEngine;
import cn.edu.buaa.crypto.utils.TestUtils;

public class MHOOSetup {
	private static CPABEMHOOEngine engine;
	
	public static void CPABEMHOOSetup(String PKpath, String MKpath) {	
		engine = CPABEMHOOEngine.getInstance();
		engine.setAccessControlEngine(LSSSLW10Engine.getInstance());
		
		try {
			PairingParameters pairingParameters = PairingFactory.getPairingParameters(TestUtils.TEST_PAIRING_PARAMETERS_PATH_a_80_256);
			PairingKeySerPair keyPair = engine.setup(pairingParameters, 100);	
			
			PairingKeySerParameter publicKey = keyPair.getPublic();
			TestUtils.serialization(publicKey,CPABEMHOOAddress.keyPairAddress + PKpath);
			
			PairingKeySerParameter masterKey = keyPair.getPrivate();			
			TestUtils.serialization(masterKey, CPABEMHOOAddress.keyPairAddress + MKpath);
			
			System.out.println("setup sucessful!");			
		 } catch (IOException e) {
	            System.out.println("setup test failed.");
	            e.printStackTrace();
	            System.exit(1);
	     } 
	}

}
