package cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.generators;

import cn.edu.buaa.crypto.access.AccessControlEngine;
import cn.edu.buaa.crypto.access.AccessControlParameter;
import cn.edu.buaa.crypto.algebra.generators.PairingEncapsulationPairGenerator;
import cn.edu.buaa.crypto.algebra.generators.PairingEncryptionGenerator;
import cn.edu.buaa.crypto.algebra.serparams.PairingCipherSerParameter;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeyEncapsulationSerPair;
import cn.edu.buaa.crypto.encryption.abe.cpabe.genparams.CPABEEncryptionGenerationParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOCiphertextSerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOHeaderSerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOIntermediateETSerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOIntermediateOTSerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOIntermediateSerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOPublicKeySerParameter;
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
public class CPABEMHOOEncryptionGenerator implements PairingEncryptionGenerator, PairingEncapsulationPairGenerator {
    private CPABEMHOOPublicKeySerParameter publicKeyParameter;
    protected CPABEEncryptionGenerationParameter parameter;
    protected AccessControlParameter accessControlParameter;
    private CPABEMHOOIntermediateSerParameter intermediate;
    protected Element s;
    protected Element sessionKey;
    protected Element C;
    protected Element C0;
    protected Map<String, Element> C1s;
    protected Map<String, Element> C2s;
    protected Map<String, Element> C3s;
    protected Map<String, Element> C4s;
    protected Map<String, Element> C5s;
    
    public void init(CipherParameters parameter) {
        this.parameter = (CPABEEncryptionGenerationParameter) parameter;
        this.publicKeyParameter = (CPABEMHOOPublicKeySerParameter) this.parameter.getPublicKeyParameter();
        if (this.parameter.isIntermediateGeneration()) {
            this.intermediate = (CPABEMHOOIntermediateSerParameter)this.parameter.getIntermediate();
        }
    }

    protected void computeEncapsulation(Element s0) {
        Pairing pairing = PairingFactory.getPairing(publicKeyParameter.getParameters());
        if (this.parameter.isIntermediateGeneration()) {//是中间密文
            int[][] accessPolicy = this.parameter.getAccessPolicy();
            String[] rhos = this.parameter.getRhos();
            AccessControlEngine accessControlEngine = this.parameter.getAccessControlEngine();
            this.accessControlParameter = accessControlEngine.generateAccessControl(accessPolicy, rhos);
            
            CPABEMHOOIntermediateOTSerParameter ot = ( CPABEMHOOIntermediateOTSerParameter)this.intermediate.getOt();
            CPABEMHOOIntermediateETSerParameter et = (CPABEMHOOIntermediateETSerParameter)this.intermediate.getEt();
            //s - eki
           
            //this.s = this.intermediate.getSi().getImmutable();        
            //this.sessionKey = this.intermediate.getSessionKey().getImmutable();     
            //this.C = this.intermediate.getC().getImmutable();
            //this.C0 = this.intermediate.getC0().getImmutable();
            this.s = ot.getSi().getImmutable();
            this.sessionKey = et.getSessionKey().getImmutable();
            this.C = et.getC().getImmutable();
            this.C0 = et.getC0().getImmutable();
            
            Map<String, Element> lambdas = accessControlEngine.secretSharing(pairing, s, accessControlParameter);
            if (lambdas.keySet().size() > ot.getP()) {
                throw new IllegalArgumentException("Intermediate size smaller than the number of rhos");
            }
            this.C1s = new HashMap<String, Element>();
            this.C2s = new HashMap<String, Element>();
            this.C3s = new HashMap<String, Element>();
            this.C4s = new HashMap<String, Element>();
            this.C5s = new HashMap<String, Element>();
            int index = 0;
            for (String rho : lambdas.keySet()) {
                Element elementRho = PairingUtils.MapStringToGroup(pairing, rho, PairingUtils.PairingGroupType.Zr);
                C1s.put(rho, et.getC1sAt(index).getImmutable());
                C2s.put(rho, et.getC2sAt(index).getImmutable());
                C3s.put(rho, et.getC3sAt(index).getImmutable());
                C4s.put(rho, lambdas.get(rho).sub(ot.getFaisAt(index)).getImmutable());
                C5s.put(rho, ot.getTsAt(index).mulZn(ot.getXsAt(index).sub(elementRho)).getImmutable());
                index++;
            }            
        } else {//不是中间密文
            int[][] accessPolicy = this.parameter.getAccessPolicy();
            String[] rhos = this.parameter.getRhos();
            AccessControlEngine accessControlEngine = this.parameter.getAccessControlEngine();
            this.accessControlParameter = accessControlEngine.generateAccessControl(accessPolicy, rhos);
            //s0 - ek0 
            this.s = s0;
            this.sessionKey = publicKeyParameter.getEggAlpha().mul(publicKeyParameter.getEggBeta()).powZn(s).getImmutable();   
            this.C0 = publicKeyParameter.getGDelta().powZn(s).getImmutable();

            Map<String, Element> lambdas = accessControlEngine.secretSharing(pairing, s, accessControlParameter);
            this.C1s = new HashMap<String, Element>();
            this.C2s = new HashMap<String, Element>();
            this.C3s = new HashMap<String, Element>();
            for (String rho : lambdas.keySet()) {
                Element elementRho = PairingUtils.MapStringToGroup(pairing, rho, PairingUtils.PairingGroupType.Zr);
                Element tj = pairing.getZr().newRandomElement().getImmutable();
                C1s.put(rho, publicKeyParameter.getD().powZn(lambdas.get(rho)).mul(publicKeyParameter.getC().powZn(tj)).getImmutable());
                C2s.put(rho, publicKeyParameter.getB().powZn(elementRho).mul(publicKeyParameter.getH()).powZn(tj.negate()).getImmutable());
                C3s.put(rho, publicKeyParameter.getG().powZn(tj).getImmutable());
            }
            this.C4s = new HashMap<String, Element>();
            this.C5s = new HashMap<String, Element>();
            for (String rho : this.C1s.keySet()) {
                C4s.put(rho, pairing.getZr().newZeroElement().getImmutable());
                C5s.put(rho, pairing.getZr().newZeroElement().getImmutable());
            }
        }
    }

    public PairingCipherSerParameter generateCiphertext(Element s) {
        computeEncapsulation(s);
        if (!this.parameter.isIntermediateGeneration()) {//不是中间密文 m0
        	Element C = this.sessionKey.mul(this.parameter.getMessage()).getImmutable();
            return new CPABEMHOOCiphertextSerParameter(publicKeyParameter.getParameters(), C, C0, C1s, C2s, C3s, C4s, C5s);     	
        }else {//是中间密文 mi
        	return new CPABEMHOOCiphertextSerParameter(publicKeyParameter.getParameters(), C, C0, C1s, C2s, C3s, C4s, C5s);
        }       
    }
    

    public PairingKeyEncapsulationSerPair generateEncryptionPair(Element s) {
        computeEncapsulation(s);
        return new PairingKeyEncapsulationSerPair(
                this.sessionKey.toBytes(),
                new CPABEMHOOHeaderSerParameter(publicKeyParameter.getParameters(), C0, C1s, C2s, C3s, C4s, C5s)
        );
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