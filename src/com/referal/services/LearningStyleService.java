package com.referal.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.referal.models.LearningStyle;
import com.referal.models.Material;

public class LearningStyleService {
	private static final String PERSISTENCE_UNIT_NAME = "referal";
	private EntityManagerFactory factory = Persistence
			.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
	EntityManager em = factory.createEntityManager();
	private static boolean first=true;

	public List<LearningStyle> getStyles() {
		Query q = em.createQuery("select s from LearningStyle s");
		/*if (first) {
			first = false;
			addStyle();
		}*/
		List<LearningStyle> stylesList = q.getResultList();
		return stylesList;
	}

	public Integer addStyle(String type) {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		LearningStyle ls = new LearningStyle(type);
		em.persist(ls);
		em.getTransaction().commit();

		return 1;
	}

	public LearningStyle getStyleById(int id) {
		LearningStyle ls = (LearningStyle) em.find(LearningStyle.class, id);
		return ls;
	}

	public void deleteStyle(Integer id) {
		LearningStyle ls = (LearningStyle) em.find(LearningStyle.class, id);
		em.getTransaction().begin();
		em.remove(ls);
		em.getTransaction().commit();
	}

}
