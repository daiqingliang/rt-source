package com.sun.corba.se.spi.oa;

import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
import javax.rmi.CORBA.Tie;
import org.omg.CORBA.portable.ServantObject;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;

public class OAInvocationInfo extends ServantObject {
  private Object servantContainer;
  
  private ObjectAdapter oa;
  
  private byte[] oid;
  
  private CookieHolder cookieHolder;
  
  private String operation;
  
  private ObjectCopierFactory factory;
  
  public OAInvocationInfo(ObjectAdapter paramObjectAdapter, byte[] paramArrayOfByte) {
    this.oa = paramObjectAdapter;
    this.oid = paramArrayOfByte;
  }
  
  public OAInvocationInfo(OAInvocationInfo paramOAInvocationInfo, String paramString) {
    this.servant = paramOAInvocationInfo.servant;
    this.servantContainer = paramOAInvocationInfo.servantContainer;
    this.cookieHolder = paramOAInvocationInfo.cookieHolder;
    this.oa = paramOAInvocationInfo.oa;
    this.oid = paramOAInvocationInfo.oid;
    this.factory = paramOAInvocationInfo.factory;
    this.operation = paramString;
  }
  
  public ObjectAdapter oa() { return this.oa; }
  
  public byte[] id() { return this.oid; }
  
  public Object getServantContainer() { return this.servantContainer; }
  
  public CookieHolder getCookieHolder() {
    if (this.cookieHolder == null)
      this.cookieHolder = new CookieHolder(); 
    return this.cookieHolder;
  }
  
  public String getOperation() { return this.operation; }
  
  public ObjectCopierFactory getCopierFactory() { return this.factory; }
  
  public void setOperation(String paramString) { this.operation = paramString; }
  
  public void setCopierFactory(ObjectCopierFactory paramObjectCopierFactory) { this.factory = paramObjectCopierFactory; }
  
  public void setServant(Object paramObject) {
    this.servantContainer = paramObject;
    if (paramObject instanceof Tie) {
      this.servant = ((Tie)paramObject).getTarget();
    } else {
      this.servant = paramObject;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\oa\OAInvocationInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */