package com.sun.corba.se.impl.naming.pcosnaming;

import com.sun.corba.se.spi.orb.ORB;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import org.omg.CORBA.LocalObject;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantLocator;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;

public class ServantManagerImpl extends LocalObject implements ServantLocator {
  private static final long serialVersionUID = 4028710359865748280L;
  
  private ORB orb;
  
  private NameService theNameService;
  
  private File logDir;
  
  private Hashtable contexts;
  
  private CounterDB counterDb;
  
  private int counter;
  
  private static final String objKeyPrefix = "NC";
  
  ServantManagerImpl(ORB paramORB, File paramFile, NameService paramNameService) {
    this.logDir = paramFile;
    this.orb = paramORB;
    this.counterDb = new CounterDB(paramFile);
    this.contexts = new Hashtable();
    this.theNameService = paramNameService;
  }
  
  public Servant preinvoke(byte[] paramArrayOfByte, POA paramPOA, String paramString, CookieHolder paramCookieHolder) throws ForwardRequest {
    String str = new String(paramArrayOfByte);
    Servant servant = (Servant)this.contexts.get(str);
    if (servant == null)
      servant = readInContext(str); 
    return servant;
  }
  
  public void postinvoke(byte[] paramArrayOfByte, POA paramPOA, String paramString, Object paramObject, Servant paramServant) {}
  
  public NamingContextImpl readInContext(String paramString) {
    NamingContextImpl namingContextImpl = (NamingContextImpl)this.contexts.get(paramString);
    if (namingContextImpl != null)
      return namingContextImpl; 
    File file = new File(this.logDir, paramString);
    if (file.exists())
      try {
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        namingContextImpl = (NamingContextImpl)objectInputStream.readObject();
        namingContextImpl.setORB(this.orb);
        namingContextImpl.setServantManagerImpl(this);
        namingContextImpl.setRootNameService(this.theNameService);
        objectInputStream.close();
      } catch (Exception exception) {} 
    if (namingContextImpl != null)
      this.contexts.put(paramString, namingContextImpl); 
    return namingContextImpl;
  }
  
  public NamingContextImpl addContext(String paramString, NamingContextImpl paramNamingContextImpl) {
    File file = new File(this.logDir, paramString);
    if (file.exists()) {
      paramNamingContextImpl = readInContext(paramString);
    } else {
      try {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(paramNamingContextImpl);
        objectOutputStream.close();
      } catch (Exception exception) {}
    } 
    try {
      this.contexts.remove(paramString);
    } catch (Exception exception) {}
    this.contexts.put(paramString, paramNamingContextImpl);
    return paramNamingContextImpl;
  }
  
  public void updateContext(String paramString, NamingContextImpl paramNamingContextImpl) {
    File file = new File(this.logDir, paramString);
    if (file.exists()) {
      file.delete();
      file = new File(this.logDir, paramString);
    } 
    try {
      FileOutputStream fileOutputStream = new FileOutputStream(file);
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
      objectOutputStream.writeObject(paramNamingContextImpl);
      objectOutputStream.close();
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  public static String getRootObjectKey() { return "NC0"; }
  
  public String getNewObjectKey() { return "NC" + this.counterDb.getNextCounter(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\naming\pcosnaming\ServantManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */