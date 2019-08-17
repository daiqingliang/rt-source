package sun.awt.image;

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.lang.ref.WeakReference;
import java.security.AccessControlContext;
import java.security.AccessController;

public abstract class ImageWatched {
  public static Link endlink = new Link();
  
  public Link watcherList = endlink;
  
  public void addWatcher(ImageObserver paramImageObserver) {
    if (paramImageObserver != null && !isWatcher(paramImageObserver))
      this.watcherList = new WeakLink(paramImageObserver, this.watcherList); 
    this.watcherList = this.watcherList.removeWatcher(null);
  }
  
  public boolean isWatcher(ImageObserver paramImageObserver) { return this.watcherList.isWatcher(paramImageObserver); }
  
  public void removeWatcher(ImageObserver paramImageObserver) {
    synchronized (this) {
      this.watcherList = this.watcherList.removeWatcher(paramImageObserver);
    } 
    if (this.watcherList == endlink)
      notifyWatcherListEmpty(); 
  }
  
  public boolean isWatcherListEmpty() {
    synchronized (this) {
      this.watcherList = this.watcherList.removeWatcher(null);
    } 
    return (this.watcherList == endlink);
  }
  
  public void newInfo(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    if (this.watcherList.newInfo(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5))
      removeWatcher(null); 
  }
  
  protected abstract void notifyWatcherListEmpty();
  
  static class AccWeakReference<T> extends WeakReference<T> {
    private final AccessControlContext acc = AccessController.getContext();
    
    AccWeakReference(T param1T) { super(param1T); }
  }
  
  public static class Link {
    public boolean isWatcher(ImageObserver param1ImageObserver) { return false; }
    
    public Link removeWatcher(ImageObserver param1ImageObserver) { return this; }
    
    public boolean newInfo(Image param1Image, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { return false; }
  }
  
  public static class WeakLink extends Link {
    private final ImageWatched.AccWeakReference<ImageObserver> myref;
    
    private ImageWatched.Link next;
    
    public WeakLink(ImageObserver param1ImageObserver, ImageWatched.Link param1Link) {
      this.myref = new ImageWatched.AccWeakReference(param1ImageObserver);
      this.next = param1Link;
    }
    
    public boolean isWatcher(ImageObserver param1ImageObserver) { return (this.myref.get() == param1ImageObserver || this.next.isWatcher(param1ImageObserver)); }
    
    public ImageWatched.Link removeWatcher(ImageObserver param1ImageObserver) {
      ImageObserver imageObserver = (ImageObserver)this.myref.get();
      if (imageObserver == null)
        return this.next.removeWatcher(param1ImageObserver); 
      if (imageObserver == param1ImageObserver)
        return this.next; 
      this.next = this.next.removeWatcher(param1ImageObserver);
      return this;
    }
    
    private static boolean update(ImageObserver param1ImageObserver, AccessControlContext param1AccessControlContext, Image param1Image, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { return (param1AccessControlContext != null || System.getSecurityManager() != null) ? ((Boolean)AccessController.doPrivileged(() -> Boolean.valueOf(param1ImageObserver.imageUpdate(param1Image, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5)), param1AccessControlContext)).booleanValue() : 0; }
    
    public boolean newInfo(Image param1Image, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      boolean bool = this.next.newInfo(param1Image, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5);
      ImageObserver imageObserver = (ImageObserver)this.myref.get();
      if (imageObserver == null) {
        bool = true;
      } else if (!update(imageObserver, this.myref.acc, param1Image, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5)) {
        this.myref.clear();
        bool = true;
      } 
      return bool;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\ImageWatched.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */