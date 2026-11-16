package com.smartpantry.app.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.smartpantry.app.R;
import com.smartpantry.app.logic.DateUtils;
import com.smartpantry.app.logic.IngredientNames;
import com.smartpantry.app.logic.UnitConverter;
import com.smartpantry.app.model.Ingredient;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Locale;

public final class IngredientFormActivity extends BaseActivity {
    public static final String EXTRA_ITEM_ID = "ingredient_id";
    private long itemId;
    private EditText nameInput, quantityInput, expiryInput;
    private Spinner unitInput;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        screen(R.layout.activity_ingredient_form, itemId < 0 ? R.string.add_ingredient : R.string.edit_ingredient,
                true, 0);
        nameInput = findViewById(R.id.nameInput);
        quantityInput = findViewById(R.id.quantityInput);
        expiryInput = findViewById(R.id.expiryInput);
        unitInput = findViewById(R.id.unitInput);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_selected, UnitConverter.UNITS);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        unitInput.setAdapter(adapter);
        findViewById(R.id.pickDate).setOnClickListener(v -> chooseDate());
        findViewById(R.id.clearDate).setOnClickListener(v -> expiryInput.setText(""));
        findViewById(R.id.saveIngredient).setOnClickListener(v -> save());
        if (itemId >= 0 && state == null) {
            Ingredient item = database.getIngredient(itemId);
            if (item == null) { Toast.makeText(this, R.string.item_not_found, Toast.LENGTH_SHORT).show(); finish(); return; }
            nameInput.setText(item.name);
            quantityInput.setText(String.format(Locale.US, "%s", item.quantity));
            unitInput.setSelection(Math.max(0, Arrays.asList(UnitConverter.UNITS).indexOf(item.unit)));
            expiryInput.setText(item.expiryDate == null ? "" : item.expiryDate);
        }
    }

    private void chooseDate() {
        String current = expiryInput.getText().toString();
        LocalDate date = !current.isEmpty() && DateUtils.valid(current) ? LocalDate.parse(current) : LocalDate.now();
        new DatePickerDialog(this, (picker, year, month, day) ->
                expiryInput.setText(String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)),
                date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth()).show();
    }

    private void save() {
        nameInput.setError(null); quantityInput.setError(null); expiryInput.setError(null);
        String name = nameInput.getText().toString().trim();
        String quantityText = quantityInput.getText().toString().trim().replace(',', '.');
        String expiry = expiryInput.getText().toString().trim();
        if (IngredientNames.canonical(name).isEmpty() || !name.matches(".*\\p{L}.*")) {
            nameInput.setError(getString(R.string.name_required)); nameInput.requestFocus(); return;
        }
        double quantity;
        try { quantity = Double.parseDouble(quantityText); }
        catch (NumberFormatException error) { quantity = Double.NaN; }
        if (!Double.isFinite(quantity) || quantity < 0.001 || quantity > 1000000) {
            quantityInput.setError(getString(R.string.quantity_error)); quantityInput.requestFocus(); return;
        }
        if (!DateUtils.valid(expiry)) {
            expiryInput.setError(getString(R.string.date_error)); expiryInput.requestFocus(); return;
        }
        Ingredient item = new Ingredient(itemId, name, quantity, unitInput.getSelectedItem().toString(), expiry);
        try {
            if (itemId < 0) database.insertIngredient(item);
            else if (!database.updateIngredient(item)) {
                Toast.makeText(this, R.string.item_not_found, Toast.LENGTH_LONG).show(); return;
            }
            Toast.makeText(this, R.string.item_saved, Toast.LENGTH_SHORT).show();
            finish();
        } catch (android.database.sqlite.SQLiteException error) {
            Toast.makeText(this, R.string.save_failed, Toast.LENGTH_LONG).show();
        }
    }
}
