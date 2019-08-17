package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MIMEMessage {
  private static final Logger LOGGER = Logger.getLogger(MIMEMessage.class.getName());
  
  MIMEConfig config;
  
  private final InputStream in;
  
  private final List<MIMEPart> partsList;
  
  private final Map<String, MIMEPart> partsMap;
  
  private final Iterator<MIMEEvent> it;
  
  private boolean parsed;
  
  private MIMEPart currentPart;
  
  private int currentIndex;
  
  public MIMEMessage(InputStream paramInputStream, String paramString) { this(paramInputStream, paramString, new MIMEConfig()); }
  
  public MIMEMessage(InputStream paramInputStream, String paramString, MIMEConfig paramMIMEConfig) {
    this.in = paramInputStream;
    this.config = paramMIMEConfig;
    MIMEParser mIMEParser = new MIMEParser(paramInputStream, paramString, paramMIMEConfig);
    this.it = mIMEParser.iterator();
    this.partsList = new ArrayList();
    this.partsMap = new HashMap();
    if (paramMIMEConfig.isParseEagerly())
      parseAll(); 
  }
  
  public List<MIMEPart> getAttachments() {
    if (!this.parsed)
      parseAll(); 
    return this.partsList;
  }
  
  public MIMEPart getPart(int paramInt) {
    LOGGER.log(Level.FINE, "index={0}", Integer.valueOf(paramInt));
    MIMEPart mIMEPart = (paramInt < this.partsList.size()) ? (MIMEPart)this.partsList.get(paramInt) : null;
    if (this.parsed && mIMEPart == null)
      throw new MIMEParsingException("There is no " + paramInt + " attachment part "); 
    if (mIMEPart == null) {
      mIMEPart = new MIMEPart(this);
      this.partsList.add(paramInt, mIMEPart);
    } 
    LOGGER.log(Level.FINE, "Got attachment at index={0} attachment={1}", new Object[] { Integer.valueOf(paramInt), mIMEPart });
    return mIMEPart;
  }
  
  public MIMEPart getPart(String paramString) {
    LOGGER.log(Level.FINE, "Content-ID={0}", paramString);
    MIMEPart mIMEPart = getDecodedCidPart(paramString);
    if (this.parsed && mIMEPart == null)
      throw new MIMEParsingException("There is no attachment part with Content-ID = " + paramString); 
    if (mIMEPart == null) {
      mIMEPart = new MIMEPart(this, paramString);
      this.partsMap.put(paramString, mIMEPart);
    } 
    LOGGER.log(Level.FINE, "Got attachment for Content-ID={0} attachment={1}", new Object[] { paramString, mIMEPart });
    return mIMEPart;
  }
  
  private MIMEPart getDecodedCidPart(String paramString) {
    MIMEPart mIMEPart = (MIMEPart)this.partsMap.get(paramString);
    if (mIMEPart == null && paramString.indexOf('%') != -1)
      try {
        String str = URLDecoder.decode(paramString, "utf-8");
        mIMEPart = (MIMEPart)this.partsMap.get(str);
      } catch (UnsupportedEncodingException unsupportedEncodingException) {} 
    return mIMEPart;
  }
  
  public final void parseAll() {
    while (makeProgress());
  }
  
  public boolean makeProgress() {
    ByteBuffer byteBuffer;
    MIMEEvent.Content content;
    MIMEPart mIMEPart2;
    MIMEPart mIMEPart1;
    String str;
    List list;
    InternetHeaders internetHeaders;
    MIMEEvent.Headers headers;
    if (!this.it.hasNext())
      return false; 
    MIMEEvent mIMEEvent = (MIMEEvent)this.it.next();
    switch (mIMEEvent.getEventType()) {
      case START_MESSAGE:
        LOGGER.log(Level.FINE, "MIMEEvent={0}", MIMEEvent.EVENT_TYPE.START_MESSAGE);
        return true;
      case START_PART:
        LOGGER.log(Level.FINE, "MIMEEvent={0}", MIMEEvent.EVENT_TYPE.START_PART);
        return true;
      case HEADERS:
        LOGGER.log(Level.FINE, "MIMEEvent={0}", MIMEEvent.EVENT_TYPE.HEADERS);
        headers = (MIMEEvent.Headers)mIMEEvent;
        internetHeaders = headers.getHeaders();
        list = internetHeaders.getHeader("content-id");
        str = (list != null) ? (String)list.get(0) : (this.currentIndex + "");
        if (str.length() > 2 && str.charAt(0) == '<')
          str = str.substring(1, str.length() - 1); 
        mIMEPart1 = (this.currentIndex < this.partsList.size()) ? (MIMEPart)this.partsList.get(this.currentIndex) : null;
        mIMEPart2 = getDecodedCidPart(str);
        if (mIMEPart1 == null && mIMEPart2 == null) {
          this.currentPart = getPart(str);
          this.partsList.add(this.currentIndex, this.currentPart);
        } else if (mIMEPart1 == null) {
          this.currentPart = mIMEPart2;
          this.partsList.add(this.currentIndex, mIMEPart2);
        } else if (mIMEPart2 == null) {
          this.currentPart = mIMEPart1;
          this.currentPart.setContentId(str);
          this.partsMap.put(str, this.currentPart);
        } else if (mIMEPart1 != mIMEPart2) {
          throw new MIMEParsingException("Created two different attachments using Content-ID and index");
        } 
        this.currentPart.setHeaders(internetHeaders);
        return true;
      case CONTENT:
        LOGGER.log(Level.FINER, "MIMEEvent={0}", MIMEEvent.EVENT_TYPE.CONTENT);
        content = (MIMEEvent.Content)mIMEEvent;
        byteBuffer = content.getData();
        this.currentPart.addBody(byteBuffer);
        return true;
      case END_PART:
        LOGGER.log(Level.FINE, "MIMEEvent={0}", MIMEEvent.EVENT_TYPE.END_PART);
        this.currentPart.doneParsing();
        this.currentIndex++;
        return true;
      case END_MESSAGE:
        LOGGER.log(Level.FINE, "MIMEEvent={0}", MIMEEvent.EVENT_TYPE.END_MESSAGE);
        this.parsed = true;
        try {
          this.in.close();
        } catch (IOException iOException) {
          throw new MIMEParsingException(iOException);
        } 
        return true;
    } 
    throw new MIMEParsingException("Unknown Parser state = " + mIMEEvent.getEventType());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\MIMEMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */