package entities.update;

import entities.Network;

public interface Update {
    void applyTo(Network network) throws Exception;
}
