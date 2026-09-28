package com.petgame.core;

public interface Action {
    String name();
    void execute(Pet pet);
}
