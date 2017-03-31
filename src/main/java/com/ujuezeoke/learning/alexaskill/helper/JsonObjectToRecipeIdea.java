package com.ujuezeoke.learning.alexaskill.helper;

import com.ujuezeoke.learning.alexaskill.recipe.domain.RecipeIdea;
import org.json.JSONObject;

/**
 * Created by Obianuju Ezeoke on 18/03/2017.
 */
public class JsonObjectToRecipeIdea {
    public static RecipeIdea convert(Object jsonObject) {
        JSONObject theJsonObject = (JSONObject) jsonObject;

        return new RecipeIdea(theJsonObject.getString("href"), theJsonObject.getString("title"));
    }
}
