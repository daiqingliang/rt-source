package javax.xml.bind.annotation.adapters;

import javax.xml.bind.DatatypeConverter;

public final class HexBinaryAdapter extends XmlAdapter<String, byte[]> {
  public byte[] unmarshal(String paramString) { return (paramString == null) ? null : DatatypeConverter.parseHexBinary(paramString); }
  
  public String marshal(byte[] paramArrayOfByte) { return (paramArrayOfByte == null) ? null : DatatypeConverter.printHexBinary(paramArrayOfByte); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\annotation\adapters\HexBinaryAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */