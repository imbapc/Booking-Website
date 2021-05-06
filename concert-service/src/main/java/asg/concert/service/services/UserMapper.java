package asg.concert.service.services;

import asg.concert.common.dto.UserDTO;
import asg.concert.service.domain.User;

public class UserMapper {

    static User toDomainModel(UserDTO userDTO){
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        return user;
    }

    static UserDTO toDto (User user){
        return new UserDTO(user.getUsername(), user.getPassword());
    }
}
