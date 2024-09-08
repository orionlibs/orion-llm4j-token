package io.github.orionlibs.orion_llm4j_token.bytepair;

import io.github.orionlibs.orion_llm4j_token.Utils;
import io.github.orionlibs.orion_tuple.Pair;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ModelSaver
{
    public void saveModel(ABytePairEncodingTokeniser tokeniser, String filePrefix) throws IOException
    {
        // Write the model file
        String modelFile = filePrefix + ".model";
        try(BufferedWriter modelWriter = new BufferedWriter(new FileWriter(modelFile)))
        {
            // Write the version, pattern, and merges
            modelWriter.write("minbpe v1\n");
            modelWriter.write(tokeniser.getPattern() + "\n");
            // Write special tokens
            modelWriter.write(tokeniser.getSpecialTokens().size() + "\n");
            for(Map.Entry<String, Integer> entry : tokeniser.getSpecialTokens().entrySet())
            {
                modelWriter.write(entry.getKey() + " " + entry.getValue() + "\n");
            }
            // Write the merges
            for(Pair<Integer, Integer> pair : tokeniser.getMerges().keySet())
            {
                modelWriter.write(pair.getFirst() + " " + pair.getSecond() + "\n");
            }
        }
        // Write the vocabulary file (human-readable)
        String vocabularyFile = filePrefix + ".vocab";
        Map<Integer, Pair<Integer, Integer>> invertedMerges = new HashMap<>();
        for(Map.Entry<Pair<Integer, Integer>, Integer> entry : tokeniser.getMerges().entrySet())
        {
            invertedMerges.put(entry.getValue(), entry.getKey());
        }
        try(BufferedWriter vocabularyWriter = new BufferedWriter(new FileWriter(vocabularyFile, StandardCharsets.UTF_8)))
        {
            for(Map.Entry<Integer, byte[]> entry : tokeniser.getVocabulary().entrySet())
            {
                int idx = entry.getKey();
                byte[] token = entry.getValue();
                String s = Utils.renderToken(token);
                // Check if the token has children (i.e., if it's a merged token)
                if(invertedMerges.containsKey(idx))
                {
                    Pair<Integer, Integer> pair = invertedMerges.get(idx);
                    String s0 = Utils.renderToken(tokeniser.getVocabulary().get(pair.getFirst()));
                    String s1 = Utils.renderToken(tokeniser.getVocabulary().get(pair.getSecond()));
                    vocabularyWriter.write("[" + s0 + "][" + s1 + "] -> [" + s + "] " + idx + "\n");
                }
                else
                {
                    // Leaf token (first 256 tokens)
                    vocabularyWriter.write("[" + s + "] " + idx + "\n");
                }
            }
        }
    }
}
