package com.infoshareacademy.model;

import java.util.List;

public class CourseSummary {

    private String name;

    private List<String> attendees;

    public CourseSummary(String name, List<String> attendees) {
        this.name = name;
        this.attendees = attendees;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<String> attendees) {
        this.attendees = attendees;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CourseSummary{");
        sb.append("name='").append(name).append('\'');
        sb.append(", attendees=").append(attendees);
        sb.append('}');
        return sb.toString();
    }
}
