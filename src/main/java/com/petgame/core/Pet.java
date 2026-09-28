package com.petgame.core;

public class Pet {
    private final String name;
    private Stats stats;
    private PetState state;

    public Pet(String name, Stats stats) {
        this.name = name;
        this.stats = stats;
        this.state = PetState.HEALTHY;
        recomputeState();
    }

    public String getName() { return name; }
    public Stats getStats() { return stats; }
    public PetState getState() { return state; }

    /** Apply raw deltas (can be negative). Clamping happens inside Stats after addition. */
    public void applyDelta(int dHunger, int dHappiness, int dEnergy, int dHygiene) {
        this.stats = this.stats.add(dHunger, dHappiness, dEnergy, dHygiene);
        recomputeState();
    }

    /** Time passes: hunger↑, happiness↓, energy↓, hygiene↓ (since bigger hygiene = cleaner). */
    public void tickDecay() {
        applyDelta(+5, -2, -2, -1);
    }

    private void recomputeState() {
        if (stats.hunger >= 100 || stats.energy <= 0) { state = PetState.DEAD; return; }
        if (stats.hunger > 70 || stats.happiness < 30 || stats.energy < 25 || stats.hygiene < 20) {
            state = PetState.NEEDS_ATTENTION; return;
        }
        if (stats.happiness < 10 || stats.hygiene < 10) { state = PetState.SICK; return; }
        state = PetState.HEALTHY;
    }

    @Override public String toString() {
        return "Pet{name='" + name + "', state=" + state + ", stats=" + stats + '}';
    }
}
