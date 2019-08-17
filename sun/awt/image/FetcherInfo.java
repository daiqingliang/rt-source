package sun.awt.image;

import java.util.Vector;
import sun.awt.AppContext;

class FetcherInfo {
  static final int MAX_NUM_FETCHERS_PER_APPCONTEXT = 4;
  
  Thread[] fetchers = new Thread[4];
  
  int numFetchers = 0;
  
  int numWaiting = 0;
  
  Vector waitList = new Vector();
  
  private static final Object FETCHER_INFO_KEY = new StringBuffer("FetcherInfo");
  
  static FetcherInfo getFetcherInfo() {
    AppContext appContext = AppContext.getAppContext();
    synchronized (appContext) {
      FetcherInfo fetcherInfo = (FetcherInfo)appContext.get(FETCHER_INFO_KEY);
      if (fetcherInfo == null) {
        fetcherInfo = new FetcherInfo();
        appContext.put(FETCHER_INFO_KEY, fetcherInfo);
      } 
      return fetcherInfo;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\FetcherInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */