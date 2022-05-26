package com.reiserx.testtrace.Models;

public class FolderPath {
        public String path, type;

        public FolderPath() {
        }

        public FolderPath(String path, String type) {
            this.path = path;
            this.type = type;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
}
