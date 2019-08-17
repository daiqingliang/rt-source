package com.sun.java.util.jar.pack;

import java.util.ListResourceBundle;

public class DriverResource_zh_CN extends ListResourceBundle {
  public static final String VERSION = "VERSION";
  
  public static final String BAD_ARGUMENT = "BAD_ARGUMENT";
  
  public static final String BAD_OPTION = "BAD_OPTION";
  
  public static final String BAD_REPACK_OUTPUT = "BAD_REPACK_OUTPUT";
  
  public static final String DETECTED_ZIP_COMMENT = "DETECTED_ZIP_COMMENT";
  
  public static final String SKIP_FOR_REPACKED = "SKIP_FOR_REPACKED";
  
  public static final String WRITE_PACK_FILE = "WRITE_PACK_FILE";
  
  public static final String WRITE_PACKGZ_FILE = "WRITE_PACKGZ_FILE";
  
  public static final String SKIP_FOR_MOVE_FAILED = "SKIP_FOR_MOVE_FAILED";
  
  public static final String PACK_HELP = "PACK_HELP";
  
  public static final String UNPACK_HELP = "UNPACK_HELP";
  
  public static final String MORE_INFO = "MORE_INFO";
  
  public static final String DUPLICATE_OPTION = "DUPLICATE_OPTION";
  
  public static final String BAD_SPEC = "BAD_SPEC";
  
  private static final Object[][] resource = { 
      { "VERSION", "{0}版本 {1}" }, { "BAD_ARGUMENT", "错误参数: {0}" }, { "BAD_OPTION", "错误选项: {0}={1}" }, { "BAD_REPACK_OUTPUT", "--repack 输出错误: {0}" }, { "DETECTED_ZIP_COMMENT", "检测到 ZIP 注释: {0}" }, { "SKIP_FOR_REPACKED", "由于已重新打包而跳过: {0}" }, { "WRITE_PACK_FILE", "要写入 *.pack 文件, 请指定 --no-gzip: {0}" }, { "WRITE_PACKGZ_FILE", "要写入 *.pack.gz 文件, 请指定 --gzip: {0}" }, { "SKIP_FOR_MOVE_FAILED", "由于移动失败而跳过重新打包: {0}" }, { "PACK_HELP", { 
          "用法:  pack200 [-opt... | --option=value]... x.pack[.gz] y.jar", "", "打包选项", "  -g, --no-gzip                   输出无格式的 *.pack 文件, 不压缩", "  --gzip                          (默认值) 使用 gzip 对打包进行后处理", "  -G, --strip-debug               打包时删除调试属性", "  -O, --no-keep-file-order        不传输文件排序信息", "  --keep-file-order               (默认值) 保留输入文件排序", "  -S{N}, --segment-limit={N}      输出段限制 (默认值 N=1Mb)", "  -E{N}, --effort={N}             打包效果 (默认值 N=5)", 
          "  -H{h}, --deflate-hint={h}       传输压缩提示: true, false 或 keep (默认值)", "  -m{V}, --modification-time={V}  传输 modtimes: latest 或 keep (默认值)", "  -P{F}, --pass-file={F}          传输未解压缩的给定输入元素", "  -U{a}, --unknown-attribute={a}  未知属性操作: error, strip 或 pass (默认值)", "  -C{N}={L}, --class-attribute={N}={L}  (用户定义的属性)", "  -F{N}={L}, --field-attribute={N}={L}  (用户定义的属性)", "  -M{N}={L}, --method-attribute={N}={L} (用户定义的属性)", "  -D{N}={L}, --code-attribute={N}={L}   (用户定义的属性)", "  -f{F}, --config-file={F}        读取文件 F 的 Pack200.Packer 属性", "  -v, --verbose                   提高程序详细程度", 
          "  -q, --quiet                     将详细程度设置为最低级别", "  -l{F}, --log-file={F}           输出到给定日志文件, 或对于 System.out 指定 '-'", "  -?, -h, --help                  输出此消息", "  -V, --version                   输出程序版本", "  -J{X}                           将选项 X 传递给基础 Java VM", "", "注:", "  -P, -C, -F, -M 和 -D 选项累计。", "  示例属性定义:  -C SourceFile=RUH。", "  Config. 文件属性由 Pack200 API 定义。", 
          "  有关 -S, -E, -H-, -m, -U 值的含义, 请参阅 Pack200 API。", "  布局定义 (例如 RUH) 由 JSR 200 定义。", "", "重新打包模式通过打包/解包周期更新 JAR 文件:", "    pack200 [-r|--repack] [-opt | --option=value]... [repackedy.jar] y.jar\n" } }, 
      { "UNPACK_HELP", { 
          "用法:  unpack200 [-opt... | --option=value]... x.pack[.gz] y.jar\n", "", "解包选项", "  -H{h}, --deflate-hint={h}     覆盖已传输的压缩提示: true, false 或 keep (默认值)", "  -r, --remove-pack-file        解包之后删除输入文件", "  -v, --verbose                   提高程序详细程度", "  -q, --quiet                     将详细程度设置为最低级别", "  -l{F}, --log-file={F}         输出到给定日志文件, 或对于 System.out 指定 '-'", "  -?, -h, --help                输出此消息", "  -V, --version                 输出程序版本", 
          "  -J{X}                         将选项 X 传递给基础 Java VM" } }, { "MORE_INFO", "(有关详细信息, 请运行 {0} --help。)" }, { "DUPLICATE_OPTION", "重复的选项: {0}" }, { "BAD_SPEC", "{0}的规范错误: {1}" } };
  
  protected Object[][] getContents() { return resource; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jav\\util\jar\pack\DriverResource_zh_CN.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */