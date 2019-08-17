package sun.java2d.cmm;

import java.awt.color.CMMException;

public class Profile {
  private final long nativePtr;
  
  protected Profile(long paramLong) { this.nativePtr = paramLong; }
  
  protected final long getNativePtr() {
    if (this.nativePtr == 0L)
      throw new CMMException("Invalid profile: ptr is null"); 
    return this.nativePtr;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\cmm\Profile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */