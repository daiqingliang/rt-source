package com.sun.corba.se.impl.copyobject;

import com.sun.corba.se.impl.orbutil.DenseIntMapImpl;
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
import com.sun.corba.se.spi.orb.ORB;

public class CopierManagerImpl implements CopierManager {
  private int defaultId = 0;
  
  private DenseIntMapImpl map = new DenseIntMapImpl();
  
  private ORB orb;
  
  public CopierManagerImpl(ORB paramORB) { this.orb = paramORB; }
  
  public void setDefaultId(int paramInt) { this.defaultId = paramInt; }
  
  public int getDefaultId() { return this.defaultId; }
  
  public ObjectCopierFactory getObjectCopierFactory(int paramInt) { return (ObjectCopierFactory)this.map.get(paramInt); }
  
  public ObjectCopierFactory getDefaultObjectCopierFactory() { return (ObjectCopierFactory)this.map.get(this.defaultId); }
  
  public void registerObjectCopierFactory(ObjectCopierFactory paramObjectCopierFactory, int paramInt) { this.map.set(paramInt, paramObjectCopierFactory); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\copyobject\CopierManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */