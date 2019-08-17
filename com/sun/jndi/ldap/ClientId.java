package com.sun.jndi.ldap;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;
import javax.naming.ldap.Control;
import javax.net.SocketFactory;

class ClientId {
  private final int version;
  
  private final String hostname;
  
  private final int port;
  
  private final String protocol;
  
  private final Control[] bindCtls;
  
  private final OutputStream trace;
  
  private final String socketFactory;
  
  private final int myHash;
  
  private final int ctlHash;
  
  private SocketFactory factory = null;
  
  private Method sockComparator = null;
  
  private boolean isDefaultSockFactory = false;
  
  public static final boolean debug = false;
  
  ClientId(int paramInt1, String paramString1, int paramInt2, String paramString2, Control[] paramArrayOfControl, OutputStream paramOutputStream, String paramString3) {
    this.version = paramInt1;
    this.hostname = paramString1.toLowerCase(Locale.ENGLISH);
    this.port = paramInt2;
    this.protocol = paramString2;
    this.bindCtls = (paramArrayOfControl != null) ? (Control[])paramArrayOfControl.clone() : null;
    this.trace = paramOutputStream;
    this.socketFactory = paramString3;
    if (paramString3 != null && !paramString3.equals("javax.net.ssl.SSLSocketFactory")) {
      try {
        Class clazz1;
        Class clazz2 = (clazz1 = Obj.helper.loadClass(paramString3)).forName("java.lang.Object");
        this.sockComparator = clazz1.getMethod("compare", new Class[] { clazz2, clazz2 });
        Method method = clazz1.getMethod("getDefault", new Class[0]);
        this.factory = (SocketFactory)method.invoke(null, new Object[0]);
      } catch (Exception exception) {}
    } else {
      this.isDefaultSockFactory = true;
    } 
    this.myHash = paramInt1 + paramInt2 + ((paramOutputStream != null) ? paramOutputStream.hashCode() : 0) + ((this.hostname != null) ? this.hostname.hashCode() : 0) + ((paramString2 != null) ? paramString2.hashCode() : 0) + (this.ctlHash = hashCodeControls(paramArrayOfControl));
  }
  
  public boolean equals(Object paramObject) {
    ClientId clientId;
    return !(paramObject instanceof ClientId) ? false : ((this.myHash == clientId.myHash && this.version == clientId.version && this.port == clientId.port && this.trace == clientId.trace && (this.hostname == clientId.hostname || (this.hostname != null && this.hostname.equals(clientId.hostname))) && (this.protocol == clientId.protocol || (this.protocol != null && this.protocol.equals(clientId.protocol))) && this.ctlHash == clientId.ctlHash && (clientId = (ClientId)paramObject).equalsControls(this.bindCtls, clientId.bindCtls) && equalsSockFactory(clientId)));
  }
  
  public int hashCode() { return this.myHash; }
  
  private static int hashCodeControls(Control[] paramArrayOfControl) {
    if (paramArrayOfControl == null)
      return 0; 
    int i = 0;
    for (byte b = 0; b < paramArrayOfControl.length; b++)
      i = i * 31 + paramArrayOfControl[b].getID().hashCode(); 
    return i;
  }
  
  private static boolean equalsControls(Control[] paramArrayOfControl1, Control[] paramArrayOfControl2) {
    if (paramArrayOfControl1 == paramArrayOfControl2)
      return true; 
    if (paramArrayOfControl1 == null || paramArrayOfControl2 == null)
      return false; 
    if (paramArrayOfControl1.length != paramArrayOfControl2.length)
      return false; 
    for (byte b = 0; b < paramArrayOfControl1.length; b++) {
      if (!paramArrayOfControl1[b].getID().equals(paramArrayOfControl2[b].getID()) || paramArrayOfControl1[b].isCritical() != paramArrayOfControl2[b].isCritical() || !Arrays.equals(paramArrayOfControl1[b].getEncodedValue(), paramArrayOfControl2[b].getEncodedValue()))
        return false; 
    } 
    return true;
  }
  
  private boolean equalsSockFactory(ClientId paramClientId) { return (this.isDefaultSockFactory && paramClientId.isDefaultSockFactory) ? true : (!paramClientId.isDefaultSockFactory ? invokeComparator(paramClientId, this) : invokeComparator(this, paramClientId)); }
  
  private boolean invokeComparator(ClientId paramClientId1, ClientId paramClientId2) {
    Object object;
    try {
      object = paramClientId1.sockComparator.invoke(paramClientId1.factory, new Object[] { paramClientId1.socketFactory, paramClientId2.socketFactory });
    } catch (Exception exception) {
      return false;
    } 
    return (((Integer)object).intValue() == 0);
  }
  
  private static String toStringControls(Control[] paramArrayOfControl) {
    if (paramArrayOfControl == null)
      return ""; 
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < paramArrayOfControl.length; b++) {
      stringBuffer.append(paramArrayOfControl[b].getID());
      stringBuffer.append(' ');
    } 
    return stringBuffer.toString();
  }
  
  public String toString() { return this.hostname + ":" + this.port + ":" + ((this.protocol != null) ? this.protocol : "") + ":" + toStringControls(this.bindCtls) + ":" + this.socketFactory; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\ClientId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */