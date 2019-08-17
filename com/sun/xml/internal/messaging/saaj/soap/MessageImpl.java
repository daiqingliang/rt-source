package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.packaging.mime.Header;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.BMMimeMultipart;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeBodyPart;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeMultipart;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimePullMultipart;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ParameterList;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.SharedInputStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.ASCIIUtility;
import com.sun.xml.internal.messaging.saaj.soap.impl.EnvelopeImpl;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.FastInfosetReflection;
import com.sun.xml.internal.messaging.saaj.util.FinalArrayList;
import com.sun.xml.internal.messaging.saaj.util.SAAJUtil;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEPart;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.stream.StreamSource;

public abstract class MessageImpl extends SOAPMessage implements SOAPConstants {
  public static final String CONTENT_ID = "Content-ID";
  
  public static final String CONTENT_LOCATION = "Content-Location";
  
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
  
  protected static final int PLAIN_XML_FLAG = 1;
  
  protected static final int MIME_MULTIPART_FLAG = 2;
  
  protected static final int SOAP1_1_FLAG = 4;
  
  protected static final int SOAP1_2_FLAG = 8;
  
  protected static final int MIME_MULTIPART_XOP_SOAP1_1_FLAG = 6;
  
  protected static final int MIME_MULTIPART_XOP_SOAP1_2_FLAG = 10;
  
  protected static final int XOP_FLAG = 13;
  
  protected static final int FI_ENCODED_FLAG = 16;
  
  protected MimeHeaders headers;
  
  protected ContentType contentType;
  
  protected SOAPPartImpl soapPartImpl;
  
  protected FinalArrayList attachments;
  
  protected boolean saved = false;
  
  protected byte[] messageBytes;
  
  protected int messageByteCount;
  
  protected HashMap properties = new HashMap();
  
  protected MimeMultipart multiPart = null;
  
  protected boolean attachmentsInitialized = false;
  
  protected boolean isFastInfoset = false;
  
  protected boolean acceptFastInfoset = false;
  
  protected MimeMultipart mmp = null;
  
  private boolean optimizeAttachmentProcessing = true;
  
  private InputStream inputStreamAfterSaveChanges = null;
  
  private static boolean switchOffBM = false;
  
  private static boolean switchOffLazyAttachment = false;
  
  private static boolean useMimePull = false;
  
  private boolean lazyAttachments = false;
  
  private static final Iterator nullIter;
  
  private static boolean isSoap1_1Type(String paramString1, String paramString2) { return ((paramString1.equalsIgnoreCase("text") && paramString2.equalsIgnoreCase("xml")) || (paramString1.equalsIgnoreCase("text") && paramString2.equalsIgnoreCase("xml-soap")) || (paramString1.equals("application") && paramString2.equals("fastinfoset"))); }
  
  private static boolean isEqualToSoap1_1Type(String paramString) { return (paramString.startsWith("text/xml") || paramString.startsWith("application/fastinfoset")); }
  
  private static boolean isSoap1_2Type(String paramString1, String paramString2) { return (paramString1.equals("application") && (paramString2.equals("soap+xml") || paramString2.equals("soap+fastinfoset"))); }
  
  private static boolean isEqualToSoap1_2Type(String paramString) { return (paramString.startsWith("application/soap+xml") || paramString.startsWith("application/soap+fastinfoset")); }
  
  protected MessageImpl() {
    this(false, false);
    this.attachmentsInitialized = true;
  }
  
  protected MessageImpl(boolean paramBoolean1, boolean paramBoolean2) {
    this.isFastInfoset = paramBoolean1;
    this.acceptFastInfoset = paramBoolean2;
    this.headers = new MimeHeaders();
    this.headers.setHeader("Accept", getExpectedAcceptHeader());
    this.contentType = new ContentType();
  }
  
  protected MessageImpl(SOAPMessage paramSOAPMessage) {
    if (!(paramSOAPMessage instanceof MessageImpl));
    MessageImpl messageImpl = (MessageImpl)paramSOAPMessage;
    this.headers = messageImpl.headers;
    this.soapPartImpl = messageImpl.soapPartImpl;
    this.attachments = messageImpl.attachments;
    this.saved = messageImpl.saved;
    this.messageBytes = messageImpl.messageBytes;
    this.messageByteCount = messageImpl.messageByteCount;
    this.properties = messageImpl.properties;
    this.contentType = messageImpl.contentType;
  }
  
  protected static boolean isSoap1_1Content(int paramInt) { return ((paramInt & 0x4) != 0); }
  
  protected static boolean isSoap1_2Content(int paramInt) { return ((paramInt & 0x8) != 0); }
  
  private static boolean isMimeMultipartXOPSoap1_2Package(ContentType paramContentType) {
    String str1 = paramContentType.getParameter("type");
    if (str1 == null)
      return false; 
    str1 = str1.toLowerCase();
    if (!str1.startsWith("application/xop+xml"))
      return false; 
    String str2 = paramContentType.getParameter("start-info");
    if (str2 == null)
      return false; 
    str2 = str2.toLowerCase();
    return isEqualToSoap1_2Type(str2);
  }
  
  private static boolean isMimeMultipartXOPSoap1_1Package(ContentType paramContentType) {
    String str1 = paramContentType.getParameter("type");
    if (str1 == null)
      return false; 
    str1 = str1.toLowerCase();
    if (!str1.startsWith("application/xop+xml"))
      return false; 
    String str2 = paramContentType.getParameter("start-info");
    if (str2 == null)
      return false; 
    str2 = str2.toLowerCase();
    return isEqualToSoap1_1Type(str2);
  }
  
  private static boolean isSOAPBodyXOPPackage(ContentType paramContentType) {
    String str1 = paramContentType.getPrimaryType();
    String str2 = paramContentType.getSubType();
    if (str1.equalsIgnoreCase("application") && str2.equalsIgnoreCase("xop+xml")) {
      String str = getTypeParameter(paramContentType);
      return (isEqualToSoap1_2Type(str) || isEqualToSoap1_1Type(str));
    } 
    return false;
  }
  
  protected MessageImpl(MimeHeaders paramMimeHeaders, InputStream paramInputStream) throws SOAPExceptionImpl {
    this.contentType = parseContentType(paramMimeHeaders);
    init(paramMimeHeaders, identifyContentType(this.contentType), this.contentType, paramInputStream);
  }
  
  private static ContentType parseContentType(MimeHeaders paramMimeHeaders) throws SOAPExceptionImpl {
    String str;
    if (paramMimeHeaders != null) {
      str = getContentType(paramMimeHeaders);
    } else {
      log.severe("SAAJ0550.soap.null.headers");
      throw new SOAPExceptionImpl("Cannot create message: Headers can't be null");
    } 
    if (str == null) {
      log.severe("SAAJ0532.soap.no.Content-Type");
      throw new SOAPExceptionImpl("Absent Content-Type");
    } 
    try {
      return new ContentType(str);
    } catch (Throwable throwable) {
      log.severe("SAAJ0535.soap.cannot.internalize.message");
      throw new SOAPExceptionImpl("Unable to internalize message", throwable);
    } 
  }
  
  protected MessageImpl(MimeHeaders paramMimeHeaders, ContentType paramContentType, int paramInt, InputStream paramInputStream) throws SOAPExceptionImpl { init(paramMimeHeaders, paramInt, paramContentType, paramInputStream); }
  
  private void init(MimeHeaders paramMimeHeaders, int paramInt, final ContentType contentType, final InputStream in) throws SOAPExceptionImpl {
    this.headers = paramMimeHeaders;
    try {
      if ((paramInt & 0x10) > 0)
        this.isFastInfoset = this.acceptFastInfoset = true; 
      if (!this.isFastInfoset) {
        String[] arrayOfString = paramMimeHeaders.getHeader("Accept");
        if (arrayOfString != null)
          for (byte b = 0; b < arrayOfString.length; b++) {
            StringTokenizer stringTokenizer = new StringTokenizer(arrayOfString[b], ",");
            while (stringTokenizer.hasMoreTokens()) {
              String str = stringTokenizer.nextToken().trim();
              if (str.equalsIgnoreCase("application/fastinfoset") || str.equalsIgnoreCase("application/soap+fastinfoset")) {
                this.acceptFastInfoset = true;
                break;
              } 
            } 
          }  
      } 
      if (!isCorrectSoapVersion(paramInt)) {
        log.log(Level.SEVERE, "SAAJ0533.soap.incorrect.Content-Type", new String[] { paramContentType.toString(), getExpectedContentType() });
        throw new SOAPVersionMismatchException("Cannot create message: incorrect content-type for SOAP version. Got: " + paramContentType + " Expected: " + getExpectedContentType());
      } 
      if ((paramInt & true) != 0) {
        if (this.isFastInfoset) {
          getSOAPPart().setContent(FastInfosetReflection.FastInfosetSource_new(paramInputStream));
        } else {
          initCharsetProperty(paramContentType);
          getSOAPPart().setContent(new StreamSource(paramInputStream));
        } 
      } else if ((paramInt & 0x2) != 0) {
        DataSource dataSource = new DataSource() {
            public InputStream getInputStream() throws IOException { return in; }
            
            public OutputStream getOutputStream() { return null; }
            
            public String getContentType() { return contentType.toString(); }
            
            public String getName() { return ""; }
          };
        this.multiPart = null;
        if (useMimePull) {
          this.multiPart = new MimePullMultipart(dataSource, paramContentType);
        } else if (switchOffBM) {
          this.multiPart = new MimeMultipart(dataSource, paramContentType);
        } else {
          this.multiPart = new BMMimeMultipart(dataSource, paramContentType);
        } 
        String str1 = paramContentType.getParameter("start");
        MimeBodyPart mimeBodyPart = null;
        InputStream inputStream = null;
        String str2 = null;
        String str3 = null;
        if (switchOffBM || switchOffLazyAttachment) {
          if (str1 == null) {
            mimeBodyPart = this.multiPart.getBodyPart(0);
            for (byte b = 1; b < this.multiPart.getCount(); b++)
              initializeAttachment(this.multiPart, b); 
          } else {
            mimeBodyPart = this.multiPart.getBodyPart(str1);
            for (byte b = 0; b < this.multiPart.getCount(); b++) {
              str2 = this.multiPart.getBodyPart(b).getContentID();
              str3 = (str2 != null) ? str2.replaceFirst("^<", "").replaceFirst(">$", "") : null;
              if (!str1.equals(str2) && !str1.equals(str3))
                initializeAttachment(this.multiPart, b); 
            } 
          } 
        } else if (useMimePull) {
          MimePullMultipart mimePullMultipart = (MimePullMultipart)this.multiPart;
          MIMEPart mIMEPart = mimePullMultipart.readAndReturnSOAPPart();
          mimeBodyPart = new MimeBodyPart(mIMEPart);
          inputStream = mIMEPart.readOnce();
        } else {
          BMMimeMultipart bMMimeMultipart = (BMMimeMultipart)this.multiPart;
          InputStream inputStream1 = bMMimeMultipart.initStream();
          SharedInputStream sharedInputStream = null;
          if (inputStream1 instanceof SharedInputStream)
            sharedInputStream = (SharedInputStream)inputStream1; 
          String str = "--" + paramContentType.getParameter("boundary");
          byte[] arrayOfByte = ASCIIUtility.getBytes(str);
          if (str1 == null) {
            mimeBodyPart = bMMimeMultipart.getNextPart(inputStream1, arrayOfByte, sharedInputStream);
            bMMimeMultipart.removeBodyPart(mimeBodyPart);
          } else {
            MimeBodyPart mimeBodyPart1 = null;
            try {
              while (!str1.equals(str2) && !str1.equals(str3)) {
                mimeBodyPart1 = bMMimeMultipart.getNextPart(inputStream1, arrayOfByte, sharedInputStream);
                str2 = mimeBodyPart1.getContentID();
                str3 = (str2 != null) ? str2.replaceFirst("^<", "").replaceFirst(">$", "") : null;
              } 
              mimeBodyPart = mimeBodyPart1;
              bMMimeMultipart.removeBodyPart(mimeBodyPart1);
            } catch (Exception exception) {
              throw new SOAPExceptionImpl(exception);
            } 
          } 
        } 
        if (inputStream == null && mimeBodyPart != null)
          inputStream = mimeBodyPart.getInputStream(); 
        ContentType contentType1 = new ContentType(mimeBodyPart.getContentType());
        initCharsetProperty(contentType1);
        String str4 = contentType1.getBaseType().toLowerCase();
        if (!isEqualToSoap1_1Type(str4) && !isEqualToSoap1_2Type(str4) && !isSOAPBodyXOPPackage(contentType1)) {
          log.log(Level.SEVERE, "SAAJ0549.soap.part.invalid.Content-Type", new Object[] { str4 });
          throw new SOAPExceptionImpl("Bad Content-Type for SOAP Part : " + str4);
        } 
        SOAPPart sOAPPart = getSOAPPart();
        setMimeHeaders(sOAPPart, mimeBodyPart);
        sOAPPart.setContent(this.isFastInfoset ? FastInfosetReflection.FastInfosetSource_new(inputStream) : new StreamSource(inputStream));
      } else {
        log.severe("SAAJ0534.soap.unknown.Content-Type");
        throw new SOAPExceptionImpl("Unrecognized Content-Type");
      } 
    } catch (Throwable throwable) {
      log.severe("SAAJ0535.soap.cannot.internalize.message");
      throw new SOAPExceptionImpl("Unable to internalize message", throwable);
    } 
    needsSave();
  }
  
  public boolean isFastInfoset() { return this.isFastInfoset; }
  
  public boolean acceptFastInfoset() { return this.acceptFastInfoset; }
  
  public void setIsFastInfoset(boolean paramBoolean) {
    if (paramBoolean != this.isFastInfoset) {
      this.isFastInfoset = paramBoolean;
      if (this.isFastInfoset)
        this.acceptFastInfoset = true; 
      this.saved = false;
    } 
  }
  
  public Object getProperty(String paramString) { return (String)this.properties.get(paramString); }
  
  public void setProperty(String paramString, Object paramObject) {
    verify(paramString, paramObject);
    this.properties.put(paramString, paramObject);
  }
  
  private void verify(String paramString, Object paramObject) {
    if (paramString.equalsIgnoreCase("javax.xml.soap.write-xml-declaration")) {
      if (!"true".equals(paramObject) && !"false".equals(paramObject))
        throw new RuntimeException(paramString + " must have value false or true"); 
      try {
        EnvelopeImpl envelopeImpl = (EnvelopeImpl)getSOAPPart().getEnvelope();
        if ("true".equalsIgnoreCase((String)paramObject)) {
          envelopeImpl.setOmitXmlDecl("no");
        } else if ("false".equalsIgnoreCase((String)paramObject)) {
          envelopeImpl.setOmitXmlDecl("yes");
        } 
      } catch (Exception exception) {
        log.log(Level.SEVERE, "SAAJ0591.soap.exception.in.set.property", new Object[] { exception.getMessage(), "javax.xml.soap.write-xml-declaration" });
        throw new RuntimeException(exception);
      } 
      return;
    } 
    if (paramString.equalsIgnoreCase("javax.xml.soap.character-set-encoding"))
      try {
        ((EnvelopeImpl)getSOAPPart().getEnvelope()).setCharsetEncoding((String)paramObject);
      } catch (Exception exception) {
        log.log(Level.SEVERE, "SAAJ0591.soap.exception.in.set.property", new Object[] { exception.getMessage(), "javax.xml.soap.character-set-encoding" });
        throw new RuntimeException(exception);
      }  
  }
  
  protected abstract boolean isCorrectSoapVersion(int paramInt);
  
  protected abstract String getExpectedContentType();
  
  protected abstract String getExpectedAcceptHeader();
  
  static int identifyContentType(ContentType paramContentType) throws SOAPExceptionImpl {
    String str1 = paramContentType.getPrimaryType().toLowerCase();
    String str2 = paramContentType.getSubType().toLowerCase();
    if (str1.equals("multipart")) {
      if (str2.equals("related")) {
        String str = getTypeParameter(paramContentType);
        if (isEqualToSoap1_1Type(str))
          return (str.equals("application/fastinfoset") ? 16 : 0) | 0x2 | 0x4; 
        if (isEqualToSoap1_2Type(str))
          return (str.equals("application/soap+fastinfoset") ? 16 : 0) | 0x2 | 0x8; 
        if (isMimeMultipartXOPSoap1_1Package(paramContentType))
          return 6; 
        if (isMimeMultipartXOPSoap1_2Package(paramContentType))
          return 10; 
        log.severe("SAAJ0536.soap.content-type.mustbe.multipart");
        throw new SOAPExceptionImpl("Content-Type needs to be Multipart/Related and with \"type=text/xml\" or \"type=application/soap+xml\"");
      } 
      log.severe("SAAJ0537.soap.invalid.content-type");
      throw new SOAPExceptionImpl("Invalid Content-Type: " + str1 + '/' + str2);
    } 
    if (isSoap1_1Type(str1, str2))
      return ((str1.equalsIgnoreCase("application") && str2.equalsIgnoreCase("fastinfoset")) ? 16 : 0) | true | 0x4; 
    if (isSoap1_2Type(str1, str2))
      return ((str1.equalsIgnoreCase("application") && str2.equalsIgnoreCase("soap+fastinfoset")) ? 16 : 0) | true | 0x8; 
    if (isSOAPBodyXOPPackage(paramContentType))
      return 13; 
    log.severe("SAAJ0537.soap.invalid.content-type");
    throw new SOAPExceptionImpl("Invalid Content-Type:" + str1 + '/' + str2 + ". Is this an error message instead of a SOAP response?");
  }
  
  private static String getTypeParameter(ContentType paramContentType) {
    String str = paramContentType.getParameter("type");
    return (str != null) ? str.toLowerCase() : "text/xml";
  }
  
  public MimeHeaders getMimeHeaders() { return this.headers; }
  
  static final String getContentType(MimeHeaders paramMimeHeaders) {
    String[] arrayOfString = paramMimeHeaders.getHeader("Content-Type");
    return (arrayOfString == null) ? null : arrayOfString[0];
  }
  
  public String getContentType() { return getContentType(this.headers); }
  
  public void setContentType(String paramString) {
    this.headers.setHeader("Content-Type", paramString);
    needsSave();
  }
  
  private ContentType contentType() {
    ContentType contentType1 = null;
    try {
      String str = getContentType();
      if (str == null)
        return this.contentType; 
      contentType1 = new ContentType(str);
    } catch (Exception exception) {}
    return contentType1;
  }
  
  public String getBaseType() { return contentType().getBaseType(); }
  
  public void setBaseType(String paramString) {
    ContentType contentType1 = contentType();
    contentType1.setParameter("type", paramString);
    this.headers.setHeader("Content-Type", contentType1.toString());
    needsSave();
  }
  
  public String getAction() { return contentType().getParameter("action"); }
  
  public void setAction(String paramString) {
    ContentType contentType1 = contentType();
    contentType1.setParameter("action", paramString);
    this.headers.setHeader("Content-Type", contentType1.toString());
    needsSave();
  }
  
  public String getCharset() { return contentType().getParameter("charset"); }
  
  public void setCharset(String paramString) {
    ContentType contentType1 = contentType();
    contentType1.setParameter("charset", paramString);
    this.headers.setHeader("Content-Type", contentType1.toString());
    needsSave();
  }
  
  private final void needsSave() { this.saved = false; }
  
  public boolean saveRequired() { return (this.saved != true); }
  
  public String getContentDescription() {
    String[] arrayOfString = this.headers.getHeader("Content-Description");
    return (arrayOfString != null && arrayOfString.length > 0) ? arrayOfString[0] : null;
  }
  
  public void setContentDescription(String paramString) {
    this.headers.setHeader("Content-Description", paramString);
    needsSave();
  }
  
  public abstract SOAPPart getSOAPPart();
  
  public void removeAllAttachments() {
    try {
      initializeAllAttachments();
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    } 
    if (this.attachments != null) {
      this.attachments.clear();
      needsSave();
    } 
  }
  
  public int countAttachments() {
    try {
      initializeAllAttachments();
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    } 
    return (this.attachments != null) ? this.attachments.size() : 0;
  }
  
  public void addAttachmentPart(AttachmentPart paramAttachmentPart) {
    try {
      initializeAllAttachments();
      this.optimizeAttachmentProcessing = true;
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    } 
    if (this.attachments == null)
      this.attachments = new FinalArrayList(); 
    this.attachments.add(paramAttachmentPart);
    needsSave();
  }
  
  public Iterator getAttachments() {
    try {
      initializeAllAttachments();
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    } 
    return (this.attachments == null) ? nullIter : this.attachments.iterator();
  }
  
  private void setFinalContentType(String paramString) {
    ContentType contentType1 = contentType();
    if (contentType1 == null)
      contentType1 = new ContentType(); 
    String[] arrayOfString = getExpectedContentType().split("/");
    contentType1.setPrimaryType(arrayOfString[0]);
    contentType1.setSubType(arrayOfString[1]);
    contentType1.setParameter("charset", paramString);
    this.headers.setHeader("Content-Type", contentType1.toString());
  }
  
  public Iterator getAttachments(MimeHeaders paramMimeHeaders) {
    try {
      initializeAllAttachments();
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    } 
    return (this.attachments == null) ? nullIter : new MimeMatchingIterator(paramMimeHeaders);
  }
  
  public void removeAttachments(MimeHeaders paramMimeHeaders) {
    try {
      initializeAllAttachments();
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    } 
    if (this.attachments == null)
      return; 
    MimeMatchingIterator mimeMatchingIterator = new MimeMatchingIterator(paramMimeHeaders);
    while (mimeMatchingIterator.hasNext()) {
      int i = this.attachments.indexOf(mimeMatchingIterator.next());
      this.attachments.set(i, null);
    } 
    FinalArrayList finalArrayList = new FinalArrayList();
    for (byte b = 0; b < this.attachments.size(); b++) {
      if (this.attachments.get(b) != null)
        finalArrayList.add(this.attachments.get(b)); 
    } 
    this.attachments = finalArrayList;
  }
  
  public AttachmentPart createAttachmentPart() { return new AttachmentPartImpl(); }
  
  public AttachmentPart getAttachment(SOAPElement paramSOAPElement) throws SOAPException {
    String str1;
    try {
      initializeAllAttachments();
    } catch (Exception null) {
      throw new RuntimeException(str1);
    } 
    String str2 = paramSOAPElement.getAttribute("href");
    if ("".equals(str2)) {
      Node node = getValueNodeStrict(paramSOAPElement);
      String str = null;
      if (node != null)
        str = node.getValue(); 
      if (str == null || "".equals(str))
        return null; 
      str1 = str;
    } else {
      str1 = str2;
    } 
    return getAttachmentPart(str1);
  }
  
  private Node getValueNodeStrict(SOAPElement paramSOAPElement) {
    Node node = (Node)paramSOAPElement.getFirstChild();
    return (node != null) ? ((node.getNextSibling() == null && node.getNodeType() == 3) ? node : null) : null;
  }
  
  private AttachmentPart getAttachmentPart(String paramString) throws SOAPException {
    AttachmentPart attachmentPart;
    try {
      if (paramString.startsWith("cid:")) {
        paramString = '<' + paramString.substring("cid:".length()) + '>';
        MimeHeaders mimeHeaders = new MimeHeaders();
        mimeHeaders.addHeader("Content-ID", paramString);
        Iterator iterator = getAttachments(mimeHeaders);
        attachmentPart = (iterator == null) ? null : (AttachmentPart)iterator.next();
      } else {
        MimeHeaders mimeHeaders = new MimeHeaders();
        mimeHeaders.addHeader("Content-Location", paramString);
        Iterator iterator = getAttachments(mimeHeaders);
        attachmentPart = (iterator == null) ? null : (AttachmentPart)iterator.next();
      } 
      if (attachmentPart == null) {
        Iterator iterator = getAttachments();
        while (iterator.hasNext()) {
          AttachmentPart attachmentPart1 = (AttachmentPart)iterator.next();
          String str = attachmentPart1.getContentId();
          if (str != null) {
            int i = str.indexOf("=");
            if (i > -1) {
              str = str.substring(1, i);
              if (str.equalsIgnoreCase(paramString)) {
                attachmentPart = attachmentPart1;
                break;
              } 
            } 
          } 
        } 
      } 
    } catch (Exception exception) {
      log.log(Level.SEVERE, "SAAJ0590.soap.unable.to.locate.attachment", new Object[] { paramString });
      throw new SOAPExceptionImpl(exception);
    } 
    return attachmentPart;
  }
  
  private final InputStream getHeaderBytes() throws IOException {
    SOAPPartImpl sOAPPartImpl = (SOAPPartImpl)getSOAPPart();
    return sOAPPartImpl.getContentAsStream();
  }
  
  private String convertToSingleLine(String paramString) {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (c != '\r' && c != '\n' && c != '\t')
        stringBuffer.append(c); 
    } 
    return stringBuffer.toString();
  }
  
  private MimeMultipart getMimeMessage() throws SOAPException {
    try {
      SOAPPartImpl sOAPPartImpl = (SOAPPartImpl)getSOAPPart();
      MimeBodyPart mimeBodyPart = sOAPPartImpl.getMimePart();
      ContentType contentType1 = new ContentType(getExpectedContentType());
      if (!this.isFastInfoset)
        contentType1.setParameter("charset", initCharset()); 
      mimeBodyPart.setHeader("Content-Type", contentType1.toString());
      MimeMultipart mimeMultipart = null;
      if (!switchOffBM && !switchOffLazyAttachment && this.multiPart != null && !this.attachmentsInitialized) {
        mimeMultipart = new BMMimeMultipart();
        mimeMultipart.addBodyPart(mimeBodyPart);
        if (this.attachments != null) {
          Iterator iterator = this.attachments.iterator();
          while (iterator.hasNext())
            mimeMultipart.addBodyPart(((AttachmentPartImpl)iterator.next()).getMimePart()); 
        } 
        InputStream inputStream = ((BMMimeMultipart)this.multiPart).getInputStream();
        if (!((BMMimeMultipart)this.multiPart).lastBodyPartFound() && !((BMMimeMultipart)this.multiPart).isEndOfStream()) {
          ((BMMimeMultipart)mimeMultipart).setInputStream(inputStream);
          ((BMMimeMultipart)mimeMultipart).setBoundary(((BMMimeMultipart)this.multiPart).getBoundary());
          ((BMMimeMultipart)mimeMultipart).setLazyAttachments(this.lazyAttachments);
        } 
      } else {
        mimeMultipart = new MimeMultipart();
        mimeMultipart.addBodyPart(mimeBodyPart);
        Iterator iterator = getAttachments();
        while (iterator.hasNext())
          mimeMultipart.addBodyPart(((AttachmentPartImpl)iterator.next()).getMimePart()); 
      } 
      ContentType contentType2 = mimeMultipart.getContentType();
      ParameterList parameterList = contentType2.getParameterList();
      parameterList.set("type", getExpectedContentType());
      parameterList.set("boundary", contentType2.getParameter("boundary"));
      ContentType contentType3 = new ContentType("multipart", "related", parameterList);
      this.headers.setHeader("Content-Type", convertToSingleLine(contentType3.toString()));
      return mimeMultipart;
    } catch (SOAPException sOAPException) {
      throw sOAPException;
    } catch (Throwable throwable) {
      log.severe("SAAJ0538.soap.cannot.convert.msg.to.multipart.obj");
      throw new SOAPExceptionImpl("Unable to convert SOAP message into a MimeMultipart object", throwable);
    } 
  }
  
  private String initCharset() {
    String str = null;
    String[] arrayOfString = getMimeHeaders().getHeader("Content-Type");
    if (arrayOfString != null && arrayOfString[false] != null)
      str = getCharsetString(arrayOfString[0]); 
    if (str == null)
      str = (String)getProperty("javax.xml.soap.character-set-encoding"); 
    return (str != null) ? str : "utf-8";
  }
  
  private String getCharsetString(String paramString) {
    try {
      int i = paramString.indexOf(";");
      if (i < 0)
        return null; 
      ParameterList parameterList = new ParameterList(paramString.substring(i));
      return parameterList.get("charset");
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public void saveChanges() {
    String str = initCharset();
    boolean bool = (this.attachments == null) ? 0 : this.attachments.size();
    if (!bool && !switchOffBM && !switchOffLazyAttachment && !this.attachmentsInitialized && this.multiPart != null)
      bool = true; 
    try {
      if (!bool && !hasXOPContent()) {
        InputStream inputStream;
        try {
          inputStream = getHeaderBytes();
          this.optimizeAttachmentProcessing = false;
          if (SOAPPartImpl.lazyContentLength)
            this.inputStreamAfterSaveChanges = inputStream; 
        } catch (IOException iOException) {
          log.severe("SAAJ0539.soap.cannot.get.header.stream");
          throw new SOAPExceptionImpl("Unable to get header stream in saveChanges: ", iOException);
        } 
        if (inputStream instanceof ByteInputStream) {
          ByteInputStream byteInputStream = (ByteInputStream)inputStream;
          this.messageBytes = byteInputStream.getBytes();
          this.messageByteCount = byteInputStream.getCount();
        } 
        setFinalContentType(str);
        if (this.messageByteCount > 0)
          this.headers.setHeader("Content-Length", Integer.toString(this.messageByteCount)); 
      } else if (hasXOPContent()) {
        this.mmp = getXOPMessage();
      } else {
        this.mmp = getMimeMessage();
      } 
    } catch (Throwable throwable) {
      log.severe("SAAJ0540.soap.err.saving.multipart.msg");
      throw new SOAPExceptionImpl("Error during saving a multipart message", throwable);
    } 
    this.saved = true;
  }
  
  private MimeMultipart getXOPMessage() throws SOAPException {
    try {
      MimeMultipart mimeMultipart = new MimeMultipart();
      SOAPPartImpl sOAPPartImpl = (SOAPPartImpl)getSOAPPart();
      MimeBodyPart mimeBodyPart = sOAPPartImpl.getMimePart();
      ContentType contentType1 = new ContentType("application/xop+xml");
      contentType1.setParameter("type", getExpectedContentType());
      String str = initCharset();
      contentType1.setParameter("charset", str);
      mimeBodyPart.setHeader("Content-Type", contentType1.toString());
      mimeMultipart.addBodyPart(mimeBodyPart);
      Iterator iterator = getAttachments();
      while (iterator.hasNext())
        mimeMultipart.addBodyPart(((AttachmentPartImpl)iterator.next()).getMimePart()); 
      ContentType contentType2 = mimeMultipart.getContentType();
      ParameterList parameterList = contentType2.getParameterList();
      parameterList.set("start-info", getExpectedContentType());
      parameterList.set("type", "application/xop+xml");
      if (isCorrectSoapVersion(8)) {
        String str1 = getAction();
        if (str1 != null)
          parameterList.set("action", str1); 
      } 
      parameterList.set("boundary", contentType2.getParameter("boundary"));
      ContentType contentType3 = new ContentType("Multipart", "Related", parameterList);
      this.headers.setHeader("Content-Type", convertToSingleLine(contentType3.toString()));
      return mimeMultipart;
    } catch (SOAPException sOAPException) {
      throw sOAPException;
    } catch (Throwable throwable) {
      log.severe("SAAJ0538.soap.cannot.convert.msg.to.multipart.obj");
      throw new SOAPExceptionImpl("Unable to convert SOAP message into a MimeMultipart object", throwable);
    } 
  }
  
  private boolean hasXOPContent() {
    String str = getContentType();
    if (str == null)
      return false; 
    ContentType contentType1 = new ContentType(str);
    return (isMimeMultipartXOPSoap1_1Package(contentType1) || isMimeMultipartXOPSoap1_2Package(contentType1) || isSOAPBodyXOPPackage(contentType1));
  }
  
  public void writeTo(OutputStream paramOutputStream) throws SOAPException, IOException {
    if (saveRequired()) {
      this.optimizeAttachmentProcessing = true;
      saveChanges();
    } 
    if (!this.optimizeAttachmentProcessing) {
      if (SOAPPartImpl.lazyContentLength && this.messageByteCount <= 0) {
        byte[] arrayOfByte = new byte[1024];
        int i = 0;
        while ((i = this.inputStreamAfterSaveChanges.read(arrayOfByte)) != -1) {
          paramOutputStream.write(arrayOfByte, 0, i);
          this.messageByteCount += i;
        } 
        if (this.messageByteCount > 0)
          this.headers.setHeader("Content-Length", Integer.toString(this.messageByteCount)); 
      } else {
        paramOutputStream.write(this.messageBytes, 0, this.messageByteCount);
      } 
    } else {
      try {
        if (hasXOPContent()) {
          this.mmp.writeTo(paramOutputStream);
        } else {
          this.mmp.writeTo(paramOutputStream);
          if (!switchOffBM && !switchOffLazyAttachment && this.multiPart != null && !this.attachmentsInitialized)
            ((BMMimeMultipart)this.multiPart).setInputStream(((BMMimeMultipart)this.mmp).getInputStream()); 
        } 
      } catch (Exception exception) {
        log.severe("SAAJ0540.soap.err.saving.multipart.msg");
        throw new SOAPExceptionImpl("Error during saving a multipart message", exception);
      } 
    } 
    if (isCorrectSoapVersion(4)) {
      String[] arrayOfString = this.headers.getHeader("SOAPAction");
      if (arrayOfString == null || arrayOfString.length == 0)
        this.headers.setHeader("SOAPAction", "\"\""); 
    } 
    this.messageBytes = null;
    needsSave();
  }
  
  public SOAPBody getSOAPBody() throws SOAPException { return getSOAPPart().getEnvelope().getBody(); }
  
  public SOAPHeader getSOAPHeader() throws SOAPException { return getSOAPPart().getEnvelope().getHeader(); }
  
  private void initializeAllAttachments() {
    if (switchOffBM || switchOffLazyAttachment)
      return; 
    if (this.attachmentsInitialized || this.multiPart == null)
      return; 
    if (this.attachments == null)
      this.attachments = new FinalArrayList(); 
    int i = this.multiPart.getCount();
    for (byte b = 0; b < i; b++)
      initializeAttachment(this.multiPart.getBodyPart(b)); 
    this.attachmentsInitialized = true;
    needsSave();
  }
  
  private void initializeAttachment(MimeBodyPart paramMimeBodyPart) throws SOAPException {
    AttachmentPartImpl attachmentPartImpl = new AttachmentPartImpl();
    DataHandler dataHandler = paramMimeBodyPart.getDataHandler();
    attachmentPartImpl.setDataHandler(dataHandler);
    AttachmentPartImpl.copyMimeHeaders(paramMimeBodyPart, attachmentPartImpl);
    this.attachments.add(attachmentPartImpl);
  }
  
  private void initializeAttachment(MimeMultipart paramMimeMultipart, int paramInt) throws Exception {
    MimeBodyPart mimeBodyPart = paramMimeMultipart.getBodyPart(paramInt);
    AttachmentPartImpl attachmentPartImpl = new AttachmentPartImpl();
    DataHandler dataHandler = mimeBodyPart.getDataHandler();
    attachmentPartImpl.setDataHandler(dataHandler);
    AttachmentPartImpl.copyMimeHeaders(mimeBodyPart, attachmentPartImpl);
    addAttachmentPart(attachmentPartImpl);
  }
  
  private void setMimeHeaders(SOAPPart paramSOAPPart, MimeBodyPart paramMimeBodyPart) throws Exception {
    paramSOAPPart.removeAllMimeHeaders();
    FinalArrayList finalArrayList = paramMimeBodyPart.getAllHeaders();
    int i = finalArrayList.size();
    for (byte b = 0; b < i; b++) {
      Header header = (Header)finalArrayList.get(b);
      paramSOAPPart.addMimeHeader(header.getName(), header.getValue());
    } 
  }
  
  private void initCharsetProperty(ContentType paramContentType) {
    String str = paramContentType.getParameter("charset");
    if (str != null) {
      ((SOAPPartImpl)getSOAPPart()).setSourceCharsetEncoding(str);
      if (!str.equalsIgnoreCase("utf-8"))
        setProperty("javax.xml.soap.character-set-encoding", str); 
    } 
  }
  
  public void setLazyAttachments(boolean paramBoolean) { this.lazyAttachments = paramBoolean; }
  
  static  {
    String str = SAAJUtil.getSystemProperty("saaj.mime.optimization");
    if (str != null && str.equals("false"))
      switchOffBM = true; 
    str = SAAJUtil.getSystemProperty("saaj.lazy.mime.optimization");
    if (str != null && str.equals("false"))
      switchOffLazyAttachment = true; 
    useMimePull = SAAJUtil.getSystemBoolean("saaj.use.mimepull");
    nullIter = Collections.EMPTY_LIST.iterator();
  }
  
  private class MimeMatchingIterator implements Iterator {
    private Iterator iter;
    
    private MimeHeaders headers;
    
    private Object nextAttachment;
    
    public MimeMatchingIterator(MimeHeaders param1MimeHeaders) {
      this.headers = param1MimeHeaders;
      this.iter = MessageImpl.this.attachments.iterator();
    }
    
    public boolean hasNext() {
      if (this.nextAttachment == null)
        this.nextAttachment = nextMatch(); 
      return (this.nextAttachment != null);
    }
    
    public Object next() {
      if (this.nextAttachment != null) {
        Object object = this.nextAttachment;
        this.nextAttachment = null;
        return object;
      } 
      return hasNext() ? this.nextAttachment : null;
    }
    
    Object nextMatch() {
      while (this.iter.hasNext()) {
        AttachmentPartImpl attachmentPartImpl = (AttachmentPartImpl)this.iter.next();
        if (attachmentPartImpl.hasAllHeaders(this.headers))
          return attachmentPartImpl; 
      } 
      return null;
    }
    
    public void remove() { this.iter.remove(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\MessageImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */