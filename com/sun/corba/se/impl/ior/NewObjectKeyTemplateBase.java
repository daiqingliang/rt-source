package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public abstract class NewObjectKeyTemplateBase extends ObjectKeyTemplateBase {
  public NewObjectKeyTemplateBase(ORB paramORB, int paramInt1, int paramInt2, int paramInt3, String paramString, ObjectAdapterId paramObjectAdapterId) {
    super(paramORB, paramInt1, paramInt2, paramInt3, paramString, paramObjectAdapterId);
    if (paramInt1 != -1347695872)
      throw this.wrapper.badMagic(new Integer(paramInt1)); 
  }
  
  public void write(ObjectId paramObjectId, OutputStream paramOutputStream) {
    super.write(paramObjectId, paramOutputStream);
    getORBVersion().write(paramOutputStream);
  }
  
  public void write(OutputStream paramOutputStream) {
    super.write(paramOutputStream);
    getORBVersion().write(paramOutputStream);
  }
  
  protected void setORBVersion(InputStream paramInputStream) {
    ORBVersion oRBVersion = ORBVersionFactory.create(paramInputStream);
    setORBVersion(oRBVersion);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\NewObjectKeyTemplateBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */