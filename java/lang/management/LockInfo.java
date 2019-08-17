package java.lang.management;

import java.lang.management.LockInfo;
import javax.management.openmbean.CompositeData;
import sun.management.LockInfoCompositeData;

public class LockInfo {
  private String className;
  
  private int identityHashCode;
  
  public LockInfo(String paramString, int paramInt) {
    if (paramString == null)
      throw new NullPointerException("Parameter className cannot be null"); 
    this.className = paramString;
    this.identityHashCode = paramInt;
  }
  
  LockInfo(Object paramObject) {
    this.className = paramObject.getClass().getName();
    this.identityHashCode = System.identityHashCode(paramObject);
  }
  
  public String getClassName() { return this.className; }
  
  public int getIdentityHashCode() { return this.identityHashCode; }
  
  public static LockInfo from(CompositeData paramCompositeData) { return (paramCompositeData == null) ? null : ((paramCompositeData instanceof LockInfoCompositeData) ? ((LockInfoCompositeData)paramCompositeData).getLockInfo() : LockInfoCompositeData.toLockInfo(paramCompositeData)); }
  
  public String toString() { return this.className + '@' + Integer.toHexString(this.identityHashCode); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\management\LockInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */