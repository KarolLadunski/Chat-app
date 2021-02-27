package com.example.android.chatapp.Model;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class MarkerClusterRenderer extends DefaultClusterRenderer<MyItem> {

    @Override
    protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {
        if (item.getIcon() != null) {
            markerOptions.icon(item.getIcon()); //Here you retrieve BitmapDescriptor from ClusterItem and set it as marker icon
        }
        markerOptions.visible(true);
    }

    public MarkerClusterRenderer(Context context, GoogleMap googleMap, ClusterManager<MyItem> clusterManager){
        super(context, googleMap, clusterManager);
        clusterManager.setRenderer(this);
    }


    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return cluster.getSize() >= 2;
    }
}
