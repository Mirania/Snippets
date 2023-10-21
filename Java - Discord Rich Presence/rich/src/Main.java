import de.jcm.discordgamesdk.Core;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import data.SystemTrayRichPresenceEntry;

public class Main {

    // for example: https://discord.com/developers/applications/1105273444427378739/rich-presence/assets
    private static Collection<SystemTrayRichPresenceEntry> richPresenceMenuEntries = List.of(
        SystemTrayRichPresenceEntry.builder()
                .setClientId(1105273444427378739L)
                .setMenuItemName("Honkai Impact")
                .addSubEntry(SystemTrayRichPresenceEntry.builder()
                        .setMenuItemName("Elysia")
                        .setLargeImage("logo2")
                        .setSmallImage("hoh")
                        .setSmallText("Herrscher of Human: Ego (Elysia)")
                        .build())
                .addSubEntry(SystemTrayRichPresenceEntry.builder()
                        .setMenuItemName("Nyx")
                        .setLargeImage("logo2")
                        .setSmallImage("nyx")
                        .setSmallText("Starchasm Nyx (Seele)")
                        .build())
                .addSubEntry(SystemTrayRichPresenceEntry.builder()
                        .setMenuItemName("HoV")
                        .setLargeImage("logo2")
                        .setSmallImage("void")
                        .setSmallText("Herrscher of the Void (Kiana)")
                        .build())
                .addSubEntry(SystemTrayRichPresenceEntry.builder()
                        .setMenuItemName("Sirin")
                        .setLargeImage("logo2")
                        .setSmallImage("sirin")
                        .setSmallText("Miracle ☆ Magical Girl (Sirin)")
                        .build())
                .build(),
            SystemTrayRichPresenceEntry.builder()
                    .setClientId(1165360100819611798L)
                    .setMenuItemName("Touhou Luna Nights")
                    .setLargeImage("logo")
                    .build()
    );

    public static void main(String[] args) {
        Core.init(Path.of("..", "sdk/lib/x86_64/discord_game_sdk.dll").toFile());
        SystemTrayApp.init(richPresenceMenuEntries);
    }
}