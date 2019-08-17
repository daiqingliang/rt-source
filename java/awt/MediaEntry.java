package java.awt;

abstract class MediaEntry {
  MediaTracker tracker;
  
  int ID;
  
  MediaEntry next;
  
  int status;
  
  boolean cancelled;
  
  static final int LOADING = 1;
  
  static final int ABORTED = 2;
  
  static final int ERRORED = 4;
  
  static final int COMPLETE = 8;
  
  static final int LOADSTARTED = 13;
  
  static final int DONE = 14;
  
  MediaEntry(MediaTracker paramMediaTracker, int paramInt) {
    this.tracker = paramMediaTracker;
    this.ID = paramInt;
  }
  
  abstract Object getMedia();
  
  static MediaEntry insert(MediaEntry paramMediaEntry1, MediaEntry paramMediaEntry2) {
    MediaEntry mediaEntry1 = paramMediaEntry1;
    MediaEntry mediaEntry2 = null;
    while (mediaEntry1 != null && mediaEntry1.ID <= paramMediaEntry2.ID) {
      mediaEntry2 = mediaEntry1;
      mediaEntry1 = mediaEntry1.next;
    } 
    paramMediaEntry2.next = mediaEntry1;
    if (mediaEntry2 == null) {
      paramMediaEntry1 = paramMediaEntry2;
    } else {
      mediaEntry2.next = paramMediaEntry2;
    } 
    return paramMediaEntry1;
  }
  
  int getID() { return this.ID; }
  
  abstract void startLoad();
  
  void cancel() { this.cancelled = true; }
  
  int getStatus(boolean paramBoolean1, boolean paramBoolean2) {
    if (paramBoolean1 && (this.status & 0xD) == 0) {
      this.status = this.status & 0xFFFFFFFD | true;
      startLoad();
    } 
    return this.status;
  }
  
  void setStatus(int paramInt) {
    synchronized (this) {
      this.status = paramInt;
    } 
    this.tracker.setDone();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\MediaEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */