package cn.edu.buaa.crypto.encryption.abe.cpabe.genparams;

import cn.edu.buaa.crypto.algebra.generators.AsymmetricKeySerPairGenerator;
import cn.edu.buaa.crypto.algebra.genparams.PairingEncapsulationGenerationParameter;
import cn.edu.buaa.crypto.algebra.genparams.PairingEncryptionGenerationParameter;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeySerParameter;
import cn.edu.buaa.crypto.chameleonhash.ChameleonHasher;
import it.unisa.dia.gas.jpbc.Element;

import org.bouncycastle.crypto.KeyGenerationParameters;

/**
 * Created by Weiran Liu on 2017/1/1.
 *
 * OO-CP-ABE intermediate ciphertext generation parameter.
 */
public class CPABEIntermediateGenerationParameter extends PairingEncryptionGenerationParameter {
    private int P;
    
    private ChameleonHasher chameleonHasher;
    private AsymmetricKeySerPairGenerator chameleonHashKeyPairGenerator;
    private KeyGenerationParameters chameleonHashKeyGenerationParameter;

    public CPABEIntermediateGenerationParameter(PairingKeySerParameter publicKeyParameter, Element message, int p) {
        super(publicKeyParameter,message);
        this.P = p;
    }

    public void setChameleonHasher(ChameleonHasher chameleonHasher) {
        this.chameleonHasher = chameleonHasher;
    }

    public void setChameleonHashKeyPairGenerator(AsymmetricKeySerPairGenerator chameleonHashKeyPairGenerator) {
        this.chameleonHashKeyPairGenerator = chameleonHashKeyPairGenerator;
    }

    public void setChameleonHashKeyGenerationParameter(KeyGenerationParameters keyGenerationParameter) {
        this.chameleonHashKeyGenerationParameter = keyGenerationParameter;
    }

    public ChameleonHasher getChameleonHasher() {
        return this.chameleonHasher;
    }

    public AsymmetricKeySerPairGenerator getChameleonHashKeyPairGenerator() {
        return this.chameleonHashKeyPairGenerator;
    }

    public KeyGenerationParameters getChameleonHashKeyGenerationParameter() {
        return this.chameleonHashKeyGenerationParameter;
    }

    public int getP() {
        return this.P;
    }
}
