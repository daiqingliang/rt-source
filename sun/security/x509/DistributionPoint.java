package sun.security.x509;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import sun.security.util.BitArray;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class DistributionPoint {
  public static final int KEY_COMPROMISE = 1;
  
  public static final int CA_COMPROMISE = 2;
  
  public static final int AFFILIATION_CHANGED = 3;
  
  public static final int SUPERSEDED = 4;
  
  public static final int CESSATION_OF_OPERATION = 5;
  
  public static final int CERTIFICATE_HOLD = 6;
  
  public static final int PRIVILEGE_WITHDRAWN = 7;
  
  public static final int AA_COMPROMISE = 8;
  
  private static final String[] REASON_STRINGS = { null, "key compromise", "CA compromise", "affiliation changed", "superseded", "cessation of operation", "certificate hold", "privilege withdrawn", "AA compromise" };
  
  private static final byte TAG_DIST_PT = 0;
  
  private static final byte TAG_REASONS = 1;
  
  private static final byte TAG_ISSUER = 2;
  
  private static final byte TAG_FULL_NAME = 0;
  
  private static final byte TAG_REL_NAME = 1;
  
  private GeneralNames fullName;
  
  private RDN relativeName;
  
  private boolean[] reasonFlags;
  
  private GeneralNames crlIssuer;
  
  public DistributionPoint(GeneralNames paramGeneralNames1, boolean[] paramArrayOfBoolean, GeneralNames paramGeneralNames2) {
    if (paramGeneralNames1 == null && paramGeneralNames2 == null)
      throw new IllegalArgumentException("fullName and crlIssuer may not both be null"); 
    this.fullName = paramGeneralNames1;
    this.reasonFlags = paramArrayOfBoolean;
    this.crlIssuer = paramGeneralNames2;
  }
  
  public DistributionPoint(RDN paramRDN, boolean[] paramArrayOfBoolean, GeneralNames paramGeneralNames) {
    if (paramRDN == null && paramGeneralNames == null)
      throw new IllegalArgumentException("relativeName and crlIssuer may not both be null"); 
    this.relativeName = paramRDN;
    this.reasonFlags = paramArrayOfBoolean;
    this.crlIssuer = paramGeneralNames;
  }
  
  public DistributionPoint(DerValue paramDerValue) throws IOException {
    if (paramDerValue.tag != 48)
      throw new IOException("Invalid encoding of DistributionPoint."); 
    while (paramDerValue.data != null && paramDerValue.data.available() != 0) {
      DerValue derValue = paramDerValue.data.getDerValue();
      if (derValue.isContextSpecific((byte)0) && derValue.isConstructed()) {
        if (this.fullName != null || this.relativeName != null)
          throw new IOException("Duplicate DistributionPointName in DistributionPoint."); 
        DerValue derValue1 = derValue.data.getDerValue();
        if (derValue1.isContextSpecific((byte)0) && derValue1.isConstructed()) {
          derValue1.resetTag((byte)48);
          this.fullName = new GeneralNames(derValue1);
          continue;
        } 
        if (derValue1.isContextSpecific((byte)1) && derValue1.isConstructed()) {
          derValue1.resetTag((byte)49);
          this.relativeName = new RDN(derValue1);
          continue;
        } 
        throw new IOException("Invalid DistributionPointName in DistributionPoint");
      } 
      if (derValue.isContextSpecific((byte)1) && !derValue.isConstructed()) {
        if (this.reasonFlags != null)
          throw new IOException("Duplicate Reasons in DistributionPoint."); 
        derValue.resetTag((byte)3);
        this.reasonFlags = derValue.getUnalignedBitString().toBooleanArray();
        continue;
      } 
      if (derValue.isContextSpecific((byte)2) && derValue.isConstructed()) {
        if (this.crlIssuer != null)
          throw new IOException("Duplicate CRLIssuer in DistributionPoint."); 
        derValue.resetTag((byte)48);
        this.crlIssuer = new GeneralNames(derValue);
        continue;
      } 
      throw new IOException("Invalid encoding of DistributionPoint.");
    } 
    if (this.crlIssuer == null && this.fullName == null && this.relativeName == null)
      throw new IOException("One of fullName, relativeName,  and crlIssuer has to be set"); 
  }
  
  public GeneralNames getFullName() { return this.fullName; }
  
  public RDN getRelativeName() { return this.relativeName; }
  
  public boolean[] getReasonFlags() { return this.reasonFlags; }
  
  public GeneralNames getCRLIssuer() { return this.crlIssuer; }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    if (this.fullName != null || this.relativeName != null) {
      DerOutputStream derOutputStream1 = new DerOutputStream();
      if (this.fullName != null) {
        DerOutputStream derOutputStream2 = new DerOutputStream();
        this.fullName.encode(derOutputStream2);
        derOutputStream1.writeImplicit(DerValue.createTag(-128, true, (byte)0), derOutputStream2);
      } else if (this.relativeName != null) {
        DerOutputStream derOutputStream2 = new DerOutputStream();
        this.relativeName.encode(derOutputStream2);
        derOutputStream1.writeImplicit(DerValue.createTag(-128, true, (byte)1), derOutputStream2);
      } 
      derOutputStream.write(DerValue.createTag(-128, true, (byte)0), derOutputStream1);
    } 
    if (this.reasonFlags != null) {
      DerOutputStream derOutputStream1 = new DerOutputStream();
      BitArray bitArray = new BitArray(this.reasonFlags);
      derOutputStream1.putTruncatedUnalignedBitString(bitArray);
      derOutputStream.writeImplicit(DerValue.createTag(-128, false, (byte)1), derOutputStream1);
    } 
    if (this.crlIssuer != null) {
      DerOutputStream derOutputStream1 = new DerOutputStream();
      this.crlIssuer.encode(derOutputStream1);
      derOutputStream.writeImplicit(DerValue.createTag(-128, true, (byte)2), derOutputStream1);
    } 
    paramDerOutputStream.write((byte)48, derOutputStream);
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof DistributionPoint))
      return false; 
    DistributionPoint distributionPoint = (DistributionPoint)paramObject;
    return (Objects.equals(this.fullName, distributionPoint.fullName) && Objects.equals(this.relativeName, distributionPoint.relativeName) && Objects.equals(this.crlIssuer, distributionPoint.crlIssuer) && Arrays.equals(this.reasonFlags, distributionPoint.reasonFlags));
  }
  
  public int hashCode() {
    int i = this.hashCode;
    if (i == 0) {
      i = 1;
      if (this.fullName != null)
        i += this.fullName.hashCode(); 
      if (this.relativeName != null)
        i += this.relativeName.hashCode(); 
      if (this.crlIssuer != null)
        i += this.crlIssuer.hashCode(); 
      if (this.reasonFlags != null)
        for (int j = 0; j < this.reasonFlags.length; j++) {
          if (this.reasonFlags[j])
            i += j; 
        }  
      this.hashCode = i;
    } 
    return i;
  }
  
  private static String reasonToString(int paramInt) { return (paramInt > 0 && paramInt < REASON_STRINGS.length) ? REASON_STRINGS[paramInt] : ("Unknown reason " + paramInt); }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    if (this.fullName != null)
      stringBuilder.append("DistributionPoint:\n     " + this.fullName + "\n"); 
    if (this.relativeName != null)
      stringBuilder.append("DistributionPoint:\n     " + this.relativeName + "\n"); 
    if (this.reasonFlags != null) {
      stringBuilder.append("   ReasonFlags:\n");
      for (byte b = 0; b < this.reasonFlags.length; b++) {
        if (this.reasonFlags[b])
          stringBuilder.append("    " + reasonToString(b) + "\n"); 
      } 
    } 
    if (this.crlIssuer != null)
      stringBuilder.append("   CRLIssuer:" + this.crlIssuer + "\n"); 
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\DistributionPoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */