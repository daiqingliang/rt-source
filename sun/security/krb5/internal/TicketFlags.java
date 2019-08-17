package sun.security.krb5.internal;

import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.internal.util.KerberosFlags;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;

public class TicketFlags extends KerberosFlags {
  public TicketFlags() { super(32); }
  
  public TicketFlags(boolean[] paramArrayOfBoolean) throws Asn1Exception {
    super(paramArrayOfBoolean);
    if (paramArrayOfBoolean.length > 32)
      throw new Asn1Exception(502); 
  }
  
  public TicketFlags(int paramInt, byte[] paramArrayOfByte) throws Asn1Exception {
    super(paramInt, paramArrayOfByte);
    if (paramInt > paramArrayOfByte.length * 8 || paramInt > 32)
      throw new Asn1Exception(502); 
  }
  
  public TicketFlags(DerValue paramDerValue) throws IOException, Asn1Exception { this(paramDerValue.getUnalignedBitString(true).toBooleanArray()); }
  
  public static TicketFlags parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean) throws Asn1Exception, IOException {
    if (paramBoolean && ((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte)
      return null; 
    DerValue derValue1 = paramDerInputStream.getDerValue();
    if (paramByte != (derValue1.getTag() & 0x1F))
      throw new Asn1Exception(906); 
    DerValue derValue2 = derValue1.getData().getDerValue();
    return new TicketFlags(derValue2);
  }
  
  public Object clone() {
    try {
      return new TicketFlags(toBooleanArray());
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public boolean match(LoginOptions paramLoginOptions) {
    boolean bool = false;
    if (get(1) == paramLoginOptions.get(1) && get(3) == paramLoginOptions.get(3) && get(8) == paramLoginOptions.get(8))
      bool = true; 
    return bool;
  }
  
  public boolean match(TicketFlags paramTicketFlags) {
    boolean bool = true;
    for (byte b = 0; b <= 31; b++) {
      if (get(b) != paramTicketFlags.get(b))
        return false; 
    } 
    return bool;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    boolean[] arrayOfBoolean = toBooleanArray();
    for (byte b = 0; b < arrayOfBoolean.length; b++) {
      if (arrayOfBoolean[b] == true)
        switch (b) {
          case false:
            stringBuffer.append("RESERVED;");
            break;
          case true:
            stringBuffer.append("FORWARDABLE;");
            break;
          case true:
            stringBuffer.append("FORWARDED;");
            break;
          case true:
            stringBuffer.append("PROXIABLE;");
            break;
          case true:
            stringBuffer.append("PROXY;");
            break;
          case true:
            stringBuffer.append("MAY-POSTDATE;");
            break;
          case true:
            stringBuffer.append("POSTDATED;");
            break;
          case true:
            stringBuffer.append("INVALID;");
            break;
          case true:
            stringBuffer.append("RENEWABLE;");
            break;
          case true:
            stringBuffer.append("INITIAL;");
            break;
          case true:
            stringBuffer.append("PRE-AUTHENT;");
            break;
          case true:
            stringBuffer.append("HW-AUTHENT;");
            break;
        }  
    } 
    String str = stringBuffer.toString();
    if (str.length() > 0)
      str = str.substring(0, str.length() - 1); 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\TicketFlags.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */