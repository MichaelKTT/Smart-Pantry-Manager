package com.smartpantry.app.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.smartpantry.app.R;
import com.smartpantry.app.logic.DateUtils;
import com.smartpantry.app.model.Ingredient;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class PantryAdapter extends BaseAdapter {
    public interface Actions { void edit(Ingredient item); void delete(Ingredient item); }
    private final Context context;
    private final Actions actions;
    private final List<Ingredient> items = new ArrayList<>();
    private boolean expiryHints;

    public PantryAdapter(Context context, Actions actions) { this.context = context; this.actions = actions; }

    public void submit(List<Ingredient> rows, boolean expiryHints) {
        items.clear(); items.addAll(rows); this.expiryHints = expiryHints; notifyDataSetChanged();
    }
    @Override public int getCount() { return items.size(); }
    @Override public Ingredient getItem(int position) { return items.get(position); }
    @Override public long getItemId(int position) { return getItem(position).id; }
    @Override public boolean hasStableIds() { return true; }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView == null ? LayoutInflater.from(context).inflate(R.layout.row_pantry, parent, false) : convertView;
        Ingredient item = getItem(position);
        ((TextView) row.findViewById(R.id.itemName)).setText(item.name);
        ((TextView) row.findViewById(R.id.itemQuantity)).setText(Ui.amount(item.quantity, item.unit));
        ((TextView) row.findViewById(R.id.itemInitial)).setText(item.name.substring(0, 1).toUpperCase(Locale.ROOT));
        TextView expiry = row.findViewById(R.id.itemExpiry);
        String today = DateUtils.today();
        boolean expired = DateUtils.expired(item.expiryDate, today);
        if (item.expiryDate == null || item.expiryDate.isEmpty()) expiry.setText(R.string.no_expiry);
        else if (expired) expiry.setText(context.getString(R.string.expired_date, item.expiryDate));
        else if (expiryHints && DateUtils.soon(item.expiryDate, today))
            expiry.setText(context.getString(R.string.use_soon_date, item.expiryDate));
        else expiry.setText(context.getString(R.string.expiry_date, item.expiryDate));
        expiry.setTextColor(context.getColor(expired ? R.color.danger : R.color.text_secondary));
        row.findViewById(R.id.editItem).setOnClickListener(v -> actions.edit(item));
        row.findViewById(R.id.deleteItem).setOnClickListener(v -> actions.delete(item));
        row.findViewById(R.id.editItem).setContentDescription(context.getString(R.string.edit_named, item.name));
        row.findViewById(R.id.deleteItem).setContentDescription(context.getString(R.string.delete_named, item.name));
        return row;
    }
}
