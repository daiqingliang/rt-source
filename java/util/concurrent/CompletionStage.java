package java.util.concurrent;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public interface CompletionStage<T> {
  <U> CompletionStage<U> thenApply(Function<? super T, ? extends U> paramFunction);
  
  <U> CompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> paramFunction);
  
  <U> CompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> paramFunction, Executor paramExecutor);
  
  CompletionStage<Void> thenAccept(Consumer<? super T> paramConsumer);
  
  CompletionStage<Void> thenAcceptAsync(Consumer<? super T> paramConsumer);
  
  CompletionStage<Void> thenAcceptAsync(Consumer<? super T> paramConsumer, Executor paramExecutor);
  
  CompletionStage<Void> thenRun(Runnable paramRunnable);
  
  CompletionStage<Void> thenRunAsync(Runnable paramRunnable);
  
  CompletionStage<Void> thenRunAsync(Runnable paramRunnable, Executor paramExecutor);
  
  <U, V> CompletionStage<V> thenCombine(CompletionStage<? extends U> paramCompletionStage, BiFunction<? super T, ? super U, ? extends V> paramBiFunction);
  
  <U, V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> paramCompletionStage, BiFunction<? super T, ? super U, ? extends V> paramBiFunction);
  
  <U, V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> paramCompletionStage, BiFunction<? super T, ? super U, ? extends V> paramBiFunction, Executor paramExecutor);
  
  <U> CompletionStage<Void> thenAcceptBoth(CompletionStage<? extends U> paramCompletionStage, BiConsumer<? super T, ? super U> paramBiConsumer);
  
  <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> paramCompletionStage, BiConsumer<? super T, ? super U> paramBiConsumer);
  
  <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> paramCompletionStage, BiConsumer<? super T, ? super U> paramBiConsumer, Executor paramExecutor);
  
  CompletionStage<Void> runAfterBoth(CompletionStage<?> paramCompletionStage, Runnable paramRunnable);
  
  CompletionStage<Void> runAfterBothAsync(CompletionStage<?> paramCompletionStage, Runnable paramRunnable);
  
  CompletionStage<Void> runAfterBothAsync(CompletionStage<?> paramCompletionStage, Runnable paramRunnable, Executor paramExecutor);
  
  <U> CompletionStage<U> applyToEither(CompletionStage<? extends T> paramCompletionStage, Function<? super T, U> paramFunction);
  
  <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> paramCompletionStage, Function<? super T, U> paramFunction);
  
  <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> paramCompletionStage, Function<? super T, U> paramFunction, Executor paramExecutor);
  
  CompletionStage<Void> acceptEither(CompletionStage<? extends T> paramCompletionStage, Consumer<? super T> paramConsumer);
  
  CompletionStage<Void> acceptEitherAsync(CompletionStage<? extends T> paramCompletionStage, Consumer<? super T> paramConsumer);
  
  CompletionStage<Void> acceptEitherAsync(CompletionStage<? extends T> paramCompletionStage, Consumer<? super T> paramConsumer, Executor paramExecutor);
  
  CompletionStage<Void> runAfterEither(CompletionStage<?> paramCompletionStage, Runnable paramRunnable);
  
  CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> paramCompletionStage, Runnable paramRunnable);
  
  CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> paramCompletionStage, Runnable paramRunnable, Executor paramExecutor);
  
  <U> CompletionStage<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> paramFunction);
  
  <U> CompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> paramFunction);
  
  <U> CompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> paramFunction, Executor paramExecutor);
  
  CompletionStage<T> exceptionally(Function<Throwable, ? extends T> paramFunction);
  
  CompletionStage<T> whenComplete(BiConsumer<? super T, ? super Throwable> paramBiConsumer);
  
  CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> paramBiConsumer);
  
  CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> paramBiConsumer, Executor paramExecutor);
  
  <U> CompletionStage<U> handle(BiFunction<? super T, Throwable, ? extends U> paramBiFunction);
  
  <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> paramBiFunction);
  
  <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> paramBiFunction, Executor paramExecutor);
  
  CompletableFuture<T> toCompletableFuture();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\CompletionStage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */