package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.BoundaryStatistic;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public final class BoundaryStatisticImpl extends StatisticImpl implements BoundaryStatistic, InvocationHandler {
  private final long lowerBound;
  
  private final long upperBound;
  
  private final BoundaryStatistic bs = (BoundaryStatistic)Proxy.newProxyInstance(BoundaryStatistic.class.getClassLoader(), new Class[] { BoundaryStatistic.class }, this);
  
  public BoundaryStatisticImpl(long paramLong1, long paramLong2, String paramString1, String paramString2, String paramString3, long paramLong3, long paramLong4) {
    super(paramString1, paramString2, paramString3, paramLong3, paramLong4);
    this.upperBound = paramLong2;
    this.lowerBound = paramLong1;
  }
  
  public BoundaryStatistic getStatistic() { return this.bs; }
  
  public Map getStaticAsMap() {
    Map map = super.getStaticAsMap();
    map.put("lowerbound", Long.valueOf(getLowerBound()));
    map.put("upperbound", Long.valueOf(getUpperBound()));
    return map;
  }
  
  public long getLowerBound() { return this.lowerBound; }
  
  public long getUpperBound() { return this.upperBound; }
  
  public void reset() {
    super.reset();
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\external\statistics\impl\BoundaryStatisticImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */