package com.example.demo.service;

import com.example.demo.utils.Constants;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.SimpleTokenizer;

public class WordsFinder {

  private final SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
  private final POSTaggerME posTagger;

  public WordsFinder() {
    InputStream inputStreamPOSTagger = getClass().getResourceAsStream("/en-pos-maxent.bin");
    POSModel posModel = null;
    try {
      posModel = new POSModel(inputStreamPOSTagger);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    this.posTagger = new POSTaggerME(posModel);
  }

  public HashMap<String, List<String>> find(String text) throws IOException {
    String[] tokens = tokenizer.tokenize(text);
    String[] tags = posTagger.tag(tokens);

    HashMap<String, List<String>> map = new HashMap<>();
    map.put(Constants.ADJECTIVE, new ArrayList<>());
    map.put(Constants.NOUN, new ArrayList<>());
    map.put(Constants.NOUNS, new ArrayList<>());

    for (int i = 0; i < tags.length; i++) {
      String token = tokens[i].toLowerCase(Locale.ROOT);
      if (token.length() >= 3) {
        List<String> list = map.get(tags[i]);
        if (list != null) {
          list.add(token);
        }
      }
    }
    return map;
  }
}
