package com.infoshareacademy.dao;

import com.infoshareacademy.model.Student;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class StudentDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Long save(Student s) {
        entityManager.persist(s);
        return s.getId();
    }

    public Student update(Student s) {
        return entityManager.merge(s);
    }

    public void delete(Long id) {
        final Student s = entityManager.find(Student.class, id);
        if (s != null) {
            entityManager.remove(s);
        }
    }

    public Student findById(Long id) {
        return entityManager.find(Student.class, id);
    }

    public List<Student> findAll() {
        final Query query = entityManager.createQuery("SELECT s FROM Student s");

        return query.getResultList();
    }

    public List<String> findNames() {
        // List<Object[]>
        // ( [imie, nazwisko], [imie, nazwisko] )
//        final Query query = entityManager.createNativeQuery("SELECT NAME, SURNAME FROM STUDENTS");
//        List<Object[]> result = query.getResultList();
//        return result.stream().map(obj -> obj[0] + " " + obj[1]).collect(Collectors.toList());

        final Query query = entityManager.createNativeQuery("SELECT concat(NAME, \" \", SURNAME) FROM STUDENTS;");
        return (List<String>) query.getResultList();
    }

    public List<LocalDate> findDates() {
        // SELECT date_of_birth FROM STUDENTS ORDER BY date_of_birth DESC;
        final Query query = entityManager.createQuery("SELECT s.dateOfBirth FROM Student s ORDER BY s.dateOfBirth DESC");
        return query.getResultList();
    }

    public List<Student> findBornAfter(LocalDate date) {
        Query query = entityManager.createNamedQuery("findStudentsBornAfter");
        query.setParameter("param", date);
        return query.getResultList();
    }
}
