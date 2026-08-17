package com.example.demo.event;

import com.example.demo.entity.SupportQuery;

public class SupportQueryCreatedEvent {

    private final SupportQuery supportQuery;

    public SupportQueryCreatedEvent(
            SupportQuery supportQuery) {

        this.supportQuery = supportQuery;
    }

    public SupportQuery getSupportQuery() {

        return supportQuery;

    }

}