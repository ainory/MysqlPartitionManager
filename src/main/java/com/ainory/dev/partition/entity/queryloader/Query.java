package com.ainory.dev.partition.entity.queryloader;

public class Query{
        private String id;
        private String value;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setValue(String value){
            this.value = value;
        }

        public String getValue(){
            return this.value;
        }

        @Override
        public String toString() {
            return "Query{" +
                    "id='" + id + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }