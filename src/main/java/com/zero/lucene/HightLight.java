package com.zero.lucene;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * 高亮和中文分词
 * @author hhr
 *
 */
public class HightLight {
	// 索引的位置
	public static final String INDEX_DIR = "D:\\lucene";
	// 索引文件的位置
	public static final String DATA_DIR = "D:\\lucene\\lucene";
	
	private Directory dir;
	private IndexReader indexReader;
	private IndexSearcher indexSearcher;
	private IndexWriter indexWriter;
	
	/**
	 * 获取IndexReader对象
	 * @return
	 * @throws Exception
	 */
	private IndexReader getIndexReader() throws Exception {
		dir = FSDirectory.open(Paths.get(INDEX_DIR));
		indexReader = DirectoryReader.open(dir);
		return indexReader;
	}
	
	/**
	 * 获取indexSearcher对象
	 * @return
	 * @throws Exception
	 */
	private IndexSearcher getIndexSearcher() throws Exception {
		indexReader = getIndexReader();
		indexSearcher = new IndexSearcher(indexReader);
		return indexSearcher;
	}
	
	/**
	 * 获取IndexWriter对象
	 * @return
	 * @throws Exception
	 */
	private IndexWriter getIndexWriter() throws Exception {
		dir = FSDirectory.open(Paths.get(INDEX_DIR));
		// 中文分词
		Analyzer analyzer = new SmartChineseAnalyzer();
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		indexWriter = new IndexWriter(dir, iwc);
		return indexWriter;
	}
	
	/**
	 * 关闭流
	 * @throws Exception
	 */
	private void close() throws Exception {
		if (null != indexReader) {
			indexReader.close();
		}
		if (null != indexWriter) {
			indexWriter.close();
		}
	}
	/**
	 * 建立索引
	 * @throws Exception
	 */
	public void index() throws Exception {
		File[] files = new File(DATA_DIR).listFiles();
		indexWriter = getIndexWriter();
		for (File file : files) {
			getIndex(file, indexWriter);
		}
		close();
	}

	/**
	 * 为每个文件创建索引
	 * @param file
	 * @throws IOException 
	 */
	private void getIndex(File file, IndexWriter indexWriter) throws IOException {
		getDocument(file, indexWriter);
	}

	/**
	 * 创建Document文档
	 * @param file
	 * @throws IOException 
	 */
	private void getDocument(File file, IndexWriter indexWriter) throws IOException {
		Document doc = new Document();
		doc.add(new TextField("title", file.getName(), Field.Store.YES));
		doc.add(new TextField("contents", new FileReader(file)));
		indexWriter.addDocument(doc);
	}
	
	public void searcher() throws Exception {
		Analyzer analyzer = new SmartChineseAnalyzer();
		QueryParser parser = new QueryParser("title", analyzer);
		Query query = parser.parse("密码");
		
		indexSearcher = getIndexSearcher();
		TopDocs topDocs = indexSearcher.search(query, 100);
		// 高亮部分
		QueryScorer queryScorer = new QueryScorer(query);
//		Formatter formatter = new SimpleHTMLFormatter(); // 默认关键字是<b></b>
		SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<b><font color='red'>", "</font></b>");
		// 设置高亮
		Highlighter hight = new Highlighter(formatter, queryScorer);
		
		Fragmenter fragmenter = new SimpleSpanFragmenter(queryScorer);
		hight.setTextFragmenter(fragmenter);
		
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			Document doc = indexReader.document(scoreDoc.doc);
			
			String title = doc.get("title");
			if (null != title) {
				// 最高分数片段值
				TokenStream tokenStream = analyzer.tokenStream("title", new StringReader(title));
				// 获取最高分数的片段
				String bestSpan = hight.getBestFragment(tokenStream, title);
				System.out.println(bestSpan);
			} else {
				System.out.println("没有匹配的数据");
			}
		}
		close();
	}
	
	public static void main(String[] args) throws Exception {
		HightLight hightLight = new HightLight();
		//hightLight.index();
		hightLight.searcher();
	}
	
}
