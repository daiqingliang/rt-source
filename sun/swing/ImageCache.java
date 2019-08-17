package sun.swing;

import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.lang.ref.SoftReference;
import java.util.LinkedList;
import java.util.ListIterator;

public class ImageCache {
  private int maxCount;
  
  private final LinkedList<SoftReference<Entry>> entries;
  
  public ImageCache(int paramInt) {
    this.maxCount = paramInt;
    this.entries = new LinkedList();
  }
  
  void setMaxCount(int paramInt) { this.maxCount = paramInt; }
  
  public void flush() { this.entries.clear(); }
  
  private Entry getEntry(Object paramObject, GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, Object[] paramArrayOfObject) {
    ListIterator listIterator = this.entries.listIterator();
    while (listIterator.hasNext()) {
      SoftReference softReference = (SoftReference)listIterator.next();
      Entry entry1 = (Entry)softReference.get();
      if (entry1 == null) {
        listIterator.remove();
        continue;
      } 
      if (entry1.equals(paramGraphicsConfiguration, paramInt1, paramInt2, paramArrayOfObject)) {
        listIterator.remove();
        this.entries.addFirst(softReference);
        return entry1;
      } 
    } 
    Entry entry = new Entry(paramGraphicsConfiguration, paramInt1, paramInt2, paramArrayOfObject);
    if (this.entries.size() >= this.maxCount)
      this.entries.removeLast(); 
    this.entries.addFirst(new SoftReference(entry));
    return entry;
  }
  
  public Image getImage(Object paramObject, GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, Object[] paramArrayOfObject) {
    Entry entry = getEntry(paramObject, paramGraphicsConfiguration, paramInt1, paramInt2, paramArrayOfObject);
    return entry.getImage();
  }
  
  public void setImage(Object paramObject, GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, Object[] paramArrayOfObject, Image paramImage) {
    Entry entry = getEntry(paramObject, paramGraphicsConfiguration, paramInt1, paramInt2, paramArrayOfObject);
    entry.setImage(paramImage);
  }
  
  private static class Entry {
    private final GraphicsConfiguration config;
    
    private final int w;
    
    private final int h;
    
    private final Object[] args;
    
    private Image image;
    
    Entry(GraphicsConfiguration param1GraphicsConfiguration, int param1Int1, int param1Int2, Object[] param1ArrayOfObject) {
      this.config = param1GraphicsConfiguration;
      this.args = param1ArrayOfObject;
      this.w = param1Int1;
      this.h = param1Int2;
    }
    
    public void setImage(Image param1Image) { this.image = param1Image; }
    
    public Image getImage() { return this.image; }
    
    public String toString() {
      null = super.toString() + "[ graphicsConfig=" + this.config + ", image=" + this.image + ", w=" + this.w + ", h=" + this.h;
      if (this.args != null)
        for (byte b = 0; b < this.args.length; b++)
          null = null + ", " + this.args[b];  
      return null + "]";
    }
    
    public boolean equals(GraphicsConfiguration param1GraphicsConfiguration, int param1Int1, int param1Int2, Object[] param1ArrayOfObject) {
      if (this.w == param1Int1 && this.h == param1Int2 && ((this.config != null && this.config.equals(param1GraphicsConfiguration)) || (this.config == null && param1GraphicsConfiguration == null))) {
        if (this.args == null && param1ArrayOfObject == null)
          return true; 
        if (this.args != null && param1ArrayOfObject != null && this.args.length == param1ArrayOfObject.length) {
          for (int i = param1ArrayOfObject.length - 1; i >= 0; i--) {
            Object object1 = this.args[i];
            Object object2 = param1ArrayOfObject[i];
            if ((object1 == null && object2 != null) || (object1 != null && !object1.equals(object2)))
              return false; 
          } 
          return true;
        } 
      } 
      return false;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\swing\ImageCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */