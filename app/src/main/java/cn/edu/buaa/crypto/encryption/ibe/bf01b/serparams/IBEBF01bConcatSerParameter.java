package cn.edu.buaa.crypto.encryption.ibe.bf01b.serparams;

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
public class IBEBF01bConcatSerParameter extends PairingCipherSerParameter{
    private transient Element ek;
    private final byte[] byteArrayEK;
	
	private PairingCipherSerParameter ot;

    public IBEBF01bConcatSerParameter(PairingParameters parameters, Element ek, PairingCipherSerParameter ot) {
		super(parameters);
		this.ek = ek;
		this.byteArrayEK = this.ek.toBytes();
		this.ot = ot;
	}

	public Element getEk(){
    	return ek;
	}
	public byte[] getByteArrayEK(){return byteArrayEK;}
	public PairingCipherSerParameter getOt() {
		return ot;
	}

}
