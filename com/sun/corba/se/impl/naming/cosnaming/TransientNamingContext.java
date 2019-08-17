package com.sun.corba.se.impl.naming.cosnaming;

import com.sun.corba.se.impl.logging.NamingSystemException;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingIterator;
import org.omg.CosNaming.BindingIteratorHelper;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.BindingTypeHolder;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.PortableServer.POA;

public class TransientNamingContext extends NamingContextImpl implements NamingContextDataStore {
  private Logger readLogger;
  
  private Logger updateLogger;
  
  private Logger lifecycleLogger;
  
  private NamingSystemException wrapper;
  
  private final Hashtable theHashtable = new Hashtable();
  
  public Object localRoot;
  
  public TransientNamingContext(ORB paramORB, Object paramObject, POA paramPOA) throws Exception {
    super(paramORB, paramPOA);
    this.wrapper = NamingSystemException.get(paramORB, "naming");
    this.localRoot = paramObject;
    this.readLogger = paramORB.getLogger("naming.read");
    this.updateLogger = paramORB.getLogger("naming.update");
    this.lifecycleLogger = paramORB.getLogger("naming.lifecycle");
    this.lifecycleLogger.fine("Root TransientNamingContext LIFECYCLE.CREATED");
  }
  
  public final void Bind(NameComponent paramNameComponent, Object paramObject, BindingType paramBindingType) throws SystemException {
    InternalBindingKey internalBindingKey = new InternalBindingKey(paramNameComponent);
    NameComponent[] arrayOfNameComponent = new NameComponent[1];
    arrayOfNameComponent[0] = paramNameComponent;
    Binding binding = new Binding(arrayOfNameComponent, paramBindingType);
    InternalBindingValue internalBindingValue1 = new InternalBindingValue(binding, null);
    internalBindingValue1.theObjectRef = paramObject;
    InternalBindingValue internalBindingValue2 = (InternalBindingValue)this.theHashtable.put(internalBindingKey, internalBindingValue1);
    if (internalBindingValue2 != null) {
      this.updateLogger.warning("<<NAMING BIND>>Name " + getName(paramNameComponent) + " Was Already Bound");
      throw this.wrapper.transNcBindAlreadyBound();
    } 
    if (this.updateLogger.isLoggable(Level.FINE))
      this.updateLogger.fine("<<NAMING BIND>><<SUCCESS>>Name Component: " + paramNameComponent.id + "." + paramNameComponent.kind); 
  }
  
  public final Object Resolve(NameComponent paramNameComponent, BindingTypeHolder paramBindingTypeHolder) throws SystemException {
    if (paramNameComponent.id.length() == 0 && paramNameComponent.kind.length() == 0) {
      paramBindingTypeHolder.value = BindingType.ncontext;
      return this.localRoot;
    } 
    InternalBindingKey internalBindingKey = new InternalBindingKey(paramNameComponent);
    InternalBindingValue internalBindingValue = (InternalBindingValue)this.theHashtable.get(internalBindingKey);
    if (internalBindingValue == null)
      return null; 
    if (this.readLogger.isLoggable(Level.FINE))
      this.readLogger.fine("<<NAMING RESOLVE>><<SUCCESS>>Namecomponent :" + getName(paramNameComponent)); 
    paramBindingTypeHolder.value = internalBindingValue.theBinding.binding_type;
    return internalBindingValue.theObjectRef;
  }
  
  public final Object Unbind(NameComponent paramNameComponent) throws SystemException {
    InternalBindingKey internalBindingKey = new InternalBindingKey(paramNameComponent);
    InternalBindingValue internalBindingValue = (InternalBindingValue)this.theHashtable.remove(internalBindingKey);
    if (internalBindingValue == null) {
      if (this.updateLogger.isLoggable(Level.FINE))
        this.updateLogger.fine("<<NAMING UNBIND>><<FAILURE>> There was no binding with the name " + getName(paramNameComponent) + " to Unbind "); 
      return null;
    } 
    if (this.updateLogger.isLoggable(Level.FINE))
      this.updateLogger.fine("<<NAMING UNBIND>><<SUCCESS>> NameComponent:  " + getName(paramNameComponent)); 
    return internalBindingValue.theObjectRef;
  }
  
  public final void List(int paramInt, BindingListHolder paramBindingListHolder, BindingIteratorHolder paramBindingIteratorHolder) throws SystemException {
    try {
      TransientBindingIterator transientBindingIterator = new TransientBindingIterator(this.orb, (Hashtable)this.theHashtable.clone(), this.nsPOA);
      transientBindingIterator.list(paramInt, paramBindingListHolder);
      byte[] arrayOfByte = this.nsPOA.activate_object(transientBindingIterator);
      Object object = this.nsPOA.id_to_reference(arrayOfByte);
      BindingIterator bindingIterator = BindingIteratorHelper.narrow(object);
      paramBindingIteratorHolder.value = bindingIterator;
    } catch (SystemException systemException) {
      this.readLogger.warning("<<NAMING LIST>><<FAILURE>>" + systemException);
      throw systemException;
    } catch (Exception exception) {
      this.readLogger.severe("<<NAMING LIST>><<FAILURE>>" + exception);
      throw this.wrapper.transNcListGotExc(exception);
    } 
  }
  
  public final NamingContext NewContext() throws SystemException {
    try {
      TransientNamingContext transientNamingContext = new TransientNamingContext(this.orb, this.localRoot, this.nsPOA);
      byte[] arrayOfByte = this.nsPOA.activate_object(transientNamingContext);
      Object object = this.nsPOA.id_to_reference(arrayOfByte);
      this.lifecycleLogger.fine("TransientNamingContext LIFECYCLE.CREATE SUCCESSFUL");
      return NamingContextHelper.narrow(object);
    } catch (SystemException systemException) {
      this.lifecycleLogger.log(Level.WARNING, "<<LIFECYCLE CREATE>><<FAILURE>>", systemException);
      throw systemException;
    } catch (Exception exception) {
      this.lifecycleLogger.log(Level.WARNING, "<<LIFECYCLE CREATE>><<FAILURE>>", exception);
      throw this.wrapper.transNcNewctxGotExc(exception);
    } 
  }
  
  public final void Destroy() throws SystemException {
    try {
      byte[] arrayOfByte = this.nsPOA.servant_to_id(this);
      if (arrayOfByte != null)
        this.nsPOA.deactivate_object(arrayOfByte); 
      if (this.lifecycleLogger.isLoggable(Level.FINE))
        this.lifecycleLogger.fine("<<LIFECYCLE DESTROY>><<SUCCESS>>"); 
    } catch (SystemException systemException) {
      this.lifecycleLogger.log(Level.WARNING, "<<LIFECYCLE DESTROY>><<FAILURE>>", systemException);
      throw systemException;
    } catch (Exception exception) {
      this.lifecycleLogger.log(Level.WARNING, "<<LIFECYCLE DESTROY>><<FAILURE>>", exception);
      throw this.wrapper.transNcDestroyGotExc(exception);
    } 
  }
  
  private String getName(NameComponent paramNameComponent) { return paramNameComponent.id + "." + paramNameComponent.kind; }
  
  public final boolean IsEmpty() { return this.theHashtable.isEmpty(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\naming\cosnaming\TransientNamingContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */