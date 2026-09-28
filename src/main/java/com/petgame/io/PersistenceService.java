package com.petgame.io;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petgame.core.Pet;
import com.petgame.core.Stats;

public class PersistenceService {
    private final ObjectMapper om = new ObjectMapper();

    public void save(Pet pet, String path) {
        try {
            Map<String, Object> m = new HashMap<>();
            m.put("name", pet.getName());
            m.put("state", pet.getState().name());
            Stats s = pet.getStats();
            Map<String, Integer> st = new HashMap<>();
            st.put("hunger", s.hunger);
            st.put("happiness", s.happiness);
            st.put("energy", s.energy);
            st.put("hygiene", s.hygiene);
            m.put("stats", st);
            om.writerWithDefaultPrettyPrinter().writeValue(new File(path), m);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save: " + path, e);
        }
    }

    public Pet load(String path) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> m = om.readValue(new File(path), Map.class);
            String name = (String) m.get("name");

            @SuppressWarnings("unchecked")
            Map<String, Object> st = (Map<String, Object>) m.get("stats");
            int hunger    = ((Number) st.get("hunger")).intValue();
            int happiness = ((Number) st.get("happiness")).intValue();
            int energy    = ((Number) st.get("energy")).intValue();
            int hygiene   = ((Number) st.get("hygiene")).intValue();

            // 仅根据属性恢复，state 会在 Pet 构造中自动计算
            return new Pet(name, new Stats(hunger, happiness, energy, hygiene));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load: " + path, e);
        }
    }

}
