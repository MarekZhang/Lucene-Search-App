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
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.FSDirectory;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class SearchIndex {
    private static String indexPath = "../index";
    private static String queryPath = "/Users/zhangbowen/Programming/Project/LuceneApp/cran/cran.qry";
    private static String outputPath = "/Users/zhangbowen/Programming/Project/LuceneApp/trec_eval-9.0.7/output-boost.txt";
    private static int scoringApproach = 1;
    private static int hitsPerPage = 10;
    private static int searchMode = 1;


    public static void main(String[] args) throws Exception{
        /** The default index folder is ../index; the default scoring approach is 1 - VSM
         *  The default query path is "/Users/zhangbowen/Programming/Project/LuceneApp/cran/cran.qry"
         *  The user can manually set the index folder/ query path by giving -index param in the terminal
         *  The scoring approach can also be changed to BM25 by giving the param -model 2
         *  The scoring approach can also be changed to TF-IDF by giving the param -model 3
         *  The searchMode represents return 50 pieces of results(default), or all the matched results(-searchMode 2)
         * */
        for(int i = 0; i < args.length; i++){
            if(args[i].equals("-query"))
                queryPath = args[i+1];
            else if(args.equals("-model"))
                scoringApproach = Integer.parseInt(args[i+1]);
            else if(args.equals("-searchMode"))
                searchMode = Integer.parseInt(args[i+1]);
        }
        File queryFolder = new File(indexPath);
        if(!queryFolder.exists())
            throw new IOException("the index folder is invalid");

        DirectoryReader DirReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        IndexSearcher indexSearcher = new IndexSearcher(DirReader);
        Analyzer analyzer = new StandardAnalyzer();
        BufferedReader bufferedReader = null;
        PrintWriter writer = new PrintWriter(outputPath,"UTF-8");
        int QueryNumber = 1;

        switch(scoringApproach){
            case 1:
                indexSearcher.setSimilarity(new ClassicSimilarity());
            case 2:
                indexSearcher.setSimilarity(new BM25Similarity());
//            case 3:
//                indexSearcher.setSimilarity(new TFIDFSimilarity() {
//                    @Override
//                    public float tf(float freq) { return 0; }
//
//                    @Override
//                    public float idf(long docFreq, long docCount) { return 0; }
//
//                    @Override
//                    public float lengthNorm(int length) { return 0; }
//
//                    @Override
//                    public float sloppyFreq(int distance) { return 0; }
//
//                    @Override
//                    public float scorePayload(int doc, int start, int end, BytesRef payload) { return 0; }
//                });
        }

        if( queryPath != null){
            bufferedReader = Files.newBufferedReader(Paths.get(queryPath), StandardCharsets.UTF_8);
        }else{
            bufferedReader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        }
//        HashMap<String, Float> boostedScores = new HashMap<String, Float>();
//        boostedScores.put("TITLE", 0.5f);
//        boostedScores.put("WRITING", 0.5f);
//        boostedScores.put("AUTHOR", 0.01f);
//        boostedScores.put("BACKGROUND", 0.01f);
        QueryParser parser = new MultiFieldQueryParser(new String[]{"TITLE",  "WRITING"},analyzer);
        String line = bufferedReader.readLine();
        while(true){
            String QueryString = "";//initiate one Query String for each query
            if(line == null)
                break;
            line = line.trim();
            if(line.length() == 0)//no query content except spaces
                break;

            if(line.matches("(\\.I)( )(\\d+)"))
                line = bufferedReader.readLine();
            if(line.matches("\\.W"));
                line = bufferedReader.readLine();
            while(! line.matches("(\\.I)( )(\\d+)")){
                QueryString = QueryString + " " + line;
                line = bufferedReader.readLine();
                if(line == null)
                    break;
            }

            Query query = parser.parse(QueryParser.escape(QueryString.trim()));
//            System.out.println(query);
            long StartTime = System.currentTimeMillis();
            doPagingSearch(bufferedReader, indexSearcher, query, hitsPerPage, writer, QueryNumber);
            long EndTime = System.currentTimeMillis();
            System.out.println((EndTime - StartTime) + "MillSeconds cost.  " + " Number " + QueryNumber );
            QueryNumber++;

        }
        writer.close();
        DirReader.close();

    }

    public static void doPagingSearch(BufferedReader in, IndexSearcher searcher, Query query,
                                      int hitsPerPage, PrintWriter writer, int queryNumber) throws IOException{
        int hitsNum = 0;
        TopDocs results = searcher.search(query, hitsPerPage * 5);
        if(searchMode == 1)
            hitsNum = hitsPerPage * 5;
        if(searchMode == 2){
            int numTotalHits = Math.toIntExact(results.totalHits.value);
            results = searcher.search(query, numTotalHits);
            hitsNum = numTotalHits;
        }
        ScoreDoc[] hits = results.scoreDocs;
        System.out.println(hitsNum + " total matching documents");
        int start = 0;
//        int end = Math.min(numTotalHits, hitsPerPage);
//
//        end = Math.min(hits.length, start + hitsPerPage);
        for (int i = start; i < hitsNum; i++) {
            Document doc = searcher.doc(hits[i].doc);
            String ID = doc.get("ID");
            if (ID != null) {
                System.out.println(queryNumber + " [" + ID + "] " + (i+1) + " " + hits[i].score);
                writer.println(queryNumber+" 0 " + ID.replace(".I ","") + " "
                               + (i+1) + " " + hits[i].score +" STANDARD");
            }
        }
    }
}