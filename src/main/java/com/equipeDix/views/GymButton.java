package com.equipeDix.views;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.text.TextAlignment;

public class GymButton extends Button {

    public GymButton(String text){
        super(text);
        setPrefSize(100,55);
        setMinSize(100, 55);
        setWrapText(true);
        setAlignment(Pos.CENTER);
        setTextAlignment(TextAlignment.CENTER);

    }
}
