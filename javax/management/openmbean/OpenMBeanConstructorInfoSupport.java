package javax.management.openmbean;

import java.util.Arrays;
import javax.management.Descriptor;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanParameterInfo;

public class OpenMBeanConstructorInfoSupport extends MBeanConstructorInfo implements OpenMBeanConstructorInfo {
  static final long serialVersionUID = -4400441579007477003L;
  
  private Integer myHashCode = null;
  
  private String myToString = null;
  
  public OpenMBeanConstructorInfoSupport(String paramString1, String paramString2, OpenMBeanParameterInfo[] paramArrayOfOpenMBeanParameterInfo) { this(paramString1, paramString2, paramArrayOfOpenMBeanParameterInfo, (Descriptor)null); }
  
  public OpenMBeanConstructorInfoSupport(String paramString1, String paramString2, OpenMBeanParameterInfo[] paramArrayOfOpenMBeanParameterInfo, Descriptor paramDescriptor) {
    super(paramString1, paramString2, arrayCopyCast(paramArrayOfOpenMBeanParameterInfo), paramDescriptor);
    if (paramString1 == null || paramString1.trim().equals(""))
      throw new IllegalArgumentException("Argument name cannot be null or empty"); 
    if (paramString2 == null || paramString2.trim().equals(""))
      throw new IllegalArgumentException("Argument description cannot be null or empty"); 
  }
  
  private static MBeanParameterInfo[] arrayCopyCast(OpenMBeanParameterInfo[] paramArrayOfOpenMBeanParameterInfo) {
    if (paramArrayOfOpenMBeanParameterInfo == null)
      return null; 
    MBeanParameterInfo[] arrayOfMBeanParameterInfo = new MBeanParameterInfo[paramArrayOfOpenMBeanParameterInfo.length];
    System.arraycopy(paramArrayOfOpenMBeanParameterInfo, 0, arrayOfMBeanParameterInfo, 0, paramArrayOfOpenMBeanParameterInfo.length);
    return arrayOfMBeanParameterInfo;
  }
  
  public boolean equals(Object paramObject) {
    OpenMBeanConstructorInfo openMBeanConstructorInfo;
    if (paramObject == null)
      return false; 
    try {
      openMBeanConstructorInfo = (OpenMBeanConstructorInfo)paramObject;
    } catch (ClassCastException classCastException) {
      return false;
    } 
    return !getName().equals(openMBeanConstructorInfo.getName()) ? false : (!!Arrays.equals(getSignature(), openMBeanConstructorInfo.getSignature()));
  }
  
  public int hashCode() {
    if (this.myHashCode == null) {
      int i = 0;
      i += getName().hashCode();
      i += Arrays.asList(getSignature()).hashCode();
      this.myHashCode = Integer.valueOf(i);
    } 
    return this.myHashCode.intValue();
  }
  
  public String toString() {
    if (this.myToString == null)
      this.myToString = getClass().getName() + "(name=" + getName() + ",signature=" + Arrays.asList(getSignature()).toString() + ",descriptor=" + getDescriptor() + ")"; 
    return this.myToString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\openmbean\OpenMBeanConstructorInfoSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */