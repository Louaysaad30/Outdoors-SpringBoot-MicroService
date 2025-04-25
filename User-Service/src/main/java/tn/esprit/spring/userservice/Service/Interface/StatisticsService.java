package tn.esprit.spring.userservice.Service.Interface;

import tn.esprit.spring.userservice.Enum.Etat;
import tn.esprit.spring.userservice.Enum.RoleType;

import java.util.List;
import java.util.Map;

public interface StatisticsService {
    long getTotalUsers();
    long getVerifiedUsers();
    long getNonVerifiedUsers();
    long getBlockedUsers();
    long getNonBlockedUsers();
    Map<RoleType, Long> getUsersByRole();
    long getTotalMessages();
    long getUnreadMessages();
    Map<Long, Long> getMessagesByUser();
    Map<Long, Long> getMessagesByChatRoom();
    Map<Etat, Long> getUsersByEtat();
    Map<String, Long> getUsersByAgeRange();
    Map<String, Long> getUsersByLocation();
}