package cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.generators;

import cn.edu.buaa.crypto.algebra.generators.PairingEncryptionGenerator;
import cn.edu.buaa.crypto.algebra.serparams.PairingCipherSerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.genparams.CPABEIntermediateGenerationParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.genparams.CPABEIntermediateGenerationParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOIntermediateETSerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOIntermediateOTSerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOIntermediateSerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOPublicKeySerParameter;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.bouncycastle.crypto.CipherParameters;

/**
 * Created by Weiran Liu on 2017/1/1.
 *
 * Hohenberger-Waters-14 OO-CP-ABE intermediate ciphertext generator.
 */
public class CPABEMHOOIntermediateGenerator implements PairingEncryptionGenerator {
    private CPABEIntermediateGenerationParameter parameter;
    private CPABEMHOOPublicKeySerParameter publicKeyParameter;
    protected int P;
    protected Element sessionKey;//eki
    
    protected Element si;
    protected Element[] fais;
    protected Element[] xs;
    protected Element[] ts;
    
    protected Element C0;
    protected Element[] C1s;
    protected Element[] C2s;
    protected Element[] C3s;

    public void init(CipherParameters parameter) {
        this.parameter = (CPABEIntermediateGenerationParameter) parameter;
        this.publicKeyParameter = (CPABEMHOOPublicKeySerParameter) this.parameter.getPublicKeyParameter();
    }

    protected void computeEncapsulation(Element s0) {
        this.P = parameter.getP();
        this.fais = new Element[P];
        this.xs = new Element[P];
        this.ts = new Element[P];

        this.C1s = new Element[P];
        this.C2s = new Element[P];
        this.C3s = new Element[P];

        Pairing pairing = PairingFactory.getPairing(publicKeyParameter.getParameters());
        this.si = pairing.getZr().newRandomElement().getImmutable();
        //TODO:
        this.sessionKey = publicKeyParameter.getEggBeta().powZn(si).mul(publicKeyParameter.getEggBeta().powZn(s0)).getImmutable();
        this.C0 = publicKeyParameter.getG().powZn(si).mul(publicKeyParameter.getG().powZn(s0)).getImmutable();
        
        for (int j = 0; j < P; j++) {
            this.fais[j] = pairing.getZr().newRandomElement().getImmutable();
            this.xs[j] = pairing.getZr().newRandomElement().getImmutable();
            this.ts[j] = pairing.getZr().newRandomElement().getImmutable();
       
            this.C1s[j] = publicKeyParameter.getD().powZn(fais[j]).mul(publicKeyParameter.getC().powZn(ts[j])).getImmutable();
            this.C2s[j] = (publicKeyParameter.getB().powZn(xs[j]).mul(publicKeyParameter.getH())).powZn(ts[j].negate()).getImmutable();
            this.C3s[j] = publicKeyParameter.getG().powZn(ts[j]).getImmutable();
        }
    }

    public PairingCipherSerParameter generateCiphertext(Element s0) {
        computeEncapsulation(s0);
        Element C = this.parameter.getMessage().mul(this.sessionKey).getImmutable();    
        return new CPABEMHOOIntermediateSerParameter(publicKeyParameter.getParameters(), 
        		new CPABEMHOOIntermediateOTSerParameter(publicKeyParameter.getParameters(), P, si, fais, xs, ts),
        		new CPABEMHOOIntermediateETSerParameter(publicKeyParameter.getParameters(), P, sessionKey, C, C0, C1s, C2s, C3s));
    }
//
//    public PairingCipherSerParameter generateCiphertext(Element s0) {
//        computeEncapsulation(s0);
//        Element C = this.parameter.getMessage().mul(this.sessionKey).getImmutable();    
//        return new CPABEMHOOIntermediateSerParameter(publicKeyParameter.getParameters(), P, 
//        		si, fais, xs, ts, sessionKey, C, C0, C1s, C2s, C3s);
//    }

	@Override
	public PairingCipherSerParameter generateCiphertext() {
		// TODO Auto-generated method stub
		return null;
	}
}