package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.Enumerated;
import java.io.Serializable;
import java.util.Hashtable;

public class EnumJvmMemPoolType extends Enumerated implements Serializable {
  static final long serialVersionUID = -7214498472962396555L;
  
  protected static Hashtable<Integer, String> intTable = new Hashtable();
  
  protected static Hashtable<String, Integer> stringTable = new Hashtable();
  
  public EnumJvmMemPoolType(int paramInt) throws IllegalArgumentException { super(paramInt); }
  
  public EnumJvmMemPoolType(Integer paramInteger) throws IllegalArgumentException { super(paramInteger); }
  
  public EnumJvmMemPoolType() throws IllegalArgumentException {}
  
  public EnumJvmMemPoolType(String paramString) throws IllegalArgumentException { super(paramString); }
  
  protected Hashtable<Integer, String> getIntTable() { return intTable; }
  
  protected Hashtable<String, Integer> getStringTable() { return stringTable; }
  
  static  {
    intTable.put(new Integer(2), "heap");
    intTable.put(new Integer(1), "nonheap");
    stringTable.put("heap", new Integer(2));
    stringTable.put("nonheap", new Integer(1));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvmmib\EnumJvmMemPoolType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */