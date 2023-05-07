package com.example.campuscompass;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ThirdFloor extends Fragment {
    View v;
    String []places = {"LH306", "LH308", "LH309", "LH309", "LH310", "LH311", "LH312", "EC Staffroom", "CS Staffroom", "Texas Instruments Lab"};
    TextView[][] nodes=new TextView[6][8];
    int[][] ids={
            {R.id.topleftNode0,R.id.topNode0,R.id.toprightNode0,R.id.rightNode0,R.id.bottomrightNode0,R.id.bottomNode0,R.id.bottomleftNode0,R.id.leftNode0},
            {R.id.topleftNode1,R.id.topNode1,R.id.toprightNode1,R.id.rightNode1,R.id.bottomrightNode1,R.id.bottomNode1,R.id.bottomleftNode1,R.id.leftNode1},
            {R.id.topleftNode2,R.id.topNode2,R.id.toprightNode2,R.id.rightNode2,R.id.bottomrightNode2,R.id.bottomNode2,R.id.bottomleftNode2,R.id.leftNode2},
            {R.id.topleftNode3,R.id.topNode3,R.id.toprightNode3,R.id.rightNode3,R.id.bottomrightNode3,R.id.bottomNode3,R.id.bottomleftNode3,R.id.leftNode3},
            {R.id.topleftNode4,R.id.topNode4,R.id.toprightNode4,R.id.rightNode4,R.id.bottomrightNode4,R.id.bottomNode4,R.id.bottomleftNode4,R.id.leftNode4},
            {R.id.topleftNode5,R.id.topNode5,R.id.toprightNode5,R.id.rightNode5,R.id.bottomrightNode5,R.id.bottomNode5,R.id.bottomleftNode5,R.id.leftNode5}
    };

    HashMap<Integer ,Integer> placesPositionMapping=new HashMap<>();
    Location locations[];
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_floor, container, false);

        placesPositionMapping.put(9,0);
        placesPositionMapping.put(8,1);
        placesPositionMapping.put(10,2);
        placesPositionMapping.put(2,3);
        placesPositionMapping.put(6,4);
        placesPositionMapping.put(4,5);
        placesPositionMapping.put(5,6);
        placesPositionMapping.put(1,7);

        Location node0 = LevelPointer.levels[3];
        Location node1 = node0.getLeft();
        Location node2 = node1.getBack();
        Location node3 = node2.getRight();
        Location node4 = node3.getRight();
        Location node5 = node4.getFront();
        Location stairs1=node0.getStairs();
        Location stairs2=node5.getStairs();

        locations=new Location[]{node0,node1,node2,node3,node4,node5};
        for(int i=0;i<6;i++){
            for(int j=0;j<8;j++){
                nodes[i][j] = v.findViewById(ids[i][j]);
            }
        }

        for(int i=0;i<6;i++){
            ArrayList<String> places=locations[i].getPlaces();
            ArrayList<Integer> placePositions=locations[i].getPlacesPositions();

            for(int j=0;j<places.size();j++){
//                Log.d("slkdf", "onCreateView: "+placesPositionMapping.get(placePositions.get(j))+" "+j);
                nodes[i][placesPositionMapping.get(placePositions.get(j))].setText(places.get(j).toString());
            }
        }


        return v;
    }

    public static int getImageResourceId(Context context, String imageName) {
        return context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
    }
}