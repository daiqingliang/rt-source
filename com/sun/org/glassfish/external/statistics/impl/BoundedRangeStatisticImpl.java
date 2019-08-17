package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.BoundedRangeStatistic;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public final class BoundedRangeStatisticImpl extends StatisticImpl implements BoundedRangeStatistic, InvocationHandler {
  private long lowerBound = 0L;
  
  private long upperBound = 0L;
  
  private long currentVal = 0L;
  
  private long highWaterMark = Float.MIN_VALUE;
  
  private long lowWaterMark = Float.MAX_VALUE;
  
  private final long initLowerBound;
  
  private final long initUpperBound;
  
  private final long initCurrentVal;
  
  private final long initHighWaterMark;
  
  private final long initLowWaterMark;
  
  private final BoundedRangeStatistic bs = (BoundedRangeStatistic)Proxy.newProxyInstance(BoundedRangeStatistic.class.getClassLoader(), new Class[] { BoundedRangeStatistic.class }, this);
  
  public String toString() { return super.toString() + NEWLINE + "Current: " + getCurrent() + NEWLINE + "LowWaterMark: " + getLowWaterMark() + NEWLINE + "HighWaterMark: " + getHighWaterMark() + NEWLINE + "LowerBound: " + getLowerBound() + NEWLINE + "UpperBound: " + getUpperBound(); }
  
  public BoundedRangeStatisticImpl(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, String paramString1, String paramString2, String paramString3, long paramLong6, long paramLong7) {
    super(paramString1, paramString2, paramString3, paramLong6, paramLong7);
    this.currentVal = paramLong1;
    this.initCurrentVal = paramLong1;
    this.highWaterMark = paramLong2;
    this.initHighWaterMark = paramLong2;
    this.lowWaterMark = paramLong3;
    this.initLowWaterMark = paramLong3;
    this.upperBound = paramLong4;
    this.initUpperBound = paramLong4;
    this.lowerBound = paramLong5;
    this.initLowerBound = paramLong5;
  }
  
  public BoundedRangeStatistic getStatistic() { return this.bs; }
  
  public Map getStaticAsMap() {
    Map map = super.getStaticAsMap();
    map.put("current", Long.valueOf(getCurrent()));
    map.put("lowerbound", Long.valueOf(getLowerBound()));
    map.put("upperbound", Long.valueOf(getUpperBound()));
    map.put("lowwatermark", Long.valueOf(getLowWaterMark()));
    map.put("highwatermark", Long.valueOf(getHighWaterMark()));
    return map;
  }
  
  public long getCurrent() { return this.currentVal; }
  
  public void setCurrent(long paramLong) {
    this.currentVal = paramLong;
    this.lowWaterMark = (paramLong >= this.lowWaterMark) ? this.lowWaterMark : paramLong;
    this.highWaterMark = (paramLong >= this.highWaterMark) ? paramLong : this.highWaterMark;
    this.sampleTime = System.currentTimeMillis();
  }
  
  public long getHighWaterMark() { return this.highWaterMark; }
  
  public void setHighWaterMark(long paramLong) { this.highWaterMark = paramLong; }
  
  public long getLowWaterMark() { return this.lowWaterMark; }
  
  public void setLowWaterMark(long paramLong) { this.lowWaterMark = paramLong; }
  
  public long getLowerBound() { return this.lowerBound; }
  
  public long getUpperBound() { return this.upperBound; }
  
  public void reset() {
    super.reset();
    this.lowerBound = this.initLowerBound;
    this.upperBound = this.initUpperBound;
    this.currentVal = this.initCurrentVal;
    this.highWaterMark = this.initHighWaterMark;
    this.lowWaterMark = this.initLowWaterMark;
    this.sampleTime = -1L;
  }
  
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\external\statistics\impl\BoundedRangeStatisticImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */