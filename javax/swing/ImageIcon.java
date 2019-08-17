package javax.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.IllegalComponentStateException;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.beans.ConstructorProperties;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleStateSet;
import sun.awt.AppContext;

public class ImageIcon implements Icon, Serializable, Accessible {
  private String filename;
  
  private URL location;
  
  Image image;
  
  int loadStatus = 0;
  
  ImageObserver imageObserver;
  
  String description = null;
  
  @Deprecated
  protected static final Component component = (Component)AccessController.doPrivileged(new PrivilegedAction<Component>() {
        public Component run() {
          try {
            Component component = ImageIcon.createNoPermsComponent();
            Field field = Component.class.getDeclaredField("appContext");
            field.setAccessible(true);
            field.set(component, null);
            return component;
          } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
          } 
        }
      });
  
  @Deprecated
  protected static final MediaTracker tracker = new MediaTracker(component);
  
  private static int mediaTrackerID;
  
  private static final Object TRACKER_KEY = new StringBuilder("TRACKER_KEY");
  
  int width = -1;
  
  int height = -1;
  
  private AccessibleImageIcon accessibleContext = null;
  
  private static Component createNoPermsComponent() { return (Component)AccessController.doPrivileged(new PrivilegedAction<Component>() {
          public Component run() { return new Component() {
              
              },  ; }
        },  new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, null) })); }
  
  public ImageIcon(String paramString1, String paramString2) {
    this.image = Toolkit.getDefaultToolkit().getImage(paramString1);
    if (this.image == null)
      return; 
    this.filename = paramString1;
    this.description = paramString2;
    loadImage(this.image);
  }
  
  @ConstructorProperties({"description"})
  public ImageIcon(String paramString) { this(paramString, paramString); }
  
  public ImageIcon(URL paramURL, String paramString) {
    this.image = Toolkit.getDefaultToolkit().getImage(paramURL);
    if (this.image == null)
      return; 
    this.location = paramURL;
    this.description = paramString;
    loadImage(this.image);
  }
  
  public ImageIcon(URL paramURL) { this(paramURL, paramURL.toExternalForm()); }
  
  public ImageIcon(Image paramImage, String paramString) {
    this(paramImage);
    this.description = paramString;
  }
  
  public ImageIcon(Image paramImage) {
    this.image = paramImage;
    Object object = paramImage.getProperty("comment", this.imageObserver);
    if (object instanceof String)
      this.description = (String)object; 
    loadImage(paramImage);
  }
  
  public ImageIcon(byte[] paramArrayOfByte, String paramString) {
    this.image = Toolkit.getDefaultToolkit().createImage(paramArrayOfByte);
    if (this.image == null)
      return; 
    this.description = paramString;
    loadImage(this.image);
  }
  
  public ImageIcon(byte[] paramArrayOfByte) {
    this.image = Toolkit.getDefaultToolkit().createImage(paramArrayOfByte);
    if (this.image == null)
      return; 
    Object object = this.image.getProperty("comment", this.imageObserver);
    if (object instanceof String)
      this.description = (String)object; 
    loadImage(this.image);
  }
  
  public ImageIcon() {}
  
  protected void loadImage(Image paramImage) {
    MediaTracker mediaTracker = getTracker();
    synchronized (mediaTracker) {
      int i = getNextID();
      mediaTracker.addImage(paramImage, i);
      try {
        mediaTracker.waitForID(i, 0L);
      } catch (InterruptedException interruptedException) {
        System.out.println("INTERRUPTED while loading Image");
      } 
      this.loadStatus = mediaTracker.statusID(i, false);
      mediaTracker.removeImage(paramImage, i);
      this.width = paramImage.getWidth(this.imageObserver);
      this.height = paramImage.getHeight(this.imageObserver);
    } 
  }
  
  private int getNextID() {
    synchronized (getTracker()) {
      return ++mediaTrackerID;
    } 
  }
  
  private MediaTracker getTracker() {
    Object object;
    AppContext appContext = AppContext.getAppContext();
    synchronized (appContext) {
      object = appContext.get(TRACKER_KEY);
      if (object == null) {
        Component component1 = new Component() {
          
          };
        object = new MediaTracker(component1);
        appContext.put(TRACKER_KEY, object);
      } 
    } 
    return (MediaTracker)object;
  }
  
  public int getImageLoadStatus() { return this.loadStatus; }
  
  @Transient
  public Image getImage() { return this.image; }
  
  public void setImage(Image paramImage) {
    this.image = paramImage;
    loadImage(paramImage);
  }
  
  public String getDescription() { return this.description; }
  
  public void setDescription(String paramString) { this.description = paramString; }
  
  public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) {
    if (this.imageObserver == null) {
      paramGraphics.drawImage(this.image, paramInt1, paramInt2, paramComponent);
    } else {
      paramGraphics.drawImage(this.image, paramInt1, paramInt2, this.imageObserver);
    } 
  }
  
  public int getIconWidth() { return this.width; }
  
  public int getIconHeight() { return this.height; }
  
  public void setImageObserver(ImageObserver paramImageObserver) { this.imageObserver = paramImageObserver; }
  
  @Transient
  public ImageObserver getImageObserver() { return this.imageObserver; }
  
  public String toString() { return (this.description != null) ? this.description : super.toString(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    int j = paramObjectInputStream.readInt();
    int[] arrayOfInt = (int[])paramObjectInputStream.readObject();
    if (arrayOfInt != null) {
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      ColorModel colorModel = ColorModel.getRGBdefault();
      this.image = toolkit.createImage(new MemoryImageSource(i, j, colorModel, arrayOfInt, 0, i));
      loadImage(this.image);
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    int i = getIconWidth();
    int j = getIconHeight();
    int[] arrayOfInt = (this.image != null) ? new int[i * j] : null;
    if (this.image != null)
      try {
        PixelGrabber pixelGrabber = new PixelGrabber(this.image, 0, 0, i, j, arrayOfInt, 0, i);
        pixelGrabber.grabPixels();
        if ((pixelGrabber.getStatus() & 0x80) != 0)
          throw new IOException("failed to load image contents"); 
      } catch (InterruptedException interruptedException) {
        throw new IOException("image load interrupted");
      }  
    paramObjectOutputStream.writeInt(i);
    paramObjectOutputStream.writeInt(j);
    paramObjectOutputStream.writeObject(arrayOfInt);
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleImageIcon(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleImageIcon extends AccessibleContext implements AccessibleIcon, Serializable {
    public AccessibleRole getAccessibleRole() { return AccessibleRole.ICON; }
    
    public AccessibleStateSet getAccessibleStateSet() { return null; }
    
    public Accessible getAccessibleParent() { return null; }
    
    public int getAccessibleIndexInParent() { return -1; }
    
    public int getAccessibleChildrenCount() { return 0; }
    
    public Accessible getAccessibleChild(int param1Int) { return null; }
    
    public Locale getLocale() throws IllegalComponentStateException { return null; }
    
    public String getAccessibleIconDescription() { return ImageIcon.this.getDescription(); }
    
    public void setAccessibleIconDescription(String param1String) { ImageIcon.this.setDescription(param1String); }
    
    public int getAccessibleIconHeight() { return ImageIcon.this.height; }
    
    public int getAccessibleIconWidth() { return ImageIcon.this.width; }
    
    private void readObject(ObjectInputStream param1ObjectInputStream) throws ClassNotFoundException, IOException { param1ObjectInputStream.defaultReadObject(); }
    
    private void writeObject(ObjectOutputStream param1ObjectOutputStream) throws IOException { param1ObjectOutputStream.defaultWriteObject(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\ImageIcon.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */