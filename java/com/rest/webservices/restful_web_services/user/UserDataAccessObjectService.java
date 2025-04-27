package com.rest.webservices.restful_web_services.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.stereotype.Component;

@Component
public class UserDataAccessObjectService {
	
	private static int userCount = 0;

	// Creating a static Array list to store user info
	private static List<User> users = new ArrayList<>();
	static {
		users.add(new User(++userCount,"Vipu",LocalDate.now().minusYears(25)));
		users.add(new User(++userCount,"Oju",LocalDate.now().minusYears(27)));
		users.add(new User(++userCount,"Adi",LocalDate.now().minusYears(28)));
	}
	
	
	// Retrieve all users 
	public List<User> findAll(){
		return users;
	}
	
	public User save(User user) {
		user.setId(++userCount);
		users.add(user);
		return user;
	}	

	
	// Retrieve specific user 
	public User findOne(int id){
		Predicate<? super User> predicate = user -> user.getId().equals(id);
		return users.stream().filter(predicate).findFirst().orElse(null);
	}
	
	// Delete specific user
	public void deleteById(int id){
		Predicate<? super User> predicate = user -> user.getId().equals(id);
		users.removeIf(predicate);
	}
	
}
