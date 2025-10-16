package com.mcon152.recipeshare.web;

import com.mcon152.recipeshare.Recipe;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private final List<Recipe> recipes = new ArrayList<>();

    private final AtomicLong counter = new AtomicLong();
    RecipeController() {}

    /**
     * Adds a new recipe to the list.
     *
     * @param recipe the recipe to add
     * @return the added recipe with its assigned ID
     */
    @PostMapping
    public Recipe addRecipe(@RequestBody Recipe recipe) {
        recipe.setId(counter.incrementAndGet());
        recipes.add(recipe);
        return recipe;
    }

    /**
     * Retrieves all recipes.
     *
     * @return a list of all recipes
     */
    @GetMapping
    public List<Recipe> getAllRecipes() {
        return recipes;
    }

    /**
     * Retrieves a recipe by its ID.
     *
     * @param id the ID of the recipe to retrieve
     * @return the recipe with the specified ID, or null if not found
     */
    @GetMapping("/{id}")
    public Recipe getRecipeById(@PathVariable long id) {
        for (Recipe recipe : recipes) {
            if (recipe.getId() == id) {
                return recipe;
            }
        }
        return null;
    }

    /**
     * Deletes a recipe by its ID.
     *
     * @param id the ID of the recipe to delete
     * @return true if the recipe was deleted, false if not found
     */
    @DeleteMapping("/{id}")
    public boolean deleteRecipe(@PathVariable long id) {
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).getId() == id) {
                recipes.remove(i);
                return true;
            }
        }
        return false;
    }
    /**
     * Updates an existing recipe by its ID.
     *
     * @param id the ID of the recipe to update
     * @param updatedRecipe the updated recipe data
     * @return the updated recipe, or null if not found
     */
    @PutMapping("/{id}")
    public Recipe updateRecipe(@PathVariable long id, @RequestBody Recipe updatedRecipe) {
        throw new UnsupportedOperationException("Update recipe not implemented");
    }

    /**
     * Partially updates an existing recipe by its ID.
     *
     * @param id the ID of the recipe to update
     * @param partialRecipe the partial recipe data to update
     * @return the updated recipe, or null if not found
     */
    @PatchMapping("/{id}")
    public Recipe patchRecipe(@PathVariable long id, @RequestBody Recipe partialRecipe) {
        throw new UnsupportedOperationException("Update recipe not implemented");
    }
}