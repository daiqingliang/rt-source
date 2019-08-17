package javax.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.swing.border.Border;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.plaf.ComponentUI;
import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ReflectUtil;
import sun.swing.SwingUtilities2;
import sun.util.CoreResourceBundleControl;

public class UIDefaults extends Hashtable<Object, Object> {
  private static final Object PENDING = new Object();
  
  private SwingPropertyChangeSupport changeSupport;
  
  private Vector<String> resourceBundles;
  
  private Locale defaultLocale = Locale.getDefault();
  
  private Map<Locale, Map<String, Object>> resourceCache;
  
  public UIDefaults() { this(700, 0.75F); }
  
  public UIDefaults(int paramInt, float paramFloat) {
    super(paramInt, paramFloat);
    this.resourceCache = new HashMap();
  }
  
  public UIDefaults(Object[] paramArrayOfObject) {
    super(paramArrayOfObject.length / 2);
    for (boolean bool = false; bool < paramArrayOfObject.length; bool += true)
      super.put(paramArrayOfObject[bool], paramArrayOfObject[bool + true]); 
  }
  
  public Object get(Object paramObject) {
    Object object = getFromHashtable(paramObject);
    return (object != null) ? object : getFromResourceBundle(paramObject, null);
  }
  
  private Object getFromHashtable(Object paramObject) {
    object = super.get(paramObject);
    if (object != PENDING && !(object instanceof ActiveValue) && !(object instanceof LazyValue))
      return object; 
    synchronized (this) {
      object = super.get(paramObject);
      if (object == PENDING) {
        do {
          try {
            wait();
          } catch (InterruptedException interruptedException) {}
          object = super.get(paramObject);
        } while (object == PENDING);
        return object;
      } 
      if (object instanceof LazyValue) {
        super.put(paramObject, PENDING);
      } else if (!(object instanceof ActiveValue)) {
        return object;
      } 
    } 
    if (object instanceof LazyValue) {
      try {
        object = ((LazyValue)object).createValue(this);
      } finally {
        synchronized (this) {
          if (object == null) {
            remove(paramObject);
          } else {
            super.put(paramObject, object);
          } 
          notifyAll();
        } 
      } 
    } else {
      object = ((ActiveValue)object).createValue(this);
    } 
    return object;
  }
  
  public Object get(Object paramObject, Locale paramLocale) {
    Object object = getFromHashtable(paramObject);
    return (object != null) ? object : getFromResourceBundle(paramObject, paramLocale);
  }
  
  private Object getFromResourceBundle(Object paramObject, Locale paramLocale) {
    if (this.resourceBundles == null || this.resourceBundles.isEmpty() || !(paramObject instanceof String))
      return null; 
    if (paramLocale == null) {
      if (this.defaultLocale == null)
        return null; 
      paramLocale = this.defaultLocale;
    } 
    synchronized (this) {
      return getResourceCache(paramLocale).get(paramObject);
    } 
  }
  
  private Map<String, Object> getResourceCache(Locale paramLocale) {
    Map map = (Map)this.resourceCache.get(paramLocale);
    if (map == null) {
      map = new TextAndMnemonicHashMap(null);
      for (int i = this.resourceBundles.size() - 1; i >= 0; i--) {
        String str = (String)this.resourceBundles.get(i);
        try {
          ResourceBundle resourceBundle;
          CoreResourceBundleControl coreResourceBundleControl = CoreResourceBundleControl.getRBControlInstance(str);
          if (coreResourceBundleControl != null) {
            resourceBundle = ResourceBundle.getBundle(str, paramLocale, coreResourceBundleControl);
          } else {
            resourceBundle = ResourceBundle.getBundle(str, paramLocale, ClassLoader.getSystemClassLoader());
          } 
          Enumeration enumeration = resourceBundle.getKeys();
          while (enumeration.hasMoreElements()) {
            String str1 = (String)enumeration.nextElement();
            if (map.get(str1) == null) {
              Object object = resourceBundle.getObject(str1);
              map.put(str1, object);
            } 
          } 
        } catch (MissingResourceException missingResourceException) {}
      } 
      this.resourceCache.put(paramLocale, map);
    } 
    return map;
  }
  
  public Object put(Object paramObject1, Object paramObject2) {
    Object object = (paramObject2 == null) ? remove(paramObject1) : super.put(paramObject1, paramObject2);
    if (paramObject1 instanceof String)
      firePropertyChange((String)paramObject1, object, paramObject2); 
    return object;
  }
  
  public void putDefaults(Object[] paramArrayOfObject) {
    boolean bool = false;
    int i = paramArrayOfObject.length;
    while (bool < i) {
      Object object = paramArrayOfObject[bool + true];
      if (object == null) {
        remove(paramArrayOfObject[bool]);
      } else {
        super.put(paramArrayOfObject[bool], object);
      } 
      bool += true;
    } 
    firePropertyChange("UIDefaults", null, null);
  }
  
  public Font getFont(Object paramObject) {
    Object object = get(paramObject);
    return (object instanceof Font) ? (Font)object : null;
  }
  
  public Font getFont(Object paramObject, Locale paramLocale) {
    Object object = get(paramObject, paramLocale);
    return (object instanceof Font) ? (Font)object : null;
  }
  
  public Color getColor(Object paramObject) {
    Object object = get(paramObject);
    return (object instanceof Color) ? (Color)object : null;
  }
  
  public Color getColor(Object paramObject, Locale paramLocale) {
    Object object = get(paramObject, paramLocale);
    return (object instanceof Color) ? (Color)object : null;
  }
  
  public Icon getIcon(Object paramObject) {
    Object object = get(paramObject);
    return (object instanceof Icon) ? (Icon)object : null;
  }
  
  public Icon getIcon(Object paramObject, Locale paramLocale) {
    Object object = get(paramObject, paramLocale);
    return (object instanceof Icon) ? (Icon)object : null;
  }
  
  public Border getBorder(Object paramObject) {
    Object object = get(paramObject);
    return (object instanceof Border) ? (Border)object : null;
  }
  
  public Border getBorder(Object paramObject, Locale paramLocale) {
    Object object = get(paramObject, paramLocale);
    return (object instanceof Border) ? (Border)object : null;
  }
  
  public String getString(Object paramObject) {
    Object object = get(paramObject);
    return (object instanceof String) ? (String)object : null;
  }
  
  public String getString(Object paramObject, Locale paramLocale) {
    Object object = get(paramObject, paramLocale);
    return (object instanceof String) ? (String)object : null;
  }
  
  public int getInt(Object paramObject) {
    Object object = get(paramObject);
    return (object instanceof Integer) ? ((Integer)object).intValue() : 0;
  }
  
  public int getInt(Object paramObject, Locale paramLocale) {
    Object object = get(paramObject, paramLocale);
    return (object instanceof Integer) ? ((Integer)object).intValue() : 0;
  }
  
  public boolean getBoolean(Object paramObject) {
    Object object = get(paramObject);
    return (object instanceof Boolean) ? ((Boolean)object).booleanValue() : 0;
  }
  
  public boolean getBoolean(Object paramObject, Locale paramLocale) {
    Object object = get(paramObject, paramLocale);
    return (object instanceof Boolean) ? ((Boolean)object).booleanValue() : 0;
  }
  
  public Insets getInsets(Object paramObject) {
    Object object = get(paramObject);
    return (object instanceof Insets) ? (Insets)object : null;
  }
  
  public Insets getInsets(Object paramObject, Locale paramLocale) {
    Object object = get(paramObject, paramLocale);
    return (object instanceof Insets) ? (Insets)object : null;
  }
  
  public Dimension getDimension(Object paramObject) {
    Object object = get(paramObject);
    return (object instanceof Dimension) ? (Dimension)object : null;
  }
  
  public Dimension getDimension(Object paramObject, Locale paramLocale) {
    Object object = get(paramObject, paramLocale);
    return (object instanceof Dimension) ? (Dimension)object : null;
  }
  
  public Class<? extends ComponentUI> getUIClass(String paramString, ClassLoader paramClassLoader) {
    try {
      String str = (String)get(paramString);
      if (str != null) {
        ReflectUtil.checkPackageAccess(str);
        Class clazz = (Class)get(str);
        if (clazz == null) {
          if (paramClassLoader == null) {
            clazz = SwingUtilities.loadSystemClass(str);
          } else {
            clazz = paramClassLoader.loadClass(str);
          } 
          if (clazz != null)
            put(str, clazz); 
        } 
        return clazz;
      } 
    } catch (ClassNotFoundException classNotFoundException) {
      return null;
    } catch (ClassCastException classCastException) {
      return null;
    } 
    return null;
  }
  
  public Class<? extends ComponentUI> getUIClass(String paramString) { return getUIClass(paramString, null); }
  
  protected void getUIError(String paramString) {
    System.err.println("UIDefaults.getUI() failed: " + paramString);
    try {
      throw new Error();
    } catch (Throwable throwable) {
      throwable.printStackTrace();
      return;
    } 
  }
  
  public ComponentUI getUI(JComponent paramJComponent) {
    Object object1 = get("ClassLoader");
    ClassLoader classLoader = (object1 != null) ? (ClassLoader)object1 : paramJComponent.getClass().getClassLoader();
    Class clazz = getUIClass(paramJComponent.getUIClassID(), classLoader);
    Object object2 = null;
    if (clazz == null) {
      getUIError("no ComponentUI class for: " + paramJComponent);
    } else {
      try {
        Method method = (Method)get(clazz);
        if (method == null) {
          method = clazz.getMethod("createUI", new Class[] { JComponent.class });
          put(clazz, method);
        } 
        object2 = MethodUtil.invoke(method, null, new Object[] { paramJComponent });
      } catch (NoSuchMethodException noSuchMethodException) {
        getUIError("static createUI() method not found in " + clazz);
      } catch (Exception exception) {
        getUIError("createUI() failed for " + paramJComponent + " " + exception);
      } 
    } 
    return (ComponentUI)object2;
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    if (this.changeSupport == null)
      this.changeSupport = new SwingPropertyChangeSupport(this); 
    this.changeSupport.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    if (this.changeSupport != null)
      this.changeSupport.removePropertyChangeListener(paramPropertyChangeListener); 
  }
  
  public PropertyChangeListener[] getPropertyChangeListeners() { return (this.changeSupport == null) ? new PropertyChangeListener[0] : this.changeSupport.getPropertyChangeListeners(); }
  
  protected void firePropertyChange(String paramString, Object paramObject1, Object paramObject2) {
    if (this.changeSupport != null)
      this.changeSupport.firePropertyChange(paramString, paramObject1, paramObject2); 
  }
  
  public void addResourceBundle(String paramString) {
    if (paramString == null)
      return; 
    if (this.resourceBundles == null)
      this.resourceBundles = new Vector(5); 
    if (!this.resourceBundles.contains(paramString)) {
      this.resourceBundles.add(paramString);
      this.resourceCache.clear();
    } 
  }
  
  public void removeResourceBundle(String paramString) {
    if (this.resourceBundles != null)
      this.resourceBundles.remove(paramString); 
    this.resourceCache.clear();
  }
  
  public void setDefaultLocale(Locale paramLocale) { this.defaultLocale = paramLocale; }
  
  public Locale getDefaultLocale() { return this.defaultLocale; }
  
  public static interface ActiveValue {
    Object createValue(UIDefaults param1UIDefaults);
  }
  
  public static class LazyInputMap implements LazyValue {
    private Object[] bindings;
    
    public LazyInputMap(Object[] param1ArrayOfObject) { this.bindings = param1ArrayOfObject; }
    
    public Object createValue(UIDefaults param1UIDefaults) { return (this.bindings != null) ? LookAndFeel.makeInputMap(this.bindings) : null; }
  }
  
  public static interface LazyValue {
    Object createValue(UIDefaults param1UIDefaults);
  }
  
  public static class ProxyLazyValue implements LazyValue {
    private AccessControlContext acc = AccessController.getContext();
    
    private String className;
    
    private String methodName;
    
    private Object[] args;
    
    public ProxyLazyValue(String param1String) { this(param1String, (String)null); }
    
    public ProxyLazyValue(String param1String1, String param1String2) { this(param1String1, param1String2, null); }
    
    public ProxyLazyValue(String param1String, Object[] param1ArrayOfObject) { this(param1String, null, param1ArrayOfObject); }
    
    public ProxyLazyValue(String param1String1, String param1String2, Object[] param1ArrayOfObject) {
      this.className = param1String1;
      this.methodName = param1String2;
      if (param1ArrayOfObject != null)
        this.args = (Object[])param1ArrayOfObject.clone(); 
    }
    
    public Object createValue(final UIDefaults table) {
      if (this.acc == null && System.getSecurityManager() != null)
        throw new SecurityException("null AccessControlContext"); 
      return AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
              try {
                Object object;
                if (table == null || !(object = table.get("ClassLoader") instanceof ClassLoader)) {
                  object = Thread.currentThread().getContextClassLoader();
                  if (object == null)
                    object = ClassLoader.getSystemClassLoader(); 
                } 
                ReflectUtil.checkPackageAccess(UIDefaults.ProxyLazyValue.this.className);
                Class clazz = Class.forName(UIDefaults.ProxyLazyValue.this.className, true, (ClassLoader)object);
                SwingUtilities2.checkAccess(clazz.getModifiers());
                if (UIDefaults.ProxyLazyValue.this.methodName != null) {
                  Class[] arrayOfClass1 = UIDefaults.ProxyLazyValue.this.getClassArray(UIDefaults.ProxyLazyValue.this.args);
                  Method method = clazz.getMethod(UIDefaults.ProxyLazyValue.this.methodName, arrayOfClass1);
                  return MethodUtil.invoke(method, clazz, UIDefaults.ProxyLazyValue.this.args);
                } 
                Class[] arrayOfClass = UIDefaults.ProxyLazyValue.this.getClassArray(UIDefaults.ProxyLazyValue.this.args);
                Constructor constructor = clazz.getConstructor(arrayOfClass);
                SwingUtilities2.checkAccess(constructor.getModifiers());
                return constructor.newInstance(UIDefaults.ProxyLazyValue.this.args);
              } catch (Exception exception) {
                return null;
              } 
            }
          }this.acc);
    }
    
    private Class[] getClassArray(Object[] param1ArrayOfObject) {
      Class[] arrayOfClass = null;
      if (param1ArrayOfObject != null) {
        arrayOfClass = new Class[param1ArrayOfObject.length];
        for (byte b = 0; b < param1ArrayOfObject.length; b++) {
          if (param1ArrayOfObject[b] instanceof Integer) {
            arrayOfClass[b] = int.class;
          } else if (param1ArrayOfObject[b] instanceof Boolean) {
            arrayOfClass[b] = boolean.class;
          } else if (param1ArrayOfObject[b] instanceof javax.swing.plaf.ColorUIResource) {
            arrayOfClass[b] = Color.class;
          } else {
            arrayOfClass[b] = param1ArrayOfObject[b].getClass();
          } 
        } 
      } 
      return arrayOfClass;
    }
    
    private String printArgs(Object[] param1ArrayOfObject) {
      String str = "{";
      if (param1ArrayOfObject != null) {
        for (byte b = 0; b < param1ArrayOfObject.length - 1; b++)
          str = str.concat(param1ArrayOfObject[b] + ","); 
        str = str.concat(param1ArrayOfObject[param1ArrayOfObject.length - 1] + "}");
      } else {
        str = str.concat("}");
      } 
      return str;
    }
  }
  
  private static class TextAndMnemonicHashMap extends HashMap<String, Object> {
    static final String AND_MNEMONIC = "AndMnemonic";
    
    static final String TITLE_SUFFIX = ".titleAndMnemonic";
    
    static final String TEXT_SUFFIX = ".textAndMnemonic";
    
    private TextAndMnemonicHashMap() {}
    
    public Object get(Object param1Object) {
      Object object = super.get(param1Object);
      if (object == null) {
        boolean bool = false;
        String str1 = param1Object.toString();
        String str2 = null;
        if (str1.endsWith("AndMnemonic"))
          return null; 
        if (str1.endsWith(".mnemonic")) {
          str2 = composeKey(str1, 9, ".textAndMnemonic");
        } else if (str1.endsWith("NameMnemonic")) {
          str2 = composeKey(str1, 12, ".textAndMnemonic");
        } else if (str1.endsWith("Mnemonic")) {
          str2 = composeKey(str1, 8, ".textAndMnemonic");
          bool = true;
        } 
        if (str2 != null) {
          object = super.get(str2);
          if (object == null && bool) {
            str2 = composeKey(str1, 8, ".titleAndMnemonic");
            object = super.get(str2);
          } 
          return (object == null) ? null : getMnemonicFromProperty(object.toString());
        } 
        if (str1.endsWith("NameText")) {
          str2 = composeKey(str1, 8, ".textAndMnemonic");
        } else if (str1.endsWith(".nameText")) {
          str2 = composeKey(str1, 9, ".textAndMnemonic");
        } else if (str1.endsWith("Text")) {
          str2 = composeKey(str1, 4, ".textAndMnemonic");
        } else if (str1.endsWith("Title")) {
          str2 = composeKey(str1, 5, ".titleAndMnemonic");
        } 
        if (str2 != null) {
          object = super.get(str2);
          return (object == null) ? null : getTextFromProperty(object.toString());
        } 
        if (str1.endsWith("DisplayedMnemonicIndex")) {
          str2 = composeKey(str1, 22, ".textAndMnemonic");
          object = super.get(str2);
          if (object == null) {
            str2 = composeKey(str1, 22, ".titleAndMnemonic");
            object = super.get(str2);
          } 
          return (object == null) ? null : getIndexFromProperty(object.toString());
        } 
      } 
      return object;
    }
    
    String composeKey(String param1String1, int param1Int, String param1String2) { return param1String1.substring(0, param1String1.length() - param1Int) + param1String2; }
    
    String getTextFromProperty(String param1String) { return param1String.replace("&", ""); }
    
    String getMnemonicFromProperty(String param1String) {
      int i = param1String.indexOf('&');
      if (0 <= i && i < param1String.length() - 1) {
        char c = param1String.charAt(i + 1);
        return Integer.toString(Character.toUpperCase(c));
      } 
      return null;
    }
    
    String getIndexFromProperty(String param1String) {
      int i = param1String.indexOf('&');
      return (i == -1) ? null : Integer.toString(i);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\UIDefaults.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */