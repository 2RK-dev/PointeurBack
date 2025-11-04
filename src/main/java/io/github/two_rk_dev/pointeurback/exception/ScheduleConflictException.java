package io.github.two_rk_dev.pointeurback.exception;

import io.github.two_rk_dev.pointeurback.model.Group;
import io.github.two_rk_dev.pointeurback.model.ScheduleItem;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;

@Getter
public class ScheduleConflictException extends RuntimeException {
    private final HashSet<String> conflicts = new HashSet<>();

    public ScheduleConflictException(String message) {
        super(message);
    }

    public ScheduleConflictException(ScheduleItem theItem, @NotNull List<ScheduleItem> conflictingItems) {
        this("Schedule conflict detected: ");
        for (ScheduleItem item : conflictingItems) {
            if (theItem.getRoom() != null && item.getRoom() != null && theItem.getRoom().getId().equals(item.getRoom().getId())) {
                conflicts.add("room");
            }
            if (theItem.getTeacher() != null && item.getTeacher() != null && theItem.getTeacher().getId().equals(item.getTeacher().getId())) {
                conflicts.add("teacher");
            }
            List<Group> groups = theItem.getGroups();
            if (groups != null && item.getGroups() != null) {
                groups.retainAll(item.getGroups());
                if (!groups.isEmpty()) {
                    conflicts.add("groups");
                }
            }
        }
    }
}
