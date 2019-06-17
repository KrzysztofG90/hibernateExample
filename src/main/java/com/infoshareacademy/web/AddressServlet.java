package com.infoshareacademy.web;

import com.infoshareacademy.dao.AddressDao;
import com.infoshareacademy.model.Address;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(urlPatterns = "/address")
public class AddressServlet extends HttpServlet {

    private Logger LOG = LoggerFactory.getLogger(AddressServlet.class);

    @Inject
    private AddressDao addressDao;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {

        final String action = req.getParameter("action");
        LOG.info("Requested action: {}", action);
        if (action == null || action.isEmpty()) {
            resp.getWriter().write("Empty action parameter.");
            return;
        }

        if (action.equals("findAll")) {
            findAll(req, resp);
        } else if (action.equals("add")) {
            addAddress(req, resp);
        } else if (action.equals("delete")) {
            deleteAddress(req, resp);
        } else if (action.equals("update")) {
            updateAddress(req, resp);
        } else {
            resp.getWriter().write("Unknown action.");
        }
    }

    private void updateAddress(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {
        final Long id = Long.parseLong(req.getParameter("id"));
        LOG.info("Updating Address with id = {}", id);

        final Address existingAddress = addressDao.findById(id);
        if (existingAddress == null) {
            LOG.info("No Address found for id = {}, nothing to be updated", id);
        } else {
            existingAddress.setStreet(req.getParameter("street"));
            existingAddress.setCity(req.getParameter("city"));

            addressDao.update(existingAddress);
            LOG.info("Address object updated: {}", existingAddress);
        }

        // Return all persisted objects
        findAll(req, resp);
    }

    private void addAddress(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {

        final Address p = new Address();
        p.setStreet(req.getParameter("street"));
        p.setCity(req.getParameter("city"));

        addressDao.save(p);
        LOG.info("Saved a new Address object: {}", p);

        // Return all persisted objects
        findAll(req, resp);
    }

    private void deleteAddress(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final Long id = Long.parseLong(req.getParameter("id"));
        LOG.info("Removing Address with id = {}", id);

        addressDao.delete(id);

        // Return all persisted objects
        findAll(req, resp);
    }

    private void findAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final List<Address> result = addressDao.findAll();
        LOG.info("Found {} objects", result.size());
        for (Address p : result) {
            resp.getWriter().write(p.toString() + "\n");
        }
    }
}