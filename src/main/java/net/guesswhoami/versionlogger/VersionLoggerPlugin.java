package net.guesswhoami.versionlogger;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import org.slf4j.Logger;

@Plugin(
        id = "version-logger",
        name = "version-logger",
        version = BuildInfo.VERSION,
        description = "Logs each player's client protocol version alongside their username and UUID on login",
        authors = {"miikkak"})
public class VersionLoggerPlugin {

    private final Logger logger;

    @Inject
    public VersionLoggerPlugin(Logger logger) {
        this.logger = logger;
    }

    // Fires once the player is authenticated (Mojang online-mode auth already happened by this
    // point) and fully connected to the proxy, but before Velocity picks a backend server - so
    // this always logs, regardless of whether the player ends up routed to a lobby or to limbo
    // for being on an outdated client.
    @Subscribe
    public void onPostLogin(PostLoginEvent event) {
        Player player = event.getPlayer();
        ProtocolVersion version = player.getProtocolVersion();
        logger.info(
                "{} ({}) connected with protocol {} ({})",
                player.getUsername(),
                player.getUniqueId(),
                version.getProtocol(),
                version.getMostRecentSupportedVersion());
    }
}
