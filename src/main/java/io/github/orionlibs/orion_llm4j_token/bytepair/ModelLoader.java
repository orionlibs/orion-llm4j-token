package io.github.orionlibs.orion_llm4j_token.bytepair;

import io.github.orionlibs.orion_tuple.Pair;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ModelLoader
{
    public void loadModel(ABytePairEncodingTokeniser tokeniser, String modelFile) throws IOException
    {
        if(!modelFile.endsWith(".model"))
        {
            throw new IllegalArgumentException("File must end with '.model'");
        }
        // Initialize the merges and special tokens maps
        Map<Pair<Integer, Integer>, Integer> merges = new HashMap<>();
        Map<String, Integer> specialTokens = new HashMap<>();
        int idx = 256;
        // Read the model file
        try(BufferedReader reader = new BufferedReader(new FileReader(modelFile, StandardCharsets.UTF_8)))
        {
            // Read the version
            String version = reader.readLine().strip();
            if(!"minbpe v1".equals(version))
            {
                throw new IllegalStateException("Unexpected version: " + version);
            }
            // Read the pattern
            tokeniser.setPattern(reader.readLine().strip());
            // Read the special tokens
            int numSpecial = Integer.parseInt(reader.readLine().strip());
            for(int i = 0; i < numSpecial; i++)
            {
                String[] tokenLine = reader.readLine().strip().split(" ");
                String special = tokenLine[0];
                int specialIdx = Integer.parseInt(tokenLine[1]);
                specialTokens.put(special, specialIdx);
            }
            // Read the merges
            String line;
            while((line = reader.readLine()) != null)
            {
                String[] tokens = line.split(" ");
                int idx1 = Integer.parseInt(tokens[0]);
                int idx2 = Integer.parseInt(tokens[1]);
                merges.put(Pair.of(idx1, idx2), idx);
                idx++;
            }
        }
        // Set the instance fields
        tokeniser.setMerges(merges);
        tokeniser.setSpecialTokens(specialTokens);
        tokeniser.setVocabulary(VocabularyBuilder.buildVocabulary(merges, specialTokens));
    }
}
