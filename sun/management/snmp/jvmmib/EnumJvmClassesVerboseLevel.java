package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.Enumerated;
import java.io.Serializable;
import java.util.Hashtable;

public class EnumJvmClassesVerboseLevel extends Enumerated implements Serializable {
  static final long serialVersionUID = -620710366914810374L;
  
  protected static Hashtable<Integer, String> intTable = new Hashtable();
  
  protected static Hashtable<String, Integer> stringTable = new Hashtable();
  
  public EnumJvmClassesVerboseLevel(int paramInt) throws IllegalArgumentException { super(paramInt); }
  
  public EnumJvmClassesVerboseLevel(Integer paramInteger) throws IllegalArgumentException { super(paramInteger); }
  
  public EnumJvmClassesVerboseLevel() throws IllegalArgumentException {}
  
  public EnumJvmClassesVerboseLevel(String paramString) throws IllegalArgumentException { super(paramString); }
  
  protected Hashtable<Integer, String> getIntTable() { return intTable; }
  
  protected Hashtable<String, Integer> getStringTable() { return stringTable; }
  
  static  {
    intTable.put(new Integer(2), "verbose");
    intTable.put(new Integer(1), "silent");
    stringTable.put("verbose", new Integer(2));
    stringTable.put("silent", new Integer(1));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvmmib\EnumJvmClassesVerboseLevel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */