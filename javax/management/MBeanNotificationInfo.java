package javax.management;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Objects;

public class MBeanNotificationInfo extends MBeanFeatureInfo implements Cloneable {
  static final long serialVersionUID = -3888371564530107064L;
  
  private static final String[] NO_TYPES = new String[0];
  
  static final MBeanNotificationInfo[] NO_NOTIFICATIONS = new MBeanNotificationInfo[0];
  
  private String[] types;
  
  private final boolean arrayGettersSafe;
  
  public MBeanNotificationInfo(String[] paramArrayOfString, String paramString1, String paramString2) { this(paramArrayOfString, paramString1, paramString2, null); }
  
  public MBeanNotificationInfo(String[] paramArrayOfString, String paramString1, String paramString2, Descriptor paramDescriptor) {
    super(paramString1, paramString2, paramDescriptor);
    this.types = (paramArrayOfString != null && paramArrayOfString.length > 0) ? (String[])paramArrayOfString.clone() : NO_TYPES;
    this.arrayGettersSafe = MBeanInfo.arrayGettersSafe(getClass(), MBeanNotificationInfo.class);
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
  }
  
  public String[] getNotifTypes() { return (this.types.length == 0) ? NO_TYPES : (String[])this.types.clone(); }
  
  private String[] fastGetNotifTypes() { return this.arrayGettersSafe ? this.types : getNotifTypes(); }
  
  public String toString() { return getClass().getName() + "[description=" + getDescription() + ", name=" + getName() + ", notifTypes=" + Arrays.asList(fastGetNotifTypes()) + ", descriptor=" + getDescriptor() + "]"; }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof MBeanNotificationInfo))
      return false; 
    MBeanNotificationInfo mBeanNotificationInfo = (MBeanNotificationInfo)paramObject;
    return (Objects.equals(mBeanNotificationInfo.getName(), getName()) && Objects.equals(mBeanNotificationInfo.getDescription(), getDescription()) && Objects.equals(mBeanNotificationInfo.getDescriptor(), getDescriptor()) && Arrays.equals(mBeanNotificationInfo.fastGetNotifTypes(), fastGetNotifTypes()));
  }
  
  public int hashCode() {
    int i = getName().hashCode();
    for (byte b = 0; b < this.types.length; b++)
      i ^= this.types[b].hashCode(); 
    return i;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    String[] arrayOfString = (String[])getField.get("types", null);
    this.types = (arrayOfString != null && arrayOfString.length != 0) ? (String[])arrayOfString.clone() : NO_TYPES;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\MBeanNotificationInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */