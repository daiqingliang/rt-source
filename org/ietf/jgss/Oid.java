package org.ietf.jgss;

import java.io.IOException;
import java.io.InputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class Oid {
  private ObjectIdentifier oid;
  
  private byte[] derEncoding;
  
  public Oid(String paramString) throws GSSException {
    try {
      this.oid = new ObjectIdentifier(paramString);
      this.derEncoding = null;
    } catch (Exception exception) {
      throw new GSSException(11, "Improperly formatted Object Identifier String - " + paramString);
    } 
  }
  
  public Oid(InputStream paramInputStream) throws GSSException {
    try {
      DerValue derValue = new DerValue(paramInputStream);
      this.derEncoding = derValue.toByteArray();
      this.oid = derValue.getOID();
    } catch (IOException iOException) {
      throw new GSSException(11, "Improperly formatted ASN.1 DER encoding for Oid");
    } 
  }
  
  public Oid(byte[] paramArrayOfByte) throws GSSException {
    try {
      DerValue derValue = new DerValue(paramArrayOfByte);
      this.derEncoding = derValue.toByteArray();
      this.oid = derValue.getOID();
    } catch (IOException iOException) {
      throw new GSSException(11, "Improperly formatted ASN.1 DER encoding for Oid");
    } 
  }
  
  static Oid getInstance(String paramString) {
    Oid oid1 = null;
    try {
      oid1 = new Oid(paramString);
    } catch (GSSException gSSException) {}
    return oid1;
  }
  
  public String toString() { return this.oid.toString(); }
  
  public boolean equals(Object paramObject) { return (this == paramObject) ? true : ((paramObject instanceof Oid) ? this.oid.equals(((Oid)paramObject).oid) : ((paramObject instanceof ObjectIdentifier) ? this.oid.equals(paramObject) : 0)); }
  
  public byte[] getDER() throws GSSException {
    if (this.derEncoding == null) {
      DerOutputStream derOutputStream = new DerOutputStream();
      try {
        derOutputStream.putOID(this.oid);
      } catch (IOException iOException) {
        throw new GSSException(11, iOException.getMessage());
      } 
      this.derEncoding = derOutputStream.toByteArray();
    } 
    return (byte[])this.derEncoding.clone();
  }
  
  public boolean containedIn(Oid[] paramArrayOfOid) {
    for (byte b = 0; b < paramArrayOfOid.length; b++) {
      if (paramArrayOfOid[b].equals(this))
        return true; 
    } 
    return false;
  }
  
  public int hashCode() { return this.oid.hashCode(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\ietf\jgss\Oid.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */