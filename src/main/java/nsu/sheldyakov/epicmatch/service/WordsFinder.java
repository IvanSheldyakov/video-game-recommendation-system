package nsu.sheldyakov.epicmatch.service;

import static nsu.sheldyakov.epicmatch.utils.Constants.*;

import java.io.InputStream;
import java.util.*;
import nsu.sheldyakov.epicmatch.exception.ServiceException;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.SimpleTokenizer;

public class WordsFinder {

  private static final Set<String> NEEDED_TAGS = Set.of(ADJECTIVE, NOUN, NOUNS);

  public List<String> find(String text) {
    SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
    String[] tokens = tokenizer.tokenize(text);

    try (InputStream inputStreamPOSTagger = getClass().getResourceAsStream(PATH_TO_POS_TAGGER)) {
      POSModel posModel = new POSModel(Objects.requireNonNull(inputStreamPOSTagger));
      POSTaggerME posTagger = new POSTaggerME(posModel);
      String[] tags = posTagger.tag(tokens);

      List<String> words = new ArrayList<>();

      for (int i = 0; i < tags.length; i++) {
        String tag = tags[i];
        if (NEEDED_TAGS.contains(tag)) {
          String token = tokens[i].toLowerCase(Locale.ROOT);
          if (token.length() < 3) {
            continue;
          }
          words.add(token);
        }
      }
      return words;
    } catch (Exception e) {
      throw new ServiceException("WordsFinder is failed", e);
    }
  }
}
