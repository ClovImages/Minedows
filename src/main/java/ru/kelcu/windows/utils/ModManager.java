package ru.kelcu.windows.utils;

import ru.kelcu.windows.components.Action;

import java.util.ArrayList;
import java.util.HashMap;

public class ModManager {
    public static HashMap<Actions, Boolean> actions = new HashMap<>();
    public static void registerModActions(Actions modActions, boolean isMenu){
        actions.put(modActions, isMenu);
    }
    public static void registerModAction(Action action, boolean isMenu){
        registerModActions(() -> {
            ArrayList<Action> actions1 = new ArrayList<>(); actions1.add(action); return actions1;
            }, isMenu);
    }
    public static ArrayList<Action> getActions(boolean isMenu){
        ArrayList<Action> fuckingShit = new ArrayList<>();
        for(Actions actions1 : actions.keySet()){
            if(isMenu == actions.get(actions1)){
                fuckingShit.addAll(actions1.labels());
            }
        }
        return fuckingShit;
    }
    public interface Actions {
        // Action / Category
        ArrayList<Action> labels();
    }
}
