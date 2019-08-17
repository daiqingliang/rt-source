package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.istack.internal.ByteArrayDataSource;
import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.TODO;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeBuiltinLeafInfo;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.output.Pcdata;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.util.ByteArrayOutputStreamEx;
import com.sun.xml.internal.bind.v2.util.DataSourceSource;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.xml.bind.MarshalException;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.SAXException;

public abstract class RuntimeBuiltinLeafInfoImpl<T> extends BuiltinLeafInfoImpl<Type, Class> implements RuntimeBuiltinLeafInfo, Transducer<T> {
  public static final Map<Type, RuntimeBuiltinLeafInfoImpl<?>> LEAVES = new HashMap();
  
  public static final RuntimeBuiltinLeafInfoImpl<String> STRING;
  
  private static final String DATE = "date";
  
  public static final List<RuntimeBuiltinLeafInfoImpl<?>> builtinBeanInfos;
  
  public static final String MAP_ANYURI_TO_URI = "mapAnyUriToUri";
  
  private static final Map<QName, String> xmlGregorianCalendarFormatString;
  
  private static final Map<QName, Integer> xmlGregorianCalendarFieldRef;
  
  private RuntimeBuiltinLeafInfoImpl(Class paramClass, QName... paramVarArgs) {
    super(paramClass, paramVarArgs);
    LEAVES.put(paramClass, this);
  }
  
  public final Class getClazz() { return (Class)getType(); }
  
  public final Transducer getTransducer() { return this; }
  
  public boolean useNamespace() { return false; }
  
  public final boolean isDefault() { return true; }
  
  public void declareNamespace(T paramT, XMLSerializer paramXMLSerializer) throws AccessorException {}
  
  public QName getTypeName(T paramT) { return null; }
  
  private static QName createXS(String paramString) { return new QName("http://www.w3.org/2001/XMLSchema", paramString); }
  
  private static byte[] decodeBase64(CharSequence paramCharSequence) {
    if (paramCharSequence instanceof Base64Data) {
      Base64Data base64Data = (Base64Data)paramCharSequence;
      return base64Data.getExact();
    } 
    return DatatypeConverterImpl._parseBase64Binary(paramCharSequence.toString());
  }
  
  private static void checkXmlGregorianCalendarFieldRef(QName paramQName, XMLGregorianCalendar paramXMLGregorianCalendar) throws MarshalException {
    StringBuilder stringBuilder = new StringBuilder();
    int i = ((Integer)xmlGregorianCalendarFieldRef.get(paramQName)).intValue();
    boolean bool = true;
    byte b = 0;
    while (i != 0) {
      int j = i & true;
      i >>>= 4;
      b++;
      if (j == 1)
        switch (b) {
          case 1:
            if (paramXMLGregorianCalendar.getSecond() == Integer.MIN_VALUE)
              stringBuilder.append("  ").append(Messages.XMLGREGORIANCALENDAR_SEC); 
          case 2:
            if (paramXMLGregorianCalendar.getMinute() == Integer.MIN_VALUE)
              stringBuilder.append("  ").append(Messages.XMLGREGORIANCALENDAR_MIN); 
          case 3:
            if (paramXMLGregorianCalendar.getHour() == Integer.MIN_VALUE)
              stringBuilder.append("  ").append(Messages.XMLGREGORIANCALENDAR_HR); 
          case 4:
            if (paramXMLGregorianCalendar.getDay() == Integer.MIN_VALUE)
              stringBuilder.append("  ").append(Messages.XMLGREGORIANCALENDAR_DAY); 
          case 5:
            if (paramXMLGregorianCalendar.getMonth() == Integer.MIN_VALUE)
              stringBuilder.append("  ").append(Messages.XMLGREGORIANCALENDAR_MONTH); 
          case 6:
            if (paramXMLGregorianCalendar.getYear() == Integer.MIN_VALUE)
              stringBuilder.append("  ").append(Messages.XMLGREGORIANCALENDAR_YEAR); 
        }  
    } 
    if (stringBuilder.length() > 0)
      throw new MarshalException(Messages.XMLGREGORIANCALENDAR_INVALID.format(new Object[] { paramQName.getLocalPart() }) + stringBuilder.toString()); 
  }
  
  static  {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() { return System.getProperty("mapAnyUriToUri"); }
        });
    new QName[10][0] = createXS("string");
    new QName[10][1] = createXS("anySimpleType");
    new QName[10][2] = createXS("normalizedString");
    new QName[10][3] = createXS("anyURI");
    new QName[10][4] = createXS("token");
    new QName[10][5] = createXS("language");
    new QName[10][6] = createXS("Name");
    new QName[10][7] = createXS("NCName");
    new QName[10][8] = createXS("NMTOKEN");
    new QName[10][9] = createXS("ENTITY");
    new QName[9][0] = createXS("string");
    new QName[9][1] = createXS("anySimpleType");
    new QName[9][2] = createXS("normalizedString");
    new QName[9][3] = createXS("token");
    new QName[9][4] = createXS("language");
    new QName[9][5] = createXS("Name");
    new QName[9][6] = createXS("NCName");
    new QName[9][7] = createXS("NMTOKEN");
    new QName[9][8] = createXS("ENTITY");
    QName[] arrayOfQName = (str == null) ? new QName[10] : new QName[9];
    STRING = new StringImplImpl(String.class, arrayOfQName);
    ArrayList arrayList1 = new ArrayList();
    arrayList1.add(new StringImpl<Character>(Character.class, new QName[] { createXS("unsignedShort") }) {
          public Character parse(CharSequence param1CharSequence) { return Character.valueOf((char)DatatypeConverterImpl._parseInt(param1CharSequence)); }
          
          public String print(Character param1Character) { return Integer.toString(param1Character.charValue()); }
        });
    arrayList1.add(new StringImpl<Calendar>(Calendar.class, new QName[] { DatatypeConstants.DATETIME }) {
          public Calendar parse(CharSequence param1CharSequence) { return DatatypeConverterImpl._parseDateTime(param1CharSequence.toString()); }
          
          public String print(Calendar param1Calendar) { return DatatypeConverterImpl._printDateTime(param1Calendar); }
        });
    arrayList1.add(new StringImpl<GregorianCalendar>(GregorianCalendar.class, new QName[] { DatatypeConstants.DATETIME }) {
          public GregorianCalendar parse(CharSequence param1CharSequence) { return DatatypeConverterImpl._parseDateTime(param1CharSequence.toString()); }
          
          public String print(GregorianCalendar param1GregorianCalendar) { return DatatypeConverterImpl._printDateTime(param1GregorianCalendar); }
        });
    arrayList1.add(new StringImpl<Date>(Date.class, new QName[] { DatatypeConstants.DATETIME }) {
          public Date parse(CharSequence param1CharSequence) { return DatatypeConverterImpl._parseDateTime(param1CharSequence.toString()).getTime(); }
          
          public String print(Date param1Date) {
            XMLSerializer xMLSerializer = XMLSerializer.getInstance();
            QName qName = xMLSerializer.getSchemaType();
            GregorianCalendar gregorianCalendar = new GregorianCalendar(0, 0, 0);
            gregorianCalendar.setTime(param1Date);
            return (qName != null && "http://www.w3.org/2001/XMLSchema".equals(qName.getNamespaceURI()) && "date".equals(qName.getLocalPart())) ? DatatypeConverterImpl._printDate(gregorianCalendar) : DatatypeConverterImpl._printDateTime(gregorianCalendar);
          }
        });
    arrayList1.add(new StringImpl<File>(File.class, new QName[] { createXS("string") }) {
          public File parse(CharSequence param1CharSequence) { return new File(WhiteSpaceProcessor.trim(param1CharSequence).toString()); }
          
          public String print(File param1File) { return param1File.getPath(); }
        });
    arrayList1.add(new StringImpl<URL>(URL.class, new QName[] { createXS("anyURI") }) {
          public URL parse(CharSequence param1CharSequence) throws SAXException {
            TODO.checkSpec("JSR222 Issue #42");
            try {
              return new URL(WhiteSpaceProcessor.trim(param1CharSequence).toString());
            } catch (MalformedURLException malformedURLException) {
              UnmarshallingContext.getInstance().handleError(malformedURLException);
              return null;
            } 
          }
          
          public String print(URL param1URL) { return param1URL.toExternalForm(); }
        });
    if (str == null)
      arrayList1.add(new StringImpl<URI>(URI.class, new QName[] { createXS("string") }) {
            public URI parse(CharSequence param1CharSequence) throws SAXException {
              try {
                return new URI(param1CharSequence.toString());
              } catch (URISyntaxException uRISyntaxException) {
                UnmarshallingContext.getInstance().handleError(uRISyntaxException);
                return null;
              } 
            }
            
            public String print(URI param1URI) { return param1URI.toString(); }
          }); 
    arrayList1.add(new StringImpl<Class>(Class.class, new QName[] { createXS("string") }) {
          public Class parse(CharSequence param1CharSequence) throws SAXException {
            TODO.checkSpec("JSR222 Issue #42");
            try {
              String str = WhiteSpaceProcessor.trim(param1CharSequence).toString();
              ClassLoader classLoader = (UnmarshallingContext.getInstance()).classLoader;
              if (classLoader == null)
                classLoader = Thread.currentThread().getContextClassLoader(); 
              return (classLoader != null) ? classLoader.loadClass(str) : Class.forName(str);
            } catch (ClassNotFoundException classNotFoundException) {
              UnmarshallingContext.getInstance().handleError(classNotFoundException);
              return null;
            } 
          }
          
          public String print(Class param1Class) { return param1Class.getName(); }
        });
    arrayList1.add(new PcdataImpl<Image>(Image.class, new QName[] { createXS("base64Binary") }) {
          public Image parse(CharSequence param1CharSequence) throws SAXException {
            try {
              if (param1CharSequence instanceof Base64Data) {
                byteArrayInputStream = ((Base64Data)param1CharSequence).getInputStream();
              } else {
                byteArrayInputStream = new ByteArrayInputStream(RuntimeBuiltinLeafInfoImpl.decodeBase64(param1CharSequence));
              } 
              try {
                return ImageIO.read(byteArrayInputStream);
              } finally {
                byteArrayInputStream.close();
              } 
            } catch (IOException iOException) {
              UnmarshallingContext.getInstance().handleError(iOException);
              return null;
            } 
          }
          
          private BufferedImage convertToBufferedImage(Image param1Image) throws IOException {
            if (param1Image instanceof BufferedImage)
              return (BufferedImage)param1Image; 
            MediaTracker mediaTracker = new MediaTracker(new Component(this) {
                
                });
            mediaTracker.addImage(param1Image, 0);
            try {
              mediaTracker.waitForAll();
            } catch (InterruptedException interruptedException) {
              throw new IOException(interruptedException.getMessage());
            } 
            BufferedImage bufferedImage = new BufferedImage(param1Image.getWidth(null), param1Image.getHeight(null), 2);
            Graphics2D graphics2D = bufferedImage.createGraphics();
            graphics2D.drawImage(param1Image, 0, 0, null);
            return bufferedImage;
          }
          
          public Base64Data print(Image param1Image) {
            ByteArrayOutputStreamEx byteArrayOutputStreamEx = new ByteArrayOutputStreamEx();
            XMLSerializer xMLSerializer = XMLSerializer.getInstance();
            String str = xMLSerializer.getXMIMEContentType();
            if (str == null || str.startsWith("image/*"))
              str = "image/png"; 
            try {
              Iterator iterator = ImageIO.getImageWritersByMIMEType(str);
              if (iterator.hasNext()) {
                ImageWriter imageWriter = (ImageWriter)iterator.next();
                ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(byteArrayOutputStreamEx);
                imageWriter.setOutput(imageOutputStream);
                imageWriter.write(convertToBufferedImage(param1Image));
                imageOutputStream.close();
                imageWriter.dispose();
              } else {
                xMLSerializer.handleEvent(new ValidationEventImpl(1, Messages.NO_IMAGE_WRITER.format(new Object[] { str }, ), xMLSerializer.getCurrentLocation(null)));
                throw new RuntimeException("no encoder for MIME type " + str);
              } 
            } catch (IOException iOException) {
              xMLSerializer.handleError(iOException);
              throw new RuntimeException(iOException);
            } 
            Base64Data base64Data = new Base64Data();
            byteArrayOutputStreamEx.set(base64Data, str);
            return base64Data;
          }
        });
    arrayList1.add(new PcdataImpl<DataHandler>(DataHandler.class, new QName[] { createXS("base64Binary") }) {
          public DataHandler parse(CharSequence param1CharSequence) { return (param1CharSequence instanceof Base64Data) ? ((Base64Data)param1CharSequence).getDataHandler() : new DataHandler(new ByteArrayDataSource(RuntimeBuiltinLeafInfoImpl.decodeBase64(param1CharSequence), UnmarshallingContext.getInstance().getXMIMEContentType())); }
          
          public Base64Data print(DataHandler param1DataHandler) {
            Base64Data base64Data = new Base64Data();
            base64Data.set(param1DataHandler);
            return base64Data;
          }
        });
    arrayList1.add(new PcdataImpl<Source>(Source.class, new QName[] { createXS("base64Binary") }) {
          public Source parse(CharSequence param1CharSequence) throws SAXException {
            try {
              return (param1CharSequence instanceof Base64Data) ? new DataSourceSource(((Base64Data)param1CharSequence).getDataHandler()) : new DataSourceSource(new ByteArrayDataSource(RuntimeBuiltinLeafInfoImpl.decodeBase64(param1CharSequence), UnmarshallingContext.getInstance().getXMIMEContentType()));
            } catch (MimeTypeParseException mimeTypeParseException) {
              UnmarshallingContext.getInstance().handleError(mimeTypeParseException);
              return null;
            } 
          }
          
          public Base64Data print(Source param1Source) {
            XMLSerializer xMLSerializer = XMLSerializer.getInstance();
            Base64Data base64Data = new Base64Data();
            String str1 = xMLSerializer.getXMIMEContentType();
            MimeType mimeType = null;
            if (str1 != null)
              try {
                mimeType = new MimeType(str1);
              } catch (MimeTypeParseException mimeTypeParseException) {
                xMLSerializer.handleError(mimeTypeParseException);
              }  
            if (param1Source instanceof DataSourceSource) {
              DataSource dataSource = ((DataSourceSource)param1Source).getDataSource();
              String str = dataSource.getContentType();
              if (str != null && (str1 == null || str1.equals(str))) {
                base64Data.set(new DataHandler(dataSource));
                return base64Data;
              } 
            } 
            String str2 = null;
            if (mimeType != null)
              str2 = mimeType.getParameter("charset"); 
            if (str2 == null)
              str2 = "UTF-8"; 
            try {
              ByteArrayOutputStreamEx byteArrayOutputStreamEx = new ByteArrayOutputStreamEx();
              Transformer transformer = xMLSerializer.getIdentityTransformer();
              String str = transformer.getOutputProperty("encoding");
              transformer.setOutputProperty("encoding", str2);
              transformer.transform(param1Source, new StreamResult(new OutputStreamWriter(byteArrayOutputStreamEx, str2)));
              transformer.setOutputProperty("encoding", str);
              byteArrayOutputStreamEx.set(base64Data, "application/xml; charset=" + str2);
              return base64Data;
            } catch (TransformerException transformerException) {
              xMLSerializer.handleError(transformerException);
            } catch (UnsupportedEncodingException unsupportedEncodingException) {
              xMLSerializer.handleError(unsupportedEncodingException);
            } 
            base64Data.set(new byte[0], "application/xml");
            return base64Data;
          }
        });
    arrayList1.add(new StringImpl<XMLGregorianCalendar>(XMLGregorianCalendar.class, new QName[] { createXS("anySimpleType"), DatatypeConstants.DATE, DatatypeConstants.DATETIME, DatatypeConstants.TIME, DatatypeConstants.GMONTH, DatatypeConstants.GDAY, DatatypeConstants.GYEAR, DatatypeConstants.GYEARMONTH, DatatypeConstants.GMONTHDAY }) {
          public String print(XMLGregorianCalendar param1XMLGregorianCalendar) {
            XMLSerializer xMLSerializer = XMLSerializer.getInstance();
            QName qName = xMLSerializer.getSchemaType();
            if (qName != null)
              try {
                RuntimeBuiltinLeafInfoImpl.checkXmlGregorianCalendarFieldRef(qName, param1XMLGregorianCalendar);
                String str = (String)xmlGregorianCalendarFormatString.get(qName);
                if (str != null)
                  return format(str, param1XMLGregorianCalendar); 
              } catch (MarshalException marshalException) {
                xMLSerializer.handleEvent(new ValidationEventImpl(0, marshalException.getMessage(), xMLSerializer.getCurrentLocation(null)));
                return "";
              }  
            return param1XMLGregorianCalendar.toXMLFormat();
          }
          
          public XMLGregorianCalendar parse(CharSequence param1CharSequence) throws SAXException {
            try {
              return DatatypeConverterImpl.getDatatypeFactory().newXMLGregorianCalendar(param1CharSequence.toString().trim());
            } catch (Exception exception) {
              UnmarshallingContext.getInstance().handleError(exception);
              return null;
            } 
          }
          
          private String format(String param1String, XMLGregorianCalendar param1XMLGregorianCalendar) {
            StringBuilder stringBuilder = new StringBuilder();
            byte b = 0;
            int i = param1String.length();
            while (b < i) {
              int j;
              char c = param1String.charAt(b++);
              if (c != '%') {
                stringBuilder.append(c);
                continue;
              } 
              switch (param1String.charAt(b++)) {
                case 'Y':
                  printNumber(stringBuilder, param1XMLGregorianCalendar.getEonAndYear(), 4);
                  continue;
                case 'M':
                  printNumber(stringBuilder, param1XMLGregorianCalendar.getMonth(), 2);
                  continue;
                case 'D':
                  printNumber(stringBuilder, param1XMLGregorianCalendar.getDay(), 2);
                  continue;
                case 'h':
                  printNumber(stringBuilder, param1XMLGregorianCalendar.getHour(), 2);
                  continue;
                case 'm':
                  printNumber(stringBuilder, param1XMLGregorianCalendar.getMinute(), 2);
                  continue;
                case 's':
                  printNumber(stringBuilder, param1XMLGregorianCalendar.getSecond(), 2);
                  if (param1XMLGregorianCalendar.getFractionalSecond() != null) {
                    String str = param1XMLGregorianCalendar.getFractionalSecond().toPlainString();
                    stringBuilder.append(str.substring(1, str.length()));
                  } 
                  continue;
                case 'z':
                  j = param1XMLGregorianCalendar.getTimezone();
                  if (j == 0) {
                    stringBuilder.append('Z');
                    continue;
                  } 
                  if (j != Integer.MIN_VALUE) {
                    if (j < 0) {
                      stringBuilder.append('-');
                      j *= -1;
                    } else {
                      stringBuilder.append('+');
                    } 
                    printNumber(stringBuilder, j / 60, 2);
                    stringBuilder.append(':');
                    printNumber(stringBuilder, j % 60, 2);
                  } 
                  continue;
              } 
              throw new InternalError();
            } 
            return stringBuilder.toString();
          }
          
          private void printNumber(StringBuilder param1StringBuilder, BigInteger param1BigInteger, int param1Int) {
            String str = param1BigInteger.toString();
            for (int i = str.length(); i < param1Int; i++)
              param1StringBuilder.append('0'); 
            param1StringBuilder.append(str);
          }
          
          private void printNumber(StringBuilder param1StringBuilder, int param1Int1, int param1Int2) {
            String str = String.valueOf(param1Int1);
            for (int i = str.length(); i < param1Int2; i++)
              param1StringBuilder.append('0'); 
            param1StringBuilder.append(str);
          }
          
          public QName getTypeName(XMLGregorianCalendar param1XMLGregorianCalendar) { return param1XMLGregorianCalendar.getXMLSchemaType(); }
        });
    ArrayList arrayList2 = new ArrayList();
    arrayList2.add(STRING);
    arrayList2.add(new StringImpl<Boolean>(Boolean.class, new QName[] { createXS("boolean") }) {
          public Boolean parse(CharSequence param1CharSequence) { return DatatypeConverterImpl._parseBoolean(param1CharSequence); }
          
          public String print(Boolean param1Boolean) { return param1Boolean.toString(); }
        });
    arrayList2.add(new PcdataImpl<byte[]>(byte[].class, new QName[] { createXS("base64Binary"), createXS("hexBinary") }) {
          public byte[] parse(CharSequence param1CharSequence) { return RuntimeBuiltinLeafInfoImpl.decodeBase64(param1CharSequence); }
          
          public Base64Data print(byte[] param1ArrayOfByte) {
            XMLSerializer xMLSerializer = XMLSerializer.getInstance();
            Base64Data base64Data = new Base64Data();
            String str = xMLSerializer.getXMIMEContentType();
            base64Data.set(param1ArrayOfByte, str);
            return base64Data;
          }
        });
    arrayList2.add(new StringImpl<Byte>(Byte.class, new QName[] { createXS("byte") }) {
          public Byte parse(CharSequence param1CharSequence) { return Byte.valueOf(DatatypeConverterImpl._parseByte(param1CharSequence)); }
          
          public String print(Byte param1Byte) { return DatatypeConverterImpl._printByte(param1Byte.byteValue()); }
        });
    arrayList2.add(new StringImpl<Short>(Short.class, new QName[] { createXS("short"), createXS("unsignedByte") }) {
          public Short parse(CharSequence param1CharSequence) { return Short.valueOf(DatatypeConverterImpl._parseShort(param1CharSequence)); }
          
          public String print(Short param1Short) { return DatatypeConverterImpl._printShort(param1Short.shortValue()); }
        });
    arrayList2.add(new StringImpl<Integer>(Integer.class, new QName[] { createXS("int"), createXS("unsignedShort") }) {
          public Integer parse(CharSequence param1CharSequence) { return Integer.valueOf(DatatypeConverterImpl._parseInt(param1CharSequence)); }
          
          public String print(Integer param1Integer) { return DatatypeConverterImpl._printInt(param1Integer.intValue()); }
        });
    arrayList2.add(new StringImpl<Long>(Long.class, new QName[] { createXS("long"), createXS("unsignedInt") }) {
          public Long parse(CharSequence param1CharSequence) { return Long.valueOf(DatatypeConverterImpl._parseLong(param1CharSequence)); }
          
          public String print(Long param1Long) { return DatatypeConverterImpl._printLong(param1Long.longValue()); }
        });
    arrayList2.add(new StringImpl<Float>(Float.class, new QName[] { createXS("float") }) {
          public Float parse(CharSequence param1CharSequence) { return Float.valueOf(DatatypeConverterImpl._parseFloat(param1CharSequence.toString())); }
          
          public String print(Float param1Float) { return DatatypeConverterImpl._printFloat(param1Float.floatValue()); }
        });
    arrayList2.add(new StringImpl<Double>(Double.class, new QName[] { createXS("double") }) {
          public Double parse(CharSequence param1CharSequence) { return Double.valueOf(DatatypeConverterImpl._parseDouble(param1CharSequence)); }
          
          public String print(Double param1Double) { return DatatypeConverterImpl._printDouble(param1Double.doubleValue()); }
        });
    arrayList2.add(new StringImpl<BigInteger>(BigInteger.class, new QName[] { createXS("integer"), createXS("positiveInteger"), createXS("negativeInteger"), createXS("nonPositiveInteger"), createXS("nonNegativeInteger"), createXS("unsignedLong") }) {
          public BigInteger parse(CharSequence param1CharSequence) { return DatatypeConverterImpl._parseInteger(param1CharSequence); }
          
          public String print(BigInteger param1BigInteger) { return DatatypeConverterImpl._printInteger(param1BigInteger); }
        });
    arrayList2.add(new StringImpl<BigDecimal>(BigDecimal.class, new QName[] { createXS("decimal") }) {
          public BigDecimal parse(CharSequence param1CharSequence) { return DatatypeConverterImpl._parseDecimal(param1CharSequence.toString()); }
          
          public String print(BigDecimal param1BigDecimal) { return DatatypeConverterImpl._printDecimal(param1BigDecimal); }
        });
    arrayList2.add(new StringImpl<QName>(QName.class, new QName[] { createXS("QName") }) {
          public QName parse(CharSequence param1CharSequence) throws SAXException {
            try {
              return DatatypeConverterImpl._parseQName(param1CharSequence.toString(), UnmarshallingContext.getInstance());
            } catch (IllegalArgumentException illegalArgumentException) {
              UnmarshallingContext.getInstance().handleError(illegalArgumentException);
              return null;
            } 
          }
          
          public String print(QName param1QName) { return DatatypeConverterImpl._printQName(param1QName, XMLSerializer.getInstance().getNamespaceContext()); }
          
          public boolean useNamespace() { return true; }
          
          public void declareNamespace(QName param1QName, XMLSerializer param1XMLSerializer) { param1XMLSerializer.getNamespaceContext().declareNamespace(param1QName.getNamespaceURI(), param1QName.getPrefix(), false); }
        });
    if (str != null)
      arrayList2.add(new StringImpl<URI>(URI.class, new QName[] { createXS("anyURI") }) {
            public URI parse(CharSequence param1CharSequence) throws SAXException {
              try {
                return new URI(param1CharSequence.toString());
              } catch (URISyntaxException uRISyntaxException) {
                UnmarshallingContext.getInstance().handleError(uRISyntaxException);
                return null;
              } 
            }
            
            public String print(URI param1URI) { return param1URI.toString(); }
          }); 
    arrayList2.add(new StringImpl<Duration>(Duration.class, new QName[] { createXS("duration") }) {
          public String print(Duration param1Duration) { return param1Duration.toString(); }
          
          public Duration parse(CharSequence param1CharSequence) {
            TODO.checkSpec("JSR222 Issue #42");
            return DatatypeConverterImpl.getDatatypeFactory().newDuration(param1CharSequence.toString());
          }
        });
    arrayList2.add(new StringImpl<Void>(Void.class, new QName[0]) {
          public String print(Void param1Void) { return ""; }
          
          public Void parse(CharSequence param1CharSequence) { return null; }
        });
    ArrayList arrayList3 = new ArrayList(arrayList1.size() + arrayList2.size() + 1);
    arrayList3.addAll(arrayList1);
    try {
      arrayList3.add(new UUIDImpl());
    } catch (LinkageError linkageError) {}
    arrayList3.addAll(arrayList2);
    builtinBeanInfos = Collections.unmodifiableList(arrayList3);
    xmlGregorianCalendarFormatString = new HashMap();
    Map map = xmlGregorianCalendarFormatString;
    map.put(DatatypeConstants.DATETIME, "%Y-%M-%DT%h:%m:%s%z");
    map.put(DatatypeConstants.DATE, "%Y-%M-%D%z");
    map.put(DatatypeConstants.TIME, "%h:%m:%s%z");
    map.put(DatatypeConstants.GMONTH, "--%M--%z");
    map.put(DatatypeConstants.GDAY, "---%D%z");
    map.put(DatatypeConstants.GYEAR, "%Y%z");
    map.put(DatatypeConstants.GYEARMONTH, "%Y-%M%z");
    map.put(DatatypeConstants.GMONTHDAY, "--%M-%D%z");
    xmlGregorianCalendarFieldRef = new HashMap();
    map = xmlGregorianCalendarFieldRef;
    map.put(DatatypeConstants.DATETIME, Integer.valueOf(17895697));
    map.put(DatatypeConstants.DATE, Integer.valueOf(17895424));
    map.put(DatatypeConstants.TIME, Integer.valueOf(16777489));
    map.put(DatatypeConstants.GDAY, Integer.valueOf(16781312));
    map.put(DatatypeConstants.GMONTH, Integer.valueOf(16842752));
    map.put(DatatypeConstants.GYEAR, Integer.valueOf(17825792));
    map.put(DatatypeConstants.GYEARMONTH, Integer.valueOf(17891328));
    map.put(DatatypeConstants.GMONTHDAY, Integer.valueOf(16846848));
  }
  
  private static abstract class PcdataImpl<T> extends RuntimeBuiltinLeafInfoImpl<T> {
    protected PcdataImpl(Class param1Class, QName... param1VarArgs) { super(param1Class, param1VarArgs, null); }
    
    public abstract Pcdata print(T param1T) throws AccessorException;
    
    public final void writeText(XMLSerializer param1XMLSerializer, T param1T, String param1String) throws IOException, SAXException, XMLStreamException, AccessorException { param1XMLSerializer.text(print(param1T), param1String); }
    
    public final void writeLeafElement(XMLSerializer param1XMLSerializer, Name param1Name, T param1T, String param1String) throws IOException, SAXException, XMLStreamException, AccessorException { param1XMLSerializer.leafElement(param1Name, print(param1T), param1String); }
  }
  
  private static abstract class StringImpl<T> extends RuntimeBuiltinLeafInfoImpl<T> {
    protected StringImpl(Class param1Class, QName... param1VarArgs) { super(param1Class, param1VarArgs, null); }
    
    public abstract String print(T param1T) throws AccessorException;
    
    public void writeText(XMLSerializer param1XMLSerializer, T param1T, String param1String) throws IOException, SAXException, XMLStreamException, AccessorException { param1XMLSerializer.text(print(param1T), param1String); }
    
    public void writeLeafElement(XMLSerializer param1XMLSerializer, Name param1Name, T param1T, String param1String) throws IOException, SAXException, XMLStreamException, AccessorException { param1XMLSerializer.leafElement(param1Name, print(param1T), param1String); }
  }
  
  private static class StringImplImpl extends StringImpl<String> {
    public StringImplImpl(Class param1Class, QName[] param1ArrayOfQName) { super(param1Class, param1ArrayOfQName); }
    
    public String parse(CharSequence param1CharSequence) { return param1CharSequence.toString(); }
    
    public String print(String param1String) { return param1String; }
    
    public final void writeText(XMLSerializer param1XMLSerializer, String param1String1, String param1String2) throws IOException, SAXException, XMLStreamException { param1XMLSerializer.text(param1String1, param1String2); }
    
    public final void writeLeafElement(XMLSerializer param1XMLSerializer, Name param1Name, String param1String1, String param1String2) throws IOException, SAXException, XMLStreamException { param1XMLSerializer.leafElement(param1Name, param1String1, param1String2); }
  }
  
  private static class UUIDImpl extends StringImpl<UUID> {
    public UUIDImpl() { super(UUID.class, new QName[] { RuntimeBuiltinLeafInfoImpl.access$400("string") }); }
    
    public UUID parse(CharSequence param1CharSequence) throws SAXException {
      TODO.checkSpec("JSR222 Issue #42");
      try {
        return UUID.fromString(WhiteSpaceProcessor.trim(param1CharSequence).toString());
      } catch (IllegalArgumentException illegalArgumentException) {
        UnmarshallingContext.getInstance().handleError(illegalArgumentException);
        return null;
      } 
    }
    
    public String print(UUID param1UUID) { return param1UUID.toString(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\RuntimeBuiltinLeafInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */