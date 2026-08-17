package cc.opentv.api21;

import java.net.URI;
import java.net.URISyntaxException;

/** Minimal navigation policy: only ordinary web URLs are rendered in the embedded browser. */
public final class WebUrlPolicy {
    private WebUrlPolicy() {}

    public static boolean isSupportedWebUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.trim().isEmpty()) return false;
        try {
            URI uri = new URI(rawUrl.trim());
            String scheme = uri.getScheme();
            return ("https".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme))
                    && uri.getHost() != null;
        } catch (URISyntaxException ignored) {
            return false;
        }
    }
}
