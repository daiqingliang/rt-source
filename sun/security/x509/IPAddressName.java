package sun.security.x509;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import sun.misc.HexDumpEncoder;
import sun.security.util.BitArray;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class IPAddressName implements GeneralNameInterface {
  private byte[] address;
  
  private boolean isIPv4;
  
  private String name;
  
  private static final int MASKSIZE = 16;
  
  public IPAddressName(DerValue paramDerValue) throws IOException { this(paramDerValue.getOctetString()); }
  
  public IPAddressName(byte[] paramArrayOfByte) throws IOException {
    if (paramArrayOfByte.length == 4 || paramArrayOfByte.length == 8) {
      this.isIPv4 = true;
    } else if (paramArrayOfByte.length == 16 || paramArrayOfByte.length == 32) {
      this.isIPv4 = false;
    } else {
      throw new IOException("Invalid IPAddressName");
    } 
    this.address = paramArrayOfByte;
  }
  
  public IPAddressName(String paramString) throws IOException {
    if (paramString == null || paramString.length() == 0)
      throw new IOException("IPAddress cannot be null or empty"); 
    if (paramString.charAt(paramString.length() - 1) == '/')
      throw new IOException("Invalid IPAddress: " + paramString); 
    if (paramString.indexOf(':') >= 0) {
      parseIPv6(paramString);
      this.isIPv4 = false;
    } else if (paramString.indexOf('.') >= 0) {
      parseIPv4(paramString);
      this.isIPv4 = true;
    } else {
      throw new IOException("Invalid IPAddress: " + paramString);
    } 
  }
  
  private void parseIPv4(String paramString) throws IOException {
    int i = paramString.indexOf('/');
    if (i == -1) {
      this.address = InetAddress.getByName(paramString).getAddress();
    } else {
      this.address = new byte[8];
      byte[] arrayOfByte1 = InetAddress.getByName(paramString.substring(i + 1)).getAddress();
      byte[] arrayOfByte2 = InetAddress.getByName(paramString.substring(0, i)).getAddress();
      System.arraycopy(arrayOfByte2, 0, this.address, 0, 4);
      System.arraycopy(arrayOfByte1, 0, this.address, 4, 4);
    } 
  }
  
  private void parseIPv6(String paramString) throws IOException {
    int i = paramString.indexOf('/');
    if (i == -1) {
      this.address = InetAddress.getByName(paramString).getAddress();
    } else {
      this.address = new byte[32];
      byte[] arrayOfByte1 = InetAddress.getByName(paramString.substring(0, i)).getAddress();
      System.arraycopy(arrayOfByte1, 0, this.address, 0, 16);
      int j = Integer.parseInt(paramString.substring(i + 1));
      if (j < 0 || j > 128)
        throw new IOException("IPv6Address prefix length (" + j + ") in out of valid range [0,128]"); 
      BitArray bitArray = new BitArray(128);
      for (byte b1 = 0; b1 < j; b1++)
        bitArray.set(b1, true); 
      byte[] arrayOfByte2 = bitArray.toByteArray();
      for (byte b2 = 0; b2 < 16; b2++)
        this.address[16 + b2] = arrayOfByte2[b2]; 
    } 
  }
  
  public int getType() { return 7; }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException { paramDerOutputStream.putOctetString(this.address); }
  
  public String toString() {
    try {
      return "IPAddress: " + getName();
    } catch (IOException iOException) {
      HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
      return "IPAddress: " + hexDumpEncoder.encodeBuffer(this.address);
    } 
  }
  
  public String getName() {
    if (this.name != null)
      return this.name; 
    if (this.isIPv4) {
      byte[] arrayOfByte = new byte[4];
      System.arraycopy(this.address, 0, arrayOfByte, 0, 4);
      this.name = InetAddress.getByAddress(arrayOfByte).getHostAddress();
      if (this.address.length == 8) {
        byte[] arrayOfByte1 = new byte[4];
        System.arraycopy(this.address, 4, arrayOfByte1, 0, 4);
        this.name += "/" + InetAddress.getByAddress(arrayOfByte1).getHostAddress();
      } 
    } else {
      byte[] arrayOfByte = new byte[16];
      System.arraycopy(this.address, 0, arrayOfByte, 0, 16);
      this.name = InetAddress.getByAddress(arrayOfByte).getHostAddress();
      if (this.address.length == 32) {
        byte[] arrayOfByte1 = new byte[16];
        for (byte b1 = 16; b1 < 32; b1++)
          arrayOfByte1[b1 - 16] = this.address[b1]; 
        BitArray bitArray = new BitArray(128, arrayOfByte1);
        byte b2;
        for (b2 = 0; b2 < '' && bitArray.get(b2); b2++);
        this.name += "/" + b2;
        while (b2 < '') {
          if (bitArray.get(b2))
            throw new IOException("Invalid IPv6 subdomain - set bit " + b2 + " not contiguous"); 
          b2++;
        } 
      } 
    } 
    return this.name;
  }
  
  public byte[] getBytes() { return (byte[])this.address.clone(); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof IPAddressName))
      return false; 
    IPAddressName iPAddressName = (IPAddressName)paramObject;
    byte[] arrayOfByte = iPAddressName.address;
    if (arrayOfByte.length != this.address.length)
      return false; 
    if (this.address.length == 8 || this.address.length == 32) {
      int i = this.address.length / 2;
      int j;
      for (j = 0; j < i; j++) {
        byte b1 = (byte)(this.address[j] & this.address[j + i]);
        byte b2 = (byte)(arrayOfByte[j] & arrayOfByte[j + i]);
        if (b1 != b2)
          return false; 
      } 
      for (j = i; j < this.address.length; j++) {
        if (this.address[j] != arrayOfByte[j])
          return false; 
      } 
      return true;
    } 
    return Arrays.equals(arrayOfByte, this.address);
  }
  
  public int hashCode() {
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.address.length; b2++)
      b1 += this.address[b2] * b2; 
    return b1;
  }
  
  public int constrains(GeneralNameInterface paramGeneralNameInterface) throws UnsupportedOperationException {
    byte b;
    if (paramGeneralNameInterface == null) {
      b = -1;
    } else if (paramGeneralNameInterface.getType() != 7) {
      b = -1;
    } else if (((IPAddressName)paramGeneralNameInterface).equals(this)) {
      b = 0;
    } else {
      IPAddressName iPAddressName = (IPAddressName)paramGeneralNameInterface;
      byte[] arrayOfByte = iPAddressName.address;
      if (arrayOfByte.length == 4 && this.address.length == 4) {
        b = 3;
      } else if ((arrayOfByte.length == 8 && this.address.length == 8) || (arrayOfByte.length == 32 && this.address.length == 32)) {
        boolean bool1 = true;
        boolean bool2 = true;
        boolean bool3 = false;
        boolean bool4 = false;
        int i = this.address.length / 2;
        for (int j = 0; j < i; j++) {
          if ((byte)(this.address[j] & this.address[j + i]) != this.address[j])
            bool3 = true; 
          if ((byte)(arrayOfByte[j] & arrayOfByte[j + i]) != arrayOfByte[j])
            bool4 = true; 
          if ((byte)(this.address[j + i] & arrayOfByte[j + i]) != this.address[j + i] || (byte)(this.address[j] & this.address[j + i]) != (byte)(arrayOfByte[j] & this.address[j + i]))
            bool1 = false; 
          if ((byte)(arrayOfByte[j + i] & this.address[j + i]) != arrayOfByte[j + i] || (byte)(arrayOfByte[j] & arrayOfByte[j + i]) != (byte)(this.address[j] & arrayOfByte[j + i]))
            bool2 = false; 
        } 
        if (bool3 || bool4) {
          if (bool3 && bool4) {
            b = 0;
          } else if (bool3) {
            b = 2;
          } else {
            b = 1;
          } 
        } else if (bool1) {
          b = 1;
        } else if (bool2) {
          b = 2;
        } else {
          b = 3;
        } 
      } else if (arrayOfByte.length == 8 || arrayOfByte.length == 32) {
        int i = 0;
        int j = arrayOfByte.length / 2;
        while (i < j && (this.address[i] & arrayOfByte[i + j]) == arrayOfByte[i])
          i++; 
        if (i == j) {
          b = 2;
        } else {
          b = 3;
        } 
      } else if (this.address.length == 8 || this.address.length == 32) {
        int i = 0;
        int j = this.address.length / 2;
        while (i < j && (arrayOfByte[i] & this.address[i + j]) == this.address[i])
          i++; 
        if (i == j) {
          b = 1;
        } else {
          b = 3;
        } 
      } else {
        b = 3;
      } 
    } 
    return b;
  }
  
  public int subtreeDepth() { throw new UnsupportedOperationException("subtreeDepth() not defined for IPAddressName"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\IPAddressName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */