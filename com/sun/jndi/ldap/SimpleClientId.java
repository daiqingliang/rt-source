package com.sun.jndi.ldap;

import java.io.OutputStream;
import java.util.Arrays;
import javax.naming.ldap.Control;

class SimpleClientId extends ClientId {
  private final String username;
  
  private final Object passwd;
  
  private final int myHash;
  
  SimpleClientId(int paramInt1, String paramString1, int paramInt2, String paramString2, Control[] paramArrayOfControl, OutputStream paramOutputStream, String paramString3, String paramString4, Object paramObject) {
    super(paramInt1, paramString1, paramInt2, paramString2, paramArrayOfControl, paramOutputStream, paramString3);
    this.username = paramString4;
    int i = 0;
    if (paramObject == null) {
      this.passwd = null;
    } else if (paramObject instanceof byte[]) {
      this.passwd = ((byte[])paramObject).clone();
      i = Arrays.hashCode((byte[])paramObject);
    } else if (paramObject instanceof char[]) {
      this.passwd = ((char[])paramObject).clone();
      i = Arrays.hashCode((char[])paramObject);
    } else {
      this.passwd = paramObject;
      i = paramObject.hashCode();
    } 
    this.myHash = super.hashCode() ^ ((paramString4 != null) ? paramString4.hashCode() : 0) ^ i;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof SimpleClientId))
      return false; 
    SimpleClientId simpleClientId = (SimpleClientId)paramObject;
    return (super.equals(paramObject) && (this.username == simpleClientId.username || (this.username != null && this.username.equals(simpleClientId.username))) && (this.passwd == simpleClientId.passwd || (this.passwd != null && simpleClientId.passwd != null && ((this.passwd instanceof String && this.passwd.equals(simpleClientId.passwd)) || (this.passwd instanceof byte[] && simpleClientId.passwd instanceof byte[] && Arrays.equals((byte[])this.passwd, (byte[])simpleClientId.passwd)) || (this.passwd instanceof char[] && simpleClientId.passwd instanceof char[] && Arrays.equals((char[])this.passwd, (char[])simpleClientId.passwd))))));
  }
  
  public int hashCode() { return this.myHash; }
  
  public String toString() { return super.toString() + ":" + this.username; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\SimpleClientId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */