package javax.swing.text.html.parser;

import java.util.Hashtable;

public final class Entity implements DTDConstants {
  public String name;
  
  public int type;
  
  public char[] data;
  
  static Hashtable<String, Integer> entityTypes = new Hashtable();
  
  public Entity(String paramString, int paramInt, char[] paramArrayOfChar) {
    this.name = paramString;
    this.type = paramInt;
    this.data = paramArrayOfChar;
  }
  
  public String getName() { return this.name; }
  
  public int getType() { return this.type & 0xFFFF; }
  
  public boolean isParameter() { return ((this.type & 0x40000) != 0); }
  
  public boolean isGeneral() { return ((this.type & 0x10000) != 0); }
  
  public char[] getData() { return this.data; }
  
  public String getString() { return new String(this.data, 0, this.data.length); }
  
  public static int name2type(String paramString) {
    Integer integer = (Integer)entityTypes.get(paramString);
    return (integer == null) ? 1 : integer.intValue();
  }
  
  static  {
    entityTypes.put("PUBLIC", Integer.valueOf(10));
    entityTypes.put("CDATA", Integer.valueOf(1));
    entityTypes.put("SDATA", Integer.valueOf(11));
    entityTypes.put("PI", Integer.valueOf(12));
    entityTypes.put("STARTTAG", Integer.valueOf(13));
    entityTypes.put("ENDTAG", Integer.valueOf(14));
    entityTypes.put("MS", Integer.valueOf(15));
    entityTypes.put("MD", Integer.valueOf(16));
    entityTypes.put("SYSTEM", Integer.valueOf(17));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\parser\Entity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */