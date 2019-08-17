package com.sun.xml.internal.ws.util.pipe;

import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class DumpTube extends AbstractFilterTubeImpl {
  private final String name;
  
  private final PrintStream out;
  
  private final XMLOutputFactory staxOut;
  
  private static boolean warnStaxUtils;
  
  public DumpTube(String paramString, PrintStream paramPrintStream, Tube paramTube) {
    super(paramTube);
    this.name = paramString;
    this.out = paramPrintStream;
    this.staxOut = XMLOutputFactory.newInstance();
  }
  
  protected DumpTube(DumpTube paramDumpTube, TubeCloner paramTubeCloner) {
    super(paramDumpTube, paramTubeCloner);
    this.name = paramDumpTube.name;
    this.out = paramDumpTube.out;
    this.staxOut = paramDumpTube.staxOut;
  }
  
  public NextAction processRequest(Packet paramPacket) {
    dump("request", paramPacket);
    return super.processRequest(paramPacket);
  }
  
  public NextAction processResponse(Packet paramPacket) {
    dump("response", paramPacket);
    return super.processResponse(paramPacket);
  }
  
  protected void dump(String paramString, Packet paramPacket) {
    this.out.println("====[" + this.name + ":" + paramString + "]====");
    if (paramPacket.getMessage() == null) {
      this.out.println("(none)");
    } else {
      try {
        XMLStreamWriter xMLStreamWriter = this.staxOut.createXMLStreamWriter(new PrintStream(this.out) {
              public void close() {}
            });
        xMLStreamWriter = createIndenter(xMLStreamWriter);
        paramPacket.getMessage().copy().writeTo(xMLStreamWriter);
        xMLStreamWriter.close();
      } catch (XMLStreamException xMLStreamException) {
        xMLStreamException.printStackTrace(this.out);
      } 
    } 
    this.out.println("============");
  }
  
  private XMLStreamWriter createIndenter(XMLStreamWriter paramXMLStreamWriter) {
    try {
      Class clazz = getClass().getClassLoader().loadClass("javanet.staxutils.IndentingXMLStreamWriter");
      Constructor constructor = clazz.getConstructor(new Class[] { XMLStreamWriter.class });
      paramXMLStreamWriter = (XMLStreamWriter)constructor.newInstance(new Object[] { paramXMLStreamWriter });
    } catch (Exception exception) {
      if (!warnStaxUtils) {
        warnStaxUtils = true;
        this.out.println("WARNING: put stax-utils.jar to the classpath to indent the dump output");
      } 
    } 
    return paramXMLStreamWriter;
  }
  
  public AbstractTubeImpl copy(TubeCloner paramTubeCloner) { return new DumpTube(this, paramTubeCloner); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\pipe\DumpTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */