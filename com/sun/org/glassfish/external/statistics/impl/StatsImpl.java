package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.Statistic;
import com.sun.org.glassfish.external.statistics.Stats;
import java.util.ArrayList;

public final class StatsImpl implements Stats {
  private final StatisticImpl[] statArray;
  
  protected StatsImpl(StatisticImpl[] paramArrayOfStatisticImpl) { this.statArray = paramArrayOfStatisticImpl; }
  
  public Statistic getStatistic(String paramString) {
    StatisticImpl statisticImpl = null;
    for (StatisticImpl statisticImpl1 : this.statArray) {
      if (statisticImpl1.getName().equals(paramString)) {
        statisticImpl = statisticImpl1;
        break;
      } 
    } 
    return statisticImpl;
  }
  
  public String[] getStatisticNames() {
    ArrayList arrayList = new ArrayList();
    for (StatisticImpl statisticImpl : this.statArray)
      arrayList.add(statisticImpl.getName()); 
    String[] arrayOfString = new String[arrayList.size()];
    return (String[])arrayList.toArray(arrayOfString);
  }
  
  public Statistic[] getStatistics() { return this.statArray; }
  
  public void reset() {
    for (StatisticImpl statisticImpl : this.statArray)
      statisticImpl.reset(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\external\statistics\impl\StatsImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */