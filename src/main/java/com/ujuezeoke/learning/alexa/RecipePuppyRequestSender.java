package com.ujuezeoke.learning.alexa;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.ujuezeoke.learning.alexa.helper.IngredientReplacementMap;
import com.ujuezeoke.learning.alexa.helper.JsonObjectToRecipeIdea;
import com.ujuezeoke.learning.alexa.recipe.domain.RecipeIdea;
import org.json.JSONArray;

import java.util.Collection;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.StreamSupport.stream;

/**
 * Created by Obianuju Ezeoke on 18/03/2017.
 */
public class RecipePuppyRequestSender {
    private static final String URL_SUFFIX = "/api/";
    private final String url;
    private final IngredientReplacementMap ingredientReplacementMap;

    public RecipePuppyRequestSender(String url, IngredientReplacementMap ingredientReplacementMap) {
        this.url = url + URL_SUFFIX;
        this.ingredientReplacementMap = ingredientReplacementMap;
    }

    public Collection<RecipeIdea> recipesWithIngredient(String ingredient) {
        final HttpRequest getRequest = Unirest
                .get(url)
                .queryString("i", ingredientReplacementMap.replacementFor(ingredient));
        try {
            final JSONArray results = getRequest
                    .asJson()
                    .getBody()
                    .getObject()
                    .getJSONArray("results");


            return stream(results.spliterator(), true)
                    .limit(3)
                    .map(JsonObjectToRecipeIdea::convert)
                    .collect(toSet());

        } catch (UnirestException e) {
            return emptySet();
        }
    }

}
