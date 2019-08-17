package java.awt;

import java.awt.event.ComponentEvent;
import java.awt.event.InvocationEvent;
import java.awt.peer.DialogPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.ref.WeakReference;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.awt.util.IdentityArrayList;
import sun.awt.util.IdentityLinkedList;
import sun.security.util.SecurityConstants;

public class Dialog extends Window {
  boolean resizable = true;
  
  boolean undecorated = false;
  
  private boolean initialized = false;
  
  public static final ModalityType DEFAULT_MODALITY_TYPE;
  
  boolean modal;
  
  ModalityType modalityType;
  
  static IdentityArrayList<Dialog> modalDialogs;
  
  IdentityArrayList<Window> blockedWindows = new IdentityArrayList();
  
  String title;
  
  private ModalEventFilter modalFilter;
  
  private static final String base = "dialog";
  
  private static int nameCounter;
  
  private static final long serialVersionUID = 5920926903803293709L;
  
  public Dialog(Frame paramFrame) { this(paramFrame, "", false); }
  
  public Dialog(Frame paramFrame, boolean paramBoolean) { this(paramFrame, "", paramBoolean); }
  
  public Dialog(Frame paramFrame, String paramString) { this(paramFrame, paramString, false); }
  
  public Dialog(Frame paramFrame, String paramString, boolean paramBoolean) { this(paramFrame, paramString, paramBoolean ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS); }
  
  public Dialog(Frame paramFrame, String paramString, boolean paramBoolean, GraphicsConfiguration paramGraphicsConfiguration) { this(paramFrame, paramString, paramBoolean ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS, paramGraphicsConfiguration); }
  
  public Dialog(Dialog paramDialog) { this(paramDialog, "", false); }
  
  public Dialog(Dialog paramDialog, String paramString) { this(paramDialog, paramString, false); }
  
  public Dialog(Dialog paramDialog, String paramString, boolean paramBoolean) { this(paramDialog, paramString, paramBoolean ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS); }
  
  public Dialog(Dialog paramDialog, String paramString, boolean paramBoolean, GraphicsConfiguration paramGraphicsConfiguration) { this(paramDialog, paramString, paramBoolean ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS, paramGraphicsConfiguration); }
  
  public Dialog(Window paramWindow) { this(paramWindow, "", ModalityType.MODELESS); }
  
  public Dialog(Window paramWindow, String paramString) { this(paramWindow, paramString, ModalityType.MODELESS); }
  
  public Dialog(Window paramWindow, ModalityType paramModalityType) { this(paramWindow, "", paramModalityType); }
  
  public Dialog(Window paramWindow, String paramString, ModalityType paramModalityType) {
    super(paramWindow);
    if (paramWindow != null && !(paramWindow instanceof Frame) && !(paramWindow instanceof Dialog))
      throw new IllegalArgumentException("Wrong parent window"); 
    this.title = paramString;
    setModalityType(paramModalityType);
    SunToolkit.checkAndSetPolicy(this);
    this.initialized = true;
  }
  
  public Dialog(Window paramWindow, String paramString, ModalityType paramModalityType, GraphicsConfiguration paramGraphicsConfiguration) {
    super(paramWindow, paramGraphicsConfiguration);
    if (paramWindow != null && !(paramWindow instanceof Frame) && !(paramWindow instanceof Dialog))
      throw new IllegalArgumentException("wrong owner window"); 
    this.title = paramString;
    setModalityType(paramModalityType);
    SunToolkit.checkAndSetPolicy(this);
    this.initialized = true;
  }
  
  String constructComponentName() {
    synchronized (Dialog.class) {
      return "dialog" + nameCounter++;
    } 
  }
  
  public void addNotify() {
    synchronized (getTreeLock()) {
      if (this.parent != null && this.parent.getPeer() == null)
        this.parent.addNotify(); 
      if (this.peer == null)
        this.peer = getToolkit().createDialog(this); 
      super.addNotify();
    } 
  }
  
  public boolean isModal() { return isModal_NoClientCode(); }
  
  final boolean isModal_NoClientCode() { return (this.modalityType != ModalityType.MODELESS); }
  
  public void setModal(boolean paramBoolean) {
    this.modal = paramBoolean;
    setModalityType(paramBoolean ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
  }
  
  public ModalityType getModalityType() { return this.modalityType; }
  
  public void setModalityType(ModalityType paramModalityType) {
    if (paramModalityType == null)
      paramModalityType = ModalityType.MODELESS; 
    if (!Toolkit.getDefaultToolkit().isModalityTypeSupported(paramModalityType))
      paramModalityType = ModalityType.MODELESS; 
    if (this.modalityType == paramModalityType)
      return; 
    checkModalityPermission(paramModalityType);
    this.modalityType = paramModalityType;
    this.modal = (this.modalityType != ModalityType.MODELESS);
  }
  
  public String getTitle() { return this.title; }
  
  public void setTitle(String paramString) {
    String str = this.title;
    synchronized (this) {
      this.title = paramString;
      DialogPeer dialogPeer = (DialogPeer)this.peer;
      if (dialogPeer != null)
        dialogPeer.setTitle(paramString); 
    } 
    firePropertyChange("title", str, paramString);
  }
  
  private boolean conditionalShow(Component paramComponent, AtomicLong paramAtomicLong) {
    boolean bool;
    closeSplashScreen();
    synchronized (getTreeLock()) {
      if (this.peer == null)
        addNotify(); 
      validateUnconditionally();
      if (this.visible) {
        toFront();
        bool = false;
      } else {
        this.visible = bool = true;
        if (!isModal()) {
          checkShouldBeBlocked(this);
        } else {
          modalDialogs.add(this);
          modalShow();
        } 
        if (paramComponent != null && paramAtomicLong != null && isFocusable() && isEnabled() && !isModalBlocked()) {
          paramAtomicLong.set(Toolkit.getEventQueue().getMostRecentKeyEventTime());
          KeyboardFocusManager.getCurrentKeyboardFocusManager().enqueueKeyEvents(paramAtomicLong.get(), paramComponent);
        } 
        mixOnShowing();
        this.peer.setVisible(true);
        if (isModalBlocked())
          this.modalBlocker.toFront(); 
        setLocationByPlatform(false);
        for (byte b = 0; b < this.ownedWindowList.size(); b++) {
          Window window = (Window)((WeakReference)this.ownedWindowList.elementAt(b)).get();
          if (window != null && window.showWithParent) {
            window.show();
            window.showWithParent = false;
          } 
        } 
        Window.updateChildFocusableWindowState(this);
        createHierarchyEvents(1400, this, this.parent, 4L, Toolkit.enabledOnToolkit(32768L));
        if (this.componentListener != null || (this.eventMask & 0x1L) != 0L || Toolkit.enabledOnToolkit(1L)) {
          ComponentEvent componentEvent = new ComponentEvent(this, 102);
          Toolkit.getEventQueue().postEvent(componentEvent);
        } 
      } 
    } 
    if (bool && (this.state & true) == 0) {
      postWindowEvent(200);
      this.state |= 0x1;
    } 
    return bool;
  }
  
  public void setVisible(boolean paramBoolean) { super.setVisible(paramBoolean); }
  
  @Deprecated
  public void show() {
    if (!this.initialized)
      throw new IllegalStateException("The dialog component has not been initialized properly"); 
    this.beforeFirstShow = false;
    if (!isModal()) {
      conditionalShow(null, null);
    } else {
      AppContext appContext = AppContext.getAppContext();
      atomicLong = new AtomicLong();
      component = null;
      try {
        component = getMostRecentFocusOwner();
        if (conditionalShow(component, atomicLong)) {
          this.modalFilter = ModalEventFilter.createFilterForDialog(this);
          Conditional conditional = new Conditional() {
              public boolean evaluate() { return (Dialog.this.windowClosingException == null); }
            };
          if (this.modalityType == ModalityType.TOOLKIT_MODAL)
            for (AppContext appContext1 : AppContext.getAppContexts()) {
              if (appContext1 == appContext)
                continue; 
              EventQueue eventQueue = (EventQueue)appContext1.get(AppContext.EVENT_QUEUE_KEY);
              Runnable runnable = new Runnable() {
                  public void run() {}
                };
              eventQueue.postEvent(new InvocationEvent(this, runnable));
              EventDispatchThread eventDispatchThread = eventQueue.getDispatchThread();
              eventDispatchThread.addEventFilter(this.modalFilter);
            }  
          modalityPushed();
          try {
            EventQueue eventQueue = (EventQueue)AccessController.doPrivileged(new PrivilegedAction<EventQueue>() {
                  public EventQueue run() { return Toolkit.getDefaultToolkit().getSystemEventQueue(); }
                });
            this.secondaryLoop = eventQueue.createSecondaryLoop(conditional, this.modalFilter, 0L);
            if (!this.secondaryLoop.enter())
              this.secondaryLoop = null; 
          } finally {
            modalityPopped();
          } 
          if (this.modalityType == ModalityType.TOOLKIT_MODAL)
            for (AppContext appContext1 : AppContext.getAppContexts()) {
              if (appContext1 == appContext)
                continue; 
              EventQueue eventQueue = (EventQueue)appContext1.get(AppContext.EVENT_QUEUE_KEY);
              EventDispatchThread eventDispatchThread = eventQueue.getDispatchThread();
              eventDispatchThread.removeEventFilter(this.modalFilter);
            }  
          if (this.windowClosingException != null) {
            this.windowClosingException.fillInStackTrace();
            throw this.windowClosingException;
          } 
        } 
      } finally {
        if (component != null)
          KeyboardFocusManager.getCurrentKeyboardFocusManager().dequeueKeyEvents(atomicLong.get(), component); 
      } 
    } 
  }
  
  final void modalityPushed() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    if (toolkit instanceof SunToolkit) {
      SunToolkit sunToolkit = (SunToolkit)toolkit;
      sunToolkit.notifyModalityPushed(this);
    } 
  }
  
  final void modalityPopped() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    if (toolkit instanceof SunToolkit) {
      SunToolkit sunToolkit = (SunToolkit)toolkit;
      sunToolkit.notifyModalityPopped(this);
    } 
  }
  
  void interruptBlocking() {
    if (isModal()) {
      disposeImpl();
    } else if (this.windowClosingException != null) {
      this.windowClosingException.fillInStackTrace();
      this.windowClosingException.printStackTrace();
      this.windowClosingException = null;
    } 
  }
  
  private void hideAndDisposePreHandler() {
    this.isInHide = true;
    synchronized (getTreeLock()) {
      if (this.secondaryLoop != null) {
        modalHide();
        if (this.modalFilter != null)
          this.modalFilter.disable(); 
        modalDialogs.remove(this);
      } 
    } 
  }
  
  private void hideAndDisposeHandler() {
    if (this.secondaryLoop != null) {
      this.secondaryLoop.exit();
      this.secondaryLoop = null;
    } 
    this.isInHide = false;
  }
  
  @Deprecated
  public void hide() {
    hideAndDisposePreHandler();
    super.hide();
    if (!this.isInDispose)
      hideAndDisposeHandler(); 
  }
  
  void doDispose() {
    this.isInDispose = true;
    super.doDispose();
    hideAndDisposeHandler();
    this.isInDispose = false;
  }
  
  public void toBack() {
    super.toBack();
    if (this.visible)
      synchronized (getTreeLock()) {
        for (Window window : this.blockedWindows)
          window.toBack_NoClientCode(); 
      }  
  }
  
  public boolean isResizable() { return this.resizable; }
  
  public void setResizable(boolean paramBoolean) {
    boolean bool = false;
    synchronized (this) {
      this.resizable = paramBoolean;
      DialogPeer dialogPeer = (DialogPeer)this.peer;
      if (dialogPeer != null) {
        dialogPeer.setResizable(paramBoolean);
        bool = true;
      } 
    } 
    if (bool)
      invalidateIfValid(); 
  }
  
  public void setUndecorated(boolean paramBoolean) {
    synchronized (getTreeLock()) {
      if (isDisplayable())
        throw new IllegalComponentStateException("The dialog is displayable."); 
      if (!paramBoolean) {
        if (getOpacity() < 1.0F)
          throw new IllegalComponentStateException("The dialog is not opaque"); 
        if (getShape() != null)
          throw new IllegalComponentStateException("The dialog does not have a default shape"); 
        Color color = getBackground();
        if (color != null && color.getAlpha() < 255)
          throw new IllegalComponentStateException("The dialog background color is not opaque"); 
      } 
      this.undecorated = paramBoolean;
    } 
  }
  
  public boolean isUndecorated() { return this.undecorated; }
  
  public void setOpacity(float paramFloat) {
    synchronized (getTreeLock()) {
      if (paramFloat < 1.0F && !isUndecorated())
        throw new IllegalComponentStateException("The dialog is decorated"); 
      super.setOpacity(paramFloat);
    } 
  }
  
  public void setShape(Shape paramShape) {
    synchronized (getTreeLock()) {
      if (paramShape != null && !isUndecorated())
        throw new IllegalComponentStateException("The dialog is decorated"); 
      super.setShape(paramShape);
    } 
  }
  
  public void setBackground(Color paramColor) {
    synchronized (getTreeLock()) {
      if (paramColor != null && paramColor.getAlpha() < 255 && !isUndecorated())
        throw new IllegalComponentStateException("The dialog is decorated"); 
      super.setBackground(paramColor);
    } 
  }
  
  protected String paramString() {
    String str = super.paramString() + "," + this.modalityType;
    if (this.title != null)
      str = str + ",title=" + this.title; 
    return str;
  }
  
  private static native void initIDs();
  
  void modalShow() {
    IdentityArrayList identityArrayList1 = new IdentityArrayList();
    for (Dialog dialog : modalDialogs) {
      if (dialog.shouldBlock(this)) {
        Window window = dialog;
        while (window != null && window != this)
          window = window.getOwner_NoClientCode(); 
        if (window == this || !shouldBlock(dialog) || this.modalityType.compareTo(dialog.getModalityType()) < 0)
          identityArrayList1.add(dialog); 
      } 
    } 
    for (byte b1 = 0; b1 < identityArrayList1.size(); b1++) {
      Dialog dialog = (Dialog)identityArrayList1.get(b1);
      if (dialog.isModalBlocked()) {
        Dialog dialog1 = dialog.getModalBlocker();
        if (!identityArrayList1.contains(dialog1))
          identityArrayList1.add(b1 + 1, dialog1); 
      } 
    } 
    if (identityArrayList1.size() > 0)
      ((Dialog)identityArrayList1.get(0)).blockWindow(this); 
    IdentityArrayList identityArrayList2 = new IdentityArrayList(identityArrayList1);
    for (byte b2 = 0; b2 < identityArrayList2.size(); b2++) {
      Window window = (Window)identityArrayList2.get(b2);
      Window[] arrayOfWindow = window.getOwnedWindows_NoClientCode();
      for (Window window1 : arrayOfWindow)
        identityArrayList2.add(window1); 
    } 
    IdentityLinkedList identityLinkedList = new IdentityLinkedList();
    IdentityArrayList identityArrayList3 = Window.getAllUnblockedWindows();
    for (Window window : identityArrayList3) {
      if (shouldBlock(window) && !identityArrayList2.contains(window)) {
        if (window instanceof Dialog && ((Dialog)window).isModal_NoClientCode()) {
          Dialog dialog = (Dialog)window;
          if (dialog.shouldBlock(this) && modalDialogs.indexOf(dialog) > modalDialogs.indexOf(this))
            continue; 
        } 
        identityLinkedList.add(window);
      } 
    } 
    blockWindows(identityLinkedList);
    if (!isModalBlocked())
      updateChildrenBlocking(); 
  }
  
  void modalHide() {
    IdentityArrayList identityArrayList = new IdentityArrayList();
    int i = this.blockedWindows.size();
    byte b;
    for (b = 0; b < i; b++) {
      Window window = (Window)this.blockedWindows.get(0);
      identityArrayList.add(window);
      unblockWindow(window);
    } 
    for (b = 0; b < i; b++) {
      Window window = (Window)identityArrayList.get(b);
      if (window instanceof Dialog && ((Dialog)window).isModal_NoClientCode()) {
        Dialog dialog = (Dialog)window;
        dialog.modalShow();
      } else {
        checkShouldBeBlocked(window);
      } 
    } 
  }
  
  boolean shouldBlock(Window paramWindow) {
    if (!isVisible_NoClientCode() || (!paramWindow.isVisible_NoClientCode() && !paramWindow.isInShow) || this.isInHide || paramWindow == this || !isModal_NoClientCode())
      return false; 
    if (paramWindow instanceof Dialog && ((Dialog)paramWindow).isInHide)
      return false; 
    for (Dialog dialog = this; dialog != null; dialog = dialog.getModalBlocker()) {
      Container container = paramWindow;
      while (container != null && container != dialog)
        container = container.getParent_NoClientCode(); 
      if (container == dialog)
        return false; 
    } 
    switch (this.modalityType) {
      case MODELESS:
        return false;
      case DOCUMENT_MODAL:
        if (paramWindow.isModalExcluded(ModalExclusionType.APPLICATION_EXCLUDE)) {
          Container container = this;
          while (container != null && container != paramWindow)
            container = container.getParent_NoClientCode(); 
          return (container == paramWindow);
        } 
        return (getDocumentRoot() == paramWindow.getDocumentRoot());
      case APPLICATION_MODAL:
        return (!paramWindow.isModalExcluded(ModalExclusionType.APPLICATION_EXCLUDE) && this.appContext == paramWindow.appContext);
      case TOOLKIT_MODAL:
        return !paramWindow.isModalExcluded(ModalExclusionType.TOOLKIT_EXCLUDE);
    } 
    return false;
  }
  
  void blockWindow(Window paramWindow) {
    if (!paramWindow.isModalBlocked()) {
      paramWindow.setModalBlocked(this, true, true);
      this.blockedWindows.add(paramWindow);
    } 
  }
  
  void blockWindows(List<Window> paramList) {
    DialogPeer dialogPeer = (DialogPeer)this.peer;
    if (dialogPeer == null)
      return; 
    Iterator iterator = paramList.iterator();
    while (iterator.hasNext()) {
      Window window = (Window)iterator.next();
      if (!window.isModalBlocked()) {
        window.setModalBlocked(this, true, false);
        continue;
      } 
      iterator.remove();
    } 
    dialogPeer.blockWindows(paramList);
    this.blockedWindows.addAll(paramList);
  }
  
  void unblockWindow(Window paramWindow) {
    if (paramWindow.isModalBlocked() && this.blockedWindows.contains(paramWindow)) {
      this.blockedWindows.remove(paramWindow);
      paramWindow.setModalBlocked(this, false, true);
    } 
  }
  
  static void checkShouldBeBlocked(Window paramWindow) {
    synchronized (paramWindow.getTreeLock()) {
      for (byte b = 0; b < modalDialogs.size(); b++) {
        Dialog dialog = (Dialog)modalDialogs.get(b);
        if (dialog.shouldBlock(paramWindow)) {
          dialog.blockWindow(paramWindow);
          break;
        } 
      } 
    } 
  }
  
  private void checkModalityPermission(ModalityType paramModalityType) {
    if (paramModalityType == ModalityType.TOOLKIT_MODAL) {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkPermission(SecurityConstants.AWT.TOOLKIT_MODALITY_PERMISSION); 
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
    GraphicsEnvironment.checkHeadless();
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    ModalityType modalityType1 = (ModalityType)getField.get("modalityType", null);
    try {
      checkModalityPermission(modalityType1);
    } catch (AccessControlException accessControlException) {
      modalityType1 = DEFAULT_MODALITY_TYPE;
    } 
    if (modalityType1 == null) {
      this.modal = getField.get("modal", false);
      setModal(this.modal);
    } else {
      this.modalityType = modalityType1;
    } 
    this.resizable = getField.get("resizable", true);
    this.undecorated = getField.get("undecorated", false);
    this.title = (String)getField.get("title", "");
    this.blockedWindows = new IdentityArrayList();
    SunToolkit.checkAndSetPolicy(this);
    this.initialized = true;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleAWTDialog(); 
    return this.accessibleContext;
  }
  
  static  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
    DEFAULT_MODALITY_TYPE = ModalityType.APPLICATION_MODAL;
    modalDialogs = new IdentityArrayList();
    nameCounter = 0;
  }
  
  protected class AccessibleAWTDialog extends Window.AccessibleAWTWindow {
    private static final long serialVersionUID = 4837230331833941201L;
    
    protected AccessibleAWTDialog() { super(Dialog.this); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.DIALOG; }
    
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
      if (Dialog.this.getFocusOwner() != null)
        accessibleStateSet.add(AccessibleState.ACTIVE); 
      if (Dialog.this.isModal())
        accessibleStateSet.add(AccessibleState.MODAL); 
      if (Dialog.this.isResizable())
        accessibleStateSet.add(AccessibleState.RESIZABLE); 
      return accessibleStateSet;
    }
  }
  
  public enum ModalExclusionType {
    NO_EXCLUDE, APPLICATION_EXCLUDE, TOOLKIT_EXCLUDE;
  }
  
  public enum ModalityType {
    MODELESS, DOCUMENT_MODAL, APPLICATION_MODAL, TOOLKIT_MODAL;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\Dialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */