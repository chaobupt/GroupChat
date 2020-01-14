package cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams;

import cn.edu.buaa.crypto.algebra.serparams.PairingKeySerParameter;
import cn.edu.buaa.crypto.utils.PairingUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Weiran Liu on 2016/11/29.
 *
 * Rouselakis-Waters CP-ABE secret key parameter.
 */
public class CPABEMHOOSecretKeySerParameter extends PairingKeySerParameter {
    private transient Element K0;
    private final byte[] byteArrayK0;

    private transient Element K1;
    private final byte[] byteArrayK1;
    
    private transient Element D1;
    private final byte[] byteArrayD1;

    private transient Map<String, Element> D2s;
    private final Map<String, byte[]> byteArraysD2s;

    private transient Map<String, Element> D3s;
    private final Map<String, byte[]> byteArraysD3s;

    public CPABEMHOOSecretKeySerParameter(PairingParameters pairingParameters, Element K0, Element K1, Element D1,
                                          Map<String, Element> D2s, Map<String, Element> D3s) {
        super(true, pairingParameters);

        this.K0 = K0.getImmutable();
        this.byteArrayK0 = this.K0.toBytes();

        this.K1 = K1.getImmutable();
        this.byteArrayK1 = this.K1.toBytes();
        
        this.D1 = D1.getImmutable();
        this.byteArrayD1 = this.D1.toBytes();

        this.D2s = new HashMap<String, Element>();
        this.byteArraysD2s = new HashMap<String, byte[]>();
        this.D3s = new HashMap<String, Element>();
        this.byteArraysD3s = new HashMap<String, byte[]>();

        for (String attribute : D2s.keySet()) {
            this.D2s.put(attribute, D2s.get(attribute).duplicate().getImmutable());
            this.byteArraysD2s.put(attribute, D2s.get(attribute).duplicate().getImmutable().toBytes());
            this.D3s.put(attribute, D3s.get(attribute).duplicate().getImmutable());
            this.byteArraysD3s.put(attribute, D3s.get(attribute).duplicate().getImmutable().toBytes());
        }
    }

    public String[] getAttributes() { return this.D2s.keySet().toArray(new String[1]); }

    public Element getK0() { return this.K0.duplicate(); }

    public Element getK1() { return this.K1.duplicate(); }
    
    public Element getD1() { return this.D1.duplicate(); }

    public Map<String, Element> getD2s() { return this.D2s; }

    public Element getD2sAt(String attribute) { return this.D2s.get(attribute).duplicate(); }

    public Map<String, Element> getD3s() { return this.D3s; }

    public Element getD3sAt(String attribute) { return this.D3s.get(attribute).duplicate(); }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof CPABEMHOOSecretKeySerParameter) {
            CPABEMHOOSecretKeySerParameter that = (CPABEMHOOSecretKeySerParameter)anObject;
            //Compare K0
            if (!PairingUtils.isEqualElement(this.K0, that.K0)) {
                return false;
            }
            if (!Arrays.equals(this.byteArrayK0, that.byteArrayK0)) {
                return false;
            }
            //Compare k1
            if (!PairingUtils.isEqualElement(this.K1, that.K1)) {
                return false;
            }
            if (!Arrays.equals(this.byteArrayK1, that.byteArrayK1)) {
                return false;
            }           
            //Compare D1
            if (!PairingUtils.isEqualElement(this.D1, that.D1)) {
                return false;
            }
            if (!Arrays.equals(this.byteArrayD1, that.byteArrayD1)) {
                return false;
            }
            //compare D2s
            if (!this.D2s.equals(that.D2s)) {
                return false;
            }
            if (!PairingUtils.isEqualByteArrayMaps(this.byteArraysD2s, that.byteArraysD2s)) {
                return false;
            }
            //compare D3s
            if (!this.D3s.equals(that.D3s)) {
                return false;
            }
            if (!PairingUtils.isEqualByteArrayMaps(this.byteArraysD3s, that.byteArraysD3s)) {
                return false;
            }
            //Compare Pairing Parameters
            return this.getParameters().toString().equals(that.getParameters().toString());
        }
        return false;
    }

    private void readObject(java.io.ObjectInputStream objectInputStream)
            throws java.io.IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        Pairing pairing = PairingFactory.getPairing(this.getParameters());
        this.K0 = pairing.getG1().newElementFromBytes(this.byteArrayK0);
        this.K1 = pairing.getG1().newElementFromBytes(this.byteArrayK1);
        this.D1 = pairing.getG1().newElementFromBytes(this.byteArrayD1);
        this.D2s = new HashMap<String, Element>();
        this.D3s = new HashMap<String, Element>();
        for (String attribute : this.byteArraysD2s.keySet()) {
            this.D2s.put(attribute, pairing.getG1().newElementFromBytes(this.byteArraysD2s.get(attribute)).getImmutable());
            this.D3s.put(attribute, pairing.getG1().newElementFromBytes(this.byteArraysD3s.get(attribute)).getImmutable());
        }
    }
}