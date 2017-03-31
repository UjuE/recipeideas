package com.ujuezeoke.learning.alexaskill.helper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Obianuju Ezeoke on 24/03/2017.
 */
public class IngredientReplacementMap {
    private final Map<String, String> intentIngredientPatternToWordReplacement;

    public IngredientReplacementMap() {
        intentIngredientPatternToWordReplacement = new HashMap<>();
        intentIngredientPatternToWordReplacement.put(".* chicken", "chicken");
        intentIngredientPatternToWordReplacement.put("onion", "onions");
    }

    public String replacementFor(String ingredient){
        return intentIngredientPatternToWordReplacement.entrySet()
                .stream()
                .filter(it -> ingredient.matches(it.getKey()))
                .map(Map.Entry::getValue)
                .findAny()
                .orElse(ingredient);
    }
}
