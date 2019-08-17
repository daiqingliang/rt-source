package javax.swing.text.html.parser;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.awt.AppContext;

public class DTD implements DTDConstants {
  public String name;
  
  public Vector<Element> elements = new Vector();
  
  public Hashtable<String, Element> elementHash = new Hashtable();
  
  public Hashtable<Object, Entity> entityHash = new Hashtable();
  
  public final Element pcdata = getElement("#pcdata");
  
  public final Element html = getElement("html");
  
  public final Element meta = getElement("meta");
  
  public final Element base = getElement("base");
  
  public final Element isindex = getElement("isindex");
  
  public final Element head = getElement("head");
  
  public final Element body = getElement("body");
  
  public final Element applet = getElement("applet");
  
  public final Element param = getElement("param");
  
  public final Element p = getElement("p");
  
  public final Element title = getElement("title");
  
  final Element style = getElement("style");
  
  final Element link = getElement("link");
  
  final Element script = getElement("script");
  
  public static final int FILE_VERSION = 1;
  
  private static final Object DTD_HASH_KEY = new Object();
  
  protected DTD(String paramString) {
    this.name = paramString;
    defEntity("#RE", 65536, 13);
    defEntity("#RS", 65536, 10);
    defEntity("#SPACE", 65536, 32);
    defineElement("unknown", 17, false, true, null, null, null, null);
  }
  
  public String getName() { return this.name; }
  
  public Entity getEntity(String paramString) { return (Entity)this.entityHash.get(paramString); }
  
  public Entity getEntity(int paramInt) { return (Entity)this.entityHash.get(Integer.valueOf(paramInt)); }
  
  boolean elementExists(String paramString) { return (!"unknown".equals(paramString) && this.elementHash.get(paramString) != null); }
  
  public Element getElement(String paramString) {
    Element element = (Element)this.elementHash.get(paramString);
    if (element == null) {
      element = new Element(paramString, this.elements.size());
      this.elements.addElement(element);
      this.elementHash.put(paramString, element);
    } 
    return element;
  }
  
  public Element getElement(int paramInt) { return (Element)this.elements.elementAt(paramInt); }
  
  public Entity defineEntity(String paramString, int paramInt, char[] paramArrayOfChar) {
    Entity entity = (Entity)this.entityHash.get(paramString);
    if (entity == null) {
      entity = new Entity(paramString, paramInt, paramArrayOfChar);
      this.entityHash.put(paramString, entity);
      if ((paramInt & 0x10000) != 0 && paramArrayOfChar.length == 1)
        switch (paramInt & 0xFFFEFFFF) {
          case 1:
          case 11:
            this.entityHash.put(Integer.valueOf(paramArrayOfChar[0]), entity);
            break;
        }  
    } 
    return entity;
  }
  
  public Element defineElement(String paramString, int paramInt, boolean paramBoolean1, boolean paramBoolean2, ContentModel paramContentModel, BitSet paramBitSet1, BitSet paramBitSet2, AttributeList paramAttributeList) {
    Element element = getElement(paramString);
    element.type = paramInt;
    element.oStart = paramBoolean1;
    element.oEnd = paramBoolean2;
    element.content = paramContentModel;
    element.exclusions = paramBitSet1;
    element.inclusions = paramBitSet2;
    element.atts = paramAttributeList;
    return element;
  }
  
  public void defineAttributes(String paramString, AttributeList paramAttributeList) {
    Element element = getElement(paramString);
    element.atts = paramAttributeList;
  }
  
  public Entity defEntity(String paramString, int paramInt1, int paramInt2) {
    char[] arrayOfChar = { (char)paramInt2 };
    return defineEntity(paramString, paramInt1, arrayOfChar);
  }
  
  protected Entity defEntity(String paramString1, int paramInt, String paramString2) {
    int i = paramString2.length();
    char[] arrayOfChar = new char[i];
    paramString2.getChars(0, i, arrayOfChar, 0);
    return defineEntity(paramString1, paramInt, arrayOfChar);
  }
  
  protected Element defElement(String paramString, int paramInt, boolean paramBoolean1, boolean paramBoolean2, ContentModel paramContentModel, String[] paramArrayOfString1, String[] paramArrayOfString2, AttributeList paramAttributeList) {
    BitSet bitSet1 = null;
    if (paramArrayOfString1 != null && paramArrayOfString1.length > 0) {
      bitSet1 = new BitSet();
      for (String str : paramArrayOfString1) {
        if (str.length() > 0)
          bitSet1.set(getElement(str).getIndex()); 
      } 
    } 
    BitSet bitSet2 = null;
    if (paramArrayOfString2 != null && paramArrayOfString2.length > 0) {
      bitSet2 = new BitSet();
      for (String str : paramArrayOfString2) {
        if (str.length() > 0)
          bitSet2.set(getElement(str).getIndex()); 
      } 
    } 
    return defineElement(paramString, paramInt, paramBoolean1, paramBoolean2, paramContentModel, bitSet1, bitSet2, paramAttributeList);
  }
  
  protected AttributeList defAttributeList(String paramString1, int paramInt1, int paramInt2, String paramString2, String paramString3, AttributeList paramAttributeList) {
    Vector vector = null;
    if (paramString3 != null) {
      vector = new Vector();
      StringTokenizer stringTokenizer = new StringTokenizer(paramString3, "|");
      while (stringTokenizer.hasMoreTokens()) {
        String str = stringTokenizer.nextToken();
        if (str.length() > 0)
          vector.addElement(str); 
      } 
    } 
    return new AttributeList(paramString1, paramInt1, paramInt2, paramString2, vector, paramAttributeList);
  }
  
  protected ContentModel defContentModel(int paramInt, Object paramObject, ContentModel paramContentModel) { return new ContentModel(paramInt, paramObject, paramContentModel); }
  
  public String toString() { return this.name; }
  
  public static void putDTDHash(String paramString, DTD paramDTD) { getDtdHash().put(paramString, paramDTD); }
  
  public static DTD getDTD(String paramString) throws IOException {
    paramString = paramString.toLowerCase();
    DTD dTD = (DTD)getDtdHash().get(paramString);
    if (dTD == null)
      dTD = new DTD(paramString); 
    return dTD;
  }
  
  private static Hashtable<String, DTD> getDtdHash() {
    AppContext appContext = AppContext.getAppContext();
    Hashtable hashtable = (Hashtable)appContext.get(DTD_HASH_KEY);
    if (hashtable == null) {
      hashtable = new Hashtable();
      appContext.put(DTD_HASH_KEY, hashtable);
    } 
    return hashtable;
  }
  
  public void read(DataInputStream paramDataInputStream) throws IOException {
    if (paramDataInputStream.readInt() != 1);
    String[] arrayOfString = new String[paramDataInputStream.readShort()];
    short s;
    for (s = 0; s < arrayOfString.length; s++)
      arrayOfString[s] = paramDataInputStream.readUTF(); 
    s = paramDataInputStream.readShort();
    byte b;
    for (b = 0; b < s; b++) {
      short s1 = paramDataInputStream.readShort();
      byte b1 = paramDataInputStream.readByte();
      String str = paramDataInputStream.readUTF();
      defEntity(arrayOfString[s1], b1 | 0x10000, str);
    } 
    s = paramDataInputStream.readShort();
    for (b = 0; b < s; b++) {
      short s1 = paramDataInputStream.readShort();
      byte b1 = paramDataInputStream.readByte();
      byte b2 = paramDataInputStream.readByte();
      ContentModel contentModel = readContentModel(paramDataInputStream, arrayOfString);
      String[] arrayOfString1 = readNameArray(paramDataInputStream, arrayOfString);
      String[] arrayOfString2 = readNameArray(paramDataInputStream, arrayOfString);
      AttributeList attributeList = readAttributeList(paramDataInputStream, arrayOfString);
      defElement(arrayOfString[s1], b1, ((b2 & true) != 0), ((b2 & 0x2) != 0), contentModel, arrayOfString1, arrayOfString2, attributeList);
    } 
  }
  
  private ContentModel readContentModel(DataInputStream paramDataInputStream, String[] paramArrayOfString) throws IOException {
    ContentModel contentModel2;
    ContentModel contentModel1;
    Element element;
    byte b2;
    byte b1 = paramDataInputStream.readByte();
    switch (b1) {
      case 0:
        return null;
      case 1:
        b2 = paramDataInputStream.readByte();
        contentModel1 = readContentModel(paramDataInputStream, paramArrayOfString);
        contentModel2 = readContentModel(paramDataInputStream, paramArrayOfString);
        return defContentModel(b2, contentModel1, contentModel2);
      case 2:
        b2 = paramDataInputStream.readByte();
        element = getElement(paramArrayOfString[paramDataInputStream.readShort()]);
        contentModel2 = readContentModel(paramDataInputStream, paramArrayOfString);
        return defContentModel(b2, element, contentModel2);
    } 
    throw new IOException("bad bdtd");
  }
  
  private String[] readNameArray(DataInputStream paramDataInputStream, String[] paramArrayOfString) throws IOException {
    short s = paramDataInputStream.readShort();
    if (s == 0)
      return null; 
    String[] arrayOfString = new String[s];
    for (byte b = 0; b < s; b++)
      arrayOfString[b] = paramArrayOfString[paramDataInputStream.readShort()]; 
    return arrayOfString;
  }
  
  private AttributeList readAttributeList(DataInputStream paramDataInputStream, String[] paramArrayOfString) throws IOException {
    AttributeList attributeList = null;
    for (byte b = paramDataInputStream.readByte(); b > 0; b--) {
      short s1 = paramDataInputStream.readShort();
      byte b1 = paramDataInputStream.readByte();
      byte b2 = paramDataInputStream.readByte();
      short s2 = paramDataInputStream.readShort();
      String str = (s2 == -1) ? null : paramArrayOfString[s2];
      Vector vector = null;
      short s3 = paramDataInputStream.readShort();
      if (s3 > 0) {
        vector = new Vector(s3);
        for (byte b3 = 0; b3 < s3; b3++)
          vector.addElement(paramArrayOfString[paramDataInputStream.readShort()]); 
      } 
      attributeList = new AttributeList(paramArrayOfString[s1], b1, b2, str, vector, attributeList);
    } 
    return attributeList;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\parser\DTD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */