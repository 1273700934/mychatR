package com.example.hp.mychat.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hp.mychat.R;
import com.example.hp.mychat.model.BO.UserBo;
import com.example.hp.mychat.model.User;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class myInfoAdapter extends BaseAdapter {
    private Context context;
    private List<User> userBoList;

    public myInfoAdapter(Context context,List<User> userBoList){
        super();
        this.context=context;
        this.userBoList=userBoList;
    }

    @Override
    public int getCount() {
        return userBoList.size();
    }

    @Override
    public Object getItem(int i) {
        return userBoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //String photoPath = "/drawable/myicon.png";
        //Drawable drawable = loadImage(photoPath);
         //int resID = context.getResources(photoName,"drawable",context.getPackageName());
        //Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
        //Drawable drawable = context.getResources().getDrawable(resID);
        //userBo.setPhoto(bitmap);
        HolderView holderView = null;
        User userBo = userBoList.get(i);
        if(holderView == null ){
            holderView = new HolderView();
            view = View.inflate(context, R.layout.my_info_item,null);
            holderView.my_info_message = view.findViewById(R.id.my_info_message);
            holderView.my_info_message.setText(userBo.getIp()+"hello");
            holderView.my_info_image = view.findViewById(R.id.my_info_image);
            holderView.my_info_image.setImageDrawable(context.getDrawable(R.drawable.myicon));
            view.setTag(holderView);
        }else {
            holderView = (HolderView) view.getTag();
        }
        return view;
    }

    class HolderView {
        TextView my_info_message;
        ImageView my_info_image;
    }
    private Drawable loadImage(String path){
        Drawable drawable = null;
            try {
                drawable = Drawable.createFromStream(new URL(path).openStream(),"myicon.png");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  drawable;
    }
}
