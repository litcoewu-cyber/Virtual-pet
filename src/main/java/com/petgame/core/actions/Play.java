package com.petgame.core.actions;

import com.petgame.core.Action;
import com.petgame.core.Pet;

public class Play implements Action {
    @Override public String name() { return "play"; }
    @Override public void execute(Pet pet) {
        pet.applyDelta(+5, +20, -15, -5);
    }
}
