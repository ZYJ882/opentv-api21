package cc.opentv.api21;

import static org.junit.Assert.assertEquals;
import java.io.StringReader;
import java.util.List;
import org.junit.Test;

public class M3uParserTest {
    @Test public void parsesChannelNameGroupAndStream() throws Exception {
        String m3u = "#EXTM3U\n#EXTINF:-1 tvg-logo=\"https://example.com/logo.png\" group-title=\"News\",Sample News\nhttps://example.com/live.m3u8\n";
        List<Channel> channels = M3uParser.parse(new StringReader(m3u));
        assertEquals(1, channels.size()); assertEquals("Sample News", channels.get(0).name); assertEquals("News", channels.get(0).group); assertEquals("https://example.com/live.m3u8", channels.get(0).streamUrl);
    }
}
