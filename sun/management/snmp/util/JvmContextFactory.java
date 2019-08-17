package sun.management.snmp.util;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.ThreadContext;
import com.sun.jmx.snmp.agent.SnmpUserDataFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JvmContextFactory implements SnmpUserDataFactory {
  public Object allocateUserData(SnmpPdu paramSnmpPdu) throws SnmpStatusException { return Collections.synchronizedMap(new HashMap()); }
  
  public void releaseUserData(Object paramObject, SnmpPdu paramSnmpPdu) throws SnmpStatusException { ((Map)paramObject).clear(); }
  
  public static Map<Object, Object> getUserData() {
    Object object = ThreadContext.get("SnmpUserData");
    return (object instanceof Map) ? (Map)Util.cast(object) : null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snm\\util\JvmContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */