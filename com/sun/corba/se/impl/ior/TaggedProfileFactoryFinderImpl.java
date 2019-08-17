package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.Identifiable;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.InputStream;

public class TaggedProfileFactoryFinderImpl extends IdentifiableFactoryFinderBase {
  public TaggedProfileFactoryFinderImpl(ORB paramORB) { super(paramORB); }
  
  public Identifiable handleMissingFactory(int paramInt, InputStream paramInputStream) { return new GenericTaggedProfile(paramInt, paramInputStream); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\TaggedProfileFactoryFinderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */