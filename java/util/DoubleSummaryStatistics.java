package java.util;

import java.util.function.DoubleConsumer;

public class DoubleSummaryStatistics implements DoubleConsumer {
  private long count;
  
  private double sum;
  
  private double sumCompensation;
  
  private double simpleSum;
  
  private double min = Double.POSITIVE_INFINITY;
  
  private double max = Double.NEGATIVE_INFINITY;
  
  public void accept(double paramDouble) {
    this.count++;
    this.simpleSum += paramDouble;
    sumWithCompensation(paramDouble);
    this.min = Math.min(this.min, paramDouble);
    this.max = Math.max(this.max, paramDouble);
  }
  
  public void combine(DoubleSummaryStatistics paramDoubleSummaryStatistics) {
    this.count += paramDoubleSummaryStatistics.count;
    this.simpleSum += paramDoubleSummaryStatistics.simpleSum;
    sumWithCompensation(paramDoubleSummaryStatistics.sum);
    sumWithCompensation(paramDoubleSummaryStatistics.sumCompensation);
    this.min = Math.min(this.min, paramDoubleSummaryStatistics.min);
    this.max = Math.max(this.max, paramDoubleSummaryStatistics.max);
  }
  
  private void sumWithCompensation(double paramDouble) {
    double d1 = paramDouble - this.sumCompensation;
    double d2 = this.sum + d1;
    this.sumCompensation = d2 - this.sum - d1;
    this.sum = d2;
  }
  
  public final long getCount() { return this.count; }
  
  public final double getSum() {
    double d = this.sum + this.sumCompensation;
    return (Double.isNaN(d) && Double.isInfinite(this.simpleSum)) ? this.simpleSum : d;
  }
  
  public final double getMin() { return this.min; }
  
  public final double getMax() { return this.max; }
  
  public final double getAverage() { return (getCount() > 0L) ? (getSum() / getCount()) : 0.0D; }
  
  public String toString() { return String.format("%s{count=%d, sum=%f, min=%f, average=%f, max=%f}", new Object[] { getClass().getSimpleName(), Long.valueOf(getCount()), Double.valueOf(getSum()), Double.valueOf(getMin()), Double.valueOf(getAverage()), Double.valueOf(getMax()) }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\DoubleSummaryStatistics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */