package javax.management;

import java.io.Serializable;

public interface ValueExp extends Serializable {
  ValueExp apply(ObjectName paramObjectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException;
  
  @Deprecated
  void setMBeanServer(MBeanServer paramMBeanServer);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\ValueExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */