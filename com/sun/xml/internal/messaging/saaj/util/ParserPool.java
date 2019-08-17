package com.sun.xml.internal.messaging.saaj.util;

import com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class ParserPool {
  private final BlockingQueue queue;
  
  private SAXParserFactory factory;
  
  private int capacity;
  
  public ParserPool(int paramInt) {
    this.capacity = paramInt;
    this.queue = new ArrayBlockingQueue(paramInt);
    this.factory = new SAXParserFactoryImpl();
    this.factory.setNamespaceAware(true);
    for (byte b = 0; b < paramInt; b++) {
      try {
        this.queue.put(this.factory.newSAXParser());
      } catch (InterruptedException interruptedException) {
        Thread.currentThread().interrupt();
        throw new RuntimeException(interruptedException);
      } catch (ParserConfigurationException parserConfigurationException) {
        throw new RuntimeException(parserConfigurationException);
      } catch (SAXException sAXException) {
        throw new RuntimeException(sAXException);
      } 
    } 
  }
  
  public SAXParser get() throws ParserConfigurationException, SAXException {
    try {
      return (SAXParser)this.queue.take();
    } catch (InterruptedException interruptedException) {
      throw new SAXException(interruptedException);
    } 
  }
  
  public void put(SAXParser paramSAXParser) { this.queue.offer(paramSAXParser); }
  
  public void returnParser(SAXParser paramSAXParser) {
    paramSAXParser.reset();
    resetSaxParser(paramSAXParser);
    put(paramSAXParser);
  }
  
  private void resetSaxParser(SAXParser paramSAXParser) {
    try {
      SymbolTable symbolTable = new SymbolTable();
      paramSAXParser.setProperty("http://apache.org/xml/properties/internal/symbol-table", symbolTable);
    } catch (SAXNotRecognizedException sAXNotRecognizedException) {
    
    } catch (SAXNotSupportedException sAXNotSupportedException) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saa\\util\ParserPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */