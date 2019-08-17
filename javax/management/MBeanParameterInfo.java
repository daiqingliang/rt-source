package javax.management;

import java.util.Objects;

public class MBeanParameterInfo extends MBeanFeatureInfo implements Cloneable {
  static final long serialVersionUID = 7432616882776782338L;
  
  static final MBeanParameterInfo[] NO_PARAMS = new MBeanParameterInfo[0];
  
  private final String type;
  
  public MBeanParameterInfo(String paramString1, String paramString2, String paramString3) { this(paramString1, paramString2, paramString3, (Descriptor)null); }
  
  public MBeanParameterInfo(String paramString1, String paramString2, String paramString3, Descriptor paramDescriptor) {
    super(paramString1, paramString3, paramDescriptor);
    this.type = paramString2;
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
  }
  
  public String getType() { return this.type; }
  
  public String toString() { return getClass().getName() + "[description=" + getDescription() + ", name=" + getName() + ", type=" + getType() + ", descriptor=" + getDescriptor() + "]"; }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof MBeanParameterInfo))
      return false; 
    MBeanParameterInfo mBeanParameterInfo = (MBeanParameterInfo)paramObject;
    return (Objects.equals(mBeanParameterInfo.getName(), getName()) && Objects.equals(mBeanParameterInfo.getType(), getType()) && Objects.equals(mBeanParameterInfo.getDescription(), getDescription()) && Objects.equals(mBeanParameterInfo.getDescriptor(), getDescriptor()));
  }
  
  public int hashCode() { return Objects.hash(new Object[] { getName(), getType() }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\MBeanParameterInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */