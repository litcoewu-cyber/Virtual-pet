package com.petgame.core;

import java.util.Objects;

/**
 * 封装宠物的四个属性
 * 0 = 最差，100 = 最好
 */
public final class Stats {
    public final int hunger;     // 饥饿：0 表示很饱，100 表示饿死
    public final int happiness;  // 快乐：0 = 抑郁，100 = 开心
    public final int energy;     // 体力：0 = 累坏，100 = 精力充沛
    public final int hygiene;    // 卫生：0 = 脏，100 = 干净

    public Stats(int hunger, int happiness, int energy, int hygiene) {
        this.hunger = clamp(hunger);
        this.happiness = clamp(happiness);
        this.energy = clamp(energy);
        this.hygiene = clamp(hygiene);
    }

    //use "clamp" to ensure that inputs < 0 will return 0 and inputs >100 will return 100
    private static int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }

    /** 返回一个“加上增量后的新 Stats” */
    public Stats add(int dhunger, int dhappiness, int denergy, int dhygiene) {
        return new Stats(hunger + dhunger,
                         happiness + dhappiness,
                         energy + denergy,
                         hygiene + dhygiene);
    }

    @Override
    public String toString() {
        return "Stats{" +
                "hunger=" + hunger +
                ", happiness=" + happiness +
                ", energy=" + energy +
                ", hygiene=" + hygiene +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stats)) return false;
        Stats stats = (Stats) o;
        return hunger == stats.hunger &&
               happiness == stats.happiness &&
               energy == stats.energy &&
               hygiene == stats.hygiene;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hunger, happiness, energy, hygiene);
    }
}
