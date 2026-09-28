package com.petgame.core.actions;

import com.petgame.core.Action;
import com.petgame.core.Pet;

public class Sleep implements Action {
    @Override public String name() { return "sleep"; }
    @Override public void execute(Pet pet) {
        pet.applyDelta(+5, +5, +35, 0);
    }
}
