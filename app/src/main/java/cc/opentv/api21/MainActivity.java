package cc.opentv.api21;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.LinearLayoutManager;
import cc.opentv.api21.databinding.ActivityMainBinding;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MainActivity extends AppCompatActivity implements ChannelAdapter.Listener {
    private ActivityMainBinding binding; private ExoPlayer player; private ChannelAdapter adapter;
    private final ExecutorService network = Executors.newSingleThreadExecutor(); private final Handler ui = new Handler(Looper.getMainLooper());
    private final ChannelRepository repository = new ChannelRepository(); private SharedPreferences preferences; private List<Channel> channels = Collections.emptyList(); private int selectedIndex = -1;
    @Override protected void onCreate(@Nullable Bundle state) {
        super.onCreate(state); binding = ActivityMainBinding.inflate(getLayoutInflater()); setContentView(binding.getRoot()); preferences = getSharedPreferences("opentv", MODE_PRIVATE);
        player = new ExoPlayer.Builder(this).build(); binding.playerView.setPlayer(player); adapter = new ChannelAdapter(this); binding.channelList.setLayoutManager(new LinearLayoutManager(this)); binding.channelList.setAdapter(adapter);
        binding.loadButton.setOnClickListener(v -> loadPlaylist());
        binding.searchInput.addTextChangedListener(new TextWatcher() { public void beforeTextChanged(CharSequence s,int a,int b,int c){} public void onTextChanged(CharSequence s,int a,int b,int c){ adapter.filter(s.toString()); } public void afterTextChanged(Editable e){} });
    }
    private void loadPlaylist() {
        final String url = binding.sourceInput.getText().toString().trim(); if (!url.startsWith("http://") && !url.startsWith("https://")) { toast("请输入 http 或 https M3U 地址"); return; }
        binding.loadButton.setEnabled(false); binding.emptyView.setText("正在加载播放列表…");
        network.execute(() -> { try { final List<Channel> loaded = repository.loadFromUrl(url); ui.post(() -> showChannels(loaded)); } catch (final Exception error) { ui.post(() -> { binding.loadButton.setEnabled(true); binding.emptyView.setText("加载失败：" + error.getMessage()); }); } });
    }
    private void showChannels(List<Channel> loaded) { channels = loaded; selectedIndex = -1; adapter.setChannels(loaded); binding.loadButton.setEnabled(true); binding.emptyView.setVisibility(loaded.isEmpty() ? View.VISIBLE : View.GONE); if (loaded.isEmpty()) binding.emptyView.setText("未从该地址解析出 M3U 频道"); else { binding.channelList.requestFocus(); toast("已加载 " + loaded.size() + " 个频道"); } }
    @Override public void onSelected(Channel channel) { selectedIndex = channels.indexOf(channel); binding.nowPlaying.setText("正在播放：" + channel.name); player.setMediaItem(MediaItem.fromUri(channel.streamUrl)); player.prepare(); player.play(); }
    @Override public void onFavoriteToggled(Channel channel) { Set<String> set = new HashSet<>(preferences.getStringSet("favorites", Collections.emptySet())); if (!set.add(channel.streamUrl)) set.remove(channel.streamUrl); preferences.edit().putStringSet("favorites", set).apply(); toast(isFavorite(channel) ? "已收藏" : "已取消收藏"); adapter.notifyDataSetChanged(); }
    @Override public boolean isFavorite(Channel channel) { return preferences.getStringSet("favorites", Collections.emptySet()).contains(channel.streamUrl); }
    @Override public boolean onKeyDown(int keyCode, KeyEvent event) { if (channels.isEmpty()) return super.onKeyDown(keyCode,event); if (keyCode == KeyEvent.KEYCODE_DPAD_UP) { playRelative(-1); return true; } if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) { playRelative(1); return true; } if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) { int index = keyCode - KeyEvent.KEYCODE_0 - 1; if (index >= 0 && index < channels.size()) onSelected(channels.get(index)); return true; } return super.onKeyDown(keyCode,event); }
    private void playRelative(int delta) { if (selectedIndex < 0) selectedIndex = 0; else selectedIndex = (selectedIndex + delta + channels.size()) % channels.size(); onSelected(channels.get(selectedIndex)); }
    private void toast(String text) { Toast.makeText(this, text, Toast.LENGTH_SHORT).show(); }
    @Override protected void onStop() { super.onStop(); player.pause(); }
    @Override protected void onDestroy() { network.shutdownNow(); player.release(); super.onDestroy(); }
}
