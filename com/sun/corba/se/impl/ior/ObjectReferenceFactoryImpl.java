package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORFactory;
import com.sun.corba.se.spi.ior.IORTemplateList;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.StreamableValue;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.omg.PortableInterceptor.ObjectReferenceFactoryHelper;

public class ObjectReferenceFactoryImpl extends ObjectReferenceProducerBase implements ObjectReferenceFactory, StreamableValue {
  private IORTemplateList iorTemplates;
  
  public static final String repositoryId = "IDL:com/sun/corba/se/impl/ior/ObjectReferenceFactoryImpl:1.0";
  
  public ObjectReferenceFactoryImpl(InputStream paramInputStream) {
    super((ORB)paramInputStream.orb());
    _read(paramInputStream);
  }
  
  public ObjectReferenceFactoryImpl(ORB paramORB, IORTemplateList paramIORTemplateList) {
    super(paramORB);
    this.iorTemplates = paramIORTemplateList;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof ObjectReferenceFactoryImpl))
      return false; 
    ObjectReferenceFactoryImpl objectReferenceFactoryImpl = (ObjectReferenceFactoryImpl)paramObject;
    return (this.iorTemplates != null && this.iorTemplates.equals(objectReferenceFactoryImpl.iorTemplates));
  }
  
  public int hashCode() { return this.iorTemplates.hashCode(); }
  
  public String[] _truncatable_ids() { return new String[] { "IDL:com/sun/corba/se/impl/ior/ObjectReferenceFactoryImpl:1.0" }; }
  
  public TypeCode _type() { return ObjectReferenceFactoryHelper.type(); }
  
  public void _read(InputStream paramInputStream) {
    InputStream inputStream = (InputStream)paramInputStream;
    this.iorTemplates = IORFactories.makeIORTemplateList(inputStream);
  }
  
  public void _write(OutputStream paramOutputStream) {
    OutputStream outputStream = (OutputStream)paramOutputStream;
    this.iorTemplates.write(outputStream);
  }
  
  public IORFactory getIORFactory() { return this.iorTemplates; }
  
  public IORTemplateList getIORTemplateList() { return this.iorTemplates; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\ObjectReferenceFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */