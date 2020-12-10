package com.gesthelp.vote.web;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gesthelp.vote.domain.Utilisateur;
import com.gesthelp.vote.service.PasswordGenerator;
import com.gesthelp.vote.service.UtilisateurService;
import com.gesthelp.vote.web.form.UserDto;

import lombok.extern.java.Log;

@Controller
@RequestMapping("/admin")
@Log
public class AdminController {
	@Autowired
	private UtilisateurService service;

	@GetMapping("/")
	public String home() {
		log.info("admin home IN");
		return "redirect:/admin/newUser";
	}

	@GetMapping("/newUser")
	public String newUser(Model model) {
		log.info("newClient IN");
		UserDto form = new UserDto();
		form.setMdp(PasswordGenerator.generatePassword(8));
		form.setRole("ROLE_VOTE");
		model.addAttribute("userForm", form);
		return "admin/userForm";
	}

	@PostMapping("/saveUser")
	public String saveUser(@Valid UserDto fo, BindingResult bindingResult, Model model) {
		log.info("saveUser IN " + fo);
		this.validateUserPassword(fo, bindingResult);
		if (bindingResult.hasErrors()) {
			log.info("saveUser errors: " + bindingResult.getErrorCount());
			return "admin/userForm";
		}
		Utilisateur u = new Utilisateur();
		u.setNom(fo.getNom());
		u.setPrenom(fo.getPrenom());
		u.setMail(fo.getMail());
		u.setMdp(fo.getMdp());
		u.setRole(fo.getRole());
		u = service.save(u);
		model.addAttribute("user", u);
		log.info("saved: " + u);
		return "admin/userSaved";
	}

	private void validateUserPassword(UserDto fo, BindingResult bindingResult) {
		if (!StringUtils.isEmpty(fo.getMdp())) {
			String input = fo.getMdp();
			String specialChars = "~!@#$%^&*()-_=+\\|[{]};:',<\\.>/?";
			char currentCharacter;
			boolean numberPresent = false;
			boolean upperCasePresent = false;
			boolean lowerCasePresent = false;
			boolean specialCharacterPresent = false;
			boolean spacePresent = false;

			for (int i = 0; i < input.length(); i++) {
				currentCharacter = input.charAt(i);
				if (Character.isDigit(currentCharacter)) {
					numberPresent = true;
				} else if (Character.isUpperCase(currentCharacter)) {
					upperCasePresent = true;
				} else if (Character.isLowerCase(currentCharacter)) {
					lowerCasePresent = true;
				} else if (specialChars.contains(String.valueOf(currentCharacter))) {
					specialCharacterPresent = true;
				} else if (Character.isSpaceChar(currentCharacter)) {
					spacePresent = true;
				}
			}
			if (spacePresent) {
				bindingResult.rejectValue("mdp", "mdp.format");
			} else {
				if (!upperCasePresent || !lowerCasePresent) {
					bindingResult.rejectValue("mdp", "mdp.format");
				} else {
					if (!numberPresent && !specialCharacterPresent) {
						bindingResult.rejectValue("mdp", "mdp.format");
					}
				}
			}
		}
	}

}
