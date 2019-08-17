package com.sun.rowset.internal;

import com.sun.rowset.JdbcRowSetResourceBundle;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.MessageFormat;
import javax.sql.RowSetInternal;
import javax.sql.rowset.WebRowSet;
import javax.sql.rowset.spi.XmlReader;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class WebRowSetXmlReader implements XmlReader, Serializable {
  private JdbcRowSetResourceBundle resBundle;
  
  static final long serialVersionUID = -9127058392819008014L;
  
  public WebRowSetXmlReader() {
    try {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
  
  public void readXML(WebRowSet paramWebRowSet, Reader paramReader) throws SQLException {
    try {
      InputSource inputSource = new InputSource(paramReader);
      XmlErrorHandler xmlErrorHandler = new XmlErrorHandler();
      XmlReaderContentHandler xmlReaderContentHandler = new XmlReaderContentHandler(paramWebRowSet);
      SAXParserFactory sAXParserFactory = SAXParserFactory.newInstance();
      sAXParserFactory.setNamespaceAware(true);
      sAXParserFactory.setValidating(true);
      SAXParser sAXParser = sAXParserFactory.newSAXParser();
      sAXParser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
      XMLReader xMLReader = sAXParser.getXMLReader();
      xMLReader.setEntityResolver(new XmlResolver());
      xMLReader.setContentHandler(xmlReaderContentHandler);
      xMLReader.setErrorHandler(xmlErrorHandler);
      xMLReader.parse(inputSource);
    } catch (SAXParseException sAXParseException) {
      System.out.println(MessageFormat.format(this.resBundle.handleGetObject("wrsxmlreader.parseerr").toString(), new Object[] { sAXParseException.getMessage(), Integer.valueOf(sAXParseException.getLineNumber()), sAXParseException.getSystemId() }));
      sAXParseException.printStackTrace();
      throw new SQLException(sAXParseException.getMessage());
    } catch (SAXException sAXException) {
      Exception exception = sAXException;
      if (sAXException.getException() != null)
        exception = sAXException.getException(); 
      exception.printStackTrace();
      throw new SQLException(exception.getMessage());
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new SQLException(this.resBundle.handleGetObject("wrsxmlreader.invalidcp").toString());
    } catch (Throwable throwable) {
      throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("wrsxmlreader.readxml").toString(), new Object[] { throwable.getMessage() }));
    } 
  }
  
  public void readXML(WebRowSet paramWebRowSet, InputStream paramInputStream) throws SQLException {
    try {
      InputSource inputSource = new InputSource(paramInputStream);
      XmlErrorHandler xmlErrorHandler = new XmlErrorHandler();
      XmlReaderContentHandler xmlReaderContentHandler = new XmlReaderContentHandler(paramWebRowSet);
      SAXParserFactory sAXParserFactory = SAXParserFactory.newInstance();
      sAXParserFactory.setNamespaceAware(true);
      sAXParserFactory.setValidating(true);
      SAXParser sAXParser = sAXParserFactory.newSAXParser();
      sAXParser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
      XMLReader xMLReader = sAXParser.getXMLReader();
      xMLReader.setEntityResolver(new XmlResolver());
      xMLReader.setContentHandler(xmlReaderContentHandler);
      xMLReader.setErrorHandler(xmlErrorHandler);
      xMLReader.parse(inputSource);
    } catch (SAXParseException sAXParseException) {
      System.out.println(MessageFormat.format(this.resBundle.handleGetObject("wrsxmlreader.parseerr").toString(), new Object[] { Integer.valueOf(sAXParseException.getLineNumber()), sAXParseException.getSystemId() }));
      System.out.println("   " + sAXParseException.getMessage());
      sAXParseException.printStackTrace();
      throw new SQLException(sAXParseException.getMessage());
    } catch (SAXException sAXException) {
      Exception exception = sAXException;
      if (sAXException.getException() != null)
        exception = sAXException.getException(); 
      exception.printStackTrace();
      throw new SQLException(exception.getMessage());
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new SQLException(this.resBundle.handleGetObject("wrsxmlreader.invalidcp").toString());
    } catch (Throwable throwable) {
      throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("wrsxmlreader.readxml").toString(), new Object[] { throwable.getMessage() }));
    } 
  }
  
  public void readData(RowSetInternal paramRowSetInternal) {}
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    try {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\rowset\internal\WebRowSetXmlReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */