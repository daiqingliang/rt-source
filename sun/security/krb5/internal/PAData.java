package sun.security.krb5.internal;

import java.io.IOException;
import sun.misc.HexDumpEncoder;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.internal.util.KerberosString;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class PAData {
  private int pADataType;
  
  private byte[] pADataValue = null;
  
  private static final byte TAG_PATYPE = 1;
  
  private static final byte TAG_PAVALUE = 2;
  
  private PAData() {}
  
  public PAData(int paramInt, byte[] paramArrayOfByte) {
    this.pADataType = paramInt;
    if (paramArrayOfByte != null)
      this.pADataValue = (byte[])paramArrayOfByte.clone(); 
  }
  
  public Object clone() {
    PAData pAData = new PAData();
    pAData.pADataType = this.pADataType;
    if (this.pADataValue != null) {
      pAData.pADataValue = new byte[this.pADataValue.length];
      System.arraycopy(this.pADataValue, 0, pAData.pADataValue, 0, this.pADataValue.length);
    } 
    return pAData;
  }
  
  public PAData(DerValue paramDerValue) throws Asn1Exception, IOException {
    DerValue derValue = null;
    if (paramDerValue.getTag() != 48)
      throw new Asn1Exception(906); 
    derValue = paramDerValue.getData().getDerValue();
    if ((derValue.getTag() & 0x1F) == 1) {
      this.pADataType = derValue.getData().getBigInteger().intValue();
    } else {
      throw new Asn1Exception(906);
    } 
    derValue = paramDerValue.getData().getDerValue();
    if ((derValue.getTag() & 0x1F) == 2)
      this.pADataValue = derValue.getData().getOctetString(); 
    if (paramDerValue.getData().available() > 0)
      throw new Asn1Exception(906); 
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.putInteger(this.pADataType);
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)1), derOutputStream2);
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.putOctetString(this.pADataValue);
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)2), derOutputStream2);
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream1);
    return derOutputStream2.toByteArray();
  }
  
  public int getType() { return this.pADataType; }
  
  public byte[] getValue() throws Asn1Exception, IOException { return (this.pADataValue == null) ? null : (byte[])this.pADataValue.clone(); }
  
  public static int getPreferredEType(PAData[] paramArrayOfPAData, int paramInt) throws IOException, Asn1Exception {
    if (paramArrayOfPAData == null)
      return paramInt; 
    DerValue derValue1 = null;
    DerValue derValue2 = null;
    for (PAData pAData : paramArrayOfPAData) {
      if (pAData.getValue() != null)
        switch (pAData.getType()) {
          case 11:
            derValue1 = new DerValue(pAData.getValue());
            break;
          case 19:
            derValue2 = new DerValue(pAData.getValue());
            break;
        }  
    } 
    if (derValue2 != null)
      while (derValue2.data.available() > 0) {
        DerValue derValue = derValue2.data.getDerValue();
        ETypeInfo2 eTypeInfo2 = new ETypeInfo2(derValue);
        if (eTypeInfo2.getParams() == null)
          return eTypeInfo2.getEType(); 
      }  
    if (derValue1 != null && derValue1.data.available() > 0) {
      DerValue derValue = derValue1.data.getDerValue();
      ETypeInfo eTypeInfo = new ETypeInfo(derValue);
      return eTypeInfo.getEType();
    } 
    return paramInt;
  }
  
  public static SaltAndParams getSaltAndParams(int paramInt, PAData[] paramArrayOfPAData) throws Asn1Exception, IOException {
    if (paramArrayOfPAData == null)
      return null; 
    DerValue derValue1 = null;
    DerValue derValue2 = null;
    String str = null;
    for (PAData pAData : paramArrayOfPAData) {
      if (pAData.getValue() != null)
        switch (pAData.getType()) {
          case 3:
            str = new String(pAData.getValue(), KerberosString.MSNAME ? "UTF8" : "8859_1");
            break;
          case 11:
            derValue1 = new DerValue(pAData.getValue());
            break;
          case 19:
            derValue2 = new DerValue(pAData.getValue());
            break;
        }  
    } 
    if (derValue2 != null)
      while (derValue2.data.available() > 0) {
        DerValue derValue = derValue2.data.getDerValue();
        ETypeInfo2 eTypeInfo2 = new ETypeInfo2(derValue);
        if (eTypeInfo2.getParams() == null && eTypeInfo2.getEType() == paramInt)
          return new SaltAndParams(eTypeInfo2.getSalt(), eTypeInfo2.getParams()); 
      }  
    if (derValue1 != null)
      while (derValue1.data.available() > 0) {
        DerValue derValue = derValue1.data.getDerValue();
        ETypeInfo eTypeInfo = new ETypeInfo(derValue);
        if (eTypeInfo.getEType() == paramInt)
          return new SaltAndParams(eTypeInfo.getSalt(), null); 
      }  
    return (str != null) ? new SaltAndParams(str, null) : null;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(">>>Pre-Authentication Data:\n\t PA-DATA type = ").append(this.pADataType).append('\n');
    switch (this.pADataType) {
      case 2:
        stringBuilder.append("\t PA-ENC-TIMESTAMP");
        break;
      case 11:
        if (this.pADataValue != null)
          try {
            DerValue derValue = new DerValue(this.pADataValue);
            while (derValue.data.available() > 0) {
              DerValue derValue1 = derValue.data.getDerValue();
              ETypeInfo eTypeInfo = new ETypeInfo(derValue1);
              stringBuilder.append("\t PA-ETYPE-INFO etype = ").append(eTypeInfo.getEType()).append(", salt = ").append(eTypeInfo.getSalt()).append('\n');
            } 
          } catch (IOException|Asn1Exception iOException) {
            stringBuilder.append("\t <Unparseable PA-ETYPE-INFO>\n");
          }  
        break;
      case 19:
        if (this.pADataValue != null)
          try {
            DerValue derValue = new DerValue(this.pADataValue);
            while (derValue.data.available() > 0) {
              DerValue derValue1 = derValue.data.getDerValue();
              ETypeInfo2 eTypeInfo2 = new ETypeInfo2(derValue1);
              stringBuilder.append("\t PA-ETYPE-INFO2 etype = ").append(eTypeInfo2.getEType()).append(", salt = ").append(eTypeInfo2.getSalt()).append(", s2kparams = ");
              byte[] arrayOfByte = eTypeInfo2.getParams();
              if (arrayOfByte == null) {
                stringBuilder.append("null\n");
                continue;
              } 
              if (arrayOfByte.length == 0) {
                stringBuilder.append("empty\n");
                continue;
              } 
              stringBuilder.append((new HexDumpEncoder()).encodeBuffer(arrayOfByte));
            } 
          } catch (IOException|Asn1Exception iOException) {
            stringBuilder.append("\t <Unparseable PA-ETYPE-INFO>\n");
          }  
        break;
      case 129:
        stringBuilder.append("\t PA-FOR-USER\n");
        break;
    } 
    return stringBuilder.toString();
  }
  
  public static class SaltAndParams {
    public final String salt;
    
    public final byte[] params;
    
    public SaltAndParams(String param1String, byte[] param1ArrayOfByte) {
      if (param1String != null && param1String.isEmpty())
        param1String = null; 
      this.salt = param1String;
      this.params = param1ArrayOfByte;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\PAData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */