package java.awt;

import java.io.Serializable;
import sun.awt.image.MultiResolutionToolkitImage;

public class MediaTracker implements Serializable {
  Component target;
  
  MediaEntry head;
  
  private static final long serialVersionUID = -483174189758638095L;
  
  public static final int LOADING = 1;
  
  public static final int ABORTED = 2;
  
  public static final int ERRORED = 4;
  
  public static final int COMPLETE = 8;
  
  static final int DONE = 14;
  
  public MediaTracker(Component paramComponent) { this.target = paramComponent; }
  
  public void addImage(Image paramImage, int paramInt) { addImage(paramImage, paramInt, -1, -1); }
  
  public void addImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3) {
    addImageImpl(paramImage, paramInt1, paramInt2, paramInt3);
    Image image = getResolutionVariant(paramImage);
    if (image != null)
      addImageImpl(image, paramInt1, (paramInt2 == -1) ? -1 : (2 * paramInt2), (paramInt3 == -1) ? -1 : (2 * paramInt3)); 
  }
  
  private void addImageImpl(Image paramImage, int paramInt1, int paramInt2, int paramInt3) { this.head = MediaEntry.insert(this.head, new ImageMediaEntry(this, paramImage, paramInt1, paramInt2, paramInt3)); }
  
  public boolean checkAll() { return checkAll(false, true); }
  
  public boolean checkAll(boolean paramBoolean) { return checkAll(paramBoolean, true); }
  
  private boolean checkAll(boolean paramBoolean1, boolean paramBoolean2) {
    MediaEntry mediaEntry = this.head;
    boolean bool = true;
    while (mediaEntry != null) {
      if ((mediaEntry.getStatus(paramBoolean1, paramBoolean2) & 0xE) == 0)
        bool = false; 
      mediaEntry = mediaEntry.next;
    } 
    return bool;
  }
  
  public boolean isErrorAny() {
    for (MediaEntry mediaEntry = this.head; mediaEntry != null; mediaEntry = mediaEntry.next) {
      if ((mediaEntry.getStatus(false, true) & 0x4) != 0)
        return true; 
    } 
    return false;
  }
  
  public Object[] getErrorsAny() {
    MediaEntry mediaEntry = this.head;
    byte b = 0;
    while (mediaEntry != null) {
      if ((mediaEntry.getStatus(false, true) & 0x4) != 0)
        b++; 
      mediaEntry = mediaEntry.next;
    } 
    if (b == 0)
      return null; 
    Object[] arrayOfObject = new Object[b];
    mediaEntry = this.head;
    b = 0;
    while (mediaEntry != null) {
      if ((mediaEntry.getStatus(false, false) & 0x4) != 0)
        arrayOfObject[b++] = mediaEntry.getMedia(); 
      mediaEntry = mediaEntry.next;
    } 
    return arrayOfObject;
  }
  
  public void waitForAll() throws InterruptedException { waitForAll(0L); }
  
  public boolean waitForAll(long paramLong) throws InterruptedException {
    long l = System.currentTimeMillis() + paramLong;
    boolean bool = true;
    while (true) {
      long l1;
      int i = statusAll(bool, bool);
      if ((i & true) == 0)
        return (i == 8); 
      bool = false;
      if (paramLong == 0L) {
        l1 = 0L;
      } else {
        l1 = l - System.currentTimeMillis();
        if (l1 <= 0L)
          return false; 
      } 
      wait(l1);
    } 
  }
  
  public int statusAll(boolean paramBoolean) { return statusAll(paramBoolean, true); }
  
  private int statusAll(boolean paramBoolean1, boolean paramBoolean2) {
    MediaEntry mediaEntry = this.head;
    int i = 0;
    while (mediaEntry != null) {
      i |= mediaEntry.getStatus(paramBoolean1, paramBoolean2);
      mediaEntry = mediaEntry.next;
    } 
    return i;
  }
  
  public boolean checkID(int paramInt) { return checkID(paramInt, false, true); }
  
  public boolean checkID(int paramInt, boolean paramBoolean) { return checkID(paramInt, paramBoolean, true); }
  
  private boolean checkID(int paramInt, boolean paramBoolean1, boolean paramBoolean2) {
    MediaEntry mediaEntry = this.head;
    boolean bool = true;
    while (mediaEntry != null) {
      if (mediaEntry.getID() == paramInt && (mediaEntry.getStatus(paramBoolean1, paramBoolean2) & 0xE) == 0)
        bool = false; 
      mediaEntry = mediaEntry.next;
    } 
    return bool;
  }
  
  public boolean isErrorID(int paramInt) {
    for (MediaEntry mediaEntry = this.head; mediaEntry != null; mediaEntry = mediaEntry.next) {
      if (mediaEntry.getID() == paramInt && (mediaEntry.getStatus(false, true) & 0x4) != 0)
        return true; 
    } 
    return false;
  }
  
  public Object[] getErrorsID(int paramInt) {
    MediaEntry mediaEntry = this.head;
    byte b = 0;
    while (mediaEntry != null) {
      if (mediaEntry.getID() == paramInt && (mediaEntry.getStatus(false, true) & 0x4) != 0)
        b++; 
      mediaEntry = mediaEntry.next;
    } 
    if (b == 0)
      return null; 
    Object[] arrayOfObject = new Object[b];
    mediaEntry = this.head;
    b = 0;
    while (mediaEntry != null) {
      if (mediaEntry.getID() == paramInt && (mediaEntry.getStatus(false, false) & 0x4) != 0)
        arrayOfObject[b++] = mediaEntry.getMedia(); 
      mediaEntry = mediaEntry.next;
    } 
    return arrayOfObject;
  }
  
  public void waitForID(int paramInt) throws InterruptedException { waitForID(paramInt, 0L); }
  
  public boolean waitForID(int paramInt, long paramLong) throws InterruptedException {
    long l = System.currentTimeMillis() + paramLong;
    boolean bool = true;
    while (true) {
      long l1;
      int i = statusID(paramInt, bool, bool);
      if ((i & true) == 0)
        return (i == 8); 
      bool = false;
      if (paramLong == 0L) {
        l1 = 0L;
      } else {
        l1 = l - System.currentTimeMillis();
        if (l1 <= 0L)
          return false; 
      } 
      wait(l1);
    } 
  }
  
  public int statusID(int paramInt, boolean paramBoolean) { return statusID(paramInt, paramBoolean, true); }
  
  private int statusID(int paramInt, boolean paramBoolean1, boolean paramBoolean2) {
    MediaEntry mediaEntry = this.head;
    int i = 0;
    while (mediaEntry != null) {
      if (mediaEntry.getID() == paramInt)
        i |= mediaEntry.getStatus(paramBoolean1, paramBoolean2); 
      mediaEntry = mediaEntry.next;
    } 
    return i;
  }
  
  public void removeImage(Image paramImage) {
    removeImageImpl(paramImage);
    Image image = getResolutionVariant(paramImage);
    if (image != null)
      removeImageImpl(image); 
    notifyAll();
  }
  
  private void removeImageImpl(Image paramImage) {
    MediaEntry mediaEntry1 = this.head;
    MediaEntry mediaEntry2 = null;
    while (mediaEntry1 != null) {
      MediaEntry mediaEntry = mediaEntry1.next;
      if (mediaEntry1.getMedia() == paramImage) {
        if (mediaEntry2 == null) {
          this.head = mediaEntry;
        } else {
          mediaEntry2.next = mediaEntry;
        } 
        mediaEntry1.cancel();
      } else {
        mediaEntry2 = mediaEntry1;
      } 
      mediaEntry1 = mediaEntry;
    } 
  }
  
  public void removeImage(Image paramImage, int paramInt) {
    removeImageImpl(paramImage, paramInt);
    Image image = getResolutionVariant(paramImage);
    if (image != null)
      removeImageImpl(image, paramInt); 
    notifyAll();
  }
  
  private void removeImageImpl(Image paramImage, int paramInt) {
    MediaEntry mediaEntry1 = this.head;
    MediaEntry mediaEntry2 = null;
    while (mediaEntry1 != null) {
      MediaEntry mediaEntry = mediaEntry1.next;
      if (mediaEntry1.getID() == paramInt && mediaEntry1.getMedia() == paramImage) {
        if (mediaEntry2 == null) {
          this.head = mediaEntry;
        } else {
          mediaEntry2.next = mediaEntry;
        } 
        mediaEntry1.cancel();
      } else {
        mediaEntry2 = mediaEntry1;
      } 
      mediaEntry1 = mediaEntry;
    } 
  }
  
  public void removeImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3) {
    removeImageImpl(paramImage, paramInt1, paramInt2, paramInt3);
    Image image = getResolutionVariant(paramImage);
    if (image != null)
      removeImageImpl(image, paramInt1, (paramInt2 == -1) ? -1 : (2 * paramInt2), (paramInt3 == -1) ? -1 : (2 * paramInt3)); 
    notifyAll();
  }
  
  private void removeImageImpl(Image paramImage, int paramInt1, int paramInt2, int paramInt3) {
    MediaEntry mediaEntry1 = this.head;
    MediaEntry mediaEntry2 = null;
    while (mediaEntry1 != null) {
      MediaEntry mediaEntry = mediaEntry1.next;
      if (mediaEntry1.getID() == paramInt1 && mediaEntry1 instanceof ImageMediaEntry && ((ImageMediaEntry)mediaEntry1).matches(paramImage, paramInt2, paramInt3)) {
        if (mediaEntry2 == null) {
          this.head = mediaEntry;
        } else {
          mediaEntry2.next = mediaEntry;
        } 
        mediaEntry1.cancel();
      } else {
        mediaEntry2 = mediaEntry1;
      } 
      mediaEntry1 = mediaEntry;
    } 
  }
  
  void setDone() throws InterruptedException { notifyAll(); }
  
  private static Image getResolutionVariant(Image paramImage) { return (paramImage instanceof MultiResolutionToolkitImage) ? ((MultiResolutionToolkitImage)paramImage).getResolutionVariant() : null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\MediaTracker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */