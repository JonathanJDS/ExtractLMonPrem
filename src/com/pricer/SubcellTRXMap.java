
 package com.pricer;
/**
 *
 * @author mohamed.derraz
 */
public class SubcellTRXMap {
   
   private String TRXID;
   private String SCMSUBCELLID;
   private String SCMTRXIDREF;
   private String TRXPORTNUM;
   
   
   
   

   
	public String getTRXPORTNUM() {
	return TRXPORTNUM;
}

public void setTRXPORTNUM(String tRXPORTNUM) {
	TRXPORTNUM = tRXPORTNUM;
}

	public String getSCMTRXIDREF() {
	return SCMTRXIDREF;
}

public void setSCMTRXIDREF(String sCMTRXIDREF) {
	SCMTRXIDREF = sCMTRXIDREF;
}

	public String getTRXID() {
        return TRXID;
    }

    public void setTRXID(String TRXID) {
        this.TRXID = TRXID;
    }

   

    public String getSCMSUBCELLID() {
        return SCMSUBCELLID;
    }

    public void setSCMSUBCELLID(String SCMSUBCELLID) {
        this.SCMSUBCELLID = SCMSUBCELLID;
    }
  

  
    
    
}
