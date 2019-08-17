package java.awt;

import java.awt.event.KeyEvent;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import sun.awt.AppContext;

public class AWTKeyStroke implements Serializable {
  static final long serialVersionUID = -6430539691155161871L;
  
  private static Map<String, Integer> modifierKeywords;
  
  private static VKCollection vks;
  
  private static Object APP_CONTEXT_CACHE_KEY = new Object();
  
  private static AWTKeyStroke APP_CONTEXT_KEYSTROKE_KEY = new AWTKeyStroke();
  
  private char keyChar = Character.MAX_VALUE;
  
  private int keyCode = 0;
  
  private int modifiers;
  
  private boolean onKeyRelease;
  
  private static Class<AWTKeyStroke> getAWTKeyStrokeClass() {
    Class clazz = (Class)AppContext.getAppContext().get(AWTKeyStroke.class);
    if (clazz == null) {
      clazz = AWTKeyStroke.class;
      AppContext.getAppContext().put(AWTKeyStroke.class, AWTKeyStroke.class);
    } 
    return clazz;
  }
  
  protected AWTKeyStroke() {}
  
  protected AWTKeyStroke(char paramChar, int paramInt1, int paramInt2, boolean paramBoolean) {
    this.keyChar = paramChar;
    this.keyCode = paramInt1;
    this.modifiers = paramInt2;
    this.onKeyRelease = paramBoolean;
  }
  
  protected static void registerSubclass(Class<?> paramClass) {
    if (paramClass == null)
      throw new IllegalArgumentException("subclass cannot be null"); 
    synchronized (AWTKeyStroke.class) {
      Class clazz = (Class)AppContext.getAppContext().get(AWTKeyStroke.class);
      if (clazz != null && clazz.equals(paramClass))
        return; 
    } 
    if (!AWTKeyStroke.class.isAssignableFrom(paramClass))
      throw new ClassCastException("subclass is not derived from AWTKeyStroke"); 
    Constructor constructor = getCtor(paramClass);
    String str = "subclass could not be instantiated";
    if (constructor == null)
      throw new IllegalArgumentException(str); 
    try {
      AWTKeyStroke aWTKeyStroke = (AWTKeyStroke)constructor.newInstance((Object[])null);
      if (aWTKeyStroke == null)
        throw new IllegalArgumentException(str); 
    } catch (NoSuchMethodError noSuchMethodError) {
      throw new IllegalArgumentException(str);
    } catch (ExceptionInInitializerError exceptionInInitializerError) {
      throw new IllegalArgumentException(str);
    } catch (InstantiationException instantiationException) {
      throw new IllegalArgumentException(str);
    } catch (IllegalAccessException illegalAccessException) {
      throw new IllegalArgumentException(str);
    } catch (InvocationTargetException invocationTargetException) {
      throw new IllegalArgumentException(str);
    } 
    synchronized (AWTKeyStroke.class) {
      AppContext.getAppContext().put(AWTKeyStroke.class, paramClass);
      AppContext.getAppContext().remove(APP_CONTEXT_CACHE_KEY);
      AppContext.getAppContext().remove(APP_CONTEXT_KEYSTROKE_KEY);
    } 
  }
  
  private static Constructor getCtor(final Class clazz) { return (Constructor)AccessController.doPrivileged(new PrivilegedAction<Constructor>() {
          public Constructor run() {
            try {
              Constructor constructor = clazz.getDeclaredConstructor((Class[])null);
              if (constructor != null)
                constructor.setAccessible(true); 
              return constructor;
            } catch (SecurityException securityException) {
            
            } catch (NoSuchMethodException noSuchMethodException) {}
            return null;
          }
        }); }
  
  private static AWTKeyStroke getCachedStroke(char paramChar, int paramInt1, int paramInt2, boolean paramBoolean) {
    Map map = (Map)AppContext.getAppContext().get(APP_CONTEXT_CACHE_KEY);
    AWTKeyStroke aWTKeyStroke1 = (AWTKeyStroke)AppContext.getAppContext().get(APP_CONTEXT_KEYSTROKE_KEY);
    if (map == null) {
      map = new HashMap();
      AppContext.getAppContext().put(APP_CONTEXT_CACHE_KEY, map);
    } 
    if (aWTKeyStroke1 == null)
      try {
        Class clazz = getAWTKeyStrokeClass();
        aWTKeyStroke1 = (AWTKeyStroke)getCtor(clazz).newInstance((Object[])null);
        AppContext.getAppContext().put(APP_CONTEXT_KEYSTROKE_KEY, aWTKeyStroke1);
      } catch (InstantiationException instantiationException) {
        assert false;
      } catch (IllegalAccessException illegalAccessException) {
        assert false;
      } catch (InvocationTargetException invocationTargetException) {
        assert false;
      }  
    aWTKeyStroke1.keyChar = paramChar;
    aWTKeyStroke1.keyCode = paramInt1;
    aWTKeyStroke1.modifiers = mapNewModifiers(mapOldModifiers(paramInt2));
    aWTKeyStroke1.onKeyRelease = paramBoolean;
    AWTKeyStroke aWTKeyStroke2 = (AWTKeyStroke)map.get(aWTKeyStroke1);
    if (aWTKeyStroke2 == null) {
      aWTKeyStroke2 = aWTKeyStroke1;
      map.put(aWTKeyStroke2, aWTKeyStroke2);
      AppContext.getAppContext().remove(APP_CONTEXT_KEYSTROKE_KEY);
    } 
    return aWTKeyStroke2;
  }
  
  public static AWTKeyStroke getAWTKeyStroke(char paramChar) { return getCachedStroke(paramChar, 0, 0, false); }
  
  public static AWTKeyStroke getAWTKeyStroke(Character paramCharacter, int paramInt) {
    if (paramCharacter == null)
      throw new IllegalArgumentException("keyChar cannot be null"); 
    return getCachedStroke(paramCharacter.charValue(), 0, paramInt, false);
  }
  
  public static AWTKeyStroke getAWTKeyStroke(int paramInt1, int paramInt2, boolean paramBoolean) { return getCachedStroke('￿', paramInt1, paramInt2, paramBoolean); }
  
  public static AWTKeyStroke getAWTKeyStroke(int paramInt1, int paramInt2) { return getCachedStroke('￿', paramInt1, paramInt2, false); }
  
  public static AWTKeyStroke getAWTKeyStrokeForEvent(KeyEvent paramKeyEvent) {
    int i = paramKeyEvent.getID();
    switch (i) {
      case 401:
      case 402:
        return getCachedStroke('￿', paramKeyEvent.getKeyCode(), paramKeyEvent.getModifiers(), (i == 402));
      case 400:
        return getCachedStroke(paramKeyEvent.getKeyChar(), 0, paramKeyEvent.getModifiers(), false);
    } 
    return null;
  }
  
  public static AWTKeyStroke getAWTKeyStroke(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("String cannot be null"); 
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, " ");
    int i = 0;
    boolean bool = false;
    boolean bool1 = false;
    boolean bool2 = false;
    synchronized (AWTKeyStroke.class) {
      if (modifierKeywords == null) {
        HashMap hashMap = new HashMap(8, 1.0F);
        hashMap.put("shift", Integer.valueOf(65));
        hashMap.put("control", Integer.valueOf(130));
        hashMap.put("ctrl", Integer.valueOf(130));
        hashMap.put("meta", Integer.valueOf(260));
        hashMap.put("alt", Integer.valueOf(520));
        hashMap.put("altGraph", Integer.valueOf(8224));
        hashMap.put("button1", Integer.valueOf(1024));
        hashMap.put("button2", Integer.valueOf(2048));
        hashMap.put("button3", Integer.valueOf(4096));
        modifierKeywords = Collections.synchronizedMap(hashMap);
      } 
    } 
    int j = stringTokenizer.countTokens();
    for (byte b = 1; b <= j; b++) {
      String str = stringTokenizer.nextToken();
      if (bool1) {
        if (str.length() != 1 || b != j)
          throw new IllegalArgumentException("String formatted incorrectly"); 
        return getCachedStroke(str.charAt(0), 0, i, false);
      } 
      if (bool2 || bool || b == j) {
        if (b != j)
          throw new IllegalArgumentException("String formatted incorrectly"); 
        String str1 = "VK_" + str;
        int k = getVKValue(str1);
        return getCachedStroke('￿', k, i, bool);
      } 
      if (str.equals("released")) {
        bool = true;
      } else if (str.equals("pressed")) {
        bool2 = true;
      } else if (str.equals("typed")) {
        bool1 = true;
      } else {
        Integer integer = (Integer)modifierKeywords.get(str);
        if (integer != null) {
          i |= integer.intValue();
        } else {
          throw new IllegalArgumentException("String formatted incorrectly");
        } 
      } 
    } 
    throw new IllegalArgumentException("String formatted incorrectly");
  }
  
  private static VKCollection getVKCollection() {
    if (vks == null)
      vks = new VKCollection(); 
    return vks;
  }
  
  private static int getVKValue(String paramString) {
    VKCollection vKCollection = getVKCollection();
    Integer integer = vKCollection.findCode(paramString);
    if (integer == null) {
      int i = 0;
      try {
        i = KeyEvent.class.getField(paramString).getInt(KeyEvent.class);
      } catch (NoSuchFieldException noSuchFieldException) {
        throw new IllegalArgumentException("String formatted incorrectly");
      } catch (IllegalAccessException illegalAccessException) {
        throw new IllegalArgumentException("String formatted incorrectly");
      } 
      integer = Integer.valueOf(i);
      vKCollection.put(paramString, integer);
    } 
    return integer.intValue();
  }
  
  public final char getKeyChar() { return this.keyChar; }
  
  public final int getKeyCode() { return this.keyCode; }
  
  public final int getModifiers() { return this.modifiers; }
  
  public final boolean isOnKeyRelease() { return this.onKeyRelease; }
  
  public final int getKeyEventType() { return (this.keyCode == 0) ? 400 : (this.onKeyRelease ? 402 : 401); }
  
  public int hashCode() { return (this.keyChar + '\001') * 2 * (this.keyCode + 1) * (this.modifiers + 1) + (this.onKeyRelease ? '\001' : '\002'); }
  
  public final boolean equals(Object paramObject) {
    if (paramObject instanceof AWTKeyStroke) {
      AWTKeyStroke aWTKeyStroke = (AWTKeyStroke)paramObject;
      return (aWTKeyStroke.keyChar == this.keyChar && aWTKeyStroke.keyCode == this.keyCode && aWTKeyStroke.onKeyRelease == this.onKeyRelease && aWTKeyStroke.modifiers == this.modifiers);
    } 
    return false;
  }
  
  public String toString() { return (this.keyCode == 0) ? (getModifiersText(this.modifiers) + "typed " + this.keyChar) : (getModifiersText(this.modifiers) + (this.onKeyRelease ? "released" : "pressed") + " " + getVKText(this.keyCode)); }
  
  static String getModifiersText(int paramInt) {
    StringBuilder stringBuilder = new StringBuilder();
    if ((paramInt & 0x40) != 0)
      stringBuilder.append("shift "); 
    if ((paramInt & 0x80) != 0)
      stringBuilder.append("ctrl "); 
    if ((paramInt & 0x100) != 0)
      stringBuilder.append("meta "); 
    if ((paramInt & 0x200) != 0)
      stringBuilder.append("alt "); 
    if ((paramInt & 0x2000) != 0)
      stringBuilder.append("altGraph "); 
    if ((paramInt & 0x400) != 0)
      stringBuilder.append("button1 "); 
    if ((paramInt & 0x800) != 0)
      stringBuilder.append("button2 "); 
    if ((paramInt & 0x1000) != 0)
      stringBuilder.append("button3 "); 
    return stringBuilder.toString();
  }
  
  static String getVKText(int paramInt) {
    VKCollection vKCollection = getVKCollection();
    Integer integer = Integer.valueOf(paramInt);
    String str = vKCollection.findName(integer);
    if (str != null)
      return str.substring(3); 
    byte b1 = 25;
    Field[] arrayOfField = KeyEvent.class.getDeclaredFields();
    for (byte b2 = 0; b2 < arrayOfField.length; b2++) {
      try {
        if (arrayOfField[b2].getModifiers() == b1 && arrayOfField[b2].getType() == int.class && arrayOfField[b2].getName().startsWith("VK_") && arrayOfField[b2].getInt(KeyEvent.class) == paramInt) {
          str = arrayOfField[b2].getName();
          vKCollection.put(str, integer);
          return str.substring(3);
        } 
      } catch (IllegalAccessException illegalAccessException) {
        assert false;
      } 
    } 
    return "UNKNOWN";
  }
  
  protected Object readResolve() throws ObjectStreamException {
    synchronized (AWTKeyStroke.class) {
      if (getClass().equals(getAWTKeyStrokeClass()))
        return getCachedStroke(this.keyChar, this.keyCode, this.modifiers, this.onKeyRelease); 
    } 
    return this;
  }
  
  private static int mapOldModifiers(int paramInt) {
    if ((paramInt & true) != 0)
      paramInt |= 0x40; 
    if ((paramInt & 0x8) != 0)
      paramInt |= 0x200; 
    if ((paramInt & 0x20) != 0)
      paramInt |= 0x2000; 
    if ((paramInt & 0x2) != 0)
      paramInt |= 0x80; 
    if ((paramInt & 0x4) != 0)
      paramInt |= 0x100; 
    return 16320;
  }
  
  private static int mapNewModifiers(int paramInt) {
    if ((paramInt & 0x40) != 0)
      paramInt |= 0x1; 
    if ((paramInt & 0x200) != 0)
      paramInt |= 0x8; 
    if ((paramInt & 0x2000) != 0)
      paramInt |= 0x20; 
    if ((paramInt & 0x80) != 0)
      paramInt |= 0x2; 
    if ((paramInt & 0x100) != 0)
      paramInt |= 0x4; 
    return paramInt;
  }
  
  static  {
    Toolkit.loadLibraries();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\AWTKeyStroke.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */