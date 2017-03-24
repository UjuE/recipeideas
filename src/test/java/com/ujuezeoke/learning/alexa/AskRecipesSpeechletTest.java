package com.ujuezeoke.learning.alexa;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.Card;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SimpleCard;
import com.ujuezeoke.learning.alexa.recipe.domain.RecipeIdea;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Obianuju Ezeoke on 18/03/2017.
 */
public class AskRecipesSpeechletTest {

    private final RecipePuppyRequestSender recipePuppyRequestSender = mock(RecipePuppyRequestSender.class);
    private final AskRecipesSpeechlet underTest = new AskRecipesSpeechlet(recipePuppyRequestSender);
    private final Session testSession = Session.builder().withSessionId("test").build();

    @Test
    public void getRecipeIdeasIntentWith2Ideas() throws Exception {
        final List<RecipeIdea> recipeIdeas = asList(
                new RecipeIdea("http://www.foo.com", "Beef Stroodle"),
                new RecipeIdea("http://www.bar.com/1221/ewewe/32442/2321.html", "Jam and Bread")
        );

        final IntentRequest intentRequest = mock(IntentRequest.class);
        final String ingredient = "onions";

        final Slot ingredientSlot = Slot.builder().withName("Ingredient").withValue(ingredient).build();
        final Intent intent = Intent.builder()
                .withName("GetRecipeIdeasIntent")
                .withSlots(slotWith("Ingredient", ingredientSlot))
                .build();
        final String expectedCardResponse = "Here are 2 recipe ideas with onions: \n " +
                "1 Beef Stroodle found on, " +
                "www.foo.com \n" +
                "2 Jam and Bread found on, www.bar.com";

        final String expectedSpokenResponse = "Here are 2 recipe ideas with onions: \n" +
                "Recipe 1, Beef Stroodle. " +
                "Recipe 2, Jam and Bread";

        when(recipePuppyRequestSender.recipesWithIngredient(ingredient)).thenReturn(recipeIdeas);
        when(intentRequest.getIntent()).thenReturn(intent);

        final SpeechletResponse speechletResponse = underTest.onIntent(intentRequest, testSession);

        assertTrue(speechletResponse.getShouldEndSession());
        assertThat(speechletResponse.getCard().getTitle(), is("RecipeIdeaList"));

        assertThat((asSimpleCard(speechletResponse.getCard())).getContent(), is(expectedCardResponse));
        assertThat((asPlainTextOutputSpeech(speechletResponse.getOutputSpeech())).getText(),
                is(expectedSpokenResponse));
    }

    @Test
    public void hasNoAmpersandsOrUnderscores() throws Exception {
        final List<RecipeIdea> recipeIdeas = asList(
                new RecipeIdea("http://www.foo.com", "Beef_Stroodle"),
                new RecipeIdea("http://www.bar.com", "Jam & Bread"),
                new RecipeIdea("http://www.kraftfoods.com/kf/recipes/roast-chicken-66318.aspx", "Roast Chicken")
        );

        final IntentRequest intentRequest = mock(IntentRequest.class);
        final String ingredient = "onions";

        final Slot ingredientSlot = Slot.builder().withName("Ingredient").withValue(ingredient).build();
        final Intent intent = Intent.builder()
                .withName("GetRecipeIdeasIntent")
                .withSlots(slotWith("Ingredient", ingredientSlot))
                .build();
        final String expectedCardResponse = "Here are 3 recipe ideas with onions: \n " +
                "1 Beef_Stroodle found on, " +
                "www.foo.com \n" +
                "2 Jam & Bread found on, www.bar.com \n" +
                "3 Roast Chicken found on, www.kraftfoods.com";

        final String expectedSpokenResponse = "Here are 3 recipe ideas with onions: \n" +
                "Recipe 1, Beef Stroodle. " +
                "Recipe 2, Jam and Bread. " +
                "Recipe 3, Roast Chicken";

        when(recipePuppyRequestSender.recipesWithIngredient(ingredient)).thenReturn(recipeIdeas);
        when(intentRequest.getIntent()).thenReturn(intent);

        final SpeechletResponse speechletResponse = underTest.onIntent(intentRequest, testSession);

        assertTrue(speechletResponse.getShouldEndSession());
        assertThat(speechletResponse.getCard().getTitle(), is("RecipeIdeaList"));

        assertThat((asSimpleCard(speechletResponse.getCard())).getContent(), is(expectedCardResponse));
        assertThat((asPlainTextOutputSpeech(speechletResponse.getOutputSpeech())).getText(),
                is(expectedSpokenResponse));
    }

    @Test
    public void getRecipeIdeasIntentWithNoIdeas() throws Exception {
        final List<RecipeIdea> recipeIdeas = Collections.emptyList();

        final IntentRequest intentRequest = mock(IntentRequest.class);
        final String ingredient = "onions";

        final Slot ingredientSlot = Slot.builder().withName("Ingredient").withValue(ingredient).build();
        final Intent intent = Intent.builder()
                .withName("GetRecipeIdeasIntent")
                .withSlots(slotWith("Ingredient", ingredientSlot))
                .build();
        final String expectedSpokenResponse = "There are no recipe ideas with onions.";

        when(recipePuppyRequestSender.recipesWithIngredient(ingredient)).thenReturn(recipeIdeas);
        when(intentRequest.getIntent()).thenReturn(intent);

        final SpeechletResponse speechletResponse = underTest.onIntent(intentRequest, testSession);

        assertTrue(speechletResponse.getShouldEndSession());
        assertThat(speechletResponse.getCard().getTitle(), is("RecipeIdeaList"));

        assertThat((asSimpleCard(speechletResponse.getCard())).getContent(), is(expectedSpokenResponse));
        assertThat((asPlainTextOutputSpeech(speechletResponse.getOutputSpeech())).getText(),
                is(expectedSpokenResponse));
    }

    @Test
    public void nullIngredientSlot() throws Exception {
        final IntentRequest intentRequest = mock(IntentRequest.class);

        final Slot ingredientSlot = Slot.builder().withName("Ingredient").build();
        final Intent intent = Intent.builder()
                .withName("GetRecipeIdeasIntent")
                .withSlots(slotWith("Ingredient", ingredientSlot))
                .build();
        final String expectedCardResponse = "I did not understand the ingredient you said. " +
                "Please try using the singular form or add adjectives. " +
                "For example, instead of onions, say onion. " +
                "Rather than chicken, try fried chicken.";

        when(intentRequest.getIntent()).thenReturn(intent);

        final SpeechletResponse speechletResponse = underTest.onIntent(intentRequest, testSession);

        verifyZeroInteractions(recipePuppyRequestSender);
        assertTrue(speechletResponse.getShouldEndSession());
        assertThat(speechletResponse.getCard().getTitle(), is("RecipeIdeaList"));

        assertThat((asSimpleCard(speechletResponse.getCard())).getContent(), is(expectedCardResponse));
        assertThat((asPlainTextOutputSpeech(speechletResponse.getOutputSpeech())).getText(),
                is(expectedCardResponse));
    }

    @Test
    public void nullIngredientSlotValue() throws Exception {
        final IntentRequest intentRequest = mock(IntentRequest.class);

        final Intent intent = Intent.builder()
                .withName("GetRecipeIdeasIntent")
                .withSlots(slotWith("Ingredient", null))
                .build();
        final String expectedCardResponse = "I did not understand the ingredient you said. " +
                "Please try using the singular form or add adjectives. " +
                "For example, instead of onions, say onion. " +
                "Rather than chicken, try fried chicken.";

        when(intentRequest.getIntent()).thenReturn(intent);

        final SpeechletResponse speechletResponse = underTest.onIntent(intentRequest, testSession);

        verifyZeroInteractions(recipePuppyRequestSender);
        assertTrue(speechletResponse.getShouldEndSession());
        assertThat(speechletResponse.getCard().getTitle(), is("RecipeIdeaList"));

        assertThat((asSimpleCard(speechletResponse.getCard())).getContent(), is(expectedCardResponse));
        assertThat((asPlainTextOutputSpeech(speechletResponse.getOutputSpeech())).getText(),
                is(expectedCardResponse));
    }

    @Test
    public void helpIntent() throws Exception {

        final IntentRequest intentRequest = mock(IntentRequest.class);

        final Intent intent = Intent.builder()
                .withName("AMAZON.HelpIntent")
                .build();
        final String expectedResponse = "With Recipe Ideas, you can get a maximum of 3 recipe Ideas from one ingredient. " +
                "For example, you can say, lettuce. Now what ingredient would you like to cook with today?";

        when(intentRequest.getIntent()).thenReturn(intent);

        final SpeechletResponse speechletResponse = underTest.onIntent(intentRequest, testSession);

        verifyZeroInteractions(recipePuppyRequestSender);

        assertFalse(speechletResponse.getShouldEndSession());
        assertThat(asPlainTextOutputSpeech(speechletResponse.getOutputSpeech()).getText(),
                is(expectedResponse));

        assertThat(asSimpleCard(speechletResponse.getCard()).getContent(), is(expectedResponse));
        assertThat(asPlainTextOutputSpeech(speechletResponse.getOutputSpeech()).getText(),
                is(expectedResponse));
    }

    @Test
    public void stopIntent() throws Exception {

        final IntentRequest intentRequest = mock(IntentRequest.class);

        final Intent intent = Intent.builder()
                .withName("AMAZON.StopIntent")
                .build();
        final String expectedResponse = "Thank you for using Recipe Ideas. Goodbye.";

        when(intentRequest.getIntent()).thenReturn(intent);

        final SpeechletResponse speechletResponse = underTest.onIntent(intentRequest, testSession);

        verifyZeroInteractions(recipePuppyRequestSender);

        assertTrue(speechletResponse.getShouldEndSession());
        assertThat(asPlainTextOutputSpeech(speechletResponse.getOutputSpeech()).getText(),
                is(expectedResponse));

        assertThat(asSimpleCard(speechletResponse.getCard()).getContent(), is(expectedResponse));
        assertThat(asPlainTextOutputSpeech(speechletResponse.getOutputSpeech()).getText(),
                is(expectedResponse));
    }

    @Test
    public void cancelIntent() throws Exception {

        final IntentRequest intentRequest = mock(IntentRequest.class);

        final Intent intent = Intent.builder()
                .withName("AMAZON.CancelIntent")
                .build();
        final String expectedResponse = "Thank you for using Recipe Ideas. Goodbye.";

        when(intentRequest.getIntent()).thenReturn(intent);

        final SpeechletResponse speechletResponse = underTest.onIntent(intentRequest, testSession);

        verifyZeroInteractions(recipePuppyRequestSender);

        assertTrue(speechletResponse.getShouldEndSession());
        assertThat(asPlainTextOutputSpeech(speechletResponse.getOutputSpeech()).getText(),
                is(expectedResponse));

        assertThat(asSimpleCard(speechletResponse.getCard()).getContent(), is(expectedResponse));
        assertThat(asPlainTextOutputSpeech(speechletResponse.getOutputSpeech()).getText(),
                is(expectedResponse));
    }

    private SimpleCard asSimpleCard(Card card) {
        return (SimpleCard) card;
    }

    private PlainTextOutputSpeech asPlainTextOutputSpeech(OutputSpeech outputSpeech) {
        return (PlainTextOutputSpeech) outputSpeech;
    }

    private HashMap<String, Slot> slotWith(String key, Slot slot) {
        final HashMap<String, Slot> stringSlotHashMap = new HashMap<>();
        stringSlotHashMap.put(key, slot);
        return stringSlotHashMap;
    }
}