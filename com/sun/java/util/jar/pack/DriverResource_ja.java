package com.sun.java.util.jar.pack;

import java.util.ListResourceBundle;

public class DriverResource_ja extends ListResourceBundle {
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
      { "VERSION", "{0}バージョン{1}" }, { "BAD_ARGUMENT", "無効な引数: {0}" }, { "BAD_OPTION", "無効なオプション: {0}={1}" }, { "BAD_REPACK_OUTPUT", "無効な--repack出力: {0}" }, { "DETECTED_ZIP_COMMENT", "検出されたZIPコメント: {0}" }, { "SKIP_FOR_REPACKED", "すでに再圧縮されているためスキップしています: {0}" }, { "WRITE_PACK_FILE", "*.packファイルを書き込むには、--no-gzipを指定します: {0}" }, { "WRITE_PACKGZ_FILE", "*.pack.gzファイルを書き込むには、--gzipを指定します: {0}" }, { "SKIP_FOR_MOVE_FAILED", "移動が失敗したため解凍をスキップしています: {0}" }, { "PACK_HELP", { 
          "使用方法:  pack200 [-opt... | --option=value]... x.pack[.gz] y.jar", "", "圧縮オプション", "  -g、--no-gzip                   圧縮せずにプレーンな*.packファイルを出力します", "  --gzip                          (デフォルト) pack出力をgzipで後処理します", "  -G、--strip-debug               圧縮中にデバッグ属性を削除します", "  -O、--no-keep-file-order        ファイルの順序付け情報を転送しません", "  --keep-file-order               (デフォルト)入力ファイルの順序付けを保持します", "  -S{N}、--segment-limit={N}       セグメント制限を出力します(デフォルトN=1Mb)", "  -E{N}、--effort={N}             圧縮の試行(デフォルトN=5)", 
          "  -H{h}、--deflate-hint={h}       デフレート・ヒントを転送します: true、falseまたはkeep(デフォルト)", "  -m{V}、--modification-time={V}  変更時間を転送します: latestまたはkeep(デフォルト)", "  -P{F}、--pass-file={F}          指定された圧縮されていない入力要素を転送します", "  -U{a}、--unknown-attribute={a}  不明の属性アクション: error、stripまたはpass(デフォルト)", "  -C{N}={L}、--class-attribute={N}={L}  (ユーザー定義属性)", "  -F{N}={L}、--field-attribute={N}={L}  (ユーザー定義属性)", "  -M{N}={L}、--method-attribute={N}={L} (ユーザー定義属性)", "  -D{N}={L}、--code-attribute={N}={L}   (ユーザー定義属性)", "  -f{F}、--config-file={F}        Pack200.PackerプロパティにファイルFを読み込みます", "  -v、--verbose                   プログラムの冗長性を高めます", 
          "  -q、--quiet                     冗長性を最低レベルに設定します", "  -l{F}、--log-file={F}           指定のログ・ファイルまたはSystem.out ('-'の場合)に出力します", "  -?、-h、--help                  このメッセージを出力します", "  -V、--version                   プログラムのバージョンを出力します", "  -J{X}                           オプションXを基礎となるJava VMに渡します", "", "注:", "  -P、-C、-F、-Mおよび-Dオプションは累積されます。", "  属性定義の例:  -C SourceFile=RUH .", "  Config.ファイル・プロパティは、Pack200 APIによって定義されます。", 
          "  -S、-E、-H、-m、-Uの値の意味は、Pack200 APIを参照してください。", "  レイアウト定義(RUHなど)はJSR 200によって定義されます。", "", "再圧縮モードでは、JARファイルが圧縮/解凍サイクルで更新されます:", "    pack200 [-r|--repack] [-opt | --option=value]... [repackedy.jar] y.jar\n" } }, 
      { "UNPACK_HELP", { 
          "使用方法:  unpack200 [-opt... | --option=value]... x.pack[.gz] y.jar\n", "", "解凍オプション", "  -H{h}、--deflate-hint={h}     転送されたデフレート・ヒントをオーバーライドします: true、falseまたはkeep(デフォルト)", "  -r、--remove-pack-file        解凍後に入力ファイルを削除します", "  -v、--verbose                 プログラムの冗長性を高めます", "  -q、--quiet                   冗長性を最低レベルに設定します", "  -l{F}、--log-file={F}         指定のログ・ファイルまたはSystem.out ('-'の場合)に出力します", "  -?、-h、--help                このメッセージを出力します", "  -V、--version                 プログラムのバージョンを出力します", 
          "  -J{X}                         オプションXを基礎となるJava VMに渡します" } }, { "MORE_INFO", "(詳細は、{0} --helpを実行してください。)" }, { "DUPLICATE_OPTION", "重複オプション: {0}" }, { "BAD_SPEC", "{0}の無効な仕様: {1}" } };
  
  protected Object[][] getContents() { return resource; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jav\\util\jar\pack\DriverResource_ja.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */