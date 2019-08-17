package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.Object;

public class ForwardException extends RuntimeException {
  private ORB orb;
  
  private Object obj;
  
  private IOR ior;
  
  public ForwardException(ORB paramORB, IOR paramIOR) {
    this.orb = paramORB;
    this.obj = null;
    this.ior = paramIOR;
  }
  
  public ForwardException(ORB paramORB, Object paramObject) {
    if (paramObject instanceof org.omg.CORBA.LocalObject)
      throw new BAD_PARAM(); 
    this.orb = paramORB;
    this.obj = paramObject;
    this.ior = null;
  }
  
  public Object getObject() {
    if (this.obj == null)
      this.obj = ORBUtility.makeObjectReference(this.ior); 
    return this.obj;
  }
  
  public IOR getIOR() {
    if (this.ior == null)
      this.ior = ORBUtility.getIOR(this.obj); 
    return this.ior;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\protocol\ForwardException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */