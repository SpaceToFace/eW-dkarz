package com.example.test;

import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class AtlasItems {

    String atlasTitle;
    String atlasProtection;
    String atlasSize;
    int atlasImage;
    public AtlasItems(String atlasTitle, String atlasProtection, String atlasSize, int atlasImage) {
        this.atlasTitle = atlasTitle;
        this.atlasProtection = atlasProtection;
        this.atlasSize = atlasSize;
        this.atlasImage = atlasImage;
    }

    //gettery i settery
    public String getAtlasTitle() {
        return atlasTitle;
    }

    public void setAtlasTitle(String atlasTitle) {
        this.atlasTitle = atlasTitle;
    }

    public String getAtlasProtection() {
        return atlasProtection;
    }

    public void setAtlasProtection(String atlasProtection) {
        this.atlasProtection = atlasProtection;
    }

    public String getAtlasSize() {
        return atlasSize;
    }

    public void setAtlasSize(String atlasSize) {
        this.atlasSize = atlasSize;
    }

    public int getAtlasImage() {
        return atlasImage;
    }

    public void setAtlasImage(int atlasImage) {
        this.atlasImage = atlasImage;
    }
}
