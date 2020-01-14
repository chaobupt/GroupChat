package cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.generators;

import cn.edu.buaa.crypto.access.AccessControlEngine;
import cn.edu.buaa.crypto.access.AccessControlParameter;
import cn.edu.buaa.crypto.algebra.generators.PairingEncryptionGenerator;
import cn.edu.buaa.crypto.algebra.serparams.PairingCipherSerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.genparams.CPABEEncryptionGenerationParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.genparams.CPABEIntermediateGenerationParameter;
import cn.edu.buaa.crypto.utils.PairingUtils;
import cn.edu.buaa.crypto.encryption.abe.cpabe.genparams.CPABEIntermediateGenerationParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOCiphertextSerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOIntermediateSerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOPublicKeySerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOUKeySerParameter;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.crypto.CipherParameters;

/**
 * Created by Weiran Liu on 2017/1/1.
 *
 * Hohenberger-Waters-14 OO-CP-ABE intermediate ciphertext generator.
 */
public class CPABEMHOOUKeyGenerator implements PairingEncryptionGenerator {
	private CPABEMHOOPublicKeySerParameter publicKeyParameter;
	protected CPABEEncryptionGenerationParameter parameter;
	protected AccessControlParameter accessControlParameter;

	protected Element s;
	protected Map<String, Element> R1s;
	protected Map<String, Element> R2s;
	protected Map<String, Element> R3s;

	public void init(CipherParameters parameter) {
		this.parameter = (CPABEEncryptionGenerationParameter) parameter;
		this.publicKeyParameter = (CPABEMHOOPublicKeySerParameter) this.parameter.getPublicKeyParameter();
	}

	protected void computeEncapsulation(Element s) {
		Pairing pairing = PairingFactory.getPairing(publicKeyParameter.getParameters());

		int[][] accessPolicy = this.parameter.getAccessPolicy();
		String[] rhos = this.parameter.getRhos();
		AccessControlEngine accessControlEngine = this.parameter.getAccessControlEngine();
		this.accessControlParameter = accessControlEngine.generateAccessControl(accessPolicy, rhos);
		// s0
		this.s = s;

		Map<String, Element> lambdas = accessControlEngine.secretSharing(pairing, s, accessControlParameter);
		this.R1s = new HashMap<String, Element>();
		this.R2s = new HashMap<String, Element>();
		this.R3s = new HashMap<String, Element>();
		for (String rho : lambdas.keySet()) {
			Element elementRho = PairingUtils.MapStringToGroup(pairing, rho, PairingUtils.PairingGroupType.Zr);
			Element tj = pairing.getZr().newRandomElement().getImmutable();
			R1s.put(rho, publicKeyParameter.getD().powZn(lambdas.get(rho)).mul(publicKeyParameter.getC().powZn(tj))
					.getImmutable());
			R2s.put(rho, publicKeyParameter.getB().powZn(elementRho).mul(publicKeyParameter.getH()).powZn(tj.negate())
					.getImmutable());
			R3s.put(rho, publicKeyParameter.getG().powZn(tj).getImmutable());
		}
	}

	public PairingCipherSerParameter UKeyGen(Element s) {
		computeEncapsulation(s);
		return new CPABEMHOOUKeySerParameter(publicKeyParameter.getParameters(), R1s, R2s, R3s);
	}

	@Override
	public PairingCipherSerParameter generateCiphertext() {
		// TODO Auto-generated method stub
		return null;
	}
}