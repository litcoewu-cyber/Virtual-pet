package com.petgame.core.actions;

import com.petgame.core.Action;
import com.petgame.core.Pet;

public class Feed implements Action {
    @Override public String name() { return "feed"; }
    @Override public void execute(Pet pet) {
        pet.applyDelta(-25, +5, 0, 0);
    }
}

