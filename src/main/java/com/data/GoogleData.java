package com.data;

public class GoogleData {

    String status;

    String origin_addresses;

    String destination_addresses;
    class rows{
        class elements{
            String status;
            class  duration{
                int value;
                String text;
            }
            class  distance{
                int value;
                String text;
            }
        }

    }
}
