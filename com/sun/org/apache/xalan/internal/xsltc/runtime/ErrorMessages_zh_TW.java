package com.sun.org.apache.xalan.internal.xsltc.runtime;

import java.util.ListResourceBundle;

public class ErrorMessages_zh_TW extends ListResourceBundle {
  public Object[][] getContents() { return new Object[][] { 
        { "RUN_TIME_INTERNAL_ERR", "''{0}'' 中的執行階段內部錯誤" }, { "RUN_TIME_COPY_ERR", "執行 <xsl:copy> 時發生執行階段錯誤" }, { "DATA_CONVERSION_ERR", "從 ''{0}'' 至 ''{1}'' 的轉換無效。" }, { "EXTERNAL_FUNC_ERR", "XSLTC 不支援外部函數 ''{0}''。" }, { "EQUALITY_EXPR_ERR", "相等性表示式中的引數類型不明。" }, { "INVALID_ARGUMENT_ERR", "呼叫 ''{1}'' 中的引數類型 ''{0}'' 無效" }, { "FORMAT_NUMBER_ERR", "嘗試使用樣式 ''{1}'' 格式化數字 ''{0}''。" }, { "ITERATOR_CLONE_ERR", "無法複製重複程式 ''{0}''。" }, { "AXIS_SUPPORT_ERR", "不支援軸 ''{0}'' 的重複程式。" }, { "TYPED_AXIS_SUPPORT_ERR", "不支援類型軸 ''{0}'' 的重複程式。" }, 
        { "STRAY_ATTRIBUTE_ERR", "屬性 ''{0}'' 在元素之外。" }, { "STRAY_NAMESPACE_ERR", "命名空間宣告 ''{0}''=''{1}'' 超出元素外。" }, { "NAMESPACE_PREFIX_ERR", "字首 ''{0}'' 的命名空間尚未宣告。" }, { "DOM_ADAPTER_INIT_ERR", "使用錯誤的來源 DOM 類型建立 DOMAdapter。" }, { "PARSER_DTD_SUPPORT_ERR", "您正在使用的 SAX 剖析器不會處理 DTD 宣告事件。" }, { "NAMESPACES_SUPPORT_ERR", "您正在使用的 SAX 剖析器不支援 XML 命名空間。" }, { "CANT_RESOLVE_RELATIVE_URI_ERR", "無法解析 URI 參照 ''{0}''。" }, { "UNSUPPORTED_XSL_ERR", "不支援的 XSL 元素 ''{0}''" }, { "UNSUPPORTED_EXT_ERR", "無法辨識的 XSLTC 擴充套件 ''{0}''" }, { "UNKNOWN_TRANSLET_VERSION_ERR", "建立指定 translet ''{0}'' 的 XSLTC 版本比使用中 XSLTC 執行階段的版本較新。您必須重新編譯樣式表，或使用較新的 XSLTC 版本來執行此 translet。" }, 
        { "INVALID_QNAME_ERR", "值必須為 QName 的屬性，具有值 ''{0}''" }, { "INVALID_NCNAME_ERR", "值必須為 NCName 的屬性，具有值 ''{0}''" }, { "UNALLOWED_EXTENSION_FUNCTION_ERR", "當安全處理功能設為真時，不允許使用擴充套件函數 ''{0}''。" }, { "UNALLOWED_EXTENSION_ELEMENT_ERR", "當安全處理功能設為真時，不允許使用擴充套件元素 ''{0}''。" } }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\runtime\ErrorMessages_zh_TW.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */