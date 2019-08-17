package javax.imageio.spi;

import java.lang.reflect.Method;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;

public abstract class ImageReaderWriterSpi extends IIOServiceProvider {
  protected String[] names = null;
  
  protected String[] suffixes = null;
  
  protected String[] MIMETypes = null;
  
  protected String pluginClassName = null;
  
  protected boolean supportsStandardStreamMetadataFormat = false;
  
  protected String nativeStreamMetadataFormatName = null;
  
  protected String nativeStreamMetadataFormatClassName = null;
  
  protected String[] extraStreamMetadataFormatNames = null;
  
  protected String[] extraStreamMetadataFormatClassNames = null;
  
  protected boolean supportsStandardImageMetadataFormat = false;
  
  protected String nativeImageMetadataFormatName = null;
  
  protected String nativeImageMetadataFormatClassName = null;
  
  protected String[] extraImageMetadataFormatNames = null;
  
  protected String[] extraImageMetadataFormatClassNames = null;
  
  public ImageReaderWriterSpi(String paramString1, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3, String paramString3, boolean paramBoolean1, String paramString4, String paramString5, String[] paramArrayOfString4, String[] paramArrayOfString5, boolean paramBoolean2, String paramString6, String paramString7, String[] paramArrayOfString6, String[] paramArrayOfString7) {
    super(paramString1, paramString2);
    if (paramArrayOfString1 == null)
      throw new IllegalArgumentException("names == null!"); 
    if (paramArrayOfString1.length == 0)
      throw new IllegalArgumentException("names.length == 0!"); 
    if (paramString3 == null)
      throw new IllegalArgumentException("pluginClassName == null!"); 
    this.names = (String[])paramArrayOfString1.clone();
    if (paramArrayOfString2 != null && paramArrayOfString2.length > 0)
      this.suffixes = (String[])paramArrayOfString2.clone(); 
    if (paramArrayOfString3 != null && paramArrayOfString3.length > 0)
      this.MIMETypes = (String[])paramArrayOfString3.clone(); 
    this.pluginClassName = paramString3;
    this.supportsStandardStreamMetadataFormat = paramBoolean1;
    this.nativeStreamMetadataFormatName = paramString4;
    this.nativeStreamMetadataFormatClassName = paramString5;
    if (paramArrayOfString4 != null && paramArrayOfString4.length > 0)
      this.extraStreamMetadataFormatNames = (String[])paramArrayOfString4.clone(); 
    if (paramArrayOfString5 != null && paramArrayOfString5.length > 0)
      this.extraStreamMetadataFormatClassNames = (String[])paramArrayOfString5.clone(); 
    this.supportsStandardImageMetadataFormat = paramBoolean2;
    this.nativeImageMetadataFormatName = paramString6;
    this.nativeImageMetadataFormatClassName = paramString7;
    if (paramArrayOfString6 != null && paramArrayOfString6.length > 0)
      this.extraImageMetadataFormatNames = (String[])paramArrayOfString6.clone(); 
    if (paramArrayOfString7 != null && paramArrayOfString7.length > 0)
      this.extraImageMetadataFormatClassNames = (String[])paramArrayOfString7.clone(); 
  }
  
  public ImageReaderWriterSpi() {}
  
  public String[] getFormatNames() { return (String[])this.names.clone(); }
  
  public String[] getFileSuffixes() { return (this.suffixes == null) ? null : (String[])this.suffixes.clone(); }
  
  public String[] getMIMETypes() { return (this.MIMETypes == null) ? null : (String[])this.MIMETypes.clone(); }
  
  public String getPluginClassName() { return this.pluginClassName; }
  
  public boolean isStandardStreamMetadataFormatSupported() { return this.supportsStandardStreamMetadataFormat; }
  
  public String getNativeStreamMetadataFormatName() { return this.nativeStreamMetadataFormatName; }
  
  public String[] getExtraStreamMetadataFormatNames() { return (this.extraStreamMetadataFormatNames == null) ? null : (String[])this.extraStreamMetadataFormatNames.clone(); }
  
  public boolean isStandardImageMetadataFormatSupported() { return this.supportsStandardImageMetadataFormat; }
  
  public String getNativeImageMetadataFormatName() { return this.nativeImageMetadataFormatName; }
  
  public String[] getExtraImageMetadataFormatNames() { return (this.extraImageMetadataFormatNames == null) ? null : (String[])this.extraImageMetadataFormatNames.clone(); }
  
  public IIOMetadataFormat getStreamMetadataFormat(String paramString) { return getMetadataFormat(paramString, this.supportsStandardStreamMetadataFormat, this.nativeStreamMetadataFormatName, this.nativeStreamMetadataFormatClassName, this.extraStreamMetadataFormatNames, this.extraStreamMetadataFormatClassNames); }
  
  public IIOMetadataFormat getImageMetadataFormat(String paramString) { return getMetadataFormat(paramString, this.supportsStandardImageMetadataFormat, this.nativeImageMetadataFormatName, this.nativeImageMetadataFormatClassName, this.extraImageMetadataFormatNames, this.extraImageMetadataFormatClassNames); }
  
  private IIOMetadataFormat getMetadataFormat(String paramString1, boolean paramBoolean, String paramString2, String paramString3, String[] paramArrayOfString1, String[] paramArrayOfString2) {
    if (paramString1 == null)
      throw new IllegalArgumentException("formatName == null!"); 
    if (paramBoolean && paramString1.equals("javax_imageio_1.0"))
      return IIOMetadataFormatImpl.getStandardFormatInstance(); 
    String str = null;
    if (paramString1.equals(paramString2)) {
      str = paramString3;
    } else if (paramArrayOfString1 != null) {
      for (byte b = 0; b < paramArrayOfString1.length; b++) {
        if (paramString1.equals(paramArrayOfString1[b])) {
          str = paramArrayOfString2[b];
          break;
        } 
      } 
    } 
    if (str == null)
      throw new IllegalArgumentException("Unsupported format name"); 
    try {
      Class clazz = Class.forName(str, true, ClassLoader.getSystemClassLoader());
      Method method = clazz.getMethod("getInstance", new Class[0]);
      return (IIOMetadataFormat)method.invoke(null, new Object[0]);
    } catch (Exception exception) {
      IllegalStateException illegalStateException = new IllegalStateException("Can't obtain format");
      illegalStateException.initCause(exception);
      throw illegalStateException;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\spi\ImageReaderWriterSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */