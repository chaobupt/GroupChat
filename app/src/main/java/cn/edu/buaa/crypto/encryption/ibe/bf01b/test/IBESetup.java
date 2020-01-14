package cn.edu.buaa.crypto.encryption.ibe.bf01b.test;
import java.io.IOException;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeySerPair;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeySerParameter;
import cn.edu.buaa.crypto.encryption.ibe.bf01b.IBEBF01bEngine;
import cn.edu.buaa.crypto.utils.TestUtils;

public class IBESetup{
	private static IBEBF01bEngine engine;
	
	public static void IBE01bSetup(String PKpath, String MKpath){	
		engine = IBEBF01bEngine.getInstance();
		PairingParameters pairingParameters = PairingFactory.getPairingParameters(TestUtils.TEST_PAIRING_PARAMETERS_PATH_a_80_256);
		
		try {
            PairingKeySerPair keyPair = engine.setup(pairingParameters);	
			
			PairingKeySerParameter publicKey = keyPair.getPublic();
			TestUtils.serialization(publicKey,IBEAddress.keyPairAddress + PKpath);
			
			PairingKeySerParameter masterKey = keyPair.getPrivate();		
			TestUtils.serialization(masterKey,IBEAddress.keyPairAddress + MKpath);
			
			System.out.println("setup sucessful!");
			
		 } catch (IOException e) {
	            System.out.println("setup test failed.");
	            e.printStackTrace();
	            System.exit(1);
	     }
	} 

}
