package javax.management;

import com.sun.jmx.mbeanserver.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class MBeanOperationInfo extends MBeanFeatureInfo implements Cloneable {
  static final long serialVersionUID = -6178860474881375330L;
  
  static final MBeanOperationInfo[] NO_OPERATIONS = new MBeanOperationInfo[0];
  
  public static final int INFO = 0;
  
  public static final int ACTION = 1;
  
  public static final int ACTION_INFO = 2;
  
  public static final int UNKNOWN = 3;
  
  private final String type;
  
  private final MBeanParameterInfo[] signature;
  
  private final int impact;
  
  private final boolean arrayGettersSafe;
  
  public MBeanOperationInfo(String paramString, Method paramMethod) { this(paramMethod.getName(), paramString, methodSignature(paramMethod), paramMethod.getReturnType().getName(), 3, Introspector.descriptorForElement(paramMethod)); }
  
  public MBeanOperationInfo(String paramString1, String paramString2, MBeanParameterInfo[] paramArrayOfMBeanParameterInfo, String paramString3, int paramInt) { this(paramString1, paramString2, paramArrayOfMBeanParameterInfo, paramString3, paramInt, (Descriptor)null); }
  
  public MBeanOperationInfo(String paramString1, String paramString2, MBeanParameterInfo[] paramArrayOfMBeanParameterInfo, String paramString3, int paramInt, Descriptor paramDescriptor) {
    super(paramString1, paramString2, paramDescriptor);
    if (paramArrayOfMBeanParameterInfo == null || paramArrayOfMBeanParameterInfo.length == 0) {
      paramArrayOfMBeanParameterInfo = MBeanParameterInfo.NO_PARAMS;
    } else {
      paramArrayOfMBeanParameterInfo = (MBeanParameterInfo[])paramArrayOfMBeanParameterInfo.clone();
    } 
    this.signature = paramArrayOfMBeanParameterInfo;
    this.type = paramString3;
    this.impact = paramInt;
    this.arrayGettersSafe = MBeanInfo.arrayGettersSafe(getClass(), MBeanOperationInfo.class);
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
  }
  
  public String getReturnType() { return this.type; }
  
  public MBeanParameterInfo[] getSignature() { return (this.signature == null) ? MBeanParameterInfo.NO_PARAMS : ((this.signature.length == 0) ? this.signature : (MBeanParameterInfo[])this.signature.clone()); }
  
  private MBeanParameterInfo[] fastGetSignature() { return this.arrayGettersSafe ? ((this.signature == null) ? MBeanParameterInfo.NO_PARAMS : this.signature) : getSignature(); }
  
  public int getImpact() { return this.impact; }
  
  public String toString() {
    switch (getImpact()) {
      case 1:
        str = "action";
        return getClass().getName() + "[description=" + getDescription() + ", name=" + getName() + ", returnType=" + getReturnType() + ", signature=" + Arrays.asList(fastGetSignature()) + ", impact=" + str + ", descriptor=" + getDescriptor() + "]";
      case 2:
        str = "action/info";
        return getClass().getName() + "[description=" + getDescription() + ", name=" + getName() + ", returnType=" + getReturnType() + ", signature=" + Arrays.asList(fastGetSignature()) + ", impact=" + str + ", descriptor=" + getDescriptor() + "]";
      case 0:
        str = "info";
        return getClass().getName() + "[description=" + getDescription() + ", name=" + getName() + ", returnType=" + getReturnType() + ", signature=" + Arrays.asList(fastGetSignature()) + ", impact=" + str + ", descriptor=" + getDescriptor() + "]";
      case 3:
        str = "unknown";
        return getClass().getName() + "[description=" + getDescription() + ", name=" + getName() + ", returnType=" + getReturnType() + ", signature=" + Arrays.asList(fastGetSignature()) + ", impact=" + str + ", descriptor=" + getDescriptor() + "]";
    } 
    String str = "(" + getImpact() + ")";
    return getClass().getName() + "[description=" + getDescription() + ", name=" + getName() + ", returnType=" + getReturnType() + ", signature=" + Arrays.asList(fastGetSignature()) + ", impact=" + str + ", descriptor=" + getDescriptor() + "]";
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof MBeanOperationInfo))
      return false; 
    MBeanOperationInfo mBeanOperationInfo = (MBeanOperationInfo)paramObject;
    return (Objects.equals(mBeanOperationInfo.getName(), getName()) && Objects.equals(mBeanOperationInfo.getReturnType(), getReturnType()) && Objects.equals(mBeanOperationInfo.getDescription(), getDescription()) && mBeanOperationInfo.getImpact() == getImpact() && Arrays.equals(mBeanOperationInfo.fastGetSignature(), fastGetSignature()) && Objects.equals(mBeanOperationInfo.getDescriptor(), getDescriptor()));
  }
  
  public int hashCode() { return Objects.hash(new Object[] { getName(), getReturnType() }); }
  
  private static MBeanParameterInfo[] methodSignature(Method paramMethod) {
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    Annotation[][] arrayOfAnnotation = paramMethod.getParameterAnnotations();
    return parameters(arrayOfClass, arrayOfAnnotation);
  }
  
  static MBeanParameterInfo[] parameters(Class<?>[] paramArrayOfClass, Annotation[][] paramArrayOfAnnotation) {
    MBeanParameterInfo[] arrayOfMBeanParameterInfo = new MBeanParameterInfo[paramArrayOfClass.length];
    assert paramArrayOfClass.length == paramArrayOfAnnotation.length;
    for (byte b = 0; b < paramArrayOfClass.length; b++) {
      Descriptor descriptor = Introspector.descriptorForAnnotations(paramArrayOfAnnotation[b]);
      String str = "p" + (b + true);
      arrayOfMBeanParameterInfo[b] = new MBeanParameterInfo(str, paramArrayOfClass[b].getName(), "", descriptor);
    } 
    return arrayOfMBeanParameterInfo;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\MBeanOperationInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */