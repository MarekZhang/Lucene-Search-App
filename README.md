# Lucene-Search-App
A implementation of search Engine based on Apache Lucene

## Running Environment

```
MacOS Catalina 10.15.3
Maven 3.6.3
Java 8
Lucene 8.4.1
```

## Running App
- 1. Download this repo</br>
- 2. Run the command below
```shell
cd {HOME}/Lucen-Search-App/LuceneSearch
```
- 3. Run Index 
```shell
java -cp target/LuceneSearch-1.0-SNAPSHOT.jar com.luceneSearch.mark.IndexFiles -file ../cran/cran.all.1400
```
- 4. Run Search
```shell
java -cp target/LuceneSearch-1.0-SNAPSHOT.jar com.luceneSearch.mark.SearchIndex -query ../cran/cran.qry -model 1 -searchMode 2
```
(-model 1 represents VSM; -model 2 represents BM25
 -searchMode 1 represents return the top 50 results; -searchMode 2 represents return all the mathced results)</br> 
 
 
- 5. Evaluation</br>
cd to the trec_eval-9.0.7 folder and run the command below
```shell
./trec_eval QRelsCorrectedforTRECeval query-result-boost.txt
```
- 6. Connect to linux
```shell
ssh -i /path/my-key-pair.pem ubuntu@ec2-107-20-117-245.compute-1.amazonaws.com
```
