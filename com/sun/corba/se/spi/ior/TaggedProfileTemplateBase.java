package com.sun.corba.se.spi.ior;

import com.sun.corba.se.impl.ior.EncapsulationUtility;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Iterator;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.IOP.TaggedComponent;

public abstract class TaggedProfileTemplateBase extends IdentifiableContainerBase implements TaggedProfileTemplate {
  public void write(OutputStream paramOutputStream) { EncapsulationUtility.writeEncapsulation(this, paramOutputStream); }
  
  public TaggedComponent[] getIOPComponents(ORB paramORB, int paramInt) {
    byte b1 = 0;
    Iterator iterator = iteratorById(paramInt);
    while (iterator.hasNext()) {
      iterator.next();
      b1++;
    } 
    TaggedComponent[] arrayOfTaggedComponent = new TaggedComponent[b1];
    byte b2 = 0;
    iterator = iteratorById(paramInt);
    while (iterator.hasNext()) {
      TaggedComponent taggedComponent = (TaggedComponent)iterator.next();
      arrayOfTaggedComponent[b2++] = taggedComponent.getIOPComponent(paramORB);
    } 
    return arrayOfTaggedComponent;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\ior\TaggedProfileTemplateBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */