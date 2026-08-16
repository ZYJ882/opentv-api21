package cc.opentv.api21;

import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public final class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.Holder> {
    public interface Listener { void onSelected(Channel channel); void onFavoriteToggled(Channel channel); boolean isFavorite(Channel channel); }
    private final List<Channel> all = new ArrayList<>();
    private final List<Channel> shown = new ArrayList<>();
    private final Listener listener;
    public ChannelAdapter(Listener listener) { this.listener = listener; setHasStableIds(true); }
    public void setChannels(List<Channel> channels) { all.clear(); all.addAll(channels); filter(""); }
    public void filter(String value) { shown.clear(); String needle = value == null ? "" : value.toLowerCase(); for (Channel c : all) if (c.name.toLowerCase().contains(needle) || c.group.toLowerCase().contains(needle)) shown.add(c); notifyDataSetChanged(); }
    @Override public long getItemId(int position) { return shown.get(position).streamUrl.hashCode(); }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) { TextView view = new TextView(parent.getContext()); view.setTextColor(Color.WHITE); view.setTextSize(16); view.setGravity(Gravity.CENTER_VERTICAL); view.setPadding(16, 14, 16, 14); view.setFocusable(true); view.setBackgroundResource(android.R.drawable.list_selector_background); return new Holder(view); }
    @Override public void onBindViewHolder(@NonNull Holder holder, int position) { Channel c = shown.get(position); holder.text.setText((listener.isFavorite(c) ? "★ " : "") + c.name + "\n" + c.group); holder.text.setOnClickListener(v -> listener.onSelected(c)); holder.text.setOnLongClickListener(v -> { listener.onFavoriteToggled(c); notifyItemChanged(holder.getBindingAdapterPosition()); return true; }); }
    @Override public int getItemCount() { return shown.size(); }
    static final class Holder extends RecyclerView.ViewHolder { final TextView text; Holder(TextView text) { super(text); this.text = text; } }
}
