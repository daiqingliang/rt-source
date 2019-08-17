package sun.nio.ch;

import java.nio.channels.AsynchronousChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ShutdownChannelGroupException;
import java.security.AccessController;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import sun.misc.InnocuousThread;
import sun.security.action.GetIntegerAction;

class Invoker {
  private static final int maxHandlerInvokeCount = ((Integer)AccessController.doPrivileged(new GetIntegerAction("sun.nio.ch.maxCompletionHandlersOnStack", 16))).intValue();
  
  private static final ThreadLocal<GroupAndInvokeCount> myGroupAndInvokeCount = new ThreadLocal<GroupAndInvokeCount>() {
      protected Invoker.GroupAndInvokeCount initialValue() { return null; }
    };
  
  static void bindToGroup(AsynchronousChannelGroupImpl paramAsynchronousChannelGroupImpl) { myGroupAndInvokeCount.set(new GroupAndInvokeCount(paramAsynchronousChannelGroupImpl)); }
  
  static GroupAndInvokeCount getGroupAndInvokeCount() { return (GroupAndInvokeCount)myGroupAndInvokeCount.get(); }
  
  static boolean isBoundToAnyGroup() { return (myGroupAndInvokeCount.get() != null); }
  
  static boolean mayInvokeDirect(GroupAndInvokeCount paramGroupAndInvokeCount, AsynchronousChannelGroupImpl paramAsynchronousChannelGroupImpl) { return (paramGroupAndInvokeCount != null && paramGroupAndInvokeCount.group() == paramAsynchronousChannelGroupImpl && paramGroupAndInvokeCount.invokeCount() < maxHandlerInvokeCount); }
  
  static <V, A> void invokeUnchecked(CompletionHandler<V, ? super A> paramCompletionHandler, A paramA, V paramV, Throwable paramThrowable) {
    if (paramThrowable == null) {
      paramCompletionHandler.completed(paramV, paramA);
    } else {
      paramCompletionHandler.failed(paramThrowable, paramA);
    } 
    Thread.interrupted();
    if (System.getSecurityManager() != null) {
      Thread thread = Thread.currentThread();
      if (thread instanceof InnocuousThread) {
        GroupAndInvokeCount groupAndInvokeCount = (GroupAndInvokeCount)myGroupAndInvokeCount.get();
        ((InnocuousThread)thread).eraseThreadLocals();
        if (groupAndInvokeCount != null)
          myGroupAndInvokeCount.set(groupAndInvokeCount); 
      } 
    } 
  }
  
  static <V, A> void invokeDirect(GroupAndInvokeCount paramGroupAndInvokeCount, CompletionHandler<V, ? super A> paramCompletionHandler, A paramA, V paramV, Throwable paramThrowable) {
    paramGroupAndInvokeCount.incrementInvokeCount();
    invokeUnchecked(paramCompletionHandler, paramA, paramV, paramThrowable);
  }
  
  static <V, A> void invoke(AsynchronousChannel paramAsynchronousChannel, CompletionHandler<V, ? super A> paramCompletionHandler, A paramA, V paramV, Throwable paramThrowable) {
    boolean bool1 = false;
    boolean bool2 = false;
    GroupAndInvokeCount groupAndInvokeCount = (GroupAndInvokeCount)myGroupAndInvokeCount.get();
    if (groupAndInvokeCount != null) {
      if (groupAndInvokeCount.group() == ((Groupable)paramAsynchronousChannel).group())
        bool2 = true; 
      if (bool2 && groupAndInvokeCount.invokeCount() < maxHandlerInvokeCount)
        bool1 = true; 
    } 
    if (bool1) {
      invokeDirect(groupAndInvokeCount, paramCompletionHandler, paramA, paramV, paramThrowable);
    } else {
      try {
        invokeIndirectly(paramAsynchronousChannel, paramCompletionHandler, paramA, paramV, paramThrowable);
      } catch (RejectedExecutionException rejectedExecutionException) {
        if (bool2) {
          invokeDirect(groupAndInvokeCount, paramCompletionHandler, paramA, paramV, paramThrowable);
        } else {
          throw new ShutdownChannelGroupException();
        } 
      } 
    } 
  }
  
  static <V, A> void invokeIndirectly(AsynchronousChannel paramAsynchronousChannel, final CompletionHandler<V, ? super A> handler, final A attachment, final V result, final Throwable exc) {
    try {
      ((Groupable)paramAsynchronousChannel).group().executeOnPooledThread(new Runnable() {
            public void run() {
              Invoker.GroupAndInvokeCount groupAndInvokeCount = (Invoker.GroupAndInvokeCount)myGroupAndInvokeCount.get();
              if (groupAndInvokeCount != null)
                groupAndInvokeCount.setInvokeCount(1); 
              Invoker.invokeUnchecked(handler, attachment, result, exc);
            }
          });
    } catch (RejectedExecutionException rejectedExecutionException) {
      throw new ShutdownChannelGroupException();
    } 
  }
  
  static <V, A> void invokeIndirectly(final CompletionHandler<V, ? super A> handler, final A attachment, final V value, final Throwable exc, Executor paramExecutor) {
    try {
      paramExecutor.execute(new Runnable() {
            public void run() { Invoker.invokeUnchecked(handler, attachment, value, exc); }
          });
    } catch (RejectedExecutionException rejectedExecutionException) {
      throw new ShutdownChannelGroupException();
    } 
  }
  
  static void invokeOnThreadInThreadPool(Groupable paramGroupable, Runnable paramRunnable) {
    boolean bool;
    GroupAndInvokeCount groupAndInvokeCount = (GroupAndInvokeCount)myGroupAndInvokeCount.get();
    AsynchronousChannelGroupImpl asynchronousChannelGroupImpl = paramGroupable.group();
    if (groupAndInvokeCount == null) {
      bool = false;
    } else {
      bool = (groupAndInvokeCount.group == asynchronousChannelGroupImpl) ? 1 : 0;
    } 
    try {
      if (bool) {
        paramRunnable.run();
      } else {
        asynchronousChannelGroupImpl.executeOnPooledThread(paramRunnable);
      } 
    } catch (RejectedExecutionException rejectedExecutionException) {
      throw new ShutdownChannelGroupException();
    } 
  }
  
  static <V, A> void invokeUnchecked(PendingFuture<V, A> paramPendingFuture) {
    assert paramPendingFuture.isDone();
    CompletionHandler completionHandler = paramPendingFuture.handler();
    if (completionHandler != null)
      invokeUnchecked(completionHandler, paramPendingFuture.attachment(), paramPendingFuture.value(), paramPendingFuture.exception()); 
  }
  
  static <V, A> void invoke(PendingFuture<V, A> paramPendingFuture) {
    assert paramPendingFuture.isDone();
    CompletionHandler completionHandler = paramPendingFuture.handler();
    if (completionHandler != null)
      invoke(paramPendingFuture.channel(), completionHandler, paramPendingFuture.attachment(), paramPendingFuture.value(), paramPendingFuture.exception()); 
  }
  
  static <V, A> void invokeIndirectly(PendingFuture<V, A> paramPendingFuture) {
    assert paramPendingFuture.isDone();
    CompletionHandler completionHandler = paramPendingFuture.handler();
    if (completionHandler != null)
      invokeIndirectly(paramPendingFuture.channel(), completionHandler, paramPendingFuture.attachment(), paramPendingFuture.value(), paramPendingFuture.exception()); 
  }
  
  static class GroupAndInvokeCount {
    private final AsynchronousChannelGroupImpl group;
    
    private int handlerInvokeCount;
    
    GroupAndInvokeCount(AsynchronousChannelGroupImpl param1AsynchronousChannelGroupImpl) { this.group = param1AsynchronousChannelGroupImpl; }
    
    AsynchronousChannelGroupImpl group() { return this.group; }
    
    int invokeCount() { return this.handlerInvokeCount; }
    
    void setInvokeCount(int param1Int) { this.handlerInvokeCount = param1Int; }
    
    void resetInvokeCount() { this.handlerInvokeCount = 0; }
    
    void incrementInvokeCount() { this.handlerInvokeCount++; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\Invoker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */