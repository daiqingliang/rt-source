package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.StringStatistic;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public final class StringStatisticImpl extends StatisticImpl implements StringStatistic, InvocationHandler {
  private final String initStr;
  
  private final StringStatistic ss = (StringStatistic)Proxy.newProxyInstance(StringStatistic.class.getClassLoader(), new Class[] { StringStatistic.class }, this);
  
  public StringStatisticImpl(String paramString1, String paramString2, String paramString3, String paramString4, long paramLong1, long paramLong2) {
    super(paramString2, paramString3, paramString4, paramLong2, paramLong1);
    this.str = paramString1;
    this.initStr = paramString1;
  }
  
  public StringStatisticImpl(String paramString1, String paramString2, String paramString3) { this("", paramString1, paramString2, paramString3, System.currentTimeMillis(), System.currentTimeMillis()); }
  
  public StringStatistic getStatistic() { return this.ss; }
  
  public Map getStaticAsMap() {
    Map map = super.getStaticAsMap();
    if (getCurrent() != null)
      map.put("current", getCurrent()); 
    return map;
  }
  
  public String toString() { return super.toString() + NEWLINE + "Current-value: " + getCurrent(); }
  
  public String getCurrent() { return this.str; }
  
  public void setCurrent(String paramString) {
    this.str = paramString;
    this.sampleTime = System.currentTimeMillis();
  }
  
  public void reset() {
    super.reset();
    this.str = this.initStr;
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\external\statistics\impl\StringStatisticImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */