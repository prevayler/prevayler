package org.prevayler.implementation;

import org.prevayler.Clock;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.foundation.FileIOTest;
import org.prevayler.foundation.serialization.*;

import java.io.*;
import java.util.Date;

public class JournalSerializerTest extends FileIOTest {

  public void testConfigureJournalSerializationStrategy() throws Exception {
    Serializer strategy = new MySerializer();

    startAndCrash("MyJournal", strategy);

    assertEquals("6;withQuery=false;systemVersion=1;executionTime=1000002\r\n" +
        " first\r\n" +
        "7;withQuery=false;systemVersion=2;executionTime=1000004\r\n" +
        " second\r\n" +
        "6;withQuery=false;systemVersion=3;executionTime=1000006\r\n" +
        " third\r\n", journalContents("MyJournal"));

    recover("MyJournal", strategy);
  }

  public void testBadSuffix() {
    PrevaylerFactory<Serializable> factory = new PrevaylerFactory<Serializable>();
    try {
      factory.configureJournalSerializer("JOURNAL", new JavaSerializer());
      fail();
    } catch (IllegalArgumentException expected) {
      assertEquals("Journal filename suffix must match /[a-zA-Z0-9]*[Jj]ournal/, but 'JOURNAL' does not", expected.getMessage());
    }
  }

  public void testTryToConfigureTwo() {
    PrevaylerFactory<Serializable> factory = new PrevaylerFactory<Serializable>();
    factory.configureJournalSerializer("journal", new JavaSerializer());
    try {
      factory.configureJournalSerializer("newjournal", new JavaSerializer());
      fail();
    } catch (IllegalStateException expected) {
    }
  }

  public void testJavaJournal() throws Exception {
    Serializer strategy = new JavaSerializer();

    startAndCrash("journal", strategy);

    assertEquals("69;withQuery=false;systemVersion=1;executionTime=1000002\r\n" +
        "\254\355\0\005sr\0.org.prevayler.implementation.AppendTransaction\312\330`~\232\305\204\035\002\0\001L\0\005toAddt\0\022Ljava/lang/String;xpt\0\006 first\r\n" +
        "6A;withQuery=false;systemVersion=2;executionTime=1000004\r\n" +
        "\254\355\0\005sr\0.org.prevayler.implementation.AppendTransaction\312\330`~\232\305\204\035\002\0\001L\0\005toAddt\0\022Ljava/lang/String;xpt\0\007 second\r\n" +
        "69;withQuery=false;systemVersion=3;executionTime=1000006\r\n" +
        "\254\355\0\005sr\0.org.prevayler.implementation.AppendTransaction\312\330`~\232\305\204\035\002\0\001L\0\005toAddt\0\022Ljava/lang/String;xpt\0\006 third\r\n",
        journalContents("journal"));

    recover("journal", strategy);
  }

  public void testXStreamJournal() throws Exception {
    Serializer strategy = new XStreamSerializer();

    startAndCrash("journal", strategy);

    assertEquals("7A;withQuery=false;systemVersion=1;executionTime=1000002\r\n" +
        "<org.prevayler.implementation.AppendTransaction>\n  <toAdd> first</toAdd>\n</org.prevayler.implementation.AppendTransaction>\r\n" +
        "7B;withQuery=false;systemVersion=2;executionTime=1000004\r\n" +
        "<org.prevayler.implementation.AppendTransaction>\n  <toAdd> second</toAdd>\n</org.prevayler.implementation.AppendTransaction>\r\n" +
        "7A;withQuery=false;systemVersion=3;executionTime=1000006\r\n" +
        "<org.prevayler.implementation.AppendTransaction>\n  <toAdd> third</toAdd>\n</org.prevayler.implementation.AppendTransaction>\r\n",
        journalContents("journal"));

    recover("journal", strategy);
  }

  public void testCompressedJournal() throws Exception {
    GZIPSerializer serializer = new GZIPSerializer(new MySerializer());

    startAndCrash("journal", serializer);

    assertEquals("1A;withQuery=false;systemVersion=1;executionTime=1000002\r\n" +
        "\037\213\b\0\0\0\0\0\0\0SH\313,*.\001\0\337\275=\342\006\0\0\0\r\n" +
        "1B;withQuery=false;systemVersion=2;executionTime=1000004\r\n" +
        "\037\213\b\0\0\0\0\0\0\0S(NM\316\317K\001\0(V\fU\007\0\0\0\r\n" +
        "1A;withQuery=false;systemVersion=3;executionTime=1000006\r\n" +
        "\037\213\b\0\0\0\0\0\0\0S(\311\310,J\001\0\354s~T\006\0\0\0\r\n",
        journalContents("journal"));

    recover("journal", serializer);
  }

  public void testCompressedAndEncryptedJournal() throws Exception {
    byte[] key = {35, 24, 45, 123, 86, 36, 21, 1};
    JavaSerializer java = new JavaSerializer();
    GZIPSerializer gzip = new GZIPSerializer(java);
    DESSerializer des = new DESSerializer(gzip, key);

    startAndCrash("journal", des);

    assertEquals("80;withQuery=false;systemVersion=1;executionTime=1000002\r\n" +
        "E+\315\256D\023p\241\271hC\n\025\313n\340%\306:\273T\022\365\002\312/\2529'\247\224\220\217\367\006\306\353\n\363\004o\b\337\032\320\207\255\265\007\032\213\177_Wg\360X\366\214\316\276\366 \341!~!\035Y*\3473OB\007\"hmDT_\347\337\354\337\360\211\226\004IV.N\205\204\246\001k8K\007\205W\236\312EI\336\360\037w\177\255\034\035me3\311\374\223\212\377\256\021\f\367\272\r\n" +
        "80;withQuery=false;systemVersion=2;executionTime=1000004\r\n" +
        "E+\315\256D\023p\241\271hC\n\025\313n\340%\306:\273T\022\365\002\312/\2529'\247\224\220\217\367\006\306\353\n\363\004o\b\337\032\320\207\255\265\007\032\213\177_Wg\360X\366\214\316\276\366 \341!~!\035Y*\3473OB\007\"hmDT_\347\337\354\337\360\211\226\004IV.N\205\204\246\001k8K\007\205W\236\346\374\017a\205C|\272\032\237\226\307\26662h2\262\277\232\022B\022\377\r\n" +
        "80;withQuery=false;systemVersion=3;executionTime=1000006\r\n" +
        "E+\315\256D\023p\241\271hC\n\025\313n\340%\306:\273T\022\365\002\312/\2529'\247\224\220\217\367\006\306\353\n\363\004o\b\337\032\320\207\255\265\007\032\213\177_Wg\360X\366\214\316\276\366 \341!~!\035Y*\3473OB\007\"hmDT_\347\337\354\337\360\211\226\004IV.N\205\204\246\001k8K\007\205W\236+\026\216\030\322O\271\3627\360\020{\\5\031\201\223\212\377\256\021\f\367\272\r\n",
        journalContents("journal"));

    recover("journal", des);
  }

  public void testTripleDES() throws Exception {
    byte[] key = {35, 24, 45, 123, 86, 36, 21, 1, 54, 45, 6, 123, 34, 57, 34, 75, 12, 32, 4, 7, 23, 78, 97, 4};
    JavaSerializer java = new JavaSerializer();
    GZIPSerializer gzip = new GZIPSerializer(java);
    DESSerializer des = new DESSerializer(gzip, key);

    startAndCrash("journal", des);

    assertEquals("80;withQuery=false;systemVersion=1;executionTime=1000002\r\n" +
        "\231\377\030\0C\265w\3245\317\362\336\324\3100\214'\021\277\215\264\310\207\f\351\nK\214\223\320\367\242^\326\314\206L#\255\224\236\377Q\223\233>\207\321\267\355\375\235K;\300m\0\277\207\021\344?,o\307S\211\321)\370e\356\263\2665\365,c\356\356\371L\325\306g\376\222\004\013{R\t\371ln3\305\200Oi\200\324\340\264\255\201z1\315(2\331k\257\211\213F\233\036\371:Ui\372\205\3027[\r\n" +
        "80;withQuery=false;systemVersion=2;executionTime=1000004\r\n" +
        "\231\377\030\0C\265w\3245\317\362\336\324\3100\214'\021\277\215\264\310\207\f\351\nK\214\223\320\367\242^\326\314\206L#\255\224\236\377Q\223\233>\207\321\267\355\375\235K;\300m\0\277\207\021\344?,o\307S\211\321)\370e\356\263\2665\365,c\356\356\371L\325\306g\376\222\004\013{R\t\371ln3\305\200Oi\200\324\340\264\375\035\250\351\310\3326\362)\203\2300\206\372\203<kD\025\217d\035w\004\r\n" +
        "80;withQuery=false;systemVersion=3;executionTime=1000006\r\n" +
        "\231\377\030\0C\265w\3245\317\362\336\324\3100\214'\021\277\215\264\310\207\f\351\nK\214\223\320\367\242^\326\314\206L#\255\224\236\377Q\223\233>\207\321\267\355\375\235K;\300m\0\277\207\021\344?,o\307S\211\321)\370e\356\263\2665\365,c\356\356\371L\325\306g\376\222\004\013{R\t\371ln3\305\200Oi\200\324\340\2640\233\205\366/\322\375\247`\031\356'xa{\311:Ui\372\205\3027[\r\n",
        journalContents("journal"));
    
    recover("journal", des);
  }

  private void startAndCrash(String suffix, Serializer journalSerializer)
      throws Exception {
    Prevayler<StringBuffer> prevayler = createPrevayler(suffix, journalSerializer);

    prevayler.execute(new AppendTransaction(" first"));
    prevayler.execute(new AppendTransaction(" second"));
    prevayler.execute(new AppendTransaction(" third"));
    assertEquals("the system first second third", prevayler.prevalentSystem().toString());
    prevayler.close();
  }

  private void recover(String suffix, Serializer journalSerializer)
      throws Exception {
    Prevayler<StringBuffer> prevayler = createPrevayler(suffix, journalSerializer);
    assertEquals("the system first second third", prevayler.prevalentSystem().toString());
  }

  private Prevayler<StringBuffer> createPrevayler(String suffix, Serializer journalSerializer)
      throws Exception {
    PrevaylerFactory<StringBuffer> factory = new PrevaylerFactory<StringBuffer>();
    factory.configurePrevalentSystem(new StringBuffer("the system"));
    factory.configurePrevalenceDirectory(_testDirectory);
    factory.configureJournalSerializer(suffix, journalSerializer);
    factory.configureClock(new Clock() {
      private long time = 1000000;

      public Date time() {
        return new Date(++time);
      }
    });
    return factory.create();
  }

  private static class MySerializer implements Serializer {

    public void writeObject(OutputStream stream, Object object) throws IOException {
      Writer writer = new OutputStreamWriter(stream, "UTF-8");
      AppendTransaction transaction = (AppendTransaction) object;
      writer.write(transaction.toAdd);
      writer.flush();
    }

    public Object readObject(InputStream stream) throws IOException, ClassNotFoundException {
      BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
      return new AppendTransaction(reader.readLine());
    }

  }

}
