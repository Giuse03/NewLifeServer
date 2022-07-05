package net.giuse.economymodule.serializer;

import net.giuse.economymodule.EconPlayer;
import net.giuse.mainmodule.serializer.Serializer;

public class EconPlayerSerializer implements Serializer<EconPlayer> {
    @Override
    public String encode(final EconPlayer econPlayer) {
        return econPlayer.getPlayer() + "," + econPlayer.getBalance();
    }

    @Override
    public EconPlayer decoder(final String str) {
        final String[] args = str.split(",");
        return new EconPlayer(args[0], Double.parseDouble(args[1]));
    }
}