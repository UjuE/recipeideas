package com.ujuezeoke.learning.alexa;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.ujuezeoke.learning.alexa.recipe.domain.RecipeIdea;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import static com.amazon.speech.speechlet.SpeechletResponse.newAskResponse;
import static com.amazon.speech.speechlet.SpeechletResponse.newTellResponse;
import static java.util.stream.Collectors.joining;

/**
 * Created by Obianuju Ezeoke on 18/03/2017.
 */
public class AskRecipesSpeechlet implements Speechlet {

    private static final String INGREDIENT_SLOT_NAME = "Ingredient";
    private final RecipePuppyRequestSender recipePuppyRequestSender;

    public AskRecipesSpeechlet(RecipePuppyRequestSender recipePuppyRequestSender) {
        this.recipePuppyRequestSender = recipePuppyRequestSender;
    }

    @Override
    public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
        //Do Nothing
    }

    @Override
    public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
        String speechText = "Welcome to the Ask Recipes skill. What ingredient would you like to cook with today";

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("WelcomeAskRecipe");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        return newAskResponse(speech, reprompt, card);
    }

    @Override
    public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
        final Intent intent = request.getIntent();
        final String intentName = intent.getName();

        if ("GetRecipeIdeasIntent".equals(intentName)) {
            return processGetRecipeIntent(intent);
        } else if ("AMAZON.HelpIntent".equals(intentName)) {
            return processHelpIntent();
        } else {
            throw new SpeechletException("Invalid Intent");
        }
    }

    @Override
    public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
        //Do Nothing
    }

    private SpeechletResponse processHelpIntent() {
        String speechText = "With Recipe Ideas, you can get a maximum of 3 recipe Ideas from one ingredient." +
                "For example, you can say, lettuce. Now what ingredient would you like to cook with today?";
        String repromptText = "What ingredient do you want to use?";
        final PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        final PlainTextOutputSpeech repromptOutputSpeech = new PlainTextOutputSpeech();
        final Reprompt reprompt = new Reprompt();
        final SimpleCard simpleCard = new SimpleCard();


        outputSpeech.setText(speechText);
        repromptOutputSpeech.setText(repromptText);
        reprompt.setOutputSpeech(repromptOutputSpeech);
        simpleCard.setTitle("RecipeIdeasHelp");
        simpleCard.setContent(speechText);
        return newAskResponse(outputSpeech, reprompt, simpleCard);
    }

    private SpeechletResponse processGetRecipeIntent(Intent intent) {
        final String ingredient = intent.getSlot(INGREDIENT_SLOT_NAME).getValue();
        AtomicInteger integer = new AtomicInteger(1);
        final Collection<RecipeIdea> recipeIdeas =
                recipePuppyRequestSender.recipesWithIngredient(ingredient);
        final String recipeIdeasText = recipeIdeas.stream().map(it -> integer.getAndIncrement()+" "+it.getRecipeTitle() + " found on, " + it.getHref())

                .collect(joining(" \n", " ", ""));

        final String speechText = String.format("Here are %d recipe ideas with %s: \n%s",
                recipeIdeas.size(), ingredient, recipeIdeasText);

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("RecipeIdeaList");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        return newTellResponse(speech, card);
    }
}
