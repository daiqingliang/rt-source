package com.sun.xml.internal.stream.util;

import java.lang.ref.SoftReference;

public class ThreadLocalBufferAllocator {
  private static ThreadLocal tlba = new ThreadLocal();
  
  public static BufferAllocator getBufferAllocator() {
    SoftReference softReference = (SoftReference)tlba.get();
    if (softReference == null || softReference.get() == null) {
      softReference = new SoftReference(new BufferAllocator());
      tlba.set(softReference);
    } 
    return (BufferAllocator)softReference.get();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\strea\\util\ThreadLocalBufferAllocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */