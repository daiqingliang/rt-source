package javax.management;

import com.sun.jmx.mbeanserver.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Objects;

public class MBeanConstructorInfo extends MBeanFeatureInfo implements Cloneable {
  static final long serialVersionUID = 4433990064191844427L;
  
  static final MBeanConstructorInfo[] NO_CONSTRUCTORS = new MBeanConstructorInfo[0];
  
  private final boolean arrayGettersSafe;
  
  private final MBeanParameterInfo[] signature;
  
  public MBeanConstructorInfo(String paramString, Constructor<?> paramConstructor) { this(paramConstructor.getName(), paramString, constructorSignature(paramConstructor), Introspector.descriptorForElement(paramConstructor)); }
  
  public MBeanConstructorInfo(String paramString1, String paramString2, MBeanParameterInfo[] paramArrayOfMBeanParameterInfo) { this(paramString1, paramString2, paramArrayOfMBeanParameterInfo, null); }
  
  public MBeanConstructorInfo(String paramString1, String paramString2, MBeanParameterInfo[] paramArrayOfMBeanParameterInfo, Descriptor paramDescriptor) {
    super(paramString1, paramString2, paramDescriptor);
    if (paramArrayOfMBeanParameterInfo == null || paramArrayOfMBeanParameterInfo.length == 0) {
      paramArrayOfMBeanParameterInfo = MBeanParameterInfo.NO_PARAMS;
    } else {
      paramArrayOfMBeanParameterInfo = (MBeanParameterInfo[])paramArrayOfMBeanParameterInfo.clone();
    } 
    this.signature = paramArrayOfMBeanParameterInfo;
    this.arrayGettersSafe = MBeanInfo.arrayGettersSafe(getClass(), MBeanConstructorInfo.class);
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
  }
  
  public MBeanParameterInfo[] getSignature() { return (this.signature.length == 0) ? this.signature : (MBeanParameterInfo[])this.signature.clone(); }
  
  private MBeanParameterInfo[] fastGetSignature() { return this.arrayGettersSafe ? this.signature : getSignature(); }
  
  public String toString() { return getClass().getName() + "[description=" + getDescription() + ", name=" + getName() + ", signature=" + Arrays.asList(fastGetSignature()) + ", descriptor=" + getDescriptor() + "]"; }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof MBeanConstructorInfo))
      return false; 
    MBeanConstructorInfo mBeanConstructorInfo = (MBeanConstructorInfo)paramObject;
    return (Objects.equals(mBeanConstructorInfo.getName(), getName()) && Objects.equals(mBeanConstructorInfo.getDescription(), getDescription()) && Arrays.equals(mBeanConstructorInfo.fastGetSignature(), fastGetSignature()) && Objects.equals(mBeanConstructorInfo.getDescriptor(), getDescriptor()));
  }
  
  public int hashCode() { return Objects.hash(new Object[] { getName() }) ^ Arrays.hashCode(fastGetSignature()); }
  
  private static MBeanParameterInfo[] constructorSignature(Constructor<?> paramConstructor) {
    Class[] arrayOfClass = paramConstructor.getParameterTypes();
    Annotation[][] arrayOfAnnotation = paramConstructor.getParameterAnnotations();
    return MBeanOperationInfo.parameters(arrayOfClass, arrayOfAnnotation);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\MBeanConstructorInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */