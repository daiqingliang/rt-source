package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.org.omg.CORBA.Repository;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.org.omg.SendingContext.CodeBase;
import com.sun.org.omg.SendingContext.CodeBaseHelper;
import com.sun.org.omg.SendingContext._CodeBaseImplBase;
import java.util.Hashtable;
import org.omg.CORBA.Object;

public class CachedCodeBase extends _CodeBaseImplBase {
  private Hashtable implementations;
  
  private Hashtable fvds;
  
  private Hashtable bases;
  
  private CorbaConnection conn;
  
  private static Object iorMapLock = new Object();
  
  private static Hashtable<IOR, CodeBase> iorMap = new Hashtable();
  
  public static void cleanCache(ORB paramORB) {
    synchronized (iorMapLock) {
      for (IOR iOR : iorMap.keySet()) {
        if (iOR.getORB() == paramORB)
          iorMap.remove(iOR); 
      } 
    } 
  }
  
  public CachedCodeBase(CorbaConnection paramCorbaConnection) { this.conn = paramCorbaConnection; }
  
  public Repository get_ir() { return null; }
  
  public String implementation(String paramString) {
    String str = null;
    if (this.implementations == null) {
      this.implementations = new Hashtable();
    } else {
      str = (String)this.implementations.get(paramString);
    } 
    if (str == null && connectedCodeBase()) {
      str = this.delegate.implementation(paramString);
      if (str != null)
        this.implementations.put(paramString, str); 
    } 
    return str;
  }
  
  public String[] implementations(String[] paramArrayOfString) {
    String[] arrayOfString = new String[paramArrayOfString.length];
    for (byte b = 0; b < arrayOfString.length; b++)
      arrayOfString[b] = implementation(paramArrayOfString[b]); 
    return arrayOfString;
  }
  
  public FullValueDescription meta(String paramString) {
    FullValueDescription fullValueDescription = null;
    if (this.fvds == null) {
      this.fvds = new Hashtable();
    } else {
      fullValueDescription = (FullValueDescription)this.fvds.get(paramString);
    } 
    if (fullValueDescription == null && connectedCodeBase()) {
      fullValueDescription = this.delegate.meta(paramString);
      if (fullValueDescription != null)
        this.fvds.put(paramString, fullValueDescription); 
    } 
    return fullValueDescription;
  }
  
  public FullValueDescription[] metas(String[] paramArrayOfString) {
    FullValueDescription[] arrayOfFullValueDescription = new FullValueDescription[paramArrayOfString.length];
    for (byte b = 0; b < arrayOfFullValueDescription.length; b++)
      arrayOfFullValueDescription[b] = meta(paramArrayOfString[b]); 
    return arrayOfFullValueDescription;
  }
  
  public String[] bases(String paramString) {
    String[] arrayOfString = null;
    if (this.bases == null) {
      this.bases = new Hashtable();
    } else {
      arrayOfString = (String[])this.bases.get(paramString);
    } 
    if (arrayOfString == null && connectedCodeBase()) {
      arrayOfString = this.delegate.bases(paramString);
      if (arrayOfString != null)
        this.bases.put(paramString, arrayOfString); 
    } 
    return arrayOfString;
  }
  
  private boolean connectedCodeBase() {
    if (this.delegate != null)
      return true; 
    if (this.conn.getCodeBaseIOR() == null) {
      if ((this.conn.getBroker()).transportDebugFlag)
        this.conn.dprint("CodeBase unavailable on connection: " + this.conn); 
      return false;
    } 
    synchronized (iorMapLock) {
      if (this.delegate != null)
        return true; 
      this.delegate = (CodeBase)iorMap.get(this.conn.getCodeBaseIOR());
      if (this.delegate != null)
        return true; 
      this.delegate = CodeBaseHelper.narrow(getObjectFromIOR());
      iorMap.put(this.conn.getCodeBaseIOR(), this.delegate);
    } 
    return true;
  }
  
  private final Object getObjectFromIOR() { return CDRInputStream_1_0.internalIORToObject(this.conn.getCodeBaseIOR(), null, this.conn.getBroker()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\CachedCodeBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */