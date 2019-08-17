package com.sun.corba.se.impl.ior;

import java.util.Arrays;
import java.util.Iterator;
import org.omg.CORBA_2_3.portable.OutputStream;

public class ObjectAdapterIdArray extends ObjectAdapterIdBase {
  private final String[] objectAdapterId;
  
  public ObjectAdapterIdArray(String[] paramArrayOfString) { this.objectAdapterId = paramArrayOfString; }
  
  public ObjectAdapterIdArray(String paramString1, String paramString2) {
    this.objectAdapterId = new String[2];
    this.objectAdapterId[0] = paramString1;
    this.objectAdapterId[1] = paramString2;
  }
  
  public int getNumLevels() { return this.objectAdapterId.length; }
  
  public Iterator iterator() { return Arrays.asList(this.objectAdapterId).iterator(); }
  
  public String[] getAdapterName() { return (String[])this.objectAdapterId.clone(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\ObjectAdapterIdArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */