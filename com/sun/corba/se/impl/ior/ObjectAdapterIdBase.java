package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.ObjectAdapterId;
import java.util.Iterator;
import org.omg.CORBA_2_3.portable.OutputStream;

abstract class ObjectAdapterIdBase implements ObjectAdapterId {
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof ObjectAdapterId))
      return false; 
    ObjectAdapterId objectAdapterId = (ObjectAdapterId)paramObject;
    Iterator iterator1 = iterator();
    Iterator iterator2 = objectAdapterId.iterator();
    while (iterator1.hasNext() && iterator2.hasNext()) {
      String str1 = (String)iterator1.next();
      String str2 = (String)iterator2.next();
      if (!str1.equals(str2))
        return false; 
    } 
    return (iterator1.hasNext() == iterator2.hasNext());
  }
  
  public int hashCode() {
    int i = 17;
    for (String str : this)
      i = 37 * i + str.hashCode(); 
    return i;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("ObjectAdapterID[");
    Iterator iterator = iterator();
    boolean bool = true;
    while (iterator.hasNext()) {
      String str = (String)iterator.next();
      if (bool) {
        bool = false;
      } else {
        stringBuffer.append("/");
      } 
      stringBuffer.append(str);
    } 
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  public void write(OutputStream paramOutputStream) {
    paramOutputStream.write_long(getNumLevels());
    for (String str : this)
      paramOutputStream.write_string(str); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\ObjectAdapterIdBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */