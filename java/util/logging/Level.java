package java.util.logging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class Level implements Serializable {
  private static final String defaultBundle = "sun.util.logging.resources.logging";
  
  private final String name;
  
  private final int value;
  
  private final String resourceBundleName;
  
  private String localizedLevelName;
  
  private Locale cachedLocale;
  
  public static final Level OFF = new Level("OFF", 2147483647, "sun.util.logging.resources.logging");
  
  public static final Level SEVERE = new Level("SEVERE", 1000, "sun.util.logging.resources.logging");
  
  public static final Level WARNING = new Level("WARNING", 900, "sun.util.logging.resources.logging");
  
  public static final Level INFO = new Level("INFO", 800, "sun.util.logging.resources.logging");
  
  public static final Level CONFIG = new Level("CONFIG", 700, "sun.util.logging.resources.logging");
  
  public static final Level FINE = new Level("FINE", 500, "sun.util.logging.resources.logging");
  
  public static final Level FINER = new Level("FINER", 400, "sun.util.logging.resources.logging");
  
  public static final Level FINEST = new Level("FINEST", 300, "sun.util.logging.resources.logging");
  
  public static final Level ALL = new Level("ALL", -2147483648, "sun.util.logging.resources.logging");
  
  private static final long serialVersionUID = -8176160795706313070L;
  
  protected Level(String paramString, int paramInt) { this(paramString, paramInt, null); }
  
  protected Level(String paramString1, int paramInt, String paramString2) { this(paramString1, paramInt, paramString2, true); }
  
  private Level(String paramString1, int paramInt, String paramString2, boolean paramBoolean) {
    if (paramString1 == null)
      throw new NullPointerException(); 
    this.name = paramString1;
    this.value = paramInt;
    this.resourceBundleName = paramString2;
    this.localizedLevelName = (paramString2 == null) ? paramString1 : null;
    this.cachedLocale = null;
    if (paramBoolean)
      KnownLevel.add(this); 
  }
  
  public String getResourceBundleName() { return this.resourceBundleName; }
  
  public String getName() { return this.name; }
  
  public String getLocalizedName() { return getLocalizedLevelName(); }
  
  final String getLevelName() { return this.name; }
  
  private String computeLocalizedLevelName(Locale paramLocale) {
    if (!"sun.util.logging.resources.logging".equals(this.resourceBundleName))
      return ResourceBundle.getBundle(this.resourceBundleName, paramLocale, ClassLoader.getSystemClassLoader()).getString(this.name); 
    ResourceBundle resourceBundle = ResourceBundle.getBundle("sun.util.logging.resources.logging", paramLocale);
    String str = resourceBundle.getString(this.name);
    Locale locale1 = resourceBundle.getLocale();
    Locale locale2 = (Locale.ROOT.equals(locale1) || this.name.equals(str.toUpperCase(Locale.ROOT))) ? Locale.ROOT : locale1;
    return Locale.ROOT.equals(locale2) ? this.name : str.toUpperCase(locale2);
  }
  
  final String getCachedLocalizedLevelName() { return (this.localizedLevelName != null && this.cachedLocale != null && this.cachedLocale.equals(Locale.getDefault())) ? this.localizedLevelName : ((this.resourceBundleName == null) ? this.name : null); }
  
  final String getLocalizedLevelName() {
    String str = getCachedLocalizedLevelName();
    if (str != null)
      return str; 
    Locale locale = Locale.getDefault();
    try {
      this.localizedLevelName = computeLocalizedLevelName(locale);
    } catch (Exception exception) {
      this.localizedLevelName = this.name;
    } 
    this.cachedLocale = locale;
    return this.localizedLevelName;
  }
  
  static Level findLevel(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    KnownLevel knownLevel = KnownLevel.findByName(paramString);
    if (knownLevel != null)
      return knownLevel.mirroredLevel; 
    try {
      int i = Integer.parseInt(paramString);
      knownLevel = KnownLevel.findByValue(i);
      if (knownLevel == null) {
        Level level = new Level(paramString, i);
        knownLevel = KnownLevel.findByValue(i);
      } 
      return knownLevel.mirroredLevel;
    } catch (NumberFormatException numberFormatException) {
      knownLevel = KnownLevel.findByLocalizedLevelName(paramString);
      return (knownLevel != null) ? knownLevel.mirroredLevel : null;
    } 
  }
  
  public final String toString() { return this.name; }
  
  public final int intValue() { return this.value; }
  
  private Object readResolve() {
    KnownLevel knownLevel = KnownLevel.matches(this);
    return (knownLevel != null) ? knownLevel.levelObject : new Level(this.name, this.value, this.resourceBundleName);
  }
  
  public static Level parse(String paramString) {
    paramString.length();
    KnownLevel knownLevel = KnownLevel.findByName(paramString);
    if (knownLevel != null)
      return knownLevel.levelObject; 
    try {
      int i = Integer.parseInt(paramString);
      knownLevel = KnownLevel.findByValue(i);
      if (knownLevel == null) {
        Level level = new Level(paramString, i);
        knownLevel = KnownLevel.findByValue(i);
      } 
      return knownLevel.levelObject;
    } catch (NumberFormatException numberFormatException) {
      knownLevel = KnownLevel.findByLocalizedLevelName(paramString);
      if (knownLevel != null)
        return knownLevel.levelObject; 
      throw new IllegalArgumentException("Bad level \"" + paramString + "\"");
    } 
  }
  
  public boolean equals(Object paramObject) {
    try {
      Level level = (Level)paramObject;
      return (level.value == this.value);
    } catch (Exception exception) {
      return false;
    } 
  }
  
  public int hashCode() { return this.value; }
  
  static final class KnownLevel {
    private static Map<String, List<KnownLevel>> nameToLevels = new HashMap();
    
    private static Map<Integer, List<KnownLevel>> intToLevels = new HashMap();
    
    final Level levelObject;
    
    final Level mirroredLevel;
    
    KnownLevel(Level param1Level) {
      this.levelObject = param1Level;
      if (param1Level.getClass() == Level.class) {
        this.mirroredLevel = param1Level;
      } else {
        this.mirroredLevel = new Level(param1Level.name, param1Level.value, param1Level.resourceBundleName, false, null);
      } 
    }
    
    static void add(Level param1Level) {
      KnownLevel knownLevel = new KnownLevel(param1Level);
      List list = (List)nameToLevels.get(param1Level.name);
      if (list == null) {
        list = new ArrayList();
        nameToLevels.put(param1Level.name, list);
      } 
      list.add(knownLevel);
      list = (List)intToLevels.get(Integer.valueOf(param1Level.value));
      if (list == null) {
        list = new ArrayList();
        intToLevels.put(Integer.valueOf(param1Level.value), list);
      } 
      list.add(knownLevel);
    }
    
    static KnownLevel findByName(String param1String) {
      List list = (List)nameToLevels.get(param1String);
      return (list != null) ? (KnownLevel)list.get(0) : null;
    }
    
    static KnownLevel findByValue(int param1Int) {
      List list = (List)intToLevels.get(Integer.valueOf(param1Int));
      return (list != null) ? (KnownLevel)list.get(0) : null;
    }
    
    static KnownLevel findByLocalizedLevelName(String param1String) {
      for (List list : nameToLevels.values()) {
        for (KnownLevel knownLevel : list) {
          String str = knownLevel.levelObject.getLocalizedLevelName();
          if (param1String.equals(str))
            return knownLevel; 
        } 
      } 
      return null;
    }
    
    static KnownLevel matches(Level param1Level) {
      List list = (List)nameToLevels.get(param1Level.name);
      if (list != null)
        for (KnownLevel knownLevel : list) {
          Level level = knownLevel.mirroredLevel;
          Class clazz = knownLevel.levelObject.getClass();
          if (param1Level.value == level.value && (param1Level.resourceBundleName == level.resourceBundleName || (param1Level.resourceBundleName != null && param1Level.resourceBundleName.equals(level.resourceBundleName))) && clazz == param1Level.getClass())
            return knownLevel; 
        }  
      return null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\logging\Level.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */