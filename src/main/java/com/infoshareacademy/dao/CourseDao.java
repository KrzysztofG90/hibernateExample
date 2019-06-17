package com.infoshareacademy.dao;

import com.infoshareacademy.model.Course;
import com.infoshareacademy.model.CourseSummary;
import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class CourseDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Long save(Course s) {
        entityManager.persist(s);
        return s.getId();
    }

    public Course update(Course s) {
        return entityManager.merge(s);
    }

    public void delete(Long id) {
        final Course s = entityManager.find(Course.class, id);
        if (s != null) {
            entityManager.remove(s);
        }
    }

    public Course findById(Long id) {
        return entityManager.find(Course.class, id);
    }

    public List<Course> findAll() {
        final Query query = entityManager.createQuery("SELECT s FROM Course s");

        return query.getResultList();
    }

    public List<CourseSummary> getCoursesSummary() {
        List<Course> courses = findAll();
        return courses.stream()
            .map(c -> {
                String courseName = c.getName();
                List<String> attendees = c.getStudents().stream()
                    .map(s -> s.getName() + " " + s.getSurname())
                    .collect(Collectors.toList());

                return new CourseSummary(courseName, attendees);
            })
            .collect(Collectors.toList());
    }
}