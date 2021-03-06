package com.ujuezeoke.learning.alexaskill;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.ujuezeoke.learning.alexaskill.recipe.domain.RecipeIdea;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.amazon.speech.speechlet.SpeechletResponse.newAskResponse;
import static com.amazon.speech.speechlet.SpeechletResponse.newTellResponse;
import static java.util.stream.Collectors.joining;

/**
 * Created by Obianuju Ezeoke on 18/03/2017.
 */
public class AskRecipesSpeechlet implements Speechlet {

    private static final Logger LOG = Logger.getRootLogger();
    private static final String INGREDIENT_SLOT_NAME = "Ingredient";
    private final RecipePuppyRequestSender recipePuppyRequestSender;

    public AskRecipesSpeechlet(RecipePuppyRequestSender recipePuppyRequestSender) {
        this.recipePuppyRequestSender = recipePuppyRequestSender;
    }

    @Override
    public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
        LOG.info("On Session started: \nRequest: " + request.getRequestId() + "\n Session: " + session.getSessionId());
    }

    @Override
    public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
        LOG.info("On Launch: \nRequest: " + request.getRequestId() + "\n Session: " + session.getSessionId());
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
        LOG.info("On Intent: \nRequest: " + request.getRequestId() + "\n Session: " + session.getSessionId());
        final Intent intent = request.getIntent();
        final String intentName = intent.getName();

        switch (intentName) {
            case "GetRecipeIdeasIntent":
                return processGetRecipeIntent(intent);
            case "AMAZON.HelpIntent":
                return processHelpIntent();
            case "AMAZON.CancelIntent":
            case "AMAZON.StopIntent":
                return processStopIntent();
            default:
                throw new SpeechletException("Invalid Intent");
        }
    }

    @Override
    public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
        LOG.info("On Session Ended: \nRequest: " + request.getRequestId() + "\n Session is new: " + session.getSessionId());
    }

    private SpeechletResponse processHelpIntent() {
        String speechText = "With Recipe Ideas, you can get a maximum of 3 recipe Ideas from one ingredient. " +
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

    private SpeechletResponse processStopIntent() {
        String speechText = "Thank you for using Recipe Ideas. Goodbye.";
        final PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        final PlainTextOutputSpeech repromptOutputSpeech = new PlainTextOutputSpeech();
        final SimpleCard simpleCard = new SimpleCard();


        outputSpeech.setText(speechText);
        repromptOutputSpeech.setText(speechText);
        simpleCard.setTitle("RecipeIdeasStop");
        simpleCard.setContent(speechText);
        return newTellResponse(outputSpeech, simpleCard);
    }

    //todo anything with chicken return chicken, for eggs return egg, for onion return onions
    private SpeechletResponse processGetRecipeIntent(Intent intent) {
        final Optional<Slot> slot = Optional.ofNullable(intent.getSlot(INGREDIENT_SLOT_NAME));
        if (slot.isPresent() && Optional.ofNullable(slot.get().getValue()).isPresent()) {
            final String ingredient = slot.get().getValue();
            return processIngredientExists(ingredient);
        }
        return noIngredientResponse();
    }

    private SpeechletResponse noIngredientResponse() {
        String response = "I did not understand the ingredient you said. " +
                "Please try using the singular form or add adjectives. " +
                "For example, instead of onions, say onion. " +
                "Rather than chicken, try fried chicken.";

        SimpleCard card = new SimpleCard();
        card.setTitle("RecipeIdeaList");
        card.setContent(response);

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(response);

        return newTellResponse(speech, card);
    }

    private SpeechletResponse processIngredientExists(String ingredient) {
        final Collection<RecipeIdea> recipeIdeas =
                recipePuppyRequestSender.recipesWithIngredient(ingredient);

        if (!recipeIdeas.isEmpty()) {
            return processWithRecipeIdeasIdeas(ingredient, recipeIdeas);
        }
        return processWithNoIdeas(ingredient);
    }

    private SpeechletResponse processWithNoIdeas(String ingredient) {
        String response = String.format("There are no recipe ideas with %s.", ingredient);
        SimpleCard card = new SimpleCard();
        card.setTitle("RecipeIdeaList");
        card.setContent(response);

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(response);

        return newTellResponse(speech, card);
    }

    private SpeechletResponse processWithRecipeIdeasIdeas(String ingredient, Collection<RecipeIdea> recipeIdeas) {
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("RecipeIdeaList");
        card.setContent(recipeIdeasForCards(recipeIdeas, ingredient));

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(recipeIdeasForSpeechText(recipeIdeas, ingredient));

        return newTellResponse(speech, card);
    }

    private String recipeIdeasForSpeechText(Collection<RecipeIdea> recipeIdeas, String ingredient) {
        AtomicInteger integer = new AtomicInteger(1);
        final String ideas = recipeIdeas
                .stream()
                .map(it -> "Recipe " + integer.getAndIncrement() + ", " + it.getRecipeTitle())
                .collect(joining(". "));
        return formatText(recipeIdeas, ingredient, ideas)
                .replace("&", "and")
                .replace("_", " ");
    }

    private String recipeIdeasForCards(Collection<RecipeIdea> recipeIdeas, String ingredient) {
        AtomicInteger integer = new AtomicInteger(1);
        final String ideas = recipeIdeas
                .stream()
                .map(it -> integer.getAndIncrement() + " " + it.getRecipeTitle() + " found on, " + it.getHrefShortened())
                .collect(joining(" \n", " ", ""));

        return formatText(recipeIdeas, ingredient, ideas);
    }

    private String formatText(Collection<RecipeIdea> recipeIdeas, String ingredient, String ideas) {
        return String.format("Here are %d recipe ideas with %s: \n%s",
                recipeIdeas.size(), ingredient, ideas);
    }
}
