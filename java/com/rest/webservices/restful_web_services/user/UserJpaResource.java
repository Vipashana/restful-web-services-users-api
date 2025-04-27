package com.rest.webservices.restful_web_services.user;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.net.URI;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;
import java.util.Optional;

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

import com.rest.webservices.restful_web_services.jpa.PostRepository;
import com.rest.webservices.restful_web_services.jpa.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;

@RestController
public class UserJpaResource {
	
	private UserRepository userRepository;
	
	private PostRepository postrepository;
	
	public UserJpaResource(UserRepository userRepository, PostRepository postrepository) {
		this.userRepository =  userRepository;
		this.postrepository = postrepository;
	}
	
	// GET Users
	@GetMapping("/jpa/users")
	public List<User> retriveAllUsers(){
		return userRepository.findAll();
	}
	
	// GET specific User
	@GetMapping("/jpa/users/{id}")
	public EntityModel<User> retriveUser(@PathVariable Integer id){
		Optional<User> user = userRepository.findById(id);
		
		if(user.isEmpty())
			throw new UserNotFoundException("id: "+id);
		
		EntityModel<User> entityModel = EntityModel.of(user.get());
		
		WebMvcLinkBuilder link = linkTo(methodOn(this.getClass()).retriveAllUsers());
		entityModel.add(link.withRel("all-users"));
		
		return entityModel;
	}
	
	//Delete specific user
	@DeleteMapping("/jpa/users/{id}")
	public void deleteUser(@PathVariable Integer id){
		userRepository.deleteById(id);
	}
	
	//Retrieve specific post for user
	@GetMapping("/jpa/users/{id}/posts")
	public List<Post> retrievePostForUser(@PathVariable Integer id){
		Optional<User> user = userRepository.findById(id);
			
		if(user.isEmpty())
			throw new UserNotFoundException("id: "+id);
		
		return user.get().getPosts();
	}
	
	
	
	//Post user (create a new user)
	@PostMapping("/jpa/users")
	public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
		User savedUser = userRepository.save(user);
		
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
	
	//Create Post for User
	@PostMapping("/jpa/users/{id}/posts")
	public ResponseEntity<Object> createPostForUser(@PathVariable Integer id, @Valid @RequestBody Post post){
		Optional<User> user = userRepository.findById(id);
			
		if(user.isEmpty())
			throw new UserNotFoundException("id: "+id);
		
		post.setUser(user.get());
		
		Post savedPost = postrepository.save(post);
		
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest().path("/{id}")
				.buildAndExpand(savedPost.getId())
				.toUri();
		
		return ResponseEntity.created(location).build();
	}
	
}
