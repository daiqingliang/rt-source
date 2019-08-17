package com.sun.corba.se.impl.resolver;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.resolver.Resolver;
import java.util.HashSet;
import java.util.Set;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;

public class BootstrapResolverImpl implements Resolver {
  private Delegate bootstrapDelegate;
  
  private ORBUtilSystemException wrapper;
  
  public BootstrapResolverImpl(ORB paramORB, String paramString, int paramInt) {
    this.wrapper = ORBUtilSystemException.get(paramORB, "orb.resolver");
    byte[] arrayOfByte = "INIT".getBytes();
    ObjectKey objectKey = paramORB.getObjectKeyFactory().create(arrayOfByte);
    IIOPAddress iIOPAddress = IIOPFactories.makeIIOPAddress(paramORB, paramString, paramInt);
    IIOPProfileTemplate iIOPProfileTemplate = IIOPFactories.makeIIOPProfileTemplate(paramORB, GIOPVersion.V1_0, iIOPAddress);
    IORTemplate iORTemplate = IORFactories.makeIORTemplate(objectKey.getTemplate());
    iORTemplate.add(iIOPProfileTemplate);
    IOR iOR = iORTemplate.makeIOR(paramORB, "", objectKey.getId());
    this.bootstrapDelegate = ORBUtility.makeClientDelegate(iOR);
  }
  
  private InputStream invoke(String paramString1, String paramString2) {
    boolean bool = true;
    InputStream inputStream = null;
    while (bool) {
      Object object = null;
      bool = false;
      OutputStream outputStream = this.bootstrapDelegate.request(object, paramString1, true);
      if (paramString2 != null)
        outputStream.write_string(paramString2); 
      try {
        inputStream = this.bootstrapDelegate.invoke(object, outputStream);
      } catch (ApplicationException applicationException) {
        throw this.wrapper.bootstrapApplicationException(applicationException);
      } catch (RemarshalException remarshalException) {
        bool = true;
      } 
    } 
    return inputStream;
  }
  
  public Object resolve(String paramString) {
    inputStream = null;
    Object object = null;
    try {
      inputStream = invoke("get", paramString);
      object = inputStream.read_Object();
    } finally {
      this.bootstrapDelegate.releaseReply(null, inputStream);
    } 
    return object;
  }
  
  public Set list() {
    inputStream = null;
    HashSet hashSet = new HashSet();
    try {
      inputStream = invoke("list", null);
      int i = inputStream.read_long();
      for (byte b = 0; b < i; b++)
        hashSet.add(inputStream.read_string()); 
    } finally {
      this.bootstrapDelegate.releaseReply(null, inputStream);
    } 
    return hashSet;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\resolver\BootstrapResolverImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */