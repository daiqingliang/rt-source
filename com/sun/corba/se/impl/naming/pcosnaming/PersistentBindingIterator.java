package com.sun.corba.se.impl.naming.pcosnaming;

import com.sun.corba.se.impl.naming.cosnaming.BindingIteratorImpl;
import java.util.Enumeration;
import java.util.Hashtable;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingHolder;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.NameComponent;
import org.omg.PortableServer.POA;

public class PersistentBindingIterator extends BindingIteratorImpl {
  private POA biPOA;
  
  private int currentSize;
  
  private Hashtable theHashtable;
  
  private Enumeration theEnumeration;
  
  private ORB orb;
  
  public PersistentBindingIterator(ORB paramORB, Hashtable paramHashtable, POA paramPOA) throws Exception {
    super(paramORB);
    this.orb = paramORB;
    this.theHashtable = paramHashtable;
    this.theEnumeration = this.theHashtable.keys();
    this.currentSize = this.theHashtable.size();
    this.biPOA = paramPOA;
  }
  
  public final boolean NextOne(BindingHolder paramBindingHolder) {
    boolean bool = this.theEnumeration.hasMoreElements();
    if (bool) {
      InternalBindingKey internalBindingKey = (InternalBindingKey)this.theEnumeration.nextElement();
      InternalBindingValue internalBindingValue = (InternalBindingValue)this.theHashtable.get(internalBindingKey);
      NameComponent nameComponent = new NameComponent(internalBindingKey.id, internalBindingKey.kind);
      NameComponent[] arrayOfNameComponent = new NameComponent[1];
      arrayOfNameComponent[0] = nameComponent;
      BindingType bindingType = internalBindingValue.theBindingType;
      paramBindingHolder.value = new Binding(arrayOfNameComponent, bindingType);
    } else {
      paramBindingHolder.value = new Binding(new NameComponent[0], BindingType.nobject);
    } 
    return bool;
  }
  
  public final void Destroy() {
    try {
      byte[] arrayOfByte = this.biPOA.servant_to_id(this);
      if (arrayOfByte != null)
        this.biPOA.deactivate_object(arrayOfByte); 
    } catch (Exception exception) {
      throw new INTERNAL("Exception in BindingIterator.Destroy " + exception);
    } 
  }
  
  public final int RemainingElements() { return this.currentSize; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\naming\pcosnaming\PersistentBindingIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */