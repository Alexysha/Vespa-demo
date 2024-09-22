package ru.sportmaster.processing.document;

import com.yahoo.docproc.DocumentProcessor;
import com.yahoo.docproc.Processing;
import com.yahoo.document.DocumentPut;
import com.yahoo.document.DocumentRemove;
import com.yahoo.document.DocumentUpdate;
import lombok.val;

public class CustomProcessing extends DocumentProcessor {

    @Override
    public Progress process(Processing processing) {
        System.out.println("------------- Start CustomProcessing -------------");
        val operations = processing.getDocumentOperations();
        for (val operation : operations) {
            val id = operation.getId();
            if (operation instanceof DocumentPut) {
                System.out.printf("Try save document with id [%s]%n", id);
            } else if (operation instanceof DocumentRemove) {
                System.out.printf("Try remove document with id [%s]%n", id);
            } else if (operation instanceof DocumentUpdate) {
                System.out.printf("Try update document with id [%s]%n", id);
            }
        }
        System.out.println("------------- End CustomProcessing -------------");
        return Progress.DONE;
    }
}