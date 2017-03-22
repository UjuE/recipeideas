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

import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by Obianuju Ezeoke on 18/03/2017.
 */
public class AskRecipesSpeechletTest {

    private final RecipePuppyRequestSender recipePuppyRequestSender = mock(RecipePuppyRequestSender.class);
    private final AskRecipesSpeechlet underTest = new AskRecipesSpeechlet(recipePuppyRequestSender);
    private final Session testSession = Session.builder().withSessionId("test").build();

    @Test
    public void getRecipeIdeasIntent() throws Exception {
        final List<RecipeIdea> recipeIdeas = asList(
                new RecipeIdea("http://www.foo.com", "Beef Stroodle"),
                new RecipeIdea("http://www.bar.com", "Jam and Bread")
        );

        final IntentRequest intentRequest = mock(IntentRequest.class);
        final String ingredient = "onions";

        final Slot ingredientSlot = Slot.builder().withName("Ingredient").withValue(ingredient).build();
        final Intent intent = Intent.builder()
                .withName("GetRecipeIdeasIntent")
                .withSlots(slotWith("Ingredient", ingredientSlot))
                .build();
        final String expectedResponse = "Here are 2 recipe ideas with onions: \n " +
                "1 Beef Stroodle found on, " +
                "http://www.foo.com \n" +
                "2 Jam and Bread found on, http://www.bar.com";

        when(recipePuppyRequestSender.recipesWithIngredient(ingredient)).thenReturn(recipeIdeas);
        when(intentRequest.getIntent()).thenReturn(intent);

        final SpeechletResponse speechletResponse = underTest.onIntent(intentRequest, testSession);

        assertTrue(speechletResponse.getShouldEndSession());
        assertThat(speechletResponse.getCard().getTitle(), is("RecipeIdeaList"));

        assertThat((asSimpleCard(speechletResponse.getCard())).getContent(), is(expectedResponse));
        assertThat((asPlainTextOutputSpeech(speechletResponse.getOutputSpeech())).getText(),
                is(expectedResponse));
    }

    @Test
    public void helpIntent() throws Exception {

        final IntentRequest intentRequest = mock(IntentRequest.class);

        final Intent intent = Intent.builder()
                .withName("AMAZON.HelpIntent")
                .build();
        final String expectedResponse = "With Recipe Ideas, you can get a maximum of 3 recipe Ideas from one ingredient." +
                "For example, you can say, lettuce. Now what ingredient would you like to cook with today?";

        when(intentRequest.getIntent()).thenReturn(intent);

        final SpeechletResponse speechletResponse = underTest.onIntent(intentRequest, testSession);

        verifyZeroInteractions(recipePuppyRequestSender);

        assertFalse(speechletResponse.getShouldEndSession());
        assertThat(asPlainTextOutputSpeech(speechletResponse.getReprompt().getOutputSpeech()).getText(),
                is("What ingredient do you want to use?"));

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