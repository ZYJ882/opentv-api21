package cc.opentv.api21;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class M3uParser {
    private static final Pattern ATTRIBUTE = Pattern.compile("([A-Za-z0-9-]+)=\"([^\"]*)\"");
    private M3uParser() {}
    public static List<Channel> parse(Reader reader) throws IOException {
        List<Channel> channels = new ArrayList<>(); BufferedReader buffered = new BufferedReader(reader);
        String pendingName = null, pendingGroup = "未分组", pendingLogo = null, line;
        while ((line = buffered.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("#EXTINF")) { int comma = line.indexOf(','); pendingName = comma >= 0 ? line.substring(comma + 1).trim() : "未命名频道"; pendingGroup = attribute(line, "group-title", "未分组"); pendingLogo = attribute(line, "tvg-logo", null); }
            else if (!line.isEmpty() && !line.startsWith("#") && pendingName != null) { channels.add(new Channel(pendingName, line, pendingGroup, pendingLogo)); pendingName = null; pendingGroup = "未分组"; pendingLogo = null; }
        }
        return channels;
    }
    private static String attribute(String line, String name, String fallback) { Matcher matcher = ATTRIBUTE.matcher(line); while (matcher.find()) if (name.equals(matcher.group(1))) return matcher.group(2); return fallback; }
}
