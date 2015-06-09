package com.referal.services;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.referal.models.LearningStyle;
import com.referal.models.User;

public class UserService {
	private static final String PERSISTENCE_UNIT_NAME = "referal";
	private EntityManagerFactory factory = Persistence
			.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
	EntityManager em = factory.createEntityManager();
	private static boolean first = true;

	public List<User> getUsers() {
		Query q = em.createQuery("select u from User u");
		/*
		 * if (first) { first = false; addUser(); }
		 */
		List<User> userList = q.getResultList();
		return userList;
	}

	public Integer addUser() {
		EntityManager em = factory.createEntityManager();
		LearningStyleService lss = new LearningStyleService();
		em.getTransaction().begin();
		User usr1 = new User("jupiter", "a", lss.getStyleById(1));
		em.persist(usr1);
		User usr2 = new User("mars", "a", lss.getStyleById(2));
		em.persist(usr2);
		User usr3 = new User("mercury", "a", lss.getStyleById(1));
		em.persist(usr3);
		em.getTransaction().commit();
		em.close();

		return 1;
	}

	public User getUserByUsername(User user) {
		List<User> u = em
				.createQuery(
						"SELECT u FROM User u where u.username = :username")
				.setParameter("username", user.getUsername()).getResultList();
		if (u.isEmpty()) {
			return null;
		} else {
			if (u.get(0).getPassword().equals(user.getPassword())) {
				return u.get(0);
			}
		}
		return null;
	}

	public void addUser(User user) {
		if (getUserByUsername(user) == null) {
			LearningStyle style = getSelectedStyle(user);
			user.setStyle(style);
			em.getTransaction().begin();
			em.persist(user);
			em.getTransaction().commit();
		}
	}

	private LearningStyle getSelectedStyle(User user) {
		LearningStyleService lss = new LearningStyleService();
		return lss.getStyleById(Integer.parseInt(user.getStyle().getType()));
	}

}
