package java.util.prefs;

import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.TreeSet;

public abstract class AbstractPreferences extends Preferences {
  private final String name;
  
  private final String absolutePath;
  
  final AbstractPreferences parent;
  
  private final AbstractPreferences root;
  
  protected boolean newNode = false;
  
  private Map<String, AbstractPreferences> kidCache = new HashMap();
  
  private boolean removed = false;
  
  private PreferenceChangeListener[] prefListeners = new PreferenceChangeListener[0];
  
  private NodeChangeListener[] nodeListeners = new NodeChangeListener[0];
  
  protected final Object lock = new Object();
  
  private static final String[] EMPTY_STRING_ARRAY = new String[0];
  
  private static final AbstractPreferences[] EMPTY_ABSTRACT_PREFS_ARRAY = new AbstractPreferences[0];
  
  private static final List<EventObject> eventQueue = new LinkedList();
  
  private static Thread eventDispatchThread = null;
  
  protected AbstractPreferences(AbstractPreferences paramAbstractPreferences, String paramString) {
    if (paramAbstractPreferences == null) {
      if (!paramString.equals(""))
        throw new IllegalArgumentException("Root name '" + paramString + "' must be \"\""); 
      this.absolutePath = "/";
      this.root = this;
    } else {
      if (paramString.indexOf('/') != -1)
        throw new IllegalArgumentException("Name '" + paramString + "' contains '/'"); 
      if (paramString.equals(""))
        throw new IllegalArgumentException("Illegal name: empty string"); 
      this.root = paramAbstractPreferences.root;
      this.absolutePath = (paramAbstractPreferences == this.root) ? ("/" + paramString) : (paramAbstractPreferences.absolutePath() + "/" + paramString);
    } 
    this.name = paramString;
    this.parent = paramAbstractPreferences;
  }
  
  public void put(String paramString1, String paramString2) {
    if (paramString1 == null || paramString2 == null)
      throw new NullPointerException(); 
    if (paramString1.length() > 80)
      throw new IllegalArgumentException("Key too long: " + paramString1); 
    if (paramString2.length() > 8192)
      throw new IllegalArgumentException("Value too long: " + paramString2); 
    synchronized (this.lock) {
      if (this.removed)
        throw new IllegalStateException("Node has been removed."); 
      putSpi(paramString1, paramString2);
      enqueuePreferenceChangeEvent(paramString1, paramString2);
    } 
  }
  
  public String get(String paramString1, String paramString2) {
    if (paramString1 == null)
      throw new NullPointerException("Null key"); 
    synchronized (this.lock) {
      if (this.removed)
        throw new IllegalStateException("Node has been removed."); 
      String str = null;
      try {
        str = getSpi(paramString1);
      } catch (Exception exception) {}
      return (str == null) ? paramString2 : str;
    } 
  }
  
  public void remove(String paramString) {
    Objects.requireNonNull(paramString, "Specified key cannot be null");
    synchronized (this.lock) {
      if (this.removed)
        throw new IllegalStateException("Node has been removed."); 
      removeSpi(paramString);
      enqueuePreferenceChangeEvent(paramString, null);
    } 
  }
  
  public void clear() throws BackingStoreException {
    synchronized (this.lock) {
      String[] arrayOfString = keys();
      for (byte b = 0; b < arrayOfString.length; b++)
        remove(arrayOfString[b]); 
    } 
  }
  
  public void putInt(String paramString, int paramInt) { put(paramString, Integer.toString(paramInt)); }
  
  public int getInt(String paramString, int paramInt) {
    int i = paramInt;
    try {
      String str = get(paramString, null);
      if (str != null)
        i = Integer.parseInt(str); 
    } catch (NumberFormatException numberFormatException) {}
    return i;
  }
  
  public void putLong(String paramString, long paramLong) { put(paramString, Long.toString(paramLong)); }
  
  public long getLong(String paramString, long paramLong) {
    long l = paramLong;
    try {
      String str = get(paramString, null);
      if (str != null)
        l = Long.parseLong(str); 
    } catch (NumberFormatException numberFormatException) {}
    return l;
  }
  
  public void putBoolean(String paramString, boolean paramBoolean) { put(paramString, String.valueOf(paramBoolean)); }
  
  public boolean getBoolean(String paramString, boolean paramBoolean) {
    boolean bool = paramBoolean;
    String str = get(paramString, null);
    if (str != null)
      if (str.equalsIgnoreCase("true")) {
        bool = true;
      } else if (str.equalsIgnoreCase("false")) {
        bool = false;
      }  
    return bool;
  }
  
  public void putFloat(String paramString, float paramFloat) { put(paramString, Float.toString(paramFloat)); }
  
  public float getFloat(String paramString, float paramFloat) {
    float f = paramFloat;
    try {
      String str = get(paramString, null);
      if (str != null)
        f = Float.parseFloat(str); 
    } catch (NumberFormatException numberFormatException) {}
    return f;
  }
  
  public void putDouble(String paramString, double paramDouble) { put(paramString, Double.toString(paramDouble)); }
  
  public double getDouble(String paramString, double paramDouble) {
    double d = paramDouble;
    try {
      String str = get(paramString, null);
      if (str != null)
        d = Double.parseDouble(str); 
    } catch (NumberFormatException numberFormatException) {}
    return d;
  }
  
  public void putByteArray(String paramString, byte[] paramArrayOfByte) { put(paramString, Base64.byteArrayToBase64(paramArrayOfByte)); }
  
  public byte[] getByteArray(String paramString, byte[] paramArrayOfByte) {
    byte[] arrayOfByte = paramArrayOfByte;
    String str = get(paramString, null);
    try {
      if (str != null)
        arrayOfByte = Base64.base64ToByteArray(str); 
    } catch (RuntimeException runtimeException) {}
    return arrayOfByte;
  }
  
  public String[] keys() throws BackingStoreException {
    synchronized (this.lock) {
      if (this.removed)
        throw new IllegalStateException("Node has been removed."); 
      return keysSpi();
    } 
  }
  
  public String[] childrenNames() throws BackingStoreException {
    synchronized (this.lock) {
      if (this.removed)
        throw new IllegalStateException("Node has been removed."); 
      TreeSet treeSet = new TreeSet(this.kidCache.keySet());
      for (String str : childrenNamesSpi())
        treeSet.add(str); 
      return (String[])treeSet.toArray(EMPTY_STRING_ARRAY);
    } 
  }
  
  protected final AbstractPreferences[] cachedChildren() { return (AbstractPreferences[])this.kidCache.values().toArray(EMPTY_ABSTRACT_PREFS_ARRAY); }
  
  public Preferences parent() {
    synchronized (this.lock) {
      if (this.removed)
        throw new IllegalStateException("Node has been removed."); 
      return this.parent;
    } 
  }
  
  public Preferences node(String paramString) {
    synchronized (this.lock) {
      if (this.removed)
        throw new IllegalStateException("Node has been removed."); 
      if (paramString.equals(""))
        return this; 
      if (paramString.equals("/"))
        return this.root; 
      if (paramString.charAt(0) != '/')
        return node(new StringTokenizer(paramString, "/", true)); 
    } 
    return this.root.node(new StringTokenizer(paramString.substring(1), "/", true));
  }
  
  private Preferences node(StringTokenizer paramStringTokenizer) {
    String str = paramStringTokenizer.nextToken();
    if (str.equals("/"))
      throw new IllegalArgumentException("Consecutive slashes in path"); 
    synchronized (this.lock) {
      AbstractPreferences abstractPreferences = (AbstractPreferences)this.kidCache.get(str);
      if (abstractPreferences == null) {
        if (str.length() > 80)
          throw new IllegalArgumentException("Node name " + str + " too long"); 
        abstractPreferences = childSpi(str);
        if (abstractPreferences.newNode)
          enqueueNodeAddedEvent(abstractPreferences); 
        this.kidCache.put(str, abstractPreferences);
      } 
      if (!paramStringTokenizer.hasMoreTokens())
        return abstractPreferences; 
      paramStringTokenizer.nextToken();
      if (!paramStringTokenizer.hasMoreTokens())
        throw new IllegalArgumentException("Path ends with slash"); 
      return abstractPreferences.node(paramStringTokenizer);
    } 
  }
  
  public boolean nodeExists(String paramString) throws BackingStoreException {
    synchronized (this.lock) {
      if (paramString.equals(""))
        return !this.removed; 
      if (this.removed)
        throw new IllegalStateException("Node has been removed."); 
      if (paramString.equals("/"))
        return true; 
      if (paramString.charAt(0) != '/')
        return nodeExists(new StringTokenizer(paramString, "/", true)); 
    } 
    return this.root.nodeExists(new StringTokenizer(paramString.substring(1), "/", true));
  }
  
  private boolean nodeExists(StringTokenizer paramStringTokenizer) throws BackingStoreException {
    String str = paramStringTokenizer.nextToken();
    if (str.equals("/"))
      throw new IllegalArgumentException("Consecutive slashes in path"); 
    synchronized (this.lock) {
      AbstractPreferences abstractPreferences = (AbstractPreferences)this.kidCache.get(str);
      if (abstractPreferences == null)
        abstractPreferences = getChild(str); 
      if (abstractPreferences == null)
        return false; 
      if (!paramStringTokenizer.hasMoreTokens())
        return true; 
      paramStringTokenizer.nextToken();
      if (!paramStringTokenizer.hasMoreTokens())
        throw new IllegalArgumentException("Path ends with slash"); 
      return abstractPreferences.nodeExists(paramStringTokenizer);
    } 
  }
  
  public void removeNode() throws BackingStoreException {
    if (this == this.root)
      throw new UnsupportedOperationException("Can't remove the root!"); 
    synchronized (this.parent.lock) {
      removeNode2();
      this.parent.kidCache.remove(this.name);
    } 
  }
  
  private void removeNode2() throws BackingStoreException {
    synchronized (this.lock) {
      if (this.removed)
        throw new IllegalStateException("Node already removed."); 
      String[] arrayOfString = childrenNamesSpi();
      for (byte b = 0; b < arrayOfString.length; b++) {
        if (!this.kidCache.containsKey(arrayOfString[b]))
          this.kidCache.put(arrayOfString[b], childSpi(arrayOfString[b])); 
      } 
      Iterator iterator = this.kidCache.values().iterator();
      while (iterator.hasNext()) {
        try {
          ((AbstractPreferences)iterator.next()).removeNode2();
          iterator.remove();
        } catch (BackingStoreException backingStoreException) {}
      } 
      removeNodeSpi();
      this.removed = true;
      this.parent.enqueueNodeRemovedEvent(this);
    } 
  }
  
  public String name() { return this.name; }
  
  public String absolutePath() { return this.absolutePath; }
  
  public boolean isUserNode() { return ((Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
          public Boolean run() { return Boolean.valueOf((AbstractPreferences.this.root == Preferences.userRoot())); }
        })).booleanValue(); }
  
  public void addPreferenceChangeListener(PreferenceChangeListener paramPreferenceChangeListener) {
    if (paramPreferenceChangeListener == null)
      throw new NullPointerException("Change listener is null."); 
    synchronized (this.lock) {
      if (this.removed)
        throw new IllegalStateException("Node has been removed."); 
      PreferenceChangeListener[] arrayOfPreferenceChangeListener = this.prefListeners;
      this.prefListeners = new PreferenceChangeListener[arrayOfPreferenceChangeListener.length + 1];
      System.arraycopy(arrayOfPreferenceChangeListener, 0, this.prefListeners, 0, arrayOfPreferenceChangeListener.length);
      this.prefListeners[arrayOfPreferenceChangeListener.length] = paramPreferenceChangeListener;
    } 
    startEventDispatchThreadIfNecessary();
  }
  
  public void removePreferenceChangeListener(PreferenceChangeListener paramPreferenceChangeListener) {
    synchronized (this.lock) {
      if (this.removed)
        throw new IllegalStateException("Node has been removed."); 
      if (this.prefListeners == null || this.prefListeners.length == 0)
        throw new IllegalArgumentException("Listener not registered."); 
      PreferenceChangeListener[] arrayOfPreferenceChangeListener = new PreferenceChangeListener[this.prefListeners.length - 1];
      byte b = 0;
      while (b < arrayOfPreferenceChangeListener.length && this.prefListeners[b] != paramPreferenceChangeListener)
        arrayOfPreferenceChangeListener[b] = this.prefListeners[b++]; 
      if (b == arrayOfPreferenceChangeListener.length && this.prefListeners[b] != paramPreferenceChangeListener)
        throw new IllegalArgumentException("Listener not registered."); 
      while (b < arrayOfPreferenceChangeListener.length)
        arrayOfPreferenceChangeListener[b] = this.prefListeners[++b]; 
      this.prefListeners = arrayOfPreferenceChangeListener;
    } 
  }
  
  public void addNodeChangeListener(NodeChangeListener paramNodeChangeListener) {
    if (paramNodeChangeListener == null)
      throw new NullPointerException("Change listener is null."); 
    synchronized (this.lock) {
      if (this.removed)
        throw new IllegalStateException("Node has been removed."); 
      if (this.nodeListeners == null) {
        this.nodeListeners = new NodeChangeListener[1];
        this.nodeListeners[0] = paramNodeChangeListener;
      } else {
        NodeChangeListener[] arrayOfNodeChangeListener = this.nodeListeners;
        this.nodeListeners = new NodeChangeListener[arrayOfNodeChangeListener.length + 1];
        System.arraycopy(arrayOfNodeChangeListener, 0, this.nodeListeners, 0, arrayOfNodeChangeListener.length);
        this.nodeListeners[arrayOfNodeChangeListener.length] = paramNodeChangeListener;
      } 
    } 
    startEventDispatchThreadIfNecessary();
  }
  
  public void removeNodeChangeListener(NodeChangeListener paramNodeChangeListener) {
    synchronized (this.lock) {
      if (this.removed)
        throw new IllegalStateException("Node has been removed."); 
      if (this.nodeListeners == null || this.nodeListeners.length == 0)
        throw new IllegalArgumentException("Listener not registered."); 
      int i;
      for (i = 0; i < this.nodeListeners.length && this.nodeListeners[i] != paramNodeChangeListener; i++);
      if (i == this.nodeListeners.length)
        throw new IllegalArgumentException("Listener not registered."); 
      NodeChangeListener[] arrayOfNodeChangeListener = new NodeChangeListener[this.nodeListeners.length - 1];
      if (i != 0)
        System.arraycopy(this.nodeListeners, 0, arrayOfNodeChangeListener, 0, i); 
      if (i != arrayOfNodeChangeListener.length)
        System.arraycopy(this.nodeListeners, i + 1, arrayOfNodeChangeListener, i, arrayOfNodeChangeListener.length - i); 
      this.nodeListeners = arrayOfNodeChangeListener;
    } 
  }
  
  protected abstract void putSpi(String paramString1, String paramString2);
  
  protected abstract String getSpi(String paramString);
  
  protected abstract void removeSpi(String paramString);
  
  protected abstract void removeNodeSpi() throws BackingStoreException;
  
  protected abstract String[] keysSpi() throws BackingStoreException;
  
  protected abstract String[] childrenNamesSpi() throws BackingStoreException;
  
  protected AbstractPreferences getChild(String paramString) throws BackingStoreException {
    synchronized (this.lock) {
      String[] arrayOfString = childrenNames();
      for (byte b = 0; b < arrayOfString.length; b++) {
        if (arrayOfString[b].equals(paramString))
          return childSpi(arrayOfString[b]); 
      } 
    } 
    return null;
  }
  
  protected abstract AbstractPreferences childSpi(String paramString) throws BackingStoreException;
  
  public String toString() { return (isUserNode() ? "User" : "System") + " Preference Node: " + absolutePath(); }
  
  public void sync() throws BackingStoreException { sync2(); }
  
  private void sync2() throws BackingStoreException {
    AbstractPreferences[] arrayOfAbstractPreferences;
    synchronized (this.lock) {
      if (this.removed)
        throw new IllegalStateException("Node has been removed"); 
      syncSpi();
      arrayOfAbstractPreferences = cachedChildren();
    } 
    for (byte b = 0; b < arrayOfAbstractPreferences.length; b++)
      arrayOfAbstractPreferences[b].sync2(); 
  }
  
  protected abstract void syncSpi() throws BackingStoreException;
  
  public void flush() throws BackingStoreException { flush2(); }
  
  private void flush2() throws BackingStoreException {
    AbstractPreferences[] arrayOfAbstractPreferences;
    synchronized (this.lock) {
      flushSpi();
      if (this.removed)
        return; 
      arrayOfAbstractPreferences = cachedChildren();
    } 
    for (byte b = 0; b < arrayOfAbstractPreferences.length; b++)
      arrayOfAbstractPreferences[b].flush2(); 
  }
  
  protected abstract void flushSpi() throws BackingStoreException;
  
  protected boolean isRemoved() {
    synchronized (this.lock) {
      return this.removed;
    } 
  }
  
  private static void startEventDispatchThreadIfNecessary() throws BackingStoreException {
    if (eventDispatchThread == null) {
      eventDispatchThread = new EventDispatchThread(null);
      eventDispatchThread.setDaemon(true);
      eventDispatchThread.start();
    } 
  }
  
  PreferenceChangeListener[] prefListeners() {
    synchronized (this.lock) {
      return this.prefListeners;
    } 
  }
  
  NodeChangeListener[] nodeListeners() {
    synchronized (this.lock) {
      return this.nodeListeners;
    } 
  }
  
  private void enqueuePreferenceChangeEvent(String paramString1, String paramString2) {
    if (this.prefListeners.length != 0)
      synchronized (eventQueue) {
        eventQueue.add(new PreferenceChangeEvent(this, paramString1, paramString2));
        eventQueue.notify();
      }  
  }
  
  private void enqueueNodeAddedEvent(Preferences paramPreferences) {
    if (this.nodeListeners.length != 0)
      synchronized (eventQueue) {
        eventQueue.add(new NodeAddedEvent(this, paramPreferences));
        eventQueue.notify();
      }  
  }
  
  private void enqueueNodeRemovedEvent(Preferences paramPreferences) {
    if (this.nodeListeners.length != 0)
      synchronized (eventQueue) {
        eventQueue.add(new NodeRemovedEvent(this, paramPreferences));
        eventQueue.notify();
      }  
  }
  
  public void exportNode(OutputStream paramOutputStream) throws IOException, BackingStoreException { XmlSupport.export(paramOutputStream, this, false); }
  
  public void exportSubtree(OutputStream paramOutputStream) throws IOException, BackingStoreException { XmlSupport.export(paramOutputStream, this, true); }
  
  private static class EventDispatchThread extends Thread {
    private EventDispatchThread() throws BackingStoreException {}
    
    public void run() throws BackingStoreException {
      while (true) {
        EventObject eventObject = null;
        synchronized (eventQueue) {
          while (true) {
            try {
              if (eventQueue.isEmpty()) {
                eventQueue.wait();
                continue;
              } 
              eventObject = (EventObject)eventQueue.remove(0);
              break;
            } catch (InterruptedException interruptedException) {
              return;
            } 
          } 
        } 
        AbstractPreferences abstractPreferences = (AbstractPreferences)eventObject.getSource();
        if (eventObject instanceof PreferenceChangeEvent) {
          PreferenceChangeEvent preferenceChangeEvent = (PreferenceChangeEvent)eventObject;
          PreferenceChangeListener[] arrayOfPreferenceChangeListener = abstractPreferences.prefListeners();
          for (byte b1 = 0; b1 < arrayOfPreferenceChangeListener.length; b1++)
            arrayOfPreferenceChangeListener[b1].preferenceChange(preferenceChangeEvent); 
          continue;
        } 
        NodeChangeEvent nodeChangeEvent = (NodeChangeEvent)eventObject;
        NodeChangeListener[] arrayOfNodeChangeListener = abstractPreferences.nodeListeners();
        if (nodeChangeEvent instanceof AbstractPreferences.NodeAddedEvent) {
          for (byte b1 = 0; b1 < arrayOfNodeChangeListener.length; b1++)
            arrayOfNodeChangeListener[b1].childAdded(nodeChangeEvent); 
          continue;
        } 
        for (byte b = 0; b < arrayOfNodeChangeListener.length; b++)
          arrayOfNodeChangeListener[b].childRemoved(nodeChangeEvent); 
      } 
    }
  }
  
  private class NodeAddedEvent extends NodeChangeEvent {
    private static final long serialVersionUID = -6743557530157328528L;
    
    NodeAddedEvent(Preferences param1Preferences1, Preferences param1Preferences2) { super(param1Preferences1, param1Preferences2); }
  }
  
  private class NodeRemovedEvent extends NodeChangeEvent {
    private static final long serialVersionUID = 8735497392918824837L;
    
    NodeRemovedEvent(Preferences param1Preferences1, Preferences param1Preferences2) { super(param1Preferences1, param1Preferences2); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\prefs\AbstractPreferences.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */