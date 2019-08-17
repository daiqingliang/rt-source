package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.xml.internal.stream.Entity;
import java.io.EOFException;
import java.io.IOException;

public class XMLVersionDetector {
  private static final char[] XML11_VERSION = { '1', '.', '1' };
  
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
  
  protected static final String fVersionSymbol = "version".intern();
  
  protected static final String fXMLSymbol = "[xml]".intern();
  
  protected SymbolTable fSymbolTable;
  
  protected XMLErrorReporter fErrorReporter;
  
  protected XMLEntityManager fEntityManager;
  
  protected String fEncoding = null;
  
  private XMLString fVersionNum = new XMLString();
  
  private final char[] fExpectedVersionString = { 
      '<', '?', 'x', 'm', 'l', ' ', 'v', 'e', 'r', 's', 
      'i', 'o', 'n', '=', ' ', ' ', ' ', ' ', ' ' };
  
  public void reset(XMLComponentManager paramXMLComponentManager) throws XMLConfigurationException {
    this.fSymbolTable = (SymbolTable)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
    this.fErrorReporter = (XMLErrorReporter)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    this.fEntityManager = (XMLEntityManager)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager");
    for (byte b = 14; b < this.fExpectedVersionString.length; b++)
      this.fExpectedVersionString[b] = ' '; 
  }
  
  public void startDocumentParsing(XMLEntityHandler paramXMLEntityHandler, short paramShort) {
    if (paramShort == 1) {
      this.fEntityManager.setScannerVersion((short)1);
    } else {
      this.fEntityManager.setScannerVersion((short)2);
    } 
    this.fErrorReporter.setDocumentLocator(this.fEntityManager.getEntityScanner());
    this.fEntityManager.setEntityHandler(paramXMLEntityHandler);
    paramXMLEntityHandler.startEntity(fXMLSymbol, this.fEntityManager.getCurrentResourceIdentifier(), this.fEncoding, null);
  }
  
  public short determineDocVersion(XMLInputSource paramXMLInputSource) throws IOException {
    this.fEncoding = this.fEntityManager.setupCurrentEntity(false, fXMLSymbol, paramXMLInputSource, false, true);
    this.fEntityManager.setScannerVersion((short)1);
    XMLEntityScanner xMLEntityScanner = this.fEntityManager.getEntityScanner();
    xMLEntityScanner.detectingVersion = true;
    try {
      if (!xMLEntityScanner.skipString("<?xml")) {
        xMLEntityScanner.detectingVersion = false;
        return 1;
      } 
      if (!xMLEntityScanner.skipDeclSpaces()) {
        fixupCurrentEntity(this.fEntityManager, this.fExpectedVersionString, 5);
        xMLEntityScanner.detectingVersion = false;
        return 1;
      } 
      if (!xMLEntityScanner.skipString("version")) {
        fixupCurrentEntity(this.fEntityManager, this.fExpectedVersionString, 6);
        xMLEntityScanner.detectingVersion = false;
        return 1;
      } 
      xMLEntityScanner.skipDeclSpaces();
      if (xMLEntityScanner.peekChar() != 61) {
        fixupCurrentEntity(this.fEntityManager, this.fExpectedVersionString, 13);
        xMLEntityScanner.detectingVersion = false;
        return 1;
      } 
      xMLEntityScanner.scanChar(null);
      xMLEntityScanner.skipDeclSpaces();
      int i = xMLEntityScanner.scanChar(null);
      this.fExpectedVersionString[14] = (char)i;
      byte b;
      for (b = 0; b < XML11_VERSION.length; b++)
        this.fExpectedVersionString[15 + b] = (char)xMLEntityScanner.scanChar(null); 
      this.fExpectedVersionString[18] = (char)xMLEntityScanner.scanChar(null);
      fixupCurrentEntity(this.fEntityManager, this.fExpectedVersionString, 19);
      for (b = 0; b < XML11_VERSION.length && this.fExpectedVersionString[15 + b] == XML11_VERSION[b]; b++);
      xMLEntityScanner.detectingVersion = false;
      return (b == XML11_VERSION.length) ? 2 : 1;
    } catch (EOFException eOFException) {
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "PrematureEOF", null, (short)2);
      xMLEntityScanner.detectingVersion = false;
      return 1;
    } 
  }
  
  private void fixupCurrentEntity(XMLEntityManager paramXMLEntityManager, char[] paramArrayOfChar, int paramInt) {
    Entity.ScannedEntity scannedEntity = paramXMLEntityManager.getCurrentEntity();
    if (scannedEntity.count - scannedEntity.position + paramInt > scannedEntity.ch.length) {
      char[] arrayOfChar = scannedEntity.ch;
      scannedEntity.ch = new char[paramInt + scannedEntity.count - scannedEntity.position + 1];
      System.arraycopy(arrayOfChar, 0, scannedEntity.ch, 0, arrayOfChar.length);
    } 
    if (scannedEntity.position < paramInt) {
      System.arraycopy(scannedEntity.ch, scannedEntity.position, scannedEntity.ch, paramInt, scannedEntity.count - scannedEntity.position);
      scannedEntity.count += paramInt - scannedEntity.position;
    } else {
      for (int i = paramInt; i < scannedEntity.position; i++)
        scannedEntity.ch[i] = ' '; 
    } 
    System.arraycopy(paramArrayOfChar, 0, scannedEntity.ch, 0, paramInt);
    scannedEntity.position = 0;
    scannedEntity.baseCharOffset = 0;
    scannedEntity.startPosition = 0;
    scannedEntity.columnNumber = scannedEntity.lineNumber = 1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\XMLVersionDetector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */