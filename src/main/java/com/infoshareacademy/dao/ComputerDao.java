package com.infoshareacademy.dao;

import com.infoshareacademy.model.Computer;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class ComputerDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Long save(Computer s) {
        entityManager.persist(s);
        return s.getId();
    }

    public Computer update(Computer s) {
        return entityManager.merge(s);
    }

    public void delete(Long id) {
        final Computer s = entityManager.find(Computer.class, id);
        if (s != null) {
            entityManager.remove(s);
        }
    }

    public Computer findById(Long id) {
        return entityManager.find(Computer.class, id);
    }

    public List<Computer> findAll() {
        final Query query = entityManager.createQuery("SELECT s FROM Computer s");

        return query.getResultList();
    }

}
