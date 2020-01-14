package cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.generators;

import cn.edu.buaa.crypto.access.AccessControlEngine;
import cn.edu.buaa.crypto.access.AccessControlParameter;
import cn.edu.buaa.crypto.algebra.generators.PairingEncapsulationPairGenerator;
import cn.edu.buaa.crypto.algebra.generators.PairingEncryptionGenerator;
import cn.edu.buaa.crypto.algebra.serparams.PairingCipherSerParameter;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeyEncapsulationSerPair;
import cn.edu.buaa.crypto.encryption.abe.cpabe.genparams.CPABECTUpdateGenerationParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.genparams.CPABEEncryptionGenerationParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOCiphertextSerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOHeaderSerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOIntermediateSerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOPublicKeySerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOUKeySerParameter;
import cn.edu.buaa.crypto.utils.PairingUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.bouncycastle.crypto.CipherParameters;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Weiran Liu on 2016/11/29.
 *
 * Rouselakis-Waters CP-ABE encryption generator.
 */
public class CPABEMHOOCTUpdateGenerator implements PairingEncryptionGenerator, PairingEncapsulationPairGenerator {
    private CPABEMHOOPublicKeySerParameter publicKeyParameter;
    protected CPABECTUpdateGenerationParameter parameter;
 
    private CPABEMHOOHeaderSerParameter ct0;
    private CPABEMHOOUKeySerParameter uk;

    public void init(CipherParameters parameter) {
        this.parameter = (CPABECTUpdateGenerationParameter) parameter;
        this.publicKeyParameter = (CPABEMHOOPublicKeySerParameter) this.parameter.getPublicKeyParameter();
        this.ct0 = (CPABEMHOOHeaderSerParameter)this.parameter.getCT0();
        this.uk = (CPABEMHOOUKeySerParameter)this.parameter.getUK();
    }

    public PairingCipherSerParameter CTUpate() {
        return new CPABEMHOOHeaderSerParameter(
                ct0.getParameters(),
                ct0.getC0(),
                uk.getR1s(),
                uk.getR2s(),
                uk.getR3s(),
                ct0.getC4s(),
                ct0.getC5s() );      
    }
    

	@Override
	public PairingKeyEncapsulationSerPair generateEncryptionPair() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public PairingCipherSerParameter generateCiphertext() {
		// TODO Auto-generated method stub
		return null;
	}

}