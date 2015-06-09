package com.referal.controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.referal.models.LearningStyle;
import com.referal.models.Material;
import com.referal.models.User;
import com.referal.services.LearningStyleService;
import com.referal.services.MaterialService;
import com.referal.services.UserService;

@Controller
public class Materials {

	@RequestMapping(value = "/materials")
	public String materials(
			@RequestParam(value = "search", required = false) String search,
			@RequestParam(value = "page", required = false) Integer page,
			Model model, HttpSession session) {
		MaterialService ms = new MaterialService();
		//List<Material> materialsList;

		User user = (User) session.getAttribute("user");
		if (user != null) {
			model.addAttribute("user", user);
		}

		PagedListHolder<Material> materialsList; 
		
		
		if (search == null || search == "") {
			if (user == null) {
				materialsList = new PagedListHolder<>(ms.getMaterials());
			} else {
				materialsList = new PagedListHolder<>(ms.getMaterials(user));
			}
		} else {
			if (user == null) {
				materialsList = new PagedListHolder<>(ms.getMaterials(search));
			} else {
				materialsList = new PagedListHolder<>(ms.getMaterials(user, search));
			}
		}
		
		materialsList.setPageSize(5); 
        if (page != null) { 
            materialsList.setPage(page); 
        } 
		model.addAttribute("search", search);
		model.addAttribute("materials", materialsList);
		return "materials";
	}

	@RequestMapping(value = "/delete")
	public String delete(@RequestParam("id") int id, Model model) {
		MaterialService ms = new MaterialService();
		ms.deleteMaterial(id);
		return "redirect:" + "/materials";
	}

	@RequestMapping(value = "/addmaterialform", method = RequestMethod.POST)
	public String addMaterialForm(
			@ModelAttribute("material") Material material,
			@RequestParam("id") Integer id,
			@RequestParam(value = "image", required = false) MultipartFile image,
			Model model) {
		MaterialService ms = new MaterialService();
		if (!image.isEmpty()) {
			try {
				File convFile = new File( image.getOriginalFilename());
		        image.transferTo(convFile);
		        
		        BufferedImage img = ImageIO.read(convFile);
		        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        ImageIO.write(img, "png", baos);
		        byte[] res = baos.toByteArray();
		        String encodedImage = Base64.encodeBase64String(baos.toByteArray());
				material.setPicture(encodedImage);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (id != 0) {
			material.setId(id);
		}
		ms.addMaterial(material);
		return "redirect:" + "/materials";
	}

	@RequestMapping(value = "/addmaterial")
	public ModelAndView addMaterial(HttpSession session) {
		ModelAndView mv = new ModelAndView("addmaterial", "material",
				new Material());
		LearningStyleService lss = new LearningStyleService();
		mv.addObject("styles", lss.getStyles());
		mv.addObject("materialform", "Add new Material Resource");
		User user = (User) session.getAttribute("user");
		if (user != null) {
			mv.addObject("user", user);
		}
		return mv;
	}

	@RequestMapping(value = "/editmaterial")
	public ModelAndView editMaterial(@RequestParam("id") int id,
			HttpServletResponse response, HttpSession session) {

		MaterialService ms = new MaterialService();
		Material material = ms.getMaterialById(id);
		ModelAndView mv = new ModelAndView("addmaterial", "material", material);
		LearningStyleService lss = new LearningStyleService();
		mv.addObject("styles", lss.getStyles());
		mv.addObject("materialform", "Edit the Selected Material");
		User user = (User) session.getAttribute("user");
		if (user != null) {
			mv.addObject("user", user);
		}
		return mv;
	}

	@RequestMapping(value = "/user")
	public String user(Model model, HttpSession session) {
		UserService us = new UserService();
		User user = (User) session.getAttribute("user");
		model.addAttribute("user", user);
		return "user";
	}

	@RequestMapping(value = "/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("user");
		return "redirect:" + "/materials";
	}

	@RequestMapping(value = "/material")
	public String user(@RequestParam("id") int id, Model model,
			HttpSession session) {
		MaterialService ms = new MaterialService();
		Material material = ms.getMaterialById(id);
		model.addAttribute("material", material);
		User user = (User) session.getAttribute("user");
		if (user != null) {
			model.addAttribute("user", user);
		}
		return "material";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@ModelAttribute("user") User user, Model model,
			HttpSession session) {
		UserService us = new UserService();
		User newUser = us.getUserByUsername(user);
		if (newUser != null) {
			session.setAttribute("user", newUser);
			return "redirect:" + "/materials";
		} else {
			return "redirect:" + "/login?error=true";
		}
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String register(@ModelAttribute("user") User user, Model model,
			HttpSession session) {
		UserService us = new UserService();
		us.addUser(user);
		User newUser = us.getUserByUsername(user);
		if (newUser != null) {
			session.setAttribute("user", newUser);
			return "redirect:" + "/materials";
		} else {
			return "redirect:" + "/register?error=true";
		}
	}

	@RequestMapping(value = "/login")
	public ModelAndView login(
			@RequestParam(value = "error", required = false) String error) {
		ModelAndView mv = new ModelAndView("login", "user", new User());
		if (error != null) {
			mv.addObject("error", "Username or password are incorect!");
		}
		mv.addObject("register", false);
		mv.addObject("userentry", "Login");
		return mv;
	}

	@RequestMapping(value = "/register")
	public ModelAndView register(
			@RequestParam(value = "error", required = false) String error) {
		LearningStyleService lss = new LearningStyleService();
		ModelAndView mv = new ModelAndView("login", "user", new User());
		if (error != null) {
			mv.addObject("error", "User already exists!");
		}
		mv.addObject("styles", lss.getStyles());
		mv.addObject("userentry", "Register");
		mv.addObject("register", true);
		return mv;
	}
	
	@RequestMapping(value = "/styles")
	public ModelAndView styles(@RequestParam(value = "delete", required = false) Integer delete) {
		LearningStyleService lss = new LearningStyleService();
		ModelAndView mv = new ModelAndView("styles", "style", new LearningStyle());
		if(delete!=null){
			lss.deleteStyle(delete);
		}
		mv.addObject("styles", lss.getStyles());
		return mv;
	}
	
	@RequestMapping(value = "/")
	public String styles() {
		return "redirect:" + "/materials";
	}
	
	@RequestMapping(value = "/styles", method = RequestMethod.POST)
	public String styles(@ModelAttribute("style") LearningStyle	ls, Model model,
			HttpSession session) {
		LearningStyleService lss = new LearningStyleService();
		lss.addStyle(ls.getType());
		return "redirect:" + "/styles";
	}
}
