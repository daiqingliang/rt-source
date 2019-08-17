package sun.security.util;

import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Timestamp;
import java.security.cert.X509Certificate;
import java.util.Date;

public class ConstraintsParameters {
  private final String algorithm;
  
  private final AlgorithmParameters algParams;
  
  private final Key publicKey;
  
  private final X509Certificate cert;
  
  private final boolean trustedMatch;
  
  private final Date pkixDate;
  
  private final Timestamp jarTimestamp;
  
  private final String variant;
  
  public ConstraintsParameters(X509Certificate paramX509Certificate, boolean paramBoolean, Date paramDate, Timestamp paramTimestamp, String paramString) {
    this.cert = paramX509Certificate;
    this.trustedMatch = paramBoolean;
    this.pkixDate = paramDate;
    this.jarTimestamp = paramTimestamp;
    this.variant = (paramString == null) ? "generic" : paramString;
    this.algorithm = null;
    this.algParams = null;
    this.publicKey = null;
  }
  
  public ConstraintsParameters(String paramString1, AlgorithmParameters paramAlgorithmParameters, Key paramKey, String paramString2) {
    this.algorithm = paramString1;
    this.algParams = paramAlgorithmParameters;
    this.publicKey = paramKey;
    this.cert = null;
    this.trustedMatch = false;
    this.pkixDate = null;
    this.jarTimestamp = null;
    this.variant = (paramString2 == null) ? "generic" : paramString2;
  }
  
  public ConstraintsParameters(X509Certificate paramX509Certificate) { this(paramX509Certificate, false, null, null, "generic"); }
  
  public ConstraintsParameters(Timestamp paramTimestamp) { this(null, false, null, paramTimestamp, "generic"); }
  
  public String getAlgorithm() { return this.algorithm; }
  
  public AlgorithmParameters getAlgParams() { return this.algParams; }
  
  public Key getPublicKey() { return this.publicKey; }
  
  public boolean isTrustedMatch() { return this.trustedMatch; }
  
  public X509Certificate getCertificate() { return this.cert; }
  
  public Date getPKIXParamDate() { return this.pkixDate; }
  
  public Timestamp getJARTimestamp() { return this.jarTimestamp; }
  
  public String getVariant() { return this.variant; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\ConstraintsParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */