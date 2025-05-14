package myproject.dao;
import myproject.model.User;
import myproject.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
//dầdasfdádfdsafdas
    public class UserDAO {
        public boolean register(User user) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction tx = session.beginTransaction();
                session.save(user);
                tx.commit();
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        public User login(String username, String password) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                return session.createQuery("FROM User WHERE username = :u AND password = :p", User.class)
                        .setParameter("u", username)
                        .setParameter("p", password)
                        .uniqueResult();
            }
        }
}
