package com.fahad.integraatask;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class PermissionResponse {

    @SerializedName("actions")
    private Actions actions;
    public Actions getActions() {
        return actions;
    }
    public void setActions(Actions actions) {
        this.actions = actions;
    }


    public static class Actions {
        @SerializedName("common")
        private Common common;
        public Common getCommon() {
            return common;
        }
        public void setCommon(Common common) {
            this.common = common;
        }

//        @SerializedName("network")
//        private Network network;

        // Getters and setters

        public static class Common {
            @SerializedName("label")
            String label;

            @SerializedName("items")
            Items items;


            public void setLabel(String label) {
                this.label = label;
            }
            public String getLabel() {
                return label;
            }

            public void setItems(Items items) {
                this.items = items;
            }
            public Items getItems() {
                return items;
            }
        }

        public static class Items {
            @SerializedName("open")
            Open open;

            public Open getOpen() {
                return open;
            }
            public void setOpen(Open open) {
                this.open = open;
            }
        }

        public static class Open {

            @SerializedName("label")
            String label;

            @SerializedName("payload")
            String payload;


            public void setLabel(String label) {
                this.label = label;
            }
            public String getLabel() {
                return label;
            }

            public void setPayload(String payload) {
                this.payload = payload;
            }
            public String getPayload() {
                return payload;
            }

        }
    }
}
