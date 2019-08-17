package jdk.management.resource.internal;

import java.io.FileDescriptor;
import java.util.Objects;
import jdk.Exported;
import jdk.management.resource.ResourceAccuracy;
import jdk.management.resource.ResourceId;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

@Exported(false)
public class ResourceIdImpl implements ResourceId {
  private static final JavaIOFileDescriptorAccess FD_ACCESS = SharedSecrets.getJavaIOFileDescriptorAccess();
  
  private final Object target;
  
  private final ResourceAccuracy accuracy;
  
  private final boolean forceUpdate;
  
  public static ResourceIdImpl of(Object paramObject) { return (paramObject == null) ? null : new ResourceIdImpl(paramObject, null, false); }
  
  public static ResourceIdImpl of(FileDescriptor paramFileDescriptor) {
    long l = -1L;
    if (paramFileDescriptor != null) {
      l = FD_ACCESS.get(paramFileDescriptor);
      if (l == -1L)
        try {
          l = FD_ACCESS.getHandle(paramFileDescriptor);
        } catch (UnsupportedOperationException unsupportedOperationException) {} 
    } 
    return (l == -1L) ? null : of(Integer.valueOf((int)l));
  }
  
  public static ResourceIdImpl of(Object paramObject, ResourceAccuracy paramResourceAccuracy) { return (paramObject == null) ? null : new ResourceIdImpl(paramObject, paramResourceAccuracy, false); }
  
  public static ResourceIdImpl of(Object paramObject, ResourceAccuracy paramResourceAccuracy, boolean paramBoolean) { return (paramObject == null) ? null : new ResourceIdImpl(paramObject, paramResourceAccuracy, paramBoolean); }
  
  protected ResourceIdImpl(Object paramObject, ResourceAccuracy paramResourceAccuracy, boolean paramBoolean) {
    this.target = paramObject;
    this.accuracy = paramResourceAccuracy;
    this.forceUpdate = paramBoolean;
  }
  
  public String getName() { return Objects.toString(this.target, null); }
  
  public ResourceAccuracy getAccuracy() { return this.accuracy; }
  
  public boolean isForcedUpdate() { return this.forceUpdate; }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(getName());
    ResourceAccuracy resourceAccuracy = getAccuracy();
    if (resourceAccuracy != null) {
      stringBuilder.append(", accuracy: ");
      stringBuilder.append(resourceAccuracy);
    } 
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\ResourceIdImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */