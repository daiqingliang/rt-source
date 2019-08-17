package sun.awt.image;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.awt.AppContext;

class ImageFetcher extends Thread {
  static final int HIGH_PRIORITY = 8;
  
  static final int LOW_PRIORITY = 3;
  
  static final int ANIM_PRIORITY = 2;
  
  static final int TIMEOUT = 5000;
  
  private ImageFetcher(ThreadGroup paramThreadGroup, int paramInt) {
    super(paramThreadGroup, "Image Fetcher " + paramInt);
    setDaemon(true);
  }
  
  public static boolean add(ImageFetchable paramImageFetchable) {
    FetcherInfo fetcherInfo = FetcherInfo.getFetcherInfo();
    synchronized (fetcherInfo.waitList) {
      if (!fetcherInfo.waitList.contains(paramImageFetchable)) {
        fetcherInfo.waitList.addElement(paramImageFetchable);
        if (fetcherInfo.numWaiting == 0 && fetcherInfo.numFetchers < fetcherInfo.fetchers.length)
          createFetchers(fetcherInfo); 
        if (fetcherInfo.numFetchers > 0) {
          fetcherInfo.waitList.notify();
        } else {
          fetcherInfo.waitList.removeElement(paramImageFetchable);
          return false;
        } 
      } 
    } 
    return true;
  }
  
  public static void remove(ImageFetchable paramImageFetchable) {
    FetcherInfo fetcherInfo = FetcherInfo.getFetcherInfo();
    synchronized (fetcherInfo.waitList) {
      if (fetcherInfo.waitList.contains(paramImageFetchable))
        fetcherInfo.waitList.removeElement(paramImageFetchable); 
    } 
  }
  
  public static boolean isFetcher(Thread paramThread) {
    FetcherInfo fetcherInfo = FetcherInfo.getFetcherInfo();
    synchronized (fetcherInfo.waitList) {
      for (byte b = 0; b < fetcherInfo.fetchers.length; b++) {
        if (fetcherInfo.fetchers[b] == paramThread)
          return true; 
      } 
    } 
    return false;
  }
  
  public static boolean amFetcher() { return isFetcher(Thread.currentThread()); }
  
  private static ImageFetchable nextImage() {
    fetcherInfo = FetcherInfo.getFetcherInfo();
    synchronized (fetcherInfo.waitList) {
      ImageFetchable imageFetchable = null;
      long l = System.currentTimeMillis() + 5000L;
      while (imageFetchable == null) {
        while (fetcherInfo.waitList.size() == 0) {
          long l1 = System.currentTimeMillis();
          if (l1 >= l)
            return null; 
          try {
            fetcherInfo.numWaiting++;
            fetcherInfo.waitList.wait(l - l1);
          } catch (InterruptedException interruptedException) {
            return null;
          } finally {
            fetcherInfo.numWaiting--;
          } 
        } 
        imageFetchable = (ImageFetchable)fetcherInfo.waitList.elementAt(0);
        fetcherInfo.waitList.removeElement(imageFetchable);
      } 
      return imageFetchable;
    } 
  }
  
  public void run() {
    fetcherInfo = FetcherInfo.getFetcherInfo();
    try {
      fetchloop();
    } catch (Exception exception) {
      exception.printStackTrace();
    } finally {
      synchronized (fetcherInfo.waitList) {
        Thread thread = Thread.currentThread();
        for (byte b = 0; b < fetcherInfo.fetchers.length; b++) {
          if (fetcherInfo.fetchers[b] == thread) {
            fetcherInfo.fetchers[b] = null;
            fetcherInfo.numFetchers--;
          } 
        } 
      } 
    } 
  }
  
  private void fetchloop() {
    Thread thread = Thread.currentThread();
    while (isFetcher(thread)) {
      thread.interrupted();
      thread.setPriority(8);
      ImageFetchable imageFetchable = nextImage();
      if (imageFetchable == null)
        return; 
      try {
        imageFetchable.doFetch();
      } catch (Exception exception) {
        System.err.println("Uncaught error fetching image:");
        exception.printStackTrace();
      } 
      stoppingAnimation(thread);
    } 
  }
  
  static void startingAnimation() {
    FetcherInfo fetcherInfo = FetcherInfo.getFetcherInfo();
    Thread thread = Thread.currentThread();
    synchronized (fetcherInfo.waitList) {
      for (byte b = 0; b < fetcherInfo.fetchers.length; b++) {
        if (fetcherInfo.fetchers[b] == thread) {
          fetcherInfo.fetchers[b] = null;
          fetcherInfo.numFetchers--;
          thread.setName("Image Animator " + b);
          if (fetcherInfo.waitList.size() > fetcherInfo.numWaiting)
            createFetchers(fetcherInfo); 
          return;
        } 
      } 
    } 
    thread.setPriority(2);
    thread.setName("Image Animator");
  }
  
  private static void stoppingAnimation(Thread paramThread) {
    FetcherInfo fetcherInfo = FetcherInfo.getFetcherInfo();
    synchronized (fetcherInfo.waitList) {
      byte b1 = -1;
      for (byte b2 = 0; b2 < fetcherInfo.fetchers.length; b2++) {
        if (fetcherInfo.fetchers[b2] == paramThread)
          return; 
        if (fetcherInfo.fetchers[b2] == null)
          b1 = b2; 
      } 
      if (b1 >= 0) {
        fetcherInfo.fetchers[b1] = paramThread;
        fetcherInfo.numFetchers++;
        paramThread.setName("Image Fetcher " + b1);
        return;
      } 
    } 
  }
  
  private static void createFetchers(final FetcherInfo info) {
    ThreadGroup threadGroup2;
    AppContext appContext = AppContext.getAppContext();
    ThreadGroup threadGroup1 = appContext.getThreadGroup();
    try {
      if (threadGroup1.getParent() != null) {
        threadGroup2 = threadGroup1;
      } else {
        threadGroup1 = Thread.currentThread().getThreadGroup();
        for (ThreadGroup threadGroup = threadGroup1.getParent(); threadGroup != null && threadGroup.getParent() != null; threadGroup = threadGroup1.getParent())
          threadGroup1 = threadGroup; 
        threadGroup2 = threadGroup1;
      } 
    } catch (SecurityException securityException) {
      threadGroup2 = appContext.getThreadGroup();
    } 
    final ThreadGroup fetcherGroup = threadGroup2;
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            for (byte b = 0; b < this.val$info.fetchers.length; b++) {
              if (this.val$info.fetchers[b] == null) {
                ImageFetcher imageFetcher = new ImageFetcher(fetcherGroup, b, null);
                try {
                  imageFetcher.start();
                  this.val$info.fetchers[b] = imageFetcher;
                  this.val$info.numFetchers++;
                  break;
                } catch (Error error) {}
              } 
            } 
            return null;
          }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\ImageFetcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */