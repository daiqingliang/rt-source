package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.Identifiable;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.InputStream;

public class TaggedProfileTemplateFactoryFinderImpl extends IdentifiableFactoryFinderBase {
  public TaggedProfileTemplateFactoryFinderImpl(ORB paramORB) { super(paramORB); }
  
  public Identifiable handleMissingFactory(int paramInt, InputStream paramInputStream) { throw this.wrapper.taggedProfileTemplateFactoryNotFound(new Integer(paramInt)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\TaggedProfileTemplateFactoryFinderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */