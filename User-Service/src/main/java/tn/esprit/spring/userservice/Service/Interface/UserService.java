package tn.esprit.spring.userservice.Service.Interface;

import tn.esprit.spring.userservice.Entity.User;
import tn.esprit.spring.userservice.dto.Request.UserUpdateRequest;
import tn.esprit.spring.userservice.dto.Response.UserDetailDTO;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    User getUserByEmail(String email);
    User updateUser(Long id, UserUpdateRequest request) throws IOException;
    User blockUser(Long id, boolean unblock);
    void deleteUser(Long id);
    User verifyUser(Long id);
    public void saveUser(User user);
    public void disconnect(User user);
    public List<User> findConnectedUsers();
    public void incrementSessionStats(Long userId);
    public void incrementNavigation(Long userId) ;
    UserDetailDTO getUserDetailDTOByUserId(Long userId);
    String predictChurn(Long userId);

    List <User> getUsersByRoleLivreur();

    public Map<String, Long> getChurnStatistics();
    public void sendEmailToChurnUsers() ;

    public List<User> getUsersWithConversations(Long userId) ;


}
