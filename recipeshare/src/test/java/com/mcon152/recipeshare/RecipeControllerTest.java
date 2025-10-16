package com.mcon152.recipeshare;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mcon152.recipeshare.web.RecipeController;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.isEmptyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static ObjectMapper mapper;

    @BeforeAll
    static void setup() {
        mapper = new ObjectMapper();
    }

    // =========================
    // Creation-related tests
    // =========================
    @Nested
    class CreationTests {

        @Test
        void addRecipe_shouldReturnSavedRecipeWithId() throws Exception {
            ObjectNode json = mapper.createObjectNode();
            json.put("title", "Cake");
            json.put("description", "Delicious cake");
            json.put("ingredients", "1 cup of flour, 1 cup of sugar, 3 eggs");
            json.put("instructions", "Mix and bake");
            String jsonString = mapper.writeValueAsString(json);

            mockMvc.perform(post("/api/recipes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonString))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Cake"))
                    .andExpect(jsonPath("$.description").value("Delicious cake"))
                    .andExpect(jsonPath("$.ingredients").value("1 cup of flour, 1 cup of sugar, 3 eggs"))
                    .andExpect(jsonPath("$.instructions").value("Mix and bake"))
                    .andExpect(jsonPath("$.id").isNumber());
        }

        @ParameterizedTest
        @CsvSource({
                "'Chocolate Cake','Rich chocolate cake','2 cups flour;1 cup cocoa;4 eggs','Bake at 350F for 30 min'",
                "'Pasta Salad','Fresh pasta salad','200g pasta;100g tomatoes;50g olives','Mix all ingredients'",
                "'Pancakes','Fluffy pancakes','1 cup flour;2 eggs;1 cup milk','Cook on skillet until golden'"
        })
        void addRecipe_withVariousValidInputs_shouldReturnSavedRecipesWithIds(String title, String description, String ingredients, String instructions) throws Exception {
            ObjectNode json = mapper.createObjectNode();
            json.put("title", title);
            json.put("description", description);
            json.put("ingredients", ingredients);
            json.put("instructions", instructions);
            String jsonString = mapper.writeValueAsString(json);

            mockMvc.perform(post("/api/recipes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonString))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value(title))
                    .andExpect(jsonPath("$.description").value(description))
                    .andExpect(jsonPath("$.ingredients").value(ingredients))
                    .andExpect(jsonPath("$.instructions").value(instructions))
                    .andExpect(jsonPath("$.id").isNumber());
        }
    }

    // =========================
    // Delete / Get / Update tests
    // =========================
    @Nested
    class DeleteAndGetTests {
        private List<Integer> recipeIds;

        @BeforeEach
        void createRecipes() throws Exception {
            recipeIds = new ArrayList<>();
            String[] recipes = {
                    "{\"title\":\"Pie\",\"description\":\"Apple pie\",\"ingredients\":\"Apples, Flour, Sugar\",\"instructions\":\"Mix and bake\"}",
                    "{\"title\":\"Soup\",\"description\":\"Tomato soup\",\"ingredients\":\"Tomatoes, Water, Salt\",\"instructions\":\"Boil and blend\"}"
            };
            for (String json : recipes) {
                String response = mockMvc.perform(post("/api/recipes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
                int id = mapper.readTree(response).get("id").asInt();
                recipeIds.add(id);
            }
        }

        @Test
        void getAllRecipes_shouldReturnAddedRecipesInOrder() throws Exception {
            mockMvc.perform(get("/api/recipes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].title").value("Pie"))
                    .andExpect(jsonPath("$[1].title").value("Soup"));
        }

        @Test
        void getRecipe_shouldReturnRequestedRecipe() throws Exception {
            int id = recipeIds.get(0);
            mockMvc.perform(get("/api/recipes/" + id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Pie"));
        }

        @Test
        void deleteRecipe_shouldRemoveAndReturnTrue() throws Exception {
            int id = recipeIds.get(0);

            mockMvc.perform(delete("/api/recipes/" + id))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));

            // verify it’s gone (controller returns 200 + empty body when not found)
            mockMvc.perform(get("/api/recipes/" + id))
                    .andExpect(status().isOk())
                    .andExpect(content().string(isEmptyString()));
        }

        @Test
        void putRecipe_shouldReplaceAndPreserveId() throws Exception {
            int id = recipeIds.get(0);

            String updated = """
                {
                  "title":"Updated Pie",
                  "description":"New apple pie",
                  "ingredients":"Apples, Flour, Sugar, Butter",
                  "instructions":"Mix and bake at 375F"
                }
                """;

            mockMvc.perform(put("/api/recipes/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updated))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.title").value("Updated Pie"))
                    .andExpect(jsonPath("$.description").value("New apple pie"))
                    .andExpect(jsonPath("$.ingredients").value("Apples, Flour, Sugar, Butter"))
                    .andExpect(jsonPath("$.instructions").value("Mix and bake at 375F"));

            // GET reflects the update
            mockMvc.perform(get("/api/recipes/" + id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Updated Pie"));
        }

        @Test
        void patchRecipe_shouldUpdateOnlyProvidedFields() throws Exception {
            int id = recipeIds.get(0);

            String patchBody = """
                { "description":"Apple pie (patched)" }
                """;

            mockMvc.perform(patch("/api/recipes/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(patchBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.description").value("Apple pie (patched)"))
                    // title remains original
                    .andExpect(jsonPath("$.title").value("Pie"));
        }
    }

    // =========================
    // Non-existing ID tests
    // =========================
    @Nested
    class NonExistingRecipeTests {

        @Test
        void getRecipe_nonExisting_shouldReturnEmptyBody() throws Exception {
            mockMvc.perform(get("/api/recipes/9999"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(isEmptyString()));
        }

        @Test
        void putRecipe_nonExisting_shouldReturnEmptyBody() throws Exception {
            String body = """
                {"title":"Ghost","description":"Does not exist","ingredients":"None","instructions":"None"}
                """;
            mockMvc.perform(put("/api/recipes/9999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(content().string(isEmptyString()));
        }

        @Test
        void patchRecipe_nonExisting_shouldReturnEmptyBody() throws Exception {
            String body = """
                {"description":"patched"}
                """;
            mockMvc.perform(patch("/api/recipes/9999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(content().string(isEmptyString()));
        }

        @Test
        void deleteRecipe_nonExisting_shouldReturnFalse() throws Exception {
            mockMvc.perform(delete("/api/recipes/9999"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));
        }
    }
}
