package javax.management;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.Objects;

public class MBeanFeatureInfo implements Serializable, DescriptorRead {
  static final long serialVersionUID = 3952882688968447265L;
  
  protected String name;
  
  protected String description;
  
  private Descriptor descriptor;
  
  public MBeanFeatureInfo(String paramString1, String paramString2) { this(paramString1, paramString2, null); }
  
  public MBeanFeatureInfo(String paramString1, String paramString2, Descriptor paramDescriptor) {
    this.name = paramString1;
    this.description = paramString2;
    this.descriptor = paramDescriptor;
  }
  
  public String getName() { return this.name; }
  
  public String getDescription() { return this.description; }
  
  public Descriptor getDescriptor() { return (Descriptor)ImmutableDescriptor.nonNullDescriptor(this.descriptor).clone(); }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof MBeanFeatureInfo))
      return false; 
    MBeanFeatureInfo mBeanFeatureInfo = (MBeanFeatureInfo)paramObject;
    return (Objects.equals(mBeanFeatureInfo.getName(), getName()) && Objects.equals(mBeanFeatureInfo.getDescription(), getDescription()) && Objects.equals(mBeanFeatureInfo.getDescriptor(), getDescriptor()));
  }
  
  public int hashCode() { return getName().hashCode() ^ getDescription().hashCode() ^ getDescriptor().hashCode(); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (this.descriptor != null && this.descriptor.getClass() == ImmutableDescriptor.class) {
      paramObjectOutputStream.write(1);
      String[] arrayOfString = this.descriptor.getFieldNames();
      paramObjectOutputStream.writeObject(arrayOfString);
      paramObjectOutputStream.writeObject(this.descriptor.getFieldValues(arrayOfString));
    } else {
      paramObjectOutputStream.write(0);
      paramObjectOutputStream.writeObject(this.descriptor);
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    Object[] arrayOfObject;
    String[] arrayOfString;
    paramObjectInputStream.defaultReadObject();
    switch (paramObjectInputStream.read()) {
      case 1:
        arrayOfString = (String[])paramObjectInputStream.readObject();
        arrayOfObject = (Object[])paramObjectInputStream.readObject();
        this.descriptor = (arrayOfString.length == 0) ? ImmutableDescriptor.EMPTY_DESCRIPTOR : new ImmutableDescriptor(arrayOfString, arrayOfObject);
        return;
      case 0:
        this.descriptor = (Descriptor)paramObjectInputStream.readObject();
        if (this.descriptor == null)
          this.descriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR; 
        return;
      case -1:
        this.descriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR;
        return;
    } 
    throw new StreamCorruptedException("Got unexpected byte.");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\MBeanFeatureInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */