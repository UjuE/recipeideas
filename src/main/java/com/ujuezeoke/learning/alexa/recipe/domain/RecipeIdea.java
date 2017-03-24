package com.ujuezeoke.learning.alexa.recipe.domain;

/**
 * Created by Obianuju Ezeoke on 18/03/2017.
 */
public class RecipeIdea {
    private final String href;
    private final String recipeTitle;

    public RecipeIdea(String href, String recipeTitle) {
        this.href = href.trim();
        this.recipeTitle = recipeTitle.trim();
    }

    public String getHref() {
        return href;
    }

    public String getRecipeTitle() {
        return recipeTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecipeIdea that = (RecipeIdea) o;

        if (href != null ? !href.equals(that.href) : that.href != null) return false;
        return recipeTitle != null ? recipeTitle.equals(that.recipeTitle) : that.recipeTitle == null;
    }

    @Override
    public int hashCode() {
        int result = href != null ? href.hashCode() : 0;
        result = 31 * result + (recipeTitle != null ? recipeTitle.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RecipeIdea{" +
                "href='" + href + '\'' +
                ", recipeTitle='" + recipeTitle + '\'' +
                '}';
    }

    public String getHrefShortened() {
        return href.replaceAll("https?:\\//([a-zA-Z0-9.]+).*", "$1");
    }
}
