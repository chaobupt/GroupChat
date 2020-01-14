package cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams;

import cn.edu.buaa.crypto.algebra.serparams.PairingCipherSerParameter;
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
public class CPABEMHOOIntermediateOTSerParameter extends PairingCipherSerParameter{
    private final int P;

    private transient Element si;
    private final byte[] byteArrayS;

    private transient Element[] fais;
    private final byte[][] byteArraysFais;

    private transient Element[] ts;
    private final byte[][] byteArraysTs;

    private transient Element[] xs;
    private final byte[][] byteArraysXs;


    public CPABEMHOOIntermediateOTSerParameter(
            PairingParameters parameters, int P,
            Element si, Element[] fais, Element[] xs, Element[] ts) {
        super(parameters);
        this.P = P;

        this.si = si.getImmutable();
        this.byteArrayS = this.si.toBytes();

        this.fais = ElementUtils.cloneImmutable(fais);
        this.byteArraysFais = PairingUtils.GetElementArrayBytes(this.fais);

        this.ts = ElementUtils.cloneImmutable(ts);
        this.byteArraysTs = PairingUtils.GetElementArrayBytes(this.ts);

        this.xs = ElementUtils.cloneImmutable(xs);
        this.byteArraysXs = PairingUtils.GetElementArrayBytes(this.xs);
    }

    public int getP() { return this.P; }
    
    public Element getSi() { return this.si.duplicate(); }

    public Element[] getFais() { return ElementUtils.duplicate(fais); }

    public Element getFaisAt(int index) { return this.fais[index].duplicate(); }

    public Element[] getXs() { return ElementUtils.duplicate(this.xs); }

    public Element getXsAt(int index) { return this.xs[index].duplicate(); }
    
    public Element[] getTs() { return ElementUtils.duplicate(this.ts); }

    public Element getTsAt(int index) { return this.ts[index].duplicate(); }
   

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof CPABEMHOOIntermediateOTSerParameter) {
            CPABEMHOOIntermediateOTSerParameter that = (CPABEMHOOIntermediateOTSerParameter)anObject;
            //compare P
            if (this.P != that.P) {
                return false;
            }
            //compare si
            if (!PairingUtils.isEqualElement(this.si, that.si)) {
                return false;
            }
            if (!Arrays.equals(this.byteArrayS, that.byteArrayS)) {
                return false;
            }
            //compare fais
            if (!Arrays.equals(this.fais, that.fais)) {
                return false;
            }
            if (!PairingUtils.isEqualByteArrays(this.byteArraysFais, that.byteArraysFais)) {
                return false;
            }
            //compare xs
            if (!Arrays.equals(this.xs, that.xs)) {
                return false;
            }
            if (!PairingUtils.isEqualByteArrays(this.byteArraysXs, that.byteArraysXs)) {
                return false;
            }
            //compare ts
            if (!Arrays.equals(this.ts, that.ts)) {
                return false;
            }
            if (!PairingUtils.isEqualByteArrays(this.byteArraysTs, that.byteArraysTs)) {
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
        this.si = pairing.getZr().newElementFromBytes(this.byteArrayS).getImmutable();       
        this.fais = PairingUtils.GetElementArrayFromBytes(pairing, this.byteArraysFais, PairingUtils.PairingGroupType.Zr);
        this.ts = PairingUtils.GetElementArrayFromBytes(pairing, this.byteArraysTs, PairingUtils.PairingGroupType.Zr);
        this.xs = PairingUtils.GetElementArrayFromBytes(pairing, this.byteArraysXs, PairingUtils.PairingGroupType.Zr);
    }
}
