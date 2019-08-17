package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.spi.ior.Identifiable;
import com.sun.corba.se.spi.ior.IdentifiableFactory;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.orb.ORB;
import java.util.HashMap;
import java.util.Map;
import org.omg.CORBA_2_3.portable.InputStream;

public abstract class IdentifiableFactoryFinderBase implements IdentifiableFactoryFinder {
  private ORB orb;
  
  private Map map = new HashMap();
  
  protected IORSystemException wrapper;
  
  protected IdentifiableFactoryFinderBase(ORB paramORB) {
    this.orb = paramORB;
    this.wrapper = IORSystemException.get(paramORB, "oa.ior");
  }
  
  protected IdentifiableFactory getFactory(int paramInt) {
    Integer integer = new Integer(paramInt);
    return (IdentifiableFactory)this.map.get(integer);
  }
  
  public abstract Identifiable handleMissingFactory(int paramInt, InputStream paramInputStream);
  
  public Identifiable create(int paramInt, InputStream paramInputStream) {
    IdentifiableFactory identifiableFactory = getFactory(paramInt);
    return (identifiableFactory != null) ? identifiableFactory.create(paramInputStream) : handleMissingFactory(paramInt, paramInputStream);
  }
  
  public void registerFactory(IdentifiableFactory paramIdentifiableFactory) {
    Integer integer = new Integer(paramIdentifiableFactory.getId());
    this.map.put(integer, paramIdentifiableFactory);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\IdentifiableFactoryFinderBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */