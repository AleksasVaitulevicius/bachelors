package entities;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum UpdateType {
    ADD_VERTEX, REMOVE_VERTEX, ADD_EDGE, REMOVE_EDGE, UPDATE_WEIGHT;

    private static final List<UpdateType> VALUES = Collections.unmodifiableList(List.of(values()));

    public static UpdateType random(List<UpdateType> values)  {
        return values.get(new Random().nextInt(values.size()));
    }
}