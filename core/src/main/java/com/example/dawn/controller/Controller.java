package com.example.dawn.controller;

import com.example.dawn.view.AppMenu;

public abstract class Controller {
    protected AppMenu view;

    public void setView(AppMenu view) {
        this.view = view;
    }
}
