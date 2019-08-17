package javax.management;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.security.AccessController;

class ClassAttributeValueExp extends AttributeValueExp {
  private static final long oldSerialVersionUID = -2212731951078526753L;
  
  private static final long newSerialVersionUID = -1081892073854801359L;
  
  private static final long serialVersionUID;
  
  private String attr = "Class";
  
  public ClassAttributeValueExp() { super("Class"); }
  
  public ValueExp apply(ObjectName paramObjectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
    Object object = getValue(paramObjectName);
    if (object instanceof String)
      return new StringValueExp((String)object); 
    throw new BadAttributeValueExpException(object);
  }
  
  public String toString() { return this.attr; }
  
  protected Object getValue(ObjectName paramObjectName) {
    try {
      MBeanServer mBeanServer = QueryEval.getMBeanServer();
      return mBeanServer.getObjectInstance(paramObjectName).getClassName();
    } catch (Exception exception) {
      return null;
    } 
  }
  
  static  {
    boolean bool = false;
    try {
      GetPropertyAction getPropertyAction = new GetPropertyAction("jmx.serial.form");
      String str = (String)AccessController.doPrivileged(getPropertyAction);
      bool = (str != null && str.equals("1.0")) ? 1 : 0;
    } catch (Exception exception) {}
    if (bool) {
      serialVersionUID = -2212731951078526753L;
    } else {
      serialVersionUID = -1081892073854801359L;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\ClassAttributeValueExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */