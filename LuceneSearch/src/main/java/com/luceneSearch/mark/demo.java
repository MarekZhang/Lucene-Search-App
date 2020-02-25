package com.luceneSearch.mark;

import org.apache.lucene.queryparser.classic.QueryParser;

public class demo {

    public static void main(String[] args){
        String str = "\\abc+cde?ffa";
        String esStr = QueryParser.escape(str);
        System.out.println(esStr);
    }
}
