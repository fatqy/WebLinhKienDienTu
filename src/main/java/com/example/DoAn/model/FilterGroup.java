package com.example.DoAn.model;

import java.util.List;

public class FilterGroup {
    private String groupName;
    private List<String> options;

    public FilterGroup(String groupName, List<String> options) {
        this.groupName = groupName;
        this.options = options;
    }

    // Getters and Setters
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
}
