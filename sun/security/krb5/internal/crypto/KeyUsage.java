package sun.security.krb5.internal.crypto;

public class KeyUsage {
  public static final int KU_UNKNOWN = 0;
  
  public static final int KU_PA_ENC_TS = 1;
  
  public static final int KU_TICKET = 2;
  
  public static final int KU_ENC_AS_REP_PART = 3;
  
  public static final int KU_TGS_REQ_AUTH_DATA_SESSKEY = 4;
  
  public static final int KU_TGS_REQ_AUTH_DATA_SUBKEY = 5;
  
  public static final int KU_PA_TGS_REQ_CKSUM = 6;
  
  public static final int KU_PA_TGS_REQ_AUTHENTICATOR = 7;
  
  public static final int KU_ENC_TGS_REP_PART_SESSKEY = 8;
  
  public static final int KU_ENC_TGS_REP_PART_SUBKEY = 9;
  
  public static final int KU_AUTHENTICATOR_CKSUM = 10;
  
  public static final int KU_AP_REQ_AUTHENTICATOR = 11;
  
  public static final int KU_ENC_AP_REP_PART = 12;
  
  public static final int KU_ENC_KRB_PRIV_PART = 13;
  
  public static final int KU_ENC_KRB_CRED_PART = 14;
  
  public static final int KU_KRB_SAFE_CKSUM = 15;
  
  public static final int KU_PA_FOR_USER_ENC_CKSUM = 17;
  
  public static final int KU_AD_KDC_ISSUED_CKSUM = 19;
  
  public static final boolean isValid(int paramInt) { return (paramInt >= 0); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\crypto\KeyUsage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */