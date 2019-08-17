package javax.management;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;

class NumericValueExp extends QueryEval implements ValueExp {
  private static final long oldSerialVersionUID = -6227876276058904000L;
  
  private static final long newSerialVersionUID = -4679739485102359104L;
  
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("longVal", long.class), new ObjectStreamField("doubleVal", double.class), new ObjectStreamField("valIsLong", boolean.class) };
  
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("val", Number.class) };
  
  private static final long serialVersionUID;
  
  private static final ObjectStreamField[] serialPersistentFields;
  
  private Number val = Double.valueOf(0.0D);
  
  private static boolean compat = false;
  
  public NumericValueExp() {}
  
  NumericValueExp(Number paramNumber) { this.val = paramNumber; }
  
  public double doubleValue() { return (this.val instanceof Long || this.val instanceof Integer) ? this.val.longValue() : this.val.doubleValue(); }
  
  public long longValue() { return (this.val instanceof Long || this.val instanceof Integer) ? this.val.longValue() : (long)this.val.doubleValue(); }
  
  public boolean isLong() { return (this.val instanceof Long || this.val instanceof Integer); }
  
  public String toString() {
    if (this.val == null)
      return "null"; 
    if (this.val instanceof Long || this.val instanceof Integer)
      return Long.toString(this.val.longValue()); 
    double d = this.val.doubleValue();
    return Double.isInfinite(d) ? ((d > 0.0D) ? "(1.0 / 0.0)" : "(-1.0 / 0.0)") : (Double.isNaN(d) ? "(0.0 / 0.0)" : Double.toString(d));
  }
  
  public ValueExp apply(ObjectName paramObjectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException { return this; }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    if (compat) {
      ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
      double d = getField.get("doubleVal", 0.0D);
      if (getField.defaulted("doubleVal"))
        throw new NullPointerException("doubleVal"); 
      long l = getField.get("longVal", 0L);
      if (getField.defaulted("longVal"))
        throw new NullPointerException("longVal"); 
      boolean bool = getField.get("valIsLong", false);
      if (getField.defaulted("valIsLong"))
        throw new NullPointerException("valIsLong"); 
      if (bool) {
        this.val = Long.valueOf(l);
      } else {
        this.val = Double.valueOf(d);
      } 
    } else {
      paramObjectInputStream.defaultReadObject();
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (compat) {
      ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
      putField.put("doubleVal", doubleValue());
      putField.put("longVal", longValue());
      putField.put("valIsLong", isLong());
      paramObjectOutputStream.writeFields();
    } else {
      paramObjectOutputStream.defaultWriteObject();
    } 
  }
  
  @Deprecated
  public void setMBeanServer(MBeanServer paramMBeanServer) { super.setMBeanServer(paramMBeanServer); }
  
  static  {
    try {
      GetPropertyAction getPropertyAction = new GetPropertyAction("jmx.serial.form");
      String str = (String)AccessController.doPrivileged(getPropertyAction);
      compat = (str != null && str.equals("1.0"));
    } catch (Exception exception) {}
    if (compat) {
      serialPersistentFields = oldSerialPersistentFields;
      serialVersionUID = -6227876276058904000L;
    } else {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = -4679739485102359104L;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\NumericValueExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */