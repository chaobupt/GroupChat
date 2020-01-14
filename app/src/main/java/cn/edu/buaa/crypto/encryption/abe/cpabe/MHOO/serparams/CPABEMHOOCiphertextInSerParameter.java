package cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams;

import cn.edu.buaa.crypto.algebra.serparams.PairingCipherSerParameter;
import cn.edu.buaa.crypto.utils.PairingUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.util.ElementUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Weiran Liu on 2017/1/1.
 *
 * Hohenberger-Waters-14 OO-CP-ABE intermediate ciphertext parameter.
 */
public class CPABEMHOOCiphertextInSerParameter extends PairingCipherSerParameter{
	protected final String[] mis;
    private transient PairingCipherSerParameter ct0;  

    private transient Element s0;
    private final byte[] byteArrayS0;

    protected transient Map<String, PairingCipherSerParameter> IT;


    public CPABEMHOOCiphertextInSerParameter(
            PairingParameters parameters, PairingCipherSerParameter ct0,
            Element s0, Map<String, PairingCipherSerParameter> IT) {
        super(parameters);
        this.mis = IT.keySet().toArray(new String[1]);
        this.ct0 = ct0;
        this.s0 = s0.getImmutable();
        this.byteArrayS0 = this.s0.toBytes();
        this.IT = new HashMap<String, PairingCipherSerParameter>();
//        for (int i = 1; i < this.mis.length; i++) {
//        	PairingCipherSerParameter it = IT.get(this.mis[i]);
//            this.IT.put(this.mis[i], it);
//        }
        for(String k:IT.keySet()){
            PairingCipherSerParameter it = IT.get(k);
            this.IT.put(k, it);
        }
 
    }

    public PairingCipherSerParameter getCt0() { return this.ct0; }
    
    public Element getS0() { return this.s0.duplicate(); }

    public Map<String, PairingCipherSerParameter> getIT() { return this.IT; }

    public PairingCipherSerParameter getITAt(String rho) { return this.IT.get(rho); }

  
}
