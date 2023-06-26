package com.example.demo.myide.domain.entity;

import com.example.demo.myide.domain.entity.Report.BadReport;
import com.example.demo.myide.domain.entity.Report.GoodReport;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
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
import org.apache.lucene.store.Directory;

import org.apache.lucene.document.Document;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LuceneFileSearch {

    public LuceneFileSearch(Directory directory, StandardAnalyzer analyzer)
    {
        super();
        indexDirectory_ = directory;
        analyzer_ = analyzer;

    }


    public Feature.ExecutionReport addFileToIndex(Path filepath)
    {
        try
        {
            Path path = filepath;
            File file = path.toFile();
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer_);
            IndexWriter indexWriter = new IndexWriter(indexDirectory_,indexWriterConfig);
            Document document = new Document();
            FileReader fileReader = new FileReader(file);
            document.add(new TextField("contents",fileReader));
            document.add(new StringField("path",file.getPath(), Field.Store.YES));
            document.add(new StringField("filename",file.getName(),Field.Store.YES));

            indexWriter.addDocument(document);
            indexWriter.close();


        }
        catch (Exception e)
        {
            return new BadReport();
        }
        return new GoodReport();
    }

    public void recurseaddFileToIndex(Node root)
    {
        if (root.isFolder()) {
            for (Node child : root.getChildren()) {
                recurseaddFileToIndex(child);
            }
        }
        else
        {
            addFileToIndex(root.getPath());
        }

    }

    public boolean searchInFiles(String Field, String stringquery)
    {
        try
        {
            Query query = new QueryParser(Field,analyzer_).parse(stringquery);
            IndexReader indexReader = DirectoryReader.open(indexDirectory_);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            TopDocs topDocs = indexSearcher.search(query,10);
            List<Document> documents = new ArrayList<>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs)
            {
                documents.add(indexSearcher.doc(scoreDoc.doc));
            }
            return documents.size() > 0;
        }
        catch (Exception e)
        {
        }
        return false;
    }


    private Directory indexDirectory_;
    private StandardAnalyzer analyzer_;

}