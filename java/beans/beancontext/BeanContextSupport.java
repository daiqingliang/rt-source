package java.beans.beancontext;

import java.beans.Beans;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.Visibility;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class BeanContextSupport extends BeanContextChildSupport implements BeanContext, Serializable, PropertyChangeListener, VetoableChangeListener {
  static final long serialVersionUID = -4879613978649577204L;
  
  protected HashMap children;
  
  private int serializable = 0;
  
  protected ArrayList bcmListeners;
  
  protected Locale locale;
  
  protected boolean okToUseGui;
  
  protected boolean designTime;
  
  private PropertyChangeListener childPCL;
  
  private VetoableChangeListener childVCL;
  
  private boolean serializing;
  
  public BeanContextSupport(BeanContext paramBeanContext, Locale paramLocale, boolean paramBoolean1, boolean paramBoolean2) {
    super(paramBeanContext);
    this.locale = (paramLocale != null) ? paramLocale : Locale.getDefault();
    this.designTime = paramBoolean1;
    this.okToUseGui = paramBoolean2;
    initialize();
  }
  
  public BeanContextSupport(BeanContext paramBeanContext, Locale paramLocale, boolean paramBoolean) { this(paramBeanContext, paramLocale, paramBoolean, true); }
  
  public BeanContextSupport(BeanContext paramBeanContext, Locale paramLocale) { this(paramBeanContext, paramLocale, false, true); }
  
  public BeanContextSupport(BeanContext paramBeanContext) { this(paramBeanContext, null, false, true); }
  
  public BeanContextSupport() { this(null, null, false, true); }
  
  public BeanContext getBeanContextPeer() { return (BeanContext)getBeanContextChildPeer(); }
  
  public Object instantiateChild(String paramString) throws IOException, ClassNotFoundException {
    BeanContext beanContext = getBeanContextPeer();
    return Beans.instantiate(beanContext.getClass().getClassLoader(), paramString, beanContext);
  }
  
  public int size() {
    synchronized (this.children) {
      return this.children.size();
    } 
  }
  
  public boolean isEmpty() {
    synchronized (this.children) {
      return this.children.isEmpty();
    } 
  }
  
  public boolean contains(Object paramObject) {
    synchronized (this.children) {
      return this.children.containsKey(paramObject);
    } 
  }
  
  public boolean containsKey(Object paramObject) {
    synchronized (this.children) {
      return this.children.containsKey(paramObject);
    } 
  }
  
  public Iterator iterator() {
    synchronized (this.children) {
      return new BCSIterator(this.children.keySet().iterator());
    } 
  }
  
  public Object[] toArray() {
    synchronized (this.children) {
      return this.children.keySet().toArray();
    } 
  }
  
  public Object[] toArray(Object[] paramArrayOfObject) {
    synchronized (this.children) {
      return this.children.keySet().toArray(paramArrayOfObject);
    } 
  }
  
  protected BCSChild createBCSChild(Object paramObject1, Object paramObject2) { return new BCSChild(paramObject1, paramObject2); }
  
  public boolean add(Object paramObject) {
    if (paramObject == null)
      throw new IllegalArgumentException(); 
    if (this.children.containsKey(paramObject))
      return false; 
    synchronized (BeanContext.globalHierarchyLock) {
      if (this.children.containsKey(paramObject))
        return false; 
      if (!validatePendingAdd(paramObject))
        throw new IllegalStateException(); 
      BeanContextChild beanContextChild1 = getChildBeanContextChild(paramObject);
      BeanContextChild beanContextChild2 = null;
      synchronized (paramObject) {
        if (paramObject instanceof BeanContextProxy) {
          beanContextChild2 = ((BeanContextProxy)paramObject).getBeanContextProxy();
          if (beanContextChild2 == null)
            throw new NullPointerException("BeanContextPeer.getBeanContextProxy()"); 
        } 
        BCSChild bCSChild1 = createBCSChild(paramObject, beanContextChild2);
        BCSChild bCSChild2 = null;
        synchronized (this.children) {
          this.children.put(paramObject, bCSChild1);
          if (beanContextChild2 != null)
            this.children.put(beanContextChild2, bCSChild2 = createBCSChild(beanContextChild2, paramObject)); 
        } 
        if (beanContextChild1 != null)
          synchronized (beanContextChild1) {
            try {
              beanContextChild1.setBeanContext(getBeanContextPeer());
            } catch (PropertyVetoException propertyVetoException) {
              synchronized (this.children) {
                this.children.remove(paramObject);
                if (beanContextChild2 != null)
                  this.children.remove(beanContextChild2); 
              } 
              throw new IllegalStateException();
            } 
            beanContextChild1.addPropertyChangeListener("beanContext", this.childPCL);
            beanContextChild1.addVetoableChangeListener("beanContext", this.childVCL);
          }  
        Visibility visibility = getChildVisibility(paramObject);
        if (visibility != null)
          if (this.okToUseGui) {
            visibility.okToUseGui();
          } else {
            visibility.dontUseGui();
          }  
        if (getChildSerializable(paramObject) != null)
          this.serializable++; 
        childJustAddedHook(paramObject, bCSChild1);
        if (beanContextChild2 != null) {
          visibility = getChildVisibility(beanContextChild2);
          if (visibility != null)
            if (this.okToUseGui) {
              visibility.okToUseGui();
            } else {
              visibility.dontUseGui();
            }  
          if (getChildSerializable(beanContextChild2) != null)
            this.serializable++; 
          childJustAddedHook(beanContextChild2, bCSChild2);
        } 
      } 
      new Object[1][0] = paramObject;
      new Object[2][0] = paramObject;
      new Object[2][1] = beanContextChild2;
      fireChildrenAdded(new BeanContextMembershipEvent(getBeanContextPeer(), (beanContextChild2 == null) ? new Object[1] : new Object[2]));
    } 
    return true;
  }
  
  public boolean remove(Object paramObject) { return remove(paramObject, true); }
  
  protected boolean remove(Object paramObject, boolean paramBoolean) {
    if (paramObject == null)
      throw new IllegalArgumentException(); 
    synchronized (BeanContext.globalHierarchyLock) {
      if (!containsKey(paramObject))
        return false; 
      if (!validatePendingRemove(paramObject))
        throw new IllegalStateException(); 
      BCSChild bCSChild1 = (BCSChild)this.children.get(paramObject);
      BCSChild bCSChild2 = null;
      Object object = null;
      synchronized (paramObject) {
        if (paramBoolean) {
          BeanContextChild beanContextChild = getChildBeanContextChild(paramObject);
          if (beanContextChild != null)
            synchronized (beanContextChild) {
              beanContextChild.removePropertyChangeListener("beanContext", this.childPCL);
              beanContextChild.removeVetoableChangeListener("beanContext", this.childVCL);
              try {
                beanContextChild.setBeanContext(null);
              } catch (PropertyVetoException propertyVetoException) {
                beanContextChild.addPropertyChangeListener("beanContext", this.childPCL);
                beanContextChild.addVetoableChangeListener("beanContext", this.childVCL);
                throw new IllegalStateException();
              } 
            }  
        } 
        synchronized (this.children) {
          this.children.remove(paramObject);
          if (bCSChild1.isProxyPeer()) {
            bCSChild2 = (BCSChild)this.children.get(object = bCSChild1.getProxyPeer());
            this.children.remove(object);
          } 
        } 
        if (getChildSerializable(paramObject) != null)
          this.serializable--; 
        childJustRemovedHook(paramObject, bCSChild1);
        if (object != null) {
          if (getChildSerializable(object) != null)
            this.serializable--; 
          childJustRemovedHook(object, bCSChild2);
        } 
      } 
      new Object[1][0] = paramObject;
      new Object[2][0] = paramObject;
      new Object[2][1] = object;
      fireChildrenRemoved(new BeanContextMembershipEvent(getBeanContextPeer(), (object == null) ? new Object[1] : new Object[2]));
    } 
    return true;
  }
  
  public boolean containsAll(Collection paramCollection) {
    synchronized (this.children) {
      Iterator iterator = paramCollection.iterator();
      while (iterator.hasNext()) {
        if (!contains(iterator.next()))
          return false; 
      } 
      return true;
    } 
  }
  
  public boolean addAll(Collection paramCollection) { throw new UnsupportedOperationException(); }
  
  public boolean removeAll(Collection paramCollection) { throw new UnsupportedOperationException(); }
  
  public boolean retainAll(Collection paramCollection) { throw new UnsupportedOperationException(); }
  
  public void clear() { throw new UnsupportedOperationException(); }
  
  public void addBeanContextMembershipListener(BeanContextMembershipListener paramBeanContextMembershipListener) {
    if (paramBeanContextMembershipListener == null)
      throw new NullPointerException("listener"); 
    synchronized (this.bcmListeners) {
      if (this.bcmListeners.contains(paramBeanContextMembershipListener))
        return; 
      this.bcmListeners.add(paramBeanContextMembershipListener);
    } 
  }
  
  public void removeBeanContextMembershipListener(BeanContextMembershipListener paramBeanContextMembershipListener) {
    if (paramBeanContextMembershipListener == null)
      throw new NullPointerException("listener"); 
    synchronized (this.bcmListeners) {
      if (!this.bcmListeners.contains(paramBeanContextMembershipListener))
        return; 
      this.bcmListeners.remove(paramBeanContextMembershipListener);
    } 
  }
  
  public InputStream getResourceAsStream(String paramString, BeanContextChild paramBeanContextChild) {
    if (paramString == null)
      throw new NullPointerException("name"); 
    if (paramBeanContextChild == null)
      throw new NullPointerException("bcc"); 
    if (containsKey(paramBeanContextChild)) {
      ClassLoader classLoader;
      return (classLoader != null) ? classLoader.getResourceAsStream(paramString) : (classLoader = paramBeanContextChild.getClass().getClassLoader()).getSystemResourceAsStream(paramString);
    } 
    throw new IllegalArgumentException("Not a valid child");
  }
  
  public URL getResource(String paramString, BeanContextChild paramBeanContextChild) {
    if (paramString == null)
      throw new NullPointerException("name"); 
    if (paramBeanContextChild == null)
      throw new NullPointerException("bcc"); 
    if (containsKey(paramBeanContextChild)) {
      ClassLoader classLoader;
      return (classLoader != null) ? classLoader.getResource(paramString) : (classLoader = paramBeanContextChild.getClass().getClassLoader()).getSystemResource(paramString);
    } 
    throw new IllegalArgumentException("Not a valid child");
  }
  
  public void setDesignTime(boolean paramBoolean) {
    if (this.designTime != paramBoolean) {
      this.designTime = paramBoolean;
      firePropertyChange("designMode", Boolean.valueOf(!paramBoolean), Boolean.valueOf(paramBoolean));
    } 
  }
  
  public boolean isDesignTime() { return this.designTime; }
  
  public void setLocale(Locale paramLocale) throws PropertyVetoException {
    if (this.locale != null && !this.locale.equals(paramLocale) && paramLocale != null) {
      Locale locale1 = this.locale;
      fireVetoableChange("locale", locale1, paramLocale);
      this.locale = paramLocale;
      firePropertyChange("locale", locale1, paramLocale);
    } 
  }
  
  public Locale getLocale() { return this.locale; }
  
  public boolean needsGui() {
    BeanContext beanContext = getBeanContextPeer();
    if (beanContext != this) {
      if (beanContext instanceof Visibility)
        return beanContext.needsGui(); 
      if (beanContext instanceof java.awt.Container || beanContext instanceof java.awt.Component)
        return true; 
    } 
    synchronized (this.children) {
      for (Object object : this.children.keySet()) {
        try {
          return ((Visibility)object).needsGui();
        } catch (ClassCastException classCastException) {
          if (object instanceof java.awt.Container || object instanceof java.awt.Component)
            return true; 
        } 
      } 
    } 
    return false;
  }
  
  public void dontUseGui() {
    if (this.okToUseGui) {
      this.okToUseGui = false;
      synchronized (this.children) {
        Iterator iterator = this.children.keySet().iterator();
        while (iterator.hasNext()) {
          Visibility visibility = getChildVisibility(iterator.next());
          if (visibility != null)
            visibility.dontUseGui(); 
        } 
      } 
    } 
  }
  
  public void okToUseGui() {
    if (!this.okToUseGui) {
      this.okToUseGui = true;
      synchronized (this.children) {
        Iterator iterator = this.children.keySet().iterator();
        while (iterator.hasNext()) {
          Visibility visibility = getChildVisibility(iterator.next());
          if (visibility != null)
            visibility.okToUseGui(); 
        } 
      } 
    } 
  }
  
  public boolean avoidingGui() { return (!this.okToUseGui && needsGui()); }
  
  public boolean isSerializing() { return this.serializing; }
  
  protected Iterator bcsChildren() {
    synchronized (this.children) {
      return this.children.values().iterator();
    } 
  }
  
  protected void bcsPreSerializationHook(ObjectOutputStream paramObjectOutputStream) throws IOException {}
  
  protected void bcsPreDeserializationHook(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {}
  
  protected void childDeserializedHook(Object paramObject, BCSChild paramBCSChild) {
    synchronized (this.children) {
      this.children.put(paramObject, paramBCSChild);
    } 
  }
  
  protected final void serialize(ObjectOutputStream paramObjectOutputStream, Collection paramCollection) throws IOException {
    byte b1 = 0;
    Object[] arrayOfObject = paramCollection.toArray();
    byte b2;
    for (b2 = 0; b2 < arrayOfObject.length; b2++) {
      if (arrayOfObject[b2] instanceof Serializable) {
        b1++;
      } else {
        arrayOfObject[b2] = null;
      } 
    } 
    paramObjectOutputStream.writeInt(b1);
    for (b2 = 0; b1 > 0; b2++) {
      Object object = arrayOfObject[b2];
      if (object != null) {
        paramObjectOutputStream.writeObject(object);
        b1--;
      } 
    } 
  }
  
  protected final void deserialize(ObjectInputStream paramObjectInputStream, Collection paramCollection) throws IOException, ClassNotFoundException {
    int i = 0;
    i = paramObjectInputStream.readInt();
    while (i-- > 0)
      paramCollection.add(paramObjectInputStream.readObject()); 
  }
  
  public final void writeChildren(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (this.serializable <= 0)
      return; 
    boolean bool = this.serializing;
    this.serializing = true;
    byte b = 0;
    synchronized (this.children) {
      Iterator iterator = this.children.entrySet().iterator();
      while (iterator.hasNext() && b < this.serializable) {
        Map.Entry entry = (Map.Entry)iterator.next();
        if (entry.getKey() instanceof Serializable) {
          try {
            paramObjectOutputStream.writeObject(entry.getKey());
            paramObjectOutputStream.writeObject(entry.getValue());
          } catch (IOException iOException) {
            this.serializing = bool;
            throw iOException;
          } 
          b++;
        } 
      } 
    } 
    this.serializing = bool;
    if (b != this.serializable)
      throw new IOException("wrote different number of children than expected"); 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    this.serializing = true;
    synchronized (BeanContext.globalHierarchyLock) {
      try {
        paramObjectOutputStream.defaultWriteObject();
        bcsPreSerializationHook(paramObjectOutputStream);
        if (this.serializable > 0 && equals(getBeanContextPeer()))
          writeChildren(paramObjectOutputStream); 
        serialize(paramObjectOutputStream, this.bcmListeners);
      } finally {
        this.serializing = false;
      } 
    } 
  }
  
  public final void readChildren(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    int i = this.serializable;
    while (i-- > 0) {
      Object object = null;
      BCSChild bCSChild = null;
      try {
        object = paramObjectInputStream.readObject();
        bCSChild = (BCSChild)paramObjectInputStream.readObject();
      } catch (IOException iOException) {
        continue;
      } catch (ClassNotFoundException classNotFoundException) {
        continue;
      } 
      synchronized (object) {
        BeanContextChild beanContextChild = null;
        try {
          beanContextChild = (BeanContextChild)object;
        } catch (ClassCastException classCastException) {}
        if (beanContextChild != null)
          try {
            beanContextChild.setBeanContext(getBeanContextPeer());
            beanContextChild.addPropertyChangeListener("beanContext", this.childPCL);
            beanContextChild.addVetoableChangeListener("beanContext", this.childVCL);
          } catch (PropertyVetoException propertyVetoException) {
            continue;
          }  
        childDeserializedHook(object, bCSChild);
      } 
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    synchronized (BeanContext.globalHierarchyLock) {
      paramObjectInputStream.defaultReadObject();
      initialize();
      bcsPreDeserializationHook(paramObjectInputStream);
      if (this.serializable > 0 && equals(getBeanContextPeer()))
        readChildren(paramObjectInputStream); 
      deserialize(paramObjectInputStream, this.bcmListeners = new ArrayList(1));
    } 
  }
  
  public void vetoableChange(PropertyChangeEvent paramPropertyChangeEvent) throws PropertyVetoException {
    String str = paramPropertyChangeEvent.getPropertyName();
    Object object = paramPropertyChangeEvent.getSource();
    synchronized (this.children) {
      if ("beanContext".equals(str) && containsKey(object) && !getBeanContextPeer().equals(paramPropertyChangeEvent.getNewValue())) {
        if (!validatePendingRemove(object))
          throw new PropertyVetoException("current BeanContext vetoes setBeanContext()", paramPropertyChangeEvent); 
        ((BCSChild)this.children.get(object)).setRemovePending(true);
      } 
    } 
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) throws PropertyVetoException {
    String str = paramPropertyChangeEvent.getPropertyName();
    Object object = paramPropertyChangeEvent.getSource();
    synchronized (this.children) {
      if ("beanContext".equals(str) && containsKey(object) && ((BCSChild)this.children.get(object)).isRemovePending()) {
        BeanContext beanContext = getBeanContextPeer();
        if (beanContext.equals(paramPropertyChangeEvent.getOldValue()) && !beanContext.equals(paramPropertyChangeEvent.getNewValue())) {
          remove(object, false);
        } else {
          ((BCSChild)this.children.get(object)).setRemovePending(false);
        } 
      } 
    } 
  }
  
  protected boolean validatePendingAdd(Object paramObject) { return true; }
  
  protected boolean validatePendingRemove(Object paramObject) { return true; }
  
  protected void childJustAddedHook(Object paramObject, BCSChild paramBCSChild) {}
  
  protected void childJustRemovedHook(Object paramObject, BCSChild paramBCSChild) {}
  
  protected static final Visibility getChildVisibility(Object paramObject) {
    try {
      return (Visibility)paramObject;
    } catch (ClassCastException classCastException) {
      return null;
    } 
  }
  
  protected static final Serializable getChildSerializable(Object paramObject) {
    try {
      return (Serializable)paramObject;
    } catch (ClassCastException classCastException) {
      return null;
    } 
  }
  
  protected static final PropertyChangeListener getChildPropertyChangeListener(Object paramObject) {
    try {
      return (PropertyChangeListener)paramObject;
    } catch (ClassCastException classCastException) {
      return null;
    } 
  }
  
  protected static final VetoableChangeListener getChildVetoableChangeListener(Object paramObject) {
    try {
      return (VetoableChangeListener)paramObject;
    } catch (ClassCastException classCastException) {
      return null;
    } 
  }
  
  protected static final BeanContextMembershipListener getChildBeanContextMembershipListener(Object paramObject) {
    try {
      return (BeanContextMembershipListener)paramObject;
    } catch (ClassCastException classCastException) {
      return null;
    } 
  }
  
  protected static final BeanContextChild getChildBeanContextChild(Object paramObject) {
    try {
      BeanContextChild beanContextChild = (BeanContextChild)paramObject;
      if (paramObject instanceof BeanContextChild && paramObject instanceof BeanContextProxy)
        throw new IllegalArgumentException("child cannot implement both BeanContextChild and BeanContextProxy"); 
      return beanContextChild;
    } catch (ClassCastException classCastException) {
      try {
        return ((BeanContextProxy)paramObject).getBeanContextProxy();
      } catch (ClassCastException classCastException1) {
        return null;
      } 
    } 
  }
  
  protected final void fireChildrenAdded(BeanContextMembershipEvent paramBeanContextMembershipEvent) {
    Object[] arrayOfObject;
    synchronized (this.bcmListeners) {
      arrayOfObject = this.bcmListeners.toArray();
    } 
    for (byte b = 0; b < arrayOfObject.length; b++)
      ((BeanContextMembershipListener)arrayOfObject[b]).childrenAdded(paramBeanContextMembershipEvent); 
  }
  
  protected final void fireChildrenRemoved(BeanContextMembershipEvent paramBeanContextMembershipEvent) {
    Object[] arrayOfObject;
    synchronized (this.bcmListeners) {
      arrayOfObject = this.bcmListeners.toArray();
    } 
    for (byte b = 0; b < arrayOfObject.length; b++)
      ((BeanContextMembershipListener)arrayOfObject[b]).childrenRemoved(paramBeanContextMembershipEvent); 
  }
  
  protected void initialize() {
    this.children = new HashMap(this.serializable + 1);
    this.bcmListeners = new ArrayList(1);
    this.childPCL = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) throws PropertyVetoException { BeanContextSupport.this.propertyChange(param1PropertyChangeEvent); }
      };
    this.childVCL = new VetoableChangeListener() {
        public void vetoableChange(PropertyChangeEvent param1PropertyChangeEvent) throws PropertyVetoException { BeanContextSupport.this.vetoableChange(param1PropertyChangeEvent); }
      };
  }
  
  protected final Object[] copyChildren() {
    synchronized (this.children) {
      return this.children.keySet().toArray();
    } 
  }
  
  protected static final boolean classEquals(Class paramClass1, Class paramClass2) { return (paramClass1.equals(paramClass2) || paramClass1.getName().equals(paramClass2.getName())); }
  
  protected class BCSChild implements Serializable {
    private static final long serialVersionUID = -5815286101609939109L;
    
    private Object child;
    
    private Object proxyPeer;
    
    private boolean removePending;
    
    BCSChild(Object param1Object1, Object param1Object2) {
      this.child = param1Object1;
      this.proxyPeer = param1Object2;
    }
    
    Object getChild() { return this.child; }
    
    void setRemovePending(boolean param1Boolean) { this.removePending = param1Boolean; }
    
    boolean isRemovePending() { return this.removePending; }
    
    boolean isProxyPeer() { return (this.proxyPeer != null); }
    
    Object getProxyPeer() { return this.proxyPeer; }
  }
  
  protected static final class BCSIterator implements Iterator {
    private Iterator src;
    
    BCSIterator(Iterator param1Iterator) { this.src = param1Iterator; }
    
    public boolean hasNext() { return this.src.hasNext(); }
    
    public Object next() { return this.src.next(); }
    
    public void remove() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\beancontext\BeanContextSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */