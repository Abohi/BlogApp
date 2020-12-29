package com.evansabohi.com.top.about;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.evansabohi.com.top.MainActivity;
import com.evansabohi.com.top.R;


public class EvansActivity extends AppCompatActivity {
  RadioGroup radioGroup;
    TextView ab_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evans);
        radioGroup = findViewById(R.id.radioGroup);
      ab_text= findViewById(R.id.evansTxt);
        ab_text.setText(Html.fromHtml("Nelson Asekhame Izevbieje is the Initiator and brain behind Topapp his passion for the growth and unity of the Owan people sparked up the need for a simple click access to Owan people and information.<br/><p></p>" +
                " The man that trekked from Iuleha to Benin just to draw attention to the plight of the Owan People”.<br/><p></p>" +
                "He is the CEO of Asenel Concept an IT establishment based in Uzebba, Owan West.<br/><p></p>" +
                "<b><i>Contact:</i></b><br/><p></p>" +
                "<b>Phone no:</b>08063243434,<br/>08164111814" +
                "<b>Email Address:</b>asenelconceptinfo@gmail.com"));

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId==R.id.evanBnt){
                    ab_text.setText(Html.fromHtml(" Abohi Evans Edokhagba, is a passionate and enthusiastic  Android and Java developer, whose keen interest is in Java programming, most times he plays with backend tools: Node.js, Php,Python.<br/> " +
                            " He Spends most of his days coding and developing apps, to him there is no greater joy, than playing a part to affect the quality of life positively,<br/> this is his main drive towards becoming an app developer; he sees it as privileged and a honour to play his part in making the world a better place by developing life impacting apps for clients.<br/><p></p>" +
                            "<b><i>Contact</i></b><br/><p></p>" +
                            "<b>Phone no:</b> 08161230058.<br/>" +
                            "<b>Website:</b> evansabohi.com<br/>"+
                            "<b>Email Address:</b> abohievanson@gmail.com"));

                }
                else if (checkedId==R.id.nelBnt){
                    ab_text.setText(Html.fromHtml("Nelson Asekhame Izevbieje is the Initiator and brain behind Topapp his passion for the growth and unity of the Owan people sparked up the need for a simple click access to Owan people and information.<br/><p></p>" +
                            " The man that trekked from Iuleha to Benin just to draw attention to the plight of the Owan People”.<br/><p></p>" +
                            "He is the CEO of Asenel Concept an IT establishment based in Uzebba, Owan West.<br/><p></p>" +
                            "<b><i>Contact</i></b><br/><p></p>" +
                            "<b>Phone no:</b> 08063243434,08164111814<br/>" +
                            "<br/><b>Email Address:</b> asenelconceptinfo@gmail.com"));
                }
            }
        });


    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction()==KeyEvent.ACTION_DOWN){
            switch(keyCode){
                case KeyEvent.KEYCODE_BACK:
                    Intent intent = new Intent(EvansActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    return  true;
            }
        }
        return super.onKeyDown(keyCode,event);
    }
}
