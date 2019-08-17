package javax.xml.bind.annotation.adapters;

public abstract class XmlAdapter<ValueType, BoundType> extends Object {
  public abstract BoundType unmarshal(ValueType paramValueType) throws Exception;
  
  public abstract ValueType marshal(BoundType paramBoundType) throws Exception;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\annotation\adapters\XmlAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */