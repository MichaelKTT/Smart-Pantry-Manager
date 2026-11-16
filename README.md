# Smart Pantry Manager

Smart Pantry Manager is an Android app that helps users keep track of leftover ingredients and decide what to cook. It suggests a recipe only when the pantry contains **every required ingredient in the right quantity**.

The app was built with **Java and XML** in **Android Studio** for the **Mobile App Development 700** assignment at Richfield.

## Features

- Add, view, edit and delete pantry ingredients.
- Record each ingredient’s quantity, unit and optional expiry date.
- Get suggestions from 20 recipes stored on the device.
- Open a recipe to see its ingredients, quantities and preparation steps.
- Turn expiry highlights on or off in Settings.
- Load a sample pantry to try the app with demonstration data.

## App screens

| Screen | What it does |
| --- | --- |
| Pantry | Displays saved ingredients and shows how many recipes can be made. |
| Add/Edit Ingredient | Lets the user enter or update an ingredient and validates the form before saving. |
| Suggested Recipes | Lists recipes that can be made with the current pantry. |
| Recipe Details | Shows the full ingredient list, preparation steps and estimated time. |
| Settings | Controls expiry highlights and lets the user load a sample pantry. |

## How to run the app

1. Download or clone the project. If you downloaded a ZIP, extract it first.
2. Open **Android Studio → File → Open** and select the **SmartPantryManager** folder containing `settings.gradle` and `app/`.
3. Wait for Gradle to sync. Install **Android SDK Platform 35** and **Build-Tools 34.0.0** if prompted. Select **JDK 17** in the Gradle settings if needed.
4. Select an emulator or connected Android device running **Android 8.0 (API 26)** or newer.
5. Select the **app** run configuration and click **Run ▶**.

On first launch, the app loads 20 recipes and starts with an empty pantry. To try it quickly, open **Settings → Load sample pantry**, confirm, and open **Recipes**. The sample contains **9 ingredient batches** and produces **6 matching recipes**.

## Why SQLite?

SQLite suits this project because a personal pantry is a small collection of records that can be stored directly on the device. The user can add, read, update and delete ingredients offline, and the records remain available after closing and reopening the app.

`DatabaseHelper.java` uses `SQLiteOpenHelper` to create three tables:

| Table | What it stores |
| --- | --- |
| `pantry_items` | The user’s ingredients, quantities, units and optional expiry dates. |
| `recipes` | Recipe names, descriptions, cooking times and preparation steps. |
| `recipe_ingredients` | The ingredients, quantities and units required by each recipe. |

The database loads the recipe collection from `recipes.json` when it is first created. The expiry highlighting preference is saved separately using `SharedPreferences`.

## How recipe matching works

`RecipeMatcher.java` compares each recipe’s requirements with the available pantry stock:

1. Standardise common ingredient names. For example, `tomato` and `tomatoes` match.
2. Convert compatible units. For example, `0.5 kg` of rice becomes `500 g`.
3. Combine quantities from separate batches of the same ingredient in compatible units. Skip batches whose expiry date has passed.
4. Check every required ingredient. If one ingredient is missing or there is too little of it, leave the recipe out of the suggestions.

For example, **Tomato rice** needs **150 g of rice** and **2 tomatoes**, along with water, oil and salt. With the sample pantry loaded, changing Tomatoes from **2 pieces to 1** removes Tomato rice from the suggested list. Restoring **2 pieces** makes it appear again.

## Project structure

| Location | Purpose |
| --- | --- |
| `app/src/main/java/com/smartpantry/app/ui/` | Screen behaviour, navigation and adapters that display list rows. |
| `app/src/main/java/com/smartpantry/app/data/` | SQLite setup, recipe loading and pantry database operations. |
| `app/src/main/java/com/smartpantry/app/logic/` | Recipe matching, name normalisation, unit conversion and expiry checks. |
| `app/src/main/java/com/smartpantry/app/model/` | Java classes for pantry ingredients, recipes and recipe requirements. |
| `app/src/main/res/layout/` | XML layouts for screens and list rows. |
| `app/src/main/assets/recipes.json` | The 20 recipes loaded into SQLite. |

## Testing

To test the app manually, add an ingredient and confirm it appears in the pantry. Edit its quantity, close and reopen the app, and check that the edited value is still there. Then delete the ingredient and confirm it disappears. Use the Tomato rice example above to test the recipe matching rule.

To run the automated matching tests, open a terminal in the project folder with **Python 3** and **JDK 17** available:

```bash
python tests/run_tests.py
```

The tests cover missing ingredients, insufficient quantities, unit conversions, common ingredient names, expired batches and all 20 recipes.

