package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.URIResolver;

public final class TemplatesImpl implements Templates, Serializable {
  static final long serialVersionUID = 673094361519270707L;
  
  public static final String DESERIALIZE_TRANSLET = "jdk.xml.enableTemplatesImplDeserialization";
  
  private static String ABSTRACT_TRANSLET = "com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet";
  
  private String _name = null;
  
  private byte[][] _bytecodes = (byte[][])null;
  
  private Class[] _class = null;
  
  private int _transletIndex = -1;
  
  private Map<String, Class<?>> _auxClasses = null;
  
  private Properties _outputProperties;
  
  private int _indentNumber;
  
  private URIResolver _uriResolver = null;
  
  private ThreadLocal _sdom = new ThreadLocal();
  
  private TransformerFactoryImpl _tfactory = null;
  
  private boolean _overrideDefaultParser;
  
  private String _accessExternalStylesheet = "all";
  
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("_name", String.class), new ObjectStreamField("_bytecodes", byte[][].class), new ObjectStreamField("_class", Class[].class), new ObjectStreamField("_transletIndex", int.class), new ObjectStreamField("_outputProperties", Properties.class), new ObjectStreamField("_indentNumber", int.class) };
  
  protected TemplatesImpl(byte[][] paramArrayOfByte, String paramString, Properties paramProperties, int paramInt, TransformerFactoryImpl paramTransformerFactoryImpl) {
    this._bytecodes = paramArrayOfByte;
    init(paramString, paramProperties, paramInt, paramTransformerFactoryImpl);
  }
  
  protected TemplatesImpl(Class[] paramArrayOfClass, String paramString, Properties paramProperties, int paramInt, TransformerFactoryImpl paramTransformerFactoryImpl) {
    this._class = paramArrayOfClass;
    this._transletIndex = 0;
    init(paramString, paramProperties, paramInt, paramTransformerFactoryImpl);
  }
  
  private void init(String paramString, Properties paramProperties, int paramInt, TransformerFactoryImpl paramTransformerFactoryImpl) {
    this._name = paramString;
    this._outputProperties = paramProperties;
    this._indentNumber = paramInt;
    this._tfactory = paramTransformerFactoryImpl;
    this._overrideDefaultParser = paramTransformerFactoryImpl.overrideDefaultParser();
    this._accessExternalStylesheet = (String)paramTransformerFactoryImpl.getAttribute("http://javax.xml.XMLConstants/property/accessExternalStylesheet");
  }
  
  public TemplatesImpl() {}
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      String str = SecuritySupport.getSystemProperty("jdk.xml.enableTemplatesImplDeserialization");
      if (str == null || (str.length() != 0 && !str.equalsIgnoreCase("true"))) {
        ErrorMsg errorMsg = new ErrorMsg("DESERIALIZE_TEMPLATES_ERR");
        throw new UnsupportedOperationException(errorMsg.toString());
      } 
    } 
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    this._name = (String)getField.get("_name", null);
    this._bytecodes = (byte[][])getField.get("_bytecodes", null);
    this._class = (Class[])getField.get("_class", null);
    this._transletIndex = getField.get("_transletIndex", -1);
    this._outputProperties = (Properties)getField.get("_outputProperties", null);
    this._indentNumber = getField.get("_indentNumber", 0);
    if (paramObjectInputStream.readBoolean())
      this._uriResolver = (URIResolver)paramObjectInputStream.readObject(); 
    this._tfactory = new TransformerFactoryImpl();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException, ClassNotFoundException {
    if (this._auxClasses != null)
      throw new NotSerializableException("com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable"); 
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("_name", this._name);
    putField.put("_bytecodes", this._bytecodes);
    putField.put("_class", this._class);
    putField.put("_transletIndex", this._transletIndex);
    putField.put("_outputProperties", this._outputProperties);
    putField.put("_indentNumber", this._indentNumber);
    paramObjectOutputStream.writeFields();
    if (this._uriResolver instanceof Serializable) {
      paramObjectOutputStream.writeBoolean(true);
      paramObjectOutputStream.writeObject((Serializable)this._uriResolver);
    } else {
      paramObjectOutputStream.writeBoolean(false);
    } 
  }
  
  public boolean overrideDefaultParser() { return this._overrideDefaultParser; }
  
  public void setURIResolver(URIResolver paramURIResolver) { this._uriResolver = paramURIResolver; }
  
  private void setTransletBytecodes(byte[][] paramArrayOfByte) { this._bytecodes = paramArrayOfByte; }
  
  private byte[][] getTransletBytecodes() { return this._bytecodes; }
  
  private Class[] getTransletClasses() {
    try {
      if (this._class == null)
        defineTransletClasses(); 
    } catch (TransformerConfigurationException transformerConfigurationException) {}
    return this._class;
  }
  
  public int getTransletIndex() {
    try {
      if (this._class == null)
        defineTransletClasses(); 
    } catch (TransformerConfigurationException transformerConfigurationException) {}
    return this._transletIndex;
  }
  
  protected void setTransletName(String paramString) { this._name = paramString; }
  
  protected String getTransletName() { return this._name; }
  
  private void defineTransletClasses() {
    if (this._bytecodes == null) {
      ErrorMsg errorMsg = new ErrorMsg("NO_TRANSLET_CLASS_ERR");
      throw new TransformerConfigurationException(errorMsg.toString());
    } 
    TransletClassLoader transletClassLoader = (TransletClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return new TemplatesImpl.TransletClassLoader(ObjectFactory.findClassLoader(), TemplatesImpl.this._tfactory.getExternalExtensionsMap()); }
        });
    try {
      int i = this._bytecodes.length;
      this._class = new Class[i];
      if (i > 1)
        this._auxClasses = new HashMap(); 
      for (byte b = 0; b < i; b++) {
        this._class[b] = transletClassLoader.defineClass(this._bytecodes[b]);
        Class clazz = this._class[b].getSuperclass();
        if (clazz.getName().equals(ABSTRACT_TRANSLET)) {
          this._transletIndex = b;
        } else {
          this._auxClasses.put(this._class[b].getName(), this._class[b]);
        } 
      } 
      if (this._transletIndex < 0) {
        ErrorMsg errorMsg = new ErrorMsg("NO_MAIN_TRANSLET_ERR", this._name);
        throw new TransformerConfigurationException(errorMsg.toString());
      } 
    } catch (ClassFormatError classFormatError) {
      ErrorMsg errorMsg = new ErrorMsg("TRANSLET_CLASS_ERR", this._name);
      throw new TransformerConfigurationException(errorMsg.toString());
    } catch (LinkageError linkageError) {
      ErrorMsg errorMsg = new ErrorMsg("TRANSLET_OBJECT_ERR", this._name);
      throw new TransformerConfigurationException(errorMsg.toString());
    } 
  }
  
  private Translet getTransletInstance() throws TransformerConfigurationException {
    try {
      if (this._name == null)
        return null; 
      if (this._class == null)
        defineTransletClasses(); 
      AbstractTranslet abstractTranslet = (AbstractTranslet)this._class[this._transletIndex].newInstance();
      abstractTranslet.postInitialization();
      abstractTranslet.setTemplates(this);
      abstractTranslet.setOverrideDefaultParser(this._overrideDefaultParser);
      abstractTranslet.setAllowedProtocols(this._accessExternalStylesheet);
      if (this._auxClasses != null)
        abstractTranslet.setAuxiliaryClasses(this._auxClasses); 
      return abstractTranslet;
    } catch (InstantiationException instantiationException) {
      ErrorMsg errorMsg = new ErrorMsg("TRANSLET_OBJECT_ERR", this._name);
      throw new TransformerConfigurationException(errorMsg.toString());
    } catch (IllegalAccessException illegalAccessException) {
      ErrorMsg errorMsg = new ErrorMsg("TRANSLET_OBJECT_ERR", this._name);
      throw new TransformerConfigurationException(errorMsg.toString());
    } 
  }
  
  public Transformer newTransformer() throws TransformerConfigurationException {
    TransformerImpl transformerImpl = new TransformerImpl(getTransletInstance(), this._outputProperties, this._indentNumber, this._tfactory);
    if (this._uriResolver != null)
      transformerImpl.setURIResolver(this._uriResolver); 
    if (this._tfactory.getFeature("http://javax.xml.XMLConstants/feature/secure-processing"))
      transformerImpl.setSecureProcessing(true); 
    return transformerImpl;
  }
  
  public Properties getOutputProperties() {
    try {
      return newTransformer().getOutputProperties();
    } catch (TransformerConfigurationException transformerConfigurationException) {
      return null;
    } 
  }
  
  public DOM getStylesheetDOM() { return (DOM)this._sdom.get(); }
  
  public void setStylesheetDOM(DOM paramDOM) { this._sdom.set(paramDOM); }
  
  static final class TransletClassLoader extends ClassLoader {
    private final Map<String, Class> _loadedExternalExtensionFunctions = null;
    
    TransletClassLoader(ClassLoader param1ClassLoader) { super(param1ClassLoader); }
    
    TransletClassLoader(ClassLoader param1ClassLoader, Map<String, Class> param1Map) { super(param1ClassLoader); }
    
    public Class<?> loadClass(String param1String) throws ClassNotFoundException {
      Class clazz = null;
      if (this._loadedExternalExtensionFunctions != null)
        clazz = (Class)this._loadedExternalExtensionFunctions.get(param1String); 
      if (clazz == null)
        clazz = super.loadClass(param1String); 
      return clazz;
    }
    
    Class defineClass(byte[] param1ArrayOfByte) { return defineClass(null, param1ArrayOfByte, 0, param1ArrayOfByte.length); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\TemplatesImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */