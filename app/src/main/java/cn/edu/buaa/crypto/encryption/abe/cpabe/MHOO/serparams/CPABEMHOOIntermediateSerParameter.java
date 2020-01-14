package cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams;

import cn.edu.buaa.crypto.algebra.serparams.PairingCipherSerParameter;
import cn.edu.buaa.crypto.algebra.serparams.PairingKeySerParameter;
import cn.edu.buaa.crypto.utils.PairingUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.util.ElementUtils;

import java.util.Arrays;

/**
 * Created by Weiran Liu on 2017/1/1.
 *
 * Hohenberger-Waters-14 OO-CP-ABE intermediate ciphertext parameter.
 */
public class CPABEMHOOIntermediateSerParameter extends PairingCipherSerParameter{
	private PairingCipherSerParameter ot;
	private PairingCipherSerParameter et;

    public CPABEMHOOIntermediateSerParameter(PairingParameters parameters, PairingCipherSerParameter ot,
			PairingCipherSerParameter et) {
		super(parameters);
		this.ot = ot;
		this.et = et;
	}

	public PairingCipherSerParameter getOt() {
		return ot;
	}

	public PairingCipherSerParameter getEt() {
		return et;
	}
}
