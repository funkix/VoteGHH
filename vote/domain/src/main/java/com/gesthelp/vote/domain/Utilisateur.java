package com.gesthelp.vote.domain;


import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Data;

@Entity
@Data
public class Utilisateur /*implements UserDetails */{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String mdp;
	private String mdpClair;
	private String role;
	private String nom;
	private String prenom;
	private String mail;
	private String telephone;
	// private String mdpEnvoye;
	@Column(name = "date_naissance")
	private Date dateNaissance;
	private String fonction;
	private String adresse;

//	@ManyToMany(fetch = FetchType.LAZY)	
//	@JoinTable(name = "utilisateur_scrutin", joinColumns = @JoinColumn(name = "utilisateur_id"), inverseJoinColumns = @JoinColumn(name = "scrutin_id"))
//	private Set<Scrutin> scrutins;

	@OneToMany(mappedBy = "utilisateur")
	Set<ScrutinVote> scrutinVote;

//	public Collection<? extends GrantedAuthority> getAuthorities() {
//		List<GrantedAuthority> authorities = new ArrayList<>();
//		if (StringUtils.isNotEmpty(role)) {
//			String[] roles = role.split(",");
//			for (String r : roles) {
//				// roles should start with 'ROLE_'. Example: 'ROLE_ADMIN', 'ROLE_VOTE'
//				authorities.add(new SimpleGrantedAuthority(r));
//			}
//		}
//		return authorities;
//
//	}
//
//	public String getPassword() {
//		return mdp;
//	}
//
//	public String getUsername() {
//		return mail;
//	}
//
//	public boolean isAccountNonExpired() {
//		return true;
//	}
//
//	public boolean isAccountNonLocked() {
//		return true;
//	}
//
//	public boolean isCredentialsNonExpired() {
//		return true;
//	}
//
//	public boolean isEnabled() {
//		return true;
//	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Utilisateur other = (Utilisateur) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

}
