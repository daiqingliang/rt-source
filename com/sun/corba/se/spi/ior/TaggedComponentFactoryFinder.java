package com.sun.corba.se.spi.ior;

import org.omg.CORBA.ORB;
import org.omg.IOP.TaggedComponent;

public interface TaggedComponentFactoryFinder extends IdentifiableFactoryFinder {
  TaggedComponent create(ORB paramORB, TaggedComponent paramTaggedComponent);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\ior\TaggedComponentFactoryFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */