package com.infoshareacademy.dao;

import com.infoshareacademy.model.Address;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class AddressDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Long save(Address s) {
        entityManager.persist(s);
        return s.getId();
    }

    public Address update(Address s) {
        return entityManager.merge(s);
    }

    public void delete(Long id) {
        final Address s = entityManager.find(Address.class, id);
        if (s != null) {
            entityManager.remove(s);
        }
    }

    public Address findById(Long id) {
        return entityManager.find(Address.class, id);
    }

    public List<Address> findAll() {
        final Query query = entityManager.createQuery("SELECT s FROM Address s");

        return query.getResultList();
    }

}
