package entities.update;

import entities.DynamicNetwork;

public interface Update {
    void applyTo(DynamicNetwork network) throws Exception;
}
