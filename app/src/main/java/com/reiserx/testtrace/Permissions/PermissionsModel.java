package com.reiserx.testtrace.Permissions;

public class PermissionsModel {
    String contentName, contentDescription;
    int PermissionCode;
    boolean isHeader;

    public PermissionsModel(String contentName, String contentDescription, int permissionCode, boolean isHeader) {
        this.contentName = contentName;
        this.contentDescription = contentDescription;
        PermissionCode = permissionCode;
        this.isHeader = isHeader;
    }

    public PermissionsModel(boolean isHeader, String contentName) {
        this.isHeader = isHeader;
        this.contentName = contentName;
    }

    public PermissionsModel() {
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public String getContentDescription() {
        return contentDescription;
    }

    public void setContentDescription(String contentDescription) {
        this.contentDescription = contentDescription;
    }

    public int getPermissionCode() {
        return PermissionCode;
    }

    public void setPermissionCode(int permissionCode) {
        PermissionCode = permissionCode;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }
}
