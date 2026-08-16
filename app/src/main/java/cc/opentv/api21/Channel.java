package cc.opentv.api21;

public final class Channel {
    public final String name;
    public final String streamUrl;
    public final String group;
    public final String logoUrl;
    public Channel(String name, String streamUrl, String group, String logoUrl) { this.name = name; this.streamUrl = streamUrl; this.group = group; this.logoUrl = logoUrl; }
}
