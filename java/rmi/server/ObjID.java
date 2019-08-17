package java.rmi.server;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.security.AccessController;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicLong;
import sun.security.action.GetPropertyAction;

public final class ObjID implements Serializable {
  public static final int REGISTRY_ID = 0;
  
  public static final int ACTIVATOR_ID = 1;
  
  public static final int DGC_ID = 2;
  
  private static final long serialVersionUID = -6386392263968365220L;
  
  private static final AtomicLong nextObjNum = new AtomicLong(0L);
  
  private static final UID mySpace = new UID();
  
  private static final SecureRandom secureRandom = new SecureRandom();
  
  private final long objNum;
  
  private final UID space;
  
  public ObjID() {
    if (useRandomIDs()) {
      this.space = new UID();
      this.objNum = secureRandom.nextLong();
    } else {
      this.space = mySpace;
      this.objNum = nextObjNum.getAndIncrement();
    } 
  }
  
  public ObjID(int paramInt) {
    this.space = new UID((short)0);
    this.objNum = paramInt;
  }
  
  private ObjID(long paramLong, UID paramUID) {
    this.objNum = paramLong;
    this.space = paramUID;
  }
  
  public void write(ObjectOutput paramObjectOutput) throws IOException {
    paramObjectOutput.writeLong(this.objNum);
    this.space.write(paramObjectOutput);
  }
  
  public static ObjID read(ObjectInput paramObjectInput) throws IOException {
    long l = paramObjectInput.readLong();
    UID uID = UID.read(paramObjectInput);
    return new ObjID(l, uID);
  }
  
  public int hashCode() { return (int)this.objNum; }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof ObjID) {
      ObjID objID = (ObjID)paramObject;
      return (this.objNum == objID.objNum && this.space.equals(objID.space));
    } 
    return false;
  }
  
  public String toString() { return "[" + (this.space.equals(mySpace) ? "" : (this.space + ", ")) + this.objNum + "]"; }
  
  private static boolean useRandomIDs() {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.rmi.server.randomIDs"));
    return (str == null) ? true : Boolean.parseBoolean(str);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\server\ObjID.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */