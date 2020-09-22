package me.tecnio.antihaxerman.playerdata;

import me.tecnio.antihaxerman.utils.CustomLocation;
import me.tecnio.antihaxerman.utils.EvictingList;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class EntityTracker {
    public EvictingList<CustomLocation> tracker = new EvictingList<>(40);

    public List<Vector> getPredictedLocation(long time) {
        List<CustomLocation> locs = new ArrayList<>();
        tracker.stream().sorted(Comparator.comparingLong(loc -> Math.abs(loc.getTimeStamp() - (System.currentTimeMillis() - time)))).filter(loc -> Math.abs(loc.getTimeStamp() - (System.currentTimeMillis() - time)) < 150).forEach(locs::add);
        return locs.stream().map(CustomLocation::toVector).collect(Collectors.toList());
    }

    public void addLocation(Location location) {
        tracker.add(new CustomLocation(location));
    }
}
