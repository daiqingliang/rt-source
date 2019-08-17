package java.util.concurrent;

import java.util.concurrent.locks.LockSupport;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import sun.misc.Unsafe;

public class CompletableFuture<T> extends Object implements Future<T>, CompletionStage<T> {
  static final AltResult NIL = new AltResult(null);
  
  private static final boolean useCommonPool = (ForkJoinPool.getCommonPoolParallelism() > 1);
  
  private static final Executor asyncPool = useCommonPool ? ForkJoinPool.commonPool() : new ThreadPerTaskExecutor();
  
  static final int SYNC = 0;
  
  static final int ASYNC = 1;
  
  static final int NESTED = -1;
  
  private static final Unsafe UNSAFE;
  
  private static final long RESULT;
  
  private static final long STACK;
  
  private static final long NEXT;
  
  final boolean internalComplete(Object paramObject) { return UNSAFE.compareAndSwapObject(this, RESULT, null, paramObject); }
  
  final boolean casStack(Completion paramCompletion1, Completion paramCompletion2) { return UNSAFE.compareAndSwapObject(this, STACK, paramCompletion1, paramCompletion2); }
  
  final boolean tryPushStack(Completion paramCompletion) {
    Completion completion = this.stack;
    lazySetNext(paramCompletion, completion);
    return UNSAFE.compareAndSwapObject(this, STACK, completion, paramCompletion);
  }
  
  final void pushStack(Completion paramCompletion) {
    do {
    
    } while (!tryPushStack(paramCompletion));
  }
  
  final boolean completeNull() { return UNSAFE.compareAndSwapObject(this, RESULT, null, NIL); }
  
  final Object encodeValue(T paramT) { return (paramT == null) ? NIL : paramT; }
  
  final boolean completeValue(T paramT) { return UNSAFE.compareAndSwapObject(this, RESULT, null, (paramT == null) ? NIL : paramT); }
  
  static AltResult encodeThrowable(Throwable paramThrowable) { return new AltResult((paramThrowable instanceof CompletionException) ? paramThrowable : new CompletionException(paramThrowable)); }
  
  final boolean completeThrowable(Throwable paramThrowable) { return UNSAFE.compareAndSwapObject(this, RESULT, null, encodeThrowable(paramThrowable)); }
  
  static Object encodeThrowable(Throwable paramThrowable, Object paramObject) {
    if (!(paramThrowable instanceof CompletionException)) {
      paramThrowable = new CompletionException(paramThrowable);
    } else if (paramObject instanceof AltResult && paramThrowable == ((AltResult)paramObject).ex) {
      return paramObject;
    } 
    return new AltResult(paramThrowable);
  }
  
  final boolean completeThrowable(Throwable paramThrowable, Object paramObject) { return UNSAFE.compareAndSwapObject(this, RESULT, null, encodeThrowable(paramThrowable, paramObject)); }
  
  Object encodeOutcome(T paramT, Throwable paramThrowable) { return (paramThrowable == null) ? ((paramT == null) ? NIL : paramT) : encodeThrowable(paramThrowable); }
  
  static Object encodeRelay(Object paramObject) {
    Throwable throwable;
    return (paramObject instanceof AltResult && (throwable = ((AltResult)paramObject).ex) != null && !(throwable instanceof CompletionException)) ? new AltResult(new CompletionException(throwable)) : paramObject;
  }
  
  final boolean completeRelay(Object paramObject) { return UNSAFE.compareAndSwapObject(this, RESULT, null, encodeRelay(paramObject)); }
  
  private static <T> T reportGet(Object paramObject) throws InterruptedException, ExecutionException {
    if (paramObject == null)
      throw new InterruptedException(); 
    if (paramObject instanceof AltResult) {
      Throwable throwable1;
      if ((throwable1 = ((AltResult)paramObject).ex) == null)
        return null; 
      if (throwable1 instanceof CancellationException)
        throw (CancellationException)throwable1; 
      Throwable throwable2;
      if (throwable1 instanceof CompletionException && (throwable2 = throwable1.getCause()) != null)
        throwable1 = throwable2; 
      throw new ExecutionException(throwable1);
    } 
    return (T)paramObject;
  }
  
  private static <T> T reportJoin(Object paramObject) throws InterruptedException, ExecutionException {
    if (paramObject instanceof AltResult) {
      Throwable throwable;
      if ((throwable = ((AltResult)paramObject).ex) == null)
        return null; 
      if (throwable instanceof CancellationException)
        throw (CancellationException)throwable; 
      if (throwable instanceof CompletionException)
        throw (CompletionException)throwable; 
      throw new CompletionException(throwable);
    } 
    return (T)paramObject;
  }
  
  static Executor screenExecutor(Executor paramExecutor) {
    if (!useCommonPool && paramExecutor == ForkJoinPool.commonPool())
      return asyncPool; 
    if (paramExecutor == null)
      throw new NullPointerException(); 
    return paramExecutor;
  }
  
  static void lazySetNext(Completion paramCompletion1, Completion paramCompletion2) { UNSAFE.putOrderedObject(paramCompletion1, NEXT, paramCompletion2); }
  
  final void postComplete() {
    CompletableFuture completableFuture = this;
    Completion completion;
    while ((completion = completableFuture.stack) != null || (completableFuture != this && (completion = (completableFuture = this).stack) != null)) {
      Completion completion1;
      if (completableFuture.casStack(completion, completion1 = completion.next)) {
        if (completion1 != null) {
          if (completableFuture != this) {
            pushStack(completion);
            continue;
          } 
          completion.next = null;
        } 
        CompletableFuture completableFuture1;
        completableFuture = ((completableFuture1 = completion.tryFire(-1)) == null) ? this : completableFuture1;
      } 
    } 
  }
  
  final void cleanStack() {
    Completion completion1 = null;
    for (Completion completion2 = this.stack; completion2 != null; completion2 = this.stack) {
      Completion completion = completion2.next;
      if (completion2.isLive()) {
        completion1 = completion2;
        completion2 = completion;
        continue;
      } 
      if (completion1 == null) {
        casStack(completion2, completion);
        completion2 = this.stack;
        continue;
      } 
      completion1.next = completion;
      if (completion1.isLive()) {
        completion2 = completion;
        continue;
      } 
      completion1 = null;
    } 
  }
  
  final void push(UniCompletion<?, ?> paramUniCompletion) {
    if (paramUniCompletion != null)
      while (this.result == null && !tryPushStack(paramUniCompletion))
        lazySetNext(paramUniCompletion, null);  
  }
  
  final CompletableFuture<T> postFire(CompletableFuture<?> paramCompletableFuture, int paramInt) {
    if (paramCompletableFuture != null && paramCompletableFuture.stack != null)
      if (paramInt < 0 || paramCompletableFuture.result == null) {
        paramCompletableFuture.cleanStack();
      } else {
        paramCompletableFuture.postComplete();
      }  
    if (this.result != null && this.stack != null) {
      if (paramInt < 0)
        return this; 
      postComplete();
    } 
    return null;
  }
  
  final <S> boolean uniApply(CompletableFuture<S> paramCompletableFuture, Function<? super S, ? extends T> paramFunction, UniApply<S, T> paramUniApply) { // Byte code:
    //   0: aload_1
    //   1: ifnull -> 18
    //   4: aload_1
    //   5: getfield result : Ljava/lang/Object;
    //   8: dup
    //   9: astore #4
    //   11: ifnull -> 18
    //   14: aload_2
    //   15: ifnonnull -> 20
    //   18: iconst_0
    //   19: ireturn
    //   20: aload_0
    //   21: getfield result : Ljava/lang/Object;
    //   24: ifnonnull -> 106
    //   27: aload #4
    //   29: instanceof java/util/concurrent/CompletableFuture$AltResult
    //   32: ifeq -> 64
    //   35: aload #4
    //   37: checkcast java/util/concurrent/CompletableFuture$AltResult
    //   40: getfield ex : Ljava/lang/Throwable;
    //   43: dup
    //   44: astore #5
    //   46: ifnull -> 61
    //   49: aload_0
    //   50: aload #5
    //   52: aload #4
    //   54: invokevirtual completeThrowable : (Ljava/lang/Throwable;Ljava/lang/Object;)Z
    //   57: pop
    //   58: goto -> 106
    //   61: aconst_null
    //   62: astore #4
    //   64: aload_3
    //   65: ifnull -> 77
    //   68: aload_3
    //   69: invokevirtual claim : ()Z
    //   72: ifne -> 77
    //   75: iconst_0
    //   76: ireturn
    //   77: aload #4
    //   79: astore #6
    //   81: aload_0
    //   82: aload_2
    //   83: aload #6
    //   85: invokeinterface apply : (Ljava/lang/Object;)Ljava/lang/Object;
    //   90: invokevirtual completeValue : (Ljava/lang/Object;)Z
    //   93: pop
    //   94: goto -> 106
    //   97: astore #6
    //   99: aload_0
    //   100: aload #6
    //   102: invokevirtual completeThrowable : (Ljava/lang/Throwable;)Z
    //   105: pop
    //   106: iconst_1
    //   107: ireturn
    // Exception table:
    //   from	to	target	type
    //   64	76	97	java/lang/Throwable
    //   77	94	97	java/lang/Throwable }
  
  private <V> CompletableFuture<V> uniApplyStage(Executor paramExecutor, Function<? super T, ? extends V> paramFunction) {
    if (paramFunction == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture = new CompletableFuture();
    if (paramExecutor != null || !completableFuture.uniApply(this, paramFunction, null)) {
      UniApply uniApply = new UniApply(paramExecutor, completableFuture, this, paramFunction);
      push(uniApply);
      uniApply.tryFire(0);
    } 
    return completableFuture;
  }
  
  final <S> boolean uniAccept(CompletableFuture<S> paramCompletableFuture, Consumer<? super S> paramConsumer, UniAccept<S> paramUniAccept) { // Byte code:
    //   0: aload_1
    //   1: ifnull -> 18
    //   4: aload_1
    //   5: getfield result : Ljava/lang/Object;
    //   8: dup
    //   9: astore #4
    //   11: ifnull -> 18
    //   14: aload_2
    //   15: ifnonnull -> 20
    //   18: iconst_0
    //   19: ireturn
    //   20: aload_0
    //   21: getfield result : Ljava/lang/Object;
    //   24: ifnonnull -> 106
    //   27: aload #4
    //   29: instanceof java/util/concurrent/CompletableFuture$AltResult
    //   32: ifeq -> 64
    //   35: aload #4
    //   37: checkcast java/util/concurrent/CompletableFuture$AltResult
    //   40: getfield ex : Ljava/lang/Throwable;
    //   43: dup
    //   44: astore #5
    //   46: ifnull -> 61
    //   49: aload_0
    //   50: aload #5
    //   52: aload #4
    //   54: invokevirtual completeThrowable : (Ljava/lang/Throwable;Ljava/lang/Object;)Z
    //   57: pop
    //   58: goto -> 106
    //   61: aconst_null
    //   62: astore #4
    //   64: aload_3
    //   65: ifnull -> 77
    //   68: aload_3
    //   69: invokevirtual claim : ()Z
    //   72: ifne -> 77
    //   75: iconst_0
    //   76: ireturn
    //   77: aload #4
    //   79: astore #6
    //   81: aload_2
    //   82: aload #6
    //   84: invokeinterface accept : (Ljava/lang/Object;)V
    //   89: aload_0
    //   90: invokevirtual completeNull : ()Z
    //   93: pop
    //   94: goto -> 106
    //   97: astore #6
    //   99: aload_0
    //   100: aload #6
    //   102: invokevirtual completeThrowable : (Ljava/lang/Throwable;)Z
    //   105: pop
    //   106: iconst_1
    //   107: ireturn
    // Exception table:
    //   from	to	target	type
    //   64	76	97	java/lang/Throwable
    //   77	94	97	java/lang/Throwable }
  
  private CompletableFuture<Void> uniAcceptStage(Executor paramExecutor, Consumer<? super T> paramConsumer) {
    if (paramConsumer == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture = new CompletableFuture();
    if (paramExecutor != null || !completableFuture.uniAccept(this, paramConsumer, null)) {
      UniAccept uniAccept = new UniAccept(paramExecutor, completableFuture, this, paramConsumer);
      push(uniAccept);
      uniAccept.tryFire(0);
    } 
    return completableFuture;
  }
  
  final boolean uniRun(CompletableFuture<?> paramCompletableFuture, Runnable paramRunnable, UniRun<?> paramUniRun) {
    Object object;
    if (paramCompletableFuture == null || (object = paramCompletableFuture.result) == null || paramRunnable == null)
      return false; 
    if (this.result == null) {
      Throwable throwable;
      if (object instanceof AltResult && (throwable = ((AltResult)object).ex) != null) {
        completeThrowable(throwable, object);
      } else {
        try {
          if (paramUniRun != null && !paramUniRun.claim())
            return false; 
          paramRunnable.run();
          completeNull();
        } catch (Throwable throwable1) {
          completeThrowable(throwable1);
        } 
      } 
    } 
    return true;
  }
  
  private CompletableFuture<Void> uniRunStage(Executor paramExecutor, Runnable paramRunnable) {
    if (paramRunnable == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture = new CompletableFuture();
    if (paramExecutor != null || !completableFuture.uniRun(this, paramRunnable, null)) {
      UniRun uniRun = new UniRun(paramExecutor, completableFuture, this, paramRunnable);
      push(uniRun);
      uniRun.tryFire(0);
    } 
    return completableFuture;
  }
  
  final boolean uniWhenComplete(CompletableFuture<T> paramCompletableFuture, BiConsumer<? super T, ? super Throwable> paramBiConsumer, UniWhenComplete<T> paramUniWhenComplete) {
    Throwable throwable = null;
    Object object;
    if (paramCompletableFuture == null || (object = paramCompletableFuture.result) == null || paramBiConsumer == null)
      return false; 
    if (this.result == null) {
      try {
        Object object1;
        if (paramUniWhenComplete != null && !paramUniWhenComplete.claim())
          return false; 
        if (object instanceof AltResult) {
          throwable = ((AltResult)object).ex;
          object1 = null;
        } else {
          Object object2 = object;
          object1 = object2;
        } 
        paramBiConsumer.accept(object1, throwable);
        if (throwable == null) {
          internalComplete(object);
          return true;
        } 
      } catch (Throwable throwable1) {
        if (throwable == null)
          throwable = throwable1; 
      } 
      completeThrowable(throwable, object);
    } 
    return true;
  }
  
  private CompletableFuture<T> uniWhenCompleteStage(Executor paramExecutor, BiConsumer<? super T, ? super Throwable> paramBiConsumer) {
    if (paramBiConsumer == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture = new CompletableFuture();
    if (paramExecutor != null || !completableFuture.uniWhenComplete(this, paramBiConsumer, null)) {
      UniWhenComplete uniWhenComplete = new UniWhenComplete(paramExecutor, completableFuture, this, paramBiConsumer);
      push(uniWhenComplete);
      uniWhenComplete.tryFire(0);
    } 
    return completableFuture;
  }
  
  final <S> boolean uniHandle(CompletableFuture<S> paramCompletableFuture, BiFunction<? super S, Throwable, ? extends T> paramBiFunction, UniHandle<S, T> paramUniHandle) {
    Object object;
    if (paramCompletableFuture == null || (object = paramCompletableFuture.result) == null || paramBiFunction == null)
      return false; 
    if (this.result == null)
      try {
        Object object2;
        Object object1;
        if (paramUniHandle != null && !paramUniHandle.claim())
          return false; 
        if (object instanceof AltResult) {
          object2 = ((AltResult)object).ex;
          object1 = null;
        } else {
          object2 = null;
          Object object3 = object;
          object1 = object3;
        } 
        completeValue(paramBiFunction.apply(object1, object2));
      } catch (Throwable throwable) {
        completeThrowable(throwable);
      }  
    return true;
  }
  
  private <V> CompletableFuture<V> uniHandleStage(Executor paramExecutor, BiFunction<? super T, Throwable, ? extends V> paramBiFunction) {
    if (paramBiFunction == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture = new CompletableFuture();
    if (paramExecutor != null || !completableFuture.uniHandle(this, paramBiFunction, null)) {
      UniHandle uniHandle = new UniHandle(paramExecutor, completableFuture, this, paramBiFunction);
      push(uniHandle);
      uniHandle.tryFire(0);
    } 
    return completableFuture;
  }
  
  final boolean uniExceptionally(CompletableFuture<T> paramCompletableFuture, Function<? super Throwable, ? extends T> paramFunction, UniExceptionally<T> paramUniExceptionally) {
    Object object;
    if (paramCompletableFuture == null || (object = paramCompletableFuture.result) == null || paramFunction == null)
      return false; 
    if (this.result == null)
      try {
        Throwable throwable;
        if (object instanceof AltResult && (throwable = ((AltResult)object).ex) != null) {
          if (paramUniExceptionally != null && !paramUniExceptionally.claim())
            return false; 
          completeValue(paramFunction.apply(throwable));
        } else {
          internalComplete(object);
        } 
      } catch (Throwable throwable) {
        completeThrowable(throwable);
      }  
    return true;
  }
  
  private CompletableFuture<T> uniExceptionallyStage(Function<Throwable, ? extends T> paramFunction) {
    if (paramFunction == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture = new CompletableFuture();
    if (!completableFuture.uniExceptionally(this, paramFunction, null)) {
      UniExceptionally uniExceptionally = new UniExceptionally(completableFuture, this, paramFunction);
      push(uniExceptionally);
      uniExceptionally.tryFire(0);
    } 
    return completableFuture;
  }
  
  final boolean uniRelay(CompletableFuture<T> paramCompletableFuture) {
    Object object;
    if (paramCompletableFuture == null || (object = paramCompletableFuture.result) == null)
      return false; 
    if (this.result == null)
      completeRelay(object); 
    return true;
  }
  
  final <S> boolean uniCompose(CompletableFuture<S> paramCompletableFuture, Function<? super S, ? extends CompletionStage<T>> paramFunction, UniCompose<S, T> paramUniCompose) { // Byte code:
    //   0: aload_1
    //   1: ifnull -> 18
    //   4: aload_1
    //   5: getfield result : Ljava/lang/Object;
    //   8: dup
    //   9: astore #4
    //   11: ifnull -> 18
    //   14: aload_2
    //   15: ifnonnull -> 20
    //   18: iconst_0
    //   19: ireturn
    //   20: aload_0
    //   21: getfield result : Ljava/lang/Object;
    //   24: ifnonnull -> 163
    //   27: aload #4
    //   29: instanceof java/util/concurrent/CompletableFuture$AltResult
    //   32: ifeq -> 64
    //   35: aload #4
    //   37: checkcast java/util/concurrent/CompletableFuture$AltResult
    //   40: getfield ex : Ljava/lang/Throwable;
    //   43: dup
    //   44: astore #5
    //   46: ifnull -> 61
    //   49: aload_0
    //   50: aload #5
    //   52: aload #4
    //   54: invokevirtual completeThrowable : (Ljava/lang/Throwable;Ljava/lang/Object;)Z
    //   57: pop
    //   58: goto -> 163
    //   61: aconst_null
    //   62: astore #4
    //   64: aload_3
    //   65: ifnull -> 77
    //   68: aload_3
    //   69: invokevirtual claim : ()Z
    //   72: ifne -> 77
    //   75: iconst_0
    //   76: ireturn
    //   77: aload #4
    //   79: astore #6
    //   81: aload_2
    //   82: aload #6
    //   84: invokeinterface apply : (Ljava/lang/Object;)Ljava/lang/Object;
    //   89: checkcast java/util/concurrent/CompletionStage
    //   92: invokeinterface toCompletableFuture : ()Ljava/util/concurrent/CompletableFuture;
    //   97: astore #7
    //   99: aload #7
    //   101: getfield result : Ljava/lang/Object;
    //   104: ifnull -> 116
    //   107: aload_0
    //   108: aload #7
    //   110: invokevirtual uniRelay : (Ljava/util/concurrent/CompletableFuture;)Z
    //   113: ifne -> 151
    //   116: new java/util/concurrent/CompletableFuture$UniRelay
    //   119: dup
    //   120: aload_0
    //   121: aload #7
    //   123: invokespecial <init> : (Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;)V
    //   126: astore #8
    //   128: aload #7
    //   130: aload #8
    //   132: invokevirtual push : (Ljava/util/concurrent/CompletableFuture$UniCompletion;)V
    //   135: aload #8
    //   137: iconst_0
    //   138: invokevirtual tryFire : (I)Ljava/util/concurrent/CompletableFuture;
    //   141: pop
    //   142: aload_0
    //   143: getfield result : Ljava/lang/Object;
    //   146: ifnonnull -> 151
    //   149: iconst_0
    //   150: ireturn
    //   151: goto -> 163
    //   154: astore #6
    //   156: aload_0
    //   157: aload #6
    //   159: invokevirtual completeThrowable : (Ljava/lang/Throwable;)Z
    //   162: pop
    //   163: iconst_1
    //   164: ireturn
    // Exception table:
    //   from	to	target	type
    //   64	76	154	java/lang/Throwable
    //   77	150	154	java/lang/Throwable }
  
  private <V> CompletableFuture<V> uniComposeStage(Executor paramExecutor, Function<? super T, ? extends CompletionStage<V>> paramFunction) {
    if (paramFunction == null)
      throw new NullPointerException(); 
    Object object;
    if (paramExecutor == null && (object = this.result) != null) {
      if (object instanceof AltResult) {
        Throwable throwable;
        if ((throwable = ((AltResult)object).ex) != null)
          return new CompletableFuture(encodeThrowable(throwable, object)); 
        object = null;
      } 
      try {
        Object object1 = object;
        CompletableFuture completableFuture1 = ((CompletionStage)paramFunction.apply(object1)).toCompletableFuture();
        Object object2 = completableFuture1.result;
        if (object2 != null)
          return new CompletableFuture(encodeRelay(object2)); 
        CompletableFuture completableFuture2 = new CompletableFuture();
        UniRelay uniRelay = new UniRelay(completableFuture2, completableFuture1);
        completableFuture1.push(uniRelay);
        uniRelay.tryFire(0);
        return completableFuture2;
      } catch (Throwable throwable) {
        return new CompletableFuture(encodeThrowable(throwable));
      } 
    } 
    CompletableFuture completableFuture = new CompletableFuture();
    UniCompose uniCompose = new UniCompose(paramExecutor, completableFuture, this, paramFunction);
    push(uniCompose);
    uniCompose.tryFire(0);
    return completableFuture;
  }
  
  final void bipush(CompletableFuture<?> paramCompletableFuture, BiCompletion<?, ?, ?> paramBiCompletion) {
    if (paramBiCompletion != null) {
      Object object;
      while ((object = this.result) == null && !tryPushStack(paramBiCompletion))
        lazySetNext(paramBiCompletion, null); 
      if (paramCompletableFuture != null && paramCompletableFuture != this && paramCompletableFuture.result == null) {
        BiCompletion<?, ?, ?> biCompletion = (object != null) ? paramBiCompletion : new CoCompletion(paramBiCompletion);
        while (paramCompletableFuture.result == null && !paramCompletableFuture.tryPushStack(biCompletion))
          lazySetNext(biCompletion, null); 
      } 
    } 
  }
  
  final CompletableFuture<T> postFire(CompletableFuture<?> paramCompletableFuture1, CompletableFuture<?> paramCompletableFuture2, int paramInt) {
    if (paramCompletableFuture2 != null && paramCompletableFuture2.stack != null)
      if (paramInt < 0 || paramCompletableFuture2.result == null) {
        paramCompletableFuture2.cleanStack();
      } else {
        paramCompletableFuture2.postComplete();
      }  
    return postFire(paramCompletableFuture1, paramInt);
  }
  
  final <R, S> boolean biApply(CompletableFuture<R> paramCompletableFuture1, CompletableFuture<S> paramCompletableFuture2, BiFunction<? super R, ? super S, ? extends T> paramBiFunction, BiApply<R, S, T> paramBiApply) { // Byte code:
    //   0: aload_1
    //   1: ifnull -> 32
    //   4: aload_1
    //   5: getfield result : Ljava/lang/Object;
    //   8: dup
    //   9: astore #5
    //   11: ifnull -> 32
    //   14: aload_2
    //   15: ifnull -> 32
    //   18: aload_2
    //   19: getfield result : Ljava/lang/Object;
    //   22: dup
    //   23: astore #6
    //   25: ifnull -> 32
    //   28: aload_3
    //   29: ifnonnull -> 34
    //   32: iconst_0
    //   33: ireturn
    //   34: aload_0
    //   35: getfield result : Ljava/lang/Object;
    //   38: ifnonnull -> 165
    //   41: aload #5
    //   43: instanceof java/util/concurrent/CompletableFuture$AltResult
    //   46: ifeq -> 78
    //   49: aload #5
    //   51: checkcast java/util/concurrent/CompletableFuture$AltResult
    //   54: getfield ex : Ljava/lang/Throwable;
    //   57: dup
    //   58: astore #7
    //   60: ifnull -> 75
    //   63: aload_0
    //   64: aload #7
    //   66: aload #5
    //   68: invokevirtual completeThrowable : (Ljava/lang/Throwable;Ljava/lang/Object;)Z
    //   71: pop
    //   72: goto -> 165
    //   75: aconst_null
    //   76: astore #5
    //   78: aload #6
    //   80: instanceof java/util/concurrent/CompletableFuture$AltResult
    //   83: ifeq -> 115
    //   86: aload #6
    //   88: checkcast java/util/concurrent/CompletableFuture$AltResult
    //   91: getfield ex : Ljava/lang/Throwable;
    //   94: dup
    //   95: astore #7
    //   97: ifnull -> 112
    //   100: aload_0
    //   101: aload #7
    //   103: aload #6
    //   105: invokevirtual completeThrowable : (Ljava/lang/Throwable;Ljava/lang/Object;)Z
    //   108: pop
    //   109: goto -> 165
    //   112: aconst_null
    //   113: astore #6
    //   115: aload #4
    //   117: ifnull -> 130
    //   120: aload #4
    //   122: invokevirtual claim : ()Z
    //   125: ifne -> 130
    //   128: iconst_0
    //   129: ireturn
    //   130: aload #5
    //   132: astore #8
    //   134: aload #6
    //   136: astore #9
    //   138: aload_0
    //   139: aload_3
    //   140: aload #8
    //   142: aload #9
    //   144: invokeinterface apply : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   149: invokevirtual completeValue : (Ljava/lang/Object;)Z
    //   152: pop
    //   153: goto -> 165
    //   156: astore #8
    //   158: aload_0
    //   159: aload #8
    //   161: invokevirtual completeThrowable : (Ljava/lang/Throwable;)Z
    //   164: pop
    //   165: iconst_1
    //   166: ireturn
    // Exception table:
    //   from	to	target	type
    //   115	129	156	java/lang/Throwable
    //   130	153	156	java/lang/Throwable }
  
  private <U, V> CompletableFuture<V> biApplyStage(Executor paramExecutor, CompletionStage<U> paramCompletionStage, BiFunction<? super T, ? super U, ? extends V> paramBiFunction) {
    CompletableFuture completableFuture1;
    if (paramBiFunction == null || (completableFuture1 = paramCompletionStage.toCompletableFuture()) == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture2 = new CompletableFuture();
    if (paramExecutor != null || !completableFuture2.biApply(this, completableFuture1, paramBiFunction, null)) {
      BiApply biApply = new BiApply(paramExecutor, completableFuture2, this, completableFuture1, paramBiFunction);
      bipush(completableFuture1, biApply);
      biApply.tryFire(0);
    } 
    return completableFuture2;
  }
  
  final <R, S> boolean biAccept(CompletableFuture<R> paramCompletableFuture1, CompletableFuture<S> paramCompletableFuture2, BiConsumer<? super R, ? super S> paramBiConsumer, BiAccept<R, S> paramBiAccept) { // Byte code:
    //   0: aload_1
    //   1: ifnull -> 32
    //   4: aload_1
    //   5: getfield result : Ljava/lang/Object;
    //   8: dup
    //   9: astore #5
    //   11: ifnull -> 32
    //   14: aload_2
    //   15: ifnull -> 32
    //   18: aload_2
    //   19: getfield result : Ljava/lang/Object;
    //   22: dup
    //   23: astore #6
    //   25: ifnull -> 32
    //   28: aload_3
    //   29: ifnonnull -> 34
    //   32: iconst_0
    //   33: ireturn
    //   34: aload_0
    //   35: getfield result : Ljava/lang/Object;
    //   38: ifnonnull -> 165
    //   41: aload #5
    //   43: instanceof java/util/concurrent/CompletableFuture$AltResult
    //   46: ifeq -> 78
    //   49: aload #5
    //   51: checkcast java/util/concurrent/CompletableFuture$AltResult
    //   54: getfield ex : Ljava/lang/Throwable;
    //   57: dup
    //   58: astore #7
    //   60: ifnull -> 75
    //   63: aload_0
    //   64: aload #7
    //   66: aload #5
    //   68: invokevirtual completeThrowable : (Ljava/lang/Throwable;Ljava/lang/Object;)Z
    //   71: pop
    //   72: goto -> 165
    //   75: aconst_null
    //   76: astore #5
    //   78: aload #6
    //   80: instanceof java/util/concurrent/CompletableFuture$AltResult
    //   83: ifeq -> 115
    //   86: aload #6
    //   88: checkcast java/util/concurrent/CompletableFuture$AltResult
    //   91: getfield ex : Ljava/lang/Throwable;
    //   94: dup
    //   95: astore #7
    //   97: ifnull -> 112
    //   100: aload_0
    //   101: aload #7
    //   103: aload #6
    //   105: invokevirtual completeThrowable : (Ljava/lang/Throwable;Ljava/lang/Object;)Z
    //   108: pop
    //   109: goto -> 165
    //   112: aconst_null
    //   113: astore #6
    //   115: aload #4
    //   117: ifnull -> 130
    //   120: aload #4
    //   122: invokevirtual claim : ()Z
    //   125: ifne -> 130
    //   128: iconst_0
    //   129: ireturn
    //   130: aload #5
    //   132: astore #8
    //   134: aload #6
    //   136: astore #9
    //   138: aload_3
    //   139: aload #8
    //   141: aload #9
    //   143: invokeinterface accept : (Ljava/lang/Object;Ljava/lang/Object;)V
    //   148: aload_0
    //   149: invokevirtual completeNull : ()Z
    //   152: pop
    //   153: goto -> 165
    //   156: astore #8
    //   158: aload_0
    //   159: aload #8
    //   161: invokevirtual completeThrowable : (Ljava/lang/Throwable;)Z
    //   164: pop
    //   165: iconst_1
    //   166: ireturn
    // Exception table:
    //   from	to	target	type
    //   115	129	156	java/lang/Throwable
    //   130	153	156	java/lang/Throwable }
  
  private <U> CompletableFuture<Void> biAcceptStage(Executor paramExecutor, CompletionStage<U> paramCompletionStage, BiConsumer<? super T, ? super U> paramBiConsumer) {
    CompletableFuture completableFuture1;
    if (paramBiConsumer == null || (completableFuture1 = paramCompletionStage.toCompletableFuture()) == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture2 = new CompletableFuture();
    if (paramExecutor != null || !completableFuture2.biAccept(this, completableFuture1, paramBiConsumer, null)) {
      BiAccept biAccept = new BiAccept(paramExecutor, completableFuture2, this, completableFuture1, paramBiConsumer);
      bipush(completableFuture1, biAccept);
      biAccept.tryFire(0);
    } 
    return completableFuture2;
  }
  
  final boolean biRun(CompletableFuture<?> paramCompletableFuture1, CompletableFuture<?> paramCompletableFuture2, Runnable paramRunnable, BiRun<?, ?> paramBiRun) {
    Object object1;
    Object object2;
    if (paramCompletableFuture1 == null || (object1 = paramCompletableFuture1.result) == null || paramCompletableFuture2 == null || (object2 = paramCompletableFuture2.result) == null || paramRunnable == null)
      return false; 
    if (this.result == null) {
      Throwable throwable;
      if (object1 instanceof AltResult && (throwable = ((AltResult)object1).ex) != null) {
        completeThrowable(throwable, object1);
      } else if (object2 instanceof AltResult && (throwable = ((AltResult)object2).ex) != null) {
        completeThrowable(throwable, object2);
      } else {
        try {
          if (paramBiRun != null && !paramBiRun.claim())
            return false; 
          paramRunnable.run();
          completeNull();
        } catch (Throwable throwable1) {
          completeThrowable(throwable1);
        } 
      } 
    } 
    return true;
  }
  
  private CompletableFuture<Void> biRunStage(Executor paramExecutor, CompletionStage<?> paramCompletionStage, Runnable paramRunnable) {
    CompletableFuture completableFuture1;
    if (paramRunnable == null || (completableFuture1 = paramCompletionStage.toCompletableFuture()) == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture2 = new CompletableFuture();
    if (paramExecutor != null || !completableFuture2.biRun(this, completableFuture1, paramRunnable, null)) {
      BiRun biRun = new BiRun(paramExecutor, completableFuture2, this, completableFuture1, paramRunnable);
      bipush(completableFuture1, biRun);
      biRun.tryFire(0);
    } 
    return completableFuture2;
  }
  
  boolean biRelay(CompletableFuture<?> paramCompletableFuture1, CompletableFuture<?> paramCompletableFuture2) {
    Object object1;
    Object object2;
    if (paramCompletableFuture1 == null || (object1 = paramCompletableFuture1.result) == null || paramCompletableFuture2 == null || (object2 = paramCompletableFuture2.result) == null)
      return false; 
    if (this.result == null) {
      Throwable throwable;
      if (object1 instanceof AltResult && (throwable = ((AltResult)object1).ex) != null) {
        completeThrowable(throwable, object1);
      } else if (object2 instanceof AltResult && (throwable = ((AltResult)object2).ex) != null) {
        completeThrowable(throwable, object2);
      } else {
        completeNull();
      } 
    } 
    return true;
  }
  
  static CompletableFuture<Void> andTree(CompletableFuture<?>[] paramArrayOfCompletableFuture, int paramInt1, int paramInt2) {
    CompletableFuture completableFuture = new CompletableFuture();
    if (paramInt1 > paramInt2) {
      completableFuture.result = NIL;
    } else {
      CompletableFuture<?> completableFuture2;
      int i = paramInt1 + paramInt2 >>> 1;
      CompletableFuture<?> completableFuture1;
      if ((completableFuture1 = (paramInt1 == i) ? paramArrayOfCompletableFuture[paramInt1] : andTree(paramArrayOfCompletableFuture, paramInt1, i)) != null) {
        if ((completableFuture2 = (paramInt1 == paramInt2) ? completableFuture1 : ((paramInt2 == i + true) ? paramArrayOfCompletableFuture[paramInt2] : andTree(paramArrayOfCompletableFuture, i + true, paramInt2))) == null)
          throw new NullPointerException(); 
      } else {
        throw new NullPointerException();
      } 
      if (!completableFuture.biRelay(completableFuture1, completableFuture2)) {
        BiRelay biRelay = new BiRelay(completableFuture, completableFuture1, completableFuture2);
        completableFuture1.bipush(completableFuture2, biRelay);
        biRelay.tryFire(0);
      } 
    } 
    return completableFuture;
  }
  
  final void orpush(CompletableFuture<?> paramCompletableFuture, BiCompletion<?, ?, ?> paramBiCompletion) {
    if (paramBiCompletion != null)
      while ((paramCompletableFuture == null || paramCompletableFuture.result == null) && this.result == null) {
        if (tryPushStack(paramBiCompletion)) {
          if (paramCompletableFuture != null && paramCompletableFuture != this && paramCompletableFuture.result == null) {
            CoCompletion coCompletion = new CoCompletion(paramBiCompletion);
            while (this.result == null && paramCompletableFuture.result == null && !paramCompletableFuture.tryPushStack(coCompletion))
              lazySetNext(coCompletion, null); 
          } 
          break;
        } 
        lazySetNext(paramBiCompletion, null);
      }  
  }
  
  final <R, S extends R> boolean orApply(CompletableFuture<R> paramCompletableFuture1, CompletableFuture<S> paramCompletableFuture2, Function<? super R, ? extends T> paramFunction, OrApply<R, S, T> paramOrApply) {
    Object object;
    if (paramCompletableFuture1 == null || paramCompletableFuture2 == null || ((object = paramCompletableFuture1.result) == null && (object = paramCompletableFuture2.result) == null) || paramFunction == null)
      return false; 
    if (this.result == null)
      try {
        if (paramOrApply != null && !paramOrApply.claim())
          return false; 
        if (object instanceof AltResult) {
          Throwable throwable;
          if ((throwable = ((AltResult)object).ex) != null) {
            completeThrowable(throwable, object);
          } else {
            object = null;
            Object object2 = object;
            completeValue(paramFunction.apply(object2));
          } 
          return true;
        } 
        Object object1 = object;
        completeValue(paramFunction.apply(object1));
      } catch (Throwable throwable) {
        completeThrowable(throwable);
      }  
    return true;
  }
  
  private <U extends T, V> CompletableFuture<V> orApplyStage(Executor paramExecutor, CompletionStage<U> paramCompletionStage, Function<? super T, ? extends V> paramFunction) {
    CompletableFuture completableFuture1;
    if (paramFunction == null || (completableFuture1 = paramCompletionStage.toCompletableFuture()) == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture2 = new CompletableFuture();
    if (paramExecutor != null || !completableFuture2.orApply(this, completableFuture1, paramFunction, null)) {
      OrApply orApply = new OrApply(paramExecutor, completableFuture2, this, completableFuture1, paramFunction);
      orpush(completableFuture1, orApply);
      orApply.tryFire(0);
    } 
    return completableFuture2;
  }
  
  final <R, S extends R> boolean orAccept(CompletableFuture<R> paramCompletableFuture1, CompletableFuture<S> paramCompletableFuture2, Consumer<? super R> paramConsumer, OrAccept<R, S> paramOrAccept) {
    Object object;
    if (paramCompletableFuture1 == null || paramCompletableFuture2 == null || ((object = paramCompletableFuture1.result) == null && (object = paramCompletableFuture2.result) == null) || paramConsumer == null)
      return false; 
    if (this.result == null)
      try {
        if (paramOrAccept != null && !paramOrAccept.claim())
          return false; 
        if (object instanceof AltResult) {
          Throwable throwable;
          if ((throwable = ((AltResult)object).ex) != null) {
            completeThrowable(throwable, object);
          } else {
            object = null;
            Object object2 = object;
            paramConsumer.accept(object2);
            completeNull();
          } 
          return true;
        } 
        Object object1 = object;
        paramConsumer.accept(object1);
        completeNull();
      } catch (Throwable throwable) {
        completeThrowable(throwable);
      }  
    return true;
  }
  
  private <U extends T> CompletableFuture<Void> orAcceptStage(Executor paramExecutor, CompletionStage<U> paramCompletionStage, Consumer<? super T> paramConsumer) {
    CompletableFuture completableFuture1;
    if (paramConsumer == null || (completableFuture1 = paramCompletionStage.toCompletableFuture()) == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture2 = new CompletableFuture();
    if (paramExecutor != null || !completableFuture2.orAccept(this, completableFuture1, paramConsumer, null)) {
      OrAccept orAccept = new OrAccept(paramExecutor, completableFuture2, this, completableFuture1, paramConsumer);
      orpush(completableFuture1, orAccept);
      orAccept.tryFire(0);
    } 
    return completableFuture2;
  }
  
  final boolean orRun(CompletableFuture<?> paramCompletableFuture1, CompletableFuture<?> paramCompletableFuture2, Runnable paramRunnable, OrRun<?, ?> paramOrRun) {
    Object object;
    if (paramCompletableFuture1 == null || paramCompletableFuture2 == null || ((object = paramCompletableFuture1.result) == null && (object = paramCompletableFuture2.result) == null) || paramRunnable == null)
      return false; 
    if (this.result == null)
      try {
        if (paramOrRun != null && !paramOrRun.claim())
          return false; 
        Throwable throwable;
        if (object instanceof AltResult && (throwable = ((AltResult)object).ex) != null) {
          completeThrowable(throwable, object);
        } else {
          paramRunnable.run();
          completeNull();
        } 
      } catch (Throwable throwable) {
        completeThrowable(throwable);
      }  
    return true;
  }
  
  private CompletableFuture<Void> orRunStage(Executor paramExecutor, CompletionStage<?> paramCompletionStage, Runnable paramRunnable) {
    CompletableFuture completableFuture1;
    if (paramRunnable == null || (completableFuture1 = paramCompletionStage.toCompletableFuture()) == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture2 = new CompletableFuture();
    if (paramExecutor != null || !completableFuture2.orRun(this, completableFuture1, paramRunnable, null)) {
      OrRun orRun = new OrRun(paramExecutor, completableFuture2, this, completableFuture1, paramRunnable);
      orpush(completableFuture1, orRun);
      orRun.tryFire(0);
    } 
    return completableFuture2;
  }
  
  final boolean orRelay(CompletableFuture<?> paramCompletableFuture1, CompletableFuture<?> paramCompletableFuture2) {
    Object object;
    if (paramCompletableFuture1 == null || paramCompletableFuture2 == null || ((object = paramCompletableFuture1.result) == null && (object = paramCompletableFuture2.result) == null))
      return false; 
    if (this.result == null)
      completeRelay(object); 
    return true;
  }
  
  static CompletableFuture<Object> orTree(CompletableFuture<?>[] paramArrayOfCompletableFuture, int paramInt1, int paramInt2) {
    CompletableFuture completableFuture = new CompletableFuture();
    if (paramInt1 <= paramInt2) {
      CompletableFuture<?> completableFuture2;
      int i = paramInt1 + paramInt2 >>> 1;
      CompletableFuture<?> completableFuture1;
      if ((completableFuture1 = (paramInt1 == i) ? paramArrayOfCompletableFuture[paramInt1] : orTree(paramArrayOfCompletableFuture, paramInt1, i)) != null) {
        if ((completableFuture2 = (paramInt1 == paramInt2) ? completableFuture1 : ((paramInt2 == i + true) ? paramArrayOfCompletableFuture[paramInt2] : orTree(paramArrayOfCompletableFuture, i + true, paramInt2))) == null)
          throw new NullPointerException(); 
      } else {
        throw new NullPointerException();
      } 
      if (!completableFuture.orRelay(completableFuture1, completableFuture2)) {
        OrRelay orRelay = new OrRelay(completableFuture, completableFuture1, completableFuture2);
        completableFuture1.orpush(completableFuture2, orRelay);
        orRelay.tryFire(0);
      } 
    } 
    return completableFuture;
  }
  
  static <U> CompletableFuture<U> asyncSupplyStage(Executor paramExecutor, Supplier<U> paramSupplier) {
    if (paramSupplier == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture = new CompletableFuture();
    paramExecutor.execute(new AsyncSupply(completableFuture, paramSupplier));
    return completableFuture;
  }
  
  static CompletableFuture<Void> asyncRunStage(Executor paramExecutor, Runnable paramRunnable) {
    if (paramRunnable == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture = new CompletableFuture();
    paramExecutor.execute(new AsyncRun(completableFuture, paramRunnable));
    return completableFuture;
  }
  
  private Object waitingGet(boolean paramBoolean) {
    Signaller signaller = null;
    boolean bool = false;
    short s = -1;
    Object object;
    while ((object = this.result) == null) {
      if (s < 0) {
        s = (Runtime.getRuntime().availableProcessors() > 1) ? 256 : 0;
        continue;
      } 
      if (s > 0) {
        if (ThreadLocalRandom.nextSecondarySeed() >= 0)
          s--; 
        continue;
      } 
      if (signaller == null) {
        signaller = new Signaller(paramBoolean, 0L, 0L);
        continue;
      } 
      if (!bool) {
        bool = tryPushStack(signaller);
        continue;
      } 
      if (paramBoolean && signaller.interruptControl < 0) {
        signaller.thread = null;
        cleanStack();
        return null;
      } 
      if (signaller.thread != null && this.result == null)
        try {
          ForkJoinPool.managedBlock(signaller);
        } catch (InterruptedException interruptedException) {
          signaller.interruptControl = -1;
        }  
    } 
    if (signaller != null) {
      signaller.thread = null;
      if (signaller.interruptControl < 0)
        if (paramBoolean) {
          object = null;
        } else {
          Thread.currentThread().interrupt();
        }  
    } 
    postComplete();
    return object;
  }
  
  private Object timedGet(long paramLong) throws TimeoutException {
    if (Thread.interrupted())
      return null; 
    if (paramLong <= 0L)
      throw new TimeoutException(); 
    long l = System.nanoTime() + paramLong;
    Signaller signaller = new Signaller(true, paramLong, (l == 0L) ? 1L : l);
    boolean bool = false;
    Object object;
    while ((object = this.result) == null) {
      if (!bool) {
        bool = tryPushStack(signaller);
        continue;
      } 
      if (signaller.interruptControl < 0 || signaller.nanos <= 0L) {
        signaller.thread = null;
        cleanStack();
        if (signaller.interruptControl < 0)
          return null; 
        throw new TimeoutException();
      } 
      if (signaller.thread != null && this.result == null)
        try {
          ForkJoinPool.managedBlock(signaller);
        } catch (InterruptedException interruptedException) {
          signaller.interruptControl = -1;
        }  
    } 
    if (signaller.interruptControl < 0)
      object = null; 
    signaller.thread = null;
    postComplete();
    return object;
  }
  
  public CompletableFuture() {}
  
  private CompletableFuture(Object paramObject) { this.result = paramObject; }
  
  public static <U> CompletableFuture<U> supplyAsync(Supplier<U> paramSupplier) { return asyncSupplyStage(asyncPool, paramSupplier); }
  
  public static <U> CompletableFuture<U> supplyAsync(Supplier<U> paramSupplier, Executor paramExecutor) { return asyncSupplyStage(screenExecutor(paramExecutor), paramSupplier); }
  
  public static CompletableFuture<Void> runAsync(Runnable paramRunnable) { return asyncRunStage(asyncPool, paramRunnable); }
  
  public static CompletableFuture<Void> runAsync(Runnable paramRunnable, Executor paramExecutor) { return asyncRunStage(screenExecutor(paramExecutor), paramRunnable); }
  
  public static <U> CompletableFuture<U> completedFuture(U paramU) { return new CompletableFuture((paramU == null) ? NIL : paramU); }
  
  public boolean isDone() { return (this.result != null); }
  
  public T get() throws InterruptedException, ExecutionException {
    Object object;
    return (T)reportGet(((object = this.result) == null) ? waitingGet(true) : object);
  }
  
  public T get(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException, ExecutionException, TimeoutException {
    long l = paramTimeUnit.toNanos(paramLong);
    Object object;
    return (T)reportGet(((object = this.result) == null) ? timedGet(l) : object);
  }
  
  public T join() throws InterruptedException, ExecutionException {
    Object object;
    return (T)reportJoin(((object = this.result) == null) ? waitingGet(false) : object);
  }
  
  public T getNow(T paramT) {
    Object object;
    return ((object = this.result) == null) ? paramT : reportJoin(object);
  }
  
  public boolean complete(T paramT) {
    boolean bool = completeValue(paramT);
    postComplete();
    return bool;
  }
  
  public boolean completeExceptionally(Throwable paramThrowable) {
    if (paramThrowable == null)
      throw new NullPointerException(); 
    boolean bool = internalComplete(new AltResult(paramThrowable));
    postComplete();
    return bool;
  }
  
  public <U> CompletableFuture<U> thenApply(Function<? super T, ? extends U> paramFunction) { return uniApplyStage(null, paramFunction); }
  
  public <U> CompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> paramFunction) { return uniApplyStage(asyncPool, paramFunction); }
  
  public <U> CompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> paramFunction, Executor paramExecutor) { return uniApplyStage(screenExecutor(paramExecutor), paramFunction); }
  
  public CompletableFuture<Void> thenAccept(Consumer<? super T> paramConsumer) { return uniAcceptStage(null, paramConsumer); }
  
  public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> paramConsumer) { return uniAcceptStage(asyncPool, paramConsumer); }
  
  public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> paramConsumer, Executor paramExecutor) { return uniAcceptStage(screenExecutor(paramExecutor), paramConsumer); }
  
  public CompletableFuture<Void> thenRun(Runnable paramRunnable) { return uniRunStage(null, paramRunnable); }
  
  public CompletableFuture<Void> thenRunAsync(Runnable paramRunnable) { return uniRunStage(asyncPool, paramRunnable); }
  
  public CompletableFuture<Void> thenRunAsync(Runnable paramRunnable, Executor paramExecutor) { return uniRunStage(screenExecutor(paramExecutor), paramRunnable); }
  
  public <U, V> CompletableFuture<V> thenCombine(CompletionStage<? extends U> paramCompletionStage, BiFunction<? super T, ? super U, ? extends V> paramBiFunction) { return biApplyStage(null, paramCompletionStage, paramBiFunction); }
  
  public <U, V> CompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> paramCompletionStage, BiFunction<? super T, ? super U, ? extends V> paramBiFunction) { return biApplyStage(asyncPool, paramCompletionStage, paramBiFunction); }
  
  public <U, V> CompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> paramCompletionStage, BiFunction<? super T, ? super U, ? extends V> paramBiFunction, Executor paramExecutor) { return biApplyStage(screenExecutor(paramExecutor), paramCompletionStage, paramBiFunction); }
  
  public <U> CompletableFuture<Void> thenAcceptBoth(CompletionStage<? extends U> paramCompletionStage, BiConsumer<? super T, ? super U> paramBiConsumer) { return biAcceptStage(null, paramCompletionStage, paramBiConsumer); }
  
  public <U> CompletableFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> paramCompletionStage, BiConsumer<? super T, ? super U> paramBiConsumer) { return biAcceptStage(asyncPool, paramCompletionStage, paramBiConsumer); }
  
  public <U> CompletableFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> paramCompletionStage, BiConsumer<? super T, ? super U> paramBiConsumer, Executor paramExecutor) { return biAcceptStage(screenExecutor(paramExecutor), paramCompletionStage, paramBiConsumer); }
  
  public CompletableFuture<Void> runAfterBoth(CompletionStage<?> paramCompletionStage, Runnable paramRunnable) { return biRunStage(null, paramCompletionStage, paramRunnable); }
  
  public CompletableFuture<Void> runAfterBothAsync(CompletionStage<?> paramCompletionStage, Runnable paramRunnable) { return biRunStage(asyncPool, paramCompletionStage, paramRunnable); }
  
  public CompletableFuture<Void> runAfterBothAsync(CompletionStage<?> paramCompletionStage, Runnable paramRunnable, Executor paramExecutor) { return biRunStage(screenExecutor(paramExecutor), paramCompletionStage, paramRunnable); }
  
  public <U> CompletableFuture<U> applyToEither(CompletionStage<? extends T> paramCompletionStage, Function<? super T, U> paramFunction) { return orApplyStage(null, paramCompletionStage, paramFunction); }
  
  public <U> CompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> paramCompletionStage, Function<? super T, U> paramFunction) { return orApplyStage(asyncPool, paramCompletionStage, paramFunction); }
  
  public <U> CompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> paramCompletionStage, Function<? super T, U> paramFunction, Executor paramExecutor) { return orApplyStage(screenExecutor(paramExecutor), paramCompletionStage, paramFunction); }
  
  public CompletableFuture<Void> acceptEither(CompletionStage<? extends T> paramCompletionStage, Consumer<? super T> paramConsumer) { return orAcceptStage(null, paramCompletionStage, paramConsumer); }
  
  public CompletableFuture<Void> acceptEitherAsync(CompletionStage<? extends T> paramCompletionStage, Consumer<? super T> paramConsumer) { return orAcceptStage(asyncPool, paramCompletionStage, paramConsumer); }
  
  public CompletableFuture<Void> acceptEitherAsync(CompletionStage<? extends T> paramCompletionStage, Consumer<? super T> paramConsumer, Executor paramExecutor) { return orAcceptStage(screenExecutor(paramExecutor), paramCompletionStage, paramConsumer); }
  
  public CompletableFuture<Void> runAfterEither(CompletionStage<?> paramCompletionStage, Runnable paramRunnable) { return orRunStage(null, paramCompletionStage, paramRunnable); }
  
  public CompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> paramCompletionStage, Runnable paramRunnable) { return orRunStage(asyncPool, paramCompletionStage, paramRunnable); }
  
  public CompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> paramCompletionStage, Runnable paramRunnable, Executor paramExecutor) { return orRunStage(screenExecutor(paramExecutor), paramCompletionStage, paramRunnable); }
  
  public <U> CompletableFuture<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> paramFunction) { return uniComposeStage(null, paramFunction); }
  
  public <U> CompletableFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> paramFunction) { return uniComposeStage(asyncPool, paramFunction); }
  
  public <U> CompletableFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> paramFunction, Executor paramExecutor) { return uniComposeStage(screenExecutor(paramExecutor), paramFunction); }
  
  public CompletableFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> paramBiConsumer) { return uniWhenCompleteStage(null, paramBiConsumer); }
  
  public CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> paramBiConsumer) { return uniWhenCompleteStage(asyncPool, paramBiConsumer); }
  
  public CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> paramBiConsumer, Executor paramExecutor) { return uniWhenCompleteStage(screenExecutor(paramExecutor), paramBiConsumer); }
  
  public <U> CompletableFuture<U> handle(BiFunction<? super T, Throwable, ? extends U> paramBiFunction) { return uniHandleStage(null, paramBiFunction); }
  
  public <U> CompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> paramBiFunction) { return uniHandleStage(asyncPool, paramBiFunction); }
  
  public <U> CompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> paramBiFunction, Executor paramExecutor) { return uniHandleStage(screenExecutor(paramExecutor), paramBiFunction); }
  
  public CompletableFuture<T> toCompletableFuture() { return this; }
  
  public CompletableFuture<T> exceptionally(Function<Throwable, ? extends T> paramFunction) { return uniExceptionallyStage(paramFunction); }
  
  public static CompletableFuture<Void> allOf(CompletableFuture<?>... paramVarArgs) { return andTree(paramVarArgs, 0, paramVarArgs.length - 1); }
  
  public static CompletableFuture<Object> anyOf(CompletableFuture<?>... paramVarArgs) { return orTree(paramVarArgs, 0, paramVarArgs.length - 1); }
  
  public boolean cancel(boolean paramBoolean) {
    boolean bool = (this.result == null && internalComplete(new AltResult(new CancellationException()))) ? 1 : 0;
    postComplete();
    return (bool || isCancelled());
  }
  
  public boolean isCancelled() {
    Object object;
    return (object = this.result instanceof AltResult && ((AltResult)object).ex instanceof CancellationException);
  }
  
  public boolean isCompletedExceptionally() {
    Object object;
    return (object = this.result instanceof AltResult && object != NIL);
  }
  
  public void obtrudeValue(T paramT) {
    this.result = (paramT == null) ? NIL : paramT;
    postComplete();
  }
  
  public void obtrudeException(Throwable paramThrowable) {
    if (paramThrowable == null)
      throw new NullPointerException(); 
    this.result = new AltResult(paramThrowable);
    postComplete();
  }
  
  public int getNumberOfDependents() {
    byte b = 0;
    for (Completion completion = this.stack; completion != null; completion = completion.next)
      b++; 
    return b;
  }
  
  public String toString() {
    Object object = this.result;
    int i;
    return super.toString() + ((object == null) ? (((i = getNumberOfDependents()) == 0) ? "[Not completed]" : ("[Not completed, " + i + " dependents]")) : ((object instanceof AltResult && ((AltResult)object).ex != null) ? "[Completed exceptionally]" : "[Completed normally]"));
  }
  
  static  {
    try {
      Unsafe unsafe = Unsafe.getUnsafe();
      UNSAFE = unsafe;
      Class clazz = CompletableFuture.class;
      RESULT = unsafe.objectFieldOffset(clazz.getDeclaredField("result"));
      STACK = unsafe.objectFieldOffset(clazz.getDeclaredField("stack"));
      NEXT = unsafe.objectFieldOffset(Completion.class.getDeclaredField("next"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  static final class AltResult {
    final Throwable ex;
    
    AltResult(Throwable param1Throwable) { this.ex = param1Throwable; }
  }
  
  static final class AsyncRun extends ForkJoinTask<Void> implements Runnable, AsynchronousCompletionTask {
    CompletableFuture<Void> dep;
    
    Runnable fn;
    
    AsyncRun(CompletableFuture<Void> param1CompletableFuture, Runnable param1Runnable) {
      this.dep = param1CompletableFuture;
      this.fn = param1Runnable;
    }
    
    public final Void getRawResult() { return null; }
    
    public final void setRawResult(Void param1Void) {}
    
    public final boolean exec() {
      run();
      return true;
    }
    
    public void run() {
      CompletableFuture completableFuture;
      Runnable runnable;
      if ((completableFuture = this.dep) != null && (runnable = this.fn) != null) {
        this.dep = null;
        this.fn = null;
        if (completableFuture.result == null)
          try {
            runnable.run();
            completableFuture.completeNull();
          } catch (Throwable throwable) {
            completableFuture.completeThrowable(throwable);
          }  
        completableFuture.postComplete();
      } 
    }
  }
  
  static final class AsyncSupply<T> extends ForkJoinTask<Void> implements Runnable, AsynchronousCompletionTask {
    CompletableFuture<T> dep;
    
    Supplier<T> fn;
    
    AsyncSupply(CompletableFuture<T> param1CompletableFuture, Supplier<T> param1Supplier) {
      this.dep = param1CompletableFuture;
      this.fn = param1Supplier;
    }
    
    public final Void getRawResult() { return null; }
    
    public final void setRawResult(Void param1Void) {}
    
    public final boolean exec() {
      run();
      return true;
    }
    
    public void run() {
      CompletableFuture completableFuture;
      Supplier supplier;
      if ((completableFuture = this.dep) != null && (supplier = this.fn) != null) {
        this.dep = null;
        this.fn = null;
        if (completableFuture.result == null)
          try {
            completableFuture.completeValue(supplier.get());
          } catch (Throwable throwable) {
            completableFuture.completeThrowable(throwable);
          }  
        completableFuture.postComplete();
      } 
    }
  }
  
  public static interface AsynchronousCompletionTask {}
  
  static final class BiAccept<T, U> extends BiCompletion<T, U, Void> {
    BiConsumer<? super T, ? super U> fn;
    
    BiAccept(Executor param1Executor, CompletableFuture<Void> param1CompletableFuture1, CompletableFuture<T> param1CompletableFuture2, CompletableFuture<U> param1CompletableFuture3, BiConsumer<? super T, ? super U> param1BiConsumer) {
      super(param1Executor, param1CompletableFuture1, param1CompletableFuture2, param1CompletableFuture3);
      this.fn = param1BiConsumer;
    }
    
    final CompletableFuture<Void> tryFire(int param1Int) {
      CompletableFuture completableFuture1;
      CompletableFuture completableFuture2;
      CompletableFuture completableFuture3;
      if ((completableFuture1 = this.dep) == null || !completableFuture1.biAccept(completableFuture2 = this.src, completableFuture3 = this.snd, this.fn, (param1Int > 0) ? null : this))
        return null; 
      this.dep = null;
      this.src = null;
      this.snd = null;
      this.fn = null;
      return completableFuture1.postFire(completableFuture2, completableFuture3, param1Int);
    }
  }
  
  static final class BiApply<T, U, V> extends BiCompletion<T, U, V> {
    BiFunction<? super T, ? super U, ? extends V> fn;
    
    BiApply(Executor param1Executor, CompletableFuture<V> param1CompletableFuture1, CompletableFuture<T> param1CompletableFuture2, CompletableFuture<U> param1CompletableFuture3, BiFunction<? super T, ? super U, ? extends V> param1BiFunction) {
      super(param1Executor, param1CompletableFuture1, param1CompletableFuture2, param1CompletableFuture3);
      this.fn = param1BiFunction;
    }
    
    final CompletableFuture<V> tryFire(int param1Int) {
      CompletableFuture completableFuture1;
      CompletableFuture completableFuture2;
      CompletableFuture completableFuture3;
      if ((completableFuture1 = this.dep) == null || !completableFuture1.biApply(completableFuture2 = this.src, completableFuture3 = this.snd, this.fn, (param1Int > 0) ? null : this))
        return null; 
      this.dep = null;
      this.src = null;
      this.snd = null;
      this.fn = null;
      return completableFuture1.postFire(completableFuture2, completableFuture3, param1Int);
    }
  }
  
  static abstract class BiCompletion<T, U, V> extends UniCompletion<T, V> {
    CompletableFuture<U> snd;
    
    BiCompletion(Executor param1Executor, CompletableFuture<V> param1CompletableFuture1, CompletableFuture<T> param1CompletableFuture2, CompletableFuture<U> param1CompletableFuture3) {
      super(param1Executor, param1CompletableFuture1, param1CompletableFuture2);
      this.snd = param1CompletableFuture3;
    }
  }
  
  static final class BiRelay<T, U> extends BiCompletion<T, U, Void> {
    BiRelay(CompletableFuture<Void> param1CompletableFuture1, CompletableFuture<T> param1CompletableFuture2, CompletableFuture<U> param1CompletableFuture3) { super(null, param1CompletableFuture1, param1CompletableFuture2, param1CompletableFuture3); }
    
    final CompletableFuture<Void> tryFire(int param1Int) {
      CompletableFuture completableFuture1;
      CompletableFuture completableFuture2;
      CompletableFuture completableFuture3;
      if ((completableFuture1 = this.dep) == null || !completableFuture1.biRelay(completableFuture2 = this.src, completableFuture3 = this.snd))
        return null; 
      this.src = null;
      this.snd = null;
      this.dep = null;
      return completableFuture1.postFire(completableFuture2, completableFuture3, param1Int);
    }
  }
  
  static final class BiRun<T, U> extends BiCompletion<T, U, Void> {
    Runnable fn;
    
    BiRun(Executor param1Executor, CompletableFuture<Void> param1CompletableFuture1, CompletableFuture<T> param1CompletableFuture2, CompletableFuture<U> param1CompletableFuture3, Runnable param1Runnable) {
      super(param1Executor, param1CompletableFuture1, param1CompletableFuture2, param1CompletableFuture3);
      this.fn = param1Runnable;
    }
    
    final CompletableFuture<Void> tryFire(int param1Int) {
      CompletableFuture completableFuture1;
      CompletableFuture completableFuture2;
      CompletableFuture completableFuture3;
      if ((completableFuture1 = this.dep) == null || !completableFuture1.biRun(completableFuture2 = this.src, completableFuture3 = this.snd, this.fn, (param1Int > 0) ? null : this))
        return null; 
      this.dep = null;
      this.src = null;
      this.snd = null;
      this.fn = null;
      return completableFuture1.postFire(completableFuture2, completableFuture3, param1Int);
    }
  }
  
  static final class CoCompletion extends Completion {
    CompletableFuture.BiCompletion<?, ?, ?> base;
    
    CoCompletion(CompletableFuture.BiCompletion<?, ?, ?> param1BiCompletion) { this.base = param1BiCompletion; }
    
    final CompletableFuture<?> tryFire(int param1Int) {
      CompletableFuture.BiCompletion biCompletion;
      CompletableFuture completableFuture;
      if ((biCompletion = this.base) == null || (completableFuture = biCompletion.tryFire(param1Int)) == null)
        return null; 
      this.base = null;
      return completableFuture;
    }
    
    final boolean isLive() {
      CompletableFuture.BiCompletion biCompletion;
      return ((biCompletion = this.base) != null && biCompletion.dep != null);
    }
  }
  
  static abstract class Completion extends ForkJoinTask<Void> implements Runnable, AsynchronousCompletionTask {
    abstract CompletableFuture<?> tryFire(int param1Int);
    
    abstract boolean isLive();
    
    public final void run() { tryFire(1); }
    
    public final boolean exec() {
      tryFire(1);
      return true;
    }
    
    public final Void getRawResult() { return null; }
    
    public final void setRawResult(Void param1Void) {}
  }
  
  static final class OrAccept<T, U extends T> extends BiCompletion<T, U, Void> {
    Consumer<? super T> fn;
    
    OrAccept(Executor param1Executor, CompletableFuture<Void> param1CompletableFuture1, CompletableFuture<T> param1CompletableFuture2, CompletableFuture<U> param1CompletableFuture3, Consumer<? super T> param1Consumer) {
      super(param1Executor, param1CompletableFuture1, param1CompletableFuture2, param1CompletableFuture3);
      this.fn = param1Consumer;
    }
    
    final CompletableFuture<Void> tryFire(int param1Int) {
      CompletableFuture completableFuture1;
      CompletableFuture completableFuture2;
      CompletableFuture completableFuture3;
      if ((completableFuture1 = this.dep) == null || !completableFuture1.orAccept(completableFuture2 = this.src, completableFuture3 = this.snd, this.fn, (param1Int > 0) ? null : this))
        return null; 
      this.dep = null;
      this.src = null;
      this.snd = null;
      this.fn = null;
      return completableFuture1.postFire(completableFuture2, completableFuture3, param1Int);
    }
  }
  
  static final class OrApply<T, U extends T, V> extends BiCompletion<T, U, V> {
    Function<? super T, ? extends V> fn;
    
    OrApply(Executor param1Executor, CompletableFuture<V> param1CompletableFuture1, CompletableFuture<T> param1CompletableFuture2, CompletableFuture<U> param1CompletableFuture3, Function<? super T, ? extends V> param1Function) {
      super(param1Executor, param1CompletableFuture1, param1CompletableFuture2, param1CompletableFuture3);
      this.fn = param1Function;
    }
    
    final CompletableFuture<V> tryFire(int param1Int) {
      CompletableFuture completableFuture1;
      CompletableFuture completableFuture2;
      CompletableFuture completableFuture3;
      if ((completableFuture1 = this.dep) == null || !completableFuture1.orApply(completableFuture2 = this.src, completableFuture3 = this.snd, this.fn, (param1Int > 0) ? null : this))
        return null; 
      this.dep = null;
      this.src = null;
      this.snd = null;
      this.fn = null;
      return completableFuture1.postFire(completableFuture2, completableFuture3, param1Int);
    }
  }
  
  static final class OrRelay<T, U> extends BiCompletion<T, U, Object> {
    OrRelay(CompletableFuture<Object> param1CompletableFuture1, CompletableFuture<T> param1CompletableFuture2, CompletableFuture<U> param1CompletableFuture3) { super(null, param1CompletableFuture1, param1CompletableFuture2, param1CompletableFuture3); }
    
    final CompletableFuture<Object> tryFire(int param1Int) {
      CompletableFuture completableFuture1;
      CompletableFuture completableFuture2;
      CompletableFuture completableFuture3;
      if ((completableFuture1 = this.dep) == null || !completableFuture1.orRelay(completableFuture2 = this.src, completableFuture3 = this.snd))
        return null; 
      this.src = null;
      this.snd = null;
      this.dep = null;
      return completableFuture1.postFire(completableFuture2, completableFuture3, param1Int);
    }
  }
  
  static final class OrRun<T, U> extends BiCompletion<T, U, Void> {
    Runnable fn;
    
    OrRun(Executor param1Executor, CompletableFuture<Void> param1CompletableFuture1, CompletableFuture<T> param1CompletableFuture2, CompletableFuture<U> param1CompletableFuture3, Runnable param1Runnable) {
      super(param1Executor, param1CompletableFuture1, param1CompletableFuture2, param1CompletableFuture3);
      this.fn = param1Runnable;
    }
    
    final CompletableFuture<Void> tryFire(int param1Int) {
      CompletableFuture completableFuture1;
      CompletableFuture completableFuture2;
      CompletableFuture completableFuture3;
      if ((completableFuture1 = this.dep) == null || !completableFuture1.orRun(completableFuture2 = this.src, completableFuture3 = this.snd, this.fn, (param1Int > 0) ? null : this))
        return null; 
      this.dep = null;
      this.src = null;
      this.snd = null;
      this.fn = null;
      return completableFuture1.postFire(completableFuture2, completableFuture3, param1Int);
    }
  }
  
  static final class Signaller extends Completion implements ForkJoinPool.ManagedBlocker {
    long nanos;
    
    final long deadline;
    
    Signaller(boolean param1Boolean, long param1Long1, long param1Long2) {
      this.interruptControl = param1Boolean ? 1 : 0;
      this.nanos = param1Long1;
      this.deadline = param1Long2;
    }
    
    final CompletableFuture<?> tryFire(int param1Int) {
      Thread thread1;
      if ((thread1 = this.thread) != null) {
        this.thread = null;
        LockSupport.unpark(thread1);
      } 
      return null;
    }
    
    public boolean isReleasable() {
      if (this.thread == null)
        return true; 
      if (Thread.interrupted()) {
        int i = this.interruptControl;
        this.interruptControl = -1;
        if (i > 0)
          return true; 
      } 
      if (this.deadline != 0L && (this.nanos <= 0L || (this.nanos = this.deadline - System.nanoTime()) <= 0L)) {
        this.thread = null;
        return true;
      } 
      return false;
    }
    
    public boolean block() {
      if (isReleasable())
        return true; 
      if (this.deadline == 0L) {
        LockSupport.park(this);
      } else if (this.nanos > 0L) {
        LockSupport.parkNanos(this, this.nanos);
      } 
      return isReleasable();
    }
    
    final boolean isLive() { return (this.thread != null); }
  }
  
  static final class ThreadPerTaskExecutor implements Executor {
    public void execute(Runnable param1Runnable) { (new Thread(param1Runnable)).start(); }
  }
  
  static final class UniAccept<T> extends UniCompletion<T, Void> {
    Consumer<? super T> fn;
    
    UniAccept(Executor param1Executor, CompletableFuture<Void> param1CompletableFuture1, CompletableFuture<T> param1CompletableFuture2, Consumer<? super T> param1Consumer) {
      super(param1Executor, param1CompletableFuture1, param1CompletableFuture2);
      this.fn = param1Consumer;
    }
    
    final CompletableFuture<Void> tryFire(int param1Int) {
      CompletableFuture completableFuture1;
      CompletableFuture completableFuture2;
      if ((completableFuture1 = this.dep) == null || !completableFuture1.uniAccept(completableFuture2 = this.src, this.fn, (param1Int > 0) ? null : this))
        return null; 
      this.dep = null;
      this.src = null;
      this.fn = null;
      return completableFuture1.postFire(completableFuture2, param1Int);
    }
  }
  
  static final class UniApply<T, V> extends UniCompletion<T, V> {
    Function<? super T, ? extends V> fn;
    
    UniApply(Executor param1Executor, CompletableFuture<V> param1CompletableFuture1, CompletableFuture<T> param1CompletableFuture2, Function<? super T, ? extends V> param1Function) {
      super(param1Executor, param1CompletableFuture1, param1CompletableFuture2);
      this.fn = param1Function;
    }
    
    final CompletableFuture<V> tryFire(int param1Int) {
      CompletableFuture completableFuture1;
      CompletableFuture completableFuture2;
      if ((completableFuture1 = this.dep) == null || !completableFuture1.uniApply(completableFuture2 = this.src, this.fn, (param1Int > 0) ? null : this))
        return null; 
      this.dep = null;
      this.src = null;
      this.fn = null;
      return completableFuture1.postFire(completableFuture2, param1Int);
    }
  }
  
  static abstract class UniCompletion<T, V> extends Completion {
    Executor executor;
    
    CompletableFuture<V> dep;
    
    CompletableFuture<T> src;
    
    UniCompletion(Executor param1Executor, CompletableFuture<V> param1CompletableFuture1, CompletableFuture<T> param1CompletableFuture2) {
      this.executor = param1Executor;
      this.dep = param1CompletableFuture1;
      this.src = param1CompletableFuture2;
    }
    
    final boolean claim() {
      Executor executor1 = this.executor;
      if (compareAndSetForkJoinTaskTag((short)0, (short)1)) {
        if (executor1 == null)
          return true; 
        this.executor = null;
        executor1.execute(this);
      } 
      return false;
    }
    
    final boolean isLive() { return (this.dep != null); }
  }
  
  static final class UniCompose<T, V> extends UniCompletion<T, V> {
    Function<? super T, ? extends CompletionStage<V>> fn;
    
    UniCompose(Executor param1Executor, CompletableFuture<V> param1CompletableFuture1, CompletableFuture<T> param1CompletableFuture2, Function<? super T, ? extends CompletionStage<V>> param1Function) {
      super(param1Executor, param1CompletableFuture1, param1CompletableFuture2);
      this.fn = param1Function;
    }
    
    final CompletableFuture<V> tryFire(int param1Int) {
      CompletableFuture completableFuture1;
      CompletableFuture completableFuture2;
      if ((completableFuture1 = this.dep) == null || !completableFuture1.uniCompose(completableFuture2 = this.src, this.fn, (param1Int > 0) ? null : this))
        return null; 
      this.dep = null;
      this.src = null;
      this.fn = null;
      return completableFuture1.postFire(completableFuture2, param1Int);
    }
  }
  
  static final class UniExceptionally<T> extends UniCompletion<T, T> {
    Function<? super Throwable, ? extends T> fn;
    
    UniExceptionally(CompletableFuture<T> param1CompletableFuture1, CompletableFuture<T> param1CompletableFuture2, Function<? super Throwable, ? extends T> param1Function) {
      super(null, param1CompletableFuture1, param1CompletableFuture2);
      this.fn = param1Function;
    }
    
    final CompletableFuture<T> tryFire(int param1Int) {
      CompletableFuture completableFuture1;
      CompletableFuture completableFuture2;
      if ((completableFuture1 = this.dep) == null || !completableFuture1.uniExceptionally(completableFuture2 = this.src, this.fn, this))
        return null; 
      this.dep = null;
      this.src = null;
      this.fn = null;
      return completableFuture1.postFire(completableFuture2, param1Int);
    }
  }
  
  static final class UniHandle<T, V> extends UniCompletion<T, V> {
    BiFunction<? super T, Throwable, ? extends V> fn;
    
    UniHandle(Executor param1Executor, CompletableFuture<V> param1CompletableFuture1, CompletableFuture<T> param1CompletableFuture2, BiFunction<? super T, Throwable, ? extends V> param1BiFunction) {
      super(param1Executor, param1CompletableFuture1, param1CompletableFuture2);
      this.fn = param1BiFunction;
    }
    
    final CompletableFuture<V> tryFire(int param1Int) {
      CompletableFuture completableFuture1;
      CompletableFuture completableFuture2;
      if ((completableFuture1 = this.dep) == null || !completableFuture1.uniHandle(completableFuture2 = this.src, this.fn, (param1Int > 0) ? null : this))
        return null; 
      this.dep = null;
      this.src = null;
      this.fn = null;
      return completableFuture1.postFire(completableFuture2, param1Int);
    }
  }
  
  static final class UniRelay<T> extends UniCompletion<T, T> {
    UniRelay(CompletableFuture<T> param1CompletableFuture1, CompletableFuture<T> param1CompletableFuture2) { super(null, param1CompletableFuture1, param1CompletableFuture2); }
    
    final CompletableFuture<T> tryFire(int param1Int) {
      CompletableFuture completableFuture1;
      CompletableFuture completableFuture2;
      if ((completableFuture1 = this.dep) == null || !completableFuture1.uniRelay(completableFuture2 = this.src))
        return null; 
      this.src = null;
      this.dep = null;
      return completableFuture1.postFire(completableFuture2, param1Int);
    }
  }
  
  static final class UniRun<T> extends UniCompletion<T, Void> {
    Runnable fn;
    
    UniRun(Executor param1Executor, CompletableFuture<Void> param1CompletableFuture1, CompletableFuture<T> param1CompletableFuture2, Runnable param1Runnable) {
      super(param1Executor, param1CompletableFuture1, param1CompletableFuture2);
      this.fn = param1Runnable;
    }
    
    final CompletableFuture<Void> tryFire(int param1Int) {
      CompletableFuture completableFuture1;
      CompletableFuture completableFuture2;
      if ((completableFuture1 = this.dep) == null || !completableFuture1.uniRun(completableFuture2 = this.src, this.fn, (param1Int > 0) ? null : this))
        return null; 
      this.dep = null;
      this.src = null;
      this.fn = null;
      return completableFuture1.postFire(completableFuture2, param1Int);
    }
  }
  
  static final class UniWhenComplete<T> extends UniCompletion<T, T> {
    BiConsumer<? super T, ? super Throwable> fn;
    
    UniWhenComplete(Executor param1Executor, CompletableFuture<T> param1CompletableFuture1, CompletableFuture<T> param1CompletableFuture2, BiConsumer<? super T, ? super Throwable> param1BiConsumer) {
      super(param1Executor, param1CompletableFuture1, param1CompletableFuture2);
      this.fn = param1BiConsumer;
    }
    
    final CompletableFuture<T> tryFire(int param1Int) {
      CompletableFuture completableFuture1;
      CompletableFuture completableFuture2;
      if ((completableFuture1 = this.dep) == null || !completableFuture1.uniWhenComplete(completableFuture2 = this.src, this.fn, (param1Int > 0) ? null : this))
        return null; 
      this.dep = null;
      this.src = null;
      this.fn = null;
      return completableFuture1.postFire(completableFuture2, param1Int);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\CompletableFuture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */