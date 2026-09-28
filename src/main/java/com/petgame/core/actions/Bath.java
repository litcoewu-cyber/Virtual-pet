package com.petgame.core.actions;

import com.petgame.core.Action;
import com.petgame.core.Pet;

public class Bath implements Action {
    @Override public String name() { return "bath"; }
    @Override public void execute(Pet pet) {
        pet.applyDelta(+5, +5, -5, +40);
    }
}

