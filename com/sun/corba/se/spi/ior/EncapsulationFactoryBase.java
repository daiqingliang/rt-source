package com.sun.corba.se.spi.ior;

import com.sun.corba.se.impl.ior.EncapsulationUtility;
import org.omg.CORBA_2_3.portable.InputStream;

public abstract class EncapsulationFactoryBase implements IdentifiableFactory {
  private int id;
  
  public int getId() { return this.id; }
  
  public EncapsulationFactoryBase(int paramInt) { this.id = paramInt; }
  
  public final Identifiable create(InputStream paramInputStream) {
    InputStream inputStream = EncapsulationUtility.getEncapsulationStream(paramInputStream);
    return readContents(inputStream);
  }
  
  protected abstract Identifiable readContents(InputStream paramInputStream);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\ior\EncapsulationFactoryBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */