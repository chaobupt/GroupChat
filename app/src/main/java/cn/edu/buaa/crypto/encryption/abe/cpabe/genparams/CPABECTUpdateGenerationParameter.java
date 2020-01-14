package cn.edu.buaa.crypto.encryption.abe.cpabe.genparams;

import cn.edu.buaa.crypto.access.AccessControlEngine;
import cn.edu.buaa.crypto.algebra.generators.AsymmetricKeySerPairGenerator;
import cn.edu.buaa.crypto.algebra.genparams.PairingEncryptionGenerationParameter;
import cn.edu.buaa.crypto.algebra.serparams.PairingCipherSerParameter;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeySerParameter;
import cn.edu.buaa.crypto.chameleonhash.ChameleonHasher;
import it.unisa.dia.gas.jpbc.Element;
import org.bouncycastle.crypto.KeyGenerationParameters;

/**
 * Created by Weiran Liu on 2016/11/19.
 *
 * CP-ABE ciphertext generation parameter.
 */
public class CPABECTUpdateGenerationParameter extends PairingEncryptionGenerationParameter {
    private ChameleonHasher chameleonHasher;
    private AsymmetricKeySerPairGenerator chameleonHashKeyPairGenerator;
    private KeyGenerationParameters chameleonHashKeyPairGenerationParameter;
    private PairingCipherSerParameter ct0;
    private PairingCipherSerParameter uk;

    public CPABECTUpdateGenerationParameter(PairingKeySerParameter publicKeyParameter,PairingCipherSerParameter ct0, PairingCipherSerParameter uk) {
        super(publicKeyParameter, null);
        this.ct0 = ct0;
        this.uk = uk;
    }

    public void setChameleonHasher(ChameleonHasher chameleonHasher) {
        this.chameleonHasher = chameleonHasher;
    }

    public void setChameleonHashKeyPairGenerator(AsymmetricKeySerPairGenerator keyPairGenerator) {
        this.chameleonHashKeyPairGenerator = keyPairGenerator;
    }

    public void setChameleonHashKeyPairGenerationParameter(KeyGenerationParameters keyGenerationParameters) {
        this.chameleonHashKeyPairGenerationParameter = keyGenerationParameters;
    }

    public ChameleonHasher getChameleonHasher() {
        return this.chameleonHasher;
    }


    public PairingCipherSerParameter getCT0() {
        return this.ct0;
    }
    
    public PairingCipherSerParameter getUK() {
        return this.uk;
    }

    public AsymmetricKeySerPairGenerator getChameleonHashKeyPairGenerator() {
        return this.chameleonHashKeyPairGenerator;
    }

    public KeyGenerationParameters getChameleonHashKeyPairGenerationParameter() {
        return this.chameleonHashKeyPairGenerationParameter;
    }
}
