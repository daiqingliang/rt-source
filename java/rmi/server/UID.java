package java.rmi.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.security.SecureRandom;

public final class UID implements Serializable {
  private static int hostUnique;
  
  private static boolean hostUniqueSet = false;
  
  private static final Object lock = new Object();
  
  private static long lastTime = System.currentTimeMillis();
  
  private static short lastCount = Short.MIN_VALUE;
  
  private static final long serialVersionUID = 1086053664494604050L;
  
  private final int unique;
  
  private final long time;
  
  private final short count;
  
  public UID() {
    synchronized (lock) {
      if (!hostUniqueSet) {
        hostUnique = (new SecureRandom()).nextInt();
        hostUniqueSet = true;
      } 
      this.unique = hostUnique;
      if (lastCount == Short.MAX_VALUE) {
        boolean bool = Thread.interrupted();
        for (boolean bool1 = false; !bool1; bool1 = true) {
          long l = System.currentTimeMillis();
          if (l == lastTime)
            try {
              Thread.sleep(1L);
              continue;
            } catch (InterruptedException interruptedException) {
              bool = true;
              continue;
            }  
          lastTime = (l < lastTime) ? (lastTime + 1L) : l;
          lastCount = Short.MIN_VALUE;
        } 
        if (bool)
          Thread.currentThread().interrupt(); 
      } 
      this.time = lastTime;
      lastCount = (short)(lastCount + 1);
      this.count = lastCount;
    } 
  }
  
  public UID(short paramShort) {
    this.unique = 0;
    this.time = 0L;
    this.count = paramShort;
  }
  
  private UID(int paramInt, long paramLong, short paramShort) {
    this.unique = paramInt;
    this.time = paramLong;
    this.count = paramShort;
  }
  
  public int hashCode() { return (int)this.time + this.count; }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof UID) {
      UID uID = (UID)paramObject;
      return (this.unique == uID.unique && this.count == uID.count && this.time == uID.time);
    } 
    return false;
  }
  
  public String toString() { return Integer.toString(this.unique, 16) + ":" + Long.toString(this.time, 16) + ":" + Integer.toString(this.count, 16); }
  
  public void write(DataOutput paramDataOutput) throws IOException {
    paramDataOutput.writeInt(this.unique);
    paramDataOutput.writeLong(this.time);
    paramDataOutput.writeShort(this.count);
  }
  
  public static UID read(DataInput paramDataInput) throws IOException {
    int i = paramDataInput.readInt();
    long l = paramDataInput.readLong();
    short s = paramDataInput.readShort();
    return new UID(i, l, s);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\server\UID.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */