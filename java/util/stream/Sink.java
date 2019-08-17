package java.util.stream;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

interface Sink<T> extends Consumer<T> {
  default void begin(long paramLong) {}
  
  default void end() {}
  
  default boolean cancellationRequested() { return false; }
  
  default void accept(int paramInt) { throw new IllegalStateException("called wrong accept method"); }
  
  default void accept(long paramLong) { throw new IllegalStateException("called wrong accept method"); }
  
  default void accept(double paramDouble) { throw new IllegalStateException("called wrong accept method"); }
  
  public static abstract class ChainedDouble<E_OUT> extends Object implements OfDouble {
    protected final Sink<? super E_OUT> downstream;
    
    public ChainedDouble(Sink<? super E_OUT> param1Sink) { this.downstream = (Sink)Objects.requireNonNull(param1Sink); }
    
    public void begin(long param1Long) { this.downstream.begin(param1Long); }
    
    public void end() { this.downstream.end(); }
    
    public boolean cancellationRequested() { return this.downstream.cancellationRequested(); }
  }
  
  public static abstract class ChainedInt<E_OUT> extends Object implements OfInt {
    protected final Sink<? super E_OUT> downstream;
    
    public ChainedInt(Sink<? super E_OUT> param1Sink) { this.downstream = (Sink)Objects.requireNonNull(param1Sink); }
    
    public void begin(long param1Long) { this.downstream.begin(param1Long); }
    
    public void end() { this.downstream.end(); }
    
    public boolean cancellationRequested() { return this.downstream.cancellationRequested(); }
  }
  
  public static abstract class ChainedLong<E_OUT> extends Object implements OfLong {
    protected final Sink<? super E_OUT> downstream;
    
    public ChainedLong(Sink<? super E_OUT> param1Sink) { this.downstream = (Sink)Objects.requireNonNull(param1Sink); }
    
    public void begin(long param1Long) { this.downstream.begin(param1Long); }
    
    public void end() { this.downstream.end(); }
    
    public boolean cancellationRequested() { return this.downstream.cancellationRequested(); }
  }
  
  public static abstract class ChainedReference<T, E_OUT> extends Object implements Sink<T> {
    protected final Sink<? super E_OUT> downstream;
    
    public ChainedReference(Sink<? super E_OUT> param1Sink) { this.downstream = (Sink)Objects.requireNonNull(param1Sink); }
    
    public void begin(long param1Long) { this.downstream.begin(param1Long); }
    
    public void end() { this.downstream.end(); }
    
    public boolean cancellationRequested() { return this.downstream.cancellationRequested(); }
  }
  
  public static interface OfDouble extends Sink<Double>, DoubleConsumer {
    void accept(double param1Double);
    
    default void accept(Double param1Double) {
      if (Tripwire.ENABLED)
        Tripwire.trip(getClass(), "{0} calling Sink.OfDouble.accept(Double)"); 
      accept(param1Double.doubleValue());
    }
  }
  
  public static interface OfInt extends Sink<Integer>, IntConsumer {
    void accept(int param1Int);
    
    default void accept(Integer param1Integer) {
      if (Tripwire.ENABLED)
        Tripwire.trip(getClass(), "{0} calling Sink.OfInt.accept(Integer)"); 
      accept(param1Integer.intValue());
    }
  }
  
  public static interface OfLong extends Sink<Long>, LongConsumer {
    void accept(long param1Long);
    
    default void accept(Long param1Long) {
      if (Tripwire.ENABLED)
        Tripwire.trip(getClass(), "{0} calling Sink.OfLong.accept(Long)"); 
      accept(param1Long.longValue());
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\Sink.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */