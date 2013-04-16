package org.wltea.analyzer.core;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Created with IntelliJ IDEA.
 * User: tcz
 * Date: 13-4-16
 * Time: 下午3:03
 */
public class IKSegmenterTest {
    IKSegmenter ikSegmenter;

    public void setUp() {
        String s = "由于分词器没有处理歧义分词的能力,才使用了IKQueryParser来解决搜索时的歧义冲突问题";
        Reader reader = new StringReader(s);
        ikSegmenter = new IKSegmenter(reader, false);
    }

    public static void main(String args[]) throws IOException {
        IKSegmenterTest ikSegmenterTest = new IKSegmenterTest();
        ikSegmenterTest.setUp();
        Lexeme s = ikSegmenterTest.ikSegmenter.next();
        while (s != null) {
            System.out.println(s);
            s = ikSegmenterTest.ikSegmenter.next();
        }
    }
}
