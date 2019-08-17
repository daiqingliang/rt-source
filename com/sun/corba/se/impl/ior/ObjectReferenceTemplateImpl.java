package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORFactory;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.IORTemplateList;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.StreamableValue;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import org.omg.PortableInterceptor.ObjectReferenceTemplateHelper;

public class ObjectReferenceTemplateImpl extends ObjectReferenceProducerBase implements ObjectReferenceTemplate, StreamableValue {
  private IORTemplate iorTemplate;
  
  public static final String repositoryId = "IDL:com/sun/corba/se/impl/ior/ObjectReferenceTemplateImpl:1.0";
  
  public ObjectReferenceTemplateImpl(InputStream paramInputStream) {
    super((ORB)paramInputStream.orb());
    _read(paramInputStream);
  }
  
  public ObjectReferenceTemplateImpl(ORB paramORB, IORTemplate paramIORTemplate) {
    super(paramORB);
    this.iorTemplate = paramIORTemplate;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof ObjectReferenceTemplateImpl))
      return false; 
    ObjectReferenceTemplateImpl objectReferenceTemplateImpl = (ObjectReferenceTemplateImpl)paramObject;
    return (this.iorTemplate != null && this.iorTemplate.equals(objectReferenceTemplateImpl.iorTemplate));
  }
  
  public int hashCode() { return this.iorTemplate.hashCode(); }
  
  public String[] _truncatable_ids() { return new String[] { "IDL:com/sun/corba/se/impl/ior/ObjectReferenceTemplateImpl:1.0" }; }
  
  public TypeCode _type() { return ObjectReferenceTemplateHelper.type(); }
  
  public void _read(InputStream paramInputStream) {
    InputStream inputStream = (InputStream)paramInputStream;
    this.iorTemplate = IORFactories.makeIORTemplate(inputStream);
    this.orb = (ORB)inputStream.orb();
  }
  
  public void _write(OutputStream paramOutputStream) {
    OutputStream outputStream = (OutputStream)paramOutputStream;
    this.iorTemplate.write(outputStream);
  }
  
  public String server_id() {
    int i = this.iorTemplate.getObjectKeyTemplate().getServerId();
    return Integer.toString(i);
  }
  
  public String orb_id() { return this.iorTemplate.getObjectKeyTemplate().getORBId(); }
  
  public String[] adapter_name() {
    ObjectAdapterId objectAdapterId = this.iorTemplate.getObjectKeyTemplate().getObjectAdapterId();
    return objectAdapterId.getAdapterName();
  }
  
  public IORFactory getIORFactory() { return this.iorTemplate; }
  
  public IORTemplateList getIORTemplateList() {
    IORTemplateList iORTemplateList = IORFactories.makeIORTemplateList();
    iORTemplateList.add(this.iorTemplate);
    iORTemplateList.makeImmutable();
    return iORTemplateList;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\ObjectReferenceTemplateImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */