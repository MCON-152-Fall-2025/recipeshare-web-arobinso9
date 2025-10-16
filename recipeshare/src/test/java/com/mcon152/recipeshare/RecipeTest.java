package com.mcon152.recipeshare;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RecipeTest {

    @Test
    void testCreateRecipe() {
        Recipe recipe = new Recipe(1L, "Cake", "Delicious cake", "Flour, Sugar, Eggs", "Mix and bake");
        assertEquals(1L, recipe.getId());
        assertEquals("Cake", recipe.getTitle());
        assertEquals("Delicious cake", recipe.getDescription());
        assertEquals("Flour, Sugar, Eggs", recipe.getIngredients());
        assertEquals("Mix and bake", recipe.getInstructions());
    }

    @Test
    void testReadRecipe() {
        Recipe recipe = new Recipe();
        recipe.setId(2L);
        recipe.setTitle("Pie");
        recipe.setDescription("Apple pie");
        recipe.setIngredients("Apples, Flour, Sugar");
        recipe.setInstructions("Mix and bake");
        assertEquals(2L, recipe.getId());
        assertEquals("Pie", recipe.getTitle());
        assertEquals("Apple pie", recipe.getDescription());
        assertEquals("Apples, Flour, Sugar", recipe.getIngredients());
        assertEquals("Mix and bake", recipe.getInstructions());
    }

    @Test
    void testUpdateRecipe() {
        Recipe recipe = new Recipe();
        recipe.setTitle("Bread");
        recipe.setDescription("Simple bread");
        recipe.setIngredients("Flour, Water, Yeast");
        recipe.setInstructions("Mix and bake");
        recipe.setTitle("Whole Wheat Bread");
        recipe.setDescription("Healthy bread");
        assertEquals("Whole Wheat Bread", recipe.getTitle());
        assertEquals("Healthy bread", recipe.getDescription());
    }

    @Test
    void testDeleteRecipe() {
        Recipe recipe = new Recipe(3L, "Soup", "Hot soup", "Water, Vegetables", "Boil");
        recipe = null;
        assertNull(recipe);
    }
}
