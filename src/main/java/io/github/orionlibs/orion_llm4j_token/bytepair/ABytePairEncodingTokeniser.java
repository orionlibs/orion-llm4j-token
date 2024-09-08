package io.github.orionlibs.orion_llm4j_token.bytepair;

import io.github.orionlibs.orion_tuple.Pair;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ABytePairEncodingTokeniser
{
    protected Map<Pair<Integer, Integer>, Integer> merges;
    private String pattern;
    private Map<String, Integer> specialTokens;
    protected Map<Integer, byte[]> vocabulary;


    public ABytePairEncodingTokeniser()
    {
        this.merges = new HashMap<>();
        this.pattern = "";
        this.specialTokens = new HashMap<>();
        this.vocabulary = VocabularyBuilder.buildVocabulary(merges, specialTokens);
    }


    public abstract void train(String text, int vocabularySize);


    public abstract List<Integer> encode(String text);


    public abstract String decode(List<Integer> tokenIDs);


    public String getPattern()
    {
        return pattern;
    }


    public Map<Pair<Integer, Integer>, Integer> getMerges()
    {
        return merges;
    }


    public Map<String, Integer> getSpecialTokens()
    {
        return specialTokens;
    }


    public Map<Integer, byte[]> getVocabulary()
    {
        return vocabulary;
    }


    public void setMerges(Map<Pair<Integer, Integer>, Integer> merges)
    {
        this.merges = merges;
    }


    public void setPattern(String pattern)
    {
        this.pattern = pattern;
    }


    public void setSpecialTokens(Map<String, Integer> specialTokens)
    {
        this.specialTokens = specialTokens;
    }


    public void setVocabulary(Map<Integer, byte[]> vocabulary)
    {
        this.vocabulary = vocabulary;
    }
}
