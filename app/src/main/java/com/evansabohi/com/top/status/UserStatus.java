package com.evansabohi.com.top.status;

import com.amulyakhare.textdrawable.TextDrawable;

/**
 * Created by ABOHI CHRISTIAN on 08/06/2018.
 */

public class UserStatus {
    TextDrawable txtDrawable;
    public UserStatus(){

    }
    public UserStatus(TextDrawable textDrawable){
        this.txtDrawable = textDrawable;
    }

    public TextDrawable getTxtDrawable() {
        return txtDrawable;
    }

    public void setTxtDrawable(TextDrawable txtDrawable) {
        this.txtDrawable = txtDrawable;
    }


}
