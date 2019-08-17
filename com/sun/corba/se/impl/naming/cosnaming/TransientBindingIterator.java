package com.sun.corba.se.impl.naming.cosnaming;

import java.util.Enumeration;
import java.util.Hashtable;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingHolder;
import org.omg.CosNaming.BindingType;
import org.omg.PortableServer.POA;

public class TransientBindingIterator extends BindingIteratorImpl {
  private POA nsPOA;
  
  private int currentSize;
  
  private Hashtable theHashtable;
  
  private Enumeration theEnumeration;
  
  public TransientBindingIterator(ORB paramORB, Hashtable paramHashtable, POA paramPOA) throws Exception {
    super(paramORB);
    this.theHashtable = paramHashtable;
    this.theEnumeration = this.theHashtable.elements();
    this.currentSize = this.theHashtable.size();
    this.nsPOA = paramPOA;
  }
  
  public final boolean NextOne(BindingHolder paramBindingHolder) {
    boolean bool = this.theEnumeration.hasMoreElements();
    if (bool) {
      paramBindingHolder.value = ((InternalBindingValue)this.theEnumeration.nextElement()).theBinding;
      this.currentSize--;
    } else {
      paramBindingHolder.value = new Binding(new org.omg.CosNaming.NameComponent[0], BindingType.nobject);
    } 
    return bool;
  }
  
  public final void Destroy() {
    try {
      byte[] arrayOfByte = this.nsPOA.servant_to_id(this);
      if (arrayOfByte != null)
        this.nsPOA.deactivate_object(arrayOfByte); 
    } catch (Exception exception) {
      NamingUtils.errprint("BindingIterator.Destroy():caught exception:");
      NamingUtils.printException(exception);
    } 
  }
  
  public final int RemainingElements() { return this.currentSize; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\naming\cosnaming\TransientBindingIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */