package id.holigo.services.holigotrainservice.services.user;

import id.holigo.services.common.model.UserDtoForUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceFeign implements UserService {

    private final UserServiceFeignClient userServiceFeignClient;

    @Override
    public UserDtoForUser getUser(Long userId) {
        ResponseEntity<UserDtoForUser> responseEntity = userServiceFeignClient.getUser(userId, userId);
        return responseEntity.getBody();
    }
}
