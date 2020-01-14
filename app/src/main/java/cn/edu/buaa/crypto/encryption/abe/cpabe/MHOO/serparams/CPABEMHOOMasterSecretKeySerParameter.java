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
 * Rouselakis-Waters CP-ABE master secret key parameter.
 */
public class CPABEMHOOMasterSecretKeySerParameter extends PairingKeySerParameter {
    private transient Element alpha;
    private final byte[] byteArrayAlpha;
    
    private transient Element beta;
    private final byte[] byteArrayBeta;
    
    private transient Element delta;
    private final byte[] byteArrayDelta;

    public CPABEMHOOMasterSecretKeySerParameter(PairingParameters pairingParameters, Element alpha, Element beta, Element delta) {
        super(true, pairingParameters);
        this.alpha = alpha.getImmutable();
        this.byteArrayAlpha = this.alpha.toBytes();
        
        this.beta = beta.getImmutable();
        this.byteArrayBeta = this.beta.toBytes();
        
        this.delta = delta.getImmutable();
        this.byteArrayDelta = this.delta.toBytes();
    }

	public Element getAlpha() { 
		return this.alpha.duplicate(); 
	}
	
    public Element getBeta() {
		return this.beta.duplicate();
	}

	public Element getDelta() {
		return this.delta.duplicate();
	}


    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof CPABEMHOOMasterSecretKeySerParameter) {
            CPABEMHOOMasterSecretKeySerParameter that = (CPABEMHOOMasterSecretKeySerParameter)anObject;
            //compare alpha
            if (!(PairingUtils.isEqualElement(this.alpha, that.alpha))) {
                return false;
            }
            if (!Arrays.equals(this.byteArrayAlpha, that.byteArrayAlpha)) {
                return false;
            }
            
            //compare beta
            if (!(PairingUtils.isEqualElement(this.beta, that.beta))) {
                return false;
            }
            if (!Arrays.equals(this.byteArrayBeta, that.byteArrayBeta)) {
                return false;
            }
            
            //compare delta
            if (!(PairingUtils.isEqualElement(this.delta, that.delta))) {
                return false;
            }
            if (!Arrays.equals(this.byteArrayDelta, that.byteArrayDelta)) {
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
        this.alpha = pairing.getZr().newElementFromBytes(this.byteArrayAlpha).getImmutable();
        this.beta = pairing.getZr().newElementFromBytes(this.byteArrayBeta).getImmutable();
        this.delta = pairing.getZr().newElementFromBytes(this.byteArrayDelta).getImmutable();
    }
}
