package cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams;

import cn.edu.buaa.crypto.algebra.serparams.PairingKeySerParameter;
import cn.edu.buaa.crypto.utils.PairingUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.Arrays;

/**
 * Created by Weiran Liu on 2016/11/29.
 *
 * Rouselakis-Waters CP-ABE public key parameter.
 */
public class CPABEMHOOPublicKeySerParameter extends PairingKeySerParameter {
    public transient Element g;
    private final byte[] byteArrayG;

    private transient Element h;
    private final byte[] byteArrayH;
    
    private transient Element b;
    private final byte[] byteArrayB;

    private transient Element c;
    private final byte[] byteArrayC;
    
    private transient Element d;
    private final byte[] byteArrayD;

    private transient Element eggAlpha;
    private final byte[] byteArrayEggAlpha;
    
    private transient Element eggBeta;
    private final byte[] byteArrayEggBeta;
    
    private transient Element gDelta;
    private final byte[] byteArrayGDelta;

    public CPABEMHOOPublicKeySerParameter(
            PairingParameters parameters, Element g, Element h, Element b, Element c, Element d, Element eggAlpha, Element eggBeta, Element gDelta) {
        super(false, parameters);

        this.g = g.getImmutable();
        this.byteArrayG = this.g.toBytes();

        this.h = h.getImmutable();
        this.byteArrayH = this.h.toBytes();

        this.b = b.getImmutable();
        this.byteArrayB = this.b.toBytes();

        this.c = c.getImmutable();
        this.byteArrayC = this.c.toBytes();

        this.d = c.getImmutable();
        this.byteArrayD = this.c.toBytes();

        this.eggAlpha = eggAlpha.getImmutable();
        this.byteArrayEggAlpha = this.eggAlpha.toBytes();
        
        this.eggBeta = eggBeta.getImmutable();
        this.byteArrayEggBeta = this.eggBeta.toBytes();
        
        this.gDelta = gDelta.getImmutable();
        this.byteArrayGDelta = this.gDelta.toBytes();
    }

    public Element getG() { return this.g.duplicate(); }

    public Element getH() { return this.h.duplicate(); }

    public Element getB() { return this.b.duplicate(); }

    public Element getC() { return this.c.duplicate(); }

    public Element getD() { return this.d.duplicate(); }

    public Element getEggAlpha() { return this.eggAlpha.duplicate(); }
    
    public Element getEggBeta() { return this.eggBeta.duplicate(); }
    
    public Element getGDelta() { return this.gDelta.duplicate(); }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof CPABEMHOOPublicKeySerParameter) {
            CPABEMHOOPublicKeySerParameter that = (CPABEMHOOPublicKeySerParameter)anObject;
            //Compare g
            if (!PairingUtils.isEqualElement(this.g, that.g)) {
                return false;
            }
            if (!Arrays.equals(this.byteArrayG, that.byteArrayG)) {
                return false;
            }
            //Compare h
            if (!PairingUtils.isEqualElement(this.h, that.h)) {
                return false;
            }
            if (!Arrays.equals(this.byteArrayH, that.byteArrayH)) {
                return false;
            }
            //Compare b
            if (!PairingUtils.isEqualElement(this.b, that.b)) {
                return false;
            }
            if (!Arrays.equals(this.byteArrayB, that.byteArrayB)) {
                return false;
            }
            //Compare c
            if (!PairingUtils.isEqualElement(this.c, that.c)) {
                return false;
            }
            if (!Arrays.equals(this.byteArrayC, that.byteArrayC)) {
                return false;
            }
            //Compare d
            if (!PairingUtils.isEqualElement(this.d, that.d)) {
                return false;
            }
            if (!Arrays.equals(this.byteArrayD, that.byteArrayD)) {
                return false;
            }
            //Compare eggAlpha
            if (!PairingUtils.isEqualElement(this.eggAlpha, that.eggAlpha)) {
                return false;
            }
            if (!Arrays.equals(this.byteArrayEggAlpha, that.byteArrayEggAlpha)) {
                return false;
            }
            //Compare eggBeta
            if (!PairingUtils.isEqualElement(this.eggBeta, that.eggBeta)) {
                return false;
            }
            if (!Arrays.equals(this.byteArrayEggBeta, that.byteArrayEggBeta)) {
                return false;
            }
            //Compare gDelta
            if (!PairingUtils.isEqualElement(this.gDelta, that.gDelta)) {
                return false;
            }
            if (!Arrays.equals(this.byteArrayGDelta, that.byteArrayGDelta)) {
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
        this.g = pairing.getG1().newElementFromBytes(this.byteArrayG).getImmutable();
        this.b = pairing.getG1().newElementFromBytes(this.byteArrayB).getImmutable();
        this.h = pairing.getG1().newElementFromBytes(this.byteArrayH).getImmutable();
        this.c = pairing.getG1().newElementFromBytes(this.byteArrayC).getImmutable();
        this.d = pairing.getG1().newElementFromBytes(this.byteArrayD).getImmutable();
        this.eggAlpha = pairing.getGT().newElementFromBytes(this.byteArrayEggAlpha).getImmutable();
        this.eggBeta = pairing.getGT().newElementFromBytes(this.byteArrayEggBeta).getImmutable();
        this.gDelta = pairing.getG1().newElementFromBytes(this.byteArrayGDelta).getImmutable();
    }
}
