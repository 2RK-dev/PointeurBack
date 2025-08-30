package io.github.two_rk_dev.pointeurback.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.util.List;
import java.util.Objects;
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;

    private String name;

    private Integer size;

    @ManyToOne
    @JoinColumn(name = "level_id")
    private Level level;

    @ManyToMany(cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinTable(
            name = "schedule_item_groups",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "schedule_item_id")
    )
    @ToString.Exclude
    private List<ScheduleItem> schedules;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Group group = (Group) o;
        return getId() != null && Objects.equals(getId(), group.getId());
    }
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public void addScheduleItem(ScheduleItem scheduleItem) {
        if (!schedules.contains(scheduleItem)) {
            schedules.add(scheduleItem);
            scheduleItem.getGroups().add(this);
        }
    }

    public void removeScheduleItem(ScheduleItem scheduleItem) {
        if (schedules.remove(scheduleItem)) {
            scheduleItem.getGroups().remove(this);
        }
    }
}
