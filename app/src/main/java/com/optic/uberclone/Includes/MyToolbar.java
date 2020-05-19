package com.optic.uberclone.Includes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.optic.uberclone.R;

public class MyToolbar {

  public static void show(AppCompatActivity activity,String titulo, Boolean upboton)
  {
      Toolbar mtolbar = activity.findViewById(R.id.toollbar);
      activity.setSupportActionBar(mtolbar);
      activity.getSupportActionBar().setTitle(titulo);
      activity.getSupportActionBar().setDisplayHomeAsUpEnabled(upboton);
  }

}
