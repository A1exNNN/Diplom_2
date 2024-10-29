package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Order {

    private List<String> ingredients;

    public Order(String[] ingredients) {
        this.ingredients = new ArrayList<>(Arrays.asList(ingredients));
    }

    // Getter для ингредиентов
    public List<String> getIngredients() {
        return ingredients;
    }

    // Setter для ингредиентов
    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}