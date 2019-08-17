package java.beans;

import com.sun.beans.finder.PrimitiveWrapperMap;
import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Insets;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuShortcut;
import java.awt.Window;
import java.awt.font.TextAttribute;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.WeakHashMap;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JTabbedPane;
import javax.swing.border.MatteBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import sun.reflect.misc.ReflectUtil;

class MetaData {
  private static final Map<String, Field> fields = Collections.synchronizedMap(new WeakHashMap());
  
  private static Hashtable<String, PersistenceDelegate> internalPersistenceDelegates = new Hashtable();
  
  private static PersistenceDelegate nullPersistenceDelegate = new NullPersistenceDelegate();
  
  private static PersistenceDelegate enumPersistenceDelegate = new EnumPersistenceDelegate();
  
  private static PersistenceDelegate primitivePersistenceDelegate = new PrimitivePersistenceDelegate();
  
  private static PersistenceDelegate defaultPersistenceDelegate = new DefaultPersistenceDelegate();
  
  private static PersistenceDelegate arrayPersistenceDelegate;
  
  private static PersistenceDelegate proxyPersistenceDelegate;
  
  public static PersistenceDelegate getPersistenceDelegate(Class paramClass) {
    if (paramClass == null)
      return nullPersistenceDelegate; 
    if (Enum.class.isAssignableFrom(paramClass))
      return enumPersistenceDelegate; 
    if (null != XMLEncoder.primitiveTypeFor(paramClass))
      return primitivePersistenceDelegate; 
    if (paramClass.isArray()) {
      if (arrayPersistenceDelegate == null)
        arrayPersistenceDelegate = new ArrayPersistenceDelegate(); 
      return arrayPersistenceDelegate;
    } 
    try {
      if (Proxy.isProxyClass(paramClass)) {
        if (proxyPersistenceDelegate == null)
          proxyPersistenceDelegate = new ProxyPersistenceDelegate(); 
        return proxyPersistenceDelegate;
      } 
    } catch (Exception exception) {}
    String str = paramClass.getName();
    PersistenceDelegate persistenceDelegate = (PersistenceDelegate)getBeanAttribute(paramClass, "persistenceDelegate");
    if (persistenceDelegate == null) {
      persistenceDelegate = (PersistenceDelegate)internalPersistenceDelegates.get(str);
      if (persistenceDelegate != null)
        return persistenceDelegate; 
      internalPersistenceDelegates.put(str, defaultPersistenceDelegate);
      try {
        String str1 = paramClass.getName();
        Class clazz = Class.forName("java.beans.MetaData$" + str1.replace('.', '_') + "_PersistenceDelegate");
        persistenceDelegate = (PersistenceDelegate)clazz.newInstance();
        internalPersistenceDelegates.put(str, persistenceDelegate);
      } catch (ClassNotFoundException classNotFoundException) {
        String[] arrayOfString = getConstructorProperties(paramClass);
        if (arrayOfString != null) {
          persistenceDelegate = new DefaultPersistenceDelegate(arrayOfString);
          internalPersistenceDelegates.put(str, persistenceDelegate);
        } 
      } catch (Exception exception) {
        System.err.println("Internal error: " + exception);
      } 
    } 
    return (persistenceDelegate != null) ? persistenceDelegate : defaultPersistenceDelegate;
  }
  
  private static String[] getConstructorProperties(Class<?> paramClass) {
    String[] arrayOfString = null;
    int i = 0;
    for (Constructor constructor : paramClass.getConstructors()) {
      String[] arrayOfString1 = getAnnotationValue(constructor);
      if (arrayOfString1 != null && i < arrayOfString1.length && isValid(constructor, arrayOfString1)) {
        arrayOfString = arrayOfString1;
        i = arrayOfString1.length;
      } 
    } 
    return arrayOfString;
  }
  
  private static String[] getAnnotationValue(Constructor<?> paramConstructor) {
    ConstructorProperties constructorProperties = (ConstructorProperties)paramConstructor.getAnnotation(ConstructorProperties.class);
    return (constructorProperties != null) ? constructorProperties.value() : null;
  }
  
  private static boolean isValid(Constructor<?> paramConstructor, String[] paramArrayOfString) {
    Class[] arrayOfClass = paramConstructor.getParameterTypes();
    if (paramArrayOfString.length != arrayOfClass.length)
      return false; 
    for (String str : paramArrayOfString) {
      if (str == null)
        return false; 
    } 
    return true;
  }
  
  private static Object getBeanAttribute(Class<?> paramClass, String paramString) {
    try {
      return Introspector.getBeanInfo(paramClass).getBeanDescriptor().getValue(paramString);
    } catch (IntrospectionException introspectionException) {
      return null;
    } 
  }
  
  static Object getPrivateFieldValue(Object paramObject, String paramString) {
    Field field = (Field)fields.get(paramString);
    if (field == null) {
      int i = paramString.lastIndexOf('.');
      final String className = paramString.substring(0, i);
      final String fieldName = paramString.substring(1 + i);
      field = (Field)AccessController.doPrivileged(new PrivilegedAction<Field>() {
            public Field run() {
              try {
                Field field = Class.forName(className).getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
              } catch (ClassNotFoundException classNotFoundException) {
                throw new IllegalStateException("Could not find class", classNotFoundException);
              } catch (NoSuchFieldException noSuchFieldException) {
                throw new IllegalStateException("Could not find field", noSuchFieldException);
              } 
            }
          });
      fields.put(paramString, field);
    } 
    try {
      return field.get(paramObject);
    } catch (IllegalAccessException illegalAccessException) {
      throw new IllegalStateException("Could not get value of the field", illegalAccessException);
    } 
  }
  
  static  {
    internalPersistenceDelegates.put("java.net.URI", new PrimitivePersistenceDelegate());
    internalPersistenceDelegates.put("javax.swing.plaf.BorderUIResource$MatteBorderUIResource", new javax_swing_border_MatteBorder_PersistenceDelegate());
    internalPersistenceDelegates.put("javax.swing.plaf.FontUIResource", new java_awt_Font_PersistenceDelegate());
    internalPersistenceDelegates.put("javax.swing.KeyStroke", new java_awt_AWTKeyStroke_PersistenceDelegate());
    internalPersistenceDelegates.put("java.sql.Date", new java_util_Date_PersistenceDelegate());
    internalPersistenceDelegates.put("java.sql.Time", new java_util_Date_PersistenceDelegate());
    internalPersistenceDelegates.put("java.util.JumboEnumSet", new java_util_EnumSet_PersistenceDelegate());
    internalPersistenceDelegates.put("java.util.RegularEnumSet", new java_util_EnumSet_PersistenceDelegate());
  }
  
  static final class ArrayPersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object param1Object1, Object param1Object2) { return (param1Object2 != null && param1Object1.getClass() == param1Object2.getClass() && Array.getLength(param1Object1) == Array.getLength(param1Object2)); }
    
    protected Expression instantiate(Object param1Object, Encoder param1Encoder) {
      Class clazz = param1Object.getClass();
      return new Expression(param1Object, Array.class, "newInstance", new Object[] { clazz.getComponentType(), new Integer(Array.getLength(param1Object)) });
    }
    
    protected void initialize(Class<?> param1Class, Object param1Object1, Object param1Object2, Encoder param1Encoder) {
      int i = Array.getLength(param1Object1);
      for (byte b = 0; b < i; b++) {
        Integer integer = new Integer(b);
        Expression expression1 = new Expression(param1Object1, "get", new Object[] { integer });
        Expression expression2 = new Expression(param1Object2, "get", new Object[] { integer });
        try {
          Object object1 = expression1.getValue();
          Object object2 = expression2.getValue();
          param1Encoder.writeExpression(expression1);
          if (!Objects.equals(object2, param1Encoder.get(object1)))
            DefaultPersistenceDelegate.invokeStatement(param1Object1, "set", new Object[] { integer, object1 }, param1Encoder); 
        } catch (Exception exception) {
          param1Encoder.getExceptionListener().exceptionThrown(exception);
        } 
      } 
    }
  }
  
  static final class EnumPersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object param1Object1, Object param1Object2) { return (param1Object1 == param1Object2); }
    
    protected Expression instantiate(Object param1Object, Encoder param1Encoder) {
      Enum enum = (Enum)param1Object;
      return new Expression(enum, Enum.class, "valueOf", new Object[] { enum.getDeclaringClass(), enum.name() });
    }
  }
  
  static final class NullPersistenceDelegate extends PersistenceDelegate {
    protected void initialize(Class<?> param1Class, Object param1Object1, Object param1Object2, Encoder param1Encoder) {}
    
    protected Expression instantiate(Object param1Object, Encoder param1Encoder) { return null; }
    
    public void writeObject(Object param1Object, Encoder param1Encoder) {}
  }
  
  static final class PrimitivePersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object param1Object1, Object param1Object2) { return param1Object1.equals(param1Object2); }
    
    protected Expression instantiate(Object param1Object, Encoder param1Encoder) { return new Expression(param1Object, param1Object.getClass(), "new", new Object[] { param1Object.toString() }); }
  }
  
  static final class ProxyPersistenceDelegate extends PersistenceDelegate {
    protected Expression instantiate(Object param1Object, Encoder param1Encoder) {
      Class clazz = param1Object.getClass();
      Proxy proxy;
      InvocationHandler invocationHandler = (proxy = (Proxy)param1Object).getInvocationHandler(proxy);
      if (invocationHandler instanceof EventHandler) {
        EventHandler eventHandler = (EventHandler)invocationHandler;
        Vector vector = new Vector();
        vector.add(clazz.getInterfaces()[0]);
        vector.add(eventHandler.getTarget());
        vector.add(eventHandler.getAction());
        if (eventHandler.getEventPropertyName() != null)
          vector.add(eventHandler.getEventPropertyName()); 
        if (eventHandler.getListenerMethodName() != null) {
          vector.setSize(4);
          vector.add(eventHandler.getListenerMethodName());
        } 
        return new Expression(param1Object, EventHandler.class, "create", vector.toArray());
      } 
      return new Expression(param1Object, Proxy.class, "newProxyInstance", new Object[] { clazz.getClassLoader(), clazz.getInterfaces(), invocationHandler });
    }
  }
  
  static class StaticFieldsPersistenceDelegate extends PersistenceDelegate {
    protected void installFields(Encoder param1Encoder, Class<?> param1Class) {
      if (Modifier.isPublic(param1Class.getModifiers()) && ReflectUtil.isPackageAccessible(param1Class)) {
        Field[] arrayOfField = param1Class.getFields();
        for (byte b = 0; b < arrayOfField.length; b++) {
          Field field = arrayOfField[b];
          if (Object.class.isAssignableFrom(field.getType()))
            param1Encoder.writeExpression(new Expression(field, "get", new Object[] { null })); 
        } 
      } 
    }
    
    protected Expression instantiate(Object param1Object, Encoder param1Encoder) { throw new RuntimeException("Unrecognized instance: " + param1Object); }
    
    public void writeObject(Object param1Object, Encoder param1Encoder) {
      if (param1Encoder.getAttribute(this) == null) {
        param1Encoder.setAttribute(this, Boolean.TRUE);
        installFields(param1Encoder, param1Object.getClass());
      } 
      super.writeObject(param1Object, param1Encoder);
    }
  }
  
  static final class java_awt_AWTKeyStroke_PersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object param1Object1, Object param1Object2) { return param1Object1.equals(param1Object2); }
    
    protected Expression instantiate(Object param1Object, Encoder param1Encoder) {
      AWTKeyStroke aWTKeyStroke = (AWTKeyStroke)param1Object;
      char c = aWTKeyStroke.getKeyChar();
      int i = aWTKeyStroke.getKeyCode();
      int j = aWTKeyStroke.getModifiers();
      boolean bool = aWTKeyStroke.isOnKeyRelease();
      Object[] arrayOfObject = null;
      if (c == Character.MAX_VALUE) {
        new Object[2][0] = Integer.valueOf(i);
        new Object[2][1] = Integer.valueOf(j);
        new Object[3][0] = Integer.valueOf(i);
        new Object[3][1] = Integer.valueOf(j);
        new Object[3][2] = Boolean.valueOf(bool);
        arrayOfObject = !bool ? new Object[2] : new Object[3];
      } else if (i == 0) {
        if (!bool) {
          new Object[1][0] = Character.valueOf(c);
          new Object[2][0] = Character.valueOf(c);
          new Object[2][1] = Integer.valueOf(j);
          arrayOfObject = (j == 0) ? new Object[1] : new Object[2];
        } else if (j == 0) {
          arrayOfObject = new Object[] { Character.valueOf(c), Boolean.valueOf(bool) };
        } 
      } 
      if (arrayOfObject == null)
        throw new IllegalStateException("Unsupported KeyStroke: " + aWTKeyStroke); 
      Class clazz = aWTKeyStroke.getClass();
      String str = clazz.getName();
      int k = str.lastIndexOf('.') + 1;
      if (k > 0)
        str = str.substring(k); 
      return new Expression(aWTKeyStroke, clazz, "get" + str, arrayOfObject);
    }
  }
  
  static final class java_awt_BorderLayout_PersistenceDelegate extends DefaultPersistenceDelegate {
    private static final String[] CONSTRAINTS = { "North", "South", "East", "West", "Center", "First", "Last", "Before", "After" };
    
    protected void initialize(Class<?> param1Class, Object param1Object1, Object param1Object2, Encoder param1Encoder) {
      super.initialize(param1Class, param1Object1, param1Object2, param1Encoder);
      BorderLayout borderLayout1 = (BorderLayout)param1Object1;
      BorderLayout borderLayout2 = (BorderLayout)param1Object2;
      for (String str : CONSTRAINTS) {
        Component component1 = borderLayout1.getLayoutComponent(str);
        Component component2 = borderLayout2.getLayoutComponent(str);
        if (component1 != null && component2 == null)
          invokeStatement(param1Object1, "addLayoutComponent", new Object[] { component1, str }, param1Encoder); 
      } 
    }
  }
  
  static final class java_awt_CardLayout_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> param1Class, Object param1Object1, Object param1Object2, Encoder param1Encoder) {
      super.initialize(param1Class, param1Object1, param1Object2, param1Encoder);
      if (getVector(param1Object2).isEmpty())
        for (Object object : getVector(param1Object1)) {
          Object[] arrayOfObject = { MetaData.getPrivateFieldValue(object, "java.awt.CardLayout$Card.name"), MetaData.getPrivateFieldValue(object, "java.awt.CardLayout$Card.comp") };
          invokeStatement(param1Object1, "addLayoutComponent", arrayOfObject, param1Encoder);
        }  
    }
    
    protected boolean mutatesTo(Object param1Object1, Object param1Object2) { return (super.mutatesTo(param1Object1, param1Object2) && getVector(param1Object2).isEmpty()); }
    
    private static Vector<?> getVector(Object param1Object) { return (Vector)MetaData.getPrivateFieldValue(param1Object, "java.awt.CardLayout.vector"); }
  }
  
  static final class java_awt_Choice_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> param1Class, Object param1Object1, Object param1Object2, Encoder param1Encoder) {
      super.initialize(param1Class, param1Object1, param1Object2, param1Encoder);
      Choice choice1 = (Choice)param1Object1;
      Choice choice2 = (Choice)param1Object2;
      for (int i = choice2.getItemCount(); i < choice1.getItemCount(); i++) {
        invokeStatement(param1Object1, "add", new Object[] { choice1.getItem(i) }, param1Encoder);
      } 
    }
  }
  
  static final class java_awt_Component_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> param1Class, Object param1Object1, Object param1Object2, Encoder param1Encoder) {
      super.initialize(param1Class, param1Object1, param1Object2, param1Encoder);
      Component component1 = (Component)param1Object1;
      Component component2 = (Component)param1Object2;
      if (!(param1Object1 instanceof Window)) {
        Color color1 = component1.isBackgroundSet() ? component1.getBackground() : null;
        Color color2 = component2.isBackgroundSet() ? component2.getBackground() : null;
        if (!Objects.equals(color1, color2))
          invokeStatement(param1Object1, "setBackground", new Object[] { color1 }, param1Encoder); 
        Color color3 = component1.isForegroundSet() ? component1.getForeground() : null;
        Color color4 = component2.isForegroundSet() ? component2.getForeground() : null;
        if (!Objects.equals(color3, color4))
          invokeStatement(param1Object1, "setForeground", new Object[] { color3 }, param1Encoder); 
        Font font1 = component1.isFontSet() ? component1.getFont() : null;
        Font font2 = component2.isFontSet() ? component2.getFont() : null;
        if (!Objects.equals(font1, font2))
          invokeStatement(param1Object1, "setFont", new Object[] { font1 }, param1Encoder); 
      } 
      Container container = component1.getParent();
      if (container == null || container.getLayout() == null) {
        boolean bool1 = component1.getLocation().equals(component2.getLocation());
        boolean bool2 = component1.getSize().equals(component2.getSize());
        if (!bool1 && !bool2) {
          invokeStatement(param1Object1, "setBounds", new Object[] { component1.getBounds() }, param1Encoder);
        } else if (!bool1) {
          invokeStatement(param1Object1, "setLocation", new Object[] { component1.getLocation() }, param1Encoder);
        } else if (!bool2) {
          invokeStatement(param1Object1, "setSize", new Object[] { component1.getSize() }, param1Encoder);
        } 
      } 
    }
  }
  
  static final class java_awt_Container_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> param1Class, Object param1Object1, Object param1Object2, Encoder param1Encoder) {
      super.initialize(param1Class, param1Object1, param1Object2, param1Encoder);
      if (param1Object1 instanceof javax.swing.JScrollPane)
        return; 
      Container container1 = (Container)param1Object1;
      Component[] arrayOfComponent1 = container1.getComponents();
      Container container2 = (Container)param1Object2;
      Component[] arrayOfComponent2 = (container2 == null) ? new Component[0] : container2.getComponents();
      BorderLayout borderLayout = (container1.getLayout() instanceof BorderLayout) ? (BorderLayout)container1.getLayout() : null;
      JLayeredPane jLayeredPane = (param1Object1 instanceof JLayeredPane) ? (JLayeredPane)param1Object1 : null;
      for (int i = arrayOfComponent2.length; i < arrayOfComponent1.length; i++) {
        new Object[2][0] = arrayOfComponent1[i];
        new Object[2][1] = borderLayout.getConstraints(arrayOfComponent1[i]);
        new Object[3][0] = arrayOfComponent1[i];
        new Object[3][1] = Integer.valueOf(jLayeredPane.getLayer(arrayOfComponent1[i]));
        new Object[3][2] = Integer.valueOf(-1);
        new Object[1][0] = arrayOfComponent1[i];
        Object[] arrayOfObject = (borderLayout != null) ? new Object[2] : ((jLayeredPane != null) ? new Object[3] : new Object[1]);
        invokeStatement(param1Object1, "add", arrayOfObject, param1Encoder);
      } 
    }
  }
  
  static final class java_awt_Font_PersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object param1Object1, Object param1Object2) { return param1Object1.equals(param1Object2); }
    
    protected Expression instantiate(Object param1Object, Encoder param1Encoder) {
      Font font = (Font)param1Object;
      byte b1 = 0;
      String str = null;
      byte b2 = 0;
      int i = 12;
      Map map = font.getAttributes();
      HashMap hashMap = new HashMap(map.size());
      for (TextAttribute textAttribute : map.keySet()) {
        Object object = map.get(textAttribute);
        if (object != null)
          hashMap.put(textAttribute, object); 
        if (textAttribute == TextAttribute.FAMILY) {
          if (object instanceof String) {
            b1++;
            str = (String)object;
          } 
          continue;
        } 
        if (textAttribute == TextAttribute.WEIGHT) {
          if (TextAttribute.WEIGHT_REGULAR.equals(object)) {
            b1++;
            continue;
          } 
          if (TextAttribute.WEIGHT_BOLD.equals(object)) {
            b1++;
            b2 |= true;
          } 
          continue;
        } 
        if (textAttribute == TextAttribute.POSTURE) {
          if (TextAttribute.POSTURE_REGULAR.equals(object)) {
            b1++;
            continue;
          } 
          if (TextAttribute.POSTURE_OBLIQUE.equals(object)) {
            b1++;
            b2 |= 0x2;
          } 
          continue;
        } 
        if (textAttribute == TextAttribute.SIZE && object instanceof Number) {
          Number number = (Number)object;
          i = number.intValue();
          if (i == number.floatValue())
            b1++; 
        } 
      } 
      Class clazz = font.getClass();
      return (b1 == hashMap.size()) ? new Expression(font, clazz, "new", new Object[] { str, Integer.valueOf(b2), Integer.valueOf(i) }) : ((clazz == Font.class) ? new Expression(font, clazz, "getFont", new Object[] { hashMap }) : new Expression(font, clazz, "new", new Object[] { Font.getFont(hashMap) }));
    }
  }
  
  static final class java_awt_GridBagLayout_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> param1Class, Object param1Object1, Object param1Object2, Encoder param1Encoder) {
      super.initialize(param1Class, param1Object1, param1Object2, param1Encoder);
      if (getHashtable(param1Object2).isEmpty())
        for (Map.Entry entry : getHashtable(param1Object1).entrySet()) {
          Object[] arrayOfObject = { entry.getKey(), entry.getValue() };
          invokeStatement(param1Object1, "addLayoutComponent", arrayOfObject, param1Encoder);
        }  
    }
    
    protected boolean mutatesTo(Object param1Object1, Object param1Object2) { return (super.mutatesTo(param1Object1, param1Object2) && getHashtable(param1Object2).isEmpty()); }
    
    private static Hashtable<?, ?> getHashtable(Object param1Object) { return (Hashtable)MetaData.getPrivateFieldValue(param1Object, "java.awt.GridBagLayout.comptable"); }
  }
  
  static final class java_awt_Insets_PersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object param1Object1, Object param1Object2) { return param1Object1.equals(param1Object2); }
    
    protected Expression instantiate(Object param1Object, Encoder param1Encoder) {
      Insets insets = (Insets)param1Object;
      Object[] arrayOfObject = { Integer.valueOf(insets.top), Integer.valueOf(insets.left), Integer.valueOf(insets.bottom), Integer.valueOf(insets.right) };
      return new Expression(insets, insets.getClass(), "new", arrayOfObject);
    }
  }
  
  static final class java_awt_List_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> param1Class, Object param1Object1, Object param1Object2, Encoder param1Encoder) {
      super.initialize(param1Class, param1Object1, param1Object2, param1Encoder);
      List list1 = (List)param1Object1;
      List list2 = (List)param1Object2;
      for (int i = list2.getItemCount(); i < list1.getItemCount(); i++) {
        invokeStatement(param1Object1, "add", new Object[] { list1.getItem(i) }, param1Encoder);
      } 
    }
  }
  
  static final class java_awt_MenuBar_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> param1Class, Object param1Object1, Object param1Object2, Encoder param1Encoder) {
      super.initialize(param1Class, param1Object1, param1Object2, param1Encoder);
      MenuBar menuBar1 = (MenuBar)param1Object1;
      MenuBar menuBar2 = (MenuBar)param1Object2;
      for (int i = menuBar2.getMenuCount(); i < menuBar1.getMenuCount(); i++) {
        invokeStatement(param1Object1, "add", new Object[] { menuBar1.getMenu(i) }, param1Encoder);
      } 
    }
  }
  
  static final class java_awt_MenuShortcut_PersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object param1Object1, Object param1Object2) { return param1Object1.equals(param1Object2); }
    
    protected Expression instantiate(Object param1Object, Encoder param1Encoder) {
      MenuShortcut menuShortcut = (MenuShortcut)param1Object;
      return new Expression(param1Object, menuShortcut.getClass(), "new", new Object[] { new Integer(menuShortcut.getKey()), Boolean.valueOf(menuShortcut.usesShiftModifier()) });
    }
  }
  
  static final class java_awt_Menu_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> param1Class, Object param1Object1, Object param1Object2, Encoder param1Encoder) {
      super.initialize(param1Class, param1Object1, param1Object2, param1Encoder);
      Menu menu1 = (Menu)param1Object1;
      Menu menu2 = (Menu)param1Object2;
      for (int i = menu2.getItemCount(); i < menu1.getItemCount(); i++) {
        invokeStatement(param1Object1, "add", new Object[] { menu1.getItem(i) }, param1Encoder);
      } 
    }
  }
  
  static final class java_awt_SystemColor_PersistenceDelegate extends StaticFieldsPersistenceDelegate {}
  
  static final class java_awt_font_TextAttribute_PersistenceDelegate extends StaticFieldsPersistenceDelegate {}
  
  static final class java_beans_beancontext_BeanContextSupport_PersistenceDelegate extends java_util_Collection_PersistenceDelegate {}
  
  static final class java_lang_Class_PersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object param1Object1, Object param1Object2) { return param1Object1.equals(param1Object2); }
    
    protected Expression instantiate(Object param1Object, Encoder param1Encoder) {
      Class clazz = (Class)param1Object;
      if (clazz.isPrimitive()) {
        Field field = null;
        try {
          field = PrimitiveWrapperMap.getType(clazz.getName()).getDeclaredField("TYPE");
        } catch (NoSuchFieldException noSuchFieldException) {
          System.err.println("Unknown primitive type: " + clazz);
        } 
        return new Expression(param1Object, field, "get", new Object[] { null });
      } 
      if (param1Object == String.class)
        return new Expression(param1Object, "", "getClass", new Object[0]); 
      if (param1Object == Class.class)
        return new Expression(param1Object, String.class, "getClass", new Object[0]); 
      Expression expression = new Expression(param1Object, Class.class, "forName", new Object[] { clazz.getName() });
      expression.loader = clazz.getClassLoader();
      return expression;
    }
  }
  
  static final class java_lang_String_PersistenceDelegate extends PersistenceDelegate {
    protected Expression instantiate(Object param1Object, Encoder param1Encoder) { return null; }
    
    public void writeObject(Object param1Object, Encoder param1Encoder) {}
  }
  
  static final class java_lang_reflect_Field_PersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object param1Object1, Object param1Object2) { return param1Object1.equals(param1Object2); }
    
    protected Expression instantiate(Object param1Object, Encoder param1Encoder) {
      Field field = (Field)param1Object;
      return new Expression(param1Object, field.getDeclaringClass(), "getField", new Object[] { field.getName() });
    }
  }
  
  static final class java_lang_reflect_Method_PersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object param1Object1, Object param1Object2) { return param1Object1.equals(param1Object2); }
    
    protected Expression instantiate(Object param1Object, Encoder param1Encoder) {
      Method method = (Method)param1Object;
      return new Expression(param1Object, method.getDeclaringClass(), "getMethod", new Object[] { method.getName(), method.getParameterTypes() });
    }
  }
  
  static final class java_sql_Timestamp_PersistenceDelegate extends java_util_Date_PersistenceDelegate {
    private static final Method getNanosMethod = getNanosMethod();
    
    private static Method getNanosMethod() {
      try {
        Class clazz = Class.forName("java.sql.Timestamp", true, null);
        return clazz.getMethod("getNanos", new Class[0]);
      } catch (ClassNotFoundException classNotFoundException) {
        return null;
      } catch (NoSuchMethodException noSuchMethodException) {
        throw new AssertionError(noSuchMethodException);
      } 
    }
    
    private static int getNanos(Object param1Object) {
      if (getNanosMethod == null)
        throw new AssertionError("Should not get here"); 
      try {
        return ((Integer)getNanosMethod.invoke(param1Object, new Object[0])).intValue();
      } catch (InvocationTargetException invocationTargetException) {
        Throwable throwable = invocationTargetException.getCause();
        if (throwable instanceof RuntimeException)
          throw (RuntimeException)throwable; 
        if (throwable instanceof Error)
          throw (Error)throwable; 
        throw new AssertionError(invocationTargetException);
      } catch (IllegalAccessException illegalAccessException) {
        throw new AssertionError(illegalAccessException);
      } 
    }
    
    protected void initialize(Class<?> param1Class, Object param1Object1, Object param1Object2, Encoder param1Encoder) {
      int i = getNanos(param1Object1);
      if (i != getNanos(param1Object2))
        param1Encoder.writeStatement(new Statement(param1Object1, "setNanos", new Object[] { Integer.valueOf(i) })); 
    }
  }
  
  static final class java_util_AbstractCollection_PersistenceDelegate extends java_util_Collection_PersistenceDelegate {}
  
  static final class java_util_AbstractList_PersistenceDelegate extends java_util_List_PersistenceDelegate {}
  
  static final class java_util_AbstractMap_PersistenceDelegate extends java_util_Map_PersistenceDelegate {}
  
  static class java_util_Collection_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> param1Class, Object param1Object1, Object param1Object2, Encoder param1Encoder) {
      Collection collection1 = (Collection)param1Object1;
      Collection collection2 = (Collection)param1Object2;
      if (collection2.size() != 0)
        invokeStatement(param1Object1, "clear", new Object[0], param1Encoder); 
      Iterator iterator = collection1.iterator();
      while (iterator.hasNext()) {
        invokeStatement(param1Object1, "add", new Object[] { iterator.next() }, param1Encoder);
      } 
    }
  }
  
  private static abstract class java_util_Collections extends PersistenceDelegate {
    private java_util_Collections() {}
    
    protected boolean mutatesTo(Object param1Object1, Object param1Object2) {
      if (!super.mutatesTo(param1Object1, param1Object2))
        return false; 
      if (param1Object1 instanceof List || param1Object1 instanceof Set || param1Object1 instanceof Map)
        return param1Object1.equals(param1Object2); 
      Collection collection1 = (Collection)param1Object1;
      Collection collection2 = (Collection)param1Object2;
      return (collection1.size() == collection2.size() && collection1.containsAll(collection2));
    }
    
    protected void initialize(Class<?> param1Class, Object param1Object1, Object param1Object2, Encoder param1Encoder) {}
    
    static final class CheckedCollection_PersistenceDelegate extends java_util_Collections {
      CheckedCollection_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) {
        Object object = MetaData.getPrivateFieldValue(param2Object, "java.util.Collections$CheckedCollection.type");
        ArrayList arrayList = new ArrayList((Collection)param2Object);
        return new Expression(param2Object, Collections.class, "checkedCollection", new Object[] { arrayList, object });
      }
    }
    
    static final class CheckedList_PersistenceDelegate extends java_util_Collections {
      CheckedList_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) {
        Object object = MetaData.getPrivateFieldValue(param2Object, "java.util.Collections$CheckedCollection.type");
        LinkedList linkedList = new LinkedList((Collection)param2Object);
        return new Expression(param2Object, Collections.class, "checkedList", new Object[] { linkedList, object });
      }
    }
    
    static final class CheckedMap_PersistenceDelegate extends java_util_Collections {
      CheckedMap_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) {
        Object object1 = MetaData.getPrivateFieldValue(param2Object, "java.util.Collections$CheckedMap.keyType");
        Object object2 = MetaData.getPrivateFieldValue(param2Object, "java.util.Collections$CheckedMap.valueType");
        HashMap hashMap = new HashMap((Map)param2Object);
        return new Expression(param2Object, Collections.class, "checkedMap", new Object[] { hashMap, object1, object2 });
      }
    }
    
    static final class CheckedRandomAccessList_PersistenceDelegate extends java_util_Collections {
      CheckedRandomAccessList_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) {
        Object object = MetaData.getPrivateFieldValue(param2Object, "java.util.Collections$CheckedCollection.type");
        ArrayList arrayList = new ArrayList((Collection)param2Object);
        return new Expression(param2Object, Collections.class, "checkedList", new Object[] { arrayList, object });
      }
    }
    
    static final class CheckedSet_PersistenceDelegate extends java_util_Collections {
      CheckedSet_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) {
        Object object = MetaData.getPrivateFieldValue(param2Object, "java.util.Collections$CheckedCollection.type");
        HashSet hashSet = new HashSet((Set)param2Object);
        return new Expression(param2Object, Collections.class, "checkedSet", new Object[] { hashSet, object });
      }
    }
    
    static final class CheckedSortedMap_PersistenceDelegate extends java_util_Collections {
      CheckedSortedMap_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) {
        Object object1 = MetaData.getPrivateFieldValue(param2Object, "java.util.Collections$CheckedMap.keyType");
        Object object2 = MetaData.getPrivateFieldValue(param2Object, "java.util.Collections$CheckedMap.valueType");
        TreeMap treeMap = new TreeMap((SortedMap)param2Object);
        return new Expression(param2Object, Collections.class, "checkedSortedMap", new Object[] { treeMap, object1, object2 });
      }
    }
    
    static final class CheckedSortedSet_PersistenceDelegate extends java_util_Collections {
      CheckedSortedSet_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) {
        Object object = MetaData.getPrivateFieldValue(param2Object, "java.util.Collections$CheckedCollection.type");
        TreeSet treeSet = new TreeSet((SortedSet)param2Object);
        return new Expression(param2Object, Collections.class, "checkedSortedSet", new Object[] { treeSet, object });
      }
    }
    
    static final class EmptyList_PersistenceDelegate extends java_util_Collections {
      EmptyList_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) { return new Expression(param2Object, Collections.class, "emptyList", null); }
    }
    
    static final class EmptyMap_PersistenceDelegate extends java_util_Collections {
      EmptyMap_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) { return new Expression(param2Object, Collections.class, "emptyMap", null); }
    }
    
    static final class EmptySet_PersistenceDelegate extends java_util_Collections {
      EmptySet_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) { return new Expression(param2Object, Collections.class, "emptySet", null); }
    }
    
    static final class SingletonList_PersistenceDelegate extends java_util_Collections {
      SingletonList_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) {
        List list = (List)param2Object;
        return new Expression(param2Object, Collections.class, "singletonList", new Object[] { list.get(0) });
      }
    }
    
    static final class SingletonMap_PersistenceDelegate extends java_util_Collections {
      SingletonMap_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) {
        Map map = (Map)param2Object;
        Object object = map.keySet().iterator().next();
        return new Expression(param2Object, Collections.class, "singletonMap", new Object[] { object, map.get(object) });
      }
    }
    
    static final class SingletonSet_PersistenceDelegate extends java_util_Collections {
      SingletonSet_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) {
        Set set = (Set)param2Object;
        return new Expression(param2Object, Collections.class, "singleton", new Object[] { set.iterator().next() });
      }
    }
    
    static final class SynchronizedCollection_PersistenceDelegate extends java_util_Collections {
      SynchronizedCollection_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) {
        ArrayList arrayList = new ArrayList((Collection)param2Object);
        return new Expression(param2Object, Collections.class, "synchronizedCollection", new Object[] { arrayList });
      }
    }
    
    static final class SynchronizedList_PersistenceDelegate extends java_util_Collections {
      SynchronizedList_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) {
        LinkedList linkedList = new LinkedList((Collection)param2Object);
        return new Expression(param2Object, Collections.class, "synchronizedList", new Object[] { linkedList });
      }
    }
    
    static final class SynchronizedMap_PersistenceDelegate extends java_util_Collections {
      SynchronizedMap_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) {
        HashMap hashMap = new HashMap((Map)param2Object);
        return new Expression(param2Object, Collections.class, "synchronizedMap", new Object[] { hashMap });
      }
    }
    
    static final class SynchronizedRandomAccessList_PersistenceDelegate extends java_util_Collections {
      SynchronizedRandomAccessList_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) {
        ArrayList arrayList = new ArrayList((Collection)param2Object);
        return new Expression(param2Object, Collections.class, "synchronizedList", new Object[] { arrayList });
      }
    }
    
    static final class SynchronizedSet_PersistenceDelegate extends java_util_Collections {
      SynchronizedSet_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) {
        HashSet hashSet = new HashSet((Set)param2Object);
        return new Expression(param2Object, Collections.class, "synchronizedSet", new Object[] { hashSet });
      }
    }
    
    static final class SynchronizedSortedMap_PersistenceDelegate extends java_util_Collections {
      SynchronizedSortedMap_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) {
        TreeMap treeMap = new TreeMap((SortedMap)param2Object);
        return new Expression(param2Object, Collections.class, "synchronizedSortedMap", new Object[] { treeMap });
      }
    }
    
    static final class SynchronizedSortedSet_PersistenceDelegate extends java_util_Collections {
      SynchronizedSortedSet_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) {
        TreeSet treeSet = new TreeSet((SortedSet)param2Object);
        return new Expression(param2Object, Collections.class, "synchronizedSortedSet", new Object[] { treeSet });
      }
    }
    
    static final class UnmodifiableCollection_PersistenceDelegate extends java_util_Collections {
      UnmodifiableCollection_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) {
        ArrayList arrayList = new ArrayList((Collection)param2Object);
        return new Expression(param2Object, Collections.class, "unmodifiableCollection", new Object[] { arrayList });
      }
    }
    
    static final class UnmodifiableList_PersistenceDelegate extends java_util_Collections {
      UnmodifiableList_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) {
        LinkedList linkedList = new LinkedList((Collection)param2Object);
        return new Expression(param2Object, Collections.class, "unmodifiableList", new Object[] { linkedList });
      }
    }
    
    static final class UnmodifiableMap_PersistenceDelegate extends java_util_Collections {
      UnmodifiableMap_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) {
        HashMap hashMap = new HashMap((Map)param2Object);
        return new Expression(param2Object, Collections.class, "unmodifiableMap", new Object[] { hashMap });
      }
    }
    
    static final class UnmodifiableRandomAccessList_PersistenceDelegate extends java_util_Collections {
      UnmodifiableRandomAccessList_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) {
        ArrayList arrayList = new ArrayList((Collection)param2Object);
        return new Expression(param2Object, Collections.class, "unmodifiableList", new Object[] { arrayList });
      }
    }
    
    static final class UnmodifiableSet_PersistenceDelegate extends java_util_Collections {
      UnmodifiableSet_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) {
        HashSet hashSet = new HashSet((Set)param2Object);
        return new Expression(param2Object, Collections.class, "unmodifiableSet", new Object[] { hashSet });
      }
    }
    
    static final class UnmodifiableSortedMap_PersistenceDelegate extends java_util_Collections {
      UnmodifiableSortedMap_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) {
        TreeMap treeMap = new TreeMap((SortedMap)param2Object);
        return new Expression(param2Object, Collections.class, "unmodifiableSortedMap", new Object[] { treeMap });
      }
    }
    
    static final class UnmodifiableSortedSet_PersistenceDelegate extends java_util_Collections {
      UnmodifiableSortedSet_PersistenceDelegate() { super(null); }
      
      protected Expression instantiate(Object param2Object, Encoder param2Encoder) {
        TreeSet treeSet = new TreeSet((SortedSet)param2Object);
        return new Expression(param2Object, Collections.class, "unmodifiableSortedSet", new Object[] { treeSet });
      }
    }
  }
  
  static class java_util_Date_PersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object param1Object1, Object param1Object2) {
      if (!super.mutatesTo(param1Object1, param1Object2))
        return false; 
      Date date1 = (Date)param1Object1;
      Date date2 = (Date)param1Object2;
      return (date1.getTime() == date2.getTime());
    }
    
    protected Expression instantiate(Object param1Object, Encoder param1Encoder) {
      Date date = (Date)param1Object;
      return new Expression(date, date.getClass(), "new", new Object[] { Long.valueOf(date.getTime()) });
    }
  }
  
  static final class java_util_EnumMap_PersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object param1Object1, Object param1Object2) { return (super.mutatesTo(param1Object1, param1Object2) && getType(param1Object1) == getType(param1Object2)); }
    
    protected Expression instantiate(Object param1Object, Encoder param1Encoder) { return new Expression(param1Object, java.util.EnumMap.class, "new", new Object[] { getType(param1Object) }); }
    
    private static Object getType(Object param1Object) { return MetaData.getPrivateFieldValue(param1Object, "java.util.EnumMap.keyType"); }
  }
  
  static final class java_util_EnumSet_PersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object param1Object1, Object param1Object2) { return (super.mutatesTo(param1Object1, param1Object2) && getType(param1Object1) == getType(param1Object2)); }
    
    protected Expression instantiate(Object param1Object, Encoder param1Encoder) { return new Expression(param1Object, java.util.EnumSet.class, "noneOf", new Object[] { getType(param1Object) }); }
    
    private static Object getType(Object param1Object) { return MetaData.getPrivateFieldValue(param1Object, "java.util.EnumSet.elementType"); }
  }
  
  static final class java_util_Hashtable_PersistenceDelegate extends java_util_Map_PersistenceDelegate {}
  
  static class java_util_List_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> param1Class, Object param1Object1, Object param1Object2, Encoder param1Encoder) {
      List list1 = (List)param1Object1;
      List list2 = (List)param1Object2;
      int i = list1.size();
      byte b1 = (list2 == null) ? 0 : list2.size();
      if (i < b1) {
        invokeStatement(param1Object1, "clear", new Object[0], param1Encoder);
        b1 = 0;
      } 
      byte b2;
      for (b2 = 0; b2 < b1; b2++) {
        Integer integer = new Integer(b2);
        Expression expression1 = new Expression(param1Object1, "get", new Object[] { integer });
        Expression expression2 = new Expression(param1Object2, "get", new Object[] { integer });
        try {
          Object object1 = expression1.getValue();
          Object object2 = expression2.getValue();
          param1Encoder.writeExpression(expression1);
          if (!Objects.equals(object2, param1Encoder.get(object1)))
            invokeStatement(param1Object1, "set", new Object[] { integer, object1 }, param1Encoder); 
        } catch (Exception exception) {
          param1Encoder.getExceptionListener().exceptionThrown(exception);
        } 
      } 
      for (b2 = b1; b2 < i; b2++) {
        invokeStatement(param1Object1, "add", new Object[] { list1.get(b2) }, param1Encoder);
      } 
    }
  }
  
  static class java_util_Map_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> param1Class, Object param1Object1, Object param1Object2, Encoder param1Encoder) {
      Map map1 = (Map)param1Object1;
      Map map2 = (Map)param1Object2;
      if (map2 != null)
        for (Object object : map2.keySet().toArray()) {
          if (!map1.containsKey(object))
            invokeStatement(param1Object1, "remove", new Object[] { object }, param1Encoder); 
        }  
      for (Object object : map1.keySet()) {
        Expression expression1 = new Expression(param1Object1, "get", new Object[] { object });
        Expression expression2 = new Expression(param1Object2, "get", new Object[] { object });
        try {
          Object object1 = expression1.getValue();
          Object object2 = expression2.getValue();
          param1Encoder.writeExpression(expression1);
          if (!Objects.equals(object2, param1Encoder.get(object1))) {
            invokeStatement(param1Object1, "put", new Object[] { object, object1 }, param1Encoder);
            continue;
          } 
          if (object2 == null && !map2.containsKey(object))
            invokeStatement(param1Object1, "put", new Object[] { object, object1 }, param1Encoder); 
        } catch (Exception exception) {
          param1Encoder.getExceptionListener().exceptionThrown(exception);
        } 
      } 
    }
  }
  
  static final class javax_swing_Box_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected boolean mutatesTo(Object param1Object1, Object param1Object2) { return (super.mutatesTo(param1Object1, param1Object2) && getAxis(param1Object1).equals(getAxis(param1Object2))); }
    
    protected Expression instantiate(Object param1Object, Encoder param1Encoder) { return new Expression(param1Object, param1Object.getClass(), "new", new Object[] { getAxis(param1Object) }); }
    
    private Integer getAxis(Object param1Object) {
      Box box = (Box)param1Object;
      return (Integer)MetaData.getPrivateFieldValue(box.getLayout(), "javax.swing.BoxLayout.axis");
    }
  }
  
  static final class javax_swing_DefaultComboBoxModel_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> param1Class, Object param1Object1, Object param1Object2, Encoder param1Encoder) {
      super.initialize(param1Class, param1Object1, param1Object2, param1Encoder);
      DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel)param1Object1;
      for (byte b = 0; b < defaultComboBoxModel.getSize(); b++) {
        invokeStatement(param1Object1, "addElement", new Object[] { defaultComboBoxModel.getElementAt(b) }, param1Encoder);
      } 
    }
  }
  
  static final class javax_swing_DefaultListModel_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> param1Class, Object param1Object1, Object param1Object2, Encoder param1Encoder) {
      super.initialize(param1Class, param1Object1, param1Object2, param1Encoder);
      DefaultListModel defaultListModel1 = (DefaultListModel)param1Object1;
      DefaultListModel defaultListModel2 = (DefaultListModel)param1Object2;
      for (int i = defaultListModel2.getSize(); i < defaultListModel1.getSize(); i++) {
        invokeStatement(param1Object1, "add", new Object[] { defaultListModel1.getElementAt(i) }, param1Encoder);
      } 
    }
  }
  
  static final class javax_swing_JFrame_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> param1Class, Object param1Object1, Object param1Object2, Encoder param1Encoder) {
      super.initialize(param1Class, param1Object1, param1Object2, param1Encoder);
      Window window1 = (Window)param1Object1;
      Window window2 = (Window)param1Object2;
      boolean bool1 = window1.isVisible();
      boolean bool2 = window2.isVisible();
      if (bool2 != bool1) {
        boolean bool = param1Encoder.executeStatements;
        param1Encoder.executeStatements = false;
        invokeStatement(param1Object1, "setVisible", new Object[] { Boolean.valueOf(bool1) }, param1Encoder);
        param1Encoder.executeStatements = bool;
      } 
    }
  }
  
  static final class javax_swing_JMenu_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> param1Class, Object param1Object1, Object param1Object2, Encoder param1Encoder) {
      super.initialize(param1Class, param1Object1, param1Object2, param1Encoder);
      JMenu jMenu = (JMenu)param1Object1;
      Component[] arrayOfComponent = jMenu.getMenuComponents();
      for (byte b = 0; b < arrayOfComponent.length; b++) {
        invokeStatement(param1Object1, "add", new Object[] { arrayOfComponent[b] }, param1Encoder);
      } 
    }
  }
  
  static final class javax_swing_JTabbedPane_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> param1Class, Object param1Object1, Object param1Object2, Encoder param1Encoder) {
      super.initialize(param1Class, param1Object1, param1Object2, param1Encoder);
      JTabbedPane jTabbedPane = (JTabbedPane)param1Object1;
      for (byte b = 0; b < jTabbedPane.getTabCount(); b++) {
        invokeStatement(param1Object1, "addTab", new Object[] { jTabbedPane.getTitleAt(b), jTabbedPane.getIconAt(b), jTabbedPane.getComponentAt(b) }, param1Encoder);
      } 
    }
  }
  
  static final class javax_swing_ToolTipManager_PersistenceDelegate extends PersistenceDelegate {
    protected Expression instantiate(Object param1Object, Encoder param1Encoder) { return new Expression(param1Object, javax.swing.ToolTipManager.class, "sharedInstance", new Object[0]); }
  }
  
  static final class javax_swing_border_MatteBorder_PersistenceDelegate extends PersistenceDelegate {
    protected Expression instantiate(Object param1Object, Encoder param1Encoder) {
      MatteBorder matteBorder = (MatteBorder)param1Object;
      Insets insets = matteBorder.getBorderInsets();
      Color color = matteBorder.getTileIcon();
      if (color == null)
        color = matteBorder.getMatteColor(); 
      Object[] arrayOfObject = { Integer.valueOf(insets.top), Integer.valueOf(insets.left), Integer.valueOf(insets.bottom), Integer.valueOf(insets.right), color };
      return new Expression(matteBorder, matteBorder.getClass(), "new", arrayOfObject);
    }
  }
  
  static final class javax_swing_tree_DefaultMutableTreeNode_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> param1Class, Object param1Object1, Object param1Object2, Encoder param1Encoder) {
      super.initialize(param1Class, param1Object1, param1Object2, param1Encoder);
      DefaultMutableTreeNode defaultMutableTreeNode1 = (DefaultMutableTreeNode)param1Object1;
      DefaultMutableTreeNode defaultMutableTreeNode2 = (DefaultMutableTreeNode)param1Object2;
      for (int i = defaultMutableTreeNode2.getChildCount(); i < defaultMutableTreeNode1.getChildCount(); i++) {
        invokeStatement(param1Object1, "add", new Object[] { defaultMutableTreeNode1.getChildAt(i) }, param1Encoder);
      } 
    }
  }
  
  static final class sun_swing_PrintColorUIResource_PersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object param1Object1, Object param1Object2) { return param1Object1.equals(param1Object2); }
    
    protected Expression instantiate(Object param1Object, Encoder param1Encoder) {
      Color color = (Color)param1Object;
      Object[] arrayOfObject = { Integer.valueOf(color.getRGB()) };
      return new Expression(color, javax.swing.plaf.ColorUIResource.class, "new", arrayOfObject);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\MetaData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */