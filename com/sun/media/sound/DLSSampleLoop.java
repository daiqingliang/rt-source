package com.sun.media.sound;

public final class DLSSampleLoop {
  public static final int LOOP_TYPE_FORWARD = 0;
  
  public static final int LOOP_TYPE_RELEASE = 1;
  
  long type;
  
  long start;
  
  long length;
  
  public long getLength() { return this.length; }
  
  public void setLength(long paramLong) { this.length = paramLong; }
  
  public long getStart() { return this.start; }
  
  public void setStart(long paramLong) { this.start = paramLong; }
  
  public long getType() { return this.type; }
  
  public void setType(long paramLong) { this.type = paramLong; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\DLSSampleLoop.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */