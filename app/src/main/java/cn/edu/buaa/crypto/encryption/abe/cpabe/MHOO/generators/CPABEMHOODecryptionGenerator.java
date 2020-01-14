package cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.generators;

import cn.edu.buaa.crypto.access.AccessControlEngine;
import cn.edu.buaa.crypto.access.AccessControlParameter;
import cn.edu.buaa.crypto.access.UnsatisfiedAccessControlException;
import cn.edu.buaa.crypto.algebra.generators.PairingDecapsulationGenerator;
import cn.edu.buaa.crypto.algebra.generators.PairingDecryptionGenerator;
import cn.edu.buaa.crypto.encryption.abe.cpabe.genparams.CPABEDecryptionGenerationParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOCiphertextSerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOHeaderSerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOPublicKeySerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOCiphertextSerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOHeaderSerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOPublicKeySerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOSecretKeySerParameter;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Weiran Liu on 2016/11/29.
 *
 * Rouselakis-Waters CP-ABE decryption generator.
 */
public class CPABEMHOODecryptionGenerator implements PairingDecryptionGenerator, PairingDecapsulationGenerator {
    protected CPABEDecryptionGenerationParameter parameter;
    protected Element sessionKey;

    
    public void init0(CipherParameters parameter) {
        this.parameter = (CPABEDecryptionGenerationParameter) parameter;
    }
    
    public void init(CipherParameters params) {
        CPABEDecryptionGenerationParameter oriParameter = (CPABEDecryptionGenerationParameter)params;
        CPABEMHOOPublicKeySerParameter oriPublicKeyParameter = (CPABEMHOOPublicKeySerParameter)oriParameter.getPublicKeyParameter();
        CPABEMHOOHeaderSerParameter oriHeaderParameter = (CPABEMHOOHeaderSerParameter)oriParameter.getCiphertextParameter();

        Map<String, Element> oriC1s = oriHeaderParameter.getC1s();
        Map<String, Element> oriC2s = oriHeaderParameter.getC2s();
        Map<String, Element> oriC4s = oriHeaderParameter.getC4s();
        Map<String, Element> oriC5s = oriHeaderParameter.getC5s();
        Map<String, Element> newC1s = new HashMap<String, Element>();
        Map<String, Element> newC2s = new HashMap<String, Element>();
        for (String attribute : oriC1s.keySet()) {
            Element newC1 = oriC1s.get(attribute).mul(oriPublicKeyParameter.getD().powZn(oriC4s.get(attribute))).getImmutable();
            newC1s.put(attribute, newC1);
        }
        for (String attribute : oriC2s.keySet()) {
            Element newC2 = oriC2s.get(attribute).mul(oriPublicKeyParameter.getB().powZn(oriC5s.get(attribute))).getImmutable();
            newC2s.put(attribute, newC2);
        }
        if (oriHeaderParameter instanceof CPABEMHOOCiphertextSerParameter) {
            CPABEMHOOCiphertextSerParameter oriCiphertextParameter = (CPABEMHOOCiphertextSerParameter)oriHeaderParameter;
            CPABEMHOOCiphertextSerParameter newCiphertextParameter = new CPABEMHOOCiphertextSerParameter(
                    oriCiphertextParameter.getParameters(),
                    oriCiphertextParameter.getC(),
                    oriCiphertextParameter.getC0(),
                    newC1s,
                    newC2s,
                    oriCiphertextParameter.getC3s(),
                    oriC4s,
                    oriC5s
            );
            CPABEDecryptionGenerationParameter resultParameter = new CPABEDecryptionGenerationParameter(
                    oriParameter.getAccessControlEngine(),
                    oriParameter.getPublicKeyParameter(),
                    oriParameter.getSecretKeyParameter(),
                    oriParameter.getAccessPolicy(),
                    oriParameter.getRhos(),
                    newCiphertextParameter);
            init0(resultParameter);
        } else {
            CPABEMHOOHeaderSerParameter newHeaderParameter = new CPABEMHOOHeaderSerParameter(
                    oriHeaderParameter.getParameters(),
                    oriHeaderParameter.getC0(),//少了C
                    newC1s,
                    newC2s,
                    oriHeaderParameter.getC3s(),
                    oriC4s,
                    oriC5s
            );
            CPABEDecryptionGenerationParameter resultParameter = new CPABEDecryptionGenerationParameter(
                    oriParameter.getAccessControlEngine(),
                    oriParameter.getPublicKeyParameter(),
                    oriParameter.getSecretKeyParameter(),
                    oriParameter.getAccessPolicy(),
                    oriParameter.getRhos(),
                    newHeaderParameter);
            init0(resultParameter);
        }
    }

    //计算m0---A0
    protected Element computeDecapsulation() throws InvalidCipherTextException {
        CPABEMHOOPublicKeySerParameter publicKeyParameter = (CPABEMHOOPublicKeySerParameter) this.parameter.getPublicKeyParameter();
        CPABEMHOOSecretKeySerParameter secretKeyParameter = (CPABEMHOOSecretKeySerParameter) this.parameter.getSecretKeyParameter();
        CPABEMHOOHeaderSerParameter ciphertextParameter = (CPABEMHOOHeaderSerParameter) this.parameter.getCiphertextParameter();

        AccessControlEngine accessControlEngine = this.parameter.getAccessControlEngine();
        Pairing pairing = PairingFactory.getPairing(publicKeyParameter.getParameters());
        try {
            AccessControlParameter accessControlParameter
                    = accessControlEngine.generateAccessControl(this.parameter.getAccessPolicy(), this.parameter.getRhos());
            Map<String, Element> omegaElementsMap = accessControlEngine.reconstructOmegas(pairing, secretKeyParameter.getAttributes(), accessControlParameter);

            this.sessionKey = pairing.pairing(ciphertextParameter.getC0(), secretKeyParameter.getK0());
            Element A0= pairing.getGT().newOneElement().getImmutable();
            for (String attribute : omegaElementsMap.keySet()) {
                Element C1 = ciphertextParameter.getC1sAt(attribute);
                Element D1 = secretKeyParameter.getD1();
                Element C2 = ciphertextParameter.getC2sAt(attribute);
                Element D2 = secretKeyParameter.getD2sAt(attribute);
                Element C3 = ciphertextParameter.getC3sAt(attribute);
                Element D3 = secretKeyParameter.getD3sAt(attribute);
                Element lambda = omegaElementsMap.get(attribute);
                A0 = A0.mul(pairing.pairing(C1, D1).mul(pairing.pairing(C2, D2)).mul(pairing.pairing(C3, D3)).powZn(lambda)).getImmutable();
            }
            sessionKey = sessionKey.div(A0).getImmutable();
            return A0;
        } catch (UnsatisfiedAccessControlException e) {
            throw new InvalidCipherTextException("Attributes associated with the ciphertext do not satisfy access policy associated with the secret key.");
        }      
    }
    
    //计算mi---A0.Ai
    protected void computeDecapsulation(Element A0) throws InvalidCipherTextException {
        CPABEMHOOPublicKeySerParameter publicKeyParameter = (CPABEMHOOPublicKeySerParameter) this.parameter.getPublicKeyParameter();
        CPABEMHOOSecretKeySerParameter secretKeyParameter = (CPABEMHOOSecretKeySerParameter) this.parameter.getSecretKeyParameter();
        CPABEMHOOHeaderSerParameter ciphertextParameter = (CPABEMHOOHeaderSerParameter) this.parameter.getCiphertextParameter();
        AccessControlEngine accessControlEngine = this.parameter.getAccessControlEngine();
        Pairing pairing = PairingFactory.getPairing(publicKeyParameter.getParameters());
        try {
            AccessControlParameter accessControlParameter
                    = accessControlEngine.generateAccessControl(this.parameter.getAccessPolicy(), this.parameter.getRhos());
            Map<String, Element> omegaElementsMap = accessControlEngine.reconstructOmegas(pairing, secretKeyParameter.getAttributes(), accessControlParameter);

            this.sessionKey = pairing.pairing(ciphertextParameter.getC0(), secretKeyParameter.getK1());
            Element Ai= pairing.getGT().newOneElement().getImmutable();
            for (String attribute : omegaElementsMap.keySet()) {
                Element C1 = ciphertextParameter.getC1sAt(attribute);
                Element D1 = secretKeyParameter.getD1();
                Element C2 = ciphertextParameter.getC2sAt(attribute);
                Element D2 = secretKeyParameter.getD2sAt(attribute);
                Element C3 = ciphertextParameter.getC3sAt(attribute);
                Element D3 = secretKeyParameter.getD3sAt(attribute);
                Element lambda = omegaElementsMap.get(attribute);
                Ai = Ai.mul(pairing.pairing(C1, D1).mul(pairing.pairing(C2, D2)).mul(pairing.pairing(C3, D3)).powZn(lambda)).getImmutable();
            }
            sessionKey = sessionKey.div(Ai.mul(A0)).getImmutable();
        } catch (UnsatisfiedAccessControlException e) {
            System.out.println("Attributes associated with the ciphertext do not satisfy access policy associated with the secret key.");
            sessionKey = pairing.getGT().newOneElement(); //不满足访问策略则设置会话密钥为 1， 使之解不出相应的人脸， 不抛出异常
           // throw new InvalidCipherTextException("Attributes associated with the ciphertext do not satisfy access policy associated with the secret key.");
        }
    }
    

    public Element recoverMessage() throws InvalidCipherTextException {
        computeDecapsulation();
        CPABEMHOOCiphertextSerParameter ciphertextParameter = (CPABEMHOOCiphertextSerParameter) this.parameter.getCiphertextParameter();
            return ciphertextParameter.getC().div(sessionKey).getImmutable();
    }
    
    public Element recoverMessagei(Element A0) throws InvalidCipherTextException {
        computeDecapsulation(A0);
        CPABEMHOOCiphertextSerParameter ciphertextParameter = (CPABEMHOOCiphertextSerParameter) this.parameter.getCiphertextParameter();
            return ciphertextParameter.getC().div(sessionKey).getImmutable();
    }
    
    public Element recoverA0() throws InvalidCipherTextException {    
        return computeDecapsulation();
    }
    
    public Element recoverEK0() throws InvalidCipherTextException {
        computeDecapsulation();
        return this.sessionKey.duplicate();
    }
    
    public Element recoverEKi(Element A0) throws InvalidCipherTextException {
        computeDecapsulation(A0);
        return this.sessionKey.duplicate();
    }
    
    public byte[] recoverKey() throws InvalidCipherTextException {
        computeDecapsulation();
        return this.sessionKey.toBytes();
    }
}
