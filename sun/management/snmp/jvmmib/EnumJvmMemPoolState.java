package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.Enumerated;
import java.io.Serializable;
import java.util.Hashtable;

public class EnumJvmMemPoolState extends Enumerated implements Serializable {
  static final long serialVersionUID = 3038175407527743027L;
  
  protected static Hashtable<Integer, String> intTable = new Hashtable();
  
  protected static Hashtable<String, Integer> stringTable = new Hashtable();
  
  public EnumJvmMemPoolState(int paramInt) throws IllegalArgumentException { super(paramInt); }
  
  public EnumJvmMemPoolState(Integer paramInteger) throws IllegalArgumentException { super(paramInteger); }
  
  public EnumJvmMemPoolState() throws IllegalArgumentException {}
  
  public EnumJvmMemPoolState(String paramString) throws IllegalArgumentException { super(paramString); }
  
  protected Hashtable<Integer, String> getIntTable() { return intTable; }
  
  protected Hashtable<String, Integer> getStringTable() { return stringTable; }
  
  static  {
    intTable.put(new Integer(2), "valid");
    intTable.put(new Integer(1), "invalid");
    stringTable.put("valid", new Integer(2));
    stringTable.put("invalid", new Integer(1));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvmmib\EnumJvmMemPoolState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */