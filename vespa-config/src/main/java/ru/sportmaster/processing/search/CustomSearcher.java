package ru.sportmaster.processing.search;

import com.yahoo.search.Query;
import com.yahoo.search.Result;
import com.yahoo.search.Searcher;
import com.yahoo.search.searchchain.Execution;
import lombok.val;

public class CustomSearcher extends Searcher {

    @Override
    public Result search(Query query, Execution execution) {
        System.out.println("------------- Start Customer Searcher -------------");
        val result = execution.search(query);
        val totalHitCount = result.getTotalHitCount();
        System.out.println("Total Hit Count: " + totalHitCount);
        System.out.println("------------- End Customer Searcher -------------");
        return result;
    }
}