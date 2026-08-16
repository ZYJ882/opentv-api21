import cc.opentv.api21.Channel;
import cc.opentv.api21.M3uParser;
import java.io.StringReader;
import java.util.List;

public final class ParserSmokeTest {
    public static void main(String[] args) throws Exception {
        String input = "#EXTM3U\n#EXTINF:-1 group-title=\"News\",Sample News\nhttps://example.com/live.m3u8\n";
        List<Channel> channels = M3uParser.parse(new StringReader(input));
        if (channels.size() != 1 || !"Sample News".equals(channels.get(0).name) || !"News".equals(channels.get(0).group)) throw new AssertionError("M3U parser smoke test failed");
        System.out.println("M3U parser smoke test passed");
    }
}
