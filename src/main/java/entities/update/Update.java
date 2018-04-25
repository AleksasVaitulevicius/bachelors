package entities.update;

import entities.network.Network;

public interface Update {
    void applyTo(Network network) throws Exception;
}
