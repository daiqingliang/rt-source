package java.util;

import java.util.function.IntConsumer;

public class IntSummaryStatistics implements IntConsumer {
  private long count;
  
  private long sum;
  
  private int min = Integer.MAX_VALUE;
  
  private int max = Integer.MIN_VALUE;
  
  public void accept(int paramInt) {
    this.count++;
    this.sum += paramInt;
    this.min = Math.min(this.min, paramInt);
    this.max = Math.max(this.max, paramInt);
  }
  
  public void combine(IntSummaryStatistics paramIntSummaryStatistics) {
    this.count += paramIntSummaryStatistics.count;
    this.sum += paramIntSummaryStatistics.sum;
    this.min = Math.min(this.min, paramIntSummaryStatistics.min);
    this.max = Math.max(this.max, paramIntSummaryStatistics.max);
  }
  
  public final long getCount() { return this.count; }
  
  public final long getSum() { return this.sum; }
  
  public final int getMin() { return this.min; }
  
  public final int getMax() { return this.max; }
  
  public final double getAverage() { return (getCount() > 0L) ? (getSum() / getCount()) : 0.0D; }
  
  public String toString() { return String.format("%s{count=%d, sum=%d, min=%d, average=%f, max=%d}", new Object[] { getClass().getSimpleName(), Long.valueOf(getCount()), Long.valueOf(getSum()), Integer.valueOf(getMin()), Double.valueOf(getAverage()), Integer.valueOf(getMax()) }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\IntSummaryStatistics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */