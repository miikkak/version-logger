package net.guesswhoami.versionlogger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.Player;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

class VersionLoggerPluginTest {

    @Test
    void logsUsernameUuidAndProtocolVersionOnLogin() {
        Logger logger = mock(Logger.class);
        Player player = mock(Player.class);
        UUID uuid = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
        when(player.getUsername()).thenReturn("playername");
        when(player.getUniqueId()).thenReturn(uuid);
        when(player.getProtocolVersion()).thenReturn(ProtocolVersion.MINECRAFT_1_21_4);

        VersionLoggerPlugin plugin = new VersionLoggerPlugin(logger);
        plugin.onPostLogin(new PostLoginEvent(player));

        verify(logger)
                .info(
                        "{} ({}) connected with protocol {} ({})",
                        "playername",
                        uuid,
                        ProtocolVersion.MINECRAFT_1_21_4.getProtocol(),
                        ProtocolVersion.MINECRAFT_1_21_4.getMostRecentSupportedVersion());
    }

    @Test
    void sanitizesCarriageReturnsAndNewlinesInUsername() {
        Logger logger = mock(Logger.class);
        Player player = mock(Player.class);
        UUID uuid = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
        when(player.getUsername()).thenReturn("evil\r\nname\n");
        when(player.getUniqueId()).thenReturn(uuid);
        when(player.getProtocolVersion()).thenReturn(ProtocolVersion.MINECRAFT_1_21_4);

        VersionLoggerPlugin plugin = new VersionLoggerPlugin(logger);
        plugin.onPostLogin(new PostLoginEvent(player));

        verify(logger)
                .info(
                        "{} ({}) connected with protocol {} ({})",
                        "evil__name_",
                        uuid,
                        ProtocolVersion.MINECRAFT_1_21_4.getProtocol(),
                        ProtocolVersion.MINECRAFT_1_21_4.getMostRecentSupportedVersion());
    }
}
