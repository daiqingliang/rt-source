package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.corba.RequestImpl;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.util.JDKBridge;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.ClientInvocationInfo;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.pept.transport.ContactInfoList;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.spi.protocol.CorbaClientDelegate;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.CorbaContactInfoListIterator;
import java.util.Iterator;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.Request;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ServantObject;

public class CorbaClientDelegateImpl extends CorbaClientDelegate {
  private ORB orb;
  
  private ORBUtilSystemException wrapper;
  
  private CorbaContactInfoList contactInfoList;
  
  public CorbaClientDelegateImpl(ORB paramORB, CorbaContactInfoList paramCorbaContactInfoList) {
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
    this.contactInfoList = paramCorbaContactInfoList;
  }
  
  public Broker getBroker() { return this.orb; }
  
  public ContactInfoList getContactInfoList() { return this.contactInfoList; }
  
  public OutputStream request(Object paramObject, String paramString, boolean paramBoolean) {
    ClientInvocationInfo clientInvocationInfo = this.orb.createOrIncrementInvocationInfo();
    Iterator iterator = clientInvocationInfo.getContactInfoListIterator();
    if (iterator == null) {
      iterator = this.contactInfoList.iterator();
      clientInvocationInfo.setContactInfoListIterator(iterator);
    } 
    if (!iterator.hasNext())
      throw ((CorbaContactInfoListIterator)iterator).getFailureException(); 
    CorbaContactInfo corbaContactInfo = (CorbaContactInfo)iterator.next();
    ClientRequestDispatcher clientRequestDispatcher = corbaContactInfo.getClientRequestDispatcher();
    clientInvocationInfo.setClientRequestDispatcher(clientRequestDispatcher);
    return (OutputStream)clientRequestDispatcher.beginRequest(paramObject, paramString, !paramBoolean, corbaContactInfo);
  }
  
  public InputStream invoke(Object paramObject, OutputStream paramOutputStream) throws ApplicationException, RemarshalException {
    ClientRequestDispatcher clientRequestDispatcher = getClientRequestDispatcher();
    return (InputStream)clientRequestDispatcher.marshalingComplete(paramObject, (OutputObject)paramOutputStream);
  }
  
  public void releaseReply(Object paramObject, InputStream paramInputStream) {
    ClientRequestDispatcher clientRequestDispatcher = getClientRequestDispatcher();
    clientRequestDispatcher.endRequest(this.orb, paramObject, (InputObject)paramInputStream);
    this.orb.releaseOrDecrementInvocationInfo();
  }
  
  private ClientRequestDispatcher getClientRequestDispatcher() { return ((CorbaInvocationInfo)this.orb.getInvocationInfo()).getClientRequestDispatcher(); }
  
  public Object get_interface_def(Object paramObject) {
    inputStream = null;
    Object object = null;
    try {
      OutputStream outputStream = request(null, "_interface", true);
      inputStream = invoke((Object)null, outputStream);
      Object object1 = inputStream.read_Object();
      if (!object1._is_a("IDL:omg.org/CORBA/InterfaceDef:1.0"))
        throw this.wrapper.wrongInterfaceDef(CompletionStatus.COMPLETED_MAYBE); 
      try {
        object = (Object)JDKBridge.loadClass("org.omg.CORBA._InterfaceDefStub").newInstance();
      } catch (Exception exception) {
        throw this.wrapper.noInterfaceDefStub(exception);
      } 
      Delegate delegate = StubAdapter.getDelegate(object1);
      StubAdapter.setDelegate(object, delegate);
    } catch (ApplicationException applicationException) {
      throw this.wrapper.applicationExceptionInSpecialMethod(applicationException);
    } catch (RemarshalException remarshalException) {
      return get_interface_def(paramObject);
    } finally {
      releaseReply((Object)null, inputStream);
    } 
    return object;
  }
  
  public boolean is_a(Object paramObject, String paramString) {
    String[] arrayOfString = StubAdapter.getTypeIds(paramObject);
    String str = this.contactInfoList.getTargetIOR().getTypeId();
    if (paramString.equals(str))
      return true; 
    for (byte b = 0; b < arrayOfString.length; b++) {
      if (paramString.equals(arrayOfString[b]))
        return true; 
    } 
    inputStream = null;
    try {
      OutputStream outputStream = request(null, "_is_a", true);
      outputStream.write_string(paramString);
      inputStream = invoke((Object)null, outputStream);
      return inputStream.read_boolean();
    } catch (ApplicationException applicationException) {
      throw this.wrapper.applicationExceptionInSpecialMethod(applicationException);
    } catch (RemarshalException remarshalException) {
      return is_a(paramObject, paramString);
    } finally {
      releaseReply((Object)null, inputStream);
    } 
  }
  
  public boolean non_existent(Object paramObject) {
    inputStream = null;
    try {
      OutputStream outputStream = request(null, "_non_existent", true);
      inputStream = invoke((Object)null, outputStream);
      return inputStream.read_boolean();
    } catch (ApplicationException applicationException) {
      throw this.wrapper.applicationExceptionInSpecialMethod(applicationException);
    } catch (RemarshalException remarshalException) {
      return non_existent(paramObject);
    } finally {
      releaseReply((Object)null, inputStream);
    } 
  }
  
  public Object duplicate(Object paramObject) { return paramObject; }
  
  public void release(Object paramObject) {}
  
  public boolean is_equivalent(Object paramObject1, Object paramObject2) {
    if (paramObject2 == null)
      return false; 
    if (!StubAdapter.isStub(paramObject2))
      return false; 
    Delegate delegate = StubAdapter.getDelegate(paramObject2);
    if (delegate == null)
      return false; 
    if (delegate == this)
      return true; 
    if (!(delegate instanceof CorbaClientDelegateImpl))
      return false; 
    CorbaClientDelegateImpl corbaClientDelegateImpl = (CorbaClientDelegateImpl)delegate;
    CorbaContactInfoList corbaContactInfoList = (CorbaContactInfoList)corbaClientDelegateImpl.getContactInfoList();
    return this.contactInfoList.getTargetIOR().isEquivalent(corbaContactInfoList.getTargetIOR());
  }
  
  public boolean equals(Object paramObject, Object paramObject1) {
    if (paramObject1 == null)
      return false; 
    if (!StubAdapter.isStub(paramObject1))
      return false; 
    Delegate delegate = StubAdapter.getDelegate(paramObject1);
    if (delegate == null)
      return false; 
    if (delegate instanceof CorbaClientDelegateImpl) {
      CorbaClientDelegateImpl corbaClientDelegateImpl = (CorbaClientDelegateImpl)delegate;
      IOR iOR = corbaClientDelegateImpl.contactInfoList.getTargetIOR();
      return this.contactInfoList.getTargetIOR().equals(iOR);
    } 
    return false;
  }
  
  public int hashCode(Object paramObject) { return hashCode(); }
  
  public int hash(Object paramObject, int paramInt) {
    int i = hashCode();
    return (i > paramInt) ? 0 : i;
  }
  
  public Request request(Object paramObject, String paramString) { return new RequestImpl(this.orb, paramObject, null, paramString, null, null, null, null); }
  
  public Request create_request(Object paramObject, Context paramContext, String paramString, NVList paramNVList, NamedValue paramNamedValue) { return new RequestImpl(this.orb, paramObject, paramContext, paramString, paramNVList, paramNamedValue, null, null); }
  
  public Request create_request(Object paramObject, Context paramContext, String paramString, NVList paramNVList, NamedValue paramNamedValue, ExceptionList paramExceptionList, ContextList paramContextList) { return new RequestImpl(this.orb, paramObject, paramContext, paramString, paramNVList, paramNamedValue, paramExceptionList, paramContextList); }
  
  public ORB orb(Object paramObject) { return this.orb; }
  
  public boolean is_local(Object paramObject) { return this.contactInfoList.getEffectiveTargetIOR().getProfile().isLocal(); }
  
  public ServantObject servant_preinvoke(Object paramObject, String paramString, Class paramClass) { return this.contactInfoList.getLocalClientRequestDispatcher().servant_preinvoke(paramObject, paramString, paramClass); }
  
  public void servant_postinvoke(Object paramObject, ServantObject paramServantObject) { this.contactInfoList.getLocalClientRequestDispatcher().servant_postinvoke(paramObject, paramServantObject); }
  
  public String get_codebase(Object paramObject) { return (this.contactInfoList.getTargetIOR() != null) ? this.contactInfoList.getTargetIOR().getProfile().getCodebase() : null; }
  
  public String toString(Object paramObject) { return this.contactInfoList.getTargetIOR().stringify(); }
  
  public int hashCode() { return this.contactInfoList.hashCode(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\CorbaClientDelegateImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */