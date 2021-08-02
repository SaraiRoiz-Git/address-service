package com.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleData{
    @Getter @Setter
    private List<String> destination_addresses;
    @Getter @Setter
    private List<String> origin_addresses;
    @Getter @Setter
    private List<Row> rows;
    @Getter @Setter
    private String status;

    private GoogleData() {
    }

    public static class Row{
        @Getter @Setter
        private List<Element> elements;

        private Row() {
        }
    }

    public static class Distance{
        @Getter @Setter
        private String text;
        @Getter @Setter
        private int value;

        private Distance() {
        }
    }

    public static class Duration{
        @Getter @Setter
        private String text;
        @Getter @Setter
        private int value;

        private Duration() {
        }
    }

    public static class Element{
        @Getter @Setter
        private Distance distance;
        @Getter @Setter
        private Duration duration;
        @Getter @Setter
        private String status;

        private Element() {
        }
    }
}
