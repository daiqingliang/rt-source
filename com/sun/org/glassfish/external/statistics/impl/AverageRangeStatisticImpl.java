package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.AverageRangeStatistic;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public final class AverageRangeStatisticImpl extends StatisticImpl implements AverageRangeStatistic, InvocationHandler {
  private long currentVal = 0L;
  
  private long highWaterMark = Float.MIN_VALUE;
  
  private long lowWaterMark = Float.MAX_VALUE;
  
  private long numberOfSamples = 0L;
  
  private long runningTotal = 0L;
  
  private final long initCurrentVal;
  
  private final long initHighWaterMark;
  
  private final long initLowWaterMark;
  
  private final long initNumberOfSamples;
  
  private final long initRunningTotal;
  
  private final AverageRangeStatistic as = (AverageRangeStatistic)Proxy.newProxyInstance(AverageRangeStatistic.class.getClassLoader(), new Class[] { AverageRangeStatistic.class }, this);
  
  public AverageRangeStatisticImpl(long paramLong1, long paramLong2, long paramLong3, String paramString1, String paramString2, String paramString3, long paramLong4, long paramLong5) {
    super(paramString1, paramString2, paramString3, paramLong4, paramLong5);
    this.currentVal = paramLong1;
    this.initCurrentVal = paramLong1;
    this.highWaterMark = paramLong2;
    this.initHighWaterMark = paramLong2;
    this.lowWaterMark = paramLong3;
    this.initLowWaterMark = paramLong3;
    this.numberOfSamples = 0L;
    this.initNumberOfSamples = this.numberOfSamples;
    this.runningTotal = 0L;
    this.initRunningTotal = this.runningTotal;
  }
  
  public AverageRangeStatistic getStatistic() { return this.as; }
  
  public String toString() { return super.toString() + NEWLINE + "Current: " + getCurrent() + NEWLINE + "LowWaterMark: " + getLowWaterMark() + NEWLINE + "HighWaterMark: " + getHighWaterMark() + NEWLINE + "Average:" + getAverage(); }
  
  public Map getStaticAsMap() {
    Map map = super.getStaticAsMap();
    map.put("current", Long.valueOf(getCurrent()));
    map.put("lowwatermark", Long.valueOf(getLowWaterMark()));
    map.put("highwatermark", Long.valueOf(getHighWaterMark()));
    map.put("average", Long.valueOf(getAverage()));
    return map;
  }
  
  public void reset() {
    super.reset();
    this.currentVal = this.initCurrentVal;
    this.highWaterMark = this.initHighWaterMark;
    this.lowWaterMark = this.initLowWaterMark;
    this.numberOfSamples = this.initNumberOfSamples;
    this.runningTotal = this.initRunningTotal;
    this.sampleTime = -1L;
  }
  
  public long getAverage() { return (this.numberOfSamples == 0L) ? -1L : (this.runningTotal / this.numberOfSamples); }
  
  public long getCurrent() { return this.currentVal; }
  
  public void setCurrent(long paramLong) {
    this.currentVal = paramLong;
    this.lowWaterMark = (paramLong >= this.lowWaterMark) ? this.lowWaterMark : paramLong;
    this.highWaterMark = (paramLong >= this.highWaterMark) ? paramLong : this.highWaterMark;
    this.numberOfSamples++;
    this.runningTotal += paramLong;
    this.sampleTime = System.currentTimeMillis();
  }
  
  public long getHighWaterMark() { return this.highWaterMark; }
  
  public long getLowWaterMark() { return this.lowWaterMark; }
  
  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) throws Throwable {
    Object object;
    checkMethod(paramMethod);
    try {
      object = paramMethod.invoke(this, paramArrayOfObject);
    } catch (InvocationTargetException invocationTargetException) {
      throw invocationTargetException.getTargetException();
    } catch (Exception exception) {
      throw new RuntimeException("unexpected invocation exception: " + exception.getMessage());
    } 
    return object;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\external\statistics\impl\AverageRangeStatisticImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */