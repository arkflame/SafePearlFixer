package com.arkflame.flamepearls.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class LocationUtil {
    // Check if the type is AIR or CARPET
    public static boolean isSafe(Material type) {
        if (type == null) {
            return true;
        }

        String typeName = type.name();
        return 
                type == Material.AIR ||
                typeName.equals("REDSTONE") ||
                typeName.equals("TRIPWIRE_HOOK") ||
                typeName.endsWith("PRESSURE_PLATE") ||
                typeName.equals("TALL_GRASS") ||
                typeName.equals("LONG_GRASS") ||
                typeName.endsWith("CARPET");
    }

    // A helper method that finds the nearest safest location from a given location,
    // origin and world
    public static Location findSafeLocation(Location location, Location origin, World world) {
        // Clone the original location
        Location clone = location.clone();
        Block originalBlock = location.getBlock();

        // Check if location is already safe
        if (isSafe(originalBlock.getType()) && isSafe(clone.add(0, 1, 0).getBlock().getType())) {
            // Return the fixed location
            return originalBlock.getLocation().add(0.5, 0, 0.5);
        }

        // Get the coordinates of the location
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        // Initialize a variable to store the minimum distance to a safe block
        double minDistance = Double.MAX_VALUE;
        // Initialize a variable to store the best safe location
        Location bestLocation = null;
        // Loop through a 3x3 square around the location on the same y level
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                // Get the block at the offset coordinates
                Material block = world.getBlockAt(x + dx, y, z + dz).getType();
                // Check if the block is air or water
                if (!block.isSolid()) {
                    // Create a new location with the offset coordinates and the same pitch and yaw
                    // as the original location
                    Location newLocation = new Location(world, x + dx + 0.5, y, z + dz + 0.5, location.getYaw(),
                            location.getPitch());
                    // Calculate the distance between the new location, the original and the origin
                    double distance = newLocation.distance(location) + newLocation.distance(origin);
                    // Check if the distance is smaller than or equal to the minimum distance
                    if (distance <= minDistance) {
                        // Update the minimum distance and the best location
                        minDistance = distance;
                        bestLocation = newLocation;
                    }
                }
            }
        }
        // If a safe location is found, return it
        if (bestLocation != null) {
            // Floor the Y value of the best location
            bestLocation.setY(Math.floor(bestLocation.getY()));

            return findBestY(bestLocation);
        }
        // If no safe location is found, return the original location
        return findBestY(location);
    }

    // The location is foot location.
    static Location findBestY(Location location) {
        if (location.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
            Location downLocation = location.clone().subtract(0, 1, 0);

            if (!downLocation.getBlock().getType().isSolid())
                return downLocation;
        }

        return location;
    }
}
