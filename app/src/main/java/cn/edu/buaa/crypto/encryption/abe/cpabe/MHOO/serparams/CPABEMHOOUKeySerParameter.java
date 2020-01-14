package cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cn.edu.buaa.crypto.algebra.serparams.PairingCipherSerParameter;
import cn.edu.buaa.crypto.utils.PairingUtils;

/**
 * Created by Weiran Liu on 16/8/23.
 *
 * Secret Key Parameters for Delerablée IBBE.
 */
public class CPABEMHOOUKeySerParameter extends PairingCipherSerParameter {
	protected final String[] rhos;

	protected transient Map<String, Element> R1s;
	private final byte[][] byteArraysR1s;

	protected transient Map<String, Element> R2s;
	private final byte[][] byteArraysR2s;

	protected transient Map<String, Element> R3s;
	private final byte[][] byteArraysR3s;

	public CPABEMHOOUKeySerParameter(PairingParameters pairingParameters, Map<String, Element> R1s,
			Map<String, Element> R2s, Map<String, Element> R3s) {
		super(pairingParameters);

		this.rhos = R1s.keySet().toArray(new String[1]);

		this.R1s = new HashMap<String, Element>();
		this.byteArraysR1s = new byte[this.rhos.length][];
		this.R2s = new HashMap<String, Element>();
		this.byteArraysR2s = new byte[this.rhos.length][];
		this.R3s = new HashMap<String, Element>();
		this.byteArraysR3s = new byte[this.rhos.length][];

		for (int i = 0; i < this.rhos.length; i++) {
			Element C1 = R1s.get(this.rhos[i]).duplicate().getImmutable();
			this.R1s.put(this.rhos[i], C1);
			this.byteArraysR1s[i] = C1.toBytes();

			Element C2 = R2s.get(this.rhos[i]).duplicate().getImmutable();
			this.R2s.put(this.rhos[i], C2);
			this.byteArraysR2s[i] = C2.toBytes();

			Element C3 = R3s.get(this.rhos[i]).duplicate().getImmutable();
			this.R3s.put(this.rhos[i], C3);
			this.byteArraysR3s[i] = C3.toBytes();

		}
	}

	public Map<String, Element> getR1s() {
		return this.R1s;
	}

	public Element getR1sAt(String rho) {
		return this.R1s.get(rho).duplicate();
	}

	public Map<String, Element> getR2s() {
		return this.R2s;
	}

	public Element getR2sAt(String rho) {
		return this.R2s.get(rho).duplicate();
	}

	public Map<String, Element> getR3s() {
		return this.R3s;
	}

	public Element getR3sAt(String rho) {
		return this.R3s.get(rho).duplicate();
	}

	@Override
	public boolean equals(Object anObject) {
		if (this == anObject) {
			return true;
		}
		if (anObject instanceof CPABEMHOOUKeySerParameter) {
			CPABEMHOOUKeySerParameter that = (CPABEMHOOUKeySerParameter) anObject;

			// Compare R1s
			if (!this.R1s.equals(that.R1s)) {
				return false;
			}
			if (!PairingUtils.isEqualByteArrays(this.byteArraysR1s, that.byteArraysR1s)) {
				return false;
			}
			// Compare R2s
			if (!this.R2s.equals(that.R2s)) {
				return false;
			}
			if (!PairingUtils.isEqualByteArrays(this.byteArraysR2s, that.byteArraysR2s)) {
				return false;
			}
			// Compare R3s
			if (!this.R3s.equals(that.R3s)) {
				return false;
			}
			if (!PairingUtils.isEqualByteArrays(this.byteArraysR3s, that.byteArraysR3s)) {
				return false;
			}

			// Compare Pairing Parameters
			return this.getParameters().toString().equals(that.getParameters().toString());
		}
		return false;
	}

	private void readObject(java.io.ObjectInputStream objectInputStream)
			throws java.io.IOException, ClassNotFoundException {
		objectInputStream.defaultReadObject();
		Pairing pairing = PairingFactory.getPairing(this.getParameters());
		this.R1s = new HashMap<String, Element>();
		this.R2s = new HashMap<String, Element>();
		this.R3s = new HashMap<String, Element>();
		for (int i = 0; i < this.rhos.length; i++) {
			this.R1s.put(this.rhos[i], pairing.getG1().newElementFromBytes(this.byteArraysR1s[i]).getImmutable());
			this.R2s.put(this.rhos[i], pairing.getG1().newElementFromBytes(this.byteArraysR2s[i]).getImmutable());
			this.R3s.put(this.rhos[i], pairing.getG1().newElementFromBytes(this.byteArraysR3s[i]).getImmutable());

		}
	}
}
