package com.example.aidltest.event;

public class TestEvent {

    public static void test(FloatEvent floatEvent){
        floatEvent.jobState = false;
    }

    public static class  t extends TestEvent{
        public static void test(FloatEvent floatEvent){
            floatEvent.jobState = false;
        }
    }
}
