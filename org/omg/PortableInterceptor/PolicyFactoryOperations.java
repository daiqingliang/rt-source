package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyError;

public interface PolicyFactoryOperations {
  Policy create_policy(int paramInt, Any paramAny) throws PolicyError;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableInterceptor\PolicyFactoryOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */