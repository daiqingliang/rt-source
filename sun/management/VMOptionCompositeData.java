package sun.management;

import com.sun.management.VMOption;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;

public class VMOptionCompositeData extends LazyCompositeData {
  private final VMOption option;
  
  private static final CompositeType vmOptionCompositeType;
  
  private static final String NAME = "name";
  
  private static final String VALUE = "value";
  
  private static final String WRITEABLE = "writeable";
  
  private static final String ORIGIN = "origin";
  
  private static final String[] vmOptionItemNames;
  
  private static final long serialVersionUID = -2395573975093578470L;
  
  private VMOptionCompositeData(VMOption paramVMOption) { this.option = paramVMOption; }
  
  public VMOption getVMOption() { return this.option; }
  
  public static CompositeData toCompositeData(VMOption paramVMOption) {
    VMOptionCompositeData vMOptionCompositeData = new VMOptionCompositeData(paramVMOption);
    return vMOptionCompositeData.getCompositeData();
  }
  
  protected CompositeData getCompositeData() {
    Object[] arrayOfObject = { this.option.getName(), this.option.getValue(), new Boolean(this.option.isWriteable()), this.option.getOrigin().toString() };
    try {
      return new CompositeDataSupport(vmOptionCompositeType, vmOptionItemNames, arrayOfObject);
    } catch (OpenDataException openDataException) {
      throw new AssertionError(openDataException);
    } 
  }
  
  static CompositeType getVMOptionCompositeType() { return vmOptionCompositeType; }
  
  public static String getName(CompositeData paramCompositeData) { return getString(paramCompositeData, "name"); }
  
  public static String getValue(CompositeData paramCompositeData) { return getString(paramCompositeData, "value"); }
  
  public static VMOption.Origin getOrigin(CompositeData paramCompositeData) {
    String str = getString(paramCompositeData, "origin");
    return (VMOption.Origin)Enum.valueOf(VMOption.Origin.class, str);
  }
  
  public static boolean isWriteable(CompositeData paramCompositeData) { return getBoolean(paramCompositeData, "writeable"); }
  
  public static void validateCompositeData(CompositeData paramCompositeData) {
    if (paramCompositeData == null)
      throw new NullPointerException("Null CompositeData"); 
    if (!isTypeMatched(vmOptionCompositeType, paramCompositeData.getCompositeType()))
      throw new IllegalArgumentException("Unexpected composite type for VMOption"); 
  }
  
  static  {
    try {
      vmOptionCompositeType = (CompositeType)MappedMXBeanType.toOpenType(VMOption.class);
    } catch (OpenDataException openDataException) {
      throw new AssertionError(openDataException);
    } 
    vmOptionItemNames = new String[] { "name", "value", "writeable", "origin" };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\VMOptionCompositeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */