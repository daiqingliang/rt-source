package javax.imageio.metadata;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.w3c.dom.Node;

public abstract class IIOMetadata {
  protected boolean standardFormatSupported;
  
  protected String nativeMetadataFormatName = null;
  
  protected String nativeMetadataFormatClassName = null;
  
  protected String[] extraMetadataFormatNames = null;
  
  protected String[] extraMetadataFormatClassNames = null;
  
  protected IIOMetadataController defaultController = null;
  
  protected IIOMetadataController controller = null;
  
  protected IIOMetadata() {}
  
  protected IIOMetadata(boolean paramBoolean, String paramString1, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2) {
    this.standardFormatSupported = paramBoolean;
    this.nativeMetadataFormatName = paramString1;
    this.nativeMetadataFormatClassName = paramString2;
    if (paramArrayOfString1 != null) {
      if (paramArrayOfString1.length == 0)
        throw new IllegalArgumentException("extraMetadataFormatNames.length == 0!"); 
      if (paramArrayOfString2 == null)
        throw new IllegalArgumentException("extraMetadataFormatNames != null && extraMetadataFormatClassNames == null!"); 
      if (paramArrayOfString2.length != paramArrayOfString1.length)
        throw new IllegalArgumentException("extraMetadataFormatClassNames.length != extraMetadataFormatNames.length!"); 
      this.extraMetadataFormatNames = (String[])paramArrayOfString1.clone();
      this.extraMetadataFormatClassNames = (String[])paramArrayOfString2.clone();
    } else if (paramArrayOfString2 != null) {
      throw new IllegalArgumentException("extraMetadataFormatNames == null && extraMetadataFormatClassNames != null!");
    } 
  }
  
  public boolean isStandardMetadataFormatSupported() { return this.standardFormatSupported; }
  
  public abstract boolean isReadOnly();
  
  public String getNativeMetadataFormatName() { return this.nativeMetadataFormatName; }
  
  public String[] getExtraMetadataFormatNames() { return (this.extraMetadataFormatNames == null) ? null : (String[])this.extraMetadataFormatNames.clone(); }
  
  public String[] getMetadataFormatNames() {
    String str1 = getNativeMetadataFormatName();
    String str2 = isStandardMetadataFormatSupported() ? "javax_imageio_1.0" : null;
    String[] arrayOfString1 = getExtraMetadataFormatNames();
    int i = 0;
    if (str1 != null)
      i++; 
    if (str2 != null)
      i++; 
    if (arrayOfString1 != null)
      i += arrayOfString1.length; 
    if (i == 0)
      return null; 
    String[] arrayOfString2 = new String[i];
    byte b = 0;
    if (str1 != null)
      arrayOfString2[b++] = str1; 
    if (str2 != null)
      arrayOfString2[b++] = str2; 
    if (arrayOfString1 != null)
      for (byte b1 = 0; b1 < arrayOfString1.length; b1++)
        arrayOfString2[b++] = arrayOfString1[b1];  
    return arrayOfString2;
  }
  
  public IIOMetadataFormat getMetadataFormat(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("formatName == null!"); 
    if (this.standardFormatSupported && paramString.equals("javax_imageio_1.0"))
      return IIOMetadataFormatImpl.getStandardFormatInstance(); 
    String str = null;
    if (paramString.equals(this.nativeMetadataFormatName)) {
      str = this.nativeMetadataFormatClassName;
    } else if (this.extraMetadataFormatNames != null) {
      for (byte b = 0; b < this.extraMetadataFormatNames.length; b++) {
        if (paramString.equals(this.extraMetadataFormatNames[b])) {
          str = this.extraMetadataFormatClassNames[b];
          break;
        } 
      } 
    } 
    if (str == null)
      throw new IllegalArgumentException("Unsupported format name"); 
    try {
      Class clazz = null;
      final IIOMetadata o = this;
      ClassLoader classLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() { return o.getClass().getClassLoader(); }
          });
      try {
        clazz = Class.forName(str, true, classLoader);
      } catch (ClassNotFoundException classNotFoundException) {
        classLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
              public Object run() { return Thread.currentThread().getContextClassLoader(); }
            });
        try {
          clazz = Class.forName(str, true, classLoader);
        } catch (ClassNotFoundException classNotFoundException1) {
          clazz = Class.forName(str, true, ClassLoader.getSystemClassLoader());
        } 
      } 
      Method method = clazz.getMethod("getInstance", new Class[0]);
      return (IIOMetadataFormat)method.invoke(null, new Object[0]);
    } catch (Exception exception) {
      IllegalStateException illegalStateException = new IllegalStateException("Can't obtain format");
      illegalStateException.initCause(exception);
      throw illegalStateException;
    } 
  }
  
  public abstract Node getAsTree(String paramString);
  
  public abstract void mergeTree(String paramString, Node paramNode) throws IIOInvalidTreeException;
  
  protected IIOMetadataNode getStandardChromaNode() { return null; }
  
  protected IIOMetadataNode getStandardCompressionNode() { return null; }
  
  protected IIOMetadataNode getStandardDataNode() { return null; }
  
  protected IIOMetadataNode getStandardDimensionNode() { return null; }
  
  protected IIOMetadataNode getStandardDocumentNode() { return null; }
  
  protected IIOMetadataNode getStandardTextNode() { return null; }
  
  protected IIOMetadataNode getStandardTileNode() { return null; }
  
  protected IIOMetadataNode getStandardTransparencyNode() { return null; }
  
  private void append(IIOMetadataNode paramIIOMetadataNode1, IIOMetadataNode paramIIOMetadataNode2) {
    if (paramIIOMetadataNode2 != null)
      paramIIOMetadataNode1.appendChild(paramIIOMetadataNode2); 
  }
  
  protected final IIOMetadataNode getStandardTree() {
    IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("javax_imageio_1.0");
    append(iIOMetadataNode, getStandardChromaNode());
    append(iIOMetadataNode, getStandardCompressionNode());
    append(iIOMetadataNode, getStandardDataNode());
    append(iIOMetadataNode, getStandardDimensionNode());
    append(iIOMetadataNode, getStandardDocumentNode());
    append(iIOMetadataNode, getStandardTextNode());
    append(iIOMetadataNode, getStandardTileNode());
    append(iIOMetadataNode, getStandardTransparencyNode());
    return iIOMetadataNode;
  }
  
  public void setFromTree(String paramString, Node paramNode) throws IIOInvalidTreeException {
    reset();
    mergeTree(paramString, paramNode);
  }
  
  public abstract void reset();
  
  public void setController(IIOMetadataController paramIIOMetadataController) { this.controller = paramIIOMetadataController; }
  
  public IIOMetadataController getController() { return this.controller; }
  
  public IIOMetadataController getDefaultController() { return this.defaultController; }
  
  public boolean hasController() { return (getController() != null); }
  
  public boolean activateController() {
    if (!hasController())
      throw new IllegalStateException("hasController() == false!"); 
    return getController().activate(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\metadata\IIOMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */