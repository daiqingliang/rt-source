package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.Cancelable;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.ComponentRegistry;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

public final class Fiber implements Runnable, Cancelable, ComponentRegistry {
  private final List<Listener> _listeners = new ArrayList();
  
  private Tube[] conts = new Tube[16];
  
  private int contsSize;
  
  private Tube next;
  
  private Packet packet;
  
  private Throwable throwable;
  
  public final Engine owner;
  
  private boolean synchronous;
  
  private boolean interrupted;
  
  private final int id;
  
  private List<FiberContextSwitchInterceptor> interceptors;
  
  @Nullable
  private ClassLoader contextClassLoader;
  
  @Nullable
  private CompletionCallback completionCallback;
  
  private boolean isDeliverThrowableInPacket = false;
  
  private Thread currentThread;
  
  private final ReentrantLock lock = new ReentrantLock();
  
  private final Condition condition = this.lock.newCondition();
  
  private boolean started;
  
  private boolean startedSync;
  
  private static final PlaceholderTube PLACEHOLDER = new PlaceholderTube(null);
  
  private static final ThreadLocal<Fiber> CURRENT_FIBER = new ThreadLocal();
  
  private static final AtomicInteger iotaGen = new AtomicInteger();
  
  private static final Logger LOGGER = Logger.getLogger(Fiber.class.getName());
  
  private static final ReentrantLock serializedExecutionLock = new ReentrantLock();
  
  private final Set<Component> components = new CopyOnWriteArraySet();
  
  public void addListener(Listener paramListener) {
    synchronized (this._listeners) {
      if (!this._listeners.contains(paramListener))
        this._listeners.add(paramListener); 
    } 
  }
  
  public void removeListener(Listener paramListener) {
    synchronized (this._listeners) {
      this._listeners.remove(paramListener);
    } 
  }
  
  List<Listener> getCurrentListeners() {
    synchronized (this._listeners) {
      return new ArrayList(this._listeners);
    } 
  }
  
  private void clearListeners() {
    synchronized (this._listeners) {
      this._listeners.clear();
    } 
  }
  
  public void setDeliverThrowableInPacket(boolean paramBoolean) { this.isDeliverThrowableInPacket = paramBoolean; }
  
  Fiber(Engine paramEngine) {
    this.owner = paramEngine;
    this.id = iotaGen.incrementAndGet();
    if (isTraceEnabled())
      LOGGER.log(Level.FINE, "{0} created", getName()); 
    this.contextClassLoader = Thread.currentThread().getContextClassLoader();
  }
  
  public void start(@NotNull Tube paramTube, @NotNull Packet paramPacket, @Nullable CompletionCallback paramCompletionCallback) { start(paramTube, paramPacket, paramCompletionCallback, false); }
  
  private void dumpFiberContext(String paramString) {
    if (isTraceEnabled()) {
      String str4;
      String str3;
      String str1 = null;
      String str2 = null;
      if (this.packet != null)
        for (SOAPVersion sOAPVersion : SOAPVersion.values()) {
          for (AddressingVersion addressingVersion : AddressingVersion.values()) {
            str1 = (this.packet.getMessage() != null) ? AddressingUtils.getAction(this.packet.getMessage().getHeaders(), addressingVersion, sOAPVersion) : null;
            str2 = (this.packet.getMessage() != null) ? AddressingUtils.getMessageID(this.packet.getMessage().getHeaders(), addressingVersion, sOAPVersion) : null;
            if (str1 != null || str2 != null)
              break; 
          } 
          if (str1 != null || str2 != null)
            break; 
        }  
      if (str1 == null && str2 == null) {
        str3 = "NO ACTION or MSG ID";
      } else {
        str3 = "'" + str1 + "' and msgId '" + str2 + "'";
      } 
      if (this.next != null) {
        str4 = this.next.toString() + ".processRequest()";
      } else {
        str4 = peekCont() + ".processResponse()";
      } 
      LOGGER.log(Level.FINE, "{0} {1} with {2} and ''current'' tube {3} from thread {4} with Packet: {5}", new Object[] { getName(), paramString, str3, str4, Thread.currentThread().getName(), (this.packet != null) ? this.packet.toShortString() : null });
    } 
  }
  
  public void start(@NotNull Tube paramTube, @NotNull Packet paramPacket, @Nullable CompletionCallback paramCompletionCallback, boolean paramBoolean) {
    this.next = paramTube;
    this.packet = paramPacket;
    this.completionCallback = paramCompletionCallback;
    if (paramBoolean) {
      this.startedSync = true;
      dumpFiberContext("starting (sync)");
      run();
    } else {
      this.started = true;
      dumpFiberContext("starting (async)");
      this.owner.addRunnable(this);
    } 
  }
  
  public void resume(@NotNull Packet paramPacket) { resume(paramPacket, false); }
  
  public void resume(@NotNull Packet paramPacket, boolean paramBoolean) { resume(paramPacket, paramBoolean, null); }
  
  public void resume(@NotNull Packet paramPacket, boolean paramBoolean, CompletionCallback paramCompletionCallback) {
    this.lock.lock();
    try {
      if (paramCompletionCallback != null)
        setCompletionCallback(paramCompletionCallback); 
      if (isTraceEnabled())
        LOGGER.log(Level.FINE, "{0} resuming. Will have suspendedCount={1}", new Object[] { getName(), Integer.valueOf(this.suspendedCount - 1) }); 
      this.packet = paramPacket;
      if (--this.suspendedCount == 0) {
        if (!this.isInsideSuspendCallbacks) {
          List list = getCurrentListeners();
          for (Listener listener : list) {
            try {
              listener.fiberResumed(this);
            } catch (Throwable throwable1) {
              if (isTraceEnabled())
                LOGGER.log(Level.FINE, "Listener {0} threw exception: {1}", new Object[] { listener, throwable1.getMessage() }); 
            } 
          } 
          if (this.synchronous) {
            this.condition.signalAll();
          } else if (paramBoolean || this.startedSync) {
            run();
          } else {
            dumpFiberContext("resuming (async)");
            this.owner.addRunnable(this);
          } 
        } 
      } else if (isTraceEnabled()) {
        LOGGER.log(Level.FINE, "{0} taking no action on resume because suspendedCount != 0: {1}", new Object[] { getName(), Integer.valueOf(this.suspendedCount) });
      } 
    } finally {
      this.lock.unlock();
    } 
  }
  
  public void resumeAndReturn(@NotNull Packet paramPacket, boolean paramBoolean) {
    if (isTraceEnabled())
      LOGGER.log(Level.FINE, "{0} resumed with Return Packet", getName()); 
    this.next = null;
    resume(paramPacket, paramBoolean);
  }
  
  public void resume(@NotNull Throwable paramThrowable) { resume(paramThrowable, this.packet, false); }
  
  public void resume(@NotNull Throwable paramThrowable, @NotNull Packet paramPacket) { resume(paramThrowable, paramPacket, false); }
  
  public void resume(@NotNull Throwable paramThrowable, boolean paramBoolean) { resume(paramThrowable, this.packet, paramBoolean); }
  
  public void resume(@NotNull Throwable paramThrowable, @NotNull Packet paramPacket, boolean paramBoolean) {
    if (isTraceEnabled())
      LOGGER.log(Level.FINE, "{0} resumed with Return Throwable", getName()); 
    this.next = null;
    this.throwable = paramThrowable;
    resume(paramPacket, paramBoolean);
  }
  
  public void cancel(boolean paramBoolean) {
    this.isCanceled = true;
    if (paramBoolean)
      synchronized (this) {
        if (this.currentThread != null)
          this.currentThread.interrupt(); 
      }  
  }
  
  private boolean suspend(Holder<Boolean> paramHolder, Runnable paramRunnable) {
    if (isTraceEnabled()) {
      LOGGER.log(Level.FINE, "{0} suspending. Will have suspendedCount={1}", new Object[] { getName(), Integer.valueOf(this.suspendedCount + 1) });
      if (this.suspendedCount > 0)
        LOGGER.log(Level.FINE, "WARNING - {0} suspended more than resumed. Will require more than one resume to actually resume this fiber.", getName()); 
    } 
    List list = getCurrentListeners();
    if (++this.suspendedCount == 1) {
      this.isInsideSuspendCallbacks = true;
      try {
        for (Listener listener : list) {
          try {
            listener.fiberSuspended(this);
          } catch (Throwable throwable1) {
            if (isTraceEnabled())
              LOGGER.log(Level.FINE, "Listener {0} threw exception: {1}", new Object[] { listener, throwable1.getMessage() }); 
          } 
        } 
      } finally {
        this.isInsideSuspendCallbacks = false;
      } 
    } 
    if (this.suspendedCount <= 0) {
      for (Listener listener : list) {
        try {
          listener.fiberResumed(this);
        } catch (Throwable throwable1) {
          if (isTraceEnabled())
            LOGGER.log(Level.FINE, "Listener {0} threw exception: {1}", new Object[] { listener, throwable1.getMessage() }); 
        } 
      } 
    } else if (paramRunnable != null) {
      if (!this.synchronous) {
        synchronized (this) {
          this.currentThread = null;
        } 
        this.lock.unlock();
        assert !this.lock.isHeldByCurrentThread();
        paramHolder.value = Boolean.FALSE;
        try {
          paramRunnable.run();
        } catch (Throwable throwable1) {
          throw new OnExitRunnableException(throwable1);
        } 
        return true;
      } 
      if (isTraceEnabled())
        LOGGER.fine("onExitRunnable used with synchronous Fiber execution -- not exiting current thread"); 
      paramRunnable.run();
    } 
    return false;
  }
  
  public void addInterceptor(@NotNull FiberContextSwitchInterceptor paramFiberContextSwitchInterceptor) {
    if (this.interceptors == null) {
      this.interceptors = new ArrayList();
    } else {
      ArrayList arrayList = new ArrayList();
      arrayList.addAll(this.interceptors);
      this.interceptors = arrayList;
    } 
    this.interceptors.add(paramFiberContextSwitchInterceptor);
  }
  
  public boolean removeInterceptor(@NotNull FiberContextSwitchInterceptor paramFiberContextSwitchInterceptor) {
    if (this.interceptors != null) {
      boolean bool = this.interceptors.remove(paramFiberContextSwitchInterceptor);
      if (this.interceptors.isEmpty()) {
        this.interceptors = null;
      } else {
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(this.interceptors);
        this.interceptors = arrayList;
      } 
      return bool;
    } 
    return false;
  }
  
  @Nullable
  public ClassLoader getContextClassLoader() { return this.contextClassLoader; }
  
  public ClassLoader setContextClassLoader(@Nullable ClassLoader paramClassLoader) {
    ClassLoader classLoader = this.contextClassLoader;
    this.contextClassLoader = paramClassLoader;
    return classLoader;
  }
  
  @Deprecated
  public void run() {
    container = ContainerResolver.getDefault().enterContainer(this.owner.getContainer());
    try {
      assert !this.synchronous;
      if (!doRun())
        if (this.startedSync && this.suspendedCount == 0 && (this.next != null || this.contsSize > 0)) {
          this.startedSync = false;
          dumpFiberContext("restarting (async) after startSync");
          this.owner.addRunnable(this);
        } else {
          completionCheck();
        }  
    } finally {
      ContainerResolver.getDefault().exitContainer(container);
    } 
  }
  
  @NotNull
  public Packet runSync(@NotNull Tube paramTube, @NotNull Packet paramPacket) {
    this.lock.lock();
    try {
      arrayOfTube = this.conts;
      i = this.contsSize;
      bool = this.synchronous;
      tube = this.next;
      if (i > 0) {
        this.conts = new Tube[16];
        this.contsSize = 0;
      } 
    } finally {
      this.lock.unlock();
    } 
  }
  
  private void completionCheck() {
    this.lock.lock();
    try {
      if (!this.isCanceled && this.contsSize == 0 && this.suspendedCount == 0) {
        if (isTraceEnabled())
          LOGGER.log(Level.FINE, "{0} completed", getName()); 
        clearListeners();
        this.condition.signalAll();
        if (this.completionCallback != null)
          if (this.throwable != null) {
            if (this.isDeliverThrowableInPacket) {
              this.packet.addSatellite(new ThrowableContainerPropertySet(this.throwable));
              this.completionCallback.onCompletion(this.packet);
            } else {
              this.completionCallback.onCompletion(this.throwable);
            } 
          } else {
            this.completionCallback.onCompletion(this.packet);
          }  
      } 
    } finally {
      this.lock.unlock();
    } 
  }
  
  private boolean doRun() {
    dumpFiberContext("running");
    if (serializeExecution) {
      serializedExecutionLock.lock();
      try {
        return _doRun(this.next);
      } finally {
        serializedExecutionLock.unlock();
      } 
    } 
    return _doRun(this.next);
  }
  
  private boolean _doRun(Tube paramTube) {
    holder = new Holder(Boolean.TRUE);
    this.lock.lock();
    try {
      List list;
      synchronized (this) {
        list = this.interceptors;
        this.currentThread = Thread.currentThread();
        if (isTraceEnabled())
          LOGGER.log(Level.FINE, "Thread entering _doRun(): {0}", this.currentThread); 
        classLoader = this.currentThread.getContextClassLoader();
        this.currentThread.setContextClassLoader(this.contextClassLoader);
      } 
      try {
        boolean bool;
        do {
          if (list == null) {
            this.next = paramTube;
            if (__doRun(holder, null))
              return true; 
          } else {
            paramTube = (new InterceptorHandler(holder, list)).invoke(paramTube);
            if (paramTube == PLACEHOLDER)
              return true; 
          } 
          synchronized (this) {
            bool = (list != this.interceptors) ? 1 : 0;
            if (bool)
              list = this.interceptors; 
          } 
        } while (bool);
      } catch (OnExitRunnableException onExitRunnableException) {
        Throwable throwable1 = onExitRunnableException.target;
        if (throwable1 instanceof WebServiceException)
          throw (WebServiceException)throwable1; 
        throw new WebServiceException(throwable1);
      } finally {
        Thread thread = Thread.currentThread();
        thread.setContextClassLoader(classLoader);
        if (isTraceEnabled())
          LOGGER.log(Level.FINE, "Thread leaving _doRun(): {0}", thread); 
      } 
      return false;
    } finally {
      if (((Boolean)holder.value).booleanValue()) {
        synchronized (this) {
          this.currentThread = null;
        } 
        this.lock.unlock();
      } 
    } 
  }
  
  private boolean __doRun(Holder<Boolean> paramHolder, List<FiberContextSwitchInterceptor> paramList) {
    assert this.lock.isHeldByCurrentThread();
    fiber = (Fiber)CURRENT_FIBER.get();
    CURRENT_FIBER.set(this);
    boolean bool = LOGGER.isLoggable(Level.FINER);
    try {
      boolean bool1 = false;
      while (isReady(paramList)) {
        if (this.isCanceled) {
          this.next = null;
          this.throwable = null;
          this.contsSize = 0;
          break;
        } 
        try {
          Tube tube;
          NextAction nextAction;
          if (this.throwable != null) {
            if (this.contsSize == 0 || bool1) {
              this.contsSize = 0;
              return false;
            } 
            tube = popCont();
            if (bool)
              LOGGER.log(Level.FINER, "{0} {1}.processException({2})", new Object[] { getName(), tube, this.throwable }); 
            nextAction = tube.processException(this.throwable);
          } else if (this.next != null) {
            if (bool)
              LOGGER.log(Level.FINER, "{0} {1}.processRequest({2})", new Object[] { getName(), this.next, (this.packet != null) ? ("Packet@" + Integer.toHexString(this.packet.hashCode())) : "null" }); 
            nextAction = this.next.processRequest(this.packet);
            tube = this.next;
          } else {
            if (this.contsSize == 0 || bool1) {
              this.contsSize = 0;
              return false;
            } 
            tube = popCont();
            if (bool)
              LOGGER.log(Level.FINER, "{0} {1}.processResponse({2})", new Object[] { getName(), tube, (this.packet != null) ? ("Packet@" + Integer.toHexString(this.packet.hashCode())) : "null" }); 
            nextAction = tube.processResponse(this.packet);
          } 
          if (bool)
            LOGGER.log(Level.FINER, "{0} {1} returned with {2}", new Object[] { getName(), tube, nextAction }); 
          if (nextAction.kind != 4) {
            if (nextAction.kind != 3 && nextAction.kind != 5)
              this.packet = nextAction.packet; 
            this.throwable = nextAction.throwable;
          } 
          switch (nextAction.kind) {
            case 0:
            case 7:
              pushCont(tube);
            case 1:
              this.next = nextAction.next;
              if (nextAction.kind == 7 && this.startedSync)
                return false; 
              break;
            case 5:
            case 6:
              bool1 = true;
              if (isTraceEnabled())
                LOGGER.log(Level.FINE, "Fiber {0} is aborting a response due to exception: {1}", new Object[] { this, nextAction.throwable }); 
            case 2:
            case 3:
              this.next = null;
              break;
            case 4:
              if (this.next != null)
                pushCont(tube); 
              this.next = nextAction.next;
              if (suspend(paramHolder, nextAction.onExitRunnable))
                return true; 
              break;
            default:
              throw new AssertionError();
          } 
        } catch (RuntimeException runtimeException) {
          if (bool)
            LOGGER.log(Level.FINER, getName() + " Caught " + runtimeException + ". Start stack unwinding", runtimeException); 
          this.throwable = runtimeException;
        } catch (Error error) {
          if (bool)
            LOGGER.log(Level.FINER, getName() + " Caught " + error + ". Start stack unwinding", error); 
          this.throwable = error;
        } 
        dumpFiberContext("After tube execution");
      } 
    } finally {
      CURRENT_FIBER.set(fiber);
    } 
    return false;
  }
  
  private void pushCont(Tube paramTube) {
    this.conts[this.contsSize++] = paramTube;
    int i = this.conts.length;
    if (this.contsSize == i) {
      Tube[] arrayOfTube = new Tube[i * 2];
      System.arraycopy(this.conts, 0, arrayOfTube, 0, i);
      this.conts = arrayOfTube;
    } 
  }
  
  private Tube popCont() { return this.conts[--this.contsSize]; }
  
  private Tube peekCont() {
    int i = this.contsSize - 1;
    return (i >= 0 && i < this.conts.length) ? this.conts[i] : null;
  }
  
  public void resetCont(Tube[] paramArrayOfTube, int paramInt) {
    this.conts = paramArrayOfTube;
    this.contsSize = paramInt;
  }
  
  private boolean isReady(List<FiberContextSwitchInterceptor> paramList) {
    if (this.synchronous) {
      while (this.suspendedCount == 1) {
        try {
          if (isTraceEnabled())
            LOGGER.log(Level.FINE, "{0} is blocking thread {1}", new Object[] { getName(), Thread.currentThread().getName() }); 
          this.condition.await();
        } catch (InterruptedException interruptedException) {
          this.interrupted = true;
        } 
      } 
      synchronized (this) {
        return (this.interceptors == paramList);
      } 
    } 
    if (this.suspendedCount > 0)
      return false; 
    synchronized (this) {
      return (this.interceptors == paramList);
    } 
  }
  
  private String getName() { return "engine-" + this.owner.id + "fiber-" + this.id; }
  
  public String toString() { return getName(); }
  
  @Nullable
  public Packet getPacket() { return this.packet; }
  
  public CompletionCallback getCompletionCallback() { return this.completionCallback; }
  
  public void setCompletionCallback(CompletionCallback paramCompletionCallback) { this.completionCallback = paramCompletionCallback; }
  
  public static boolean isSynchronous() { return (current()).synchronous; }
  
  public boolean isStartedSync() { return this.startedSync; }
  
  @NotNull
  public static Fiber current() {
    Fiber fiber = (Fiber)CURRENT_FIBER.get();
    if (fiber == null)
      throw new IllegalStateException("Can be only used from fibers"); 
    return fiber;
  }
  
  public static Fiber getCurrentIfSet() { return (Fiber)CURRENT_FIBER.get(); }
  
  private static boolean isTraceEnabled() { return LOGGER.isLoggable(Level.FINE); }
  
  public <S> S getSPI(Class<S> paramClass) {
    for (Component component : this.components) {
      Object object = component.getSPI(paramClass);
      if (object != null)
        return (S)object; 
    } 
    return null;
  }
  
  public Set<Component> getComponents() { return this.components; }
  
  public static interface CompletionCallback {
    void onCompletion(@NotNull Packet param1Packet);
    
    void onCompletion(@NotNull Throwable param1Throwable);
  }
  
  private class InterceptorHandler extends Object implements FiberContextSwitchInterceptor.Work<Tube, Tube> {
    private final Holder<Boolean> isUnlockRequired;
    
    private final List<FiberContextSwitchInterceptor> ints;
    
    private int idx;
    
    public InterceptorHandler(Holder<Boolean> param1Holder, List<FiberContextSwitchInterceptor> param1List) {
      this.isUnlockRequired = param1Holder;
      this.ints = param1List;
    }
    
    Tube invoke(Tube param1Tube) {
      this.idx = 0;
      return execute(param1Tube);
    }
    
    public Tube execute(Tube param1Tube) {
      if (this.idx == this.ints.size()) {
        Fiber.this.next = param1Tube;
        if (Fiber.this.__doRun(this.isUnlockRequired, this.ints))
          return PLACEHOLDER; 
      } else {
        FiberContextSwitchInterceptor fiberContextSwitchInterceptor = (FiberContextSwitchInterceptor)this.ints.get(this.idx++);
        return (Tube)fiberContextSwitchInterceptor.execute(Fiber.this, param1Tube, this);
      } 
      return Fiber.this.next;
    }
  }
  
  public static interface Listener {
    void fiberSuspended(Fiber param1Fiber);
    
    void fiberResumed(Fiber param1Fiber);
  }
  
  private static final class OnExitRunnableException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    Throwable target;
    
    public OnExitRunnableException(Throwable param1Throwable) {
      super((Throwable)null);
      this.target = param1Throwable;
    }
  }
  
  private static class PlaceholderTube extends AbstractTubeImpl {
    private PlaceholderTube() {}
    
    public NextAction processRequest(Packet param1Packet) { throw new UnsupportedOperationException(); }
    
    public NextAction processResponse(Packet param1Packet) { throw new UnsupportedOperationException(); }
    
    public NextAction processException(Throwable param1Throwable) { return doThrow(param1Throwable); }
    
    public void preDestroy() {}
    
    public PlaceholderTube copy(TubeCloner param1TubeCloner) { throw new UnsupportedOperationException(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\pipe\Fiber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */