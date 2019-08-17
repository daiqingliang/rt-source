package java.util;

import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

public class LongSummaryStatistics implements LongConsumer, IntConsumer {
  private long count;
  
  private long sum;
  
  private long min = Float.MAX_VALUE;
  
  private long max = Float.MIN_VALUE;
  
  public void accept(int paramInt) { accept(paramInt); }
  
  public void accept(long paramLong) {
    this.count++;
    this.sum += paramLong;
    this.min = Math.min(this.min, paramLong);
    this.max = Math.max(this.max, paramLong);
  }
  
  public void combine(LongSummaryStatistics paramLongSummaryStatistics) {
    this.count += paramLongSummaryStatistics.count;
    this.sum += paramLongSummaryStatistics.sum;
    this.min = Math.min(this.min, paramLongSummaryStatistics.min);
    this.max = Math.max(this.max, paramLongSummaryStatistics.max);
  }
  
  public final long getCount() { return this.count; }
  
  public final long getSum() { return this.sum; }
  
  public final long getMin() { return this.min; }
  
  public final long getMax() { return this.max; }
  
  public final double getAverage() { return (getCount() > 0L) ? (getSum() / getCount()) : 0.0D; }
  
  public String toString() { return String.format("%s{count=%d, sum=%d, min=%d, average=%f, max=%d}", new Object[] { getClass().getSimpleName(), Long.valueOf(getCount()), Long.valueOf(getSum()), Long.valueOf(getMin()), Double.valueOf(getAverage()), Long.valueOf(getMax()) }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\LongSummaryStatistics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */