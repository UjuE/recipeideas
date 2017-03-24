package com.ujuezeoke.learning.alexa;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;
import com.ujuezeoke.learning.alexa.helper.IngredientReplacementMap;

import java.util.HashSet;

import static java.util.Collections.singletonList;

@SuppressWarnings("unused")
public class AskRecipesSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {

    public static final String RECIPE_PUPPY_URL = "http://www.recipepuppy.com";

    public AskRecipesSpeechletRequestStreamHandler() {
        super(new AskRecipesSpeechlet(new RecipePuppyRequestSender(RECIPE_PUPPY_URL, new IngredientReplacementMap())),
                new HashSet<>(singletonList(uniqueAlexaAppID())));
    }

    private static String uniqueAlexaAppID() {
        return System.getenv().getOrDefault("recipe_ideas_arn", "[unique.id.here]");
    }
}
