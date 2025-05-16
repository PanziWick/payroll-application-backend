package com.mexxar.payroll.user;

import com.mexxar.payroll.authentication.PasswordResetRequestDTO;
import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.common.enums.StatusEnum;
import com.mexxar.payroll.role.RoleModel;
import com.mexxar.payroll.role.RoleService;
import com.mexxar.payroll.user.exception.UserException;
import com.mexxar.payroll.user.exception.UserNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    private static final Logger logger = LogManager.getLogger(UserService.class);

    private static final String USER_NOT_FOUND_MSG = "User not found with id: ";
    private static final String USER_ALREADY_EXISTS_MSG = "Username already exists";
    private static final String EMAIL_ALREADY_EXISTS_MSG = "Email already exists";
    private static final String USER_NOT_FOUND_WITH_USERNAME_MSG = "User not found with username: ";
    private static final String NO_ROLES_REMOVED_MSG = "No roles were found to be removed";

    @Transactional
    public ApiResponseDTO<UserResponseDTO> registerUser(UserRequestDTO request) {
        logger.info("Registering user with username: {}", request.userName());

        if (userRepository.findByUserName(request.userName()).isPresent()) {
            logger.error(USER_ALREADY_EXISTS_MSG);
            throw new UserException(USER_ALREADY_EXISTS_MSG);
        }

        if (userRepository.findByEmail(request.email()).isPresent()) {
            logger.error(EMAIL_ALREADY_EXISTS_MSG);
            throw new UserException(EMAIL_ALREADY_EXISTS_MSG);
        }

        UserModel user = new UserModel();
        user.setUserName(request.userName());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setContactNumber(request.contactNumber());
        user.setStatus(StatusEnum.ACTIVE);

        RoleModel defaultRole = roleService.findDefaultRole();
        user.setRoles(Set.of(defaultRole));

        Instant start = Instant.now();
        userRepository.save(user);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("User registered successfully with username: {} in {} ms", user.getUserName(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("User Registered Successfully", convertToResponseDTO(user));
    }

    public ApiResponseDTO<UserResponseDTO> assignRolesToUser(Long userId, List<Long> roleIds) {
        logger.info("Assigning roles to user with id: {}", userId);

        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MSG + userId));

        Set<RoleModel> newRoles = roleIds.stream()
                .map(roleService::findById)
                .collect(Collectors.toSet());
        user.getRoles().addAll(newRoles);

        Instant start = Instant.now();
        userRepository.save(user);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Roles assigned to user with id: {} in {} ms", userId, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Roles Assigned To The User Successfully", convertToResponseDTO(user));
    }

    public ApiResponseDTO<UserResponseDTO> getUserById(Long id) {
        logger.info("Fetching user by id: {}", id);

        Instant start = Instant.now();
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MSG + id));

        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("User fetched successfully with id: {} in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("User Fetched Successfully",  convertToResponseDTO(user));
    }

    public ApiResponseDTO<List<UserResponseDTO>> getAllUsers() {
        logger.info("Fetching all users");

        Instant start = Instant.now();
        List<UserResponseDTO> users = userRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Fetched {} users in {} ms", users.size(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Successfully Fetched All Users", users);
    }

    @Transactional
    public ApiResponseDTO<UserResponseDTO> updateUser(Long id, UserRequestDTO request) {
        logger.info("Updating user with id: {}", id);

        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MSG + id));

        user.setUserName(request.userName());
        user.setEmail(request.email());
        user.setContactNumber(request.contactNumber());

        if (request.password() != null && !request.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        if (request.status() != null) {
            user.setStatus(request.status());
        }

        Instant start = Instant.now();
        userRepository.save(user);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("User with id: {} updated successfully in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("User Updated Successfully", convertToResponseDTO(user));
    }

    public void deleteUser(Long id) {
        logger.info("Deleting user with id: {}", id);

        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MSG + id));
        user.setStatus(StatusEnum.INACTIVE);

        Instant start = Instant.now();
        userRepository.save(user);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("User with id: {} deleted (status set to INACTIVE) in {} ms", id, timeElapsed.toMillis());
    }

    public ApiResponseDTO<UserResponseDTO> removeRolesFromUser(Long id, Set<Long> roleIds) {
        logger.info("Removing roles from user with id: {}", id);

        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MSG + id));

        Set<RoleModel> rolesToRemove = roleIds.stream()
                .map(roleService::findById)
                .collect(Collectors.toSet());

        boolean rolesRemoved = user.getRoles().removeAll(rolesToRemove);

        if (!rolesRemoved) {
            logger.error(NO_ROLES_REMOVED_MSG);
            throw new UserException(NO_ROLES_REMOVED_MSG);
        }

        Instant start = Instant.now();
        userRepository.save(user);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Roles removed from user with id: {} in {}", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Roles Removed From User Successfully", convertToResponseDTO(user));
    }

    public List<UserModel> findUsersWithRole(RoleModel role) {
        logger.info("Finding users with role: {}", role.getName());

        return userRepository.findByRoles(role);
    }

    public UserModel findUserModelByEmail(String username) {
        logger.info("Finding user model with email: {}", username);

        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_WITH_USERNAME_MSG + username));
    }

    public void resetPassword(PasswordResetRequestDTO request) {
        logger.info("Resetting password for user with email: {}", request.email());
        UserModel user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserException(USER_NOT_FOUND_WITH_USERNAME_MSG + request.email()));
        String hashedPassword = passwordEncoder.encode(request.newPassword());
        user.setPassword(hashedPassword);

        Instant start = Instant.now();
        userRepository.save(user);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Password reset successfully for user with email: {} in {} ms", request.email(), timeElapsed.toMillis());
    }

    @Override
    @Transactional //This ensures that the Hibernate session stays open long enough to initialize lazy collections
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Loading user by username: {}", username);

        UserModel user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_MSG + username));

        logger.info("User loaded with username: {}", username);

        return new CustomUserDetails(user);
    }

    private UserResponseDTO convertToResponseDTO(UserModel user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getContactNumber(),
                user.getStatus().name(),
                user.getRoles().stream().map(RoleModel::getName).collect(Collectors.toList())
        );
    }
}
