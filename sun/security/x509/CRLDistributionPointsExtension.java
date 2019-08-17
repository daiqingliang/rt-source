package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class CRLDistributionPointsExtension extends Extension implements CertAttrSet<String> {
  public static final String IDENT = "x509.info.extensions.CRLDistributionPoints";
  
  public static final String NAME = "CRLDistributionPoints";
  
  public static final String POINTS = "points";
  
  private List<DistributionPoint> distributionPoints;
  
  private String extensionName;
  
  public CRLDistributionPointsExtension(List<DistributionPoint> paramList) throws IOException { this(false, paramList); }
  
  public CRLDistributionPointsExtension(boolean paramBoolean, List<DistributionPoint> paramList) throws IOException { this(PKIXExtensions.CRLDistributionPoints_Id, paramBoolean, paramList, "CRLDistributionPoints"); }
  
  protected CRLDistributionPointsExtension(ObjectIdentifier paramObjectIdentifier, boolean paramBoolean, List<DistributionPoint> paramList, String paramString) throws IOException {
    this.extensionId = paramObjectIdentifier;
    this.critical = paramBoolean;
    this.distributionPoints = paramList;
    encodeThis();
    this.extensionName = paramString;
  }
  
  public CRLDistributionPointsExtension(Boolean paramBoolean, Object paramObject) throws IOException { this(PKIXExtensions.CRLDistributionPoints_Id, paramBoolean, paramObject, "CRLDistributionPoints"); }
  
  protected CRLDistributionPointsExtension(ObjectIdentifier paramObjectIdentifier, Boolean paramBoolean, Object paramObject, String paramString) throws IOException {
    this.extensionId = paramObjectIdentifier;
    this.critical = paramBoolean.booleanValue();
    if (!(paramObject instanceof byte[]))
      throw new IOException("Illegal argument type"); 
    this.extensionValue = (byte[])paramObject;
    DerValue derValue = new DerValue(this.extensionValue);
    if (derValue.tag != 48)
      throw new IOException("Invalid encoding for " + paramString + " extension."); 
    this.distributionPoints = new ArrayList();
    while (derValue.data.available() != 0) {
      DerValue derValue1 = derValue.data.getDerValue();
      DistributionPoint distributionPoint = new DistributionPoint(derValue1);
      this.distributionPoints.add(distributionPoint);
    } 
    this.extensionName = paramString;
  }
  
  public String getName() { return this.extensionName; }
  
  public void encode(OutputStream paramOutputStream) throws IOException { encode(paramOutputStream, PKIXExtensions.CRLDistributionPoints_Id, false); }
  
  protected void encode(OutputStream paramOutputStream, ObjectIdentifier paramObjectIdentifier, boolean paramBoolean) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    if (this.extensionValue == null) {
      this.extensionId = paramObjectIdentifier;
      this.critical = paramBoolean;
      encodeThis();
    } 
    encode(derOutputStream);
    paramOutputStream.write(derOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (paramString.equalsIgnoreCase("points")) {
      if (!(paramObject instanceof List))
        throw new IOException("Attribute value should be of type List."); 
      this.distributionPoints = (List)paramObject;
    } else {
      throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:" + this.extensionName + ".");
    } 
    encodeThis();
  }
  
  public List<DistributionPoint> get(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("points"))
      return this.distributionPoints; 
    throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:" + this.extensionName + ".");
  }
  
  public void delete(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("points")) {
      this.distributionPoints = Collections.emptyList();
    } else {
      throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:" + this.extensionName + '.');
    } 
    encodeThis();
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("points");
    return attributeNameEnumeration.elements();
  }
  
  private void encodeThis() throws IOException {
    if (this.distributionPoints.isEmpty()) {
      this.extensionValue = null;
    } else {
      DerOutputStream derOutputStream1 = new DerOutputStream();
      for (DistributionPoint distributionPoint : this.distributionPoints)
        distributionPoint.encode(derOutputStream1); 
      DerOutputStream derOutputStream2 = new DerOutputStream();
      derOutputStream2.write((byte)48, derOutputStream1);
      this.extensionValue = derOutputStream2.toByteArray();
    } 
  }
  
  public String toString() { return super.toString() + this.extensionName + " [\n  " + this.distributionPoints + "]\n"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\CRLDistributionPointsExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */