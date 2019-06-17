package com.infoshareacademy.web;

import com.infoshareacademy.dao.AddressDao;
import com.infoshareacademy.dao.ComputerDao;
import com.infoshareacademy.dao.CourseDao;
import com.infoshareacademy.dao.StudentDao;
import com.infoshareacademy.model.Address;
import com.infoshareacademy.model.Computer;
import com.infoshareacademy.model.Course;
import com.infoshareacademy.model.CourseSummary;
import com.infoshareacademy.model.Student;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(urlPatterns = "/student")
@Transactional
public class StudentServlet extends HttpServlet {

    private Logger LOG = LoggerFactory.getLogger(StudentServlet.class);

    @Inject
    private StudentDao studentDao;

    @Inject
    private ComputerDao computerDao;

    @Inject
    private AddressDao addressDao;

    @Inject
    private CourseDao courseDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // Test data
        // Courses
        Course course1 = new Course("JJDZ5");
        courseDao.save(course1);

        Course course2 = new Course("JJDD5");
        courseDao.save(course2);

        Course course3 = new Course("JJFD1");
        courseDao.save(course3);

        // Addresses
        Address a1 = new Address("Grunwaldzka", "Gdansk");
        addressDao.save(a1);

        Address a2 = new Address("Sobieskiego", "Wejherowo");
        addressDao.save(a2);

        // Computers
        Computer c1 = new Computer("HP 8160P", "Windows 7");
        computerDao.save(c1);

        Computer c2 = new Computer("Dell Latitude 15", "Ubuntu 16.04");
        computerDao.save(c2);

        computerDao.save(new Computer("Asus", "Windows"));

        // Students
        List<Course> student1Courses = Arrays.asList(course1, course2);
        Student s1 = new Student("Michal", "Kowalski", LocalDate.of(1980, 11, 12), c1, a1, student1Courses);
        studentDao.save(s1);

        List<Course> student2Courses = Arrays.asList(course1, course2, course3);
        Student s2 = new Student("Marek", "Malinowski", LocalDate.of(1985, 7, 13), c2, a1, student2Courses);
        studentDao.save(s2);

        LOG.info("System time zone is: {}", ZoneId.systemDefault());
    }

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
            addStudent(req, resp);
        } else if (action.equals("delete")) {
            deleteStudent(req, resp);
        } else if (action.equals("update")) {
            updateStudent(req, resp);
        } else if (action.equals("findNames")) {
            findNames(req, resp);
        } else if (action.equals("findDates")) {
            findDates(req, resp);
        } else if (action.equals("findBornAfter")) {
            findBornAfter(req, resp);
        } else if (action.equals("summary")) {
            summary(req, resp);
        } else {
            resp.getWriter().write("Unknown action.");
        }
    }

    private void summary(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<CourseSummary> result = courseDao.getCoursesSummary();
        for (CourseSummary c : result) {
            resp.getWriter().println(c);
        }
    }

    private void findNames(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<String> names = studentDao.findNames();
        for (String name : names) {
            resp.getWriter().println(name);
        }
    }

    private void findDates(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<LocalDate> dates = studentDao.findDates();
        for (LocalDate d : dates) {
            resp.getWriter().println(d);
        }
    }

    private void findBornAfter(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LocalDate date = LocalDate.parse(req.getParameter("date"));

        List<Student> students = studentDao.findBornAfter(date);
        for (Student d : students) {
            resp.getWriter().println(d);
        }
    }

    private void updateStudent(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {
        final Long id = Long.parseLong(req.getParameter("id"));
        LOG.info("Updating Student with id = {}", id);

        final Student existingStudent = studentDao.findById(id);
        if (existingStudent == null) {
            LOG.info("No Student found for id = {}, nothing to be updated", id);
        } else {
            existingStudent.setName(req.getParameter("name"));
            existingStudent.setSurname(req.getParameter("surname"));

            Long computerId = Long.valueOf(req.getParameter("computer_id"));
            Computer computer = computerDao.findById(computerId);
            existingStudent.setComputer(computer);

            Long addressId = Long.valueOf(req.getParameter("address_id"));
            Address address = addressDao.findById(addressId);
            existingStudent.setAddress(address);

            Long courseId = Long.valueOf(req.getParameter("course_id"));
            Course course = courseDao.findById(courseId);

            List<Course> existingCourses = existingStudent.getCourses();
            if (existingCourses == null) {
                LOG.error("existingCourse = null");
            } else {
                LOG.info("existingCourse.size() = " + existingCourses.size());
            }

            existingCourses.add(course);

            // YYYY-MM-DD
            LocalDate dateOfBirth = LocalDate.parse(req.getParameter("dateOfBirth"));
            existingStudent.setDateOfBirth(dateOfBirth);

            studentDao.update(existingStudent);
            LOG.info("Student object updated: {}", existingStudent);
        }

        // Return all persisted objects
        findAll(req, resp);
    }

    private void addStudent(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {

        Long computerId = Long.valueOf(req.getParameter("computer_id"));
        Computer computer = computerDao.findById(computerId);

        Long addressId = Long.valueOf(req.getParameter("address_id"));
        Address address = addressDao.findById(addressId);

        final Student p = new Student();
        p.setName(req.getParameter("name"));
        p.setSurname(req.getParameter("surname"));
        p.setComputer(computer);
        p.setAddress(address);

        // YYYY-MM-DD
        LocalDate dateOfBirth = LocalDate.parse(req.getParameter("dateOfBirth"));
        p.setDateOfBirth(dateOfBirth);

        studentDao.save(p);
        LOG.info("Saved a new Student object: {}", p);

        // Return all persisted objects
        findAll(req, resp);
    }

    private void deleteStudent(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final Long id = Long.parseLong(req.getParameter("id"));
        LOG.info("Removing Student with id = {}", id);

        studentDao.delete(id);

        // Return all persisted objects
        findAll(req, resp);
    }

    private void findAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final List<Student> result = studentDao.findAll();
        LOG.info("Found {} objects", result.size());
        for (Student p : result) {
            resp.getWriter().write(p.toString() + "\n");
        }
    }
}

