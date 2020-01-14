package cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.generators;

import cn.edu.buaa.crypto.algebra.generators.PairingKeyParameterGenerator;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeySerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.genparams.CPABESecretKeyGenerationParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOMasterSecretKeySerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOPublicKeySerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOSecretKeySerParameter;
import cn.edu.buaa.crypto.utils.PairingUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.bouncycastle.crypto.KeyGenerationParameters;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Weiran Liu on 2016/11/29.
 *
 * Rouselakia-Waters CP-ABE secret key generator.
 */
public class CPABEMHOOSecretKeyGenerator implements PairingKeyParameterGenerator {
    protected CPABESecretKeyGenerationParameter parameter;

    public void init(KeyGenerationParameters keyGenerationParameter) {
        this.parameter = (CPABESecretKeyGenerationParameter)keyGenerationParameter;
    }

    public PairingKeySerParameter generateKey() {
        CPABEMHOOMasterSecretKeySerParameter masterSecretKeyParameter = (CPABEMHOOMasterSecretKeySerParameter)parameter.getMasterSecretKeyParameter();
        CPABEMHOOPublicKeySerParameter publicKeyParameter = (CPABEMHOOPublicKeySerParameter)parameter.getPublicKeyParameter();

        String[] attributes = this.parameter.getAttributes();
        Pairing pairing = PairingFactory.getPairing(publicKeyParameter.getParameters());
        Map<String, Element> D2s = new HashMap<String, Element>();
        Map<String, Element> D3s = new HashMap<String, Element>();
        Element r = pairing.getZr().newRandomElement().getImmutable();
        Element K0 = publicKeyParameter.getG().powZn(masterSecretKeyParameter.getAlpha()).mul(publicKeyParameter.getG().powZn(masterSecretKeyParameter.getBeta())).powZn(masterSecretKeyParameter.getDelta().invert())
        		.mul(publicKeyParameter.getD().powZn(r).powZn(masterSecretKeyParameter.getDelta().invert())).getImmutable();
        Element K1 = publicKeyParameter.getG().powZn(masterSecretKeyParameter.getBeta()).mul(publicKeyParameter.getD().powZn(r)).getImmutable();      
        Element D1 = publicKeyParameter.getG().powZn(r).getImmutable();

        Element D3Temp = publicKeyParameter.getC().powZn(r.negate()).getImmutable();
        for (String attribute : attributes) {
            Element elementAttribute = PairingUtils.MapStringToGroup(pairing, attribute, PairingUtils.PairingGroupType.Zr);
            Element rj = pairing.getZr().newRandomElement().getImmutable();
            D2s.put(attribute, publicKeyParameter.getG().powZn(rj).getImmutable());
            Element D3j = (publicKeyParameter.getB().powZn(elementAttribute).mul(publicKeyParameter.getH())).powZn(rj).getImmutable();
            D3j = D3j.mul(D3Temp).getImmutable();
            D3s.put(attribute, D3j);
        }
        return new CPABEMHOOSecretKeySerParameter(publicKeyParameter.getParameters(), K0, K1, D1, D2s, D3s);
    }
}
