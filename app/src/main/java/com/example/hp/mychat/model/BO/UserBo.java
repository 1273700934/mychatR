package com.example.hp.mychat.model.BO;

import android.graphics.drawable.Icon;
import android.media.Image;

import com.example.hp.mychat.model.User;

public class UserBo extends User {
    private Image photo;

    public Image getPhoto() {
        return photo;
    }

    public void setPhoto(Image photo) {
        this.photo = photo;
    }
}
