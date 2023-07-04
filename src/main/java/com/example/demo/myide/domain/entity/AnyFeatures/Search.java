package com.example.demo.myide.domain.entity.AnyFeatures;

import com.example.demo.MyIde;
import com.example.demo.myide.domain.entity.*;
import com.example.demo.myide.domain.entity.Report.BadReport;
import com.example.demo.myide.domain.entity.Report.GoodReport;
import com.example.demo.myide.domain.entity.aspect.Any;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.util.List;

public class Search extends Any implements Feature {
    @Override
    //fuction gitadd

    public ExecutionReport execute(Project project, Object... params) {
        try
        {
            ProjectClass project_class = (ProjectClass) project;
            project_class.deleteDir(MyIde.configuration_.indexFile().toFile());
            Directory directory = FSDirectory.open(MyIde.configuration_.indexFile());
            LuceneFileSearch luceneFileSearch = new LuceneFileSearch(directory,new StandardAnalyzer());
            if (project_class.getRootNode().isFolder()) {
                luceneFileSearch.recurseaddFileToIndex(project_class.getRootNode());
            }
            else
            {
                luceneFileSearch.addFileToIndex(project_class.getRootNode().getPath());
            }
            List<Document> docs = luceneFileSearch.searchInFiles("contents",params[0].toString());
            return new GoodReport<List<Document>>(docs);
        }
        catch (Exception e)
        {
            return new BadReport();
        }
        //return new BadReport();
    }

    @Override
    public Feature.Type type() {
        return Mandatory.Features.Any.SEARCH;
    }

}
