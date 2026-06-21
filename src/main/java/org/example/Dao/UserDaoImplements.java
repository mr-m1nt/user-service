package org.example.Dao;

import org.example.entity.User;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

public class UserDaoImplements implements UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDaoImplements.class);
    private final SessionFactory sessionFactory;
    public UserDaoImplements() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }
    public UserDaoImplements(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            logger.info("User saved: {}", user.getName());
        } catch (ConstraintViolationException e) {
            if (transaction != null) transaction.rollback();  // отменяем
            logger.error("Email already exist: {}", user.getEmail());
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Save error: {}", e.getMessage());
        }
    }
    @Override
    public Optional<User> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Find by ID error {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }
    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).list();
        } catch (Exception e) {
            logger.error("Error getting list: {}", e.getMessage());
            return List.of();
        }
    }
    @Override
    public void update(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
            logger.info("User updated: ID {}", user.getId());
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("User update error: {}", e.getMessage());
        }
    }
    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
                transaction.commit();
                logger.info("User deleted: ID {}", id);
            } else {
                logger.warn("User ID {} not found", id);
            }
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("User delete error: {}", e.getMessage());
        }
    }
}