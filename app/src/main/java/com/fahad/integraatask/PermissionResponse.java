package com.fahad.integraatask;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

//public class PermissionResponse {
//
//    @SerializedName("actions")
//    private Actions actions;
//    public Actions getActions() {
//        return actions;
//    }
//    public void setActions(Actions actions) {
//        this.actions = actions;
//    }
//
//
//    public static class Actions {
//        @SerializedName("common")
//        private Common common;
//        public Common getCommon() {
//            return common;
//        }
//        public void setCommon(Common common) {
//            this.common = common;
//        }
//
////        @SerializedName("network")
////        private Network network;
//
//        // Getters and setters
//
//        public static class Common {
//            @SerializedName("label")
//            String label;
//
//            @SerializedName("items")
//            Items items;
//
//
//            public void setLabel(String label) {
//                this.label = label;
//            }
//            public String getLabel() {
//                return label;
//            }
//
//            public void setItems(Items items) {
//                this.items = items;
//            }
//            public Items getItems() {
//                return items;
//            }
//        }
//
//        public static class Items {
//            @SerializedName("open")
//            Open open;
//
//            public Open getOpen() {
//                return open;
//            }
//            public void setOpen(Open open) {
//                this.open = open;
//            }
//        }
//
//        public static class Open {
//
//            @SerializedName("label")
//            String label;
//
//            @SerializedName("payload")
//            String payload;
//
//
//            public void setLabel(String label) {
//                this.label = label;
//            }
//            public String getLabel() {
//                return label;
//            }
//
//            public void setPayload(String payload) {
//                this.payload = payload;
//            }
//            public String getPayload() {
//                return payload;
//            }
//
//        }
//    }
//}


//_________________________________________________________________________________________________

//public class PermissionResponse {
//
//    @SerializedName("actions")
//    private List<Action> actions;
//
//    public List<Action> getActions() {
//        return actions;
//    }
//
//    public static class Action {
//        @SerializedName("label")
//        private String label;
//
//        @SerializedName("items")
//        private List<Item> items;
//
//        public String getLabel() {
//            return label;
//        }
//
//        public List<Item> getItems() {
//            return items;
//        }
//
//        public static class Item {
//            @SerializedName("label")
//            private String label;
//
//            @SerializedName("payload")
//            private String payload;
//
//            @SerializedName("parameters")
//            private Map<String, Checksum> parameters;
//
//            public String getLabel() {
//                return label;
//            }
//
//            public String getPayload() {
//                return payload;
//            }
//
//            public Map<String, Checksum> getParameters() {
//                return parameters;
//            }
//        }
//    }
//
//    public static class Checksum {
//        @SerializedName("type")
//        private String type;
//
//        public String getType() {
//            return type;
//        }
//    }
//}

//--------------------------------------------------------------------------------------------------

public class PermissionResponse {
    @SerializedName("actions")
    private List<Action> actions;

    public List<Action> getActions() {
        return actions;
    }

    public static class Action {
        @SerializedName("label")
        private String label;

        @SerializedName("items")
        private List<Item> items;

        public String getLabel() {
            return label;
        }

        public List<Item> getItems() {
            return items;
        }

        public static class Item {
            @SerializedName("label")
            private String label;

            @SerializedName("payload")
            private String payload;

            @SerializedName("parameters")
            private Map<String, Parameter> parameters;

            public String getLabel() {
                return label;
            }

            public String getPayload() {
                return payload;
            }

            public Map<String, Parameter> getParameters() {
                return parameters;
            }
        }
    }

    public static class Parameter {
        @SerializedName("label")
        private String label;

        @SerializedName("type")
        private String type;

        @SerializedName("value")
        private String value;

        @SerializedName("required")
        private String required;

        @SerializedName("min")
        private Integer min;

        @SerializedName("max")
        private Integer max;

        public String getLabel() {
            return label;
        }

        public String getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        public String getRequired() {
            return required;
        }

        public Integer getMin() {
            return min;
        }

        public Integer getMax() {
            return max;
        }
    }
}
