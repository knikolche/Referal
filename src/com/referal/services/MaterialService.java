package com.referal.services;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.referal.models.LearningStyle;
import com.referal.models.Material;
import com.referal.models.User;

public class MaterialService {
	private static final String PERSISTENCE_UNIT_NAME = "referal";
	private EntityManagerFactory factory = Persistence
			.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
	EntityManager em = factory.createEntityManager();

	public List<Material> getMaterials() {
		Query q = em.createQuery("select m from Material m");
		List<Material> materialsList = q.getResultList();
		return materialsList;
	}

	public Material getMaterialById(int id) {
		Material material = (Material) em.find(Material.class, id);
		return material;
	}

	public Integer deleteMaterial(int id) {
		Material material = (Material) em.find(Material.class, id);
		em.getTransaction().begin();
		em.remove(material);
		em.getTransaction().commit();
		em.close();
		return null;
	}

	public Integer addMaterial(String title, String type, String reference,
			List<LearningStyle> styles) {
		em.getTransaction().begin();
		Material material = new Material(title, type, reference, styles);
		em.persist(material);
		em.getTransaction().commit();
		em.close();
		return 1;
	}

	public Integer addMaterial(Material material) {
		Material sentinel = (Material) em
				.find(Material.class, material.getId());
		if (sentinel == null) {
			List<LearningStyle> styles = getSelectedStyles(material);
			material.setStyles(styles);
			em.getTransaction().begin();
			em.persist(material);
			em.getTransaction().commit();
			em.close();
		} else {
			if (material.getPicture()==null) {
				material.setPicture(sentinel.getPicture());
			}
			editMaterial(material);
		}
		return 1;
	}

	private List<LearningStyle> getSelectedStyles(Material material) {
		LearningStyleService lss = new LearningStyleService();
		List<LearningStyle> styles = new ArrayList<LearningStyle>();
		for (LearningStyle ls : material.getStyles()) {
			styles.add(lss.getStyleById(Integer.parseInt(ls.getType())));
		}
		return styles;
	}

	public Integer editMaterial(Material material) {
		List<LearningStyle> styles = getSelectedStyles(material);
		material.setStyles(styles);
		em.getTransaction().begin();
		em.merge(material);
		em.getTransaction().commit();
		em.close();
		return null;
	}

	/*
	 * Query q = em.createQuery("delete m from Material m where id=:id");
	 * q.setParameter("id", id); return q.executeUpdate();
	 */

	public List<Material> getMaterials(String search) {
		Query q = em
				.createQuery("select m from Material m where m.title like  :search ");
		q.setParameter("search", "%" + search + "%");
		List<Material> materialsList = q.getResultList();
		return materialsList;
	}

	public List<Material> getMaterials(User user) {
		// join m.styles ms where ms.material_id = m.id
		Query q = em
				.createQuery("select m from Material m join m.styles ms ORDER BY CASE WHEN ms.id = :style THEN 0 ELSE ms.id END ASC");
		q.setParameter("style", user.getStyle().getId());
		List<Material> materialsList = q.getResultList();
		return materialsList;
	}

	public List<Material> getMaterials(User user, String search) {
		Query q = em
				.createQuery("select m from Material m join m.styles ms where m.title like :search ORDER BY CASE WHEN ms.id = :style THEN 0 ELSE ms.id END ASC");
		q.setParameter("search", "%" + search + "%");
		q.setParameter("style", user.getStyle().getId());
		List<Material> materialsList = q.getResultList();
		return materialsList;
	}
}
