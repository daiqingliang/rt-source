package sun.management;

import java.lang.management.LockInfo;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;

public class LockInfoCompositeData extends LazyCompositeData {
  private final LockInfo lock;
  
  private static final CompositeType lockInfoCompositeType;
  
  private static final String CLASS_NAME = "className";
  
  private static final String IDENTITY_HASH_CODE = "identityHashCode";
  
  private static final String[] lockInfoItemNames;
  
  private static final long serialVersionUID = -6374759159749014052L;
  
  private LockInfoCompositeData(LockInfo paramLockInfo) { this.lock = paramLockInfo; }
  
  public LockInfo getLockInfo() { return this.lock; }
  
  public static CompositeData toCompositeData(LockInfo paramLockInfo) {
    if (paramLockInfo == null)
      return null; 
    LockInfoCompositeData lockInfoCompositeData = new LockInfoCompositeData(paramLockInfo);
    return lockInfoCompositeData.getCompositeData();
  }
  
  protected CompositeData getCompositeData() {
    Object[] arrayOfObject = { new String(this.lock.getClassName()), new Integer(this.lock.getIdentityHashCode()) };
    try {
      return new CompositeDataSupport(lockInfoCompositeType, lockInfoItemNames, arrayOfObject);
    } catch (OpenDataException openDataException) {
      throw Util.newException(openDataException);
    } 
  }
  
  static CompositeType getLockInfoCompositeType() { return lockInfoCompositeType; }
  
  public static LockInfo toLockInfo(CompositeData paramCompositeData) {
    if (paramCompositeData == null)
      throw new NullPointerException("Null CompositeData"); 
    if (!isTypeMatched(lockInfoCompositeType, paramCompositeData.getCompositeType()))
      throw new IllegalArgumentException("Unexpected composite type for LockInfo"); 
    String str = getString(paramCompositeData, "className");
    int i = getInt(paramCompositeData, "identityHashCode");
    return new LockInfo(str, i);
  }
  
  static  {
    try {
      lockInfoCompositeType = (CompositeType)MappedMXBeanType.toOpenType(LockInfo.class);
    } catch (OpenDataException openDataException) {
      throw Util.newException(openDataException);
    } 
    lockInfoItemNames = new String[] { "className", "identityHashCode" };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\LockInfoCompositeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */