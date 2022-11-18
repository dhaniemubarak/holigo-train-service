package id.holigo.services.holigotrainservice.services.user;

import id.holigo.services.common.model.UserDtoForUser;

public interface UserService {

    UserDtoForUser getUser(Long userId);
}
