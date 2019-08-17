package sun.awt;

final class MostRecentKeyValue {
  Object key;
  
  Object value;
  
  MostRecentKeyValue(Object paramObject1, Object paramObject2) {
    this.key = paramObject1;
    this.value = paramObject2;
  }
  
  void setPair(Object paramObject1, Object paramObject2) {
    this.key = paramObject1;
    this.value = paramObject2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\MostRecentKeyValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */