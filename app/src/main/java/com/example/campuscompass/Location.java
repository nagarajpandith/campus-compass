package com.example.campuscompass;

import java.util.ArrayList;

public class Location {
    private String name;
    private ArrayList<String> places;

    private ArrayList<Integer>placePositions;
    private int image;
    private int level;
    private boolean isStairs;
    private float stairsAngle;
    private Location stairs;
    private Location left;
    private Location right;
    private Location front;
    private Location back;
    private boolean inRoute;

    private Location up;
    private Location down;

    private float upAngle;
    private float downAngle;
    private float angle;

    public Location(String name, ArrayList<String> places,ArrayList<Integer> placePositions, int image, int level, boolean isStairs, float stairsAngle, Location stairs, Location left, Location right, Location front, Location back) {
        this.name = name;
        this.places = places;
        this.image = image;
        this.level = level;
        this.isStairs = isStairs;
        this.stairsAngle = stairsAngle;
        this.stairs = stairs;
        this.left = left;
        this.right = right;
        this.front = front;
        this.back = back;
        this.placePositions=placePositions;
        this.inRoute=false;
        this.angle=0;
        this.up=null;
        this.down=null;
        this.upAngle=0;
        this.downAngle=0;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getPlaces() {
        return places;
    }
    public ArrayList<Integer> getPlacesPositions() {
        return placePositions;
    }

    public int getImage() {
        return image;
    }

    public int getLevel() {
        return level;
    }

    public boolean isStairs() {
        return isStairs;
    }

    public Location getLeft() {
        return left;
    }

    public void setLeft(Location left) {
        this.left = left;
    }

    public Location getRight() {
        return right;
    }

    public void setRight(Location right) {
        this.right = right;
    }

    public Location getFront() {
        return front;
    }

    public void setFront(Location front) {
        this.front = front;
    }

    public Location getBack() {
        return back;
    }

    public void setBack(Location back) {
        this.back = back;
    }

    public void setStairs(Location stairs){this.stairs=stairs;}
    public Location getStairs(){return this.stairs;}
    public void setStairsAngle(float angle){this.stairsAngle=angle;}
    public float getStairsAngle(){return this.stairsAngle;}

    public void setInRoute(boolean set){this.inRoute=set;}
    public boolean getInRoute(){return this.inRoute;}

    public void setUp(Location up){this.up=up;}
    public Location getUp(){return this.up;}

    public void setDown(Location down){this.down=down;}
    public Location getDown(){return this.down;}

    public void setAngle(float angle){this.angle=angle;}

    public float getAngle() {return angle;}

    public void setUpAngle(float upAngle){this.upAngle=upAngle;}
    public float getUpAngle(){return this.upAngle;}

    public void setDownAngle(float downAngle){this.downAngle=downAngle;}
    public float getDownAngle(){return this.downAngle;}
}

