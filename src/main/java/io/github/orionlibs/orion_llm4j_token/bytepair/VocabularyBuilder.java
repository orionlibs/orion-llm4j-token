package io.github.orionlibs.orion_llm4j_token.bytepair;

import io.github.orionlibs.orion_tuple.Pair;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

class VocabularyBuilder
{
    //vocabulary is simply and deterministically derived from merges
    static Map<Integer, byte[]> buildVocabulary(Map<Pair<Integer, Integer>, Integer> merges, Map<String, Integer> specialTokens)
    {
        Map<Integer, byte[]> vocabulary = new HashMap<>();
        //256 is the vocabulary size + the merges
        for(int i = 0; i < 256; i++)
        {
            vocabulary.put(i, new byte[] {(byte)i});
        }
        // Process merges
        for(Map.Entry<Pair<Integer, Integer>, Integer> entry : merges.entrySet())
        {
            Pair<Integer, Integer> pair = entry.getKey();
            int p0 = pair.getFirst();
            int p1 = pair.getSecond();
            int idx = entry.getValue();
            // Concatenate byte arrays from vocabulary.get(p0) and vocabulary.get(p1)
            byte[] mergedBytes = new byte[vocabulary.get(p0).length + vocabulary.get(p1).length];
            System.arraycopy(vocabulary.get(p0), 0, mergedBytes, 0, vocabulary.get(p0).length);
            System.arraycopy(vocabulary.get(p1), 0, mergedBytes, vocabulary.get(p0).length, vocabulary.get(p1).length);
            // Add the merged byte array to vocab
            vocabulary.put(idx, mergedBytes);
        }
        // Process special tokens
        for(Map.Entry<String, Integer> entry : specialTokens.entrySet())
        {
            String special = entry.getKey();
            int idx = entry.getValue();
            vocabulary.put(idx, special.getBytes(StandardCharsets.UTF_8));
        }
        return vocabulary;
    }
}
