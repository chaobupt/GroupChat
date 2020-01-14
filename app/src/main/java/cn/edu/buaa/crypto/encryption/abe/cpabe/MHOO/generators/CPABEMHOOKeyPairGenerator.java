package cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.generators;

import cn.edu.buaa.crypto.algebra.generators.PairingKeyPairGenerator;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeySerPair;
import cn.edu.buaa.crypto.encryption.abe.cpabe.genparams.CPABEKeyPairGenerationParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOMasterSecretKeySerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOPublicKeySerParameter;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.bouncycastle.crypto.KeyGenerationParameters;

/**
 * Created by Weiran Liu on 2016/11/29.
 *
 * Rouselakis-Waters CP-ABE public key / master secret key generator.
 */
public class CPABEMHOOKeyPairGenerator implements PairingKeyPairGenerator {
    protected CPABEKeyPairGenerationParameter parameters;

    public void init(KeyGenerationParameters keyGenerationParameter) {
        this.parameters = (CPABEKeyPairGenerationParameter) keyGenerationParameter;
    }

    public PairingKeySerPair generateKeyPair() {
        Pairing pairing = PairingFactory.getPairing(this.parameters.getPairingParameters());

        Element alpha = pairing.getZr().newRandomElement().getImmutable();
        Element beta= pairing.getZr().newRandomElement().getImmutable();
        Element delta = pairing.getZr().newRandomElement().getImmutable();
        Element g = pairing.getG1().newRandomElement().getImmutable();
        Element h = pairing.getG1().newRandomElement().getImmutable();
        Element b = pairing.getG1().newRandomElement().getImmutable();
        Element c = pairing.getG1().newRandomElement().getImmutable();
        Element d = pairing.getG1().newRandomElement().getImmutable();
        Element eggAlpha = pairing.pairing(g, g).powZn(alpha).getImmutable();
        Element eggBeta = pairing.pairing(g, g).powZn(beta).getImmutable();
        Element gDelta = g.powZn(delta).getImmutable();

        return new PairingKeySerPair(
                new CPABEMHOOPublicKeySerParameter(this.parameters.getPairingParameters(), g, h, b, c, d, eggAlpha, eggBeta, gDelta),
                new CPABEMHOOMasterSecretKeySerParameter(this.parameters.getPairingParameters(), alpha, beta, delta));
    }
}