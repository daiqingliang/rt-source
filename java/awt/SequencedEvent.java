package java.awt;

import java.util.LinkedList;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.SunToolkit;

class SequencedEvent extends AWTEvent implements ActiveEvent {
  private static final long serialVersionUID = 547742659238625067L;
  
  private static final int ID = 1006;
  
  private static final LinkedList<SequencedEvent> list = new LinkedList();
  
  private final AWTEvent nested;
  
  private AppContext appContext;
  
  private boolean disposed;
  
  public SequencedEvent(AWTEvent paramAWTEvent) {
    super(paramAWTEvent.getSource(), 1006);
    this.nested = paramAWTEvent;
    SunToolkit.setSystemGenerated(paramAWTEvent);
    synchronized (SequencedEvent.class) {
      list.add(this);
    } 
  }
  
  public final void dispatch() {
    try {
      this.appContext = AppContext.getAppContext();
      if (getFirst() != this)
        if (EventQueue.isDispatchThread()) {
          EventDispatchThread eventDispatchThread = (EventDispatchThread)Thread.currentThread();
          eventDispatchThread.pumpEvents(1007, new Conditional() {
                public boolean evaluate() { return !SequencedEvent.this.isFirstOrDisposed(); }
              });
        } else {
          while (!isFirstOrDisposed()) {
            synchronized (SequencedEvent.class) {
              try {
                SequencedEvent.class.wait(1000L);
              } catch (InterruptedException interruptedException) {
                break;
              } 
            } 
          } 
        }  
      if (!this.disposed) {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().setCurrentSequencedEvent(this);
        Toolkit.getEventQueue().dispatchEvent(this.nested);
      } 
    } finally {
      dispose();
    } 
  }
  
  private static final boolean isOwnerAppContextDisposed(SequencedEvent paramSequencedEvent) {
    if (paramSequencedEvent != null) {
      Object object = paramSequencedEvent.nested.getSource();
      if (object instanceof Component)
        return ((Component)object).appContext.isDisposed(); 
    } 
    return false;
  }
  
  public final boolean isFirstOrDisposed() { return this.disposed ? true : ((this == getFirstWithContext() || this.disposed)); }
  
  private static final SequencedEvent getFirst() { return (SequencedEvent)list.getFirst(); }
  
  private static final SequencedEvent getFirstWithContext() {
    SequencedEvent sequencedEvent;
    while ((sequencedEvent = getFirst()).isOwnerAppContextDisposed(sequencedEvent)) {
      sequencedEvent.dispose();
      sequencedEvent = getFirst();
    } 
    return sequencedEvent;
  }
  
  final void dispose() {
    synchronized (SequencedEvent.class) {
      if (this.disposed)
        return; 
      if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getCurrentSequencedEvent() == this)
        KeyboardFocusManager.getCurrentKeyboardFocusManager().setCurrentSequencedEvent(null); 
      this.disposed = true;
    } 
    if (this.appContext != null)
      SunToolkit.postEvent(this.appContext, new SentEvent()); 
    SequencedEvent sequencedEvent = null;
    synchronized (SequencedEvent.class) {
      SequencedEvent.class.notifyAll();
      if (list.getFirst() == this) {
        list.removeFirst();
        if (!list.isEmpty())
          sequencedEvent = (SequencedEvent)list.getFirst(); 
      } else {
        list.remove(this);
      } 
    } 
    if (sequencedEvent != null && sequencedEvent.appContext != null)
      SunToolkit.postEvent(sequencedEvent.appContext, new SentEvent()); 
  }
  
  static  {
    AWTAccessor.setSequencedEventAccessor(new AWTAccessor.SequencedEventAccessor() {
          public AWTEvent getNested(AWTEvent param1AWTEvent) { return ((SequencedEvent)param1AWTEvent).nested; }
          
          public boolean isSequencedEvent(AWTEvent param1AWTEvent) { return param1AWTEvent instanceof SequencedEvent; }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\SequencedEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */