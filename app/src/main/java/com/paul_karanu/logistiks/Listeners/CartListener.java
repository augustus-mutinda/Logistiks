package com.paul_karanu.logistiks.Listeners;

public interface CartListener {
    void onCancel(int position);
    void onCheckOut(int position);
}
