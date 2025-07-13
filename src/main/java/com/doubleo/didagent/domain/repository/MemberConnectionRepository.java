package com.doubleo.didagent.domain.repository;

import com.doubleo.didagent.domain.domain.MemberConnection;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface MemberConnectionRepository extends CrudRepository<MemberConnection, String> {
    Optional<MemberConnection> findMemberConnectionByConnectionId(String connectionId);
}
