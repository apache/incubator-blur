package com.nearinfinity.blur.manager.writer;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.nearinfinity.blur.analysis.BlurAnalyzer;
import com.nearinfinity.blur.thrift.generated.Column;
import com.nearinfinity.blur.thrift.generated.ColumnFamily;
import com.nearinfinity.blur.thrift.generated.Row;

public class BlurIndexWriterTest {
    
    private static final int TEST_NUMBER = 10000;
    private BlurIndexWriter writer;
    private BlurIndexCloser closer;
    private Random random = new Random();
    private BlurIndexRefresher refresher;
    private File dir;

    @Before
    public void setup() throws IOException {
        dir = new File("./tmp/blur-index-writer-test");
        rm(dir);
        dir.mkdirs();
        closer = new BlurIndexCloser();
        closer.init();
        
        BlurAnalyzer analyzer = new BlurAnalyzer(new KeywordAnalyzer());
        
        refresher = new BlurIndexRefresher();
        refresher.init();
        
        writer = new BlurIndexWriter();
        writer.setDirectory(FSDirectory.open(dir));
        writer.setCloser(closer);
        writer.setAnalyzer(analyzer);
        writer.setRefresher(refresher);
        writer.init();   
    }
    
    @After
    public void tearDown() throws IOException {
        writer.close();
        refresher.close();
        closer.close();
        rm(dir);
    }

    private void rm(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                rm(f);
            }
        }
        file.delete();
    }

    @Test
    public void testBlurIndexWriter() throws IOException {
        long s = System.nanoTime();
        int total = 0;
        for (int i = 0; i < TEST_NUMBER; i++) {
            writer.replaceRow(genRow());
            total++;
        }
        long e = System.nanoTime();
        double seconds = (e-s) / 1000000000.0;
        double rate = total / seconds;
        System.out.println("Rate " + rate);
        IndexReader reader = writer.getIndexReader(true);
        assertEquals(TEST_NUMBER,reader.numDocs());
    }

    private Row genRow() {
        Row row = new Row();
        row.setId(Long.toString(random.nextLong()));
        ColumnFamily cf = new ColumnFamily();
        cf.setFamily("testing");
        Map<String, Set<Column>> records = new HashMap<String, Set<Column>>();
        Set<Column> values = new HashSet<Column>();
        for (int i = 0; i < 10; i++) {
            values.add(new Column("col" + i, Arrays.asList(Long.toString(random.nextLong()))));
        }
        records.put(Long.toString(random.nextLong()), values);
        cf.setRecords(records);
        row.addToColumnFamilies(cf);
        return row;
    }

}
