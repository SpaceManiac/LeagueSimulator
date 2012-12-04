package com.platymuus.lolsim;

import com.platymuus.lolsim.matchmaking.Team;
import com.platymuus.lolsim.players.Summoner;

import java.util.Collection;
import java.util.Map;
import java.util.Random;

/**
 * A Random provided with additional utility methods for the simulation.
 */
public class SimRandom extends Random {

    public SimRandom() {
    }

    public SimRandom(long seed) {
        super(seed);
    }

    /**
     * Randomly calculates whether an event should occur this tick based on the mean number of ticks until that event.
     *
     * @param mean The mean time it should take for an event to occur, in ticks.
     * @return Whether the event should occur this tick.
     */
    public boolean nextTickChance(double mean) {
        return nextDouble() < tickChanceByTime(mean);
    }

    /**
     * Calculate the per-tick chance of an event based on the mean number of ticks until that event.
     *
     * @param mean The mean time it should take for an event to occur, in seconds.
     * @return The random chance that the event should occur each tick.
     */
    private double tickChanceByTime(double mean) {
        // k = 0.5 = % chance that event will have occurred by the mean
        // k = 1 - (1 - p)^n
        // 1 - k = (1 - p)^n
        // n\(1-k) = 1 - p
        // n\(1-k) - 1 = -p
        // p = 1 - n root (1-k)
        return 1 - Math.pow(1 - 0.5, 1 / mean);
    }

    public static String generateName() {
        String templates[] = {
                "Cvc",
                "Cvcc",
                "Ccvcv Vc'Ccvvcv",
                "Ccvcvc CvcCcvccvc",
                "Ccvcvcvcvvc",
                "Ccvcccvcc",
                "Cvccvc",
                "Cvcvc",
                "Cvcccvc",
                "Cvcccc",
                "Vcvcvvc",
                "Vcvcv",
                "Cvcn",
                "Cvcnn",
                "CnC",
                "Cvcvccvcvv",
                "Cvccvc",
                "Cvcvc",
                "Cvcccvc",
                "Cvc",
                "Cvcc",
        };
        String consonants = "bcdfghijklmnpqrstvwxyz";
        String vowels = "aeiouy";
        String numbers = "123456789";

        String template = templates[(int) (Math.random() * templates.length)];
        String result = "";
        for (char ch : template.toCharArray()) {
            if (ch == 'C') {
                result += Character.toUpperCase(randChar(consonants));
            } else if (ch == 'c') {
                result += randChar(consonants);
            } else if (ch == 'V') {
                result += Character.toUpperCase(randChar(vowels));
            } else if (ch == 'v') {
                result += randChar(vowels);
            } else if (ch == 'n') {
                result += randChar(numbers);
            } else {
                result += ch;
            }
        }
        return result;
    }

    private static char randChar(String chars) {
        return chars.charAt((int) (Math.random() * chars.length()));
    }
    
    public <T> T weightedRandom(final Map<T, ? extends Number> items) {
        double total = 0;
        for (Number n : items.values()) {
            total += n.doubleValue();
        }

        double random = nextDouble() * total;
        total = 0;
        for (Map.Entry<T, ? extends Number> entry : items.entrySet()) {
            total += entry.getValue().doubleValue();
            if (random < total) return entry.getKey();
        }
        return items.entrySet().iterator().next().getKey(); // should never happen
    }

}
