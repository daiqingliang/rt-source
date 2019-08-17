package com.sun.xml.internal.ws.commons.xmlutil;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public final class Converter {
  public static final String UTF_8 = "UTF-8";
  
  private static final Logger LOGGER = Logger.getLogger(Converter.class);
  
  private static final ContextClassloaderLocal<XMLOutputFactory> xmlOutputFactory = new ContextClassloaderLocal<XMLOutputFactory>() {
      protected XMLOutputFactory initialValue() throws Exception { return XMLOutputFactory.newInstance(); }
    };
  
  private static final AtomicBoolean logMissingStaxUtilsWarning = new AtomicBoolean(false);
  
  public static String toString(Throwable paramThrowable) {
    if (paramThrowable == null)
      return "[ No exception ]"; 
    StringWriter stringWriter = new StringWriter();
    paramThrowable.printStackTrace(new PrintWriter(stringWriter));
    return stringWriter.toString();
  }
  
  public static String toString(Packet paramPacket) { return (paramPacket == null) ? "[ Null packet ]" : ((paramPacket.getMessage() == null) ? "[ Empty packet ]" : toString(paramPacket.getMessage())); }
  
  public static String toStringNoIndent(Packet paramPacket) { return (paramPacket == null) ? "[ Null packet ]" : ((paramPacket.getMessage() == null) ? "[ Empty packet ]" : toStringNoIndent(paramPacket.getMessage())); }
  
  public static String toString(Message paramMessage) { return toString(paramMessage, true); }
  
  public static String toStringNoIndent(Message paramMessage) { return toString(paramMessage, false); }
  
  private static String toString(Message paramMessage, boolean paramBoolean) {
    if (paramMessage == null)
      return "[ Null message ]"; 
    stringWriter = null;
    try {
      stringWriter = new StringWriter();
      xMLStreamWriter = null;
      try {
        xMLStreamWriter = ((XMLOutputFactory)xmlOutputFactory.get()).createXMLStreamWriter(stringWriter);
        if (paramBoolean)
          xMLStreamWriter = createIndenter(xMLStreamWriter); 
        paramMessage.copy().writeTo(xMLStreamWriter);
      } catch (Exception exception) {
        LOGGER.log(Level.WARNING, "Unexpected exception occured while dumping message", exception);
      } finally {
        if (xMLStreamWriter != null)
          try {
            xMLStreamWriter.close();
          } catch (XMLStreamException xMLStreamException) {
            LOGGER.fine("Unexpected exception occured while closing XMLStreamWriter", xMLStreamException);
          }  
      } 
      return stringWriter.toString();
    } finally {
      if (stringWriter != null)
        try {
          stringWriter.close();
        } catch (IOException iOException) {
          LOGGER.finest("An exception occured when trying to close StringWriter", iOException);
        }  
    } 
  }
  
  public static byte[] toBytes(Message paramMessage, String paramString) throws XMLStreamException {
    byteArrayOutputStream = new ByteArrayOutputStream();
    try {
      if (paramMessage != null) {
        xMLStreamWriter = ((XMLOutputFactory)xmlOutputFactory.get()).createXMLStreamWriter(byteArrayOutputStream, paramString);
        try {
          paramMessage.writeTo(xMLStreamWriter);
        } finally {
          try {
            xMLStreamWriter.close();
          } catch (XMLStreamException xMLStreamException) {
            LOGGER.warning("Unexpected exception occured while closing XMLStreamWriter", xMLStreamException);
          } 
        } 
      } 
      return byteArrayOutputStream.toByteArray();
    } finally {
      try {
        byteArrayOutputStream.close();
      } catch (IOException iOException) {
        LOGGER.warning("Unexpected exception occured while closing ByteArrayOutputStream", iOException);
      } 
    } 
  }
  
  public static Message toMessage(@NotNull InputStream paramInputStream, String paramString) throws XMLStreamException {
    XMLStreamReader xMLStreamReader = XmlUtil.newXMLInputFactory(true).createXMLStreamReader(paramInputStream, paramString);
    return Messages.create(xMLStreamReader);
  }
  
  public static String messageDataToString(byte[] paramArrayOfByte, String paramString) {
    try {
      return toString(toMessage(new ByteArrayInputStream(paramArrayOfByte), paramString));
    } catch (XMLStreamException xMLStreamException) {
      LOGGER.warning("Unexpected exception occured while converting message data to string", xMLStreamException);
      return "[ Message Data Conversion Failed ]";
    } 
  }
  
  private static XMLStreamWriter createIndenter(XMLStreamWriter paramXMLStreamWriter) {
    try {
      Class clazz = Converter.class.getClassLoader().loadClass("javanet.staxutils.IndentingXMLStreamWriter");
      Constructor constructor = clazz.getConstructor(new Class[] { XMLStreamWriter.class });
      paramXMLStreamWriter = (XMLStreamWriter)XMLStreamWriter.class.cast(constructor.newInstance(new Object[] { paramXMLStreamWriter }));
    } catch (Exception exception) {
      if (logMissingStaxUtilsWarning.compareAndSet(false, true))
        LOGGER.log(Level.WARNING, "Put stax-utils.jar to the classpath to indent the dump output", exception); 
    } 
    return paramXMLStreamWriter;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\commons\xmlutil\Converter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */