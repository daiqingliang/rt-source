package java.rmi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.ObjectInputFilter;
import sun.rmi.server.MarshalInputStream;
import sun.rmi.server.MarshalOutputStream;

public final class MarshalledObject<T> extends Object implements Serializable {
  private byte[] objBytes = null;
  
  private byte[] locBytes = null;
  
  private int hash;
  
  private ObjectInputFilter objectInputFilter = null;
  
  private static final long serialVersionUID = 8988374069173025854L;
  
  public MarshalledObject(T paramT) throws IOException {
    if (paramT == null) {
      this.hash = 13;
      return;
    } 
    ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
    ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
    MarshalledObjectOutputStream marshalledObjectOutputStream = new MarshalledObjectOutputStream(byteArrayOutputStream1, byteArrayOutputStream2);
    marshalledObjectOutputStream.writeObject(paramT);
    marshalledObjectOutputStream.flush();
    this.objBytes = byteArrayOutputStream1.toByteArray();
    this.locBytes = marshalledObjectOutputStream.hadAnnotations() ? byteArrayOutputStream2.toByteArray() : null;
    byte b = 0;
    for (byte b1 = 0; b1 < this.objBytes.length; b1++)
      b = 31 * b + this.objBytes[b1]; 
    this.hash = b;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.objectInputFilter = ObjectInputFilter.Config.getObjectInputFilter(paramObjectInputStream);
  }
  
  public T get() throws IOException, ClassNotFoundException {
    if (this.objBytes == null)
      return null; 
    ByteArrayInputStream byteArrayInputStream1 = new ByteArrayInputStream(this.objBytes);
    ByteArrayInputStream byteArrayInputStream2 = (this.locBytes == null) ? null : new ByteArrayInputStream(this.locBytes);
    MarshalledObjectInputStream marshalledObjectInputStream = new MarshalledObjectInputStream(byteArrayInputStream1, byteArrayInputStream2, this.objectInputFilter);
    Object object = marshalledObjectInputStream.readObject();
    marshalledObjectInputStream.close();
    return (T)object;
  }
  
  public int hashCode() { return this.hash; }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (paramObject != null && paramObject instanceof MarshalledObject) {
      MarshalledObject marshalledObject = (MarshalledObject)paramObject;
      if (this.objBytes == null || marshalledObject.objBytes == null)
        return (this.objBytes == marshalledObject.objBytes); 
      if (this.objBytes.length != marshalledObject.objBytes.length)
        return false; 
      for (byte b = 0; b < this.objBytes.length; b++) {
        if (this.objBytes[b] != marshalledObject.objBytes[b])
          return false; 
      } 
      return true;
    } 
    return false;
  }
  
  private static class MarshalledObjectInputStream extends MarshalInputStream {
    private ObjectInputStream locIn;
    
    MarshalledObjectInputStream(InputStream param1InputStream1, InputStream param1InputStream2, final ObjectInputFilter filter) throws IOException {
      super(param1InputStream1);
      this.locIn = (param1InputStream2 == null) ? null : new ObjectInputStream(param1InputStream2);
      if (param1ObjectInputFilter != null)
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
              public Void run() {
                ObjectInputFilter.Config.setObjectInputFilter(MarshalledObject.MarshalledObjectInputStream.this, filter);
                if (MarshalledObject.MarshalledObjectInputStream.this.locIn != null)
                  ObjectInputFilter.Config.setObjectInputFilter(MarshalledObject.MarshalledObjectInputStream.this.locIn, filter); 
                return null;
              }
            }); 
    }
    
    protected Object readLocation() throws IOException, ClassNotFoundException { return (this.locIn == null) ? null : this.locIn.readObject(); }
  }
  
  private static class MarshalledObjectOutputStream extends MarshalOutputStream {
    private ObjectOutputStream locOut;
    
    private boolean hadAnnotations;
    
    MarshalledObjectOutputStream(OutputStream param1OutputStream1, OutputStream param1OutputStream2) throws IOException {
      super(param1OutputStream1);
      useProtocolVersion(2);
      this.locOut = new ObjectOutputStream(param1OutputStream2);
      this.hadAnnotations = false;
    }
    
    boolean hadAnnotations() { return this.hadAnnotations; }
    
    protected void writeLocation(String param1String) throws IOException {
      this.hadAnnotations |= ((param1String != null));
      this.locOut.writeObject(param1String);
    }
    
    public void flush() throws IOException {
      super.flush();
      this.locOut.flush();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\MarshalledObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */