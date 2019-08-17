package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.TaggedComponent;
import org.omg.CORBA.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.IOP.TaggedComponent;

public class GenericTaggedComponent extends GenericIdentifiable implements TaggedComponent {
  public GenericTaggedComponent(int paramInt, InputStream paramInputStream) { super(paramInt, paramInputStream); }
  
  public GenericTaggedComponent(int paramInt, byte[] paramArrayOfByte) { super(paramInt, paramArrayOfByte); }
  
  public TaggedComponent getIOPComponent(ORB paramORB) { return new TaggedComponent(getId(), getData()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\GenericTaggedComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */