package data;

import java.util.ArrayList;
import java.util.List;

public class SystemTrayRichPresenceEntry {

    public long clientId;

    public String menuItemName;

    public String details;

    public String state;

    public String largeImage;

    public String largeText;

    public String smallImage;

    public String smallText;

    public List<SystemTrayRichPresenceEntry> subEntries;

    private SystemTrayRichPresenceEntry() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {

        private long clientId;

        private String menuItemName;

        private String details;

        private String state;

        private String largeImage;

        private String largeText;

        private String smallImage;

        private String smallText;

        private List<SystemTrayRichPresenceEntry> subEntries = new ArrayList<>();

        public Builder setClientId(long clientId) { this.clientId = clientId; return this; }
        public Builder setMenuItemName(String menuItemName) { this.menuItemName = menuItemName; return this; }
        public Builder setDetails(String details) { this.details = details; return this; }
        public Builder setState(String state) { this.state = state; return this; }
        public Builder setLargeImage(String largeImage) { this.largeImage = largeImage; return this; }
        public Builder setLargeText(String largeText) { this.largeText = largeText; return this; }
        public Builder setSmallImage(String smallImage) { this.smallImage = smallImage; return this; }
        public Builder setSmallText(String smallText) { this.smallText = smallText; return this; }
        public Builder addSubEntry(SystemTrayRichPresenceEntry subEntry) { subEntry.clientId = this.clientId; this.subEntries.add(subEntry); return this; }

        public SystemTrayRichPresenceEntry build() {
            var built = new SystemTrayRichPresenceEntry();
            built.clientId = clientId;
            built.menuItemName = menuItemName;
            built.details = details;
            built.state = state;
            built.largeImage = largeImage;
            built.largeText = largeText;
            built.smallImage = smallImage;
            built.smallText = smallText;
            built.subEntries = subEntries.isEmpty() ? null : this.subEntries;
            return built;
        }

    }
}
