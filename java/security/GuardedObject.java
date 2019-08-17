package java.security;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class GuardedObject implements Serializable {
  private static final long serialVersionUID = -5240450096227834308L;
  
  private Object object;
  
  private Guard guard;
  
  public GuardedObject(Object paramObject, Guard paramGuard) {
    this.guard = paramGuard;
    this.object = paramObject;
  }
  
  public Object getObject() throws SecurityException {
    if (this.guard != null)
      this.guard.checkGuard(this.object); 
    return this.object;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (this.guard != null)
      this.guard.checkGuard(this.object); 
    paramObjectOutputStream.defaultWriteObject();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\GuardedObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */