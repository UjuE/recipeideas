package com.ujuezeoke.learning.alexa;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

import java.util.HashSet;

import static java.util.Collections.singletonList;

@SuppressWarnings("unused")
public class AskRecipesSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {

    public static final String RECIPE_PUPPY_URL = "http://www.recipepuppy.com";

    public AskRecipesSpeechletRequestStreamHandler() {
        super(new AskRecipesSpeechlet(new RecipePuppyRequestSender(RECIPE_PUPPY_URL)),
                new HashSet<>(singletonList("amzn1.echo-sdk-ams.app." + uniqueAppID())));
    }

    private static String uniqueAppID() {
        return System.getenv().getOrDefault("recipe_ideas_arn", "[unique.id.here]");
    }
}
