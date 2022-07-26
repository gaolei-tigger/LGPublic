package com.example.textviewdebug;

public class UserDesign {

    public BaseDesing bd = new BestDesign();

    public UserDesign() {

    }

    public void doSome() {
        BestDesign bs = (BestDesign)bd;
        bs.self();
    }
}
