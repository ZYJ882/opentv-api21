package cc.opentv.api21;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public final class ChannelRepository {
    public List<Channel> loadFromUrl(String value) throws Exception {
        URL url = new URL(value);
        if (!"http".equals(url.getProtocol()) && !"https".equals(url.getProtocol())) throw new IllegalArgumentException("仅允许 http 或 https 源");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(); connection.setConnectTimeout(10000); connection.setReadTimeout(15000); connection.setRequestProperty("User-Agent", "OpenTV-API21/1.0");
        try { if (connection.getResponseCode() < 200 || connection.getResponseCode() >= 300) throw new IllegalStateException("订阅源返回 HTTP " + connection.getResponseCode()); return M3uParser.parse(new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))); }
        finally { connection.disconnect(); }
    }
}

