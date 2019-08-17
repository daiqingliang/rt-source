package java.awt.datatransfer;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OptionalDataException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import sun.awt.datatransfer.DataTransferer;
import sun.reflect.misc.ReflectUtil;
import sun.security.util.SecurityConstants;

public class DataFlavor implements Externalizable, Cloneable {
  private static final long serialVersionUID = 8367026044764648243L;
  
  private static final Class<InputStream> ioInputStreamClass = InputStream.class;
  
  public static final DataFlavor stringFlavor;
  
  public static final DataFlavor imageFlavor;
  
  @Deprecated
  public static final DataFlavor plainTextFlavor;
  
  public static final String javaSerializedObjectMimeType = "application/x-java-serialized-object";
  
  public static final DataFlavor javaFileListFlavor;
  
  public static final String javaJVMLocalObjectMimeType = "application/x-java-jvm-local-objectref";
  
  public static final String javaRemoteObjectMimeType = "application/x-java-remote-object";
  
  public static DataFlavor selectionHtmlFlavor;
  
  public static DataFlavor fragmentHtmlFlavor;
  
  public static DataFlavor allHtmlFlavor = (fragmentHtmlFlavor = (selectionHtmlFlavor = (javaFileListFlavor = (plainTextFlavor = (imageFlavor = (stringFlavor = createConstant(String.class, "Unicode String")).createConstant("image/x-java-image; class=java.awt.Image", "Image")).createConstant("text/plain; charset=unicode; class=java.io.InputStream", "Plain Text")).createConstant("application/x-java-file-list;class=java.util.List", null)).initHtmlDataFlavor("selection")).initHtmlDataFlavor("fragment")).initHtmlDataFlavor("all");
  
  private static Comparator<DataFlavor> textFlavorComparator;
  
  int atom;
  
  MimeType mimeType;
  
  private String humanPresentableName;
  
  private Class<?> representationClass;
  
  protected static final Class<?> tryToLoadClass(String paramString, ClassLoader paramClassLoader) throws ClassNotFoundException {
    ReflectUtil.checkPackageAccess(paramString);
    try {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION); 
      ClassLoader classLoader = ClassLoader.getSystemClassLoader();
      try {
        return Class.forName(paramString, true, classLoader);
      } catch (ClassNotFoundException classNotFoundException) {
        classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null)
          try {
            return Class.forName(paramString, true, classLoader);
          } catch (ClassNotFoundException classNotFoundException1) {} 
      } 
    } catch (SecurityException securityException) {}
    return Class.forName(paramString, true, paramClassLoader);
  }
  
  private static DataFlavor createConstant(Class<?> paramClass, String paramString) {
    try {
      return new DataFlavor(paramClass, paramString);
    } catch (Exception exception) {
      return null;
    } 
  }
  
  private static DataFlavor createConstant(String paramString1, String paramString2) {
    try {
      return new DataFlavor(paramString1, paramString2);
    } catch (Exception exception) {
      return null;
    } 
  }
  
  private static DataFlavor initHtmlDataFlavor(String paramString) {
    try {
      return new DataFlavor("text/html; class=java.lang.String;document=" + paramString + ";charset=Unicode");
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public DataFlavor() {}
  
  private DataFlavor(String paramString1, String paramString2, MimeTypeParameterList paramMimeTypeParameterList, Class<?> paramClass, String paramString3) {
    if (paramString1 == null)
      throw new NullPointerException("primaryType"); 
    if (paramString2 == null)
      throw new NullPointerException("subType"); 
    if (paramClass == null)
      throw new NullPointerException("representationClass"); 
    if (paramMimeTypeParameterList == null)
      paramMimeTypeParameterList = new MimeTypeParameterList(); 
    paramMimeTypeParameterList.set("class", paramClass.getName());
    if (paramString3 == null) {
      paramString3 = paramMimeTypeParameterList.get("humanPresentableName");
      if (paramString3 == null)
        paramString3 = paramString1 + "/" + paramString2; 
    } 
    try {
      this.mimeType = new MimeType(paramString1, paramString2, paramMimeTypeParameterList);
    } catch (MimeTypeParseException mimeTypeParseException) {
      throw new IllegalArgumentException("MimeType Parse Exception: " + mimeTypeParseException.getMessage());
    } 
    this.representationClass = paramClass;
    this.humanPresentableName = paramString3;
    this.mimeType.removeParameter("humanPresentableName");
  }
  
  public DataFlavor(Class<?> paramClass, String paramString) {
    this("application", "x-java-serialized-object", null, paramClass, paramString);
    if (paramClass == null)
      throw new NullPointerException("representationClass"); 
  }
  
  public DataFlavor(String paramString1, String paramString2) {
    if (paramString1 == null)
      throw new NullPointerException("mimeType"); 
    try {
      initialize(paramString1, paramString2, getClass().getClassLoader());
    } catch (MimeTypeParseException mimeTypeParseException) {
      throw new IllegalArgumentException("failed to parse:" + paramString1);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new IllegalArgumentException("can't find specified class: " + classNotFoundException.getMessage());
    } 
  }
  
  public DataFlavor(String paramString1, String paramString2, ClassLoader paramClassLoader) throws ClassNotFoundException {
    if (paramString1 == null)
      throw new NullPointerException("mimeType"); 
    try {
      initialize(paramString1, paramString2, paramClassLoader);
    } catch (MimeTypeParseException mimeTypeParseException) {
      throw new IllegalArgumentException("failed to parse:" + paramString1);
    } 
  }
  
  public DataFlavor(String paramString) throws ClassNotFoundException {
    if (paramString == null)
      throw new NullPointerException("mimeType"); 
    try {
      initialize(paramString, null, getClass().getClassLoader());
    } catch (MimeTypeParseException mimeTypeParseException) {
      throw new IllegalArgumentException("failed to parse:" + paramString);
    } 
  }
  
  private void initialize(String paramString1, String paramString2, ClassLoader paramClassLoader) throws ClassNotFoundException {
    if (paramString1 == null)
      throw new NullPointerException("mimeType"); 
    this.mimeType = new MimeType(paramString1);
    String str = getParameter("class");
    if (str == null) {
      if ("application/x-java-serialized-object".equals(this.mimeType.getBaseType()))
        throw new IllegalArgumentException("no representation class specified for:" + paramString1); 
      this.representationClass = InputStream.class;
    } else {
      this.representationClass = tryToLoadClass(str, paramClassLoader);
    } 
    this.mimeType.setParameter("class", this.representationClass.getName());
    if (paramString2 == null) {
      paramString2 = this.mimeType.getParameter("humanPresentableName");
      if (paramString2 == null)
        paramString2 = this.mimeType.getPrimaryType() + "/" + this.mimeType.getSubType(); 
    } 
    this.humanPresentableName = paramString2;
    this.mimeType.removeParameter("humanPresentableName");
  }
  
  public String toString() {
    null = getClass().getName();
    return null + "[" + paramString() + "]";
  }
  
  private String paramString() {
    String str = "";
    str = str + "mimetype=";
    if (this.mimeType == null) {
      str = str + "null";
    } else {
      str = str + this.mimeType.getBaseType();
    } 
    str = str + ";representationclass=";
    if (this.representationClass == null) {
      str = str + "null";
    } else {
      str = str + this.representationClass.getName();
    } 
    if (DataTransferer.isFlavorCharsetTextType(this) && (isRepresentationClassInputStream() || isRepresentationClassByteBuffer() || byte[].class.equals(this.representationClass)))
      str = str + ";charset=" + DataTransferer.getTextCharset(this); 
    return str;
  }
  
  public static final DataFlavor getTextPlainUnicodeFlavor() {
    String str = null;
    DataTransferer dataTransferer = DataTransferer.getInstance();
    if (dataTransferer != null)
      str = dataTransferer.getDefaultUnicodeEncoding(); 
    return new DataFlavor("text/plain;charset=" + str + ";class=java.io.InputStream", "Plain Text");
  }
  
  public static final DataFlavor selectBestTextFlavor(DataFlavor[] paramArrayOfDataFlavor) {
    if (paramArrayOfDataFlavor == null || paramArrayOfDataFlavor.length == 0)
      return null; 
    if (textFlavorComparator == null)
      textFlavorComparator = new TextFlavorComparator(); 
    DataFlavor dataFlavor = (DataFlavor)Collections.max(Arrays.asList(paramArrayOfDataFlavor), textFlavorComparator);
    return !dataFlavor.isFlavorTextType() ? null : dataFlavor;
  }
  
  public Reader getReaderForText(Transferable paramTransferable) throws UnsupportedFlavorException, IOException {
    Object object = paramTransferable.getTransferData(this);
    if (object == null)
      throw new IllegalArgumentException("getTransferData() returned null"); 
    if (object instanceof Reader)
      return (Reader)object; 
    if (object instanceof String)
      return new StringReader((String)object); 
    if (object instanceof CharBuffer) {
      CharBuffer charBuffer = (CharBuffer)object;
      int i = charBuffer.remaining();
      char[] arrayOfChar = new char[i];
      charBuffer.get(arrayOfChar, 0, i);
      return new CharArrayReader(arrayOfChar);
    } 
    if (object instanceof char[])
      return new CharArrayReader((char[])object); 
    InputStream inputStream = null;
    if (object instanceof InputStream) {
      inputStream = (InputStream)object;
    } else if (object instanceof ByteBuffer) {
      ByteBuffer byteBuffer = (ByteBuffer)object;
      int i = byteBuffer.remaining();
      byte[] arrayOfByte = new byte[i];
      byteBuffer.get(arrayOfByte, 0, i);
      inputStream = new ByteArrayInputStream(arrayOfByte);
    } else if (object instanceof byte[]) {
      inputStream = new ByteArrayInputStream((byte[])object);
    } 
    if (inputStream == null)
      throw new IllegalArgumentException("transfer data is not Reader, String, CharBuffer, char array, InputStream, ByteBuffer, or byte array"); 
    String str = getParameter("charset");
    return (str == null) ? new InputStreamReader(inputStream) : new InputStreamReader(inputStream, str);
  }
  
  public String getMimeType() { return (this.mimeType != null) ? this.mimeType.toString() : null; }
  
  public Class<?> getRepresentationClass() { return this.representationClass; }
  
  public String getHumanPresentableName() { return this.humanPresentableName; }
  
  public String getPrimaryType() { return (this.mimeType != null) ? this.mimeType.getPrimaryType() : null; }
  
  public String getSubType() { return (this.mimeType != null) ? this.mimeType.getSubType() : null; }
  
  public String getParameter(String paramString) { return paramString.equals("humanPresentableName") ? this.humanPresentableName : ((this.mimeType != null) ? this.mimeType.getParameter(paramString) : null); }
  
  public void setHumanPresentableName(String paramString) throws ClassNotFoundException { this.humanPresentableName = paramString; }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof DataFlavor && equals((DataFlavor)paramObject)); }
  
  public boolean equals(DataFlavor paramDataFlavor) {
    if (paramDataFlavor == null)
      return false; 
    if (this == paramDataFlavor)
      return true; 
    if (!Objects.equals(getRepresentationClass(), paramDataFlavor.getRepresentationClass()))
      return false; 
    if (this.mimeType == null) {
      if (paramDataFlavor.mimeType != null)
        return false; 
    } else {
      if (!this.mimeType.match(paramDataFlavor.mimeType))
        return false; 
      if ("text".equals(getPrimaryType())) {
        if (DataTransferer.doesSubtypeSupportCharset(this) && this.representationClass != null && !isStandardTextRepresentationClass()) {
          String str1 = DataTransferer.canonicalName(getParameter("charset"));
          String str2 = DataTransferer.canonicalName(paramDataFlavor.getParameter("charset"));
          if (!Objects.equals(str1, str2))
            return false; 
        } 
        if ("html".equals(getSubType())) {
          String str1 = getParameter("document");
          String str2 = paramDataFlavor.getParameter("document");
          if (!Objects.equals(str1, str2))
            return false; 
        } 
      } 
    } 
    return true;
  }
  
  @Deprecated
  public boolean equals(String paramString) { return (paramString == null || this.mimeType == null) ? false : isMimeTypeEqual(paramString); }
  
  public int hashCode() {
    int i = 0;
    if (this.representationClass != null)
      i += this.representationClass.hashCode(); 
    if (this.mimeType != null) {
      String str = this.mimeType.getPrimaryType();
      if (str != null)
        i += str.hashCode(); 
      if ("text".equals(str)) {
        if (DataTransferer.doesSubtypeSupportCharset(this) && this.representationClass != null && !isStandardTextRepresentationClass()) {
          String str1 = DataTransferer.canonicalName(getParameter("charset"));
          if (str1 != null)
            i += str1.hashCode(); 
        } 
        if ("html".equals(getSubType())) {
          String str1 = getParameter("document");
          if (str1 != null)
            i += str1.hashCode(); 
        } 
      } 
    } 
    return i;
  }
  
  public boolean match(DataFlavor paramDataFlavor) { return equals(paramDataFlavor); }
  
  public boolean isMimeTypeEqual(String paramString) {
    if (paramString == null)
      throw new NullPointerException("mimeType"); 
    if (this.mimeType == null)
      return false; 
    try {
      return this.mimeType.match(new MimeType(paramString));
    } catch (MimeTypeParseException mimeTypeParseException) {
      return false;
    } 
  }
  
  public final boolean isMimeTypeEqual(DataFlavor paramDataFlavor) { return isMimeTypeEqual(paramDataFlavor.mimeType); }
  
  private boolean isMimeTypeEqual(MimeType paramMimeType) { return (this.mimeType == null) ? ((paramMimeType == null)) : this.mimeType.match(paramMimeType); }
  
  private boolean isStandardTextRepresentationClass() { return (isRepresentationClassReader() || String.class.equals(this.representationClass) || isRepresentationClassCharBuffer() || char[].class.equals(this.representationClass)); }
  
  public boolean isMimeTypeSerializedObject() { return isMimeTypeEqual("application/x-java-serialized-object"); }
  
  public final Class<?> getDefaultRepresentationClass() { return ioInputStreamClass; }
  
  public final String getDefaultRepresentationClassAsString() { return getDefaultRepresentationClass().getName(); }
  
  public boolean isRepresentationClassInputStream() { return ioInputStreamClass.isAssignableFrom(this.representationClass); }
  
  public boolean isRepresentationClassReader() { return Reader.class.isAssignableFrom(this.representationClass); }
  
  public boolean isRepresentationClassCharBuffer() { return CharBuffer.class.isAssignableFrom(this.representationClass); }
  
  public boolean isRepresentationClassByteBuffer() { return ByteBuffer.class.isAssignableFrom(this.representationClass); }
  
  public boolean isRepresentationClassSerializable() { return java.io.Serializable.class.isAssignableFrom(this.representationClass); }
  
  public boolean isRepresentationClassRemote() { return DataTransferer.isRemote(this.representationClass); }
  
  public boolean isFlavorSerializedObjectType() { return (isRepresentationClassSerializable() && isMimeTypeEqual("application/x-java-serialized-object")); }
  
  public boolean isFlavorRemoteObjectType() { return (isRepresentationClassRemote() && isRepresentationClassSerializable() && isMimeTypeEqual("application/x-java-remote-object")); }
  
  public boolean isFlavorJavaFileListType() { return (this.mimeType == null || this.representationClass == null) ? false : ((java.util.List.class.isAssignableFrom(this.representationClass) && this.mimeType.match(javaFileListFlavor.mimeType))); }
  
  public boolean isFlavorTextType() { return (DataTransferer.isFlavorCharsetTextType(this) || DataTransferer.isFlavorNoncharsetTextType(this)); }
  
  public void writeExternal(ObjectOutput paramObjectOutput) throws IOException {
    if (this.mimeType != null) {
      this.mimeType.setParameter("humanPresentableName", this.humanPresentableName);
      paramObjectOutput.writeObject(this.mimeType);
      this.mimeType.removeParameter("humanPresentableName");
    } else {
      paramObjectOutput.writeObject(null);
    } 
    paramObjectOutput.writeObject(this.representationClass);
  }
  
  public void readExternal(ObjectInput paramObjectInput) throws IOException, ClassNotFoundException {
    String str = null;
    this.mimeType = (MimeType)paramObjectInput.readObject();
    if (this.mimeType != null) {
      this.humanPresentableName = this.mimeType.getParameter("humanPresentableName");
      this.mimeType.removeParameter("humanPresentableName");
      str = this.mimeType.getParameter("class");
      if (str == null)
        throw new IOException("no class parameter specified in: " + this.mimeType); 
    } 
    try {
      this.representationClass = (Class)paramObjectInput.readObject();
    } catch (OptionalDataException optionalDataException) {
      if (!optionalDataException.eof || optionalDataException.length != 0)
        throw optionalDataException; 
      if (str != null)
        this.representationClass = tryToLoadClass(str, getClass().getClassLoader()); 
    } 
  }
  
  public Object clone() throws CloneNotSupportedException {
    Object object = super.clone();
    if (this.mimeType != null)
      ((DataFlavor)object).mimeType = (MimeType)this.mimeType.clone(); 
    return object;
  }
  
  @Deprecated
  protected String normalizeMimeTypeParameter(String paramString1, String paramString2) { return paramString2; }
  
  @Deprecated
  protected String normalizeMimeType(String paramString) { return paramString; }
  
  static class TextFlavorComparator extends DataTransferer.DataFlavorComparator {
    public int compare(Object param1Object1, Object param1Object2) {
      DataFlavor dataFlavor1 = (DataFlavor)param1Object1;
      DataFlavor dataFlavor2 = (DataFlavor)param1Object2;
      return dataFlavor1.isFlavorTextType() ? (dataFlavor2.isFlavorTextType() ? super.compare(param1Object1, param1Object2) : 1) : (dataFlavor2.isFlavorTextType() ? -1 : 0);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\datatransfer\DataFlavor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */