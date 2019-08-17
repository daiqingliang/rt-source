package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import java.util.EmptyStackException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.PortableServer.Current;
import org.omg.PortableServer.CurrentPackage.NoContext;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;

public class POACurrent extends ObjectImpl implements Current {
  private ORB orb;
  
  private POASystemException wrapper;
  
  public POACurrent(ORB paramORB) {
    this.orb = paramORB;
    this.wrapper = POASystemException.get(paramORB, "oa.invocation");
  }
  
  public String[] _ids() {
    String[] arrayOfString = new String[1];
    arrayOfString[0] = "IDL:omg.org/PortableServer/Current:1.0";
    return arrayOfString;
  }
  
  public POA get_POA() throws NoContext {
    POA pOA = (POA)peekThrowNoContext().oa();
    throwNoContextIfNull(pOA);
    return pOA;
  }
  
  public byte[] get_object_id() throws NoContext {
    byte[] arrayOfByte = peekThrowNoContext().id();
    throwNoContextIfNull(arrayOfByte);
    return arrayOfByte;
  }
  
  public ObjectAdapter getOA() {
    ObjectAdapter objectAdapter = peekThrowInternal().oa();
    throwInternalIfNull(objectAdapter);
    return objectAdapter;
  }
  
  public byte[] getObjectId() throws NoContext {
    byte[] arrayOfByte = peekThrowInternal().id();
    throwInternalIfNull(arrayOfByte);
    return arrayOfByte;
  }
  
  Servant getServant() { return (Servant)peekThrowInternal().getServantContainer(); }
  
  CookieHolder getCookieHolder() {
    CookieHolder cookieHolder = peekThrowInternal().getCookieHolder();
    throwInternalIfNull(cookieHolder);
    return cookieHolder;
  }
  
  public String getOperation() {
    String str = peekThrowInternal().getOperation();
    throwInternalIfNull(str);
    return str;
  }
  
  void setServant(Servant paramServant) { peekThrowInternal().setServant(paramServant); }
  
  private OAInvocationInfo peekThrowNoContext() throws NoContext {
    OAInvocationInfo oAInvocationInfo = null;
    try {
      oAInvocationInfo = this.orb.peekInvocationInfo();
    } catch (EmptyStackException emptyStackException) {
      throw new NoContext();
    } 
    return oAInvocationInfo;
  }
  
  private OAInvocationInfo peekThrowInternal() throws NoContext {
    OAInvocationInfo oAInvocationInfo = null;
    try {
      oAInvocationInfo = this.orb.peekInvocationInfo();
    } catch (EmptyStackException emptyStackException) {
      throw this.wrapper.poacurrentUnbalancedStack(emptyStackException);
    } 
    return oAInvocationInfo;
  }
  
  private void throwNoContextIfNull(Object paramObject) throws NoContext {
    if (paramObject == null)
      throw new NoContext(); 
  }
  
  private void throwInternalIfNull(Object paramObject) throws NoContext {
    if (paramObject == null)
      throw this.wrapper.poacurrentNullField(CompletionStatus.COMPLETED_MAYBE); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\oa\poa\POACurrent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */