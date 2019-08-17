package javax.swing.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.WeakHashMap;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import sun.font.FontUtilities;

public class StyleContext implements Serializable, AbstractDocument.AttributeContext {
  private static StyleContext defaultContext;
  
  public static final String DEFAULT_STYLE = "default";
  
  private static Hashtable<Object, String> freezeKeyMap;
  
  private static Hashtable<String, Object> thawKeyMap;
  
  private Style styles = new NamedStyle(null);
  
  private FontKey fontSearch = new FontKey(null, 0, 0);
  
  private Hashtable<FontKey, Font> fontTable = new Hashtable();
  
  private Map<SmallAttributeSet, WeakReference<SmallAttributeSet>> attributesPool = Collections.synchronizedMap(new WeakHashMap());
  
  private MutableAttributeSet search = new SimpleAttributeSet();
  
  private int unusedSets;
  
  static final int THRESHOLD = 9;
  
  public static final StyleContext getDefaultStyleContext() {
    if (defaultContext == null)
      defaultContext = new StyleContext(); 
    return defaultContext;
  }
  
  public StyleContext() { addStyle("default", null); }
  
  public Style addStyle(String paramString, Style paramStyle) {
    NamedStyle namedStyle = new NamedStyle(paramString, paramStyle);
    if (paramString != null)
      this.styles.addAttribute(paramString, namedStyle); 
    return namedStyle;
  }
  
  public void removeStyle(String paramString) { this.styles.removeAttribute(paramString); }
  
  public Style getStyle(String paramString) { return (Style)this.styles.getAttribute(paramString); }
  
  public Enumeration<?> getStyleNames() { return this.styles.getAttributeNames(); }
  
  public void addChangeListener(ChangeListener paramChangeListener) { this.styles.addChangeListener(paramChangeListener); }
  
  public void removeChangeListener(ChangeListener paramChangeListener) { this.styles.removeChangeListener(paramChangeListener); }
  
  public ChangeListener[] getChangeListeners() { return ((NamedStyle)this.styles).getChangeListeners(); }
  
  public Font getFont(AttributeSet paramAttributeSet) {
    byte b = 0;
    if (StyleConstants.isBold(paramAttributeSet))
      b |= true; 
    if (StyleConstants.isItalic(paramAttributeSet))
      b |= 0x2; 
    String str = StyleConstants.getFontFamily(paramAttributeSet);
    int i = StyleConstants.getFontSize(paramAttributeSet);
    if (StyleConstants.isSuperscript(paramAttributeSet) || StyleConstants.isSubscript(paramAttributeSet))
      i -= 2; 
    return getFont(str, b, i);
  }
  
  public Color getForeground(AttributeSet paramAttributeSet) { return StyleConstants.getForeground(paramAttributeSet); }
  
  public Color getBackground(AttributeSet paramAttributeSet) { return StyleConstants.getBackground(paramAttributeSet); }
  
  public Font getFont(String paramString, int paramInt1, int paramInt2) {
    this.fontSearch.setValue(paramString, paramInt1, paramInt2);
    Font font = (Font)this.fontTable.get(this.fontSearch);
    if (font == null) {
      Style style = getStyle("default");
      if (style != null) {
        Font font1 = (Font)style.getAttribute("FONT_ATTRIBUTE_KEY");
        if (font1 != null && font1.getFamily().equalsIgnoreCase(paramString))
          font = font1.deriveFont(paramInt1, paramInt2); 
      } 
      if (font == null)
        font = new Font(paramString, paramInt1, paramInt2); 
      if (!FontUtilities.fontSupportsDefaultEncoding(font))
        font = FontUtilities.getCompositeFontUIResource(font); 
      FontKey fontKey = new FontKey(paramString, paramInt1, paramInt2);
      this.fontTable.put(fontKey, font);
    } 
    return font;
  }
  
  public FontMetrics getFontMetrics(Font paramFont) { return Toolkit.getDefaultToolkit().getFontMetrics(paramFont); }
  
  public AttributeSet addAttribute(AttributeSet paramAttributeSet, Object paramObject1, Object paramObject2) {
    if (paramAttributeSet.getAttributeCount() + 1 <= getCompressionThreshold()) {
      this.search.removeAttributes(this.search);
      this.search.addAttributes(paramAttributeSet);
      this.search.addAttribute(paramObject1, paramObject2);
      reclaim(paramAttributeSet);
      return getImmutableUniqueSet();
    } 
    MutableAttributeSet mutableAttributeSet = getMutableAttributeSet(paramAttributeSet);
    mutableAttributeSet.addAttribute(paramObject1, paramObject2);
    return mutableAttributeSet;
  }
  
  public AttributeSet addAttributes(AttributeSet paramAttributeSet1, AttributeSet paramAttributeSet2) {
    if (paramAttributeSet1.getAttributeCount() + paramAttributeSet2.getAttributeCount() <= getCompressionThreshold()) {
      this.search.removeAttributes(this.search);
      this.search.addAttributes(paramAttributeSet1);
      this.search.addAttributes(paramAttributeSet2);
      reclaim(paramAttributeSet1);
      return getImmutableUniqueSet();
    } 
    MutableAttributeSet mutableAttributeSet = getMutableAttributeSet(paramAttributeSet1);
    mutableAttributeSet.addAttributes(paramAttributeSet2);
    return mutableAttributeSet;
  }
  
  public AttributeSet removeAttribute(AttributeSet paramAttributeSet, Object paramObject) {
    if (paramAttributeSet.getAttributeCount() - 1 <= getCompressionThreshold()) {
      this.search.removeAttributes(this.search);
      this.search.addAttributes(paramAttributeSet);
      this.search.removeAttribute(paramObject);
      reclaim(paramAttributeSet);
      return getImmutableUniqueSet();
    } 
    MutableAttributeSet mutableAttributeSet = getMutableAttributeSet(paramAttributeSet);
    mutableAttributeSet.removeAttribute(paramObject);
    return mutableAttributeSet;
  }
  
  public AttributeSet removeAttributes(AttributeSet paramAttributeSet, Enumeration<?> paramEnumeration) {
    if (paramAttributeSet.getAttributeCount() <= getCompressionThreshold()) {
      this.search.removeAttributes(this.search);
      this.search.addAttributes(paramAttributeSet);
      this.search.removeAttributes(paramEnumeration);
      reclaim(paramAttributeSet);
      return getImmutableUniqueSet();
    } 
    MutableAttributeSet mutableAttributeSet = getMutableAttributeSet(paramAttributeSet);
    mutableAttributeSet.removeAttributes(paramEnumeration);
    return mutableAttributeSet;
  }
  
  public AttributeSet removeAttributes(AttributeSet paramAttributeSet1, AttributeSet paramAttributeSet2) {
    if (paramAttributeSet1.getAttributeCount() <= getCompressionThreshold()) {
      this.search.removeAttributes(this.search);
      this.search.addAttributes(paramAttributeSet1);
      this.search.removeAttributes(paramAttributeSet2);
      reclaim(paramAttributeSet1);
      return getImmutableUniqueSet();
    } 
    MutableAttributeSet mutableAttributeSet = getMutableAttributeSet(paramAttributeSet1);
    mutableAttributeSet.removeAttributes(paramAttributeSet2);
    return mutableAttributeSet;
  }
  
  public AttributeSet getEmptySet() { return SimpleAttributeSet.EMPTY; }
  
  public void reclaim(AttributeSet paramAttributeSet) {
    if (SwingUtilities.isEventDispatchThread())
      this.attributesPool.size(); 
  }
  
  protected int getCompressionThreshold() { return 9; }
  
  protected SmallAttributeSet createSmallAttributeSet(AttributeSet paramAttributeSet) { return new SmallAttributeSet(paramAttributeSet); }
  
  protected MutableAttributeSet createLargeAttributeSet(AttributeSet paramAttributeSet) { return new SimpleAttributeSet(paramAttributeSet); }
  
  void removeUnusedSets() { this.attributesPool.size(); }
  
  AttributeSet getImmutableUniqueSet() {
    SmallAttributeSet smallAttributeSet1 = createSmallAttributeSet(this.search);
    WeakReference weakReference = (WeakReference)this.attributesPool.get(smallAttributeSet1);
    SmallAttributeSet smallAttributeSet2;
    if (weakReference == null || (smallAttributeSet2 = (SmallAttributeSet)weakReference.get()) == null) {
      smallAttributeSet2 = smallAttributeSet1;
      this.attributesPool.put(smallAttributeSet2, new WeakReference(smallAttributeSet2));
    } 
    return smallAttributeSet2;
  }
  
  MutableAttributeSet getMutableAttributeSet(AttributeSet paramAttributeSet) { return (paramAttributeSet instanceof MutableAttributeSet && paramAttributeSet != SimpleAttributeSet.EMPTY) ? (MutableAttributeSet)paramAttributeSet : createLargeAttributeSet(paramAttributeSet); }
  
  public String toString() {
    removeUnusedSets();
    String str = "";
    for (SmallAttributeSet smallAttributeSet : this.attributesPool.keySet())
      str = str + smallAttributeSet + "\n"; 
    return str;
  }
  
  public void writeAttributes(ObjectOutputStream paramObjectOutputStream, AttributeSet paramAttributeSet) throws IOException { writeAttributeSet(paramObjectOutputStream, paramAttributeSet); }
  
  public void readAttributes(ObjectInputStream paramObjectInputStream, MutableAttributeSet paramMutableAttributeSet) throws ClassNotFoundException, IOException { readAttributeSet(paramObjectInputStream, paramMutableAttributeSet); }
  
  public static void writeAttributeSet(ObjectOutputStream paramObjectOutputStream, AttributeSet paramAttributeSet) throws IOException {
    int i = paramAttributeSet.getAttributeCount();
    paramObjectOutputStream.writeInt(i);
    Enumeration enumeration = paramAttributeSet.getAttributeNames();
    while (enumeration.hasMoreElements()) {
      Object object1 = enumeration.nextElement();
      if (object1 instanceof Serializable) {
        paramObjectOutputStream.writeObject(object1);
      } else {
        Object object = freezeKeyMap.get(object1);
        if (object == null)
          throw new NotSerializableException(object1.getClass().getName() + " is not serializable as a key in an AttributeSet"); 
        paramObjectOutputStream.writeObject(object);
      } 
      Object object2 = paramAttributeSet.getAttribute(object1);
      Object object3 = freezeKeyMap.get(object2);
      if (object2 instanceof Serializable) {
        paramObjectOutputStream.writeObject((object3 != null) ? object3 : object2);
        continue;
      } 
      if (object3 == null)
        throw new NotSerializableException(object2.getClass().getName() + " is not serializable as a value in an AttributeSet"); 
      paramObjectOutputStream.writeObject(object3);
    } 
  }
  
  public static void readAttributeSet(ObjectInputStream paramObjectInputStream, MutableAttributeSet paramMutableAttributeSet) throws ClassNotFoundException, IOException {
    int i = paramObjectInputStream.readInt();
    for (byte b = 0; b < i; b++) {
      Object object1 = paramObjectInputStream.readObject();
      Object object2 = paramObjectInputStream.readObject();
      if (thawKeyMap != null) {
        Object object3 = thawKeyMap.get(object1);
        if (object3 != null)
          object1 = object3; 
        Object object4 = thawKeyMap.get(object2);
        if (object4 != null)
          object2 = object4; 
      } 
      paramMutableAttributeSet.addAttribute(object1, object2);
    } 
  }
  
  public static void registerStaticAttributeKey(Object paramObject) {
    String str = paramObject.getClass().getName() + "." + paramObject.toString();
    if (freezeKeyMap == null) {
      freezeKeyMap = new Hashtable();
      thawKeyMap = new Hashtable();
    } 
    freezeKeyMap.put(paramObject, str);
    thawKeyMap.put(str, paramObject);
  }
  
  public static Object getStaticAttribute(Object paramObject) { return (thawKeyMap == null || paramObject == null) ? null : thawKeyMap.get(paramObject); }
  
  public static Object getStaticAttributeKey(Object paramObject) { return paramObject.getClass().getName() + "." + paramObject.toString(); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    removeUnusedSets();
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    this.fontSearch = new FontKey(null, 0, 0);
    this.fontTable = new Hashtable();
    this.search = new SimpleAttributeSet();
    this.attributesPool = Collections.synchronizedMap(new WeakHashMap());
    paramObjectInputStream.defaultReadObject();
  }
  
  static  {
    try {
      int i = StyleConstants.keys.length;
      for (byte b = 0; b < i; b++)
        registerStaticAttributeKey(StyleConstants.keys[b]); 
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    } 
  }
  
  static class FontKey {
    private String family;
    
    private int style;
    
    private int size;
    
    public FontKey(String param1String, int param1Int1, int param1Int2) { setValue(param1String, param1Int1, param1Int2); }
    
    public void setValue(String param1String, int param1Int1, int param1Int2) {
      this.family = (param1String != null) ? param1String.intern() : null;
      this.style = param1Int1;
      this.size = param1Int2;
    }
    
    public int hashCode() {
      int i = (this.family != null) ? this.family.hashCode() : 0;
      return i ^ this.style ^ this.size;
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object instanceof FontKey) {
        FontKey fontKey = (FontKey)param1Object;
        return (this.size == fontKey.size && this.style == fontKey.style && this.family == fontKey.family);
      } 
      return false;
    }
  }
  
  class KeyBuilder {
    private Vector<Object> keys = new Vector();
    
    private Vector<Object> data = new Vector();
    
    public void initialize(AttributeSet param1AttributeSet) {
      if (param1AttributeSet instanceof StyleContext.SmallAttributeSet) {
        initialize(((StyleContext.SmallAttributeSet)param1AttributeSet).attributes);
      } else {
        this.keys.removeAllElements();
        this.data.removeAllElements();
        Enumeration enumeration = param1AttributeSet.getAttributeNames();
        while (enumeration.hasMoreElements()) {
          Object object = enumeration.nextElement();
          addAttribute(object, param1AttributeSet.getAttribute(object));
        } 
      } 
    }
    
    private void initialize(Object[] param1ArrayOfObject) {
      this.keys.removeAllElements();
      this.data.removeAllElements();
      int i = param1ArrayOfObject.length;
      for (boolean bool = false; bool < i; bool += true) {
        this.keys.addElement(param1ArrayOfObject[bool]);
        this.data.addElement(param1ArrayOfObject[bool + true]);
      } 
    }
    
    public Object[] createTable() {
      int i = this.keys.size();
      Object[] arrayOfObject = new Object[2 * i];
      for (byte b = 0; b < i; b++) {
        byte b1 = 2 * b;
        arrayOfObject[b1] = this.keys.elementAt(b);
        arrayOfObject[b1 + 1] = this.data.elementAt(b);
      } 
      return arrayOfObject;
    }
    
    int getCount() { return this.keys.size(); }
    
    public void addAttribute(Object param1Object1, Object param1Object2) {
      this.keys.addElement(param1Object1);
      this.data.addElement(param1Object2);
    }
    
    public void addAttributes(AttributeSet param1AttributeSet) {
      if (param1AttributeSet instanceof StyleContext.SmallAttributeSet) {
        Object[] arrayOfObject = ((StyleContext.SmallAttributeSet)param1AttributeSet).attributes;
        int i = arrayOfObject.length;
        for (boolean bool = false; bool < i; bool += true)
          addAttribute(arrayOfObject[bool], arrayOfObject[bool + true]); 
      } else {
        Enumeration enumeration = param1AttributeSet.getAttributeNames();
        while (enumeration.hasMoreElements()) {
          Object object = enumeration.nextElement();
          addAttribute(object, param1AttributeSet.getAttribute(object));
        } 
      } 
    }
    
    public void removeAttribute(Object param1Object) {
      int i = this.keys.size();
      for (byte b = 0; b < i; b++) {
        if (this.keys.elementAt(b).equals(param1Object)) {
          this.keys.removeElementAt(b);
          this.data.removeElementAt(b);
          return;
        } 
      } 
    }
    
    public void removeAttributes(Enumeration param1Enumeration) {
      while (param1Enumeration.hasMoreElements()) {
        Object object = param1Enumeration.nextElement();
        removeAttribute(object);
      } 
    }
    
    public void removeAttributes(AttributeSet param1AttributeSet) {
      Enumeration enumeration = param1AttributeSet.getAttributeNames();
      while (enumeration.hasMoreElements()) {
        Object object1 = enumeration.nextElement();
        Object object2 = param1AttributeSet.getAttribute(object1);
        removeSearchAttribute(object1, object2);
      } 
    }
    
    private void removeSearchAttribute(Object param1Object1, Object param1Object2) {
      int i = this.keys.size();
      for (byte b = 0; b < i; b++) {
        if (this.keys.elementAt(b).equals(param1Object1)) {
          if (this.data.elementAt(b).equals(param1Object2)) {
            this.keys.removeElementAt(b);
            this.data.removeElementAt(b);
          } 
          return;
        } 
      } 
    }
  }
  
  class KeyEnumeration extends Object implements Enumeration<Object> {
    Object[] attr;
    
    int i;
    
    KeyEnumeration(Object[] param1ArrayOfObject) {
      this.attr = param1ArrayOfObject;
      this.i = 0;
    }
    
    public boolean hasMoreElements() { return (this.i < this.attr.length); }
    
    public Object nextElement() {
      if (this.i < this.attr.length) {
        Object object = this.attr[this.i];
        this.i += 2;
        return object;
      } 
      throw new NoSuchElementException();
    }
  }
  
  public class NamedStyle implements Style, Serializable {
    protected EventListenerList listenerList = new EventListenerList();
    
    protected ChangeEvent changeEvent = null;
    
    private AttributeSet attributes;
    
    public NamedStyle(String param1String, Style param1Style) {
      this.attributes = this$0.getEmptySet();
      if (param1String != null)
        setName(param1String); 
      if (param1Style != null)
        setResolveParent(param1Style); 
    }
    
    public NamedStyle(Style param1Style) { this(null, param1Style); }
    
    public NamedStyle() { this.attributes = this$0.getEmptySet(); }
    
    public String toString() { return "NamedStyle:" + getName() + " " + this.attributes; }
    
    public String getName() { return isDefined(StyleConstants.NameAttribute) ? getAttribute(StyleConstants.NameAttribute).toString() : null; }
    
    public void setName(String param1String) {
      if (param1String != null)
        addAttribute(StyleConstants.NameAttribute, param1String); 
    }
    
    public void addChangeListener(ChangeListener param1ChangeListener) { this.listenerList.add(ChangeListener.class, param1ChangeListener); }
    
    public void removeChangeListener(ChangeListener param1ChangeListener) { this.listenerList.remove(ChangeListener.class, param1ChangeListener); }
    
    public ChangeListener[] getChangeListeners() { return (ChangeListener[])this.listenerList.getListeners(ChangeListener.class); }
    
    protected void fireStateChanged() {
      Object[] arrayOfObject = this.listenerList.getListenerList();
      for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
        if (arrayOfObject[i] == ChangeListener.class) {
          if (this.changeEvent == null)
            this.changeEvent = new ChangeEvent(this); 
          ((ChangeListener)arrayOfObject[i + 1]).stateChanged(this.changeEvent);
        } 
      } 
    }
    
    public <T extends java.util.EventListener> T[] getListeners(Class<T> param1Class) { return (T[])this.listenerList.getListeners(param1Class); }
    
    public int getAttributeCount() { return this.attributes.getAttributeCount(); }
    
    public boolean isDefined(Object param1Object) { return this.attributes.isDefined(param1Object); }
    
    public boolean isEqual(AttributeSet param1AttributeSet) { return this.attributes.isEqual(param1AttributeSet); }
    
    public AttributeSet copyAttributes() {
      NamedStyle namedStyle = new NamedStyle(StyleContext.this);
      namedStyle.attributes = this.attributes.copyAttributes();
      return namedStyle;
    }
    
    public Object getAttribute(Object param1Object) { return this.attributes.getAttribute(param1Object); }
    
    public Enumeration<?> getAttributeNames() { return this.attributes.getAttributeNames(); }
    
    public boolean containsAttribute(Object param1Object1, Object param1Object2) { return this.attributes.containsAttribute(param1Object1, param1Object2); }
    
    public boolean containsAttributes(AttributeSet param1AttributeSet) { return this.attributes.containsAttributes(param1AttributeSet); }
    
    public AttributeSet getResolveParent() { return this.attributes.getResolveParent(); }
    
    public void addAttribute(Object param1Object1, Object param1Object2) {
      StyleContext styleContext = StyleContext.this;
      this.attributes = styleContext.addAttribute(this.attributes, param1Object1, param1Object2);
      fireStateChanged();
    }
    
    public void addAttributes(AttributeSet param1AttributeSet) {
      StyleContext styleContext = StyleContext.this;
      this.attributes = styleContext.addAttributes(this.attributes, param1AttributeSet);
      fireStateChanged();
    }
    
    public void removeAttribute(Object param1Object) {
      StyleContext styleContext = StyleContext.this;
      this.attributes = styleContext.removeAttribute(this.attributes, param1Object);
      fireStateChanged();
    }
    
    public void removeAttributes(Enumeration<?> param1Enumeration) {
      StyleContext styleContext = StyleContext.this;
      this.attributes = styleContext.removeAttributes(this.attributes, param1Enumeration);
      fireStateChanged();
    }
    
    public void removeAttributes(AttributeSet param1AttributeSet) {
      StyleContext styleContext = StyleContext.this;
      if (param1AttributeSet == this) {
        this.attributes = styleContext.getEmptySet();
      } else {
        this.attributes = styleContext.removeAttributes(this.attributes, param1AttributeSet);
      } 
      fireStateChanged();
    }
    
    public void setResolveParent(AttributeSet param1AttributeSet) {
      if (param1AttributeSet != null) {
        addAttribute(StyleConstants.ResolveAttribute, param1AttributeSet);
      } else {
        removeAttribute(StyleConstants.ResolveAttribute);
      } 
    }
    
    private void writeObject(ObjectOutputStream param1ObjectOutputStream) throws IOException {
      param1ObjectOutputStream.defaultWriteObject();
      StyleContext.writeAttributeSet(param1ObjectOutputStream, this.attributes);
    }
    
    private void readObject(ObjectInputStream param1ObjectInputStream) throws ClassNotFoundException, IOException {
      param1ObjectInputStream.defaultReadObject();
      this.attributes = SimpleAttributeSet.EMPTY;
      StyleContext.readAttributeSet(param1ObjectInputStream, this);
    }
  }
  
  public class SmallAttributeSet implements AttributeSet {
    Object[] attributes;
    
    AttributeSet resolveParent;
    
    public SmallAttributeSet(Object[] param1ArrayOfObject) {
      this.attributes = param1ArrayOfObject;
      updateResolveParent();
    }
    
    public SmallAttributeSet(AttributeSet param1AttributeSet) {
      int i = param1AttributeSet.getAttributeCount();
      Object[] arrayOfObject = new Object[2 * i];
      Enumeration enumeration = param1AttributeSet.getAttributeNames();
      for (boolean bool = false; enumeration.hasMoreElements(); bool += true) {
        arrayOfObject[bool] = enumeration.nextElement();
        arrayOfObject[bool + true] = param1AttributeSet.getAttribute(arrayOfObject[bool]);
      } 
      this.attributes = arrayOfObject;
      updateResolveParent();
    }
    
    private void updateResolveParent() {
      this.resolveParent = null;
      Object[] arrayOfObject = this.attributes;
      for (boolean bool = false; bool < arrayOfObject.length; bool += true) {
        if (arrayOfObject[bool] == StyleConstants.ResolveAttribute) {
          this.resolveParent = (AttributeSet)arrayOfObject[bool + true];
          break;
        } 
      } 
    }
    
    Object getLocalAttribute(Object param1Object) {
      if (param1Object == StyleConstants.ResolveAttribute)
        return this.resolveParent; 
      Object[] arrayOfObject = this.attributes;
      for (boolean bool = false; bool < arrayOfObject.length; bool += true) {
        if (param1Object.equals(arrayOfObject[bool]))
          return arrayOfObject[bool + true]; 
      } 
      return null;
    }
    
    public String toString() {
      null = "{";
      Object[] arrayOfObject = this.attributes;
      for (boolean bool = false; bool < arrayOfObject.length; bool += true) {
        if (arrayOfObject[bool + true] instanceof AttributeSet) {
          null = null + arrayOfObject[bool] + "=AttributeSet,";
        } else {
          null = null + arrayOfObject[bool] + "=" + arrayOfObject[bool + true] + ",";
        } 
      } 
      return null + "}";
    }
    
    public int hashCode() {
      int i = 0;
      Object[] arrayOfObject = this.attributes;
      for (boolean bool = true; bool < arrayOfObject.length; bool += true)
        i ^= arrayOfObject[bool].hashCode(); 
      return i;
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object instanceof AttributeSet) {
        AttributeSet attributeSet = (AttributeSet)param1Object;
        return (getAttributeCount() == attributeSet.getAttributeCount() && containsAttributes(attributeSet));
      } 
      return false;
    }
    
    public Object clone() { return this; }
    
    public int getAttributeCount() { return this.attributes.length / 2; }
    
    public boolean isDefined(Object param1Object) {
      Object[] arrayOfObject = this.attributes;
      int i = arrayOfObject.length;
      for (boolean bool = false; bool < i; bool += true) {
        if (param1Object.equals(arrayOfObject[bool]))
          return true; 
      } 
      return false;
    }
    
    public boolean isEqual(AttributeSet param1AttributeSet) { return (param1AttributeSet instanceof SmallAttributeSet) ? ((param1AttributeSet == this)) : ((getAttributeCount() == param1AttributeSet.getAttributeCount() && containsAttributes(param1AttributeSet))); }
    
    public AttributeSet copyAttributes() { return this; }
    
    public Object getAttribute(Object param1Object) {
      Object object = getLocalAttribute(param1Object);
      if (object == null) {
        AttributeSet attributeSet = getResolveParent();
        if (attributeSet != null)
          object = attributeSet.getAttribute(param1Object); 
      } 
      return object;
    }
    
    public Enumeration<?> getAttributeNames() { return new StyleContext.KeyEnumeration(StyleContext.this, this.attributes); }
    
    public boolean containsAttribute(Object param1Object1, Object param1Object2) { return param1Object2.equals(getAttribute(param1Object1)); }
    
    public boolean containsAttributes(AttributeSet param1AttributeSet) {
      boolean bool = true;
      Enumeration enumeration = param1AttributeSet.getAttributeNames();
      while (bool && enumeration.hasMoreElements()) {
        Object object = enumeration.nextElement();
        bool = param1AttributeSet.getAttribute(object).equals(getAttribute(object));
      } 
      return bool;
    }
    
    public AttributeSet getResolveParent() { return this.resolveParent; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\StyleContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */