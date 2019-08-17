package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORFactory;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.IORTemplateList;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.orb.ORB;
import java.util.ArrayList;
import java.util.Iterator;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class IORTemplateListImpl extends FreezableList implements IORTemplateList {
  public Object set(int paramInt, Object paramObject) {
    if (paramObject instanceof IORTemplate)
      return super.set(paramInt, paramObject); 
    if (paramObject instanceof IORTemplateList) {
      Object object = remove(paramInt);
      add(paramInt, paramObject);
      return object;
    } 
    throw new IllegalArgumentException();
  }
  
  public void add(int paramInt, Object paramObject) {
    if (paramObject instanceof IORTemplate) {
      super.add(paramInt, paramObject);
    } else if (paramObject instanceof IORTemplateList) {
      IORTemplateList iORTemplateList = (IORTemplateList)paramObject;
      addAll(paramInt, iORTemplateList);
    } else {
      throw new IllegalArgumentException();
    } 
  }
  
  public IORTemplateListImpl() { super(new ArrayList()); }
  
  public IORTemplateListImpl(InputStream paramInputStream) {
    this();
    int i = paramInputStream.read_long();
    for (byte b = 0; b < i; b++) {
      IORTemplate iORTemplate = IORFactories.makeIORTemplate(paramInputStream);
      add(iORTemplate);
    } 
    makeImmutable();
  }
  
  public void makeImmutable() {
    makeElementsImmutable();
    super.makeImmutable();
  }
  
  public void write(OutputStream paramOutputStream) {
    paramOutputStream.write_long(size());
    for (IORTemplate iORTemplate : this)
      iORTemplate.write(paramOutputStream); 
  }
  
  public IOR makeIOR(ORB paramORB, String paramString, ObjectId paramObjectId) { return new IORImpl(paramORB, paramString, this, paramObjectId); }
  
  public boolean isEquivalent(IORFactory paramIORFactory) {
    if (!(paramIORFactory instanceof IORTemplateList))
      return false; 
    IORTemplateList iORTemplateList = (IORTemplateList)paramIORFactory;
    Iterator iterator1 = iterator();
    Iterator iterator2 = iORTemplateList.iterator();
    while (iterator1.hasNext() && iterator2.hasNext()) {
      IORTemplate iORTemplate1 = (IORTemplate)iterator1.next();
      IORTemplate iORTemplate2 = (IORTemplate)iterator2.next();
      if (!iORTemplate1.isEquivalent(iORTemplate2))
        return false; 
    } 
    return (iterator1.hasNext() == iterator2.hasNext());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\IORTemplateListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */