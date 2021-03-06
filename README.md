# Lucene-Search-App
A implementation of search Engine based on Apache Lucene

## Running Environment

```
Linux 4.4.0
Maven 3.3.9
Java 1.8
gcc 5.4.0
Lucene 8.4.1
```

## Running App
- 1. Download this repo</br>
- 2. Build project
```shell
cd Lucen-Search-App/LuceneSearch
```
```shell
mvn clean
```
```shell
mvn package
```
- 3. Run Index 
```shell
java -cp target/LuceneSearch-1.0-SNAPSHOT.jar com.luceneSearch.mark.IndexFiles -file ../cran/cran.all.1400
```
- 4. Run Search
```shell
java -cp target/LuceneSearch-1.0-SNAPSHOT.jar com.luceneSearch.mark.SearchIndex -query ../cran/cran.qry -model 2 -searchMode 2
```
(-model 1 represents VSM; -model 2 represents BM25
 -searchMode 1 represents return the top 50 results; -searchMode 2 represents return all the mathced results)</br> 
 
 
- 5. Evaluation</br>
```shell
cd ..
```
```shell
cd trec_eval-9.0.7/
```
```shell
./trec_eval QRelsCorrectedforTRECeval query-result-boost.txt
```
- 6. Connect to linux
```shell
ssh -i /path/my-key-pair.pem ubuntu@ec2-107-20-117-245.compute-1.amazonaws.com
```
