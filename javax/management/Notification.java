package javax.management;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.util.Date;
import java.util.EventObject;

public class Notification extends EventObject {
  private static final long oldSerialVersionUID = 1716977971058914352L;
  
  private static final long newSerialVersionUID = -7516092053498031989L;
  
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("message", String.class), new ObjectStreamField("sequenceNumber", long.class), new ObjectStreamField("source", Object.class), new ObjectStreamField("sourceObjectName", ObjectName.class), new ObjectStreamField("timeStamp", long.class), new ObjectStreamField("type", String.class), new ObjectStreamField("userData", Object.class) };
  
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("message", String.class), new ObjectStreamField("sequenceNumber", long.class), new ObjectStreamField("source", Object.class), new ObjectStreamField("timeStamp", long.class), new ObjectStreamField("type", String.class), new ObjectStreamField("userData", Object.class) };
  
  private static final long serialVersionUID;
  
  private static final ObjectStreamField[] serialPersistentFields;
  
  private static boolean compat = false;
  
  private String type;
  
  private long sequenceNumber;
  
  private long timeStamp;
  
  private Object userData = null;
  
  private String message = "";
  
  protected Object source = null;
  
  public Notification(String paramString, Object paramObject, long paramLong) {
    super(paramObject);
    this.source = paramObject;
    this.type = paramString;
    this.sequenceNumber = paramLong;
    this.timeStamp = (new Date()).getTime();
  }
  
  public Notification(String paramString1, Object paramObject, long paramLong, String paramString2) {
    super(paramObject);
    this.source = paramObject;
    this.type = paramString1;
    this.sequenceNumber = paramLong;
    this.timeStamp = (new Date()).getTime();
    this.message = paramString2;
  }
  
  public Notification(String paramString, Object paramObject, long paramLong1, long paramLong2) {
    super(paramObject);
    this.source = paramObject;
    this.type = paramString;
    this.sequenceNumber = paramLong1;
    this.timeStamp = paramLong2;
  }
  
  public Notification(String paramString1, Object paramObject, long paramLong1, long paramLong2, String paramString2) {
    super(paramObject);
    this.source = paramObject;
    this.type = paramString1;
    this.sequenceNumber = paramLong1;
    this.timeStamp = paramLong2;
    this.message = paramString2;
  }
  
  public void setSource(Object paramObject) {
    super.source = paramObject;
    this.source = paramObject;
  }
  
  public long getSequenceNumber() { return this.sequenceNumber; }
  
  public void setSequenceNumber(long paramLong) { this.sequenceNumber = paramLong; }
  
  public String getType() { return this.type; }
  
  public long getTimeStamp() { return this.timeStamp; }
  
  public void setTimeStamp(long paramLong) { this.timeStamp = paramLong; }
  
  public String getMessage() { return this.message; }
  
  public Object getUserData() { return this.userData; }
  
  public void setUserData(Object paramObject) { this.userData = paramObject; }
  
  public String toString() { return super.toString() + "[type=" + this.type + "][message=" + this.message + "]"; }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    super.source = this.source;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (compat) {
      ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
      putField.put("type", this.type);
      putField.put("sequenceNumber", this.sequenceNumber);
      putField.put("timeStamp", this.timeStamp);
      putField.put("userData", this.userData);
      putField.put("message", this.message);
      putField.put("source", this.source);
      paramObjectOutputStream.writeFields();
    } else {
      paramObjectOutputStream.defaultWriteObject();
    } 
  }
  
  static  {
    try {
      GetPropertyAction getPropertyAction = new GetPropertyAction("jmx.serial.form");
      String str = (String)AccessController.doPrivileged(getPropertyAction);
      compat = (str != null && str.equals("1.0"));
    } catch (Exception exception) {}
    if (compat) {
      serialPersistentFields = oldSerialPersistentFields;
      serialVersionUID = 1716977971058914352L;
    } else {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = -7516092053498031989L;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\Notification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */