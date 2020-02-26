/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.luceneSearch.mark;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.InputStream;


public class IndexFiles {
    private static String FILE_DIR = "/Users/zhangbowen/Programming/Project/LuceneApp/cran/cran.all.1400";
    private static String INDEX_DIR = "../index";
    private IndexFiles(){}

    public static void main(String[] args) throws IOException {

/**      reading args from terminal; the default file folder is
 *       "/Users/zhangbowen/Programming/Project/cran/cran.all.1400"
 *       the user can manually change the dir in terminal by giving the para -file.
 */
        for(int i = 0; i < args.length; i++){
            if(args[i].equals("-file"))
                FILE_DIR = args[++i];
        }

        if(FILE_DIR == null)
            throw new IOException("File path must be given");

        Path filePath = Paths.get(FILE_DIR);

        if(!Files.isReadable(filePath))
            throw new IOException("The given file path is not valid or the file is not readable");

        Analyzer analyzer = new StandardAnalyzer();
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIR));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(directory, config);
        //calculate the execution time
        long startTime = System.currentTimeMillis();

        //add documents to Index
        System.out.println("Creating Index, please wait...");
        addIndex(writer, filePath);

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Execution time in MillSeconds:" + executionTime);

        writer.close();
        directory.close();
    }

    /**
     * Adding the terms, docs into the index through the given path
     * @param  writer  an absolute URL giving the base location of the image
     * @param  filePath the location of the image, relative to the url argument
     */
    private static void addIndex(IndexWriter writer, Path filePath){
        try(InputStream stream = Files.newInputStream(filePath)){
            Document doc;
            String TermType = "";
            //Efficient reading files with BufferReader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line = bufferedReader.readLine();
            System.out.println("adding " + filePath +" to the Index, please wait...");
            while(line != null){
                boolean isID = line.matches("(\\.I)( )(\\d+)");
                if(isID){
                    doc = new Document();
                    //use StringField to Store the ID of separated articles, StringField would not be tokenized
                    Field articleID = new StringField("ID", line, Field.Store.YES);
                    doc.add(articleID);
                    //read next line;
                    line = bufferedReader.readLine();
                    isID = line.matches("(\\.I)( )(\\d+)");
                    while(!isID){
                        if(line.matches("\\.T"))
                            TermType = "TITLE";
                        else if(line.matches("\\.A"))
                            TermType = "AUTHOR";
                        else if(line.matches("\\.B"))
                            TermType = "BACKGROUND";
                        else if(line.matches("\\.W"))
                            TermType = "WRITING";
                        else// the current line is String content
                            doc.add(new TextField(TermType, line, Field.Store.YES));

                        //read next line(TermType or ID);
                        line = bufferedReader.readLine();
                        if(line == null)
                            break;
                        isID = line.matches("(\\.I)( )(\\d+)");

                    }
                    writer.addDocument(doc);
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
