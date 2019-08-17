package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.Enumerated;
import java.io.Serializable;
import java.util.Hashtable;

public class EnumJvmThreadCpuTimeMonitoring extends Enumerated implements Serializable {
  static final long serialVersionUID = -532837824105215699L;
  
  protected static Hashtable<Integer, String> intTable = new Hashtable();
  
  protected static Hashtable<String, Integer> stringTable = new Hashtable();
  
  public EnumJvmThreadCpuTimeMonitoring(int paramInt) throws IllegalArgumentException { super(paramInt); }
  
  public EnumJvmThreadCpuTimeMonitoring(Integer paramInteger) throws IllegalArgumentException { super(paramInteger); }
  
  public EnumJvmThreadCpuTimeMonitoring() throws IllegalArgumentException {}
  
  public EnumJvmThreadCpuTimeMonitoring(String paramString) throws IllegalArgumentException { super(paramString); }
  
  protected Hashtable<Integer, String> getIntTable() { return intTable; }
  
  protected Hashtable<String, Integer> getStringTable() { return stringTable; }
  
  static  {
    intTable.put(new Integer(3), "enabled");
    intTable.put(new Integer(4), "disabled");
    intTable.put(new Integer(1), "unsupported");
    stringTable.put("enabled", new Integer(3));
    stringTable.put("disabled", new Integer(4));
    stringTable.put("unsupported", new Integer(1));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvmmib\EnumJvmThreadCpuTimeMonitoring.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */