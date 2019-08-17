package sun.misc;

public final class ThreadGroupUtils {
  public static ThreadGroup getRootThreadGroup() {
    ThreadGroup threadGroup1 = Thread.currentThread().getThreadGroup();
    for (ThreadGroup threadGroup2 = threadGroup1.getParent(); threadGroup2 != null; threadGroup2 = threadGroup1.getParent())
      threadGroup1 = threadGroup2; 
    return threadGroup1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\ThreadGroupUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */