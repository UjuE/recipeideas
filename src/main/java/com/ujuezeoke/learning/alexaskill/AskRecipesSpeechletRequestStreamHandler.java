package com.ujuezeoke.learning.alexaskill;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;
import com.ujuezeoke.learning.alexaskill.helper.IngredientReplacementMap;

import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

@SuppressWarnings("unused")
public class AskRecipesSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {

    public static final String RECIPE_PUPPY_URL = "http://www.recipepuppy.com";

    public AskRecipesSpeechletRequestStreamHandler() {
        super(new AskRecipesSpeechlet(new RecipePuppyRequestSender(RECIPE_PUPPY_URL, new IngredientReplacementMap())),
                uniqueAlexaAppAppIDs());
    }

    private static Set<String> uniqueAlexaAppAppIDs() {
        return Stream.of(System.getenv().getOrDefault("recipe_ideas_arn", "").split(","))
                .collect(toSet());
    }
}
