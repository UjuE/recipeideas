package com.ujuezeoke.learning.alexa;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.ujuezeoke.learning.alexa.recipe.domain.RecipeIdea;
import org.junit.Rule;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.sun.jndi.toolkit.url.UrlUtil.encode;
import static com.ujuezeoke.learning.alexa.SampleResponses.SAMPLE_RESPONSE_WITH_RECIPES;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class RecipePuppyRequestSenderTest {

    private final RecipePuppyRequestSender underTest = new RecipePuppyRequestSender("http://localhost:8089");

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Test
    public void requestWithMoreThan3Details() {
        final String ingredient = "onion";

        stubFor(get(urlEqualTo("/api/?i="+ingredient))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(SAMPLE_RESPONSE_WITH_RECIPES)));

        Collection<RecipeIdea> recipeIdeas = underTest.recipesWithIngredient(ingredient);

        assertThat(recipeIdeas.size(), is(3));
        assertTrue(recipeIdeas.stream()
                .allMatch(it -> isNotNullOrBlank(it.getHref(), it.getRecipeTitle()))
        );
    }

    @Test
    public void requestWithMoreThan3DetailsUrlEncoded() throws UnsupportedEncodingException {
        final String ingredient = "fried chicken";

        final String encode = encode(ingredient, "utf-8");

        stubFor(get(urlEqualTo("/api/?i="+ ingredient.replace(" ","+")))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(SAMPLE_RESPONSE_WITH_RECIPES)));

        Collection<RecipeIdea> recipeIdeas = underTest.recipesWithIngredient(ingredient);

        assertThat(recipeIdeas.size(), is(3));
        assertTrue(recipeIdeas.stream()
                .allMatch(it -> isNotNullOrBlank(it.getHref(), it.getRecipeTitle()))
        );
    }

    @Test
    public void noRecipeIdeasWhenNotReachable() {
        Collection<RecipeIdea> recipeIdeas = underTest.recipesWithIngredient("someIngredient");

        assertThat(recipeIdeas.size(), is(0));
    }

    private boolean isNotNullOrBlank(String... strings) {
        return Stream.of(strings)
                .allMatch(it -> it != null && !it.isEmpty());
    }

}