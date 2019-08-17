package com.sun.org.apache.xml.internal.serializer.utils;

import java.util.ListResourceBundle;

public class SerializerMessages_zh_TW extends ListResourceBundle {
  public Object[][] getContents() { return new Object[][] { 
        { "BAD_MSGKEY", "訊息索引鍵 ''{0}'' 的訊息類別不是 ''{1}''" }, { "BAD_MSGFORMAT", "訊息類別 ''{1}'' 中的訊息 ''{0}'' 格式不正確。" }, { "ER_SERIALIZER_NOT_CONTENTHANDLER", "serializer 類別 ''{0}'' 不實行 org.xml.sax.ContentHandler。" }, { "ER_RESOURCE_COULD_NOT_FIND", "找不到資源 [ {0} ]。\n{1}" }, { "ER_RESOURCE_COULD_NOT_LOAD", "無法載入資源 [ {0} ]: {1} \n {2} \t {3}" }, { "ER_BUFFER_SIZE_LESSTHAN_ZERO", "緩衝區大小 <=0" }, { "ER_INVALID_UTF16_SURROGATE", "偵測到無效的 UTF-16 代理: {0}？" }, { "ER_OIERROR", "IO 錯誤" }, { "ER_ILLEGAL_ATTRIBUTE_POSITION", "在產生子項節點之後，或在產生元素之前，不可新增屬性 {0}。屬性會被忽略。" }, { "ER_NAMESPACE_PREFIX", "字首 ''{0}'' 的命名空間尚未宣告。" }, 
        { "ER_STRAY_ATTRIBUTE", "屬性 ''{0}'' 在元素之外。" }, { "ER_STRAY_NAMESPACE", "命名空間宣告 ''{0}''=''{1}'' 超出元素外。" }, { "ER_COULD_NOT_LOAD_RESOURCE", "無法載入 ''{0}'' (檢查 CLASSPATH)，目前只使用預設值" }, { "ER_ILLEGAL_CHARACTER", "嘗試輸出整數值 {0} 的字元，但是它不是以指定的 {1} 輸出編碼呈現。" }, { "ER_COULD_NOT_LOAD_METHOD_PROPERTY", "無法載入輸出方法 ''{1}'' 的屬性檔 ''{0}'' (檢查 CLASSPATH)" }, { "ER_INVALID_PORT", "無效的連接埠號碼" }, { "ER_PORT_WHEN_HOST_NULL", "主機為空值時，無法設定連接埠" }, { "ER_HOST_ADDRESS_NOT_WELLFORMED", "主機沒有完整的位址" }, { "ER_SCHEME_NOT_CONFORMANT", "配置不一致。" }, { "ER_SCHEME_FROM_NULL_STRING", "無法從空值字串設定配置" }, 
        { "ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE", "路徑包含無效的遁離序列" }, { "ER_PATH_INVALID_CHAR", "路徑包含無效的字元: {0}" }, { "ER_FRAG_INVALID_CHAR", "片段包含無效的字元" }, { "ER_FRAG_WHEN_PATH_NULL", "路徑為空值時，無法設定片段" }, { "ER_FRAG_FOR_GENERIC_URI", "只能對一般 URI 設定片段" }, { "ER_NO_SCHEME_IN_URI", "在 URI 找不到配置" }, { "ER_CANNOT_INIT_URI_EMPTY_PARMS", "無法以空白參數起始設定 URI" }, { "ER_NO_FRAGMENT_STRING_IN_PATH", "路徑和片段不能同時指定片段" }, { "ER_NO_QUERY_STRING_IN_PATH", "在路徑及查詢字串中不可指定查詢字串" }, { "ER_NO_PORT_IF_NO_HOST", "如果沒有指定主機，不可指定連接埠" }, 
        { "ER_NO_USERINFO_IF_NO_HOST", "如果沒有指定主機，不可指定 Userinfo" }, { "ER_XML_VERSION_NOT_SUPPORTED", "警告:  要求的輸出文件版本為 ''{0}''。不支援此版本的 XML。輸出文件的版本將會是 ''1.0''。" }, { "ER_SCHEME_REQUIRED", "必須有配置！" }, { "ER_FACTORY_PROPERTY_MISSING", "傳遞給 SerializerFactory 的 Properties 物件沒有 ''{0}'' 屬性。" }, { "ER_ENCODING_NOT_SUPPORTED", "警告:  Java Runtime 不支援編碼 ''{0}''。" } }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serialize\\utils\SerializerMessages_zh_TW.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */