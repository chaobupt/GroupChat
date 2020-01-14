package cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams;

import cn.edu.buaa.crypto.utils.PairingUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by Weiran Liu on 2016/11/29.
 *
 * Rouselakis-Waters CP-ABE ciphertext parameter.
 */
public class CPABEMHOOCiphertextSerParameter extends CPABEMHOOHeaderSerParameter {
    private transient Element C;
    private final byte[] byteArrayC;

    public CPABEMHOOCiphertextSerParameter(PairingParameters pairingParameters, Element C, Element C0,
            Map<String, Element> C1s, Map<String, Element> C2s, Map<String, Element> C3s,
            Map<String, Element> C4s, Map<String, Element> C5s) {
    	super(pairingParameters, C0, C1s, C2s, C3s, C4s, C5s);

        this.C = C.getImmutable();
        this.byteArrayC = this.C.toBytes();
    }

    public CPABEMHOOCiphertextSerParameter(PairingParameters pairingParameters, byte[] C, Element C0,
                                           Map<String, Element> C1s, Map<String, Element> C2s, Map<String, Element> C3s,
                                           Map<String, Element> C4s, Map<String, Element> C5s) {
        super(pairingParameters, C0, C1s, C2s, C3s, C4s, C5s);
        this.byteArrayC = C;
    }

    public Element getC() { return this.C.duplicate(); }

    public byte[] getByteArrayC() {
        return byteArrayC;
    }
    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof CPABEMHOOCiphertextSerParameter) {
            CPABEMHOOCiphertextSerParameter that = (CPABEMHOOCiphertextSerParameter) anObject;
            return PairingUtils.isEqualElement(this.C, that.C)
                    && Arrays.equals(this.byteArrayC, that.byteArrayC)
                    && super.equals(anObject);
        }
        return false;
    }

    private void readObject(java.io.ObjectInputStream objectInputStream)
            throws java.io.IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        Pairing pairing = PairingFactory.getPairing(this.getParameters());
        this.C = pairing.getGT().newElementFromBytes(this.byteArrayC).getImmutable();
    }
}