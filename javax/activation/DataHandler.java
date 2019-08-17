package javax.activation;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URL;

public class DataHandler implements Transferable {
  private DataSource dataSource = null;
  
  private DataSource objDataSource = null;
  
  private Object object = null;
  
  private String objectMimeType = null;
  
  private CommandMap currentCommandMap = null;
  
  private static final DataFlavor[] emptyFlavors = new DataFlavor[0];
  
  private DataFlavor[] transferFlavors = emptyFlavors;
  
  private DataContentHandler dataContentHandler = null;
  
  private DataContentHandler factoryDCH = null;
  
  private static DataContentHandlerFactory factory = null;
  
  private DataContentHandlerFactory oldFactory = null;
  
  private String shortType = null;
  
  public DataHandler(DataSource paramDataSource) {
    this.dataSource = paramDataSource;
    this.oldFactory = factory;
  }
  
  public DataHandler(Object paramObject, String paramString) {
    this.object = paramObject;
    this.objectMimeType = paramString;
    this.oldFactory = factory;
  }
  
  public DataHandler(URL paramURL) {
    this.dataSource = new URLDataSource(paramURL);
    this.oldFactory = factory;
  }
  
  private CommandMap getCommandMap() { return (this.currentCommandMap != null) ? this.currentCommandMap : CommandMap.getDefaultCommandMap(); }
  
  public DataSource getDataSource() {
    if (this.dataSource == null) {
      if (this.objDataSource == null)
        this.objDataSource = new DataHandlerDataSource(this); 
      return this.objDataSource;
    } 
    return this.dataSource;
  }
  
  public String getName() { return (this.dataSource != null) ? this.dataSource.getName() : null; }
  
  public String getContentType() { return (this.dataSource != null) ? this.dataSource.getContentType() : this.objectMimeType; }
  
  public InputStream getInputStream() throws IOException {
    InputStream inputStream = null;
    if (this.dataSource != null) {
      inputStream = this.dataSource.getInputStream();
    } else {
      DataContentHandler dataContentHandler1 = getDataContentHandler();
      if (dataContentHandler1 == null)
        throw new UnsupportedDataTypeException("no DCH for MIME type " + getBaseType()); 
      if (dataContentHandler1 instanceof ObjectDataContentHandler && ((ObjectDataContentHandler)dataContentHandler1).getDCH() == null)
        throw new UnsupportedDataTypeException("no object DCH for MIME type " + getBaseType()); 
      DataContentHandler dataContentHandler2 = dataContentHandler1;
      PipedOutputStream pipedOutputStream = new PipedOutputStream();
      PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);
      (new Thread(new Runnable(this, dataContentHandler2, pipedOutputStream) {
            public void run() {
              try {
                fdch.writeTo(DataHandler.this.object, DataHandler.this.objectMimeType, pos);
              } catch (IOException iOException) {
                try {
                  pos.close();
                } catch (IOException iOException) {}
              } finally {
                try {
                  pos.close();
                } catch (IOException iOException) {}
              } 
            }
          }"DataHandler.getInputStream")).start();
      inputStream = pipedInputStream;
    } 
    return inputStream;
  }
  
  public void writeTo(OutputStream paramOutputStream) throws IOException {
    if (this.dataSource != null) {
      inputStream = null;
      byte[] arrayOfByte = new byte[8192];
      inputStream = this.dataSource.getInputStream();
      try {
        int i;
        while ((i = inputStream.read(arrayOfByte)) > 0)
          paramOutputStream.write(arrayOfByte, 0, i); 
      } finally {
        inputStream.close();
        inputStream = null;
      } 
    } else {
      DataContentHandler dataContentHandler1 = getDataContentHandler();
      dataContentHandler1.writeTo(this.object, this.objectMimeType, paramOutputStream);
    } 
  }
  
  public OutputStream getOutputStream() throws IOException { return (this.dataSource != null) ? this.dataSource.getOutputStream() : null; }
  
  public DataFlavor[] getTransferDataFlavors() {
    if (factory != this.oldFactory)
      this.transferFlavors = emptyFlavors; 
    if (this.transferFlavors == emptyFlavors)
      this.transferFlavors = getDataContentHandler().getTransferDataFlavors(); 
    return (this.transferFlavors == emptyFlavors) ? this.transferFlavors : (DataFlavor[])this.transferFlavors.clone();
  }
  
  public boolean isDataFlavorSupported(DataFlavor paramDataFlavor) {
    DataFlavor[] arrayOfDataFlavor = getTransferDataFlavors();
    for (byte b = 0; b < arrayOfDataFlavor.length; b++) {
      if (arrayOfDataFlavor[b].equals(paramDataFlavor))
        return true; 
    } 
    return false;
  }
  
  public Object getTransferData(DataFlavor paramDataFlavor) throws UnsupportedFlavorException, IOException { return getDataContentHandler().getTransferData(paramDataFlavor, this.dataSource); }
  
  public void setCommandMap(CommandMap paramCommandMap) {
    if (paramCommandMap != this.currentCommandMap || paramCommandMap == null) {
      this.transferFlavors = emptyFlavors;
      this.dataContentHandler = null;
      this.currentCommandMap = paramCommandMap;
    } 
  }
  
  public CommandInfo[] getPreferredCommands() { return (this.dataSource != null) ? getCommandMap().getPreferredCommands(getBaseType(), this.dataSource) : getCommandMap().getPreferredCommands(getBaseType()); }
  
  public CommandInfo[] getAllCommands() { return (this.dataSource != null) ? getCommandMap().getAllCommands(getBaseType(), this.dataSource) : getCommandMap().getAllCommands(getBaseType()); }
  
  public CommandInfo getCommand(String paramString) { return (this.dataSource != null) ? getCommandMap().getCommand(getBaseType(), paramString, this.dataSource) : getCommandMap().getCommand(getBaseType(), paramString); }
  
  public Object getContent() throws IOException { return (this.object != null) ? this.object : getDataContentHandler().getContent(getDataSource()); }
  
  public Object getBean(CommandInfo paramCommandInfo) {
    Object object1 = null;
    try {
      ClassLoader classLoader = null;
      classLoader = SecuritySupport.getContextClassLoader();
      if (classLoader == null)
        classLoader = getClass().getClassLoader(); 
      object1 = paramCommandInfo.getCommandObject(this, classLoader);
    } catch (IOException iOException) {
    
    } catch (ClassNotFoundException classNotFoundException) {}
    return object1;
  }
  
  private DataContentHandler getDataContentHandler() {
    if (factory != this.oldFactory) {
      this.oldFactory = factory;
      this.factoryDCH = null;
      this.dataContentHandler = null;
      this.transferFlavors = emptyFlavors;
    } 
    if (this.dataContentHandler != null)
      return this.dataContentHandler; 
    String str = getBaseType();
    if (this.factoryDCH == null && factory != null)
      this.factoryDCH = factory.createDataContentHandler(str); 
    if (this.factoryDCH != null)
      this.dataContentHandler = this.factoryDCH; 
    if (this.dataContentHandler == null)
      if (this.dataSource != null) {
        this.dataContentHandler = getCommandMap().createDataContentHandler(str, this.dataSource);
      } else {
        this.dataContentHandler = getCommandMap().createDataContentHandler(str);
      }  
    if (this.dataSource != null) {
      this.dataContentHandler = new DataSourceDataContentHandler(this.dataContentHandler, this.dataSource);
    } else {
      this.dataContentHandler = new ObjectDataContentHandler(this.dataContentHandler, this.object, this.objectMimeType);
    } 
    return this.dataContentHandler;
  }
  
  private String getBaseType() {
    if (this.shortType == null) {
      String str = getContentType();
      try {
        MimeType mimeType = new MimeType(str);
        this.shortType = mimeType.getBaseType();
      } catch (MimeTypeParseException mimeTypeParseException) {
        this.shortType = str;
      } 
    } 
    return this.shortType;
  }
  
  public static void setDataContentHandlerFactory(DataContentHandlerFactory paramDataContentHandlerFactory) {
    if (factory != null)
      throw new Error("DataContentHandlerFactory already defined"); 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      try {
        securityManager.checkSetFactory();
      } catch (SecurityException securityException) {
        if (DataHandler.class.getClassLoader() != paramDataContentHandlerFactory.getClass().getClassLoader())
          throw securityException; 
      }  
    factory = paramDataContentHandlerFactory;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\activation\DataHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */