package com.example.demo.service;

import com.example.demo.utils.Constants;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.SimpleTokenizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class WordsFinder {

    public HashMap<String, List<String>> find(String text) throws IOException {
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize(text);

        InputStream inputStreamPOSTagger = getClass()
                .getResourceAsStream("/en-pos-maxent.bin");


        POSModel posModel = new POSModel(inputStreamPOSTagger);
        POSTaggerME posTagger = new POSTaggerME(posModel);
        String[] tags = posTagger.tag(tokens);

        HashMap<String, List<String>> map = new HashMap<>();
        map.put(Constants.adjective, new ArrayList<>());
        map.put(Constants.noun, new ArrayList<>());
        map.put(Constants.nouns, new ArrayList<>());

        for (int i = 0; i < tags.length; i++) {
            String tag = tags[i];
            if (map.containsKey(tag)) {
                String token = tokens[i].toLowerCase(Locale.ROOT);
                if (token.length() < 3) {
                    continue;
                }
                map.get(tag).add(token);
            }
        }
        return map;
    }
}
