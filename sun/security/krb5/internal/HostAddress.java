package sun.security.krb5.internal;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class HostAddress implements Cloneable {
  int addrType;
  
  byte[] address = null;
  
  private static InetAddress localInetAddress;
  
  private static final boolean DEBUG = Krb5.DEBUG;
  
  private HostAddress(int paramInt) {}
  
  public Object clone() {
    HostAddress hostAddress = new HostAddress(0);
    hostAddress.addrType = this.addrType;
    if (this.address != null)
      hostAddress.address = (byte[])this.address.clone(); 
    return hostAddress;
  }
  
  public int hashCode() {
    if (this.hashCode == 0) {
      int i = 17;
      i = 37 * i + this.addrType;
      if (this.address != null)
        for (byte b = 0; b < this.address.length; b++)
          i = 37 * i + this.address[b];  
      this.hashCode = i;
    } 
    return this.hashCode;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof HostAddress))
      return false; 
    HostAddress hostAddress = (HostAddress)paramObject;
    if (this.addrType != hostAddress.addrType || (this.address != null && hostAddress.address == null) || (this.address == null && hostAddress.address != null))
      return false; 
    if (this.address != null && hostAddress.address != null) {
      if (this.address.length != hostAddress.address.length)
        return false; 
      for (byte b = 0; b < this.address.length; b++) {
        if (this.address[b] != hostAddress.address[b])
          return false; 
      } 
    } 
    return true;
  }
  
  private static InetAddress getLocalInetAddress() throws UnknownHostException {
    if (localInetAddress == null)
      localInetAddress = InetAddress.getLocalHost(); 
    if (localInetAddress == null)
      throw new UnknownHostException(); 
    return localInetAddress;
  }
  
  public InetAddress getInetAddress() throws UnknownHostException { return (this.addrType == 2 || this.addrType == 24) ? InetAddress.getByAddress(this.address) : null; }
  
  private int getAddrType(InetAddress paramInetAddress) {
    byte b = 0;
    if (paramInetAddress instanceof java.net.Inet4Address) {
      b = 2;
    } else if (paramInetAddress instanceof java.net.Inet6Address) {
      b = 24;
    } 
    return b;
  }
  
  public HostAddress() throws UnknownHostException {
    InetAddress inetAddress = getLocalInetAddress();
    this.addrType = getAddrType(inetAddress);
    this.address = inetAddress.getAddress();
  }
  
  public HostAddress(int paramInt, byte[] paramArrayOfByte) throws KrbApErrException, UnknownHostException {
    switch (paramInt) {
      case 2:
        if (paramArrayOfByte.length != 4)
          throw new KrbApErrException(0, "Invalid Internet address"); 
        break;
      case 5:
        if (paramArrayOfByte.length != 2)
          throw new KrbApErrException(0, "Invalid CHAOSnet address"); 
        break;
      case 6:
        if (paramArrayOfByte.length != 6)
          throw new KrbApErrException(0, "Invalid XNS address"); 
        break;
      case 16:
        if (paramArrayOfByte.length != 3)
          throw new KrbApErrException(0, "Invalid DDP address"); 
        break;
      case 12:
        if (paramArrayOfByte.length != 2)
          throw new KrbApErrException(0, "Invalid DECnet Phase IV address"); 
        break;
      case 24:
        if (paramArrayOfByte.length != 16)
          throw new KrbApErrException(0, "Invalid Internet IPv6 address"); 
        break;
    } 
    this.addrType = paramInt;
    if (paramArrayOfByte != null)
      this.address = (byte[])paramArrayOfByte.clone(); 
    if (DEBUG && (this.addrType == 2 || this.addrType == 24))
      System.out.println("Host address is " + InetAddress.getByAddress(this.address)); 
  }
  
  public HostAddress(InetAddress paramInetAddress) {
    this.addrType = getAddrType(paramInetAddress);
    this.address = paramInetAddress.getAddress();
  }
  
  public HostAddress(DerValue paramDerValue) throws Asn1Exception, IOException {
    DerValue derValue = paramDerValue.getData().getDerValue();
    if ((derValue.getTag() & 0x1F) == 0) {
      this.addrType = derValue.getData().getBigInteger().intValue();
    } else {
      throw new Asn1Exception(906);
    } 
    derValue = paramDerValue.getData().getDerValue();
    if ((derValue.getTag() & 0x1F) == 1) {
      this.address = derValue.getData().getOctetString();
    } else {
      throw new Asn1Exception(906);
    } 
    if (paramDerValue.getData().available() > 0)
      throw new Asn1Exception(906); 
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.putInteger(this.addrType);
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)0), derOutputStream2);
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.putOctetString(this.address);
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)1), derOutputStream2);
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream1);
    return derOutputStream2.toByteArray();
  }
  
  public static HostAddress parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean) throws Asn1Exception, IOException {
    if (paramBoolean && ((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte)
      return null; 
    DerValue derValue1 = paramDerInputStream.getDerValue();
    if (paramByte != (derValue1.getTag() & 0x1F))
      throw new Asn1Exception(906); 
    DerValue derValue2 = derValue1.getData().getDerValue();
    return new HostAddress(derValue2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\HostAddress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */