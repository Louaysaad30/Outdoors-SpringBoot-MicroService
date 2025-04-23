package tn.esprit.spring.userservice.Service.IMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.userservice.Entity.Role;
import tn.esprit.spring.userservice.Entity.User;
import tn.esprit.spring.userservice.Enum.Etat;
import tn.esprit.spring.userservice.Enum.RoleType;
import tn.esprit.spring.userservice.Repository.ChatMessageRepository;
import tn.esprit.spring.userservice.Repository.RoleRepository;
import tn.esprit.spring.userservice.Repository.UserRepository;
import tn.esprit.spring.userservice.Service.Interface.StatisticsService;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public long getTotalUsers() {
        return userRepository.count();
    }

    @Override
    public long getVerifiedUsers() {
        return userRepository.findAll().stream()
                .filter(user -> Boolean.TRUE.equals(user.getStatus()))
                .count();
    }

    @Override
    public long getNonVerifiedUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !Boolean.TRUE.equals(user.getStatus()))
                .count();
    }

    @Override
    public long getBlockedUsers() {
        return userRepository.findAll().stream()
                .filter(User::isAccountLocked)
                .count();
    }

    @Override
    public long getNonBlockedUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !user.isAccountLocked())
                .count();
    }

    @Override
    public Map<RoleType, Long> getUsersByRole() {
        return roleRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Role::getRoleType,
                        role -> (long) role.getUsers().size()
                ));
    }

    @Override
    public long getTotalMessages() {
        return chatMessageRepository.count();
    }

    @Override
    public long getUnreadMessages() {
        return chatMessageRepository.findAll().stream()
                .filter(message -> !message.isRead())
                .count();
    }

    @Override
    public Map<Long, Long> getMessagesByUser() {
        return chatMessageRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        message -> message.getSender().getId(),
                        Collectors.counting()
                ));
    }

    @Override
    public Map<Long, Long> getMessagesByChatRoom() {
        return chatMessageRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        message -> message.getChatRoom().getId(),
                        Collectors.counting()
                ));
    }

    // 🔽 New Methods Below 🔽

    @Override
    public Map<Etat, Long> getUsersByEtat() {
        return userRepository.findAll().stream()
                .filter(user -> user.getEtat() != null)
                .collect(Collectors.groupingBy(
                        User::getEtat,
                        Collectors.counting()
                ));
    }

    @Override
    public Map<String, Long> getUsersByAgeRange() {
        return userRepository.findAll().stream()
                .filter(user -> user.getDateNaissance() != null)
                .collect(Collectors.groupingBy(
                        user -> {
                            int age = LocalDate.now().getYear() - user.getDateNaissance().getYear();
                            if (age < 20) return "<20";
                            else if (age < 30) return "20–29";
                            else if (age < 40) return "30–39";
                            else if (age < 50) return "40–49";
                            else return "50+";
                        },
                        Collectors.counting()
                ));
    }

    @Override
    public Map<String, Long> getUsersByLocation() {
        return userRepository.findAll().stream()
                .filter(user -> user.getLocation() != null && !user.getLocation().isEmpty())
                .collect(Collectors.groupingBy(
                        User::getLocation,
                        Collectors.counting()
                ));
    }
}
