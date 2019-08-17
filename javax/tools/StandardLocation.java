package javax.tools;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public static enum StandardLocation implements JavaFileManager.Location {
  CLASS_OUTPUT, SOURCE_OUTPUT, CLASS_PATH, SOURCE_PATH, ANNOTATION_PROCESSOR_PATH, PLATFORM_CLASS_PATH, NATIVE_HEADER_OUTPUT;
  
  private static final ConcurrentMap<String, JavaFileManager.Location> locations;
  
  public static JavaFileManager.Location locationFor(final String name) {
    if (locations.isEmpty())
      for (StandardLocation standardLocation : values())
        locations.putIfAbsent(standardLocation.getName(), standardLocation);  
    locations.putIfAbsent(paramString.toString(), new JavaFileManager.Location() {
          public String getName() { return name; }
          
          public boolean isOutputLocation() { return name.endsWith("_OUTPUT"); }
        });
    return (JavaFileManager.Location)locations.get(paramString);
  }
  
  public String getName() { return name(); }
  
  public boolean isOutputLocation() {
    switch (this) {
      case CLASS_OUTPUT:
      case SOURCE_OUTPUT:
      case NATIVE_HEADER_OUTPUT:
        return true;
    } 
    return false;
  }
  
  static  {
    locations = new ConcurrentHashMap();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\tools\StandardLocation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */