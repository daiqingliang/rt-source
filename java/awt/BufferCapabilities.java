package java.awt;

public class BufferCapabilities implements Cloneable {
  private ImageCapabilities frontCaps;
  
  private ImageCapabilities backCaps;
  
  private FlipContents flipContents;
  
  public BufferCapabilities(ImageCapabilities paramImageCapabilities1, ImageCapabilities paramImageCapabilities2, FlipContents paramFlipContents) {
    if (paramImageCapabilities1 == null || paramImageCapabilities2 == null)
      throw new IllegalArgumentException("Image capabilities specified cannot be null"); 
    this.frontCaps = paramImageCapabilities1;
    this.backCaps = paramImageCapabilities2;
    this.flipContents = paramFlipContents;
  }
  
  public ImageCapabilities getFrontBufferCapabilities() { return this.frontCaps; }
  
  public ImageCapabilities getBackBufferCapabilities() { return this.backCaps; }
  
  public boolean isPageFlipping() { return (getFlipContents() != null); }
  
  public FlipContents getFlipContents() { return this.flipContents; }
  
  public boolean isFullScreenRequired() { return false; }
  
  public boolean isMultiBufferAvailable() { return false; }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  public static final class FlipContents extends AttributeValue {
    private static int I_UNDEFINED = 0;
    
    private static int I_BACKGROUND = 1;
    
    private static int I_PRIOR = 2;
    
    private static int I_COPIED = 3;
    
    private static final String[] NAMES = { "undefined", "background", "prior", "copied" };
    
    public static final FlipContents UNDEFINED = new FlipContents(I_UNDEFINED);
    
    public static final FlipContents BACKGROUND = new FlipContents(I_BACKGROUND);
    
    public static final FlipContents PRIOR = new FlipContents(I_PRIOR);
    
    public static final FlipContents COPIED = new FlipContents(I_COPIED);
    
    private FlipContents(int param1Int) { super(param1Int, NAMES); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\BufferCapabilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */