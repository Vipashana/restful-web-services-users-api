package com.rest.webservices.restful_web_services.user;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.net.URI;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;

@RestController
public class UserResource {
	
	public UserDataAccessObjectService service;
	
	public UserResource(UserDataAccessObjectService service) {
		this.service =  service;
	}
	
	// GET Users
	@GetMapping("/users")
	public List<User> retriveAllUsers(){
		return service.findAll();
	}
	
	// GET specific User
	@GetMapping("/users/{id}")
	public EntityModel<User> retriveUser(@PathVariable Integer id){
		User user = service.findOne(id);
		
		if(user==null)
			throw new UserNotFoundException("id: "+id);
		
		EntityModel<User> entityModel = EntityModel.of(user);
		
		WebMvcLinkBuilder link = linkTo(methodOn(this.getClass()).retriveAllUsers());
		entityModel.add(link.withRel("all-users"));
		
		return entityModel;
	}
	
	//Delete specific user
	@DeleteMapping("/users/{id}")
	public void deleteUser(@PathVariable Integer id){
		service.deleteById(id);
	}
	
	//Post user (create a new user)
	@PostMapping("/users")
	public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
		User savedUser = service.save(user);
		
		// We want to return the newly created user info i.e. new location
		// To do so, we will need to
		// /users/4(new user ID) -> /users/{id} & replace value of {id} with user.getID
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest().path("/{id}")
				.buildAndExpand(savedUser.getId())
				.toUri();
		
		// Once the user is created we are returning
		// back the 201 (Created) response
		return ResponseEntity.created(location).build();
	}
	
}
