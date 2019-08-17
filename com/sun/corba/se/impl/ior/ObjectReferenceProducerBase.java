package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORFactory;
import com.sun.corba.se.spi.ior.IORTemplateList;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Object;

public abstract class ObjectReferenceProducerBase {
  protected ORB orb;
  
  public abstract IORFactory getIORFactory();
  
  public abstract IORTemplateList getIORTemplateList();
  
  public ObjectReferenceProducerBase(ORB paramORB) { this.orb = paramORB; }
  
  public Object make_object(String paramString, byte[] paramArrayOfByte) {
    ObjectId objectId = IORFactories.makeObjectId(paramArrayOfByte);
    IOR iOR = getIORFactory().makeIOR(this.orb, paramString, objectId);
    return ORBUtility.makeObjectReference(iOR);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\ObjectReferenceProducerBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */