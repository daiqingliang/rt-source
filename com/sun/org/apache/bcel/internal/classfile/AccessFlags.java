package com.sun.org.apache.bcel.internal.classfile;

import java.io.Serializable;

public abstract class AccessFlags implements Serializable {
  protected int access_flags;
  
  public AccessFlags() {}
  
  public AccessFlags(int paramInt) { this.access_flags = paramInt; }
  
  public final int getAccessFlags() { return this.access_flags; }
  
  public final int getModifiers() { return this.access_flags; }
  
  public final void setAccessFlags(int paramInt) { this.access_flags = paramInt; }
  
  public final void setModifiers(int paramInt) { setAccessFlags(paramInt); }
  
  private final void setFlag(int paramInt, boolean paramBoolean) {
    if ((this.access_flags & paramInt) != 0) {
      if (!paramBoolean)
        this.access_flags ^= paramInt; 
    } else if (paramBoolean) {
      this.access_flags |= paramInt;
    } 
  }
  
  public final void isPublic(boolean paramBoolean) { setFlag(1, paramBoolean); }
  
  public final boolean isPublic() { return ((this.access_flags & true) != 0); }
  
  public final void isPrivate(boolean paramBoolean) { setFlag(2, paramBoolean); }
  
  public final boolean isPrivate() { return ((this.access_flags & 0x2) != 0); }
  
  public final void isProtected(boolean paramBoolean) { setFlag(4, paramBoolean); }
  
  public final boolean isProtected() { return ((this.access_flags & 0x4) != 0); }
  
  public final void isStatic(boolean paramBoolean) { setFlag(8, paramBoolean); }
  
  public final boolean isStatic() { return ((this.access_flags & 0x8) != 0); }
  
  public final void isFinal(boolean paramBoolean) { setFlag(16, paramBoolean); }
  
  public final boolean isFinal() { return ((this.access_flags & 0x10) != 0); }
  
  public final void isSynchronized(boolean paramBoolean) { setFlag(32, paramBoolean); }
  
  public final boolean isSynchronized() { return ((this.access_flags & 0x20) != 0); }
  
  public final void isVolatile(boolean paramBoolean) { setFlag(64, paramBoolean); }
  
  public final boolean isVolatile() { return ((this.access_flags & 0x40) != 0); }
  
  public final void isTransient(boolean paramBoolean) { setFlag(128, paramBoolean); }
  
  public final boolean isTransient() { return ((this.access_flags & 0x80) != 0); }
  
  public final void isNative(boolean paramBoolean) { setFlag(256, paramBoolean); }
  
  public final boolean isNative() { return ((this.access_flags & 0x100) != 0); }
  
  public final void isInterface(boolean paramBoolean) { setFlag(512, paramBoolean); }
  
  public final boolean isInterface() { return ((this.access_flags & 0x200) != 0); }
  
  public final void isAbstract(boolean paramBoolean) { setFlag(1024, paramBoolean); }
  
  public final boolean isAbstract() { return ((this.access_flags & 0x400) != 0); }
  
  public final void isStrictfp(boolean paramBoolean) { setFlag(2048, paramBoolean); }
  
  public final boolean isStrictfp() { return ((this.access_flags & 0x800) != 0); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\AccessFlags.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */