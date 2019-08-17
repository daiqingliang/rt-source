package sun.management;

import com.sun.management.GcInfo;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryUsage;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

public class GcInfoBuilder {
  private final GarbageCollectorMXBean gc;
  
  private final String[] poolNames;
  
  private String[] allItemNames;
  
  private CompositeType gcInfoCompositeType;
  
  private final int gcExtItemCount;
  
  private final String[] gcExtItemNames;
  
  private final String[] gcExtItemDescs;
  
  private final char[] gcExtItemTypes;
  
  GcInfoBuilder(GarbageCollectorMXBean paramGarbageCollectorMXBean, String[] paramArrayOfString) {
    this.gc = paramGarbageCollectorMXBean;
    this.poolNames = paramArrayOfString;
    this.gcExtItemCount = getNumGcExtAttributes(paramGarbageCollectorMXBean);
    this.gcExtItemNames = new String[this.gcExtItemCount];
    this.gcExtItemDescs = new String[this.gcExtItemCount];
    this.gcExtItemTypes = new char[this.gcExtItemCount];
    fillGcAttributeInfo(paramGarbageCollectorMXBean, this.gcExtItemCount, this.gcExtItemNames, this.gcExtItemTypes, this.gcExtItemDescs);
    this.gcInfoCompositeType = null;
  }
  
  GcInfo getLastGcInfo() {
    MemoryUsage[] arrayOfMemoryUsage1 = new MemoryUsage[this.poolNames.length];
    MemoryUsage[] arrayOfMemoryUsage2 = new MemoryUsage[this.poolNames.length];
    Object[] arrayOfObject = new Object[this.gcExtItemCount];
    return getLastGcInfo0(this.gc, this.gcExtItemCount, arrayOfObject, this.gcExtItemTypes, arrayOfMemoryUsage1, arrayOfMemoryUsage2);
  }
  
  public String[] getPoolNames() { return this.poolNames; }
  
  int getGcExtItemCount() { return this.gcExtItemCount; }
  
  CompositeType getGcInfoCompositeType() {
    if (this.gcInfoCompositeType != null)
      return this.gcInfoCompositeType; 
    String[] arrayOfString1 = GcInfoCompositeData.getBaseGcInfoItemNames();
    OpenType[] arrayOfOpenType1 = GcInfoCompositeData.getBaseGcInfoItemTypes();
    int i = arrayOfString1.length;
    int j = i + this.gcExtItemCount;
    this.allItemNames = new String[j];
    String[] arrayOfString2 = new String[j];
    OpenType[] arrayOfOpenType2 = new OpenType[j];
    System.arraycopy(arrayOfString1, 0, this.allItemNames, 0, i);
    System.arraycopy(arrayOfString1, 0, arrayOfString2, 0, i);
    System.arraycopy(arrayOfOpenType1, 0, arrayOfOpenType2, 0, i);
    if (this.gcExtItemCount > 0) {
      fillGcAttributeInfo(this.gc, this.gcExtItemCount, this.gcExtItemNames, this.gcExtItemTypes, this.gcExtItemDescs);
      System.arraycopy(this.gcExtItemNames, 0, this.allItemNames, i, this.gcExtItemCount);
      System.arraycopy(this.gcExtItemDescs, 0, arrayOfString2, i, this.gcExtItemCount);
      int k = i;
      for (byte b = 0; b < this.gcExtItemCount; b++) {
        switch (this.gcExtItemTypes[b]) {
          case 'Z':
            arrayOfOpenType2[k] = SimpleType.BOOLEAN;
            break;
          case 'B':
            arrayOfOpenType2[k] = SimpleType.BYTE;
            break;
          case 'C':
            arrayOfOpenType2[k] = SimpleType.CHARACTER;
            break;
          case 'S':
            arrayOfOpenType2[k] = SimpleType.SHORT;
            break;
          case 'I':
            arrayOfOpenType2[k] = SimpleType.INTEGER;
            break;
          case 'J':
            arrayOfOpenType2[k] = SimpleType.LONG;
            break;
          case 'F':
            arrayOfOpenType2[k] = SimpleType.FLOAT;
            break;
          case 'D':
            arrayOfOpenType2[k] = SimpleType.DOUBLE;
            break;
          default:
            throw new AssertionError("Unsupported type [" + this.gcExtItemTypes[k] + "]");
        } 
        k++;
      } 
    } 
    CompositeType compositeType = null;
    try {
      String str = "sun.management." + this.gc.getName() + ".GcInfoCompositeType";
      compositeType = new CompositeType(str, "CompositeType for GC info for " + this.gc.getName(), this.allItemNames, arrayOfString2, arrayOfOpenType2);
    } catch (OpenDataException openDataException) {
      throw Util.newException(openDataException);
    } 
    this.gcInfoCompositeType = compositeType;
    return this.gcInfoCompositeType;
  }
  
  String[] getItemNames() {
    if (this.allItemNames == null)
      getGcInfoCompositeType(); 
    return this.allItemNames;
  }
  
  private native int getNumGcExtAttributes(GarbageCollectorMXBean paramGarbageCollectorMXBean);
  
  private native void fillGcAttributeInfo(GarbageCollectorMXBean paramGarbageCollectorMXBean, int paramInt, String[] paramArrayOfString1, char[] paramArrayOfChar, String[] paramArrayOfString2);
  
  private native GcInfo getLastGcInfo0(GarbageCollectorMXBean paramGarbageCollectorMXBean, int paramInt, Object[] paramArrayOfObject, char[] paramArrayOfChar, MemoryUsage[] paramArrayOfMemoryUsage1, MemoryUsage[] paramArrayOfMemoryUsage2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\GcInfoBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */