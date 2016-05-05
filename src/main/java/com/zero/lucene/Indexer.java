package com.zero.lucene;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

/**
 * 建立索引
 * @author hhr
 *
 */
public class Indexer {
	private IndexWriter writer;  // 写索引实例
	
	/**
	 * 写入索引
	 * @param indexDir		将示例写入到某个文件的路径
	 * @throws Exception
	 */
	public Indexer(String indexDir) throws Exception {
		FSDirectory dir = FSDirectory.open(Paths.get(indexDir));
		// 英文标准分词
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		writer = new IndexWriter(dir, config);
	}
	
	/**
	 * 关闭写入流
	 * @throws Exception
	 */
	public void close() throws Exception {
		writer.close();
	}
	
	/**
	 * 建立索引
	 * @param dataDir		被索引的数据源
	 * @throws Exception
	 */
	public void index(String dataDir) throws Exception {
		File[] files = new File(dataDir).listFiles();
		for (File file : files) {
			indexFile(file);
		}
		// 建立了多少个索引文件
		System.out.println("建立索引文件的数目有: " + writer.numDocs());
	}

	/**
	 * 为每个文件建立索引
	 * @param file
	 * @throws Exception
	 */
	private void indexFile(File file) throws Exception {
		getDocument(file);
	}

	/**
	 * 为每个文件获取文档
	 * @param file
	 * @throws IOException 
	 */
	private void getDocument(File file) throws IOException {
		Document doc = new Document();
		doc.add(new TextField("title", file.getName(), Store.YES));
		doc.add(new TextField("contents", new FileReader(file)));
		writer.addDocument(doc);
	}
	
	public static void main(String[] args) {
		String indexDir = "D://lucene";
		String dataDir = "D://lucene//lucene";
		Indexer indexer = null;
		try {
			indexer = new Indexer(indexDir);
			indexer.index(dataDir);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				indexer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
