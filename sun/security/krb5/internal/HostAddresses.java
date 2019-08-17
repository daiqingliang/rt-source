package sun.security.krb5.internal;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Vector;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.internal.ccache.CCacheOutputStream;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class HostAddresses implements Cloneable {
  private static boolean DEBUG = Krb5.DEBUG;
  
  private HostAddress[] addresses = null;
  
  public HostAddresses(HostAddress[] paramArrayOfHostAddress) throws IOException {
    if (paramArrayOfHostAddress != null) {
      this.addresses = new HostAddress[paramArrayOfHostAddress.length];
      for (byte b = 0; b < paramArrayOfHostAddress.length; b++) {
        if (paramArrayOfHostAddress[b] == null)
          throw new IOException("Cannot create a HostAddress"); 
        this.addresses[b] = (HostAddress)paramArrayOfHostAddress[b].clone();
      } 
    } 
  }
  
  public HostAddresses() throws UnknownHostException {
    this.addresses = new HostAddress[1];
    this.addresses[0] = new HostAddress();
  }
  
  private HostAddresses(int paramInt) {}
  
  public HostAddresses(PrincipalName paramPrincipalName) throws UnknownHostException, KrbException {
    String[] arrayOfString = paramPrincipalName.getNameStrings();
    if (paramPrincipalName.getNameType() != 3 || arrayOfString.length < 2)
      throw new KrbException(60, "Bad name"); 
    String str = arrayOfString[1];
    InetAddress[] arrayOfInetAddress = InetAddress.getAllByName(str);
    HostAddress[] arrayOfHostAddress = new HostAddress[arrayOfInetAddress.length];
    for (byte b = 0; b < arrayOfInetAddress.length; b++)
      arrayOfHostAddress[b] = new HostAddress(arrayOfInetAddress[b]); 
    this.addresses = arrayOfHostAddress;
  }
  
  public Object clone() {
    HostAddresses hostAddresses = new HostAddresses(0);
    if (this.addresses != null) {
      hostAddresses.addresses = new HostAddress[this.addresses.length];
      for (byte b = 0; b < this.addresses.length; b++)
        hostAddresses.addresses[b] = (HostAddress)this.addresses[b].clone(); 
    } 
    return hostAddresses;
  }
  
  public boolean inList(HostAddress paramHostAddress) {
    if (this.addresses != null)
      for (byte b = 0; b < this.addresses.length; b++) {
        if (this.addresses[b].equals(paramHostAddress))
          return true; 
      }  
    return false;
  }
  
  public int hashCode() {
    if (this.hashCode == 0) {
      int i = 17;
      if (this.addresses != null)
        for (byte b = 0; b < this.addresses.length; b++)
          i = 37 * i + this.addresses[b].hashCode();  
      this.hashCode = i;
    } 
    return this.hashCode;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof HostAddresses))
      return false; 
    HostAddresses hostAddresses = (HostAddresses)paramObject;
    if ((this.addresses == null && hostAddresses.addresses != null) || (this.addresses != null && hostAddresses.addresses == null))
      return false; 
    if (this.addresses != null && hostAddresses.addresses != null) {
      if (this.addresses.length != hostAddresses.addresses.length)
        return false; 
      for (byte b = 0; b < this.addresses.length; b++) {
        if (!this.addresses[b].equals(hostAddresses.addresses[b]))
          return false; 
      } 
    } 
    return true;
  }
  
  public HostAddresses(DerValue paramDerValue) throws Asn1Exception, IOException {
    Vector vector = new Vector();
    DerValue derValue = null;
    while (paramDerValue.getData().available() > 0) {
      derValue = paramDerValue.getData().getDerValue();
      vector.addElement(new HostAddress(derValue));
    } 
    if (vector.size() > 0) {
      this.addresses = new HostAddress[vector.size()];
      vector.copyInto(this.addresses);
    } 
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    if (this.addresses != null && this.addresses.length > 0)
      for (byte b = 0; b < this.addresses.length; b++)
        derOutputStream1.write(this.addresses[b].asn1Encode());  
    derOutputStream2.write((byte)48, derOutputStream1);
    return derOutputStream2.toByteArray();
  }
  
  public static HostAddresses parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean) throws Asn1Exception, IOException {
    if (paramBoolean && ((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte)
      return null; 
    DerValue derValue1 = paramDerInputStream.getDerValue();
    if (paramByte != (derValue1.getTag() & 0x1F))
      throw new Asn1Exception(906); 
    DerValue derValue2 = derValue1.getData().getDerValue();
    return new HostAddresses(derValue2);
  }
  
  public void writeAddrs(CCacheOutputStream paramCCacheOutputStream) throws IOException {
    paramCCacheOutputStream.write32(this.addresses.length);
    for (byte b = 0; b < this.addresses.length; b++) {
      paramCCacheOutputStream.write16((this.addresses[b]).addrType);
      paramCCacheOutputStream.write32((this.addresses[b]).address.length);
      paramCCacheOutputStream.write((this.addresses[b]).address, 0, (this.addresses[b]).address.length);
    } 
  }
  
  public InetAddress[] getInetAddresses() {
    if (this.addresses == null || this.addresses.length == 0)
      return null; 
    ArrayList arrayList = new ArrayList(this.addresses.length);
    for (byte b = 0; b < this.addresses.length; b++) {
      try {
        if ((this.addresses[b]).addrType == 2 || (this.addresses[b]).addrType == 24)
          arrayList.add(this.addresses[b].getInetAddress()); 
      } catch (UnknownHostException unknownHostException) {
        return null;
      } 
    } 
    InetAddress[] arrayOfInetAddress = new InetAddress[arrayList.size()];
    return (InetAddress[])arrayList.toArray(arrayOfInetAddress);
  }
  
  public static HostAddresses getLocalAddresses() throws IOException {
    String str = null;
    InetAddress[] arrayOfInetAddress = null;
    try {
      InetAddress inetAddress = InetAddress.getLocalHost();
      str = inetAddress.getHostName();
      arrayOfInetAddress = InetAddress.getAllByName(str);
      HostAddress[] arrayOfHostAddress = new HostAddress[arrayOfInetAddress.length];
      byte b;
      for (b = 0; b < arrayOfInetAddress.length; b++)
        arrayOfHostAddress[b] = new HostAddress(arrayOfInetAddress[b]); 
      if (DEBUG) {
        System.out.println(">>> KrbKdcReq local addresses for " + str + " are: ");
        for (b = 0; b < arrayOfInetAddress.length; b++) {
          System.out.println("\n\t" + arrayOfInetAddress[b]);
          if (arrayOfInetAddress[b] instanceof java.net.Inet4Address)
            System.out.println("IPv4 address"); 
          if (arrayOfInetAddress[b] instanceof java.net.Inet6Address)
            System.out.println("IPv6 address"); 
        } 
      } 
      return new HostAddresses(arrayOfHostAddress);
    } catch (Exception exception) {
      throw new IOException(exception.toString());
    } 
  }
  
  public HostAddresses(InetAddress[] paramArrayOfInetAddress) {
    if (paramArrayOfInetAddress == null) {
      this.addresses = null;
      return;
    } 
    this.addresses = new HostAddress[paramArrayOfInetAddress.length];
    for (byte b = 0; b < paramArrayOfInetAddress.length; b++)
      this.addresses[b] = new HostAddress(paramArrayOfInetAddress[b]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\HostAddresses.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */