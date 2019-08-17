package sun.awt.im;

import java.awt.AWTEvent;
import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.im.InputContext;
import java.awt.im.InputMethodRequests;
import java.awt.im.spi.InputMethod;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import sun.awt.SunToolkit;
import sun.util.logging.PlatformLogger;

public class InputContext extends InputContext implements ComponentListener, WindowListener {
  private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.im.InputContext");
  
  private InputMethodLocator inputMethodLocator;
  
  private InputMethod inputMethod;
  
  private boolean inputMethodCreationFailed;
  
  private HashMap<InputMethodLocator, InputMethod> usedInputMethods;
  
  private Component currentClientComponent;
  
  private Component awtFocussedComponent;
  
  private boolean isInputMethodActive;
  
  private Character.Subset[] characterSubsets = null;
  
  private boolean compositionAreaHidden = false;
  
  private static InputContext inputMethodWindowContext;
  
  private static InputMethod previousInputMethod = null;
  
  private boolean clientWindowNotificationEnabled = false;
  
  private Window clientWindowListened;
  
  private Rectangle clientWindowLocation = null;
  
  private HashMap<InputMethod, Boolean> perInputMethodState;
  
  private static AWTKeyStroke inputMethodSelectionKey;
  
  private static boolean inputMethodSelectionKeyInitialized = false;
  
  private static final String inputMethodSelectionKeyPath = "/java/awt/im/selectionKey";
  
  private static final String inputMethodSelectionKeyCodeName = "keyCode";
  
  private static final String inputMethodSelectionKeyModifiersName = "modifiers";
  
  protected InputContext() {
    InputMethodManager inputMethodManager = InputMethodManager.getInstance();
    synchronized (InputContext.class) {
      if (!inputMethodSelectionKeyInitialized) {
        inputMethodSelectionKeyInitialized = true;
        if (inputMethodManager.hasMultipleInputMethods())
          initializeInputMethodSelectionKey(); 
      } 
    } 
    selectInputMethod(inputMethodManager.getDefaultKeyboardLocale());
  }
  
  public boolean selectInputMethod(Locale paramLocale) {
    if (paramLocale == null)
      throw new NullPointerException(); 
    if (this.inputMethod != null) {
      if (this.inputMethod.setLocale(paramLocale))
        return true; 
    } else if (this.inputMethodLocator != null && this.inputMethodLocator.isLocaleAvailable(paramLocale)) {
      this.inputMethodLocator = this.inputMethodLocator.deriveLocator(paramLocale);
      return true;
    } 
    InputMethodLocator inputMethodLocator1 = InputMethodManager.getInstance().findInputMethod(paramLocale);
    if (inputMethodLocator1 != null) {
      changeInputMethod(inputMethodLocator1);
      return true;
    } 
    if (this.inputMethod == null && this.inputMethodLocator != null) {
      this.inputMethod = getInputMethod();
      if (this.inputMethod != null)
        return this.inputMethod.setLocale(paramLocale); 
    } 
    return false;
  }
  
  public Locale getLocale() { return (this.inputMethod != null) ? this.inputMethod.getLocale() : ((this.inputMethodLocator != null) ? this.inputMethodLocator.getLocale() : null); }
  
  public void setCharacterSubsets(Character.Subset[] paramArrayOfSubset) {
    if (paramArrayOfSubset == null) {
      this.characterSubsets = null;
    } else {
      this.characterSubsets = new Character.Subset[paramArrayOfSubset.length];
      System.arraycopy(paramArrayOfSubset, 0, this.characterSubsets, 0, this.characterSubsets.length);
    } 
    if (this.inputMethod != null)
      this.inputMethod.setCharacterSubsets(paramArrayOfSubset); 
  }
  
  public void reconvert() {
    InputMethod inputMethod1 = getInputMethod();
    if (inputMethod1 == null)
      throw new UnsupportedOperationException(); 
    inputMethod1.reconvert();
  }
  
  public void dispatchEvent(AWTEvent paramAWTEvent) {
    if (paramAWTEvent instanceof java.awt.event.InputMethodEvent)
      return; 
    if (paramAWTEvent instanceof FocusEvent) {
      Component component = ((FocusEvent)paramAWTEvent).getOppositeComponent();
      if (component != null && getComponentWindow(component) instanceof InputMethodWindow && component.getInputContext() == this)
        return; 
    } 
    InputMethod inputMethod1 = getInputMethod();
    int i = paramAWTEvent.getID();
    switch (i) {
      case 1004:
        focusGained((Component)paramAWTEvent.getSource());
        return;
      case 1005:
        focusLost((Component)paramAWTEvent.getSource(), ((FocusEvent)paramAWTEvent).isTemporary());
        return;
      case 401:
        if (checkInputMethodSelectionKey((KeyEvent)paramAWTEvent)) {
          InputMethodManager.getInstance().notifyChangeRequestByHotKey((Component)paramAWTEvent.getSource());
          return;
        } 
        break;
    } 
    if (inputMethod1 != null && paramAWTEvent instanceof java.awt.event.InputEvent)
      inputMethod1.dispatchEvent(paramAWTEvent); 
  }
  
  private void focusGained(Component paramComponent) {
    synchronized (paramComponent.getTreeLock()) {
      synchronized (this) {
        if (!"sun.awt.im.CompositionArea".equals(paramComponent.getClass().getName()) && !(getComponentWindow(paramComponent) instanceof InputMethodWindow)) {
          if (!paramComponent.isDisplayable())
            return; 
          if (this.inputMethod != null && this.currentClientComponent != null && this.currentClientComponent != paramComponent) {
            if (!this.isInputMethodActive)
              activateInputMethod(false); 
            endComposition();
            deactivateInputMethod(false);
          } 
          this.currentClientComponent = paramComponent;
        } 
        this.awtFocussedComponent = paramComponent;
        if (this.inputMethod instanceof InputMethodAdapter)
          ((InputMethodAdapter)this.inputMethod).setAWTFocussedComponent(paramComponent); 
        if (!this.isInputMethodActive)
          activateInputMethod(true); 
        InputMethodContext inputMethodContext = (InputMethodContext)this;
        if (!inputMethodContext.isCompositionAreaVisible()) {
          InputMethodRequests inputMethodRequests = paramComponent.getInputMethodRequests();
          if (inputMethodRequests != null && inputMethodContext.useBelowTheSpotInput()) {
            inputMethodContext.setCompositionAreaUndecorated(true);
          } else {
            inputMethodContext.setCompositionAreaUndecorated(false);
          } 
        } 
        if (this.compositionAreaHidden == true) {
          ((InputMethodContext)this).setCompositionAreaVisible(true);
          this.compositionAreaHidden = false;
        } 
      } 
    } 
  }
  
  private void activateInputMethod(boolean paramBoolean) {
    if (inputMethodWindowContext != null && inputMethodWindowContext != this && inputMethodWindowContext.inputMethodLocator != null && !inputMethodWindowContext.inputMethodLocator.sameInputMethod(this.inputMethodLocator) && inputMethodWindowContext.inputMethod != null)
      inputMethodWindowContext.inputMethod.hideWindows(); 
    inputMethodWindowContext = this;
    if (this.inputMethod != null) {
      if (previousInputMethod != this.inputMethod && previousInputMethod instanceof InputMethodAdapter)
        ((InputMethodAdapter)previousInputMethod).stopListening(); 
      previousInputMethod = null;
      if (log.isLoggable(PlatformLogger.Level.FINE))
        log.fine("Current client component " + this.currentClientComponent); 
      if (this.inputMethod instanceof InputMethodAdapter)
        ((InputMethodAdapter)this.inputMethod).setClientComponent(this.currentClientComponent); 
      this.inputMethod.activate();
      this.isInputMethodActive = true;
      if (this.perInputMethodState != null) {
        Boolean bool = (Boolean)this.perInputMethodState.remove(this.inputMethod);
        if (bool != null)
          this.clientWindowNotificationEnabled = bool.booleanValue(); 
      } 
      if (this.clientWindowNotificationEnabled) {
        if (!addedClientWindowListeners())
          addClientWindowListeners(); 
        synchronized (this) {
          if (this.clientWindowListened != null)
            notifyClientWindowChange(this.clientWindowListened); 
        } 
      } else if (addedClientWindowListeners()) {
        removeClientWindowListeners();
      } 
    } 
    InputMethodManager.getInstance().setInputContext(this);
    ((InputMethodContext)this).grabCompositionArea(paramBoolean);
  }
  
  static Window getComponentWindow(Component paramComponent) {
    while (true) {
      if (paramComponent == null)
        return null; 
      if (paramComponent instanceof Window)
        return (Window)paramComponent; 
      paramComponent = paramComponent.getParent();
    } 
  }
  
  private void focusLost(Component paramComponent, boolean paramBoolean) {
    synchronized (paramComponent.getTreeLock()) {
      synchronized (this) {
        if (this.isInputMethodActive)
          deactivateInputMethod(paramBoolean); 
        this.awtFocussedComponent = null;
        if (this.inputMethod instanceof InputMethodAdapter)
          ((InputMethodAdapter)this.inputMethod).setAWTFocussedComponent(null); 
        InputMethodContext inputMethodContext = (InputMethodContext)this;
        if (inputMethodContext.isCompositionAreaVisible()) {
          inputMethodContext.setCompositionAreaVisible(false);
          this.compositionAreaHidden = true;
        } 
      } 
    } 
  }
  
  private boolean checkInputMethodSelectionKey(KeyEvent paramKeyEvent) {
    if (inputMethodSelectionKey != null) {
      AWTKeyStroke aWTKeyStroke = AWTKeyStroke.getAWTKeyStrokeForEvent(paramKeyEvent);
      return inputMethodSelectionKey.equals(aWTKeyStroke);
    } 
    return false;
  }
  
  private void deactivateInputMethod(boolean paramBoolean) {
    InputMethodManager.getInstance().setInputContext(null);
    if (this.inputMethod != null) {
      this.isInputMethodActive = false;
      this.inputMethod.deactivate(paramBoolean);
      previousInputMethod = this.inputMethod;
    } 
  }
  
  void changeInputMethod(InputMethodLocator paramInputMethodLocator) {
    if (this.inputMethodLocator == null) {
      this.inputMethodLocator = paramInputMethodLocator;
      this.inputMethodCreationFailed = false;
      return;
    } 
    if (this.inputMethodLocator.sameInputMethod(paramInputMethodLocator)) {
      Locale locale1 = paramInputMethodLocator.getLocale();
      if (locale1 != null && this.inputMethodLocator.getLocale() != locale1) {
        if (this.inputMethod != null)
          this.inputMethod.setLocale(locale1); 
        this.inputMethodLocator = paramInputMethodLocator;
      } 
      return;
    } 
    Locale locale = this.inputMethodLocator.getLocale();
    boolean bool1 = this.isInputMethodActive;
    boolean bool = false;
    boolean bool2 = false;
    if (this.inputMethod != null) {
      try {
        bool2 = this.inputMethod.isCompositionEnabled();
        bool = true;
      } catch (UnsupportedOperationException unsupportedOperationException) {}
      if (this.currentClientComponent != null) {
        if (!this.isInputMethodActive)
          activateInputMethod(false); 
        endComposition();
        deactivateInputMethod(false);
        if (this.inputMethod instanceof InputMethodAdapter)
          ((InputMethodAdapter)this.inputMethod).setClientComponent(null); 
      } 
      locale = this.inputMethod.getLocale();
      if (this.usedInputMethods == null)
        this.usedInputMethods = new HashMap(5); 
      if (this.perInputMethodState == null)
        this.perInputMethodState = new HashMap(5); 
      this.usedInputMethods.put(this.inputMethodLocator.deriveLocator(null), this.inputMethod);
      this.perInputMethodState.put(this.inputMethod, Boolean.valueOf(this.clientWindowNotificationEnabled));
      enableClientWindowNotification(this.inputMethod, false);
      if (this == inputMethodWindowContext) {
        this.inputMethod.hideWindows();
        inputMethodWindowContext = null;
      } 
      this.inputMethodLocator = null;
      this.inputMethod = null;
      this.inputMethodCreationFailed = false;
    } 
    if (paramInputMethodLocator.getLocale() == null && locale != null && paramInputMethodLocator.isLocaleAvailable(locale))
      paramInputMethodLocator = paramInputMethodLocator.deriveLocator(locale); 
    this.inputMethodLocator = paramInputMethodLocator;
    this.inputMethodCreationFailed = false;
    if (bool1) {
      this.inputMethod = getInputMethodInstance();
      if (this.inputMethod instanceof InputMethodAdapter)
        ((InputMethodAdapter)this.inputMethod).setAWTFocussedComponent(this.awtFocussedComponent); 
      activateInputMethod(true);
    } 
    if (bool) {
      this.inputMethod = getInputMethod();
      if (this.inputMethod != null)
        try {
          this.inputMethod.setCompositionEnabled(bool2);
        } catch (UnsupportedOperationException unsupportedOperationException) {} 
    } 
  }
  
  Component getClientComponent() { return this.currentClientComponent; }
  
  public void removeNotify(Component paramComponent) {
    if (paramComponent == null)
      throw new NullPointerException(); 
    if (this.inputMethod == null) {
      if (paramComponent == this.currentClientComponent)
        this.currentClientComponent = null; 
      return;
    } 
    if (paramComponent == this.awtFocussedComponent)
      focusLost(paramComponent, false); 
    if (paramComponent == this.currentClientComponent) {
      if (this.isInputMethodActive)
        deactivateInputMethod(false); 
      this.inputMethod.removeNotify();
      if (this.clientWindowNotificationEnabled && addedClientWindowListeners())
        removeClientWindowListeners(); 
      this.currentClientComponent = null;
      if (this.inputMethod instanceof InputMethodAdapter)
        ((InputMethodAdapter)this.inputMethod).setClientComponent(null); 
      if (EventQueue.isDispatchThread()) {
        ((InputMethodContext)this).releaseCompositionArea();
      } else {
        EventQueue.invokeLater(new Runnable() {
              public void run() { ((InputMethodContext)InputContext.this).releaseCompositionArea(); }
            });
      } 
    } 
  }
  
  public void dispose() {
    if (this.currentClientComponent != null)
      throw new IllegalStateException("Can't dispose InputContext while it's active"); 
    if (this.inputMethod != null) {
      if (this == inputMethodWindowContext) {
        this.inputMethod.hideWindows();
        inputMethodWindowContext = null;
      } 
      if (this.inputMethod == previousInputMethod)
        previousInputMethod = null; 
      if (this.clientWindowNotificationEnabled) {
        if (addedClientWindowListeners())
          removeClientWindowListeners(); 
        this.clientWindowNotificationEnabled = false;
      } 
      this.inputMethod.dispose();
      if (this.clientWindowNotificationEnabled)
        enableClientWindowNotification(this.inputMethod, false); 
      this.inputMethod = null;
    } 
    this.inputMethodLocator = null;
    if (this.usedInputMethods != null && !this.usedInputMethods.isEmpty()) {
      Iterator iterator = this.usedInputMethods.values().iterator();
      this.usedInputMethods = null;
      while (iterator.hasNext())
        ((InputMethod)iterator.next()).dispose(); 
    } 
    this.clientWindowNotificationEnabled = false;
    this.clientWindowListened = null;
    this.perInputMethodState = null;
  }
  
  public Object getInputMethodControlObject() {
    InputMethod inputMethod1 = getInputMethod();
    return (inputMethod1 != null) ? inputMethod1.getControlObject() : null;
  }
  
  public void setCompositionEnabled(boolean paramBoolean) {
    InputMethod inputMethod1 = getInputMethod();
    if (inputMethod1 == null)
      throw new UnsupportedOperationException(); 
    inputMethod1.setCompositionEnabled(paramBoolean);
  }
  
  public boolean isCompositionEnabled() {
    InputMethod inputMethod1 = getInputMethod();
    if (inputMethod1 == null)
      throw new UnsupportedOperationException(); 
    return inputMethod1.isCompositionEnabled();
  }
  
  public String getInputMethodInfo() {
    InputMethod inputMethod1 = getInputMethod();
    if (inputMethod1 == null)
      throw new UnsupportedOperationException("Null input method"); 
    String str = null;
    if (inputMethod1 instanceof InputMethodAdapter)
      str = ((InputMethodAdapter)inputMethod1).getNativeInputMethodInfo(); 
    if (str == null && this.inputMethodLocator != null)
      str = this.inputMethodLocator.getDescriptor().getInputMethodDisplayName(getLocale(), SunToolkit.getStartupLocale()); 
    return (str != null && !str.equals("")) ? str : (inputMethod1.toString() + "-" + inputMethod1.getLocale().toString());
  }
  
  public void disableNativeIM() {
    InputMethod inputMethod1 = getInputMethod();
    if (inputMethod1 != null && inputMethod1 instanceof InputMethodAdapter)
      ((InputMethodAdapter)inputMethod1).stopListening(); 
  }
  
  private InputMethod getInputMethod() {
    if (this.inputMethod != null)
      return this.inputMethod; 
    if (this.inputMethodCreationFailed)
      return null; 
    this.inputMethod = getInputMethodInstance();
    return this.inputMethod;
  }
  
  private InputMethod getInputMethodInstance() {
    InputMethodLocator inputMethodLocator1 = this.inputMethodLocator;
    if (inputMethodLocator1 == null) {
      this.inputMethodCreationFailed = true;
      return null;
    } 
    Locale locale = inputMethodLocator1.getLocale();
    InputMethod inputMethod1 = null;
    if (this.usedInputMethods != null) {
      inputMethod1 = (InputMethod)this.usedInputMethods.remove(inputMethodLocator1.deriveLocator(null));
      if (inputMethod1 != null) {
        if (locale != null)
          inputMethod1.setLocale(locale); 
        inputMethod1.setCharacterSubsets(this.characterSubsets);
        Boolean bool = (Boolean)this.perInputMethodState.remove(inputMethod1);
        if (bool != null)
          enableClientWindowNotification(inputMethod1, bool.booleanValue()); 
        ((InputMethodContext)this).setInputMethodSupportsBelowTheSpot((!(inputMethod1 instanceof InputMethodAdapter) || ((InputMethodAdapter)inputMethod1).supportsBelowTheSpot()));
        return inputMethod1;
      } 
    } 
    try {
      inputMethod1 = inputMethodLocator1.getDescriptor().createInputMethod();
      if (locale != null)
        inputMethod1.setLocale(locale); 
      inputMethod1.setInputMethodContext((InputMethodContext)this);
      inputMethod1.setCharacterSubsets(this.characterSubsets);
    } catch (Exception exception) {
      logCreationFailed(exception);
      this.inputMethodCreationFailed = true;
      if (inputMethod1 != null)
        inputMethod1 = null; 
    } catch (LinkageError linkageError) {
      logCreationFailed(linkageError);
      this.inputMethodCreationFailed = true;
    } 
    ((InputMethodContext)this).setInputMethodSupportsBelowTheSpot((!(inputMethod1 instanceof InputMethodAdapter) || ((InputMethodAdapter)inputMethod1).supportsBelowTheSpot()));
    return inputMethod1;
  }
  
  private void logCreationFailed(Throwable paramThrowable) {
    PlatformLogger platformLogger = PlatformLogger.getLogger("sun.awt.im");
    if (platformLogger.isLoggable(PlatformLogger.Level.CONFIG)) {
      String str = Toolkit.getProperty("AWT.InputMethodCreationFailed", "Could not create {0}. Reason: {1}");
      Object[] arrayOfObject = { this.inputMethodLocator.getDescriptor().getInputMethodDisplayName(null, Locale.getDefault()), paramThrowable.getLocalizedMessage() };
      MessageFormat messageFormat = new MessageFormat(str);
      platformLogger.config(messageFormat.format(arrayOfObject));
    } 
  }
  
  InputMethodLocator getInputMethodLocator() { return (this.inputMethod != null) ? this.inputMethodLocator.deriveLocator(this.inputMethod.getLocale()) : this.inputMethodLocator; }
  
  public void endComposition() {
    if (this.inputMethod != null)
      this.inputMethod.endComposition(); 
  }
  
  void enableClientWindowNotification(InputMethod paramInputMethod, boolean paramBoolean) {
    if (paramInputMethod != this.inputMethod) {
      if (this.perInputMethodState == null)
        this.perInputMethodState = new HashMap(5); 
      this.perInputMethodState.put(paramInputMethod, Boolean.valueOf(paramBoolean));
      return;
    } 
    if (this.clientWindowNotificationEnabled != paramBoolean) {
      this.clientWindowLocation = null;
      this.clientWindowNotificationEnabled = paramBoolean;
    } 
    if (this.clientWindowNotificationEnabled) {
      if (!addedClientWindowListeners())
        addClientWindowListeners(); 
      if (this.clientWindowListened != null) {
        this.clientWindowLocation = null;
        notifyClientWindowChange(this.clientWindowListened);
      } 
    } else if (addedClientWindowListeners()) {
      removeClientWindowListeners();
    } 
  }
  
  private void notifyClientWindowChange(Window paramWindow) {
    if (this.inputMethod == null)
      return; 
    if (!paramWindow.isVisible() || (paramWindow instanceof Frame && ((Frame)paramWindow).getState() == 1)) {
      this.clientWindowLocation = null;
      this.inputMethod.notifyClientWindowChange(null);
      return;
    } 
    Rectangle rectangle = paramWindow.getBounds();
    if (this.clientWindowLocation == null || !this.clientWindowLocation.equals(rectangle)) {
      this.clientWindowLocation = rectangle;
      this.inputMethod.notifyClientWindowChange(this.clientWindowLocation);
    } 
  }
  
  private void addClientWindowListeners() {
    Component component = getClientComponent();
    if (component == null)
      return; 
    Window window = getComponentWindow(component);
    if (window == null)
      return; 
    window.addComponentListener(this);
    window.addWindowListener(this);
    this.clientWindowListened = window;
  }
  
  private void removeClientWindowListeners() {
    this.clientWindowListened.removeComponentListener(this);
    this.clientWindowListened.removeWindowListener(this);
    this.clientWindowListened = null;
  }
  
  private boolean addedClientWindowListeners() { return (this.clientWindowListened != null); }
  
  public void componentResized(ComponentEvent paramComponentEvent) { notifyClientWindowChange((Window)paramComponentEvent.getComponent()); }
  
  public void componentMoved(ComponentEvent paramComponentEvent) { notifyClientWindowChange((Window)paramComponentEvent.getComponent()); }
  
  public void componentShown(ComponentEvent paramComponentEvent) { notifyClientWindowChange((Window)paramComponentEvent.getComponent()); }
  
  public void componentHidden(ComponentEvent paramComponentEvent) { notifyClientWindowChange((Window)paramComponentEvent.getComponent()); }
  
  public void windowOpened(WindowEvent paramWindowEvent) {}
  
  public void windowClosing(WindowEvent paramWindowEvent) {}
  
  public void windowClosed(WindowEvent paramWindowEvent) {}
  
  public void windowIconified(WindowEvent paramWindowEvent) { notifyClientWindowChange(paramWindowEvent.getWindow()); }
  
  public void windowDeiconified(WindowEvent paramWindowEvent) { notifyClientWindowChange(paramWindowEvent.getWindow()); }
  
  public void windowActivated(WindowEvent paramWindowEvent) {}
  
  public void windowDeactivated(WindowEvent paramWindowEvent) {}
  
  private void initializeInputMethodSelectionKey() { AccessController.doPrivileged(new PrivilegedAction<Object>() {
          public Object run() {
            Preferences preferences = Preferences.userRoot();
            inputMethodSelectionKey = InputContext.this.getInputMethodSelectionKeyStroke(preferences);
            if (inputMethodSelectionKey == null) {
              preferences = Preferences.systemRoot();
              inputMethodSelectionKey = InputContext.this.getInputMethodSelectionKeyStroke(preferences);
            } 
            return null;
          }
        }); }
  
  private AWTKeyStroke getInputMethodSelectionKeyStroke(Preferences paramPreferences) {
    try {
      if (paramPreferences.nodeExists("/java/awt/im/selectionKey")) {
        Preferences preferences = paramPreferences.node("/java/awt/im/selectionKey");
        int i = preferences.getInt("keyCode", 0);
        if (i != 0) {
          int j = preferences.getInt("modifiers", 0);
          return AWTKeyStroke.getAWTKeyStroke(i, j);
        } 
      } 
    } catch (BackingStoreException backingStoreException) {}
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\im\InputContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */